package order;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import communication.IMulticaster;
import gcom.INode;

public class CausalOrderer extends AbstractOrderer {
	private int id;
	private HashMap<Integer, Long> vectorClock = new HashMap<>();
	/*
	 * TODO: use a smarter buffer (with timeout?) to make it possible to discard
	 * messages and possibly detect failing nodes in this way.
	 */
	private List<Message<?>> buffer = new ArrayList<>();

	public CausalOrderer(IMulticaster multicaster) {
		this(-1, multicaster);
	}

	public CausalOrderer(int id, IMulticaster multicaster) {
		this.id = id;
		this.multicaster = multicaster;
		// Initialize the vector clock
		vectorClock = new HashMap<>();
		vectorClock.putIfAbsent(id, (long) 0);
	}

	@Override
	public List<INode> send(Message<?> message) {
		vectorClock.compute(id, (k, v) -> v += 1);
		message.setVectorClock(vectorClock);
		message.sender = id;

		return multicaster.multicast(message);
	}

	@Override
	public boolean receive(Message<?> message) {
		if (message.getVectorClock() == null) {
			// We cannot deliver a message without vector clock!
			return false;
		}

		/*
		 * TODO: We should probably check if the sender id == our id. Any broadcaster
		 * should send the message back to the sender, so this will happen. In this case
		 * it would be good to check if the vector clock matches our own, in order to
		 * detect multiple nodes with the same id. (Did someone forget to set id?)
		 */

		if (isAheadOfTime(message)) {
			buffer.add(message);
		} else {
			subscribers.forEach(sub -> sub.deliverMessage(message.data));
			vectorClock.compute(message.sender, (k, v) -> v += 1);
		}

		for (Message<?> buffMsg : buffer) {
			if (!isAheadOfTime(buffMsg)) {
				subscribers.forEach(sub -> sub.deliverMessage(buffMsg.data));
				vectorClock.compute(message.sender, (k, v) -> v += 1);
			}
		}

		return true;
	}

	/**
	 * Set the ID of this orderer (should be the same as the node id). This also
	 * clears the vector clock.
	 * 
	 * @param id
	 *            the new id.
	 */
	public void setId(int id) {
		this.id = id;
		// Changing id means that we (re)joined a group and must zero the clock
		vectorClock = new HashMap<>();
		vectorClock.putIfAbsent(id, (long) 0);
	}

	/**
	 * Check if this message must wait on some other message before delivery.
	 * 
	 * @param message
	 *            the message to check
	 * @return
	 */
	private boolean isAheadOfTime(Message<?> message) {
		HashMap<Integer, Long> mClock = message.getVectorClock();
		boolean shouldWait = false;
		for (Integer id : mClock.keySet()) {
			// Initialize clock if this is the first message from this node
			vectorClock.putIfAbsent(id, 0L);
			mClock.putIfAbsent(id, 0L);

			// Senders clock should be exactly one ahead since we must deliver the messages
			// in FIFO order.
			if (id == message.sender) {
				if (mClock.get(message.sender) != vectorClock.get(message.sender) + 1) {
					shouldWait = true;
					break;
				} else {
					continue;
				}

			}
			// We must wait for any message that could have "caused" this one to be sent
			if (mClock.get(id) > vectorClock.get(id)) {
				shouldWait = true;
				break;
			}
		}
		return shouldWait;
	}
}

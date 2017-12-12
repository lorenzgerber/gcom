package order;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import communication.IMulticaster;
import gcom.INode;

public class CausalOrderer extends AbstractOrderer {
	private UUID id;
	private HashMap<UUID, Long> vectorClock = new HashMap<>();
	/*
	 * TODO: use a smarter buffer (with timeout?) to make it possible to discard
	 * messages and possibly detect failing nodes in this way.
	 */
	private List<Message<?>> buffer = new ArrayList<>();

	public CausalOrderer(UUID id, IMulticaster multicaster) {
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
	public void setId(UUID id) {
		this.id = id;
		// Changing id means that we (re)joined a group and must zero the clock
		reset();
	}

	/**
	 * Check if this message must wait on some other message before delivery.
	 * 
	 * @param message
	 *            the message to check
	 * @return
	 */
	private boolean isAheadOfTime(Message<?> message) {
		HashMap<UUID, Long> mClock = message.getVectorClock();
		boolean shouldWait = false;
		for (UUID id : mClock.keySet()) {
			// Initialize clock if this is the first message from this node
			vectorClock.putIfAbsent(id, 0L);

			if (id.equals(this.id)) {
				// We received our own message. The clocks should be equal.
				if (mClock.get(message.sender) != vectorClock.get(message.sender)) {
					shouldWait = true;
					break;
				}
			} else if (id.equals(message.sender)) {
				// Senders clock must be exactly one ahead (if not self) since we must deliver
				// the messages in FIFO order.
				if (mClock.get(message.sender) != vectorClock.get(message.sender) + 1) {
					shouldWait = true;
					break;
				}
			} else if (mClock.get(id) > vectorClock.get(id)) {
				// We must wait for any message that could have "caused" this one to be sent
				shouldWait = true;
				break;
			}
		}
		return shouldWait;
	}

	@Override
	public void reset() {
		// Reset the vector clock and buffer
		vectorClock = new HashMap<>();
		vectorClock.putIfAbsent(id, (long) 0);
		buffer = new ArrayList<>();
	}
}

package order;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import communication.IMulticaster;
import gcom.INode;

public class CausalOrderer extends AbstractOrderer {
	private int id;
	private HashMap<Integer, Long> vectorClock = new HashMap<>();
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

	public void setId(int id) {
		this.id = id;
		// Changing id means that we (re)joined a group and must zero the clock
		vectorClock = new HashMap<>();
		vectorClock.putIfAbsent(id, (long) 0);
	}

	private boolean isAheadOfTime(Message<?> message) {
		HashMap<Integer, Long> mClock = message.getVectorClock();
		boolean shouldWait = false;
		for (Integer id : mClock.keySet()) {
			// Initialize clock if this is first message from this node
			vectorClock.putIfAbsent(id, 0L);
			mClock.putIfAbsent(id, 0L);

			// Senders clock should be exactly one ahead
			if (id == message.sender) {
				if (mClock.get(message.sender) != vectorClock.get(message.sender) + 1) {
					shouldWait = true;
					break;
				} else {
					continue;
				}

			}
			if (mClock.get(id) > vectorClock.get(id)) {
				shouldWait = true;
				break;
			}
		}
		return shouldWait;
	}
}

package order;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import communication.IMulticaster;
import gcom.INode;

public class CausalOrderer extends AbstractOrderer {
	private UUID id;
	// The vector clock is for received messages
	private HashMap<UUID, Long> vectorClock = new HashMap<>();
	// A clock to keep track of (the number of) sent messages
	private long clock = 0;
	private List<Message<?>> buffer = new ArrayList<>();
	private Set<UUID> dead = new HashSet<>();

	public CausalOrderer(IMulticaster multicaster) {
		this.multicaster = multicaster;
		// Initialize the vector clock
		vectorClock = new HashMap<>();
		vectorClock.putIfAbsent(id, (long) 0);

	}

	@Override
	public void setId(UUID id) {
		if (this.id != id) {
			vectorClock.remove(this.id);
		}
		this.id = id;
		reset();
	}

	@Override
	public List<INode> send(Message<?> message) {
		// Copy vector clock to attach to message
		HashMap<UUID, Long> messageClock = new HashMap<>();
		vectorClock.forEach((k, v) -> messageClock.put(k, v));
		// Increase the clock but not the vector clock (this is only increased when
		// receiving messages)
		clock += 1;
		messageClock.compute(id, (k, v) -> v = clock);

		message.setVectorClock(messageClock);
		message.sender = id;

		return multicaster.multicast(message);
	}

	@Override
	public void removeMember(UUID id) {
		if (!bufferedMessages(id)) {
			vectorClock.remove(id);
		} else {
			dead.add(id);
		}
	}

	/**
	 * Are there any messages buffered from the sender with this id?
	 * 
	 * @param id
	 *            sender id
	 * @return true if there are buffered messages from the specified id
	 */
	private boolean bufferedMessages(UUID id) {
		Optional<Message<?>> msg = buffer.stream().filter(m -> m.sender.equals(id)).findAny();
		if (msg.isPresent()) {
			return true;
		}
		return false;
	}

	@Override
	public boolean receive(Message<?> message) {
		if (message.getVectorClock() == null) {
			// We cannot deliver a message without vector clock!
			return false;
		}

		long time = messageTime(message);
		if (time > 0) {
			// This message is ahead of time, add it to the buffer.
			buffer.add(message);
		} else if (messageTime(message) == 0) {
			// This message is right on time, deliver it.
			subscribers.forEach(sub -> sub.deliverMessage(message.data));
			vectorClock.compute(message.sender, (k, v) -> v += 1);
		}
		/*
		 * Note: if time < 0, the message is old and can never be delivered.
		 */

		// Check if any of the buffered messages can be delivered now.
		Iterator<Message<?>> iter = buffer.iterator();
		while (iter.hasNext()) {
			Message<?> buffMsg = iter.next();
			if (messageTime(buffMsg) == 0) {
				subscribers.forEach(sub -> sub.deliverMessage(buffMsg.data));
				vectorClock.compute(message.sender, (k, v) -> v += 1);
				iter.remove();
				// Check if any dead nodes can be removed
				dead.forEach(id -> removeMember(id));
			}
		}

		return true;
	}

	/**
	 * Calculate the message time in relation to this orderers time. If the message
	 * time is > 0, it is ahead, if it is < 0 it is old.
	 * 
	 * @param message
	 *            the message in question
	 * @return a number indicating how much ahead this message is
	 */
	private long messageTime(Message<?> message) {
		long time = 0;
		HashMap<UUID, Long> mClock = message.getVectorClock();

		for (UUID id : mClock.keySet()) {
			// Initialize clock if this is the first message from this node
			vectorClock.putIfAbsent(id, mClock.get(id) - 1);

			if (id.equals(message.sender)) {
				time += mClock.get(message.sender) - (vectorClock.get(message.sender) + 1);
			} else {
				time += mClock.get(id) - vectorClock.get(id);
			}
		}
		return time;
	}

	@Override
	public void reset() {
		// Reset the vector clock and buffer
		vectorClock = new HashMap<>();
		vectorClock.putIfAbsent(id, (long) 0);
		buffer = new ArrayList<>();
		clock = 0;
	}

	@Override
	public long debugGetMessagesSent() {
		return clock;
	}

	@Override
	public HashMap<UUID, Long> debugGetVectorClock() {
		return vectorClock;
	}

	@Override
	public List<Message<?>> debugGetBuffer() {
		return buffer;
	}
}

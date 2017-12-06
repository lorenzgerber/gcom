package order;

import java.util.HashMap;
import java.util.List;

import communication.IMulticaster;
import gcom.INode;

public class CausalOrderer extends AbstractOrderer {
	private int id;
	private HashMap<Integer, Long> vectorClock = new HashMap<>();

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

		return multicaster.multicast(message);
	}

	@Override
	public boolean receive(Message<?> message) {
		subscribers.forEach(sub -> sub.deliverMessage(message.data));
		return true;
	}

	public void setId(int id) {
		this.id = id;
		// Changing id means that we (re)joined a group and must zero the clock
		vectorClock = new HashMap<>();
		vectorClock.putIfAbsent(id, (long) 0);
	}

}

package order;

import java.util.List;

import communication.IMulticaster;
import gcom.INode;

public class UnorderedOrderer extends AbstractOrderer {

	public UnorderedOrderer(IMulticaster multicaster) {
		this.multicaster = multicaster;
	}

	@Override
	public List<INode> send(Message<?> message) {
		return multicaster.multicast(message);
	}

	@Override
	public boolean receive(Message<?> message) {
		subscribers.forEach(sub -> sub.deliverMessage(message.data));
		return true;
	}

}

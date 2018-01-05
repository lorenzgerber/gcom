package order;

import java.util.List;
import java.util.UUID;

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

	@Override
	public void reset() {
		// We do not need to do anything here since there is no ordering in this class
	}

	@Override
	public void setId(UUID id) {
		// We do not use id.
	}

	@Override
	public void removeMember(UUID id) {
		// We do not keep track of members.
	}

}

package order;

import java.util.ArrayList;
import java.util.List;

import communication.IMulticaster;
import gcom.INode;
import gcom.ISubscriber;

public class UnorderedOrderer implements IOrderer {

	private List<ISubscriber> subscribers = new ArrayList<>();
	private IMulticaster multicaster;

	public UnorderedOrderer(IMulticaster multicaster) {
		this.multicaster = multicaster;
	}

	@Override
	public List<INode> send(Message<?> message) {
		return multicaster.multicast(message);
	}

	@Override
	public boolean receive(Message<?> message) {
		for (ISubscriber subscriber : subscribers) {
			subscriber.deliverMessage(message.data);
		}

		return true;
	}

	@Override
	public void subscribe(ISubscriber subscriber) {
		subscribers.add(subscriber);
	}

	@Override
	public void cancelSubscription(ISubscriber subscriber) {
		subscribers.remove(subscriber);
	}

	@Override
	public void setMulticaster(IMulticaster multicaster) {
		this.multicaster = multicaster;
	}

}

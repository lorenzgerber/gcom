package order;

import java.util.ArrayList;
import java.util.List;

import communication.IMulticaster;
import gcom.ISubscriber;

public abstract class AbstractOrderer implements IOrderer {

	protected List<ISubscriber> subscribers = new ArrayList<>();
	protected IMulticaster multicaster;

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
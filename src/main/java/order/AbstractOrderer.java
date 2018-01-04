package order;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

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
	public void unSubscribe(ISubscriber subscriber) {
		subscribers.remove(subscriber);
	}

	@Override
	public void setMulticaster(IMulticaster multicaster) {
		this.multicaster = multicaster;
	}

	@Override
	public long debugGetMessagesSent() {
		return -1;
	}

	@Override
	public HashMap<UUID, Long> debugGetVectorClock() {
		return null;
	}

	@Override
	public List<Message<?>> debugGetBuffer() {
		return null;
	}

	@Override
	public void debugSubscribe(IDebugOrdererSubscriber subscriber) {
		return;
	}

}
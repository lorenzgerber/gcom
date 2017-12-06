package gcom;

import java.util.List;

import communication.IMulticaster;
import order.IOrderer;
import order.Message;

public class Debugger implements IOrderer, IMulticaster {

	@Override
	public List<Integer> multicast(Message<?> message) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Integer> send(Message<?> message) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean receive(Message<?> message) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void subscribe(ISubscriber subscriber) {
		// TODO Auto-generated method stub

	}

	@Override
	public void cancelSubscription(ISubscriber subscriber) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setMulticaster(IMulticaster multicaster) {
		// TODO Auto-generated method stub

	}

}

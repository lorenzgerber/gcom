package order;

import java.util.List;

import communication.IMulticaster;
import gcom.INode;
import gcom.ISubscriber;

public class CausalOrderer implements IOrderer {

	@Override
	public List<INode> send(Message<?> message) {
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

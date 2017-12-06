package gcom;

import communication.IMulticaster;
import order.IOrderer;
import order.Message;

public class Node implements GCom, INode {

	@Override
	public void join(String group) {
		// TODO Auto-generated method stub

	}

	@Override
	public <T> void Send(T data) {
		// TODO Auto-generated method stub

	}

	@Override
	public void leave() {
		// TODO Auto-generated method stub

	}

	@Override
	public void subscribe(ISubscriber subscriber) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setConfig(IOrderer orderer, IMulticaster multicaster) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deliver(Message message) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addToGroup(INode node) {
		// TODO Auto-generated method stub

	}

}

package gcom;

import java.util.ArrayList;
import java.util.List;

import communication.IMulticaster;
import order.IOrderer;
import order.Message;

public class Debugger implements IOrderer {

	private IOrderer orderer;
	private boolean holdMessages = false;
	private List<Message<?>> heldMessages = new ArrayList<Message<?>>();

	public Debugger(IOrderer orderer) {
		this.orderer = orderer;
	}

	public void holdMessages(boolean hold) {
		holdMessages = hold;
	}

	public void releaseMessages() {
		holdMessages = false;
		heldMessages.forEach(m -> orderer.receive(m));
	}

	@Override
	public List<INode> send(Message<?> message) {
		return orderer.send(message);
	}

	@Override
	public boolean receive(Message<?> message) {
		if (holdMessages) {
			heldMessages.add(message);
			return true;
		} else {
			return orderer.receive(message);
		}
	}

	@Override
	public void subscribe(ISubscriber subscriber) {
		orderer.subscribe(subscriber);
	}

	@Override
	public void unSubscribe(ISubscriber subscriber) {
		orderer.unSubscribe(subscriber);
	}

	@Override
	public void setMulticaster(IMulticaster multicaster) {
		orderer.setMulticaster(multicaster);
	}

	@Override
	public void reset() {
		orderer.reset();
	}

}

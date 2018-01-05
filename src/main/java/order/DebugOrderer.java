package order;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import communication.IMulticaster;
import gcom.INode;
import gcom.ISubscriber;

public class DebugOrderer implements IOrderer {

	private IOrderer orderer;
	private boolean holdMessages = false;
	private List<Message<?>> heldMessages = new ArrayList<>();
	private List<IDebugOrdererSubscriber> subscribers = new ArrayList<>();

	public DebugOrderer(IOrderer orderer) {
		this.orderer = orderer;
	}

	/**
	 * Should the debugger hold back messages? Note that setting hold to false does
	 * not release held messages.
	 * 
	 * @param hold
	 *            true for holding messages, false otherwise
	 */
	public void holdMessages(boolean hold) {
		holdMessages = hold;
	}

	/**
	 * Release any held messages and stop catching new ones.
	 */
	public void releaseMessages() {
		holdMessages = false;
		heldMessages.forEach(m -> orderer.receive(m));
		heldMessages = new ArrayList<>();
		notifySubscribers();
	}

	@Override
	public List<INode> send(Message<?> message) {
		List<INode> result = orderer.send(message);
		notifySubscribers();
		return result;
	}

	@Override
	public boolean receive(Message<?> message) {
		if (holdMessages) {
			heldMessages.add(message);
			notifySubscribers();
			return true;
		} else {
			notifySubscribers();
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

	@Override
	public long debugGetMessagesSent() {
		return orderer.debugGetMessagesSent();
	}

	@Override
	public HashMap<UUID, Long> debugGetVectorClock() {
		return orderer.debugGetVectorClock();
	}

	/**
	 * Return the list of messages in the buffer or an empty list if no buffer is
	 * used.
	 */
	@Override
	public List<Message<?>> debugGetBuffer() {
		List<Message<?>> messages = orderer.debugGetBuffer();
		if (messages == null) {
			messages = Collections.emptyList();
		}
		return messages;
	}

	@Override
	public void setId(UUID id) {
		orderer.setId(id);
	}

	@Override
	public void debugSubscribe(IDebugOrdererSubscriber subscriber) {
		subscribers.add(subscriber);
	}

	public List<Message<?>> debugHeldMessages() {
		return heldMessages;
	}

	private void notifySubscribers() {
		subscribers.forEach(s -> s.debugEventOccured());
	}

	@Override
	public void removeMember(UUID id) {
		orderer.removeMember(id);
	}

}

package order;

import java.util.List;

import gcom.ISubscriber;

public interface IOrderer {
	/**
	 * Add any needed clocks or signatures to the message and pass it on the the
	 * multicaster for sending.
	 * 
	 * @param message
	 *            the message to send
	 * @return a list of IDs of failed nodes
	 */
	public List<Integer> send(Message message);

	/**
	 * Receive a message and order it correctly before delivering it to subscribers.
	 * 
	 * @param message
	 *            the received message
	 * @return true if successful
	 */
	public boolean receive(Message message);

	/**
	 * Add a subscriber that should get ordered messages.
	 * 
	 * @param subscriber
	 *            the new subscriber
	 */
	public void subscribe(ISubscriber subscriber);
}

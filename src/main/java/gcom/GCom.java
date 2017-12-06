package gcom;

import communication.IMulticaster;
import order.IOrderer;

public interface GCom {
	/**
	 * Join or create a group if it does not exist.
	 * 
	 * @param group
	 *            name of the group
	 */
	public void join(String group);

	/**
	 * Send a message to the group.
	 * 
	 * @param data
	 *            the message
	 */
	public <T> void Send(T data);

	/**
	 * Leave the current group.
	 */
	public void leave();

	/**
	 * Subscribe to receive messages from the current group.
	 * 
	 * @param subscriber
	 *            the object that should get new messages delivered
	 */
	public void subscribe(ISubscriber subscriber);

	/**
	 * Remove the specified subscriber.
	 * 
	 * @param subscriber
	 *            the subscriber
	 */
	public void unSubscribe(ISubscriber subscriber);

	/**
	 * Specify ordering and multicasting configurations.
	 * 
	 * @param orderer
	 *            the orderer to use
	 * @param multicaster
	 *            the multicaster to use
	 */
	public void setConfig(IOrderer orderer, IMulticaster multicaster);
}

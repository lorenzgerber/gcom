package gcom;

import java.rmi.RemoteException;
import java.util.List;

import order.DebugOrderer;
import order.IOrderer;

public interface GCom {
	/**
	 * Join or create a group if it does not exist.
	 * 
	 * @param group
	 *            name of the group
	 * @throws RemoteException
	 *             if unable to join/create the group
	 */
	public void join(String group) throws RemoteException;

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
	 * Get a list of currently available groups.
	 * 
	 * @return a list of group names
	 * @throws RemoteException
	 */
	public List<String> getGroups() throws RemoteException;

	/**
	 * Specify ordering and multicasting configurations.
	 * 
	 * @param orderer
	 *            the orderer to use
	 */
	public void setOrderer(IOrderer orderer);

	/**
	 * Get a debugger if possible.
	 * 
	 * @return the debugger or null if not available
	 */
	public DebugOrderer getDebugger();
}

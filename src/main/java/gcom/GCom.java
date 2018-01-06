package gcom;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.List;

import order.DebugOrderer;

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
	public <T extends Serializable> void Send(T data);

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
	 * Get a list of groups and their leader nodes.
	 * 
	 * @return group names and leader nodes
	 * @throws RemoteException
	 */
	public HashMap<String, INode> getNodeList() throws RemoteException;

	/**
	 * Get a orderer debugger if possible.
	 * 
	 * @return the debugger or null if not available
	 */
	public DebugOrderer getOrdererDebugger();

}

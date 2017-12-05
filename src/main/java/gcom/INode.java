package gcom;

import java.rmi.RemoteException;

public interface INode {
	/**
	 * Send a message to the group
	 * 
	 * @throws RemoteException
	 */
	public void sendMessage(String message) throws RemoteException;

	/**
	 * This server joins the specified group.
	 * 
	 * @param group
	 *            name of the group to join
	 * @throws RemoteException
	 */
	public void join(String group) throws RemoteException;
}

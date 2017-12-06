package gcom;

import java.rmi.RemoteException;

import order.Message;

public interface INode {
	/**
	 * Get the ID of this node.
	 * 
	 * @return the ID
	 */
	public int getId() throws RemoteException;

	/**
	 * Deliver the message to the orderer for ordering.
	 * 
	 * @param message
	 *            the message
	 */
	public void deliver(Message<?> message) throws RemoteException;

	/**
	 * Add this node to the group.
	 * 
	 * The leader must make sure that all members add the new node.
	 * 
	 * @param node
	 *            the new node to add
	 */
	public void addToGroup(INode node) throws RemoteException;
}

package gcom;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.UUID;

import order.Message;

public interface INode extends Remote {
	/**
	 * Get the ID of this node.
	 * 
	 * @return the ID
	 */
	public UUID getId() throws RemoteException;

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

	/**
	 * Remove this node from the group.
	 * 
	 * The leader is responsible that all members remove a leaving node
	 * 
	 * @param node
	 *            the node to remove
	 */
	public void removeFromGroup(INode node) throws RemoteException;

	/**
	 * Sends a request to the leader, requesting removal of a node
	 * 
	 * @param node
	 */
	public void requestRemoveFromGroup(INode node) throws RemoteException;
	
	/**
	 * Induce Node to request leader from NameServer
	 * 
	 * @throws RemoteException
	 */
	public void updateLeader() throws RemoteException;

	/**
	 * Check in group module if we are group leader
	 * 
	 * @return true if this is the leader
	 */
	public Boolean isLeader() throws RemoteException;

}

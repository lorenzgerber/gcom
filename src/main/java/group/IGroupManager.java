package group;

import gcom.INode;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public interface IGroupManager {

	/**
	 * Get the ID of this node.
	 * 
	 * @return the id
	 */
	public UUID getId();

	/**
	 * Join the group with the specified name.
	 * 
	 * @param group
	 *            name of the group to join
	 * @throws RemoteException
	 */
	public void join(String group) throws RemoteException;

	/**
	 * Add the given node to the group. When called on the leader, this should make
	 * all members add the new node.
	 * 
	 * @param node
	 */
	public void addToGroup(INode node);

	/**
	 * Remove the provided node. When called on the leader, this should make all
	 * members remove the provided node.
	 * 
	 * @param node
	 */
	public void removeMember(INode node);

	/**
	 * Leave the current group.
	 */
	public void leave();

	/**
	 * Send some data to all members in the group.
	 * 
	 * @param data
	 *            the data to send
	 */
	public <T extends Serializable> void send(T data);

	/**
	 * Update Leader to current NameServer data.
	 */
	public void updateLeader();

	/**
	 * Get a list of available groups.
	 * 
	 * @return a list of group names
	 * @throws RemoteException
	 */
	public List<String> getGroups() throws RemoteException;

	/**
	 * Get a list of group names and leader nodes.
	 * 
	 * @return group names and their corresponding leaders
	 * @throws RemoteException
	 */
	public HashMap<String, INode> getNodeList() throws RemoteException;

}

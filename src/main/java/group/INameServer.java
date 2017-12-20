package group;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import gcom.INode;

public interface INameServer extends Remote {
	/**
	 * Get a reference to the leader of the specified group.
	 * 
	 * @param group
	 *            name of the group
	 * @return a reference to the leader
	 */
	public INode getLeader(String group) throws RemoteException;

	/**
	 * Set the leader of a group.
	 * 
	 * @param group
	 *            name of the group
	 * @param leader
	 *            the new leader
	 * @return true if successful
	 */
	public boolean setLeader(String group, INode leader) throws RemoteException;

	/**
	 * Get a list of available groups.
	 * 
	 * @return a list of group names
	 */
	public List<String> getGroups() throws RemoteException;
}

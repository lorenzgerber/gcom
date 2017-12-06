package group;

import gcom.INode;

public interface INameServer {
	/**
	 * Get a reference to the leader of the specified group.
	 * 
	 * @param group
	 *            name of the group
	 * @return a reference to the leader
	 */
	public INode getLeader(String group);

	/**
	 * Set the leader of a group.
	 * 
	 * @param group
	 *            name of the group
	 * @param leader
	 *            the new leader
	 * @return true if successful
	 */
	public boolean setLeader(String group, INode leader);
}

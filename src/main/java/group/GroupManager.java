package group;

import java.util.UUID;

import gcom.INode;

public class GroupManager {
	
	NameServer nameServer;
	
	public GroupManager(NameServer nameServer) {
		this.nameServer = nameServer;
		
	}

	/**
	 * Join the specified group.
	 * 
	 * @param group
	 *            name of the group to join
	 * @return the assigned node ID in this group
	 */
	public UUID join(String group) {
		// TODO implement this
		return null;
	}

	/**
	 * Add the given node to the group. When called on the leader, this should make
	 * all members add the new node.
	 * 
	 * @param node
	 */
	public void addToGroup(INode node) {
		// TODO implement this
	}

	/**
	 * Leave the current group.
	 */
	public void leave() {
		// TODO implement this
	}

	/**
	 * Send some data to all members in the group.
	 * 
	 * @param data
	 *            the data to send
	 */
	public <T> void send(T data) {
		// TODO implement this
	}
}

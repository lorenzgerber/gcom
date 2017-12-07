package group;

import java.rmi.RemoteException;
import java.util.UUID;

import gcom.INode;

public class GroupManager {

	INameServer nameServer;
	INode parent;
	boolean isLeader = true;

	public GroupManager(INameServer nameServer, INode parent) {
		this.nameServer = nameServer;
		this.parent = parent;
	}

	/**
	 * Join the specified group.
	 * 
	 * @param group
	 *            name of the group to join
	 * @return the assigned node ID in this group
	 */
	public UUID join(String group) {
		UUID uuid = null;
		try {
			INode leader = nameServer.getLeader(group);
			if (leader == null) {
				// There is no leader, so we create the group and become leader.
				isLeader = true;
				nameServer.setLeader(group, parent);
				uuid = UUID.randomUUID();
			} else {
				// Ask the leader to add us to the group and give us a UUID
				uuid = leader.addToGroup(parent);
			}
		} catch (RemoteException e1) {
			System.err.println("The name service is down! Unable to continue, exiting...");
			System.exit(-1);
		}
		return uuid;
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

package group;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import gcom.INode;

public class GroupManager {

	INameServer nameServer;
	INode parent;
	boolean isLeader = true;
	List<INode> peers;

	public GroupManager(INameServer nameServer, INode parent) {
		this.nameServer = nameServer;
		this.parent = parent;
		peers = new ArrayList<>();
		peers.add(parent);
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
				isLeader = false;
				// Ask the leader to add us to the group and give us a UUID
				uuid = leader.addToGroup(parent);
			}
		} catch (RemoteException e1) {
			// TODO: Should we quit or throw exception here?
			System.err.println("The name service is down! Unable to continue...");
			System.exit(-1);
		}
		return uuid;
	}

	/**
	 * Add the given node to the group. When called on the leader, this should make
	 * all members add the new node.
	 * 
	 * @param node
	 * @return the uuid of the new member if this is the leader, null otherwise.
	 */
	public UUID addToGroup(INode node) {
		UUID uuid = null;
		if (isLeader) {
			uuid = UUID.randomUUID();
			Iterator<INode> iter = peers.iterator();
			while (iter.hasNext()) {
				INode peer = iter.next();
				// Do not call addToGroup on self
				if (peer.equals(parent)) {
					continue;
				}

				// Add the new node to each group member and add all members to the new node.
				try {
					peer.addToGroup(node);
				} catch (RemoteException e) {
					removeMember(peer);
				}
				try {
					node.addToGroup(peer);
				} catch (RemoteException e) {
					removeMember(node);
				}
			}

			try {
				node.addToGroup(parent);
			} catch (RemoteException e) {
				removeMember(node);
			}
			peers.add(node);
		} else {
			peers.add(node);
		}

		return uuid;
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

	private void removeMember(INode member) {
		// TODO implement this
	}
}

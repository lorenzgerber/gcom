package group;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import gcom.INode;
import order.IOrderer;
import order.Message;

public class GroupManager {

	INameServer nameServer;
	INode parent;
	INode currentLeader = null;
	IOrderer orderer;
	HashMap<INode, UUID> peers;

	public GroupManager(INameServer nameServer, INode parent, IOrderer orderer) {
		this.nameServer = nameServer;
		this.parent = parent;
		// We start out as the leader of our own private group
		currentLeader = parent;
		peers = new HashMap<>();
		try {
			peers.put(parent, parent.getId());
		} catch (RemoteException e) {
			// should never happen as it's
			// invoked only local
		}

		this.orderer = orderer;
	}

	/**
	 * Join the specified group.
	 * 
	 * @param group
	 *            name of the group to join
	 */
	public void join(String group) {
		try {
			INode leaderNameServer = nameServer.getLeader(group);
			if (leaderNameServer == null) {
				// There is no leader, so we create the group and become leader.
				currentLeader = parent;
				nameServer.setLeader(group, parent);
			} else {
				currentLeader = leaderNameServer;
				// Ask the leader to add us to the group and give us a UUID
				leaderNameServer.addToGroup(parent);
			}
		} catch (RemoteException e1) {
			// TODO: Should we quit or throw exception here?
			System.err.println("The name service is down! Unable to continue...");
			System.exit(-1);
		}
		// Reset the orderer always when joining a new group.
		orderer.reset();
	}

	/**
	 * Check if provided node is member in our group
	 * 
	 * @param node
	 *            check this node for membership
	 * @return true if node is member
	 */
	public boolean isMember(INode node) {
		return peers.keySet().contains(node);
	}

	/**
	 * Add the given node to the group. When called on the leader, this should make
	 * all members add the new node.
	 * 
	 * @param node
	 */
	public void addToGroup(INode node) {

		if (isLeader()) {
			Iterator<INode> iter = peers.keySet().iterator();
			while (iter.hasNext()) {
				INode peer = iter.next();
				// Do not call addToGroup on self
				if (peer.equals(parent)) {
					continue;
				}

				// Add the new node to each group member and add all members to the new node.
				tryToAdd(node, peer);
				tryToAdd(peer, node);
			}

			// Add ourself to the new node
			tryToAdd(parent, node);
			try {
				peers.put(node, node.getId());
			} catch (RemoteException e) {
				// No need to do anything
			}
		} else {
			try {
				peers.put(node, node.getId());
			} catch (RemoteException e) {
				// ignore if node has left
			}
		}
	}

	/**
	 * remove the provided node from the group. When called on the leader, this
	 * should make all members remove the provided node.
	 * 
	 * @param node
	 */
	public void removeFromGroup(INode node) {

		if (isLeader()) {
			Iterator<INode> iter = peers.keySet().iterator();
			while (iter.hasNext()) {
				INode peer = iter.next();
				// Do not call removeFromGroup on self
				if (peer.equals(parent)) {
					continue;
				}

				// Remove the indicated node from each group member and add all members to the
				// new node.
				try {
					peer.removeFromGroup(node);
				} catch (RemoteException e) {
					// if remove throws exception, this peer has also left
					// initially, we just ignore that.
				}
			}

			peers.remove(node);
		} else {
			peers.remove(node);
		}

		return;

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
		Message<T> message = new Message<T>(data);
		ArrayList<INode> recipients = new ArrayList<>(peers.keySet());
		message.setRecipients(recipients);
		List<INode> failed = orderer.send(message);
		failed.forEach(n -> requestRemoveFromGroup(n));
	}

	private void removeMember(INode member) {
		// TODO implement this
	}

	public boolean isLeader() {
		return currentLeader == parent;
	}

	/**
	 * Request to leader
	 * 
	 * The request for removal is directed to the leader. This will assure that it
	 * is regularly checked whether the leader is still alive.
	 * 
	 * @param member
	 *            node to remove
	 */
	public void requestRemoveFromGroup(INode member) {
		try {
			currentLeader.removeFromGroup(member);
		} catch (RemoteException e) {
			// If we get an exception, we assume the
			// leader is down, hence we have to call
			// for a new election
		}
	}

	/**
	 * Attempt to add added to receivers group.
	 * 
	 * @param added
	 *            the node to be added
	 * @param receiver
	 *            the node to add to
	 */
	private void tryToAdd(INode added, INode receiver) {
		try {
			receiver.addToGroup(added);
		} catch (RemoteException e) {
			removeMember(receiver);
		}
	}
}

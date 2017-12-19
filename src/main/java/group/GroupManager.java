package group;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import gcom.INode;
import order.IOrderer;
import order.Message;

public class GroupManager {

	INameServer nameServer;
	INode parent;
	String currentGroup;
	INode currentLeader = null;
	IOrderer orderer;
	HashMap<INode, UUID> peers;

	public GroupManager(INameServer nameServer, INode parent, IOrderer orderer) {
		this.nameServer = nameServer;
		this.parent = parent;
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
	 * @throws RemoteException
	 *             if unable to join
	 */
	public void join(String group) throws RemoteException {
		INode leaderNameServer = nameServer.getLeader(group);
		if (leaderNameServer == null) {
			// There is no leader, so we create the group and become leader.
			currentLeader = parent;
			nameServer.setLeader(group, parent);
		} else {
			currentLeader = leaderNameServer;
			// Ask the leader to add us to the group
			leaderNameServer.addToGroup(parent);
		}
		// Reset the orderer always when joining a new group.
		this.currentGroup = group;
		orderer.reset();
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
		}

		// Add the node to our peers
		try {
			peers.put(node, node.getId());
		} catch (RemoteException e) {
			// ignore if node has left
		}
	}

	/**
	 * Remove the provided node. When called on the leader, this should make all
	 * members remove the provided node.
	 * 
	 * @param node
	 */
	public void removeMember(INode node) {
		if (isLeader()) {
			// The leader need to make sure everyone removes the node.
			List<INode> failed = new ArrayList<>();
			Iterator<INode> iter = peers.keySet().iterator();
			while (iter.hasNext()) {
				INode peer = iter.next();
				// Do not remove from self yet
				if (peer.equals(parent)) {
					continue;
				}

				// Remove the indicated node from each group member
				try {
					peer.removeFromGroup(node);
				} catch (RemoteException e) {
					// if remove throws exception, this peer has also left
					// Note: we cannot remove while in the loop, just store for now.
					failed.add(peer);
				}
			}

			failed.forEach(p -> tryRemoveFromGroup(p));
		}

		// Remove from self
		peers.remove(node);
	}

	/**
	 * Leave the current group.
	 */
	public void leave() {
		try {
			if (isLeader()) {
				// Try to find someone else to be leader
				Optional<INode> other = peers.keySet().stream().filter(n -> !n.equals(parent)).findAny();
				if (other.isPresent()) {
					electLeader(other.get());
				} else {
					// We are the only ones left, set leader to null
					nameServer.setLeader(currentGroup, null);
				}
			} else {
				currentLeader.removeFromGroup(parent);
			}
		} catch (RemoteException e) {
			// We are leaving anyway
		}

		// Reset our list of peers
		peers = new HashMap<>();
		try {
			peers.put(parent, parent.getId());
		} catch (RemoteException e) {
			// Our parent should never be remote
		}

	}

	/**
	 * Send some data to all members in the group.
	 * 
	 * @param data
	 *            the data to send
	 */
	public <T> void send(T data) {
		Message<T> message = new Message<T>(data);
		message.setRecipients(new ArrayList<INode>(peers.keySet()));
		List<INode> failed = orderer.send(message);
		failed.forEach(n -> tryRemoveFromGroup(n));
	}

	/**
	 * Request to leader for removal of node from group
	 * 
	 * The request for removal is directed to the leader. This will assure that it
	 * is regularly checked whether the leader is still alive.
	 * 
	 * @param member
	 *            node to remove
	 */
	private void tryRemoveFromGroup(INode member) {
		try {
			currentLeader.removeFromGroup(member);
		} catch (RemoteException e) {
			// If we get an exception, we assume the
			// leader is down, hence we have to call
			// for a new election
			try {
				electLeader(parent);
			} catch (RemoteException e1) {
				// Well we tried...
				System.err.println("Unable to reach name server");
			}
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
			tryRemoveFromGroup(receiver);
		}
	}

	/**
	 * Replace a crashed leader
	 * 
	 * This method is called by the node that realizes that the leader is not
	 * reachable.
	 * 
	 * First the node calls the nameserver to update with his ID. Then it iterates
	 * over the list of nodes and tells them to update.
	 */
	public void setLeaderGlobal() {
		INode leaderNameServer = null;

		// check if current leader ID is the same as in the name server
		try {
			leaderNameServer = nameServer.getLeader(currentGroup);
		} catch (RemoteException e) {
			// If the NameServer does not reply,
			// we should shutdown
		}

		if (leaderNameServer.equals(currentLeader)) {
			try {

				nameServer.setLeader(currentGroup, parent);

				// iterate over all nodes and make them call updateLocal()
				Iterator<INode> iter = peers.keySet().iterator();
				while (iter.hasNext()) {
					INode peer = iter.next();

					if (peer != parent) {
						try {
							peer.updateLeader();
						} catch (RemoteException e) {
							// ignore in this case
						}
					}
				}

			} catch (RemoteException e) {
				// If the NameServer is inaccessible
				// we should shutdown
			}
		} else {
			this.currentLeader = leaderNameServer;
		}
	}

	/**
	 * Update Leader to current NameServer data.
	 */
	public void updateLeader() {
		// request leaderID from Nameserver and update the local value
		try {
			this.currentLeader = nameServer.getLeader(currentGroup);
		} catch (RemoteException e) {
			// so it be
		}
	}

	/**
	 * Make the given INode leader of the group.
	 * 
	 * @param newLeader
	 *            the soon to be leader node
	 * @throws RemoteException
	 *             if unable to reach name server
	 */
	private void electLeader(INode newLeader) throws RemoteException {
		nameServer.setLeader(currentGroup, newLeader);
		// Update all nodes
		Iterator<INode> iter = peers.keySet().iterator();
		while (iter.hasNext()) {
			INode peer = iter.next();
			try {
				peer.updateLeader();
			} catch (RemoteException e) {
				newLeader.removeFromGroup(peer);
			}
		}
	}

	/**
	 * Is this node leader?
	 * 
	 * @return true if leader, false otherwise
	 */
	private boolean isLeader() {
		return currentLeader.equals(parent);
	}
}

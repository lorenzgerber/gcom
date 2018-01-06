package group;

import java.io.Serializable;
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

public class GroupManager implements IGroupManager {
	INameServer nameServer;
	INode parent;
	protected UUID id = UUID.randomUUID();
	String currentGroup;
	INode currentLeader = null;
	IOrderer orderer;
	HashMap<INode, UUID> peers;

	public GroupManager(INameServer nameServer, INode parent, IOrderer orderer) {
		this.nameServer = nameServer;
		this.parent = parent;
		currentLeader = parent;
		peers = new HashMap<>();
		peers.put(parent, id);

		this.orderer = orderer;
		this.orderer.setId(id);
	}

	@Override
	public UUID getId() {
		return id;
	}

	@Override
	public void join(String group) throws RemoteException {
		// Get a new ID when joining a new group
		id = UUID.randomUUID();
		INode leaderNameServer = nameServer.getLeader(group);
		if (leaderNameServer == null) {
			// There is no leader, so we create the group and become leader.
			currentLeader = parent;
			nameServer.setLeader(group, parent);
		} else {
			currentLeader = leaderNameServer;
			try {
				// Ask the leader to add us to the group
				leaderNameServer.addToGroup(parent);
			} catch (RemoteException e) {
				// The leader appears to be dead!
				currentLeader = parent;
				nameServer.setLeader(group, parent);
			}
		}
		// Reset the orderer always when joining a new group.
		this.currentGroup = group;
		orderer.setId(id);
	}

	@Override
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

	@Override
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
					// Note2: adding node to failed will create an infinite loop, since it is
					// already crashed!
					if (!peer.equals(node)) {
						failed.add(peer);
					}
				}
			}

			failed.forEach(p -> tryRemoveFromGroup(p));
		}

		// Reset peer list if we removed our self
		if (!node.equals(parent)) {
			peers.remove(node);
			orderer.removeMember(peers.get(node));
		}
	}

	@Override
	public void leave() {
		try {
			if (isLeader()) {
				// Try to find someone else to be leader
				Optional<INode> other = peers.keySet().stream().filter(n -> !n.equals(parent)).findAny();
				if (other.isPresent()) {
					electLeader(other.get());
				} else {
					// We are the only ones left, set leader to null
					// No need to disturb the NameServer if we don't have a group.
					if (currentGroup != null) {
						nameServer.setLeader(currentGroup, null);
					}
				}
			}
			currentLeader.removeFromGroup(parent);
		} catch (RemoteException e) {
			// We are leaving anyway
		}

		// Reset our list of peers
		peers = new HashMap<>();
		peers.put(parent, id);

	}

	@Override
	public <T extends Serializable> void send(T data) {
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
				/*
				 * The newly elected leader will detect failed nodes and remove them so no need
				 * to retry here
				 */
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

	@Override
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

		List<INode> failed = new ArrayList<>();
		// Update all nodes
		Iterator<INode> iter = peers.keySet().iterator();
		while (iter.hasNext()) {
			INode peer = iter.next();
			try {
				peer.updateLeader();
			} catch (RemoteException e) {
				failed.add(peer);
			}
		}

		for (INode f : failed) {
			newLeader.removeFromGroup(f);
			// Note: nodes should try to elect them selves so this should never throw an
			// exception.
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

	@Override
	public List<String> getGroups() throws RemoteException {
		return nameServer.getGroups();
	}

	@Override
	public HashMap<String, INode> getNodeList() throws RemoteException {
		return nameServer.getNodeList();
	}

}

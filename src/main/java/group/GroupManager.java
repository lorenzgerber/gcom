package group;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;

import gcom.INode;
import order.IOrderer;
import order.Message;

public class GroupManager {

	INameServer nameServer;
	INode parent;
	INode currentLeader = null;
	//List<INode> peers;
	IOrderer orderer;
	HashMap<INode, UUID> peers;

	public GroupManager(INameServer nameServer, INode parent, IOrderer orderer) {
		this.nameServer = nameServer;
		this.parent = parent;
		//peers = new ArrayList<>();
		peers = new HashMap<>();
		try {
			peers.put(parent, parent.getId());
		} catch (RemoteException e) {
			// should never happen as it's
			// invoked only local
		}
		
		//peers.add(parent);
		this.orderer = orderer;
	}

	/**
	 * Join the specified group.
	 * 
	 * @param group
	 *            name of the group to join
	 * @return the assigned node ID in this group
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
	}

	/**
	 * Check if provided node is member in our group
	 * 
	 * @param node
	 *            check this node for membership
	 * @return true if node is member
	 */
	public boolean isMember(INode node) {
		Iterator<INode> iter = peers.keySet().iterator();
		while (iter.hasNext()) {
			INode peer = iter.next();
			if (peer.equals(node)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Add the given node to the group. When called on the leader, this should make
	 * all members add the new node.
	 * 
	 * @param node
	 */
	public void addToGroup(INode node) {
		
		if (currentLeader == parent) {
			Iterator<INode> iter = peers.keySet().iterator();
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
					// do we need this if we get an
					// exception? same below.
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
			try {
				peers.put(node, node.getId());
			} catch (RemoteException e) {
				// ignore if node has left
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
		
		if (currentLeader == parent) {
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
		ArrayList<INode> recipients = new ArrayList<>();
		recipients.addAll(peers.keySet());
		message.setRecipients(recipients);
		orderer.send(message);
	}

	private void removeMember(INode member) {
		// TODO implement this
	}

	public boolean isLeader() {
		return currentLeader == null;
	}

	public void requestRemoveFromGroup(INode member) {
		if(currentLeader == parent) {
			this.removeFromGroup(member);
		} else {
			Iterator<INode> iter = peers.keySet().iterator();
			while (iter.hasNext()) {
				INode peer = iter.next();
				if (peer.equals(currentLeader)) {
					try {
						peer.removeFromGroup(member);
					} catch (RemoteException e) {
						// If we get an exception, we assume the
						// leader is down, hence we have to call
						// for a new election
					}
				}
			}
		}
	}
}

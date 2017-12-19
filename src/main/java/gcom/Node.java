package gcom;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.UUID;

import communication.UnreliableMulticaster;
import group.GroupManager;
import group.INameServer;
import group.NameServer;
import order.IOrderer;
import order.Message;
import order.UnorderedOrderer;

public class Node extends UnicastRemoteObject implements GCom, INode {

	private static final long serialVersionUID = 6210826964208775888L;
	private UUID nodeID;
	private INameServer nameServer;
	private Registry remoteRegistry;
	private IOrderer orderer;
	private GroupManager groupManager;

	public Node(String nameServerHost) throws RemoteException {
		this(nameServerHost, new UnorderedOrderer(new UnreliableMulticaster()));
	}

	public Node(String nameServerHost, IOrderer orderer) throws RemoteException {

		this.nodeID = UUID.randomUUID();
		try {
			remoteRegistry = LocateRegistry.getRegistry(nameServerHost);
			nameServer = (INameServer) remoteRegistry.lookup(NameServer.nameServer);
		} catch (RemoteException | NotBoundException e) {
			e.printStackTrace();
		}

		this.orderer = orderer;
		this.groupManager = new GroupManager(nameServer, this, orderer);

	}

	@Override
	public UUID getId() {
		return nodeID;
	}

	@Override
	public void join(String group) throws RemoteException {
		groupManager.join(group);
	}

	@Override
	public <T> void Send(T data) {
		groupManager.send(data);
	}

	@Override
	public void leave() {
		groupManager.leave();

	}

	@Override
	public void subscribe(ISubscriber subscriber) {
		orderer.subscribe(subscriber);
	}

	@Override
	public void setOrderer(IOrderer orderer) {
		this.orderer = orderer;
		// TODO: Is this a good idea? Changing the orderer cannot happen safely in
		// operation so maybe this should be done only in the constructor.
		groupManager = new GroupManager(nameServer, this, orderer);

	}

	@Override
	public void deliver(Message<?> message) {
		orderer.receive(message);
	}

	@Override
	public void addToGroup(INode node) {
		groupManager.addToGroup(node);
	}

	@Override
	public void unSubscribe(ISubscriber subscriber) {
		orderer.unSubscribe(subscriber);
	}

	@Override
	public void removeFromGroup(INode node) throws RemoteException {
		groupManager.removeMember(node);
	}

	@Override
	public void updateLeader() throws RemoteException {
		groupManager.updateLeader();
		
	}
}

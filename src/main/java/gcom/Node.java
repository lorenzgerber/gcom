package gcom;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.UUID;

import communication.IMulticaster;
import communication.UnreliableMulticaster;
import group.GroupManager;
import group.NameServer;
import order.IOrderer;
import order.Message;
import order.UnorderedOrderer;

public class Node extends UnicastRemoteObject implements GCom, INode {

	private static final long serialVersionUID = 6210826964208775888L;
	private UUID nodeID;
	private String name;
	private String nameServerHost;
	private NameServer nameServer;
	private Registry remoteRegistry;
	private IOrderer orderer;
	private IMulticaster multicaster;
	private GroupManager groupManager;

	public Node(String name, String nameServerHost) throws RemoteException {

		this.nodeID = UUID.randomUUID();
		this.name = name;
		this.nameServerHost = nameServerHost;
		try {
			remoteRegistry = LocateRegistry.getRegistry(nameServerHost);
			nameServer = (NameServer) remoteRegistry.lookup(NameServer.nameServer);
		} catch (RemoteException | NotBoundException e) {
			e.printStackTrace();
		}

		this.multicaster = new UnreliableMulticaster();
		this.orderer = new UnorderedOrderer(multicaster);
		this.groupManager = new GroupManager(nameServer, this, orderer);

	}

	@Override
	public UUID getId() {

		return nodeID;
	}

	@Override
	public void join(String group) {
		groupManager.join(group);
	}

	@Override
	public <T> void Send(T data) {
		// TODO Auto-generated method stub

	}

	@Override
	public void leave() {
		groupManager.leave();

	}

	@Override
	public void subscribe(ISubscriber subscriber) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setConfig(IOrderer orderer, IMulticaster multicaster) {
		this.orderer = orderer;
		this.multicaster = multicaster;

	}

	@Override
	public void deliver(Message<?> message) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addToGroup(INode node) {
		groupManager.addToGroup(node);
	}

	@Override
	public void unSubscribe(ISubscriber subscriber) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeFromGroup(INode node) throws RemoteException {
		groupManager.removeFromGroup(node);
	}

	@Override
	public void requestRemoveFromGroup(INode node) throws RemoteException {
		groupManager.requestRemoveFromGroup(node);
	}

	@Override
	public Boolean isLeader() {
		groupManager.isLeader();
		return null;
	}
}

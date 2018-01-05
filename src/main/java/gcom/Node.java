package gcom;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import communication.UnreliableMulticaster;
import group.GroupManager;
import group.IGroupManager;
import group.INameServer;
import group.NameServer;
import order.DebugOrderer;
import order.IOrderer;
import order.Message;
import order.UnorderedOrderer;

public class Node extends UnicastRemoteObject implements GCom, INode {

	private static final long serialVersionUID = 6210826964208775888L;
	private INameServer nameServer;
	private Registry remoteRegistry;
	private IOrderer orderer;
	private IGroupManager groupManager;

	public Node(String nameServerHost) throws RemoteException {
		this(nameServerHost, new UnorderedOrderer(new UnreliableMulticaster()));
	}

	public Node(String nameServerHost, IOrderer orderer) throws RemoteException {

		try {
			remoteRegistry = LocateRegistry.getRegistry(nameServerHost);
			this.nameServer = (INameServer) remoteRegistry.lookup(NameServer.nameServer);
		} catch (RemoteException | NotBoundException e) {
			e.printStackTrace();
		}

		this.orderer = orderer;
		this.groupManager = new GroupManager(nameServer, this, orderer);
	}

	@Override
	public UUID getId() {
		return groupManager.getId();
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

	@Override
	public List<String> getGroups() throws RemoteException {
		return groupManager.getGroups();
	}

	@Override
	public HashMap<String, INode> getNodeList() throws RemoteException {
		return groupManager.getNodeList();
	}

	@Override
	public DebugOrderer getOrdererDebugger() {
		if (orderer.getClass().equals(DebugOrderer.class)) {
			return (DebugOrderer) orderer;
		} else {
			return null;
		}
	}

}

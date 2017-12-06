package gcom;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import communication.IMulticaster;
import group.NameServer;
import order.IOrderer;
import order.Message;

public class Node extends UnicastRemoteObject implements GCom, INode {
	


	private static final long serialVersionUID = 6210826964208775888L;
	private String name;
	private String nameServerHost;
	private NameServer nameServer;
	private Registry remoteRegistry;
	private IOrderer orderer;
	private IMulticaster multicaster;
	
	
	
	public Node(String name, String nameServerHost) throws RemoteException {
		this.name = name;
		this.nameServerHost = nameServerHost;
		try {
			remoteRegistry = LocateRegistry.getRegistry(nameServerHost);
			nameServer = (NameServer) remoteRegistry.lookup(NameServer.nameServer);
		} catch (RemoteException | NotBoundException e) {
			e.printStackTrace();
		} 
	}

	@Override
	public int getId() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void join(String group) {
		// TODO Auto-generated method stub

	}

	@Override
	public <T> void Send(T data) {
		// TODO Auto-generated method stub

	}

	@Override
	public void leave() {
		// TODO Auto-generated method stub

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
		// TODO Auto-generated method stub

	}

	@Override
	public void cancelSubscription(ISubscriber subscriber) {
		// TODO Auto-generated method stub

	}
}

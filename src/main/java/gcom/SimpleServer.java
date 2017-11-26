package gcom;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class SimpleServer extends UnicastRemoteObject implements ChatServer {
	private static final long serialVersionUID = -8851476756715325706L;
	String name;
	Registry registry;
	List<ChatServer> peers = new ArrayList<>();
	UnreliableBasicBroadcaster broadcaster = new UnreliableBasicBroadcaster();

	public SimpleServer(String name) throws RemoteException {
		this.name = name;
		try {
			registry = LocateRegistry.getRegistry();
			registry.rebind(name, this);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean deliverMessage(String message) throws RemoteException {
		System.out.println("[" + name + " received] " + message);
		return true;
	}

	@Override
	public void join(ChatServer leader) throws RemoteException {
		leader.addToGroup(this);
	}

	@Override
	public void addPeer(ChatServer peer) throws RemoteException {
		peers.add(peer);
	}

	@Override
	public void addToGroup(ChatServer peer) throws RemoteException {
		for (ChatServer member : peers) {
			member.addPeer(peer);
			peer.addPeer(member);
		}
		peers.add(peer);
		peer.addPeer(this);
	}

	@Override
	public void sendMessage(String message) throws RemoteException {
		broadcaster.broadcastMessage(message, peers);
	}

}

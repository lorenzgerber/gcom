package gcom;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class SimpleServer extends UnicastRemoteObject implements ChatServer {
	private static final long serialVersionUID = -8851476756715325706L;
	private String name;
	private Registry remoteRegistry;
	private List<ChatServer> peers = new ArrayList<>();
	private UnreliableBasicBroadcaster broadcaster = new UnreliableBasicBroadcaster();
	private NameServerInterface nameServer;

	// static final String nameServerHost = "hathi.cs.umu.se";
	static final String nameServerHost = "localhost";

	public SimpleServer(String name) throws RemoteException {
		this.name = name;
		try {
			remoteRegistry = LocateRegistry.getRegistry(nameServerHost);
			nameServer = (NameServerInterface) remoteRegistry.lookup(NameServer.nameServerName);
		} catch (RemoteException | NotBoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean deliverMessage(String message) throws RemoteException {
		System.out.println("[" + name + " received] " + message);
		return true;
	}

	@Override
	public void join(String group) throws RemoteException {
		ChatServer leader = nameServer.getLeader(group);
		if (leader == null) {
			System.out.println("No current leader");
			peers.add(this);
			System.out.println("Attempting to set leader");
			nameServer.setLeader(group, this);
			System.out.println("Leader set");
		} else {
			leader.addToGroup(this);
		}
	}

	@Override
	public void addPeer(ChatServer peer) throws RemoteException {
		peers.add(peer);
	}

	@Override
	public void addToGroup(ChatServer peer) throws RemoteException {
		for (ChatServer member : peers) {
			if (!member.equals(this)) {
				member.addPeer(peer);
			}
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

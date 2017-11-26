package gcom;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;

public class SimpleChatPeer {
	ChatServer server;
	List<ChatServer> peers = new ArrayList<>();
	UnreliableBasicBroadcaster broadcaster = new UnreliableBasicBroadcaster();
	Registry registry;
	String name;

	public SimpleChatPeer(String name) {
		this.name = name;
		try {
			registry = LocateRegistry.getRegistry();
			server = new SimpleServer(name);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	public void connectTo(String name) {
		try {
			ChatServer peer = (ChatServer) registry.lookup(name);
			peers.add(peer);
		} catch (RemoteException | NotBoundException e) {
			e.printStackTrace();
		}
	}

	public void sendMessage(String message) {
		try {
			broadcaster.broadcastMessage(message, peers);
		} catch (RemoteException e) {
			System.err.println("Error while sending message!");
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		String name1 = "client1";
		String name2 = "client2";

		SimpleChatPeer peer1 = new SimpleChatPeer(name1);
		SimpleChatPeer peer2 = new SimpleChatPeer(name2);

		peer1.connectTo(name2);
		peer2.connectTo(name1);

		peer1.sendMessage("Hello is anyone there?");
		peer2.sendMessage("Sure, I'm here!");

		System.exit(0);
	}

}

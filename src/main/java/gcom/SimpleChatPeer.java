package gcom;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class SimpleChatPeer {
	ChatServer server;
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

	public void join(String name) {
		try {
			ChatServer leader = (ChatServer) registry.lookup(name);
			server.join(leader);
		} catch (RemoteException | NotBoundException e) {
			e.printStackTrace();
		}
	}

	public void sendMessage(String message) {
		try {
			server.sendMessage(message);
			server.deliverMessage(message);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		String name1 = "client1";
		String name2 = "client2";
		String name3 = "client3";

		SimpleChatPeer leader = new SimpleChatPeer(name1);
		SimpleChatPeer peer2 = new SimpleChatPeer(name2);
		SimpleChatPeer peer3 = new SimpleChatPeer(name3);

		peer2.join(name1);

		leader.sendMessage("Hello is anyone there?");
		peer2.sendMessage("Sure, I'm here!");

		peer3.join(name1);

		peer3.sendMessage("Client 3 here. Who can hear me?");
		leader.sendMessage("Leader to everyone: Party is over, everybody out!");

		System.exit(0);
	}

}

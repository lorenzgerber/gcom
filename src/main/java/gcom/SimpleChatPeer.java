package gcom;

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

	public void join(ChatServer leader) {
		try {
			server.join(leader);
		} catch (RemoteException e) {
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

	public static void main(String[] args) throws RemoteException {
		String name1 = "client1";
		String name2 = "client2";
		String name3 = "client3";
		String group = "SuperGroup";

		NameServer nameServer = new NameServer();

		SimpleChatPeer leader = new SimpleChatPeer(name1);
		SimpleChatPeer peer2 = new SimpleChatPeer(name2);
		SimpleChatPeer peer3 = new SimpleChatPeer(name3);

		nameServer.setLeader(group, leader.server);

		peer2.join(nameServer.getLeader(group));

		leader.sendMessage("Hello is anyone there?");
		peer2.sendMessage("Sure, I'm here!");

		peer3.join(nameServer.getLeader(group));

		peer3.sendMessage("Client 3 here. Who can hear me?");
		leader.sendMessage("Leader to everyone: Party is over, everybody out!");

		System.exit(0);
	}

}

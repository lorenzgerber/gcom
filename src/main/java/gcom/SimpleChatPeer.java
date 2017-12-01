package gcom;

import java.rmi.RemoteException;

public class SimpleChatPeer {
	Node server;
	String name;

	public SimpleChatPeer(String name) {
		this.name = name;
		try {
			server = new SimpleServer(name);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	public void join(String group) {
		try {
			server.join(group);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	public void sendMessage(String message) {
		try {
			server.sendMessage(message);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws RemoteException {
		String name1 = "client1";
		String name2 = "client2";
		String name3 = "client3";
		String group = "SuperGroup";

		SimpleChatPeer leader = new SimpleChatPeer(name1);
		SimpleChatPeer peer2 = new SimpleChatPeer(name2);
		SimpleChatPeer peer3 = new SimpleChatPeer(name3);

		System.out.println("Leader joining group");
		leader.join(group);
		System.out.println("peer joining");
		peer2.join(group);

		leader.sendMessage("Hello is anyone there?");
		peer2.sendMessage("Sure, I'm here!");

		peer3.join(group);

		peer3.sendMessage("Client 3 here. Who can hear me?");
		leader.sendMessage("Leader to everyone: Party is over, everybody out!");

		System.exit(0);
	}

}

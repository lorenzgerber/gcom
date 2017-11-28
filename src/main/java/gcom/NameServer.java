package gcom;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;

public class NameServer extends UnicastRemoteObject implements Remote {

	private static final long serialVersionUID = 6563464540429779982L;
	Registry registry;
	HashMap<String, ChatServer> table;

	public NameServer() throws RemoteException {
		try {
			registry = LocateRegistry.getRegistry();
			registry.rebind("GComNameServer", this);
		} catch (RemoteException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		table = new HashMap<>();
	}

	public ChatServer getLeader(String group) throws RemoteException {
		return table.getOrDefault(group, null);
	}

	public void setLeader(String group, ChatServer leader)
			throws RemoteException {
		table.put(group, leader);
	}
}

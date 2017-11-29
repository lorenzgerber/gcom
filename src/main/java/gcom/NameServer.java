package gcom;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;

public class NameServer extends UnicastRemoteObject implements NameServerInterface {

	public static final String nameServerName = "GComNameServer";
	private static final long serialVersionUID = 6563464540429779982L;
	Registry registry;
	HashMap<String, ChatServer> table;

	public NameServer() throws RemoteException {
		try {
			registry = LocateRegistry.getRegistry();
			registry.rebind(nameServerName, this);
		} catch (RemoteException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		table = new HashMap<>();
	}

	@Override
	public ChatServer getLeader(String group) throws RemoteException {
		System.out.println("Getting leader for group " + group);
		ChatServer ret = table.getOrDefault(group, null);
		System.out.println("Leader is " + ret);
		return ret;
	}

	@Override
	public boolean setLeader(String group, ChatServer leader) throws RemoteException {
		System.out.println("Setting leader for group " + group);
		table.put(group, leader);
		System.out.println("Leader set");
		return true;
	}

	public static void main(String[] args) throws RemoteException {

		NameServerInterface server = new NameServer();
		System.out.println("Name server started");
		while (true) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				System.err.println("Interrupted!");
			}
		}
		// System.out.println("Name server stopping");
	}
}
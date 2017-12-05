package gcom.nameserver;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import gcom.ChatServer;



public class NameServer extends UnicastRemoteObject implements INameServer {

	public static final String nameServerName = "GComNameServer";
	private static final long serialVersionUID = 6563464540429779982L;
	private Registry registry;
	private HashMap<String, ChatServer> table;

	static Logger LOGGER = Logger.getLogger(NameServer.class.getSimpleName());
	static {
		System.setProperty("java.util.logging.SimpleFormatter.format", "[%4$-4s] %5$s%n");
		LOGGER.setLevel(Level.INFO);
	}

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

	

	public static void main(String[] args) throws RemoteException {

		NameServer server = new NameServer();
		LOGGER.info("Name server started");
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

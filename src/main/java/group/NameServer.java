package group;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import gcom.INode;

public class NameServer extends UnicastRemoteObject implements INameServer {

	private static final long serialVersionUID = -8461849108178765576L;
	public static final String nameServer = "gcomNameServer";
	private Registry registry;
	private HashMap<String, INode> nodeList;

	static Logger LOGGER = Logger.getLogger(NameServer.class.getSimpleName());
	static {
		System.setProperty("java.util.logging.SimpleFormatter.format", "[%4$-4s] %5$s%n");
		LOGGER.setLevel(Level.INFO);
	}

	public NameServer() throws RemoteException {
		try {
			registry = LocateRegistry.getRegistry();
			registry.rebind(nameServer, this);
		} catch (RemoteException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		nodeList = new HashMap<>();
	}

	@Override
	public INode getLeader(String group) {
		LOGGER.fine("Getting leader for group " + group);
		INode ret = nodeList.getOrDefault(group, null);
		LOGGER.fine("Group leader is " + ret);
		return ret;
	}

	@Override
	public boolean setLeader(String group, INode leader) {
		LOGGER.fine("Setting leader for group " + group);
		nodeList.put(group, leader);
		LOGGER.fine("Leader set");
		return false;
	}

	public static void main(String[] args) throws RemoteException {

		@SuppressWarnings("unused")
		NameServer server = new NameServer();
		LOGGER.info("Name server started");
		while (true) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				System.err.println("Interrupted");
			}
		}
	}

	@Override
	public List<String> getGroups() {
		return new ArrayList<>(nodeList.keySet());
	}

}

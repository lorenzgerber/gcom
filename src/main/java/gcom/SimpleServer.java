package gcom;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class SimpleServer extends UnicastRemoteObject implements ChatServer {
	private static final long serialVersionUID = -8851476756715325706L;
	String name;
	Registry registry;

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

}

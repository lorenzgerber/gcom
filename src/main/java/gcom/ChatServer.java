package gcom;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ChatServer extends Remote {
	public boolean deliverMessage(String message) throws RemoteException;
}

package gcom;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ChatServer extends Remote, GroupManager {
	public boolean deliverMessage(String message) throws RemoteException;

	public void join(ChatServer leader) throws RemoteException;
}

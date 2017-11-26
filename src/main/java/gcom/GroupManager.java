package gcom;

import java.rmi.RemoteException;

public interface GroupManager {
	public void addPeer(ChatServer peer) throws RemoteException;

	public void addToGroup(ChatServer peer) throws RemoteException;

	public void sendMessage(String message) throws RemoteException;
}

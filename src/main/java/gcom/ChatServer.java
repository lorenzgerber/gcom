package gcom;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ChatServer extends Remote {

	/**
	 * Add the specified peer to this managers list of peers.
	 * 
	 * @throws RemoteException
	 */
	public void addPeer(ChatServer peer) throws RemoteException;

	/**
	 * Add the specified peer to the group.
	 * 
	 * @throws RemoteException
	 */
	public void addToGroup(ChatServer peer) throws RemoteException;

	/**
	 * Deliver a message to this server.
	 * 
	 * @param message
	 *            the message
	 * @return true if successful
	 * @throws RemoteException
	 */
	public boolean deliverMessage(String message) throws RemoteException;
}

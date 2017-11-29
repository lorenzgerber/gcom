package gcom;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface NameServerInterface extends Remote {

	ChatServer getLeader(String group) throws RemoteException;

	boolean setLeader(String group, ChatServer leader) throws RemoteException;

}
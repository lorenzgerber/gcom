package group;

import gcom.INode;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public interface IGroupManager {

	public UUID getId();

	public void join(String group) throws RemoteException;

	public void addToGroup(INode node);

	public void removeMember(INode node);

	public void leave();

	public <T> void send(T data);

	public void tryRemoveFromGroup(INode member);

	public void tryToAdd(INode added, INode receiver);

	public void updateLeader();

	public void electLeader(INode newLeader) throws RemoteException;

	public boolean isLeader();

	public List<String> getGroups() throws RemoteException;

	public HashMap<String, INode> getNodeList() throws RemoteException;

}

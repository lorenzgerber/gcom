package group;

import gcom.INode;

public interface INameServer {
	public INode getLeader(String group);

	public boolean setLeader(INode leader);
}

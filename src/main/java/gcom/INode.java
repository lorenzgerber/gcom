package gcom;

import order.Message;

public interface INode {
	public void deliver(Message message);

	public void addToGroup(INode node);
}

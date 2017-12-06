package order;

import java.util.List;

import gcom.ISubscriber;

public interface IOrderer {
	public List<Integer> send(Message message);

	public boolean receive(Message message);

	public void subscribe(ISubscriber subscriber);
}

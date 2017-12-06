package communication;

import java.util.List;

import order.Message;

public interface IMulticaster {
	public List<Integer> multicast(Message message);
}

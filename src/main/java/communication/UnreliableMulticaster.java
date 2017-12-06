package communication;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import gcom.INode;
import order.Message;

public class UnreliableMulticaster implements IMulticaster {

	@Override
	public List<INode> multicast(Message<?> message) {
		List<INode> failed = new ArrayList<>();
		for (INode node : message.getRecipients()) {
			try {
				node.deliver(message);
			} catch (RemoteException e) {
				failed.add(node);
			}
		}
		return failed;
	}

}

package group;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import gcom.INode;
import order.IOrderer;


public class DebugGroupManager extends AbstractGroupManager {
	
	private List<IDebugGroupManagerSubscriber> subscribers = new ArrayList<>();
	
	public DebugGroupManager(INameServer nameServer, INode parent, IOrderer orderer) {
		this.nameServer = nameServer;
		this.parent = parent;
		currentLeader = parent;
		peers = new HashMap<>();
		try {
			peers.put(parent, parent.getId());
		} catch (RemoteException e) {
			// should never happen as it's
			// invoked only local
		}

		this.orderer = orderer;
	}

	@Override
	public void debugSubscribe(IDebugGroupManagerSubscriber subscriber) {
		subscribers.add(subscriber);
	}
	
	public void notifySubscribers() {
		subscribers.forEach( s -> s.groupManagerEventOccured());
	}

}

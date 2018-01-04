package group;

import java.rmi.RemoteException;
import java.util.HashMap;

import gcom.INode;
import order.IOrderer;

public class DebugGroupManager extends AbstractGroupManager {
	
	public DebugGroupManager(INameServer nameServer, INode parent, IOrderer orderer)  {
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

}

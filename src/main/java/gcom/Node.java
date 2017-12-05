package gcom;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;

import gcom.com.Com;
import gcom.group.Group;
import gcom.order.Order;

public class Node implements INode {
	
	private static final long serialVersionUID = -8851476756715325706L;
	private String name;
	private Registry remoteRegistry;
	private List<Node> nodes = new ArrayList<>();
	private NameServer nameServer;
	
	
	private Group group;
	private Com com;
	private Order order;
	
	// static final String nameServerHost = "hathi.cs.umu.se";
	static final String nameServerHost = "localhost";
	
	
	public Node(String name) {
		group = new Group();
		com = new Com();
		order = new Order();
		
		this.name = name;
		
		try {
			remoteRegistry = LocateRegistry.getRegistry(nameServerHost);
			nameServer = (NameServer) remoteRegistry.lookup(SimpleNameServer.nameServerName);
		} catch (RemoteException | NotBoundException e) {
			e.printStackTrace();
		}
	}
	

	@Override
	public void sendMessage(String message) throws RemoteException {
		// TODO Auto-generated method stub

	}

	@Override
	public void join(String group) throws RemoteException {
		// TODO Auto-generated method stub

	}

}

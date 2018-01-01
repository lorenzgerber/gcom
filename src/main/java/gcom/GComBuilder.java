package gcom;

import java.rmi.RemoteException;

import communication.UnreliableMulticaster;
import order.CausalOrderer;
import order.DebugOrderer;
import order.IOrderer;
import order.Orderers;
import order.UnorderedOrderer;

public class GComBuilder {
	private Node node;
	private String nameServerUrl;
	private IOrderer orderer;

	public GComBuilder withNameServer(String url) {
		this.nameServerUrl = url;
		return this;
	}

	public GComBuilder withOrderer(Orderers orderer) {
		if (orderer.equals(Orderers.Unordered)) {
			this.orderer = new UnorderedOrderer(new UnreliableMulticaster());
		} else if (orderer.equals(Orderers.Causal)) {
			this.orderer = new CausalOrderer(new UnreliableMulticaster());
		}

		return this;
	}

	public GComBuilder debug(boolean on) {
		if (on) {
			DebugOrderer debugger = new DebugOrderer(orderer);
			this.orderer = debugger;
		}

		return this;
	}

	public GCom build() throws RemoteException {
		node = new Node(nameServerUrl, orderer);
		return node;
	}
}

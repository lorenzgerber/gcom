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

	/**
	 * Specify the name server host to use for the GCom.
	 * 
	 * @param url
	 *            the hosts URL
	 * @return the GComBuilder with url set
	 */
	public GComBuilder withNameServer(String url) {
		this.nameServerUrl = url;
		return this;
	}

	/**
	 * Use the specified Orderer when building the GCom.
	 * 
	 * @param orderer
	 *            the orderer
	 * @return the GComBuilder with orderer set
	 */
	public GComBuilder withOrderer(Orderers orderer) {
		if (orderer.equals(Orderers.Unordered)) {
			this.orderer = new UnorderedOrderer(new UnreliableMulticaster());
		} else if (orderer.equals(Orderers.Causal)) {
			this.orderer = new CausalOrderer(new UnreliableMulticaster());
		}

		return this;
	}

	/**
	 * Should the GCom have debugging enabled?
	 * 
	 * @param on
	 *            enable debug?
	 * @return the GComBuilder with debugging specified
	 */
	public GComBuilder debug(boolean on) {
		if (on) {
			DebugOrderer debugger = new DebugOrderer(orderer);
			this.orderer = debugger;
		}

		return this;
	}

	/**
	 * Create the GCom using the previously specified settings.
	 * 
	 * @return the GCom
	 * @throws RemoteException
	 */
	public GCom build() throws RemoteException {
		node = new Node(nameServerUrl, orderer);
		return node;
	}
}

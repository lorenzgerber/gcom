package gcom;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import static org.mockito.Mockito.*;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import group.INameServer;
import group.NameServer;
import order.IOrderer;
import order.Message;

public class INodeTest {

	static INameServer nameServer;
	INode node;
	String group = "SuperGroup";
	IOrderer orderer;

	@BeforeClass
	public static void initialize() throws RemoteException {
		try {
			LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
		} catch (Exception e) {
			System.err.println("Failed to create registry");
			e.printStackTrace();
		}
		// TODO: Is it possible to mock this?
		nameServer = new NameServer();
	}

	@Before
	public void setUp() throws Exception {
		orderer = mock(IOrderer.class);
		node = new Node("localhost", orderer);
	}

	@Test
	public void idShouldNotBeNull() throws RemoteException {
		assertThat(node.getId(), is(notNullValue()));
	}

	@Test
	public void deliverMessageToOrderer() throws RemoteException {
		Message<String> message = new Message<>("Hello");
		node.deliver(message);
		verify(orderer).receive(message);
	}

}

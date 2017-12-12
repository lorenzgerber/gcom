package gcom;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import static org.mockito.Mockito.*;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.UUID;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import group.INameServer;
import group.NameServer;
import order.IOrderer;
import order.Message;

public class INodeTest {

	static INameServer nameServer;
	private INode node;
	private IOrderer orderer;

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

	@Test
	public void addNodeToGroupManager() throws RemoteException {
		INode newNode = mock(INode.class);
		when(newNode.getId()).thenReturn(UUID.randomUUID());

		node.addToGroup(newNode);
		// The new node should try to add us back
		verify(newNode).addToGroup(node);
	}

	@Test
	public void tryToAddCrashedNode() throws RemoteException {
		INode newNode = mock(INode.class);
		when(newNode.getId()).thenReturn(UUID.randomUUID());

		// It should not be a problem if the new node is crashed
		doThrow(new RemoteException()).when(newNode).addToGroup(node);
		node.addToGroup(newNode);
		// The exception should be captured and dealt with.
	}

	@Test
	public void removeNode() throws RemoteException {
		node.removeFromGroup(mock(INode.class));
		// Nothing special should happen
	}

	@Test
	public void checkLeader() throws RemoteException {
		// The node should be its own leader at the beginning
		assertThat(node.isLeader(), is(true));
	}
}

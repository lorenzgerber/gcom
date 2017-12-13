package gcom;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.hamcrest.CoreMatchers.*;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import communication.IMulticaster;
import communication.UnreliableMulticaster;
import group.NameServer;
import order.CausalOrderer;
import order.IOrderer;

public class IntegrationTest {

	private static final String group = "test group";
	private static final String LOCALHOST = "localhost";
	static NameServer nameServer;
	private Node gcom;
	private ISubscriber clientApplication = mock(ISubscriber.class);
	private String data = "Hello";

	@BeforeClass
	public static void initialize() throws RemoteException {
		try {
			LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
		} catch (Exception e) {
			System.err.println("Failed to create registry");
			e.printStackTrace();
		}
		nameServer = new NameServer();
	}

	@Before
	public void setUp() throws Exception {
		gcom = new Node(LOCALHOST);
		gcom.subscribe(clientApplication);
	}

	@After
	public void cleanUp() {
		nameServer.setLeader(group, null);
	}

	@Test
	public void sendMessageToSelf() {
		gcom.Send(data);
		verify(clientApplication).deliverMessage(data);
	}

	@Test
	public void createGroupSendToMember() throws RemoteException {
		// Create the group by joining it
		gcom.join(group);
		assertThat(nameServer.getLeader(group).getId(), is(gcom.getId()));

		// Make a group member
		GCom member = new Node(LOCALHOST);
		// Create a client for the member
		ISubscriber memberClient = mock(ISubscriber.class);
		member.subscribe(memberClient);
		member.join(group);
		assertThat(nameServer.getLeader(group).getId(), is(gcom.getId()));

		gcom.Send(data);

		verify(clientApplication).deliverMessage(data);
		verify(memberClient).deliverMessage(data);
	}

	@Test
	public void joinExistingGroup() throws RemoteException {
		GCom leader = new Node(LOCALHOST);
		leader.join(group);
		gcom.join(group);

		leader.Send(data);

		verify(clientApplication).deliverMessage(data);
	}

	@Test
	public void useCausalOrderer() {
		IMulticaster multicaster = new UnreliableMulticaster();
		IOrderer causal = new CausalOrderer(gcom.getId(), multicaster);
		gcom.setOrderer(causal);

		gcom.subscribe(clientApplication);
		gcom.Send(data);
		verify(clientApplication).deliverMessage(data);
	}

}
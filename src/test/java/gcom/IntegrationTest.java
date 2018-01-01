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
import org.mockito.InOrder;

import group.NameServer;
import order.DebugOrderer;
import order.Orderers;

public class IntegrationTest {

	private static final String group = "test group";
	private static final String LOCALHOST = "localhost";
	static NameServer nameServer;
	private GCom gcom;
	private ISubscriber clientApplication = mock(ISubscriber.class);
	private String data = "Hello";

	@BeforeClass
	public static void initialize() throws RemoteException {
		try {
			LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
		} catch (Exception e) {
			System.err.println("Failed to create registry");
		}
		nameServer = new NameServer();
	}

	@Before
	public void setUp() throws Exception {
		gcom = new GComBuilder().withNameServer(LOCALHOST).build();
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
		assertThat(nameServer.getLeader(group).getId(), is(((Node) gcom).getId()));

		// Make a group member
		GCom member = new Node(LOCALHOST);
		// Create a client for the member
		ISubscriber memberClient = mock(ISubscriber.class);
		member.subscribe(memberClient);
		member.join(group);
		assertThat(nameServer.getLeader(group).getId(), is(((Node) gcom).getId()));

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
	public void useCausalOrderer() throws RemoteException {
		gcom = new GComBuilder().withNameServer(LOCALHOST).withOrderer(Orderers.Causal).build();

		gcom.subscribe(clientApplication);
		gcom.Send(data);
		verify(clientApplication).deliverMessage(data);
	}

	@Test
	public void holdMessages() throws RemoteException {
		DebugOrderer debugger = setUpDebugger(Orderers.Unordered);

		debugger.holdMessages(true);

		gcom.Send(data);
		verify(clientApplication, never()).deliverMessage(data);

		debugger.releaseMessages();
		verify(clientApplication).deliverMessage(data);
	}

	@Test
	public void doNotOrder() throws RemoteException {
		DebugOrderer debugger = setUpDebugger(Orderers.Unordered);

		debugger.holdMessages(true);

		gcom.Send(data);
		String data2 = "Second message";

		debugger.holdMessages(false);
		gcom.Send(data2);

		// The second message should arrive before the first
		verify(clientApplication).deliverMessage(data2);

		// Now we release the first message
		debugger.releaseMessages();
		// both messages should now be delivered in order
		verify(clientApplication).deliverMessage(data);
	}

	@Test
	public void orderCausally() throws RemoteException {
		DebugOrderer debugger = setUpDebugger(Orderers.Causal);
		InOrder mockOrder = inOrder(clientApplication);

		debugger.holdMessages(true);

		gcom.Send(data);
		String data2 = "Second message";

		debugger.holdMessages(false);
		gcom.Send(data2);

		// The second message should not arrive before the first
		verify(clientApplication, never()).deliverMessage(data2);

		// Now we release the first message
		debugger.releaseMessages();
		// both messages should now be delivered in order
		mockOrder.verify(clientApplication).deliverMessage(data);
		mockOrder.verify(clientApplication).deliverMessage(data2);
	}

	@Test
	public void leavingGroup() throws RemoteException {
		// Create the group by joining it
		gcom.join(group);

		// Create group members
		GCom member1 = new GComBuilder().withNameServer(LOCALHOST).build();
		GCom member2 = new GComBuilder().withNameServer(LOCALHOST).build();

		// Create a clients for the members
		ISubscriber memberClient1 = mock(ISubscriber.class);
		ISubscriber memberClient2 = mock(ISubscriber.class);
		member1.subscribe(memberClient1);
		member1.join(group);
		member2.subscribe(memberClient2);
		member2.join(group);

		gcom.Send(data);

		verify(clientApplication).deliverMessage(data);
		verify(memberClient1).deliverMessage(data);
		verify(memberClient2).deliverMessage(data);

		member1.leave();

		String data2 = "Second message";
		gcom.Send(data2);

		verify(clientApplication).deliverMessage(data2);
		verify(memberClient1, never()).deliverMessage(data2);
		verify(memberClient2).deliverMessage(data2);

		gcom.leave();

		String data3 = "Third";
		member2.Send(data3);

		verify(clientApplication, never()).deliverMessage(data3);
		verify(memberClient1, never()).deliverMessage(data3);
		verify(memberClient2).deliverMessage(data3);
	}

	private DebugOrderer setUpDebugger(Orderers orderer) throws RemoteException {
		gcom = new GComBuilder().withNameServer(LOCALHOST).withOrderer(orderer).debug(true).build();
		gcom.subscribe(clientApplication);
		return gcom.getDebugger();
	}

}

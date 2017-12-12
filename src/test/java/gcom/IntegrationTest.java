package gcom;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.hamcrest.CoreMatchers.*;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import group.NameServer;

public class IntegrationTest {

	static NameServer nameServer;
	private Node gcom;
	private String group = "Some group name here";
	private ISubscriber clientApplication = mock(ISubscriber.class);

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
		gcom = new Node("localhost");
		gcom.subscribe(clientApplication);
	}

	@Test
	public void sendMessageToSelf() {
		String data = "Hello";

		gcom.Send(data);
		verify(clientApplication).deliverMessage(data);
	}

	@Test
	public void createGroupAndAddMember() throws RemoteException {
		// Create the group by joining it
		gcom.join(group);
		assertThat(nameServer.getLeader(group).getId(), is(gcom.getId()));

		// Make a group member
		GCom member = new Node("localhost");
		// Create a client for the member
		ISubscriber memberClient = mock(ISubscriber.class);
		member.subscribe(memberClient);
		member.join(group);
		assertThat(nameServer.getLeader(group).getId(), is(gcom.getId()));

		String data = "Hello members";
		gcom.Send(data);

		verify(clientApplication).deliverMessage(data);
		verify(memberClient).deliverMessage(data);
	}

}

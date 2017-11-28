package gcom;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class NameServerTest {
	NameServer server;

	@BeforeClass
	public static void initialize() {
		try {
			LocateRegistry.createRegistry(1099);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Before
	public void setup() throws RemoteException {
		server = new NameServer();
	}

	@Test
	public void noGroup() throws RemoteException {
		ChatServer leader = server.getLeader("AnyGroup");
		assertThat(leader, is(nullValue()));
	}

	@Test
	public void setLeaderOfNewGroup() throws RemoteException {
		String group = "MyGroup";
		ChatServer leader = new SimpleServer("leader");
		server.setLeader(group, leader);
		assertThat(server.getLeader(group), is(leader));
	}

	@Test
	public void changeLeader() throws RemoteException {
		String group = "MyGroup";
		ChatServer leader1 = new SimpleServer("leader");
		ChatServer leader2 = new SimpleServer("leader2");

		server.setLeader(group, leader1);
		assertThat(server.getLeader(group), is(leader1));
		server.setLeader(group, leader2);
		assertThat(server.getLeader(group), is(leader2));
	}

}

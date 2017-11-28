package gcom;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import java.rmi.RemoteException;

import org.junit.Before;
import org.junit.Test;

public class NameServerTest {
	NameServer server;

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

}

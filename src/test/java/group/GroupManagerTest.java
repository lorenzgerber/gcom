package group;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.rmi.RemoteException;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.*;

import org.junit.Before;
import org.junit.Test;

import gcom.INode;

public class GroupManagerTest {

	private GroupManager manager;
	private INode node;
	private INameServer nameServer;

	@Before
	public void setUp() throws Exception {
		nameServer = mock(INameServer.class);
		node = mock(INode.class);
		manager = new GroupManager(nameServer, node);
	}

	@Test
	public void joinGroup() throws RemoteException {
		String group = "SuperGroup";
		UUID uuid = UUID.randomUUID();
		INode leader = mock(INode.class);

		when(nameServer.getLeader(group)).thenReturn(leader);
		when(leader.addToGroup(node)).thenReturn(uuid);

		assertThat(manager.join(group), is(uuid));
	}

	@Test
	public void createNewGroup() throws RemoteException {
		String group = "new group";

		when(nameServer.getLeader(group)).thenReturn(null);

		assertThat(manager.join(group), is(notNullValue()));
		verify(nameServer).setLeader(group, node);
	}

}

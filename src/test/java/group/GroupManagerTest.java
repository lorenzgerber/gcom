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
	private String group = "SuperGroup";

	@Before
	public void setUp() throws Exception {
		nameServer = mock(INameServer.class);
		node = mock(INode.class);
		manager = new GroupManager(nameServer, node);
	}

	@Test
	public void joinGroup() throws RemoteException {
		UUID uuid = UUID.randomUUID();
		INode leader = mock(INode.class);

		when(nameServer.getLeader(group)).thenReturn(leader);
		when(leader.addToGroup(node)).thenReturn(uuid);

		assertThat(manager.join(group), is(uuid));
	}

	@Test
	public void createNewGroup() throws RemoteException {
		when(nameServer.getLeader(group)).thenReturn(null);

		assertThat(manager.join(group), is(notNullValue()));
		verify(nameServer).setLeader(group, node);
	}

	@Test
	public void addToNonLeader() throws RemoteException {
		UUID uuid = UUID.randomUUID();
		INode leader = mock(INode.class);

		when(nameServer.getLeader(group)).thenReturn(leader);
		when(leader.addToGroup(node)).thenReturn(uuid);

		manager.join(group);
		assertThat(manager.addToGroup(mock(INode.class)), is(nullValue()));
	}

	@Test
	public void addToLeader() throws RemoteException {
		when(nameServer.getLeader(group)).thenReturn(null);

		manager.join(group);
		assertThat(manager.addToGroup(mock(INode.class)), is(notNullValue()));
	}

	@Test
	public void addToLeaderWithMembers() throws RemoteException {
		INode member1 = mock(INode.class);
		INode member2 = mock(INode.class);

		when(nameServer.getLeader(group)).thenReturn(null);

		manager.join(group);
		assertThat(manager.addToGroup(member1), is(notNullValue()));
		assertThat(manager.addToGroup(member2), is(notNullValue()));
	}
	
	@Test
	public void removeFromNonLeader() throws RemoteException {
		UUID uuid = UUID.randomUUID();
		INode leader = mock(INode.class);
		INode member = mock(INode.class);
		when(member.getId()).thenReturn(uuid);
		when(nameServer.getLeader(group)).thenReturn(leader);
		manager.join(group);
		manager.addToGroup(member);

		assertThat(manager.removeFromGroup(member), is(notNullValue()));

	}

}

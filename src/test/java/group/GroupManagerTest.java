package group;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import org.mockito.ArgumentCaptor;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.*;

import org.junit.Before;
import org.junit.Test;

import gcom.INode;
import order.IOrderer;
import order.Message;

public class GroupManagerTest {

	private GroupManager manager;
	private INode node;
	private INameServer nameServer;
	private IOrderer orderer;
	private String group = "SuperGroup";

	@Before
	public void setUp() throws Exception {
		nameServer = mock(INameServer.class);
		node = mock(INode.class);
		orderer = mock(IOrderer.class);
		manager = new GroupManager(nameServer, node, orderer);
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
		manager.removeFromGroup(member);

		assertThat(manager.isMember(member), is(false));

	}

	@Test
	public void removeFromLeader() throws RemoteException {
		UUID uuid = UUID.randomUUID();
		INode member = mock(INode.class);
		when(member.getId()).thenReturn(uuid);
		when(nameServer.getLeader(group)).thenReturn(null);
		manager.join(group);
		manager.addToGroup(member);
		manager.removeFromGroup(member);

		assertThat(manager.isMember(member), is(false));

	}

	@Test
	public void sendToSelf() {
		String data = "Hello";
		// We expect to have only the "self" node as recipient
		List<INode> expected = Arrays.asList(node);

		@SuppressWarnings("unchecked")
		ArgumentCaptor<Message<String>> captor = ArgumentCaptor.forClass(Message.class);

		manager.send(data);
		verify(orderer).send(captor.capture());
		Message<String> sent = captor.getValue();
		assertThat(sent.data, is(data));
		assertThat(sent.getRecipients(), is(expected));
	}

	@Test
	public void sendToGroup() throws RemoteException {
		String data = "Hello";

		List<INode> expected = setUpGroup();

		@SuppressWarnings("unchecked")
		ArgumentCaptor<Message<String>> captor = ArgumentCaptor.forClass(Message.class);

		manager.send(data);
		verify(orderer).send(captor.capture());
		Message<String> sent = captor.getValue();
		assertThat(sent.data, is(data));
		assertThat(sent.getRecipients().size(), is(expected.size()));
		assertThat(expected.containsAll(sent.getRecipients()), is(true));
	}

	/**
	 * Set up a group.
	 * 
	 * @return the list of members
	 * @throws RemoteException
	 */
	private List<INode> setUpGroup() throws RemoteException {
		List<INode> members = new ArrayList<>();
		INode leader = mock(INode.class);

		when(nameServer.getLeader(group)).thenReturn(leader);
		when(leader.addToGroup(node)).thenReturn(UUID.randomUUID());

		// A leader and a member
		members.add(leader);
		members.add(mock(INode.class));
		// Add them to the manager
		members.forEach(m -> manager.addToGroup(m));
		// Make the tested manager a member of the group
		members.add(node);
		return members;
	}
}

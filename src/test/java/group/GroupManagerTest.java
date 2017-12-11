package group;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import org.mockito.ArgumentCaptor;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
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
	private List<INode> members;
	private INode leader;

	@Before
	public void setUp() throws Exception {
		nameServer = mock(INameServer.class);
		node = mock(INode.class);
		orderer = mock(IOrderer.class);
		manager = new GroupManager(nameServer, node, orderer);
	}

	@Test
	public void joinGroup() throws RemoteException {
		leader = mock(INode.class);

		when(nameServer.getLeader(group)).thenReturn(leader);

		manager.join(group);
		verify(leader).addToGroup(node);
	}

	@Test
	public void createNewGroup() throws RemoteException {
		when(nameServer.getLeader(group)).thenReturn(null);

		manager.join(group);
		verify(nameServer).setLeader(group, node);
	}

	@Test
	public void addToNonLeader() throws RemoteException {
		INode leader = mock(INode.class);

		when(nameServer.getLeader(group)).thenReturn(leader);

		manager.join(group);
		INode newMember = mock(INode.class);
		manager.addToGroup(newMember);
		assertThat(manager.peers.containsKey(newMember), is(true));
	}

	@Test
	public void addToLeader() throws RemoteException {
		when(nameServer.getLeader(group)).thenReturn(null);

		manager.join(group);
		INode newMember = mock(INode.class);
		manager.addToGroup(newMember);
		verify(newMember).addToGroup(node);
		assertThat(manager.peers.containsKey(newMember), is(true));
	}

	@Test
	public void addToLeaderWithMembers() throws RemoteException {
		INode member1 = mock(INode.class);
		INode member2 = mock(INode.class);

		when(nameServer.getLeader(group)).thenReturn(null);
		manager.join(group);

		manager.addToGroup(member1);
		manager.addToGroup(member2);
		verify(member1).addToGroup(node);
		verify(member2).addToGroup(node);
		assertThat(manager.peers.containsKey(member1), is(true));
		assertThat(manager.peers.containsKey(member2), is(true));
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
		Collection<INode> expected = Arrays.asList(node);

		@SuppressWarnings("unchecked")
		ArgumentCaptor<Message<String>> captor = ArgumentCaptor.forClass(Message.class);

		manager.send(data);
		verify(orderer).send(captor.capture());
		Message<String> sent = captor.getValue();
		assertThat(sent.data, is(data));
		assertThat(sent.getRecipients().size(), is(expected.size()));
		assertThat(expected.containsAll(sent.getRecipients()), is(true));
	}

	@Test
	public void sendToGroup() throws RemoteException {
		String data = "Hello";

		setUpGroup();
		List<INode> expected = members;

		@SuppressWarnings("unchecked")
		ArgumentCaptor<Message<String>> captor = ArgumentCaptor.forClass(Message.class);

		manager.send(data);
		verify(orderer).send(captor.capture());
		Message<String> sent = captor.getValue();
		assertThat(sent.data, is(data));
		assertThat(sent.getRecipients().size(), is(expected.size()));
		assertThat(expected.containsAll(sent.getRecipients()), is(true));
	}

	@Test
	public void sendToFailedNode() throws RemoteException {
		String data = "Hello";
		setUpGroup();
		INode failed = members.get(1);

		when(orderer.send(any())).thenReturn(Arrays.asList(failed));

		manager.send(data);
		verify(orderer).send(any());
		verify(leader).removeFromGroup(failed);
	}

	/**
	 * Set up a group. (Initialize the class fields members and leader).
	 * 
	 * @throws RemoteException
	 */
	private void setUpGroup() throws RemoteException {
		members = new ArrayList<>();
		leader = mock(INode.class);

		when(nameServer.getLeader(group)).thenReturn(leader);
		when(leader.isLeader()).thenReturn(true);
		// when(leader.addToGroup(node)).thenReturn(UUID.randomUUID());

		manager.join(group);
		// A leader and a member
		members.add(leader);
		members.add(mock(INode.class));
		// Add them to the manager
		members.forEach(m -> manager.addToGroup(m));
		// Make the tested manager a member of the group
		members.add(node);
	}
}

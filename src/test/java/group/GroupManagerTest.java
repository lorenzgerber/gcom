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
		node = getMockedNode();
		orderer = mock(IOrderer.class);
		manager = new GroupManager(nameServer, node, orderer);
	}

	@Test
	public void joinGroup() throws RemoteException {
		leader = getMockedNode();

		when(nameServer.getLeader(group)).thenReturn(leader);

		manager.join(group);
		verify(leader).addToGroup(node);
		// Orderer must reset when joining a new group
		verify(orderer).reset();
	}

	@Test
	public void createNewGroup() throws RemoteException {
		when(nameServer.getLeader(group)).thenReturn(null);

		manager.join(group);
		verify(nameServer).setLeader(group, node);
	}

	@Test
	public void addToNonLeader() throws RemoteException {
		INode leader = getMockedNode();

		when(nameServer.getLeader(group)).thenReturn(leader);

		manager.join(group);
		INode newMember = getMockedNode();
		manager.addToGroup(newMember);
		assertThat(manager.peers.containsKey(newMember), is(true));
	}

	@Test
	public void addToLeader() throws RemoteException {
		when(nameServer.getLeader(group)).thenReturn(null);

		manager.join(group);
		INode newMember = getMockedNode();
		manager.addToGroup(newMember);
		verify(newMember).addToGroup(node);
		assertThat(manager.peers.containsKey(newMember), is(true));
	}

	@Test
	public void addToLeaderWithMembers() throws RemoteException {
		INode member1 = getMockedNode();
		INode member2 = getMockedNode();

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
		INode leader = getMockedNode();
		INode member = getMockedNode();

		when(nameServer.getLeader(group)).thenReturn(leader);
		manager.join(group);
		manager.addToGroup(member);
		manager.removeFromGroup(member);

		assertThat(manager.peers.containsKey(member), is(false));
	}

	@Test
	public void removeFromLeader() throws RemoteException {
		INode member = getMockedNode();

		when(nameServer.getLeader(group)).thenReturn(null);
		manager.join(group);
		manager.addToGroup(member);
		manager.removeFromGroup(member);

		assertThat(manager.peers.containsKey(member), is(false));
	}

	@Test
	public void leaderShouldRemoveFromOthers() throws RemoteException {
		INode member = getMockedNode();
		INode badMember = getMockedNode();

		when(nameServer.getLeader(group)).thenReturn(null);
		manager.join(group);
		manager.addToGroup(member);
		manager.addToGroup(badMember);
		manager.removeFromGroup(badMember);

		assertThat(manager.peers.containsKey(badMember), is(false));
		assertThat(manager.peers.containsKey(member), is(true));
		verify(member).removeFromGroup(badMember);
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

	@Test
	public void leaveNoGroup() throws RemoteException {
		// Leaving without belonging to a group should be safe.
		manager.leave();
		// The groups leader must not be changed!
		verify(nameServer, never()).setLeader(group, null);
		assertThat(manager.peers.keySet().size(), is(1));
	}

	@Test
	public void leaveSingleMemberGroup() throws RemoteException {
		manager.join(group);
		manager.leave();
		// This leader should be set to null to indicate that the group is empty
		verify(nameServer).setLeader(group, null);
		assertThat(manager.peers.keySet().size(), is(1));
	}

	@Test
	public void leaveGroupWithMembers() throws RemoteException {
		setUpGroup();
		manager.leave();
		verify(leader).removeFromGroup(node);
		verify(nameServer, never()).setLeader(group, null);
		assertThat(manager.peers.keySet().size(), is(1));
	}

	@Test
	public void leaveGroupAsLeader() throws RemoteException {
		when(nameServer.getLeader(group)).thenReturn(null);
		manager.join(group);

		INode nextLeader = getMockedNode();
		manager.addToGroup(nextLeader);
		manager.leave();

		verify(nameServer).setLeader(group, nextLeader);
		verify(nextLeader).updateLeader();
		assertThat(manager.peers.keySet().size(), is(1));
	}

	@Test
	public void updateLeader() throws RemoteException {
		// Updating should be done by asking the name server for the current leader
		setUpGroup();
		manager.updateLeader();
		verify(nameServer).getLeader(group);
	}

	/**
	 * Set up a group. (Initialize the class fields members and leader).
	 * 
	 * @throws RemoteException
	 */
	private void setUpGroup() throws RemoteException {
		members = new ArrayList<>();
		leader = getMockedNode();

		when(nameServer.getLeader(group)).thenReturn(leader);
		manager.join(group);
		// Reset the name server so that any calls made during join are reset
		reset(nameServer);
		when(nameServer.getLeader(group)).thenReturn(leader);
		// A leader and a member
		members.add(leader);
		members.add(getMockedNode());

		// Add them to the manager
		members.forEach(m -> manager.addToGroup(m));
		// Make the tested manager a member of the group
		members.add(node);
	}

	private INode getMockedNode() throws RemoteException {
		INode mock = mock(INode.class);
		when(mock.getId()).thenReturn(UUID.randomUUID());
		return mock;
	}
}

package communication;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.hamcrest.CoreMatchers.*;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import gcom.INode;
import order.Message;

public class UnreliableMulticasterTest {

	UnreliableMulticaster multicaster;
	Message<String> message = new Message<>("Hello");

	@Before
	public void setUp() throws Exception {
		multicaster = new UnreliableMulticaster();
	}

	@Test
	public void testSuccessfulMulticasting() throws RemoteException {
		// Send to nobody
		List<INode> expected = Collections.emptyList();
		List<INode> actual = multicaster.multicast(message);

		assertThat(actual, is(expected));

		// Send to one
		List<INode> recipients = mockRecipients(1);
		message.setRecipients(recipients);

		actual = multicaster.multicast(message);
		assertThat(actual, is(expected));
		verifyDelivery(message, recipients);

		// Send to multiple
		recipients = mockRecipients(5);
		message.setRecipients(recipients);

		actual = multicaster.multicast(message);
		assertThat(actual, is(expected));
		verifyDelivery(message, recipients);

	}

	@Test
	public void testFailedMulticasting() throws RemoteException {
		// Send to single that fails
		List<INode> recipients = failingMockRecipients(1);
		message.setRecipients(recipients);

		List<INode> expected = recipients;
		List<INode> actual = multicaster.multicast(message);
		assertThat(actual, is(expected));
		verifyDelivery(message, recipients);

		// Send to multiple that fails
		recipients = failingMockRecipients(4);
		message.setRecipients(recipients);

		expected = recipients;
		actual = multicaster.multicast(message);
		assertThat(actual, is(expected));
		verifyDelivery(message, recipients);

		// Send to multiple, only some fail
		List<INode> failing = failingMockRecipients(2);
		List<INode> good = mockRecipients(3);
		recipients = new ArrayList<>(failing);
		recipients.addAll(good);

		message.setRecipients(recipients);

		expected = failing;
		actual = multicaster.multicast(message);
		assertThat(actual, is(expected));
		verifyDelivery(message, recipients);
	}

	private List<INode> mockRecipients(int num) {
		List<INode> recipients = new ArrayList<>();
		for (int i = 0; i < num; i++) {
			recipients.add(mock(INode.class));
		}

		return recipients;
	}

	private List<INode> failingMockRecipients(int num) throws RemoteException {
		List<INode> recipients = mockRecipients(num);
		for (INode node : recipients) {
			doThrow(new RemoteException()).when(node).deliver(any());
		}
		return recipients;
	}

	private void verifyDelivery(Message<?> message, List<INode> recipients) throws RemoteException {
		for (INode node : recipients) {
			verify(node).deliver(message);
		}
	}

}

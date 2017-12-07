package order;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.inOrder;

import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;

import communication.IMulticaster;
import gcom.ISubscriber;

public class CausalOrdererTest {

	CausalOrderer orderer;
	IMulticaster multicaster;
	Message<String> message = new Message<>(1, "Hello");
	Message<String> message2 = new Message<>(2, "Hello again");
	Message<String> message3 = new Message<>(3, "Goodbye");
	private OrdererTester tester = new OrdererTester();

	@Before
	public void setUp() throws Exception {
		multicaster = mock(IMulticaster.class);
		orderer = new CausalOrderer(multicaster);
	}

	@Test
	public void basicSendTests() {
		OrdererTester tester = new OrdererTester();
		tester.sendNoFailures(orderer, multicaster);
		tester.sendSingleFailure(orderer, multicaster);
		tester.sendMultipleFailures(orderer, multicaster);
	}

	@Test
	public void basicReceiveTests() {
		HashMap<Integer, Long> clock = new HashMap<>();
		clock.put(0, 1L);
		message.setVectorClock(clock);
		message2.setVectorClock(clock);

		orderer.setId(0);
		tester.receiveWithoutSubscriber(orderer, message);
		orderer.setId(0);
		tester.receiveSingleSubscriber(orderer, message);
		orderer.setId(0);
		tester.receiveMultipleSubscribers(orderer, message);
	}

	@Test
	public void testSubscription() {
		HashMap<Integer, Long> clock = new HashMap<>();
		clock.put(0, 1L);
		message.setVectorClock(clock);

		tester.testCancelSubscription(orderer, message);
	}

	@Test
	public void testSendVectorClock() {
		int id = 0;
		long expected = 0L;
		orderer.setId(id);
		// No send failures
		orderer.send(message);
		expected++;
		// Vector clock should have been incremented
		assertThat(message.getVectorClock().get(id), is(expected));

		// Failure should also increment
		orderer.send(message);
		expected++;
		assertThat(message.getVectorClock().get(id), is(expected));
	}

	/**
	 * A message without vector clock cannot be ordered and must thus be discarder.
	 * This could happen if a node thinks that the messages do not need to be
	 * ordered and so uses an UnorderedOrderer.
	 */
	@Test
	public void unorderedMessage() {
		assertThat(orderer.receive(message), is(false));
	}

	/**
	 * Messages from a single sender must be delivered in FIFO order (from the
	 * senders point of view).
	 */
	@Test
	public void reversedMessages() {
		// Id of sender and receiver
		int receiver = 0;
		int sender = 1;
		message.sender = sender;
		message2.sender = sender;
		orderer.setId(receiver);

		// Add subscriber
		ISubscriber sub = mock(ISubscriber.class);
		orderer.subscribe(sub);
		// We want to check the order of invocations for this subscriber
		InOrder mockOrder = inOrder(sub);

		HashMap<Integer, Long> clock1 = new HashMap<>();
		HashMap<Integer, Long> clock2 = new HashMap<>();
		// First clock is for the first message sent
		clock1.put(sender, 1L);
		// Set clock2 to 2 since this is the second message
		clock2.put(sender, 2L);
		message.setVectorClock(clock1);
		message2.setVectorClock(clock2);

		// Receive second message first
		orderer.receive(message2);
		// This message should not be delivered yet
		verify(sub, never()).deliverMessage(message2.data);
		// Receive the first message
		orderer.receive(message);

		mockOrder.verify(sub).deliverMessage(message.data);
		mockOrder.verify(sub).deliverMessage(message2.data);
	}

	/**
	 * A CausalOrderer need to wait for messages that "caused" the received message
	 * to be sent. I.e if node A sends message m1 to nodes B and C, and node B sends
	 * message m2 after delivering m1, then C must deliver m1 before m2 even if they
	 * are received in reversed order.
	 */
	@Test
	public void causalDependency() {
		int receiver = 0;
		int sender1 = 1;
		int sender2 = 2;
		message.sender = sender1;
		message2.sender = sender2;
		orderer.setId(receiver);

		// Add subscriber
		ISubscriber sub = mock(ISubscriber.class);
		orderer.subscribe(sub);
		// We want to check the order of invocations for this subscriber
		InOrder mockOrder = inOrder(sub);

		HashMap<Integer, Long> clock1 = new HashMap<>();
		HashMap<Integer, Long> clock2 = new HashMap<>();
		// First clock is for the first message sent
		clock1.put(sender1, 1L);
		// The second message "knows" about/was "caused" by the first
		clock2.put(sender1, 1L);
		clock2.put(sender2, 1L);
		message.setVectorClock(clock1);
		message2.setVectorClock(clock2);

		// Receive second message first
		orderer.receive(message2);
		// This message should not be delivered yet
		verify(sub, never()).deliverMessage(message2.data);
		// Receive the first message
		orderer.receive(message);

		mockOrder.verify(sub).deliverMessage(message.data);
		mockOrder.verify(sub).deliverMessage(message2.data);
	}
}

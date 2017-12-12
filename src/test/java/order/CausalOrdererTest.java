package order;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.inOrder;

import java.util.HashMap;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;

import communication.IMulticaster;
import gcom.ISubscriber;

public class CausalOrdererTest {

	private CausalOrderer orderer;
	private UUID id = UUID.randomUUID();
	private IMulticaster multicaster;
	private Message<String> message = new Message<>("Hello");
	private Message<String> message2 = new Message<>("Hello again");
	private OrdererTester tester = new OrdererTester();

	@Before
	public void setUp() throws Exception {
		multicaster = mock(IMulticaster.class);
		orderer = new CausalOrderer(id, multicaster);
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
		HashMap<UUID, Long> clock = new HashMap<>();
		clock.put(id, 1L);
		message.sender = id;
		message.setVectorClock(clock);

		orderer.reset();
		tester.receiveWithoutSubscriber(orderer, message);
		orderer.reset();
		tester.receiveSingleSubscriber(orderer, message);
		orderer.reset();
		tester.receiveMultipleSubscribers(orderer, message);
	}

	@Test
	public void testSubscription() {
		HashMap<UUID, Long> clock = new HashMap<>();
		clock.put(id, 1L);
		message.setVectorClock(clock);

		tester.testCancelSubscription(orderer, message);
	}

	@Test
	public void testSendVectorClock() {
		long expected = 0L;
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
		UUID receiver = id;
		UUID sender = UUID.randomUUID();
		message.sender = sender;
		message2.sender = sender;
		orderer.setId(receiver);

		// Add subscriber
		ISubscriber sub = mock(ISubscriber.class);
		orderer.subscribe(sub);
		// We want to check the order of invocations for this subscriber
		InOrder mockOrder = inOrder(sub);

		HashMap<UUID, Long> clock1 = new HashMap<>();
		HashMap<UUID, Long> clock2 = new HashMap<>();
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
		UUID receiver = id;
		UUID sender1 = UUID.randomUUID();
		UUID sender2 = UUID.randomUUID();
		message.sender = sender1;
		message2.sender = sender2;
		orderer.setId(receiver);

		// Add subscriber
		ISubscriber sub = mock(ISubscriber.class);
		orderer.subscribe(sub);
		// We want to check the order of invocations for this subscriber
		InOrder mockOrder = inOrder(sub);

		HashMap<UUID, Long> clock1 = new HashMap<>();
		HashMap<UUID, Long> clock2 = new HashMap<>();
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

	@Test
	public void testReset() {
		long expected = 0L;
		// No send failures
		orderer.send(message);
		// Not incrementing expected...
		orderer.reset();
		// Clock should now be reset
		orderer.send(message);
		expected++;
		assertThat(message.getVectorClock().get(id), is(expected));
	}
}

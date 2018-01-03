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

	private IOrderer orderer;
	private UUID id = UUID.randomUUID();
	private IMulticaster multicaster;
	private Message<String> message = new Message<>("Hello");
	private Message<String> message2 = new Message<>("Hello again");
	private OrdererTester tester = new OrdererTester();

	@Before
	public void setUp() throws Exception {
		multicaster = mock(IMulticaster.class);
		orderer = new CausalOrderer(multicaster);
		orderer.setId(id);
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
		// Send message first to initialize the message and orderer clocks.
		// Reset in between to start from clean state.
		orderer.reset();
		orderer.send(message);
		tester.receiveWithoutSubscriber(orderer, message);
		orderer.reset();
		orderer.send(message);
		tester.receiveSingleSubscriber(orderer, message);
		orderer.reset();
		orderer.send(message);
		tester.receiveMultipleSubscribers(orderer, message);
	}

	@Test
	public void testSubscription() {
		HashMap<UUID, Long> clock = new HashMap<>();
		clock.put(id, 1L);
		message.sender = id;
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
		// Clock should be incremented
		assertThat(orderer.debugGetMessagesSent(), is(1L));

		// Failure should also increment
		orderer.send(message);
		expected++;
		assertThat(message.getVectorClock().get(id), is(expected));
		assertThat(orderer.debugGetMessagesSent(), is(2L));
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
		// Initial message to initialize the clock
		Message<String> initial = new Message<>("Initial");
		// Id of sender
		UUID sender = UUID.randomUUID();
		message.sender = sender;
		message2.sender = sender;
		initial.sender = sender;

		// Add subscriber
		ISubscriber sub = mock(ISubscriber.class);
		orderer.subscribe(sub);
		// We want to check the order of invocations for this subscriber
		InOrder mockOrder = inOrder(sub);

		HashMap<UUID, Long> clockInit = new HashMap<>();
		HashMap<UUID, Long> clock1 = new HashMap<>();
		HashMap<UUID, Long> clock2 = new HashMap<>();

		clockInit.put(sender, 1L);
		// First clock is for the first (of the reversed) message sent
		clock1.put(sender, 2L);
		// This is for the second (reversed) message
		clock2.put(sender, 3L);
		initial.setVectorClock(clockInit);
		message.setVectorClock(clock1);
		message2.setVectorClock(clock2);

		orderer.receive(initial);

		// Receive second message first
		orderer.receive(message2);
		// This message should not be delivered yet
		verify(sub, never()).deliverMessage(message2.data);
		assertThat(orderer.debugGetBuffer().size(), is(1));
		// Receive the first message
		orderer.receive(message);

		mockOrder.verify(sub).deliverMessage(message.data);
		mockOrder.verify(sub).deliverMessage(message2.data);
		// The buffer should be empty now
		assertThat(orderer.debugGetBuffer().isEmpty(), is(true));
		// Vector clock should have been updated
		assertThat(orderer.debugGetVectorClock().get(sender), is(clock2.get(sender)));
	}

	/**
	 * A CausalOrderer need to wait for messages that "caused" the received message
	 * to be sent. I.e if node A sends message m1 to nodes B and C, and node B sends
	 * message m2 after delivering m1, then C must deliver m1 before m2 even if they
	 * are received in reversed order.
	 */
	@Test
	public void causalDependency() {
		UUID sender1 = UUID.randomUUID();
		UUID sender2 = UUID.randomUUID();
		message.sender = sender1;
		message2.sender = sender2;

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
		// The buffer should be empty now
		assertThat(orderer.debugGetBuffer().isEmpty(), is(true));
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

	/**
	 * A new member must be able to receive messages also from older members, whose
	 * clocks are not "starting" from 0.
	 */
	@Test
	public void joinOldGroup() {
		UUID sender = UUID.randomUUID();
		HashMap<UUID, Long> clock = new HashMap<>();
		clock.put(sender, 4L);
		message.setVectorClock(clock);
		message.sender = sender;

		ISubscriber sub = mock(ISubscriber.class);
		orderer.subscribe(sub);

		orderer.receive(message);

		verify(sub).deliverMessage(message.data);
	}

	/**
	 * When joining an old group, it is possible to first receive a new message and
	 * later an old. The old message must not be delivered in this case, since it
	 * would violate the causal order.
	 */
	@Test
	public void joinOldAndReceiveReversed() {
		// Id of sender
		UUID sender = UUID.randomUUID();
		message.sender = sender;
		message2.sender = sender;

		// Add subscriber
		ISubscriber sub = mock(ISubscriber.class);
		orderer.subscribe(sub);

		HashMap<UUID, Long> clock1 = new HashMap<>();
		HashMap<UUID, Long> clock2 = new HashMap<>();
		// First clock is for the first message sent
		clock1.put(sender, 2L);
		// This is for the second message (received first)
		clock2.put(sender, 3L);
		message.setVectorClock(clock1);
		message2.setVectorClock(clock2);

		// Receive second message first
		orderer.receive(message2);
		// This message should be delivered immediately
		verify(sub).deliverMessage(message2.data);
		// Receive the first message, this must not be delivered!
		orderer.receive(message);
		verify(sub, never()).deliverMessage(message.data);
	}
}

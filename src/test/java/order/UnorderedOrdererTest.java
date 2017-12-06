package order;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.*;

import org.junit.Before;
import org.junit.Test;

import communication.IMulticaster;
import gcom.ISubscriber;

public class UnorderedOrdererTest {

	UnorderedOrderer orderer;
	IMulticaster multicaster;
	Message<String> message = new Message<>(1, "Hello");
	Message<String> message2 = new Message<>(2, "Hello again");
	Message<String> message3 = new Message<>(3, "Goodbye");

	@Before
	public void setUp() throws Exception {
		multicaster = mock(IMulticaster.class);
		orderer = new UnorderedOrderer(multicaster);
	}

	@Test
	public void sendTest() {
		// No failures
		List<Integer> expected = Collections.emptyList();
		when(multicaster.multicast(message)).thenReturn(expected);

		List<Integer> actual = orderer.send(message);

		assertThat(actual, is(expected));
		verify(multicaster).multicast(message);

		// Single failure
		expected = Arrays.asList(2);
		when(multicaster.multicast(message)).thenReturn(expected);

		actual = orderer.send(message);

		assertThat(actual, is(expected));

		// Multiple failures
		expected = Arrays.asList(2, 3, 4);
		when(multicaster.multicast(message)).thenReturn(expected);

		actual = orderer.send(message);

		assertThat(actual, is(expected));
	}

	@Test
	public void receiveTest() {
		// Send message to nobody
		assertThat(orderer.receive(message), is(true));

		// Add subscriber
		ISubscriber sub = mock(ISubscriber.class);
		orderer.subscribe(sub);

		assertThat(orderer.receive(message), is(true));
		verify(sub).deliverMessage(message.data);

		// Add second subscriber
		ISubscriber sub2 = mock(ISubscriber.class);
		orderer.subscribe(sub2);

		assertThat(orderer.receive(message2), is(true));
		verify(sub).deliverMessage(message2.data);
		verify(sub2).deliverMessage(message2.data);

		// Remove subscribers
		orderer.cancelSubscription(sub);
		orderer.cancelSubscription(sub2);

		assertThat(orderer.receive(message3), is(true));
		verify(sub, never()).deliverMessage(message3.data);
		verify(sub2, never()).deliverMessage(message3.data);
	}

	@Test
	public void setMulticasterTest() {
		orderer.send(message);
		verify(multicaster).multicast(message);

		IMulticaster newMulticaster = mock(IMulticaster.class);
		orderer.setMulticaster(newMulticaster);
		orderer.send(message);
		verify(newMulticaster).multicast(message);
	}

}

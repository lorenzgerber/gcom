package order;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import communication.IMulticaster;
import gcom.INode;
import gcom.ISubscriber;

public class OrdererTester {

	Message<String> message = new Message<>(1, "Hello");
	Message<String> message2 = new Message<>(2, "Hello again");
	Message<String> message3 = new Message<>(3, "Goodbye");

	public void sendNoFailures(IOrderer orderer, IMulticaster mock) {
		List<INode> expected = Collections.emptyList();
		when(mock.multicast(message)).thenReturn(expected);

		List<INode> actual = orderer.send(message);

		assertThat(actual, is(expected));
		verify(mock).multicast(message);
	}

	public void sendSingleFailure(IOrderer orderer, IMulticaster mock) {
		List<INode> expected = Arrays.asList(mock(INode.class));
		when(mock.multicast(message)).thenReturn(expected);

		List<INode> actual = orderer.send(message);

		assertThat(actual, is(expected));
	}

	public void sendMultipleFailures(IOrderer orderer, IMulticaster mock) {
		List<INode> expected = Arrays.asList(mock(INode.class), mock(INode.class), mock(INode.class));
		when(mock.multicast(message)).thenReturn(expected);

		List<INode> actual = orderer.send(message);

		assertThat(actual, is(expected));
	}

	public void testReceive(IOrderer orderer) {
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

}

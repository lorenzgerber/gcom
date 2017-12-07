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

	private Message<String> message = new Message<>(1, "Hello");

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

	public void receiveWithoutSubscriber(IOrderer orderer, Message<?> message) {
		assertThat(orderer.receive(message), is(true));
	}

	public void receiveSingleSubscriber(IOrderer orderer, Message<?> message) {
		ISubscriber sub = mock(ISubscriber.class);
		orderer.subscribe(sub);

		assertThat(orderer.receive(message), is(true));
		verify(sub).deliverMessage(message.data);
	}

	public void receiveMultipleSubscribers(IOrderer orderer, Message<?> message) {
		ISubscriber sub = mock(ISubscriber.class);
		ISubscriber sub2 = mock(ISubscriber.class);
		orderer.subscribe(sub);
		orderer.subscribe(sub2);

		assertThat(orderer.receive(message), is(true));
		verify(sub).deliverMessage(message.data);
		verify(sub2).deliverMessage(message.data);
	}

	public void testCancelSubscription(IOrderer orderer, Message<?> message) {
		ISubscriber sub = mock(ISubscriber.class);
		orderer.subscribe(sub);

		orderer.unSubscribe(sub);

		assertThat(orderer.receive(message), is(true));
		verify(sub, never()).deliverMessage(message.data);
	}

}

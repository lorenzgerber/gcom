package order;

import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;

import communication.IMulticaster;

public class UnorderedOrdererTest {

	UnorderedOrderer orderer;
	IMulticaster multicaster;
	Message<String> message = new Message<>(1, "Hello");
	Message<String> message2 = new Message<>(2, "Hello again");
	Message<String> message3 = new Message<>(3, "Goodbye");
	private OrdererTester tester = new OrdererTester();

	@Before
	public void setUp() throws Exception {
		multicaster = mock(IMulticaster.class);
		orderer = new UnorderedOrderer(multicaster);
	}

	@Test
	public void sendTests() {
		OrdererTester tester = new OrdererTester();
		tester.sendNoFailures(orderer, multicaster);
		tester.sendSingleFailure(orderer, multicaster);
		tester.sendMultipleFailures(orderer, multicaster);
	}

	@Test
	public void receiveTest() {
		tester.receiveWithoutSubscriber(orderer, message);
		tester.receiveSingleSubscriber(orderer, message);
	}

	@Test
	public void testSubscription() {
		tester.testCancelSubscription(orderer, message);
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

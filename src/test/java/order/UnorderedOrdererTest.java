package order;

import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;

import communication.IMulticaster;

public class UnorderedOrdererTest {

	private IOrderer orderer;
	private IMulticaster multicaster;
	private Message<String> message = new Message<>("Hello");
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
		tester.receiveMultipleSubscribers(orderer, message);
	}

	@Test
	public void testSubscription() {
		tester.testCancelSubscription(orderer, message);
	}

}

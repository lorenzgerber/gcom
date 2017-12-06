package order;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;

import communication.IMulticaster;

public class CausalOrdererTest {

	CausalOrderer orderer;
	IMulticaster multicaster;
	Message<String> message = new Message<>(1, "Hello");
	Message<String> message2 = new Message<>(2, "Hello again");
	Message<String> message3 = new Message<>(3, "Goodbye");

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
		OrdererTester tester = new OrdererTester();
		tester.testReceive(orderer);
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

}

package gcom;

import static org.mockito.Mockito.*;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class UnreliableBasicBroadcasterTest {
	private UnreliableBasicBroadcaster broadcaster;
	private List<ChatServer> recipents;

	@Before
	public void setUp() throws Exception {
		broadcaster = new UnreliableBasicBroadcaster();
		recipents = new ArrayList<>();
		recipents.add(mock(ChatServer.class));
	}

	@Test
	public void sendMessageToNobody() throws RemoteException {
		String message = "Hello!";
		List<ChatServer> recipents = Collections.emptyList();
		broadcaster.broadcastMessage(message, recipents);
	}
	
	@Test
	public void sendMessageToSomebody() throws RemoteException {
		String message = "Hello!";
		broadcaster.broadcastMessage(message, recipents);
		verify(recipents.get(0)).deliverMessage(message);
	}
	
	@Test
	public void sendToMultiple() throws RemoteException {
		String message = "Hello all!";
		recipents.add(mock(ChatServer.class));
		broadcaster.broadcastMessage(message, recipents);
		
		for (ChatServer rec : recipents) {
			verify(rec).deliverMessage(message);
		}
	}

}

package gcom;

import java.rmi.RemoteException;
import java.util.List;

public class UnreliableBasicBroadcaster {

	public void broadcastMessage(String message, List<ChatServer> recipents) throws RemoteException {
		for (ChatServer receiver : recipents) {
			receiver.deliverMessage(message);
		}
	}
}

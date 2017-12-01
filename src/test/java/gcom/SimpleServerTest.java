package gcom;

import static org.junit.Assert.*;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class SimpleServerTest {

	private SimpleServer server;
	private NameServer nameServer;

	@BeforeClass
	public static void initialize() {
		try {
			LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Before
	public void setUp() throws Exception {
		// The SimpleServer needs an available NameServer so this must be created first.
		nameServer = new SimpleNameServer();
		server = new SimpleServer("TestServer");
	}

	@Test
	public void sendMessageToNobody() throws RemoteException {
		server.sendMessage("Hello");
	}

}

package chatapp;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import gcom.GCom;
import gcom.INode;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import order.DebugOrderer;
import order.IDebugOrdererSubscriber;

public class DebugAppController extends Parent implements IDebugOrdererSubscriber {

	public GCom node;

	@FXML
	private CheckBox holdMessages;

	@FXML
	private Button releaseMessages;

	@FXML
	private ListView<String> messageBuffer;

	@FXML
	private ListView<String> vectorClocks;

	@FXML
	private ListView<String> heldMessages;

	@FXML
	private ListView<String> currentLeaders;

	public void intitialize() {

	}

	@FXML
	private void holdMessages() {
		if (holdMessages.isSelected()) {
			node.getOrdererDebugger().holdMessages(true);
		} else {
			node.getOrdererDebugger().holdMessages(false);
		}
	}

	@FXML
	private void releaseMessages() {
		node.getOrdererDebugger().releaseMessages();
		holdMessages.setSelected(false);
		debugEventOccured();
	}

	public void setNode(GCom node) {
		this.node = node;
		this.node.getOrdererDebugger().debugSubscribe(this);
	}

	protected void updateVectorClock() {
		DebugOrderer debugger = node.getOrdererDebugger();
		if (debugger.debugGetVectorClock() == null) {
			// The ordered does not appear to use a vector clock...
			return;
		}

		List<String> clocks = debugger.debugGetVectorClock().entrySet().stream()
				.map(entry -> entry.getKey().toString().substring(0, 10) + " " + entry.getValue().toString())
				.collect(Collectors.toList());
		ObservableList<String> items = FXCollections.observableArrayList(clocks);

		Platform.runLater(() -> vectorClocks.setItems(items));
	}

	private void updateMessageBuffer() {
		DebugOrderer debugger = node.getOrdererDebugger();
		List<String> buffered = debugger.debugGetBuffer().stream().map(m -> m.data.toString())
				.collect(Collectors.toList());

		ObservableList<String> items = FXCollections.observableArrayList(buffered);

		Platform.runLater(() -> messageBuffer.setItems(items));
	}

	private void updateHeldMessages() {
		DebugOrderer debugger = node.getOrdererDebugger();
		List<String> held = debugger.debugHeldMessages().stream().map(m -> m.data.toString())
				.collect(Collectors.toList());
		ObservableList<String> messages = FXCollections.observableArrayList(held);

		Platform.runLater(() -> heldMessages.setItems(messages));
	}

	private void updateLeaders() {
		try {
			HashMap<String, INode> test = node.getNodeList();
			test.values().removeIf(Objects::isNull);
			test.keySet().removeIf(Objects::isNull);
			List<String> leaders = test.entrySet().stream().map(entry -> {
				try {
					return entry.getKey().toString() + " " + entry.getValue().getId().toString().substring(0, 10);
				} catch (RemoteException e) {
					return null;
				}
			}).filter(Objects::nonNull).collect(Collectors.toList());
			ObservableList<String> items = FXCollections.observableArrayList(leaders);

			Platform.runLater(() -> currentLeaders.setItems(items));
		} catch (RemoteException e1) {
			System.err.println("Unable to reach name server!");
		}
	}

	@Override
	public void debugEventOccured() {
		updateHeldMessages();
		updateMessageBuffer();
		updateVectorClock();
		updateLeaders();
	}

}

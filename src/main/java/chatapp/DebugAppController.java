package chatapp;

import java.util.List;
import java.util.stream.Collectors;

import gcom.GCom;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import order.DebugOrderer;
import order.IDebugSubscriber;

public class DebugAppController extends Parent implements IDebugSubscriber {

	private GCom node;

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

	public void intitialize() {

	}

	@FXML
	private void holdMessages() {
		if (holdMessages.isSelected()) {
			node.getDebugger().holdMessages(true);
		} else {
			node.getDebugger().holdMessages(false);
		}
	}

	@FXML
	private void releaseMessages() {
		node.getDebugger().releaseMessages();
		holdMessages.setSelected(false);
		eventOccured();
	}

	public void setNode(GCom node) {
		this.node = node;
		this.node.getDebugger().debugSubscribe(this);
	}

	protected void updateVectorClock() {
		DebugOrderer debugger = node.getDebugger();
		if (debugger.debugGetVectorClock() == null) {
			// The ordered does not appear to use a vector clock...
			return;
		}

		List<String> clocks = debugger.debugGetVectorClock().entrySet().stream()
				.map(entry -> entry.getKey().toString() + " " + entry.getValue().toString())
				.collect(Collectors.toList());
		ObservableList<String> items = FXCollections.observableArrayList(clocks);
		vectorClocks.setItems(items);
	}

	private void updateMessageBuffer() {
		DebugOrderer debugger = node.getDebugger();
		List<String> buffered = debugger.debugGetBuffer().stream().map(m -> m.data.toString())
				.collect(Collectors.toList());

		ObservableList<String> items = FXCollections.observableArrayList(buffered);
		messageBuffer.setItems(items);
	}

	private void updateHeldMessages() {
		DebugOrderer debugger = node.getDebugger();
		List<String> held = debugger.debugHeldMessages().stream().map(m -> m.data.toString())
				.collect(Collectors.toList());
		ObservableList<String> messages = FXCollections.observableArrayList(held);
		heldMessages.setItems(messages);
	}

	@Override
	public void eventOccured() {
		updateHeldMessages();
		updateMessageBuffer();
		updateVectorClock();
	}

}

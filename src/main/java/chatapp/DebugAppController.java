package chatapp;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import gcom.GCom;
import gcom.INode;
import group.DebugGroupManager;
import group.IDebugGroupManagerSubscriber;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import order.DebugOrderer;
import order.IDebugOrdererSubscriber;

public class DebugAppController extends Parent implements IDebugOrdererSubscriber, IDebugGroupManagerSubscriber {

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
		ordererEventOccured();
	}

	public void setNode(GCom node) {
		this.node = node;
		this.node.getOrdererDebugger().debugSubscribe(this);
		this.node.getGroupManagerDebugger().debugSubscribe(this);
	}

	protected void updateVectorClock() {
		DebugOrderer debugger = node.getOrdererDebugger();
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
		DebugOrderer debugger = node.getOrdererDebugger();
		List<String> buffered = debugger.debugGetBuffer().stream().map(m -> m.data.toString())
				.collect(Collectors.toList());

		ObservableList<String> items = FXCollections.observableArrayList(buffered);
		messageBuffer.setItems(items);
	}

	private void updateHeldMessages() {
		DebugOrderer debugger = node.getOrdererDebugger();
		List<String> held = debugger.debugHeldMessages().stream().map(m -> m.data.toString())
				.collect(Collectors.toList());
		ObservableList<String> messages = FXCollections.observableArrayList(held);
		heldMessages.setItems(messages);
	}
	
	private void updateLeaders() {
		DebugGroupManager debugger = node.getGroupManagerDebugger();
		HashMap<String, INode> test = debugger.getNodeList();
		/*List<String> leaders = debugger.getNodeList().entrySet().stream()
				.map(entry -> entry.getKey().toString() + " " + entry.getValue()
				.toString()).collect(Collectors.toList());
		ObservableList<String> items = FXCollections.observableArrayList(leaders);
		currentLeaders.setItems(items);*/
	}

	@Override
	public void ordererEventOccured() {
		updateHeldMessages();
		updateMessageBuffer();
		updateVectorClock();
	}

	@Override
	public void groupManagerEventOccured() {
		updateLeaders();
		
	}

}

package chatapp;

import java.rmi.RemoteException;

import gcom.GCom;
import gcom.INode;
import gcom.ISubscriber;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import order.IDebugOrdererSubscriber;

public class ChatAppController extends Parent implements ISubscriber {

	private ChatApp app;
	private GCom node;
	private String nickName;
	private boolean surpressJoin;
	private IDebugOrdererSubscriber debugger;

	@FXML
	private TextField inputArea;

	@FXML
	private TextArea messageArea;

	@FXML
	private ComboBox<String> groups;

	@FXML
	private TextField createGroupField;

	@FXML
	private void sendButtonPressed(ActionEvent event) {
		sendMessage();
	}

	@FXML
	private void handleKeyEvent(KeyEvent event) {
		// Send message if enter is pressed
		if (event.getCode().equals(KeyCode.ENTER)) {
			sendMessage();
		}
	}

	@FXML
	private void loadGroups() {
		surpressJoin = true;
		try {
			ObservableList<String> groupNames = FXCollections.observableArrayList(node.getGroups());
			groups.setItems(groupNames);
		} catch (RemoteException e) {
			System.err.println("Unable to get list of groups!");
			e.printStackTrace();
		}
		surpressJoin = false;
		notifyDebugger();
	}

	@FXML
	private void groupHandler(ActionEvent event) {
		if (!surpressJoin) {
			joinGroup(groups.getValue());
		}
		notifyDebugger();
	}

	@FXML
	private void createGroupButtonPressed() {
		joinGroup(createGroupField.getText());
		createGroupField.clear();
		notifyDebugger();
	}

	@FXML
	private void leaveButtonPressed() {
		node.leave();
		notifyDebugger();
	}

	public void initialize() {

	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public void setChatApp(ChatApp app) {
		this.app = app;
	}

	public void setNode(GCom node) {
		this.node = node;
	}

	public void setSubscriber() {
		this.app.setSubscriber(this);
	}

	private void sendMessage() {
		StringBuilder message = new StringBuilder();
		message.append(this.nickName);
		message.append(": ");
		message.append(inputArea.getText());
		node.Send(message.toString());
		inputArea.clear();
	}

	private void joinGroup(String name) {
		try {
			node.leave();
			node.join(name);
			System.out.println("Joined group: " + name);
			INode myNode = (INode) node;
			StringBuilder title = new StringBuilder();
			title.append("GCOM - Node: ");
			title.append(myNode.getId().toString());
			app.setTitle(title.toString());
		} catch (RemoteException e) {
			System.err.println("Unable to join group!");
			e.printStackTrace();
		}
		loadGroups();
		surpressJoin = true;
		groups.setValue(name);
		surpressJoin = false;
	}

	@Override
	public <T> void deliverMessage(T message) {
		String msg = (String) message;
		messageArea.appendText(msg + "\n");
		notifyDebugger();
	}

	protected void setDebugSubscriber(IDebugOrdererSubscriber debugger) {
		this.debugger = debugger;
	}

	private void notifyDebugger() {
		if (debugger != null) {
			debugger.debugEventOccured();
		}
	}
}

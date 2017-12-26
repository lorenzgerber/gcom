package chatapp;

import java.rmi.RemoteException;

import gcom.ISubscriber;
import gcom.Node;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class DebugAppController implements ISubscriber {
	
	private ChatApp app;
	private Node node;
	private String nickName;

	
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
		try {
			ObservableList<String> groupNames = FXCollections.observableArrayList(node.getGroups());
			groups.setItems(groupNames);
		} catch (RemoteException e) {
			System.err.println("Unable to get list of groups!");
			e.printStackTrace();
		}
	}

	@FXML
	private void groupHandler(ActionEvent event) {
		joinGroup(groups.getValue());
	}

	@FXML
	private void createGroupButtonPressed() {
		joinGroup(createGroupField.getText());
		createGroupField.clear();
	}

	@FXML
	private void leaveButtonPressed() {
		node.leave();
	}

	public void initialize() {

	}
	
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	
	public void setChatApp(ChatApp app) {
		this.app = app;
	}
	
	public void setNode(Node node) {
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
		} catch (RemoteException e) {
			System.err.println("Unable to join group!");
			e.printStackTrace();
		}
		loadGroups();
		groups.setValue(name);
	}

	@Override
	public <T> void deliverMessage(T message) {
		String msg = (String) message;
		messageArea.appendText(msg + "\n");
	}

}

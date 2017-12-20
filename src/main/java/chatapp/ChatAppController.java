package chatapp;

import java.rmi.RemoteException;

import gcom.GCom;
import gcom.ISubscriber;
import gcom.Node;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class ChatAppController implements ISubscriber {

	@FXML
	private TextField inputArea;

	@FXML
	private TextArea messageArea;

	@FXML
	private ComboBox<String> groups;

	@FXML
	private TextField createGroupField;

	private GCom node;

	public ChatAppController() {
		try {
			node = new Node("localhost");
			node.subscribe(this);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

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
		System.out.println("Load groups");
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

	private void sendMessage() {
		node.Send(inputArea.getText());
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

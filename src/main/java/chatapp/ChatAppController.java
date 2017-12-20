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
	private void groupHandler(ActionEvent event) {
		try {
			node.leave();
			node.join(groups.getValue());
			System.out.println("Joined group: " + groups.getValue());
		} catch (RemoteException e) {
			System.err.println("Unable to join group!");
			e.printStackTrace();
		}
	}

	public void initialize() {

	}

	private void sendMessage() {
		node.Send(inputArea.getText());
		inputArea.clear();
	}

	@FXML
	private void loadGroups() {
		System.out.println("Load groups");
	}

	@Override
	public <T> void deliverMessage(T message) {
		String msg = (String) message;
		messageArea.appendText(msg + "\n");
	}

}

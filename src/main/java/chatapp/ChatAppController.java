package chatapp;

import java.rmi.RemoteException;

import gcom.GCom;
import gcom.ISubscriber;
import gcom.Node;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class ChatAppController implements ISubscriber {

	@FXML
	private TextField inputArea;

	@FXML
	private TextArea messageArea;

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

	public void initialize() {

	}

	private void sendMessage() {
		node.Send(inputArea.getText());
		inputArea.clear();
	}

	@Override
	public <T> void deliverMessage(T message) {
		String msg = (String) message;
		messageArea.appendText(msg + "\n");
	}

}

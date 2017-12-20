package chatapp;

import java.rmi.RemoteException;

import gcom.GCom;
import gcom.ISubscriber;
import gcom.Node;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

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
		node.Send(inputArea.getText());
		inputArea.clear();
	}

	public void initialize() {

	}

	@Override
	public <T> void deliverMessage(T message) {
		String msg = (String) message;
		messageArea.appendText(msg + "\n");
	}

}

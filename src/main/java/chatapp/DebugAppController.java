package chatapp;

import gcom.GCom;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;

public class DebugAppController extends Parent {

	private GCom node;
	private ChatApp app;

	@FXML
	private CheckBox holdMessages;

	@FXML
	private Button releaseMessages;
	
	@FXML
	private ChatApp chatApp;

	public void intitialize() {

	}

	@FXML
	private void holdMessages() {
		if (holdMessages.isArmed()) {
			node.getDebugger().holdMessages(true);
		} else {
			node.getDebugger().holdMessages(false);
		}
	}

	@FXML
	private void releaseMessages() {
		node.getDebugger().releaseMessages();
	}

	public void setChatApp(ChatApp app) {
		this.chatApp = app;
	}

	public void setNode(GCom node) {
		this.node = node;
	}

	public void setSubscriber() {
		this.app.setSubscriber(chatApp);
	}

}

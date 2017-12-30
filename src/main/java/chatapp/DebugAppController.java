package chatapp;


import gcom.Node;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;

public class DebugAppController extends Parent {

	private Node node;
	private ChatApp app;

	@FXML
	private CheckBox holdMessages;

	@FXML
	private Button releaseMessages;
	

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
		this.app = app;
	}

	public void setNode(Node node) {
		this.node = node;
	}

	public void setSubscriber() {
		this.app.setSubscriber(this.app);
	}

}

package chatapp;

import gcom.GCom;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;

public class DebugAppController extends Parent {

	private GCom node;

	@FXML
	private CheckBox holdMessages;

	@FXML
	private Button releaseMessages;

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
	}

	public void setNode(GCom node) {
		this.node = node;
	}

}

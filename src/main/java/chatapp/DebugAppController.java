package chatapp;

import gcom.GCom;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.CheckBox;

public class DebugAppController extends Parent {

	private GCom node;

	@FXML
	private CheckBox holdMessages;

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

}

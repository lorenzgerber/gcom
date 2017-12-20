package chatapp;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.stage.Stage;

public class StartMenuController extends Parent {

	ChatApp parent;

	public void setApp(ChatApp parent) {
		this.parent = parent;
	}

	@FXML
	private void chatAppButtonPressed(ActionEvent event) {
		startChatApp();
	}

	@FXML
	private void debugAppButtonPressed(ActionEvent event) {
		startDebugApp();
	}

	public Stage startChatApp() {
		try {
			parent.replaceSceneContent("ChatApp.fxml");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return parent.primaryStage;
	}

	public Stage startDebugApp() {
		try {
			parent.replaceSceneContent("DebugApp.fxml");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return parent.primaryStage;
	}

	public void initialize() {

	}

}

package chatapp;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource(ChatApp.appFxml));
		// TODO: Setup controller with GCom node, orderer, multicaster...
		loader.setController(new ChatAppController());
		try {
			parent.replaceSceneContent(ChatApp.appFxml);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return parent.primaryStage;
	}

	public Stage startDebugApp() {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource(ChatApp.debugFxml));
		loader.setController(new DebugAppController());
		try {
			parent.replaceSceneContent(ChatApp.debugFxml);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return parent.primaryStage;
	}

	public void initialize() {

	}

}

package chatapp;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextField;
import javafx.scene.Parent;
import javafx.stage.Stage;

public class StartMenuController extends Parent {

	ChatApp parent;

	@FXML
	private TextField nickName;
	
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

		ChatAppController controller = new ChatAppController();
		controller.setNickName(nickName.getText());
		controller.setChatApp(parent);
		controller.setNode(parent.node);
		controller.setSubscriber();
		loader.setController(controller);
		try {
			parent.replaceSceneContent(loader);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return parent.primaryStage;
	}

	public Stage startDebugApp() {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource(ChatApp.debugFxml));

		DebugAppController controller = new DebugAppController();
		controller.setChatApp(parent);
		controller.setNode(parent.node);
		controller.setSubscriber();
		loader.setController(controller);
		try {
			parent.replaceSceneContent(loader);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return parent.primaryStage;
	}

	public void initialize() {

	}

}

package chatapp;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.Parent;

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

	public void startChatApp() {

		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource(ChatApp.appFxml));
		Parent root = null;
		try {
			root = loader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
		ChatAppController controller = loader.getController();
		controller.setNickName(nickName.getText());
		controller.setChatApp(parent);
		controller.setNode(parent.node);
		controller.setSubscriber();
		parent.replaceScene(root, 500, 300);

	}

	public void startDebugApp() {

		VBox root = new VBox();

		FXMLLoader loaderChat = new FXMLLoader();
		loaderChat.setLocation(getClass().getResource(ChatApp.appFxml));

		try {
			root.getChildren().add(loaderChat.load());
		} catch (IOException e) {
			e.printStackTrace();
		}
		ChatAppController controllerChat = loaderChat.getController();
		controllerChat.setNickName(nickName.getText());
		controllerChat.setChatApp(parent);
		controllerChat.setNode(parent.node);
		controllerChat.setSubscriber();

		FXMLLoader loaderDebug = new FXMLLoader();
		loaderDebug.setLocation(getClass().getResource(ChatApp.debugFxml));

		try {
			root.getChildren().add(loaderDebug.load());
		} catch (IOException e) {
			e.printStackTrace();
		}

		DebugAppController controllerDebug = loaderDebug.getController();

		controllerDebug.setNode(parent.node);
		parent.replaceScene(root, 680, 700);

	}

	public void initialize() {

	}

}

package chatapp;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class StartMenuController {
	
	ChatApp app = null;
	
	@FXML
	private void chatAppButtonPressed(ActionEvent event) {
		// do some stuff on button press
		System.out.println("Open new window");
		startChatApp();
	}
	
	@FXML
	private void debugAppButtonPressed(ActionEvent event) {
		// do some stuff on button press
		System.out.println("Open new window");
		startDebugApp();
	}
	
	@FXML
	private TextField nickName;
	
	public Stage startChatApp() {
		
		FXMLLoader loader = new FXMLLoader(
				getClass().getResource("ChatApp.fxml"));
		
		Stage stage = new Stage(StageStyle.DECORATED);
		try {
			stage.setScene(	new Scene( (Pane) loader.load()));
		} catch (IOException e) {
			// ignore for the moment
		}
		
		ChatAppController chatController = 
				loader.<ChatAppController>getController();
		
		// configuring the ChatApp instance
		chatController.setNickName(this.nickName.getText());
		chatController.setChatApp(this.app);
		chatController.setNode(this.app.getNode());
		chatController.setSubscriber();
		
		stage.show();
		return stage;
	}
	
	public Stage startDebugApp() {
		
		FXMLLoader loader = new FXMLLoader(
				getClass().getResource("DebugApp.fxml"));
		
		Stage stage = new Stage(StageStyle.DECORATED);
		try {
			stage.setScene(	new Scene( (Pane) loader.load()));
		} catch (IOException e) {
			// ignore for the moment
		}
		
		DebugAppController debugController = 
				loader.<DebugAppController>getController();
		
		// configuring the ChatApp instance
		debugController.setNickName(this.nickName.getText());
		debugController.setChatApp(this.app);
		debugController.setNode(this.app.getNode());
		debugController.setSubscriber();
		
		stage.show();
		return stage;
	}
	
	public void setChatApp(ChatApp app) {
		this.app = app;
	}
	
	
	public void initialize() {
		
	}

}

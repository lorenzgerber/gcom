package chatapp;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class ChatAppController {
	
	@FXML 
	private TextField inputArea; 

	@FXML
	private TextArea messageArea; 
	
	@FXML
	private void sendButtonPressed(ActionEvent event) {
		// do some stuff on button press
		System.out.println("Open new window");
		showStartMenu();
	}
	
	
	public Stage showStartMenu() {
		
		FXMLLoader loader = new FXMLLoader(
				getClass().getResource("startMenu.fxml"));
		
		Stage stage = new Stage(StageStyle.DECORATED);
		try {
			stage.setScene(	new Scene( (Pane) loader.load()));
		} catch (IOException e) {
			// ignore for the moment
		}
		
		StartMenuController controller = 
				loader.<StartMenuController>getController();
		
		stage.show();
		
		
		return stage;
	}
	

	
	public void initialize() {
		
	}

}

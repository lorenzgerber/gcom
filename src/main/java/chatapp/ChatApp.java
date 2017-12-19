package chatapp;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class ChatApp extends Application {

	@Override
	public void start(Stage stage) throws Exception {
		  Parent root = FXMLLoader.load(getClass().getResource("ChatApp.fxml"));
		  
		  Scene scene = new Scene(root);
		  stage.setTitle("Chat App");
		  stage.setScene(scene);
		  stage.show();    	
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
		
		StartMenuController controller = loader.<StartMenuController>getController();
		
		stage.show();
		
		
		return stage;
	}
	
	
	public static void main(String[] args) {	      
		launch(args); 		   
	}
	

}

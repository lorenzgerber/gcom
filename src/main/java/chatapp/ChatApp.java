package chatapp;


import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ChatApp extends Application {
	
	Stage primaryStage;

	@Override
	public void start(Stage stage) throws Exception {
		
		primaryStage = stage;
		primaryStage.setOnCloseRequest(e -> {
			Platform.exit();
			System.exit(0);
		});
		
		
		
		  Parent root = FXMLLoader.load(getClass().getResource("StartMenu.fxml"));
		  
		  Scene scene = new Scene(root);
		  stage.setTitle("GCOM Demo Apps");
		  stage.setScene(scene);
		  stage.show();    	
	}
	
	
	
	
	public static void main(String[] args) {	      
		launch(args); 		   
	}
	

}

package chatapp;


import gcom.Node;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ChatApp extends Application {
	
	Stage primaryStage;
	Node node;

	@Override
	public void start(Stage stage) throws Exception {
		
		primaryStage = stage;
		primaryStage.setOnCloseRequest(e -> {
			Platform.exit();
			System.exit(0);
		});
		
		FXMLLoader loader = new FXMLLoader(getClass().getResource("StartMenu.fxml"));
		Parent root = loader.load();
		StartMenuController startMenuController = loader.getController();

		Scene scene = new Scene(root);
		stage.setTitle("GCOM Demo Apps");
		stage.setScene(scene);
		stage.show();
	}
	
	
	
	
	public static void main(String[] args) {	      
		launch(args); 		   
	}
	

}

package chatapp;


import java.rmi.RemoteException;

import gcom.ISubscriber;
import gcom.Node;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ChatApp extends Application implements ISubscriber {
	
	Stage primaryStage;
	Node node;
	ISubscriber subscriber;

	@Override
	public void start(Stage stage) throws Exception {
		
		try {
			node = new Node("localhost");
			node.subscribe(this);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		primaryStage = stage;
		primaryStage.setOnCloseRequest(e -> {
			Platform.exit();
			System.exit(0);
		});
		
		FXMLLoader loader = new FXMLLoader(getClass().getResource("StartMenu.fxml"));
		Parent root = loader.load();
		StartMenuController startMenuController = loader.getController();
		startMenuController.setChatApp(this);

		Scene scene = new Scene(root);
		stage.setTitle("GCOM Demo Apps");
		stage.setScene(scene);
		stage.show();
	}
	
	
	public static void main(String[] args) {	      
		launch(args); 		   
	}
	
	public void setSubscriber(ISubscriber subscriber) {
		this.subscriber = subscriber;
	}
	
	public Node getNode() {
		return this.node;
	}


	@Override
	public <T> void deliverMessage(T message) {
		subscriber.deliverMessage(message);	
	}
	

}

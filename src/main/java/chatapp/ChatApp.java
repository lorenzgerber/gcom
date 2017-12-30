package chatapp;

import java.io.IOException;

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

	static final String appFxml = "ChatApp.fxml";
	static final String startMenuFxml = "StartMenu.fxml";
	static final String debugFxml = "DebugApp.fxml";

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

		stage.setTitle("GCOM Demo Apps");
		stage.show();
		showStartMenu();
	}

	private void showStartMenu() {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource(ChatApp.startMenuFxml));
			StartMenuController controller = (StartMenuController) replaceSceneContent(loader);
			controller.setApp(this);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	Parent replaceSceneContent(FXMLLoader loader) throws IOException {
		Parent page = (Parent) loader.load();

		Scene scene = primaryStage.getScene();
		if (scene == null) {
			scene = new Scene(page, 700, 650);
			primaryStage.setScene(scene);
		} else {
			primaryStage.getScene().setRoot(page);
		}
		primaryStage.sizeToScene();
		return loader.getController();
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

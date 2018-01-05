package chatapp;

import java.io.IOException;

import gcom.GCom;
import gcom.ISubscriber;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ChatApp extends Application implements ISubscriber {

	Stage primaryStage;
	GCom node;
	ISubscriber subscriber;

	static final String appFxml = "ChatApp.fxml";
	static final String startMenuFxml = "StartMenu.fxml";
	static final String debugFxml = "DebugApp.fxml";

	@Override
	public void start(Stage stage) throws Exception {

		primaryStage = stage;
		primaryStage.setOnCloseRequest(e -> {
			Platform.exit();
			System.exit(0);
		});

		primaryStage.setTitle("GCOM Demo Apps");
		primaryStage.show();
		showStartMenu();
	}

	private void showStartMenu() {

		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource(ChatApp.startMenuFxml));
			Parent root = loader.load();
			StartMenuController controller = loader.getController();
			controller.setApp(this);
			replaceScene(root, 200, 250);

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void replaceScene(Parent root, int width, int height) {
		Scene scene = new Scene(root, width, height);
		primaryStage.setScene(scene);
	}

	public void setTitle(String title) {
		Platform.runLater(() -> primaryStage.setTitle(title));
	}

	public static void main(String[] args) {
		launch(args);
	}

	public void setSubscriber(ISubscriber subscriber) {
		this.subscriber = subscriber;
	}

	public GCom getNode() {
		return this.node;
	}

	@Override
	public <T> void deliverMessage(T message) {
		subscriber.deliverMessage(message);
	}

}

package chatapp;

import java.io.IOException;

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

		stage.setTitle("GCOM Demo Apps");
		stage.show();
		showStartMenu();
	}

	public static void main(String[] args) {
		launch(args);
	}

	private void showStartMenu() {
		try {
			StartMenuController controller = (StartMenuController) replaceSceneContent("StartMenu.fxml");
			controller.setApp(this);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	Parent replaceSceneContent(String fxml) throws IOException {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource(fxml));
		Parent page = (Parent) loader.load();

		Scene scene = primaryStage.getScene();
		if (scene == null) {
			scene = new Scene(page, 700, 450);
			primaryStage.setScene(scene);
		} else {
			primaryStage.getScene().setRoot(page);
		}
		primaryStage.sizeToScene();
		return loader.getController();
	}

}

package chatapp;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.stream.Collectors;

import gcom.GComBuilder;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import order.Orderers;
import javafx.scene.Parent;

public class StartMenuController extends Parent {

	ChatApp parent;

	@FXML
	private TextField nameServer;

	@FXML
	private TextField nickName;

	@FXML
	private ChoiceBox<String> ordererChoice;

	@FXML
	private CheckBox debug;

	public void setApp(ChatApp parent) {
		this.parent = parent;
	}

	@FXML
	private void chatAppButtonPressed(ActionEvent event) {
		startChatApp();
	}

	private void setNode() {
		try {
			GComBuilder builder = new GComBuilder();
			parent.node = builder.withNameServer(nameServer.getText())
					.withOrderer(Orderers.valueOf(ordererChoice.getValue())).debug(debug.isSelected()).build();

			parent.node.subscribe(parent);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void startChatApp() {

		setNode();
		VBox root = new VBox();

		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource(ChatApp.appFxml));

		try {
			root.getChildren().add(loader.load());
		} catch (IOException e) {
			e.printStackTrace();
		}
		ChatAppController controller = loader.getController();
		controller.setNickName(nickName.getText());
		controller.setChatApp(parent);
		controller.setNode(parent.node);
		controller.setSubscriber();

		if (debug.isSelected()) {
			FXMLLoader loaderDebug = new FXMLLoader();
			loaderDebug.setLocation(getClass().getResource(ChatApp.debugFxml));

			try {
				root.getChildren().add(loaderDebug.load());
			} catch (IOException e) {
				e.printStackTrace();
			}

			DebugAppController controllerDebug = loaderDebug.getController();
			controller.setDebugSubscriber(controllerDebug);

			controllerDebug.setNode(parent.node);

			parent.replaceScene(root, 680, 700);
		} else {
			parent.replaceScene(root, 700, 500);
		}

	}

	@FXML
	public void initialize() {
		ObservableList<String> list = FXCollections.observableArrayList(
				Arrays.stream(Orderers.values()).map(o -> o.toString()).collect(Collectors.toList()));
		ordererChoice.setItems(list);
		ordererChoice.setValue(Orderers.Unordered.toString());

	}

}

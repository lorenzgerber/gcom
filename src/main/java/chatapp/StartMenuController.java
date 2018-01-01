package chatapp;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.UUID;

import communication.UnreliableMulticaster;
import gcom.Node;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import order.CausalOrderer;
import order.DebugOrderer;
import order.IOrderer;
import order.UnorderedOrderer;
import javafx.scene.Parent;

public class StartMenuController extends Parent {

	ChatApp parent;

	@FXML
	private TextField nickName;
	
	@FXML
	private ChoiceBox<String> orderer;

	public void setApp(ChatApp parent) {
		this.parent = parent;
	}

	@FXML
	private void chatAppButtonPressed(ActionEvent event) {
		startChatApp();
	}

	@FXML
	private void debugAppButtonPressed(ActionEvent event) {
		startDebugApp();
	}
	
	private void setNode() {
		
		if(orderer.getValue().equals("unordered")){
			
		} else {
			
		}
		
		try {
			if(orderer.getValue().equals("unordered")){
				parent.node = new Node("localhost", new UnorderedOrderer(new UnreliableMulticaster()));
			} else {
				CausalOrderer causalOrderer = new CausalOrderer(UUID.randomUUID(),new UnreliableMulticaster());
				parent.node = new Node("localhost", causalOrderer );
				causalOrderer.setId(parent.node.getId());
			}
			
			parent.node.subscribe(parent);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public void startChatApp() {
		
		setNode();
		
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource(ChatApp.appFxml));
		Parent root = null;
		try {
			root = loader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
		ChatAppController controller = loader.getController();
		controller.setNickName(nickName.getText());
		controller.setChatApp(parent);
		controller.setNode(parent.node);
		controller.setSubscriber();
		parent.replaceScene(root, 500, 300);

	}

	public void startDebugApp() {
		
		setNode();
		VBox root = new VBox();

		// TODO: Should allow configuring orderer!
		// Setup debugger
		IOrderer orderer = new UnorderedOrderer(new UnreliableMulticaster());
		DebugOrderer debugger = new DebugOrderer(orderer);
		parent.node.setOrderer(debugger);
		/*
		 * TODO: This is quite ugly, can we automatically make the node update
		 * subscribers, or should we just remove the setSubscriber method and use a
		 * constructor instead?
		 */
		parent.node.subscribe(parent);

		FXMLLoader loaderChat = new FXMLLoader();
		loaderChat.setLocation(getClass().getResource(ChatApp.appFxml));

		try {
			root.getChildren().add(loaderChat.load());
		} catch (IOException e) {
			e.printStackTrace();
		}
		ChatAppController controllerChat = loaderChat.getController();
		controllerChat.setNickName(nickName.getText());
		controllerChat.setChatApp(parent);
		controllerChat.setNode(parent.node);
		controllerChat.setSubscriber();

		FXMLLoader loaderDebug = new FXMLLoader();
		loaderDebug.setLocation(getClass().getResource(ChatApp.debugFxml));

		try {
			root.getChildren().add(loaderDebug.load());
		} catch (IOException e) {
			e.printStackTrace();
		}

		DebugAppController controllerDebug = loaderDebug.getController();

		controllerDebug.setNode(parent.node);
		parent.replaceScene(root, 680, 700);

	}
	
	@FXML
	public void initialize() {
		ArrayList<String> ordererChoice = new ArrayList<>();
		ordererChoice.add("unordered");
		ordererChoice.add("causal");
		ObservableList<String> list = FXCollections.observableArrayList(ordererChoice);
		orderer.setItems(list);
		orderer.setValue("unordered");


	}

}

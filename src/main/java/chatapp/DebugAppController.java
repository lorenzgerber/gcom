package chatapp;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class DebugAppController {
	
	@FXML 
	private TextField inputArea; 

	@FXML
	private TextArea messageArea; 
	
	@FXML
	private void sendButtonPressed(ActionEvent event) {
		// do some stuff on button press
		System.out.println("send the text to everybody");
	}
	
	public void intitialize() {
		
	}

}

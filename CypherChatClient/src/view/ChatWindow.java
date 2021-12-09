package view;

import control.ChatController;
import control.ConnectionController;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ChatWindow extends Stage{
	
	private Scene scene;
	private TextField tfMessage;
	private Button btnSend;
	private TextArea taMessages;
	private Label clientListLabel;
	private ChatController controller;
	
	public ChatWindow() {
		
		tfMessage = new TextField("");
		btnSend = new Button("Send");
		taMessages = new TextArea();
		clientListLabel = new Label("Client List: ");
		
		
		VBox vBox = new VBox();
		vBox.getChildren().add(taMessages);
		vBox.getChildren().add(tfMessage);
		vBox.getChildren().add(btnSend);
		vBox.getChildren().add(clientListLabel);
		vBox.getChildren().add(new Label("Note: To send PMs use the format 'PM:destiny:message' \n also, type 'CL' to update the client list"));
		
		
		scene = new Scene(vBox, 400, 400);
		this.setScene(scene);
		
		controller = new ChatController(this);
	}

	public TextField getTfMessage() {
		return tfMessage;
	}

	public Button getBtnSend() {
		return btnSend;
	}

	public TextArea getTaMessages() {
		return taMessages;
	}

	public Label getClientListLabel(){return clientListLabel;}
	
	

}

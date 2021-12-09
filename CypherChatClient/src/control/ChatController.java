package control;

import com.google.gson.Gson;
import com.google.gson.InstanceCreator;
import commucation.OnMessageListener;
import commucation.PKCOMM;
import commucation.TCPConnection;
import javafx.application.Platform;
import view.ChatWindow;

import java.lang.reflect.Type;
import java.security.PublicKey;

public class ChatController implements OnMessageListener{
	
	private ChatWindow view;
	private TCPConnection connection;
	
	public ChatController(ChatWindow view) {
		this.view = view;
		connection = TCPConnection.getInstance();
		connection.initReceiver();
		connection.initSender();
		connection.setReceiverListener(this);
		connection.getReceiver().receiveMessage();
		
		initView();
	}

	private void initView() {
		view.getBtnSend().setOnAction(
				(event)->{
					String line = view.getTfMessage().getText();
					if(line.substring(0,2).equals("DI") || line.equals("CL") || line.substring(0,1).equals("DH")){
						connection.getEmitter().sendMessage(line);
					}else if(line.substring(0,1).equals("PM")){
						String subMsg = connection.encrypt(line.substring(2, line.length()));
						if (subMsg != null) {
							String encryptedMsg = line.substring(0, 1) + subMsg;
							connection.getEmitter().sendMessage(encryptedMsg);
						} else {
							closeConnection();
							connection.close();
						}
					}else{
						String encryptedMsg = connection.encrypt(line);
						if (encryptedMsg != null) {
							connection.getEmitter().sendMessage(connection.encrypt(line));
						}else{
							closeConnection();
							connection.close();
						}
					}
				}
		);
		view.setOnCloseRequest(e->{
			String disMsg = "DI:"+connection.getServerIp();
			connection.getEmitter().sendMessage(disMsg);
		});
	}

	public void closeConnection(){
		String disMsg = "DI:"+connection.getServerIp();
		connection.getEmitter().sendMessage(disMsg);
	}

	@Override
	public void onMessageReceived(String msg) {
		String decryptedMsg = connection.decrypt(msg);
		Platform.runLater(
				()->{
					view.getTaMessages().appendText(decryptedMsg+"\n");
					System.out.println("ChatController>"+decryptedMsg);
				}
		);
	}

	@Override
	public void onClientListUpdate(String list) {
		Platform.runLater(()->{
			view.getClientListLabel().setText("Client List: "+list);
		});
	}

	@Override
	public void onDH(String key) {
		System.out.println(key.substring(2, key.length()));
		String keyJ = key.substring(2, key.length());
		Gson gson = new Gson();
		PKCOMM serverPK = gson.fromJson(keyJ, PKCOMM.class);
		connection.performDHExchange(serverPK);
	}
}

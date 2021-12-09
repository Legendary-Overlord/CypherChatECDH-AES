package server.communication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

public class Receiver {
	
	private Connection connection;
	private Socket socket;
	private BufferedReader reader;

	public Receiver(Connection connection) {
		this.connection = connection;
		this.socket = connection.getSocket();
		init();
	}
	
	private void init() {
		try {
			InputStream is = this.socket.getInputStream();
			reader = new BufferedReader(new InputStreamReader(is));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void receiveMessage() {
		new Thread(
				()->{
					while(true) {
						try {
							String line = reader.readLine();
							if(line.substring(0,2).equals("DI")){
								String[] arr = line.split(":");
								observer.onDisconnection(arr[1]);
							}else if(line.substring(0,2).equals("PM")){
								String decryptedMsg = connection.decrypt(line.substring(2, line.length()));
								String[] arr = decryptedMsg.split(":");
								observer.onPM(arr[1],arr[2]);
							}else if(line.equals("CL")){
								observer.onCLRequest();
							}else if(line.substring(0,2).equals("DH")){
								System.out.println("Performing ECDH");
								observer.onDHExchange(line.substring(2,line.length()));
							}else{
								observer.onMessageReceived(connection.getId(), connection.decrypt(line));
							}
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
		).start();
	}
	
	//Patr�n Observer
	private OnMessageListener observer;
	
	public void setObserver(OnMessageListener observer) {
		this.observer = observer;
	}
	
}

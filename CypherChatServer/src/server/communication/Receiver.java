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
								String fromPM = "PM:" + connection.getSocket().getInetAddress().getHostAddress() + ":" + arr[2];
								observer.onPM(arr[1],fromPM);
							}else if(line.equals("CL")){
								observer.onCLRequest();
							}else if(line.substring(0,2).equals("DH")){
								System.out.println("Performing ECDH");
								observer.onDHExchange(line.substring(2,line.length()));
							}else{
								System.out.println("ELSE ON RECEIVER");
								System.out.println("Line: "+line);
								String decrypted = connection.getSocket().getInetAddress().getHostAddress()+":"+connection.decrypt(line);
								System.out.println(decrypted);
								observer.onMessageReceived(connection.getId(), decrypted);
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

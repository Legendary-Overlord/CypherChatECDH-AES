package commucation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;


public class Receiver {
	private Socket socket;
	private BufferedReader reader;

	public Receiver(Socket socket) {
		this.socket = socket;
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
							System.out.println(line.substring(0,2));
							if(line.substring(0,2).equals("CL")){
								observer.onClientListUpdate(line);
							}else if(line.substring(0,2).equals("DH")){
								System.out.println("Receive Server PK");
								observer.onDH(line);
							}else{
								observer.onMessageReceived(line);
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

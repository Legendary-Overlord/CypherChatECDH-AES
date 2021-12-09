package server.communication;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.UUID;

import com.google.gson.Gson;
import server.communication.Emitter;
import server.communication.Receiver;
import server.main.Server;

public class TCPConnection {
	
	private static TCPConnection instance;
	
	private ServerSocket socketDispatcher;
	private int puerto;
	private Server server;
	private ArrayList<Connection> connections;
	
	public static synchronized TCPConnection getInstance() {
		if(instance == null) {
			instance = new TCPConnection();
		}
		return instance;
	}
	
	private TCPConnection() {
		connections = new ArrayList<>();
	}
	
	public void setPuerto(int puerto) {
		this.puerto = puerto;
	}
	
	public void waitForConnection() {
		new Thread(
				()->{
					try {
						socketDispatcher = new ServerSocket(this.puerto);
						
						while(true) {
							System.out.println("Ready...");
							Socket socket = socketDispatcher.accept();
							System.out.println("Connection Established.");
							Connection connection = new Connection(socket);
							connection.setId(UUID.randomUUID().toString());
							connections.add(connection);
							connection.setListener(server);
							connection.initSender();
							connection.initReceiver();
							//Diffie-Hellman Key Ex
							System.out.println("Performing ECDH");
							Gson gson = new Gson();
							PKCOMM pkcomm = new PKCOMM(connection.getId(), connection.getPublickey().getEncoded());
							String keyJson = gson.toJson(pkcomm);
							connection.getEmitter().sendMessage("DH" +keyJson);
							System.out.println(connection.getPublickey().getAlgorithm());
							System.out.println(connection.getPublickey().getFormat());
							System.out.println("Sent Server Key");
//							sendBroadcast("Client Num: "+connections.size());
						}
						
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
		).start();
	}
	
	public void setObserver(Server server) {
		this.server = server;
	}

	public void sendBroadcast(String msg) {
		for(int i=0 ; i<connections.size() ; i++) {
			Connection connection = connections.get(i);
			msg = connection.encrypt(msg);
			connection.getEmitter().sendMessage(msg);
		}
	}
	public void sendDisconnect(String ip){
		connections.removeIf(e->{
			return e.getSocket().getInetAddress().getHostAddress().equals(ip);
		});
		sendBroadcast(ip+" disconnected.");
		sendBroadcast("Client Num: "+connections.size());
	}
	public void sendPM(String id, String msg){
		for (Connection connection : connections) {
			if(connection.getId().equals(id)){
				connection.getEmitter().sendMessage("PM: "+connection.encrypt(msg));
				break;
			}
		}
	}
	public void sendClientList(){
		String msg = "CL:";
		for (Connection connection : connections) {
			msg+=connection.getSocket().getInetAddress().getHostAddress()+",";
		}
		sendBroadcast(msg);
	}

	public void setDH(String key) {
		Gson gson = new Gson();
		PKCOMM clientPK = gson.fromJson(key, PKCOMM.class);
		for (Connection connection : connections) {
			if(connection.getId().equals(clientPK.getUuid())){
				try {
					//regenerating publicKey from JSON
					KeyFactory keyFactory = KeyFactory.getInstance("EC");
					X509EncodedKeySpec x509 = new X509EncodedKeySpec(clientPK.getPublicKey());
					PublicKey publicKey = keyFactory.generatePublic(x509);
					//Setting PublicKey for connection
					connection.setReceiverPublicKey(publicKey);
					System.out.println("Set connection PK");
				} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
					e.printStackTrace();
				}
				break;
			}
		}
	}
}

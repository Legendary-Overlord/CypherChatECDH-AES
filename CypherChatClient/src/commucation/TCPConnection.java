package commucation;

import java.io.IOException;
import java.net.Socket;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

import com.google.gson.Gson;

public class TCPConnection extends AESDiffieHellmanKeyExchange{
	
	private OnConnectionListener listener;
	
	private static TCPConnection instance;
	
	private int puerto;
	private String serverIp;
	private Socket socket;
	
	
	private Receiver receiver;
	private Emitter emitter;
	
	public static synchronized TCPConnection getInstance() {
		if(instance == null) {
			instance = new TCPConnection();
		}
		return instance;
	}
	
	public void setListener(OnConnectionListener listener) {
		this.listener = listener;
	}
	
	private TCPConnection() {}
	
	public void setPuerto(int puerto) {
		this.puerto = puerto;
	}
	
	public void setServerIp(String serverIp) {
		this.serverIp = serverIp;
	}
	
	public void requestConnection() {
		new Thread(
				()->{
					try {
						this.socket = new Socket(serverIp, puerto);
						System.out.println("Connection established.");
						if(listener != null) listener.onConnection("OK");
					} catch (IOException e) {
						System.out.println(e.getMessage());
						if(listener != null) listener.onConnection("ERROR");
					}
				}
		).start();
	}

	public void initReceiver() {
		receiver = new Receiver(socket);
	}
	
	public void initSender() {
		emitter = new Emitter(socket);
	}

	public void close() {
		try {
			this.socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Receiver getReceiver() {
		return receiver;
	}

	public Emitter getEmitter() {
		return emitter;
	}

	public void setReceiverListener(OnMessageListener listener) {
		receiver.setObserver(listener);
	}
	public String getServerIp(){return serverIp;}

	/**
	 * Deserializes, Assigns Public Key, and sends its own to the server
	 * @param key
	 */
	public void performDHExchange(PKCOMM key) {
		System.out.println("Perform ECDH");
		try {
			//Creating KeyFactory Instance for Elliptic Curve
			KeyFactory keyFactory = KeyFactory.getInstance("EC");
			//Gets and sets the Public key to the connection
			this.setReceiverPublicKey(keyFactory.generatePublic(new X509EncodedKeySpec(key.getPublicKey())));
			//send own key
			Gson gson = new Gson();
			//Serializing PKCOMM Objecto to send as JSON
			PKCOMM pkcommClient = new PKCOMM(key.getUuid(), getPublicKey().getEncoded());
			String clientPK = gson.toJson(pkcommClient);
			//sending PublicKey
			getEmitter().sendMessage("DH"+clientPK);
			System.out.println("Sent client PK");
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			e.printStackTrace();
		}
	}
}

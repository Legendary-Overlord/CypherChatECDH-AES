package server.main;

import java.util.Calendar;
import java.util.Scanner;

import com.google.gson.Gson;

import server.communication.GenericModel;
import server.communication.MessageModel;
import server.communication.OnMessageListener;
import server.communication.TCPConnection;
import server.communication.UserModel;

public class Server implements OnMessageListener{

	public static void main(String[] args) {
		Server server = new Server();
	}

	TCPConnection conexion;
	
	public Server() {
		conexion = TCPConnection.getInstance();
		conexion.setPuerto(5000);
		conexion.setObserver(this);
		conexion.waitForConnection();
	}

	@Override
	public void onMessageReceived(String id, String msg) {
		System.out.println(id+":"+msg);
		conexion.sendBroadcast(msg);
	}

	@Override
	public void onDisconnection(String ip) {
		conexion.sendDisconnect(ip);
	}

	@Override
	public void onPM(String ip, String msg) {
		conexion.sendPM(ip,msg);
	}

	@Override
	public void onCLRequest() {
		conexion.sendClientList();
	}

	@Override
	public void onDHExchange(String key) {
		conexion.setDH(key);
	}
}

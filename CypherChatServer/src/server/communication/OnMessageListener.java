package server.communication;

public interface OnMessageListener{
	void onMessageReceived(String id, String msg);
	void onDisconnection(String id);
	void onPM(String id, String msg);
	void onCLRequest();
	void onDHExchange(String key);
}

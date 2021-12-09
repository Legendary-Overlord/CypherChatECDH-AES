package commucation;

public interface OnMessageListener{
	void onMessageReceived(String msg);
	void onClientListUpdate(String list);
	void onDH(String key);
}

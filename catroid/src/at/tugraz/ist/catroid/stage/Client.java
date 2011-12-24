package at.tugraz.ist.catroid.stage;
import java.io.ObjectOutputStream;
import java.net.Socket;


public class Client {
	Socket requestSocket;
	ObjectOutputStream out;
 	String message;
	
 	public void connect()
 	{
		try{
			requestSocket = new Socket("10.0.2.2", 22000);
			System.out.println("Connected to localhost in port 2005");
			out = new ObjectOutputStream(requestSocket.getOutputStream());
			out.flush();
		}
		catch(Exception e){
			System.out.println("Exception while trying to connect");
		}
 	}

	void sendMessage(String msg)
	{
		try{
			out.writeObject(msg);
			out.flush();
			System.out.println("client>" + msg);
		}
		catch(Exception e){
			System.out.println("Exception while sending Message");
		}
	}

}

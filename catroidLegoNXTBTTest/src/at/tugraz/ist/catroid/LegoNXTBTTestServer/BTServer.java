package at.tugraz.ist.catroid.LegoNXTBTTestServer;

import java.io.IOException;
 
import javax.bluetooth.*;
import javax.microedition.io.*;
 
/**
 * Class that implements an SPP Server which accepts single line of
 * message from an SPP client and sends a single line of response to the client.
 */
public class BTServer {
    

	private boolean run = true;
	static StreamConnection connection = null;
	static BTServer btServer;
	
    private void startServer() throws IOException{
 
        //Create a UUID for SPP
        UUID uuid = new UUID("1101", true);
        //Create the servicve url
        String connectionString = "btspp://localhost:" + uuid +";name=BT Test Server";
        
        //open server url
        StreamConnectionNotifier streamConnNotifier = (StreamConnectionNotifier)Connector.open( connectionString );
        
        //Wait for client connection
        GUI.writeMessage("Bluetooth Server started. Waiting for Bluetooth test clients... \n");
        
        while(run){
        	connection=streamConnNotifier.acceptAndOpen();
        	BTClientHandler btc = new BTClientHandler(connection);
        	btc.start();
        }
        
        streamConnNotifier.close();
    }

    public static void main(String[] args){
        
    	try{
    		GUI.startGUI();
	        //display local device address and name
	        LocalDevice localDevice = LocalDevice.getLocalDevice();
	        GUI.writeMessage("Local System:\n");
	        GUI.writeMessage("Address: "+localDevice.getBluetoothAddress() + "\n");
	        GUI.writeMessage("Name: "+localDevice.getFriendlyName() + "\n");
	        
	        
	        btServer = new BTServer();
	        btServer.startServer();
	        
    	}
    	catch(IOException e){
    		e.printStackTrace();
    	}
        
    }
}
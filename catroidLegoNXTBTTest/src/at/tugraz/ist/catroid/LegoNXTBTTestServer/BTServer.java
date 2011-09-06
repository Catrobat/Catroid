package at.tugraz.ist.catroid.LegoNXTBTTestServer;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
 
import javax.bluetooth.*;
import javax.microedition.io.*;
 
/**
 * Class that implements an SPP Server which accepts single line of
 * message from an SPP client and sends a single line of response to the client.
 */
public class BTServer {
    
	
	public static byte DIRECT_COMMAND_REPLY = 0x00;
	public static byte SYSTEM_COMMAND_REPLY = 0x01;
	public static byte REPLY_COMMAND = 0x02;
	public static byte DIRECT_COMMAND_NOREPLY = (byte) 0x80; // Avoids ~100ms latency
	public static byte SYSTEM_COMMAND_NOREPLY = (byte) 0x81; // Avoids ~100ms latency
	public static final byte SET_OUTPUT_STATE = 0x04;
	public static final byte SET_INPUT_MODE = 0x05;
	public static final byte GET_OUTPUT_STATE = 0x06;
	public static final byte GET_INPUT_VALUES = 0x07;
    //start server
    private void startServer() throws IOException{
 
        //Create a UUID for SPP
        UUID uuid = new UUID("1101", true);
        //Create the servicve url
        String connectionString = "btspp://localhost:" + uuid +";name=Sample SPP Server";
        
        //open server url
        StreamConnectionNotifier streamConnNotifier = (StreamConnectionNotifier)Connector.open( connectionString );
        
        //Wait for client connection
        System.out.println("\nServer Started. Waiting for clients to connect...");
        StreamConnection connection=streamConnNotifier.acceptAndOpen();
 
        RemoteDevice dev = RemoteDevice.getRemoteDevice(connection);
        System.out.println("Remote device address: "+dev.getBluetoothAddress());
        System.out.println("Remote device name: "+dev.getFriendlyName(true));
        
        //read string from spp client
        InputStream inStream=connection.openInputStream();
        BufferedReader bReader=new BufferedReader(new InputStreamReader(inStream));
        
        OutputStream outStream=connection.openOutputStream();
        PrintWriter pWriter=new PrintWriter(new OutputStreamWriter(outStream));
        
        char[] lastMessage = null;
        int messageLength = 0;
        int firstLenByte = 0;
        int secondLenByte = 0;
        while((firstLenByte = bReader.read()) != -1){
        	secondLenByte = (bReader.read() << 8);
        	messageLength = firstLenByte + secondLenByte;
        	System.out.println("next message len: " + messageLength);
        	char[] buf = new char[messageLength];
        	char[] reply = null;
        	bReader.read(buf);
        	
        	//reply required?
        	if((messageLength == 3) && (buf[0] == DIRECT_COMMAND_REPLY) && (buf[1] == GET_OUTPUT_STATE)){
        		reply = new char[32];
        		reply[0] = (char)REPLY_COMMAND;
        		reply[1] = (char)GET_OUTPUT_STATE;
        		reply[3] = lastMessage[2]; //used motor
        		reply[2] = 0; //status 0 = no error
        		reply[4] = lastMessage[3]; //speed
        		reply[5] = lastMessage[4];
        		reply[6] = lastMessage[5];
        		reply[7] = lastMessage[6];
        		reply[8] = lastMessage[7];
        		reply[9] = lastMessage[8];
        		reply[10] = lastMessage[9];
        		reply[11] = lastMessage[10];
        	}

        	
        	System.out.println((int)buf[0] + " " + (int)buf[1] + " " + (int)buf[2]);
        	//System.out.println(buf.toString());
            
        	if(buf[0] == DIRECT_COMMAND_REPLY){
        		System.out.println("sending reply");
	        	pWriter.write(reply.length);
	            pWriter.write(0);
	            pWriter.write(reply);
	            pWriter.flush();
        	}
            lastMessage = buf;
        }
        
        System.out.println("fini");
 
        pWriter.close();
        streamConnNotifier.close();
 
    }
 
 
    public static void main(String[] args){
        
    	try{
	        //display local device address and name
	        LocalDevice localDevice = LocalDevice.getLocalDevice();
	        System.out.println("Address: "+localDevice.getBluetoothAddress());
	        System.out.println("Name: "+localDevice.getFriendlyName());
	        
	        BTServer sampleSPPServer=new BTServer();
	        sampleSPPServer.startServer();
    	}
    	catch(IOException e){
    		e.printStackTrace();
    	}
        
    }
}
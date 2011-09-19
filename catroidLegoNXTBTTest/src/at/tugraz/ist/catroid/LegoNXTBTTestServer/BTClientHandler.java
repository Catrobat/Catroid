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
public class BTClientHandler extends Thread{
    
	public static byte DIRECT_COMMAND_REPLY = 0x00;
	public static byte SYSTEM_COMMAND_REPLY = 0x01;
	public static byte REPLY_COMMAND = 0x02;
	public static byte DIRECT_COMMAND_NOREPLY = (byte) 0x80; // Avoids ~100ms latency
	public static byte SYSTEM_COMMAND_NOREPLY = (byte) 0x81; // Avoids ~100ms latency
	public static final byte SET_OUTPUT_STATE = 0x04;
	public static final byte SET_INPUT_MODE = 0x05;
	public static final byte GET_OUTPUT_STATE = 0x06;
	public static final byte GET_INPUT_VALUES = 0x07;
	
	private StreamConnection connection;
	
	public BTClientHandler(StreamConnection connection) {
		this.connection = connection;
	}
    //start server
    public void run(){
    	String client = "null";
	    try{
	    	RemoteDevice dev = RemoteDevice.getRemoteDevice(connection);
	    	client = dev.getFriendlyName(true);
	    	GUI.writeMessage("Remote device address: " + dev.getBluetoothAddress() + "\n");
	    	GUI.writeMessage("Remote device name: " + client + "\n");
	        
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
	        	GUI.writeMessage("Received message, length (byte): " + messageLength + "\n");
	        	char[] buf = new char[messageLength];
	        	byte[] reply = null;
	        	bReader.read(buf);
	        	
	        	//reply required?
	        	if((messageLength == 3) && (buf[0] == DIRECT_COMMAND_REPLY) && (buf[1] == GET_OUTPUT_STATE)){
	        		reply = getLegoNXTReplyMessage(lastMessage);
	        	}
	        	
	        	if(buf[0] == DIRECT_COMMAND_REPLY){
		        	GUI.writeMessage("Reply message:\n");
		        	for(int i = 0; i < reply.length; i++){
		        		GUI.writeMessage("Byte" + i + ": " + (int)reply[i] + " ");
		        	}
	        		GUI.writeMessage("\nSending reply message \n");
	        		outStream.write(reply.length);
	        		outStream.write(0);
	        		outStream.write(reply);
	        		outStream.flush();
	        	}
	            lastMessage = buf;
	        }

	        pWriter.close();
	        bReader.close();
	        connection.close();
		 }
		 catch(IOException e){
			 e.printStackTrace();
		 }
	    GUI.writeMessage("Client " + client + " disconnected!\n");
    
    }
 
    public byte[] getLegoNXTReplyMessage(char[] lastMessage){
    	
		byte[] reply = new byte[32];
		reply[0] = REPLY_COMMAND;
		reply[1] = GET_OUTPUT_STATE;
		reply[3] = (byte)lastMessage[2]; //used motor
		reply[2] = 0; //status 0 = no error
		reply[4] = (byte)lastMessage[3]; //speed
		reply[5] = (byte)lastMessage[4];
		reply[6] = (byte)lastMessage[5];
		reply[7] = (byte)lastMessage[6];
		reply[9] = (byte)lastMessage[8];
		reply[10] = (byte)lastMessage[9];
		reply[11] = (byte)lastMessage[10];
		reply[12] = (byte)lastMessage[11];
    	return reply;
    }
 

}
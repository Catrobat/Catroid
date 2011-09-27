package at.tugraz.ist.catroid.LegoNXTBTTestServer;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;

public class BTServer
{
  private boolean run = true;
  private static boolean gui = false;
  static StreamConnection connection = null;
  static BTServer btServer;
  private static Writer out = null;

  private void startServer() throws IOException
  {
    UUID uuid = new UUID("1101", true);

    String connectionString = "btspp://localhost:" + uuid + ";name=BT Test Server";

    StreamConnectionNotifier streamConnNotifier = (StreamConnectionNotifier)Connector.open(connectionString);

    writeMessage("Bluetooth Server started. Waiting for Bluetooth test clients... \n");

    while (this.run) {
      connection = streamConnNotifier.acceptAndOpen();
      BTClientHandler btc = new BTClientHandler(connection);
      btc.start();
    }

    streamConnNotifier.close();
  }

  public static void writeMessage(String arg)
  {
	  if(gui == false){
	    try {
	      out.write(arg);
	      out.flush();
	    }
	    catch (Exception localException)
	    {
	    }
	  }
	  else{
		  GUI.writeMessage(arg);
	  }
  }

  public static void main(String[] args)
  {
	  if(args.length == 0){
		  gui = true;
		  GUI.startGUI();
	      btServer = new BTServer();
	      try {
			btServer.startServer();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	  }
	  else{
	    try {
	      out = new OutputStreamWriter(new FileOutputStream(args[0]));
	
	      LocalDevice localDevice = LocalDevice.getLocalDevice();
	      writeMessage("Local System:\n");
	      writeMessage("Address: " + localDevice.getBluetoothAddress() + "\n");
	      writeMessage("Name: " + localDevice.getFriendlyName() + "\n");
	
	      btServer = new BTServer();
	      btServer.startServer();
    } 
    catch (IOException e)
    {
      e.printStackTrace();
    }
	  }
  }
}
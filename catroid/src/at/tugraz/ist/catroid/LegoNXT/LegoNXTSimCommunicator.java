/*    Catroid: An on-device graphical programming language for Android devices
 *    Copyright (C) 2010  Catroid development team
 *    (<http://code.google.com/p/catroid/wiki/Credits>)
 *
 *    This program is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *    
 *    This file incorporates work covered by the following copyright and  
 *    permission notice: 
 *    
 *		   	Copyright 2010 Guenther Hoelzl, Shawn Brown
 *
 *		   	This file is part of MINDdroid.
 *
 * 		  	MINDdroid is free software: you can redistribute it and/or modify
 * 		  	it under the terms of the GNU General Public License as published by
 * 		  	the Free Software Foundation, either version 3 of the License, or
 *   		(at your option) any later version.
 *
 *   		MINDdroid is distributed in the hope that it will be useful,
 *   		but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   		MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   		GNU General Public License for more details.
 *
 *   		You should have received a copy of the GNU General Public License
 *   		along with MINDdroid.  If not, see <http://www.gnu.org/licenses/>.
 */

package at.tugraz.ist.catroid.LegoNXT;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import android.content.res.Resources;
import android.os.Handler;
import android.util.Log;
import at.tugraz.ist.catroid.R;

/**
 * This class is for talking to the LEGO NXT robot simulator
 */
public class LegoNXTSimCommunicator extends LegoNXTCommunicator {

	//Be aware of addresses!
	//localhost will not work within an AVD simulator!! use address 10.0.2.2 in there or see documentation!
	//http://developer.android.com/guide/developing/devices/emulator.html#emulatornetworking
	//avdLocalAddress = InetAddress.getByAddress("10.0.2.2".getBytes());

	private static final String localhost = "localhost";
	private static final String avdLocalAddress = "10.0.2.2";
	private int port = 6787;
	private Socket socket = null;

	PrintWriter output = null;
	BufferedReader input = null;

	public LegoNXTSimCommunicator(Handler uiHandler, Resources resources) {
		super(uiHandler, resources);
	}

	/**
	 * Creates the connection, waits for incoming messages and dispatches them. The thread will be terminated
	 * on closing of the connection.
	 */
	@Override
	public void run() {

		try {
			createNXTconnection();
		} catch (IOException e) {
		}
		String buffer;

		try {
			while ((buffer = input.readLine()) != null) {
				//blabla
				Log.i("bt", buffer);
			}

		} catch (Exception e) {
		}

	}

	/**
	 * Create a bluetooth connection with SerialPortServiceClass_UUID
	 * 
	 * @see <a href=
	 *      "http://lejos.sourceforge.net/forum/viewtopic.php?t=1991&highlight=android"
	 *      />
	 *      On error the method either sends a message to it's owner or creates an exception in the
	 *      case of no message handler.
	 */
	@Override
	public void createNXTconnection() throws IOException {
		try {

			socket = new Socket(avdLocalAddress, port);
			output = new PrintWriter(new DataOutputStream(socket.getOutputStream()));
			input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			connected = true;
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Closes the bluetooth connection. On error the method either sends a message
	 * to it's owner or creates an exception in the case of no message handler.
	 */
	@Override
	public void destroyNXTconnection() throws IOException {
		try {
			if (socket != null) {
				connected = false;
				output.close();
				input.close();
				socket.close();
				socket = null;
			}

		} catch (IOException e) {
			if (uiHandler == null) {
				throw e;
			} else {
				sendToast(mResources.getString(R.string.problem_at_closing));
			}
		}
	}

	/**
	 * Sends a message on the opened OutputStream
	 * 
	 * @param message
	 *            , the message as a byte array
	 */
	@Override
	public void sendMessage(byte[] message) throws IOException {

		if (output == null) {
			throw new IOException();
		}

		// send message length
		//		int messageLength = message.length;
		//		char[] mess = new char[messageLength];
		//		for (int i = 0; i < messageLength; i++) {
		//			mess[i] = (char) message[i];
		//		}

		String mess = message.toString();
		mess += "\n";

		//output.write(messageLength);
		output.write(mess);
		output.flush();
	}

	/**
	 * Receives a message on the opened InputStream
	 * 
	 * @return the message
	 */
	@Override
	public byte[] receiveMessage() throws IOException {
		if (input == null) {
			throw new IOException();
		}

		char[] buf = new char[5];
		int length = input.read(buf, 0, 4);
		Log.i("bt", buf.toString());
		return returnMessage;
	}

}
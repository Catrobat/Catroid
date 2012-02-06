/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010-2011 The Catroid Team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://www.catroid.org/catroid_license_additional_term
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *    
 *    This file incorporates work covered by the following copyright and  
 *    permission notice: 
 *    
 *		   	Copyright 2010 Guenther Hoelzl, Shawn Brown
 *
 *		   	This file is part of MINDdroid.
 *
 * 		  	MINDdroid is free software: you can redistribute it and/or modify
 * 		  	it under the terms of the GNU Affero General Public License as
 * 		  	published by the Free Software Foundation, either version 3 of the
 *   		License, or (at your option) any later version.
 *
 *   		MINDdroid is distributed in the hope that it will be useful,
 *   		but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   		MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   		GNU Affero General Public License for more details.
 *
 *   		You should have received a copy of the GNU Affero General Public License
 *   		along with MINDdroid.  If not, see <http://www.gnu.org/licenses/>.
 */

package at.tugraz.ist.catroid.LegoNXT;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.res.Resources;
import android.os.Handler;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.bluetooth.BTConnectable;

/**
 * This class is for talking to a LEGO NXT robot via bluetooth.
 * The communciation to the robot is done via LCP (LEGO communication protocol).
 * Objects of this class can either be run as standalone thread or controlled
 * by the owners, i.e. calling the send/recive methods by themselves.
 */
public class LegoNXTBtCommunicator extends LegoNXTCommunicator {

	private static final UUID SERIAL_PORT_SERVICE_CLASS_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	// this is the only OUI registered by LEGO, see http://standards.ieee.org/regauth/oui/index.shtml

	private BluetoothAdapter btAdapter;
	private BluetoothSocket nxtBTsocket = null;
	private OutputStream nxtOutputStream = null;
	private InputStream nxtInputStream = null;

	private String mMACaddress;
	private BTConnectable myOwner;

	public LegoNXTBtCommunicator(BTConnectable myOwner, Handler uiHandler, BluetoothAdapter btAdapter,
			Resources resources) {
		super(uiHandler, resources);

		this.myOwner = myOwner;
		this.btAdapter = btAdapter;
	}

	public void setMACAddress(String mMACaddress) {
		this.mMACaddress = mMACaddress;
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

		while (connected) {
			try {
				returnMessage = receiveMessage();
				if ((returnMessage.length >= 2)
						&& ((returnMessage[0] == LCPMessage.REPLY_COMMAND) || (returnMessage[0] == LCPMessage.DIRECT_COMMAND_NOREPLY))) {
					dispatchMessage(returnMessage);
				}

			} catch (IOException e) {
				// don't inform the user when connection is already closed
				if (connected) {
					sendState(STATE_RECEIVEERROR);
				}
				return;
			}
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
			BluetoothSocket nxtBTSocketTemporary;
			BluetoothDevice nxtDevice = null;
			nxtDevice = btAdapter.getRemoteDevice(mMACaddress);
			if (nxtDevice == null) {
				if (uiHandler == null) {
					throw new IOException();
				} else {
					sendToast(mResources.getString(R.string.no_paired_nxt));
					sendState(STATE_CONNECTERROR);
					return;
				}
			}

			nxtBTSocketTemporary = nxtDevice.createRfcommSocketToServiceRecord(SERIAL_PORT_SERVICE_CLASS_UUID);
			try {

				nxtBTSocketTemporary.connect();

			} catch (IOException e) {
				if (myOwner.isPairing()) {
					if (uiHandler != null) {
						sendToast(mResources.getString(R.string.pairing_message));
						sendState(STATE_CONNECTERROR_PAIRING);
					} else {
						throw e;
					}
					return;
				}

				// try another method for connection, this should work on the HTC desire, credits to Michael Biermann
				try {

					Method mMethod = nxtDevice.getClass().getMethod("createRfcommSocket", new Class[] { int.class });
					nxtBTSocketTemporary = (BluetoothSocket) mMethod.invoke(nxtDevice, Integer.valueOf(1));
					nxtBTSocketTemporary.connect();
				} catch (Exception e1) {
					if (uiHandler == null) {
						throw new IOException();
					} else {
						sendState(STATE_CONNECTERROR);
					}
					return;
				}
			}
			nxtBTsocket = nxtBTSocketTemporary;
			nxtInputStream = nxtBTsocket.getInputStream();
			nxtOutputStream = nxtBTsocket.getOutputStream();
			connected = true;
		} catch (IOException e) {
			if (uiHandler == null) {
				throw e;
			} else {
				if (myOwner.isPairing()) {
					sendToast(mResources.getString(R.string.pairing_message));
				}
				sendState(STATE_CONNECTERROR);
				return;
			}
		}
		// everything was OK
		if (uiHandler != null) {
			sendState(STATE_CONNECTED);
		}
	}

	/**
	 * Closes the bluetooth connection. On error the method either sends a message
	 * to it's owner or creates an exception in the case of no message handler.
	 */
	@Override
	public void destroyNXTconnection() throws IOException {

		if (connected) {
			stopAllNXTMovement();
		}

		try {
			if (nxtBTsocket != null) {
				connected = false;
				nxtBTsocket.close();
				nxtBTsocket = null;
			}

			nxtInputStream = null;
			nxtOutputStream = null;

		} catch (IOException e) {
			if (uiHandler == null) {
				throw e;
			} else {
				sendToast(mResources.getString(R.string.problem_at_closing));
			}
		}
	}

	@Override
	public void stopAllNXTMovement() {
		myHandler.removeMessages(0);
		myHandler.removeMessages(1);
		myHandler.removeMessages(2);

		moveMotor(0, 0, 0);
		moveMotor(1, 0, 0);
		moveMotor(2, 0, 0);
	}

	/**
	 * Sends a message on the opened OutputStream
	 * 
	 * @param message
	 *            , the message as a byte array
	 */
	@Override
	public void sendMessage(byte[] message) throws IOException {

		if (nxtOutputStream == null) {
			throw new IOException();
		}

		// send message length
		int messageLength = message.length;
		nxtOutputStream.write(messageLength);
		nxtOutputStream.write(messageLength >> 8);
		nxtOutputStream.write(message, 0, message.length);
		nxtOutputStream.flush();
	}

	/**
	 * Receives a message on the opened InputStream
	 * 
	 * @return the message
	 */
	@Override
	public byte[] receiveMessage() throws IOException {
		if (nxtInputStream == null) {
			throw new IOException();
		}

		int length = nxtInputStream.read();
		length = (nxtInputStream.read() << 8) + length;
		byte[] returnMessage = new byte[length];
		nxtInputStream.read(returnMessage);
		//Log.i("bt", returnMessage.toString());
		return returnMessage;
	}

}
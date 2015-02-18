/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2015 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * An additional term exception under section 7 of the GNU Affero
 * General Public License, version 3, is available at
 * http://developer.catrobat.org/license_additional_term
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.legonxt;

import android.bluetooth.BluetoothSocket;
import android.content.res.Resources;
import android.os.Handler;
import android.util.Log;

import org.catrobat.catroid.R;
import org.catrobat.catroid.bluetooth.BluetoothConnection;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

/**
 * This class is for talking to a LEGO NXT robot via bluetooth.
 * The communciation to the robot is done via LCP (LEGO communication protocol).
 * Objects of this class can either be run as standalone thread or controlled
 * by the owners, i.e. calling the send/recive methods by themselves.
 */
public class LegoNXTBtCommunicator extends LegoNXTCommunicator {

	private static final String TAG = LegoNXTBtCommunicator.class.getSimpleName();
	private static final UUID SERIAL_PORT_SERVICE_CLASS_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	// this is the only OUI registered by LEGO, see http://standards.ieee.org/regauth/oui/index.shtml

	private BluetoothSocket bluetoothSocket = null;
	private OutputStream nxtOutputStream = null;
	private InputStream nxtInputStream = null;

	private String macAddress;

	public LegoNXTBtCommunicator(Handler uiHandler, Resources resources) {
		super(uiHandler, resources);
	}

	public void setMACAddress(String mMACaddress) {
		this.macAddress = mMACaddress;
	}

	/**
	 * Creates the connection, waits for incoming messages and dispatches them. The thread will be terminated
	 * on closing of the connection.
	 */
	@Override
	public void run() {

		try {
			createNXTconnection();
		} catch (IOException ioException) {
			Log.e(TAG, Log.getStackTraceString(ioException));
		}

		while (connected) {
			try {
				returnMessage = receiveMessage();
				if ((returnMessage.length >= 2)
						&& ((returnMessage[0] == LCPMessage.REPLY_COMMAND) || (returnMessage[0] == LCPMessage.DIRECT_COMMAND_NOREPLY))) {
					dispatchMessage(returnMessage);
				}

			} catch (IOException ioException) {
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
		BluetoothConnection bluetoothConnection = new BluetoothConnection(macAddress, SERIAL_PORT_SERVICE_CLASS_UUID);
		BluetoothConnection.State state = bluetoothConnection.connect();

		switch (state) {
			case CONNECTED:
				break;
			case ERROR_NOT_BONDED:
			case ERROR_STILL_BONDING:
				sendToast(resources.getString(R.string.no_paired_nxt));
			default:
				sendState(STATE_CONNECTERROR);
				throw new IOException("Bluetooth connecting error " + state.name());
		}

		bluetoothSocket = bluetoothConnection.getBluetoothSocket();
		nxtInputStream = bluetoothSocket.getInputStream();
		nxtOutputStream = bluetoothSocket.getOutputStream();
		connected = true;
		sendState(STATE_CONNECTED);
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
			if (bluetoothSocket != null) {
				connected = false;
				bluetoothSocket.close();
				bluetoothSocket = null;
			}

			nxtInputStream = null;
			nxtOutputStream = null;

		} catch (IOException ioException) {
			sendToast(resources.getString(R.string.problem_at_closing));
			Log.e(TAG, Log.getStackTraceString(ioException));
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
			throw new IOException("Outputstream was null");
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
			throw new IOException("Inputstream was null");
		}

		int length = nxtInputStream.read();
		length = (nxtInputStream.read() << 8) + length;
		byte[] returnMessage = new byte[length];
		nxtInputStream.read(returnMessage);
		//Log.i("bt", returnMessage.toString());
		return returnMessage;
	}

}

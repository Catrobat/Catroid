/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2013 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://developer.catrobat.org/license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
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
package org.catrobat.catroid.robot.albert;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.res.Resources;
import android.os.Handler;
import android.util.Log;

import org.catrobat.catroid.R;
import org.catrobat.catroid.bluetooth.BTConnectable;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.UUID;

//@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH) just needed for debug output: btSocket.isConnected()
public class RobotAlbertBtCommunicator extends RobotAlbertCommunicator {

	private static final UUID SERIAL_PORT_SERVICE_CLASS_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fc");
	//private static final UUID SERIAL_PORT_SERVICE_CLASS_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
	//private static final UUID SERIAL_PORT_SERVICE_CLASS_UUID = UUID.fromString("eb8ec53a-f070-46e0-b6ff-1645c931f858");

	// this is the only OUI registered by LEGO, see http://standards.ieee.org/regauth/oui/index.shtml

	private static final byte PACKET_HEADER_1 = (byte) 0xAA;
	private static final byte PACKET_HEADER_2 = 0x55;
	private static final byte PACKET_TAIL_1 = 0x0D;
	private static final byte PACKET_TAIL_2 = 0x0A;

	private static final byte COMMAND_SENSOR = 0x06;
	private static final byte COMMAND_EXTERNAL = 0x20;
	private BluetoothAdapter btAdapter;
	private BluetoothSocket btSocket = null;
	private OutputStream outputStream = null;
	private InputStream inputStream = null;

	private String mMACaddress;
	private BTConnectable myOwner;
	private static boolean DEBUG_OUTPUT = false;

	public RobotAlbertBtCommunicator(BTConnectable myOwner, Handler uiHandler, BluetoothAdapter btAdapter,
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
			createConnection();
		} catch (IOException e) {
		}

		while (connected) {

			//TODO: Test it on more devices. If everything works use this else use Version in branch albert_robot_presentation_V2 
			try {
				receiveMessage();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				Log.d("RobotAlbertBtComm", "IOException in run:receiveMessage occured: " + e.toString());
				//Log.d("Test", e.getMessage());
				//This error occurs if robot albert is suddenly switched of
				//if (e.getMessage().equalsIgnoreCase("Software caused connection abort")) {
				if (connected == true) {
					sendState(STATE_CONNECTERROR);
					connected = false;
				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				Log.d("RobotAlbertBtComm", "Exception in run:receiveMessage occured: " + e.toString());
				if (connected == true) {
					sendState(STATE_CONNECTERROR);
					connected = false;
				}
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
	public void createConnection() throws IOException {
		try {
			BluetoothSocket btSocketTemporary;
			BluetoothDevice btDevice = null;
			btDevice = btAdapter.getRemoteDevice(mMACaddress);
			if (btDevice == null) {
				if (uiHandler == null) {
					throw new IOException();
				} else {
					sendToast(mResources.getString(R.string.no_paired_nxt));
					sendState(STATE_CONNECTERROR);
					return;
				}
			}

			btSocketTemporary = btDevice.createRfcommSocketToServiceRecord(SERIAL_PORT_SERVICE_CLASS_UUID);
			try {

				Log.d("TestRobotAlbert", "before btSocketTemporary.connect();\n" + SERIAL_PORT_SERVICE_CLASS_UUID);
				btSocketTemporary.connect();
				Log.d("TestRobotAlbert", "after btSocketTemporary.connect()");

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

				//try another method for connection, this should work on the HTC desire, credits to Michael Biermann
				try {

					Method mMethod = btDevice.getClass().getMethod("createRfcommSocket", new Class[] { int.class });
					btSocketTemporary = (BluetoothSocket) mMethod.invoke(btDevice, Integer.valueOf(1));
					btSocketTemporary.connect();
				} catch (Exception e1) {
					if (uiHandler == null) {
						throw new IOException();
					} else {
						sendState(STATE_CONNECTERROR);
					}
					return;
				}
			}
			btSocket = btSocketTemporary;
			inputStream = btSocket.getInputStream();
			outputStream = btSocket.getOutputStream();
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
	public void destroyConnection() throws IOException {

		Log.d("RobotAlbertBtComm", "destroyRobotAlbertConnection");

		if (connected) {
			stopAllMovement();
		}

		try {
			if (btSocket != null) {
				connected = false;
				btSocket.close();
				btSocket = null;
			}

			inputStream = null;
			outputStream = null;

		} catch (IOException e) {
			if (uiHandler == null) {
				throw e;
			} else {
				sendToast(mResources.getString(R.string.problem_at_closing));
			}
		}
	}

	@Override
	public void stopAllMovement() {
		myHandler.removeMessages(0);
		myHandler.removeMessages(1);
		myHandler.removeMessages(2);
		resetRobotAlbert();
	}

	/**
	 * Sends a message on the opened OutputStream
	 * 
	 * @param message
	 *            , the message as a byte array
	 */
	@Override
	public void sendMessage(byte[] message) throws IOException {

		try {
			if (outputStream == null) {
				throw new IOException();
			}
			outputStream.write(message, 0, message.length);
			outputStream.flush();
		} catch (Exception e) {
			Log.d("RobotAlbertBtComm", "ERROR: Exception occured in sendMessage " + e.getMessage());
		}
	}

	/**
	 * Receives a message on the opened InputStream
	 * 
	 * @return the message
	 */
	@Override
	public byte[] receiveMessage() throws IOException, Exception {

		if (inputStream == null) {
			throw new IOException(" Software caused connection abort ");
		}

		@SuppressWarnings("unused")
		int read = 0;
		byte[] buf = new byte[1];

		int count = 0;

		do {
			do {
				checkIfDataIsAvailable(1);
				read = inputStream.read(buf);
				count++;
				if (count > 400) {
					return null;
				}
			} while (buf[0] != PACKET_HEADER_1);

			checkIfDataIsAvailable(1);
			read = inputStream.read(buf);
		} while (buf[0] != PACKET_HEADER_2);

		byte[] length = new byte[1];
		checkIfDataIsAvailable(1);
		read = inputStream.read(length);

		byte[] buffer = new byte[length[0] - 1];
		checkIfDataIsAvailable(length[0] - 1);
		read = inputStream.read(buffer);

		if (buffer[length[0] - 3] != PACKET_TAIL_1 || buffer[length[0] - 2] != PACKET_TAIL_2) {
			Log.d("RobotAlbertBtComm", "ERROR: Packet tail not found!");
			return null;
		}

		switch (buffer[0]) {
			case COMMAND_SENSOR:

				int leftDistance = (buffer[11] + buffer[13] + buffer[15] + buffer[17]) / 4;
				int rightDistance = (buffer[10] + buffer[12] + buffer[14] + buffer[16]) / 4;

				if (leftDistance > 25 || rightDistance > 25) {
					int divisor1 = 0;
					int divisor2 = 0;
					for (int i = 11; i < 19; i += 2) {
						if (buffer[i] != 0) {
							divisor1++;
						}
						if (buffer[i + 1] != 0) {
							divisor2++;
						}
					}
					if (divisor1 == 0) {
						divisor1 = 1;
					}
					if (divisor2 == 0) {
						divisor2 = 1;
					}
					leftDistance = (buffer[11] + buffer[13] + buffer[15] + buffer[17]) / divisor1;
					rightDistance = (buffer[10] + buffer[12] + buffer[14] + buffer[16]) / divisor2;
				}

				sensors.setValueOfLeftDistanceSensor(leftDistance);
				sensors.setValueOfRightDistanceSensor(rightDistance);

				if (DEBUG_OUTPUT == true) {
					Log.d("RobotAlbertBtComm", "receiveMessage:  leftDistance=" + leftDistance);
					Log.d("RobotAlbertBtComm", "receiveMessage: rightDistance=" + rightDistance);
				}

				break;
			case COMMAND_EXTERNAL:
				Log.d("RobotAlbertBtComm", "External Packet received!");
				break;

			default:
				Log.d("RobotAlbertBtComm", "Unknown Command! id = " + buffer[0]);
				break;
		}

		return buffer;
	}

	public void checkIfDataIsAvailable(int neededBytes) throws IOException {
		int available = 0;
		long timeStart = System.currentTimeMillis();
		long timePast;

		while (true) {
			if (inputStream == null) {
				throw new IOException(" Software caused connection abort ");
			}
			available = inputStream.available();
			if (available >= neededBytes) {
				break;
			}
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// here you can optionally check elapsed time, and time out
			timePast = System.currentTimeMillis();
			if ((timePast - timeStart) > 60000) {
				Log.d("AlbertRobot-Timeout", "TIMEOUT for receive message occured");
				throw new IOException(" Software caused connection abort because of timeout");
			}
		}
	}

}

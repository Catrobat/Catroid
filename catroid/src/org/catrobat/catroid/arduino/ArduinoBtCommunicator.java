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
 */
package org.catrobat.catroid.arduino;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.res.Resources;
import android.os.Handler;
import android.util.Log;

import org.catrobat.catroid.R;
import org.catrobat.catroid.bluetooth.BTConnectable;
import org.catrobat.catroid.bluetooth.BluetoothConnection;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.UUID;

public class ArduinoBtCommunicator extends ArduinoCommunicator {

	private static final UUID SERIAL_PORT_SERVICE_CLASS_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fc");

	private static final byte BOF = (byte) 126; //Ascii table "~"

	private static final String TAG = ArduinoBtCommunicator.class.getSimpleName();

	private BluetoothAdapter btAdapter;
	private BluetoothSocket btSocket = null;
	private OutputStream outputStream = null;
	private InputStream inputStream = null;

	private String macAddress;
	private BTConnectable myOwner;
	private static boolean debugOutput = true;

	public ArduinoBtCommunicator(Handler uiHandler, Resources resources) {
		super(uiHandler, resources);
	}

//	public ArduinoBtCommunicator(BTConnectable myOwner, Handler uiHandler, BluetoothAdapter btAdapter,
//			Resources resources) {
//		super(uiHandler, resources);
//
//		this.myOwner = myOwner;
//		this.btAdapter = btAdapter;
//	}

	public void setMACAddress(String macAddress) {
		this.macAddress = macAddress;
	}

	@Override
	public void run() {

		try {
			createConnection();
		} catch (IOException e) {
			Log.d("ArduinoBtComm", "Could not create connection");
		}

		while (isConnected) {
			try {
				Log.d("ArduinoBtComm", "receiveMessage() was called ");
				receiveMessage();

			} catch (IOException e) {
				Log.d("ArduinoBtComm", "IOException in run:receiveMessage occured: " + e.toString());
				if (isConnected == true) {
					sendState(STATE_CONNECTERROR);
					isConnected = false;
				}
			} catch (Exception e) {
				Log.d("ArduinoBtComm", "Exception in run:receiveMessage occured: " + e.toString());
				if (isConnected == true) {
					sendState(STATE_CONNECTERROR);
					isConnected = false;
				}
			}
		}
	}

	@Override
	public void createConnection() throws IOException {
			BluetoothConnection bluetoothConnection = new BluetoothConnection(macAddress,
					SERIAL_PORT_SERVICE_CLASS_UUID);
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

			btSocket = bluetoothConnection.getBluetoothSocket();
			inputStream = btSocket.getInputStream();
			outputStream = btSocket.getOutputStream();
			isConnected = true;
			sendState(STATE_CONNECTED);
	}

	@Override
	public void destroyConnection() throws IOException {

		Log.d("ArduinoBtComm", "destroyArduinoConnection");

		try {
			if (btSocket != null) {
				isConnected = false;
				btSocket.close();
				btSocket = null;
			}

			inputStream = null;
			outputStream = null;

		} catch (IOException e) {
			if (uiHandler == null) {
				throw e;
			} else {
				sendToast(resources.getString(R.string.problem_at_closing));
			}
		}
	}

	@Override
	public void stopSensors() {
		pauseArduinoBoard();
	}

	/**
	 * Sends a message on the opened OutputStream
	 * 
	 * @param message , the message as a byte array
	 */
	@Override
	public void sendMessage(byte[] message) throws IOException {
		Log.d("ArduinoBtComm", "<<< sendMessage() was called >>> ");
		try {
			if (outputStream == null) {
				throw new IOException();
			}
			Log.d("ArduinoBtComm", "message[] to write to the outputstream: " + +message[0] + message[1] + message[2]
					+ " length: " + message.length);
			outputStream.write(message, 0, message.length);
			outputStream.flush();
		} catch (Exception e) {
			Log.d("ArduinoBtComm", "ERROR: Exception occured in sendMessage " + e.getMessage());
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

		do {
			checkIfDataIsAvailable(1);
			read = inputStream.read(buf);
		} while (buf[0] != BOF);

		byte[] length = new byte[1];
		//checkIfDataIsAvailable(1);
		read = inputStream.read(length);

		byte[] buffer = new byte[length[0] - 48]; //48 decimal = 0
		//checkIfDataIsAvailable(length[0] - 1);
		read = inputStream.read(buffer);
		switch (buffer[0]) {
			case 'D':
				if ((buffer[1] > 0) && (buffer[2] > 0) && (buffer[3] > 0)) {
					sensors.setArduinoDigitalSensor(buffer[3]);
				} else {
					Log.d("ArduinoBtComm", "receiveMessage error: received message NOT saved ");
				}

				if (debugOutput == true) {
					Log.d("ArduinoBtComm", "sensor packet found");
					Log.d("ArduinoBtComm", "receiveMessage: Value=" + buffer[1] + buffer[2] + buffer[3]);
				}
				break;
			case 'A':
				int SensorValue = 0;
				int position = 1;
				for (int i = 6; i > 2; i--) {
					if ((buffer[i] - 48) >= 0) {
						SensorValue += (buffer[i] - 48) * position;
						position = position * 10;
					}
				}
				Log.d("ArduinoBtComm", "computed Analog-Sensor-Value: " + SensorValue);
				sensors.setArduinoAnalogSensor(SensorValue);

				if (debugOutput == true) {
					Log.d("ArduinoBtComm", "sensor packet found");
					Log.d("ArduinoBtComm", "receiveMessage: Value=" + buffer[1] + buffer[2] + buffer[3]);
				}
				break;
			default:
				Log.d("ArduinoBtComm", "Unknown Command! id = " + buffer[0]);
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
			} catch (InterruptedException interruptedException) {
				Log.e(TAG, Log.getStackTraceString(interruptedException));
			}
			// here you can optionally check elapsed time, and time out
			timePast = System.currentTimeMillis();
			if ((timePast - timeStart) > 16000) {
				Log.d("Arduino-Timeout", "TIMEOUT for receive message occured");
				throw new IOException(" Software caused connection abort because of timeout");
			}
		}
	}
}

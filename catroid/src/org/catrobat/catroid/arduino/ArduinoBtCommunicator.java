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

public class ArduinoBtCommunicator extends ArduinoCommunicator {

	private static final String TAG = ArduinoBtCommunicator.class.getSimpleName();
	private static final UUID SERIAL_PORT_SERVICE_CLASS_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fc");

	private static final byte BOF = (byte) 126; //Ascii table "~"

	private BluetoothSocket btSocket = null;
	private OutputStream arduinoOutputStream = null;
	private InputStream arduinoInputStream = null;

	private String macAddress;

	public ArduinoBtCommunicator(Handler uiHandler, Resources resources) {
		super(uiHandler, resources);
	}

	public void setMACAddress(String macAddress) {
		this.macAddress = macAddress;
	}

	@Override
	public void run() {

		try {
			createArduinoConnection();
		} catch (IOException ioException) {
			Log.e(TAG, Log.getStackTraceString(ioException));
		}

		while (isConnected) {
			try {
				receiveMessage();
			} catch (IOException ioException) {
				if (isConnected) {
					sendState(STATE_RECEIVEERROR);
				}
			} catch (Exception ioException) {
				if (isConnected) {
					sendState(STATE_RECEIVEERROR);
				}
				return;
			}
		}
	}

	@Override
	public void createArduinoConnection() throws IOException {
		BluetoothConnection bluetoothConnection = new BluetoothConnection(macAddress,
				SERIAL_PORT_SERVICE_CLASS_UUID);
		BluetoothConnection.State state = bluetoothConnection.connect();

		switch (state) {
			case CONNECTED:
				break;
			case ERROR_NOT_BONDED:
			case ERROR_STILL_BONDING:
				sendToast(resources.getString(R.string.no_paired_nxt)); //change stringS
			default:
				sendState(STATE_CONNECTERROR);
				throw new IOException("Bluetooth connecting error " + state.name());
		}

		btSocket = bluetoothConnection.getBluetoothSocket();
		arduinoInputStream = btSocket.getInputStream();
		arduinoOutputStream = btSocket.getOutputStream();
		isConnected = true;
		sendState(STATE_CONNECTED);
	}

	@Override
	public void destroyArduinoConnection() throws IOException {
		try {
			if (btSocket != null) {
				isConnected = false;
				btSocket.close();
				btSocket = null;
			}

			arduinoInputStream = null;
			arduinoOutputStream = null;

		} catch (IOException ioException) {
			sendToast(resources.getString(R.string.problem_at_closing));
			Log.e(TAG, Log.getStackTraceString(ioException));
		}
	}

	@Override
	public void stopSensors() {
		pauseArduinoBoard();
	}

	@Override
	public void sendMessage(byte[] message) throws IOException {
		Log.d("ArduinoBtComm", "<<< sendMessage() was called >>> ");

		if (arduinoOutputStream == null) {
			throw new IOException("Arduino Outputstream was null");
		}
		Log.d("ArduinoBtComm", "message[] to write to the outputstream: " + +message[0] + message[1] + message[2]
				+ " length: " + message.length);
		arduinoOutputStream.write(message, 0, message.length);
		arduinoOutputStream.flush();
	}

	@Override
	public byte[] receiveMessage() throws IOException, Exception {

		if (arduinoInputStream == null) {
			throw new IOException("Arduino Inputstream was null");
		}

		@SuppressWarnings("unused")
		int read = 0;
		byte[] buf = new byte[1];

		do {
			//checkIfDataIsAvailable(1);
			read = arduinoInputStream.read(buf);
		} while (buf[0] != BOF);

		byte[] length = new byte[1];
		//checkIfDataIsAvailable(1);
		read = arduinoInputStream.read(length);

		byte[] buffer = new byte[length[0] - 48]; //48 decimal = 0
		//checkIfDataIsAvailable(length[0] - 1);
		read = arduinoInputStream.read(buffer);
		switch (buffer[0]) {
			case 'D':
				if ((buffer[1] > 0) && (buffer[2] > 0) && (buffer[3] > 0)) {
					sensors.setArduinoDigitalSensor(buffer[3]);
				} else {
					Log.d("ArduinoBtComm", "receiveMessage error: received message NOT saved ");
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
				break;
			default:
				Log.d("ArduinoBtComm", "Unknown Command! id = " + buffer[0]);
				break;
		}
		return buffer;
	}

	/*public void checkIfDataIsAvailable(int neededBytes) throws IOException {
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
	}*/
}

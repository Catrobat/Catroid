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
package org.catrobat.catroid.uitest.util;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import junit.framework.Assert;

import org.apache.http.util.ByteArrayBuffer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.UUID;

public class BTDummyClient {
	private BluetoothAdapter btAdapter = null;
	private BluetoothSocket btSocket = null;
	private OutputStream outputStream = null;
	private InputStream inputStream = null;
	//  private String MACAddress = "00:1F:3A:E9:70:58"; // Martin Laptop
	//	private String MACAddress = "EC:55:F9:DE:41:6A"; // Manuel Laptop
	private String MACAddress = "40:2C:F4:69:D0:21"; // Michael Laptop
	private ByteArrayBuffer receivedFeedback = new ByteArrayBuffer(1024);
	private boolean connected = false;

	private static final String CLOSECONNECTION = "closethisconnection";
	private static final String COMMANDSETVARIABLE = "setvariable;";
	//public static final UUID DUMMYCONNECTIONUUID = UUID.fromString("eb8ec53af07046e0b6ff1645c931f858");
	public static final UUID DUMMYCONNECTIONUUID = UUID.fromString("eb8ec53a-f070-46e0-b6ff-1645c931f858");
	//public static final UUID TEMPUUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
	//public static final UUID TEMPUUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
	//public static final UUID DUMMYCONNECTIONUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	//public static final UUID DUMMYCONNECTIONUUID = new UUID("1101", true);//UUID.fromString("1101");
	public static final String SERVERDUMMYROBOTALBERT = "albert;" + "0000110100001000800000805F9B34FC";

	public BTDummyClient() {
		this.btAdapter = BluetoothAdapter.getDefaultAdapter();
		Assert.assertTrue(
				"Just put there for testing. Lint throws a warining if this class doesnt contain any assertions", true);
	}

	public void initializeAndConnectToServer(String option) {
		BluetoothDevice dummyServer = btAdapter.getRemoteDevice(MACAddress);

		if (!connected) {
			try {
				btSocket = dummyServer.createRfcommSocketToServiceRecord(DUMMYCONNECTIONUUID);
				Log.d("TestRobotAlbert", "before btSocket.connect();");
				btSocket.connect();
				Log.d("TestRobotAlbert", "after btSocket.connect();");
				connected = true;

				this.outputStream = btSocket.getOutputStream();
				outputStream.write(option.getBytes(), 0, option.length());
				outputStream.flush();

				readFeedbackThread.start();
			} catch (IOException e) {
				Log.e("DummyServer", "DummyServer is not running pls start it");
				return;
			}
		}
	}

	private final Thread readFeedbackThread = new Thread() {
		@Override
		public void run() {
			try {
				inputStream = btSocket.getInputStream();
				byte[] buffer = new byte[1024];
				int receivedbytes = 0;

				while (true) {
					receivedbytes = inputStream.read(buffer);
					if (receivedbytes < 0) {
						break;
					}
					if (new String(buffer, 0, receivedbytes, "ASCII").equals(CLOSECONNECTION)) {
						inputStream.close();
						break;
					}
					receivedFeedback.append(buffer, 0, receivedbytes);
				}

			} catch (IOException e) {
				Log.d("Multiplayer", "TestReceiver Thread END in Exeption");
			}
			Log.d("Multiplayer", "TestReceiver Thread END");
		}
	};

	public ByteArrayBuffer getReceivedFeedback() {
		return receivedFeedback;
	}

	public Double getVariableValue(String variableName) {
		String receivedFeedbackString;
		try {
			receivedFeedbackString = new String(receivedFeedback.toByteArray(), 0, receivedFeedback.length(), "ASCII");
			int startIndexValue = receivedFeedbackString.indexOf(variableName);
			if (startIndexValue != -1) {
				Double variableValue = ByteBuffer.wrap(receivedFeedback.toByteArray()).getDouble(startIndexValue);
				return variableValue;
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return null;
	}

	public void sendSetVariableCommandToDummyServer(String name, Double value) throws IOException {
		byte[] bytes = new byte[1024];

		ByteBuffer.wrap(COMMANDSETVARIABLE.getBytes());
		ByteBuffer.wrap(bytes).put((name + ":").getBytes());
		ByteBuffer.wrap(bytes).putDouble(name.length(), value);
		outputStream.write(bytes, 0, name.length() + 1 + COMMANDSETVARIABLE.length() + 8);
		outputStream.flush();
	}
}

/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010  Catroid development team 
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package at.tugraz.ist.catroid.arduino;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import at.abraxas.amarino.Amarino;
import at.tugraz.ist.catroid.bluetooth.BTConnectable;
import at.tugraz.ist.catroid.bluetooth.BtCommunicator;
import at.tugraz.ist.catroid.content.bricks.SensorBrick;

public class ArduinoBtCommunicator extends Thread implements BtCommunicator {

	private BTConnectable myOwner;
	private Handler uiHandler;
	private BluetoothAdapter btAdapter;
	private Resources mResources;
	private double[] outGoingPackage;
	//private int type;
	private Context context;
	private String mMACaddress;
	private boolean connected = false;
	public static final int STANDARD_ARDUINO_PACKAGE = 0;
	public static final int DISCONNECT = -1;
	private static final UUID SERIAL_PORT_SERVICE_CLASS_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	private BluetoothSocket nxtBTsocket = null;
	private OutputStream nxtOutputStream = null;
	private InputStream nxtInputStream = null;

	public ArduinoBtCommunicator(BTConnectable myOwner, Handler uiHandler, BluetoothAdapter btAdapter, Context context) {
		this.myOwner = myOwner;
		this.uiHandler = uiHandler;
		this.btAdapter = btAdapter;
		this.context = context;
	}

	/**
	 * Creates the connection, waits for incoming messages and dispatches them. The thread will be terminated
	 * on closing of the connection.
	 */
	@Override
	public void run() {

		createArduinoConnection();

		//		while (connected) {
		//			try {
		//				byte[] returnMessage = receiveMessage();
		//
		//				Log.i("bt", "lolol arduino " + returnMessage[0]);
		//
		//			} catch (IOException e) {
		//				// don't inform the user when connection is already closed
		//				Log.i("bt", "alles kaputt");
		//				return;
		//			}
		//		}

	}

	//

	public void createArduinoConnection() {

		//Arduino lib
		if (btAdapter.isEnabled() && btAdapter.getBondedDevices() != null) {
			//Log.i("bt", "Connection is up " + mMACaddress);
			//Amarino.connect(this.context, this.mMACaddress);
			connected = true;
		}

		//		//bluetooth input
		//		BluetoothSocket nxtBTSocketTemporary = null;
		//		BluetoothDevice nxtDevice = null;
		//		nxtDevice = btAdapter.getRemoteDevice(mMACaddress);
		//
		//		try {
		//			nxtBTSocketTemporary = nxtDevice.createRfcommSocketToServiceRecord(SERIAL_PORT_SERVICE_CLASS_UUID);
		//			nxtBTSocketTemporary.connect();
		//		} catch (IOException e2) {
		//			// TODO Auto-generated catch block
		//			e2.printStackTrace();
		//		}
		//
		//		// try another method for connection, this should work on the HTC desire, credits to Michael Biermann
		//		//			try {
		//		//
		//		//				Method mMethod = nxtDevice.getClass().getMethod("createRfcommSocket", new Class[] { int.class });
		//		//				nxtBTSocketTemporary = (BluetoothSocket) mMethod.invoke(nxtDevice, Integer.valueOf(1));
		//		//				nxtBTSocketTemporary.connect();
		//		//			} catch (Exception e1) {
		//		//				if (uiHandler == null) {
		//		//					throw new IOException();
		//		//				}
		//		//				return;
		//		//			}
		//
		//		nxtBTsocket = nxtBTSocketTemporary;
		//		try {
		//			nxtInputStream = nxtBTsocket.getInputStream();
		//		} catch (IOException e) {
		//			// TODO Auto-generated catch block
		//			e.printStackTrace();
		//		}
		//nxtOutputStream = nxtBTsocket.getOutputStream();
	}

	public void sendArduinoPackage(int type, double[] message) {
		Log.i("bt", "send this: " + type);

		if (btAdapter.isEnabled() && btAdapter.getBondedDevices() != null) {
			Amarino.connect(this.context, this.mMACaddress);
			if (type == SensorBrick.DIGITAL) {
				Amarino.sendDataToArduino(this.context, this.mMACaddress, 'd', outGoingPackage);
			} else if (type == SensorBrick.ANALOG) {
				Amarino.sendDataToArduino(this.context, this.mMACaddress, 'a', outGoingPackage);
				Log.i("bt", "analog" + type);
			}
		}

		Amarino.disconnect(this.context, this.mMACaddress);

		//		if (type == SensorBrick.DIGITAL) {
		//			Amarino.sendDataToArduino(this.context, this.mMACaddress, 'd', outGoingPackage);
		//
		//		} else if (type == SensorBrick.ANALOG) {
		//			Amarino.sendDataToArduino(this.context, this.mMACaddress, 'a', outGoingPackage);
		//		}
	}

	/**
	 * 
	 */
	//	public void setOutGoingMessage(double[] outGoingPackage, int type) {
	//		// TODO Auto-generated method stub
	//		this.outGoingPackage = outGoingPackage;
	//		this.type = type;
	//	}

	/**
	 * Closes the bluetooth connection. On error the method either sends a message
	 * to it's owner or creates an exception in the case of no message handler.
	 */
	public void destroyArduinoConnection() throws IOException {
		connected = false;
		Amarino.disconnect(this.context, this.mMACaddress);
	}

	public void setMACAddress(String mMACaddress) {
		Log.d("TAG", "MAC:" + mMACaddress);
		this.mMACaddress = mMACaddress;
	}

	public boolean isConnected() {
		return connected;
	}

	final Handler myHandler = new Handler() {
		@Override
		public void handleMessage(Message myMessage) {
			Log.d("TAG", "message" + myMessage.getData().getInt("message"));

			switch (myMessage.what) {
				case STANDARD_ARDUINO_PACKAGE:
					sendArduinoPackage(myMessage.getData().getInt("type"), myMessage.getData()
							.getDoubleArray("package"));
					break;
				case DISCONNECT:
					break;

			}
		}
	};

	/*
	 * (non-Javadoc)
	 * 
	 * @see at.tugraz.ist.catroid.bluetooth.BtCommunicator#getHandler()
	 */
	public Handler getHandler() {
		return myHandler;
	}

	public byte[] receiveMessage() throws IOException {
		if (nxtInputStream == null) {
			Log.i("bt", "inputstream null");
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

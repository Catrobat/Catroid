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

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.util.Log;
import at.abraxas.amarino.Amarino;
import at.tugraz.ist.catroid.bluetooth.BTConnectable;
import at.tugraz.ist.catroid.bluetooth.BtCommunicator;
import at.tugraz.ist.catroid.content.bricks.SensorBrick;

/**
 * @author Sercan Akpolat
 * 
 */
public class ArduinoBtCommunicator extends Thread implements BtCommunicator {

	private BTConnectable myOwner;
	private Handler uiHandler;
	private BluetoothAdapter btAdapter;
	private Resources mResources;
	private double[] outGoingPackage;
	private int type;
	private Context context;
	private String mMACaddress;
	private boolean connected = false;
	private Handler myHandler;

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

		if (this.connected) {
			if (btAdapter.isEnabled() && btAdapter.getBondedDevices() != null) {
				Amarino.connect(this.context, this.mMACaddress);
				if (type == SensorBrick.DIGITAL) {
					Amarino.sendDataToArduino(this.context, this.mMACaddress, 'd', outGoingPackage);
				} else if (type == SensorBrick.ANALOG) {
					Amarino.sendDataToArduino(this.context, this.mMACaddress, 'a', outGoingPackage);
				}
			}

			Amarino.disconnect(this.context, this.mMACaddress);
		}
	}

	/**
	 * 
	 */
	public void setOutGoingMessage(double[] outGoingPackage, int type) {
		// TODO Auto-generated method stub
		this.outGoingPackage = outGoingPackage;
		this.type = type;
	}

	/**
	 * Closes the bluetooth connection. On error the method either sends a message
	 * to it's owner or creates an exception in the case of no message handler.
	 */
	public void destroyArduinoConnection() throws IOException {
		connected = false;
	}

	public void setMACAddress(String mMACaddress) {
		Log.d("TAG", "MAC:" + mMACaddress);
		this.mMACaddress = mMACaddress;
	}

	public boolean isConnected() {
		return connected;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see at.tugraz.ist.catroid.bluetooth.BtCommunicator#getHandler()
	 */
	public Handler getHandler() {
		return myHandler;
	}

}

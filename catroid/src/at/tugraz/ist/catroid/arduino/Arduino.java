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

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import at.tugraz.ist.catroid.LegoNXT.LegoNXTBtCommunicator;
import at.tugraz.ist.catroid.bluetooth.BTConnectable;

/**
 * @author Sercan Akpolat
 * 
 */
public class Arduino implements BTConnectable {

	private ArduinoBtCommunicator myBTCommunicator;

	private boolean pairing;
	private static Handler btcHandler;
	private Handler recieverHandler;
	private Context context;
	private ProgressDialog connectingProgressDialog;

	public Arduino(Context context, Handler recieverHandler) {
		this.context = context;
		this.recieverHandler = recieverHandler;
	}

	/**
	 * 
	 */
	public void startConnection(String macAddress) {
		if (myBTCommunicator != null) {
			myBTCommunicator.destroy();
		}
		myBTCommunicator = new ArduinoBtCommunicator(this, recieverHandler, BluetoothAdapter.getDefaultAdapter(),
				context);
		btcHandler = myBTCommunicator.getHandler();
		myBTCommunicator.setMACAddress(macAddress);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see at.tugraz.ist.catroid.bluetooth.BTConnectable#isPairing()
	 */
	public boolean isPairing() {
		// TODO Auto-generated method stub
		return false;
	}

	public static Handler getBTCHandler() {
		return btcHandler;
	}

	/**
	 * @throws IOException
	 * 
	 */
	public void destroyBTCommunicator() throws IOException {

		if (myBTCommunicator != null) {
			myBTCommunicator.destroyArduinoConnection();

			myBTCommunicator = null;
		}
	}

	/**
	 * Receive messages from the BTCommunicator
	 */
	final Handler myHandler = new Handler() {
		@Override
		public void handleMessage(Message myMessage) {
			Log.d("TAG", "message" + myMessage.getData().getInt("message"));

			switch (myMessage.getData().getInt("message")) {
				case LegoNXTBtCommunicator.DISPLAY_TOAST:
					//showToast(myMessage.getData().getString("toastText"), Toast.LENGTH_SHORT);
					break;
				case LegoNXTBtCommunicator.STATE_CONNECTED:
					connectingProgressDialog.dismiss();

					break;

			}
		}
	};

}

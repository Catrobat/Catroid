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
package at.tugraz.ist.catroid.connections;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * @author manuelzoderer
 */

public class Bluetooth {

	private String selectedAddress;
	public Context context;
	private BluetoothAdapter bluetoothAdapter;

	/**
	 * @param context
	 */
	public Bluetooth(Context context) {
		this.context = context;
		this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		BluetoothBroadcastReceiver broadCaster = new BluetoothBroadcastReceiver(context);
	}

	/**
	 * @return the bluetoothAdapter
	 */
	public BluetoothAdapter getBluetoothAdapter() {
		return bluetoothAdapter;
	}

	public String getBluetoothArrayAdapter() {

		return selectedAddress;
	}

	public void start() {

		int REQUEST_ENABLE_BT = 0;

		// Checking if the Device supports Bluetooth
		if (bluetoothAdapter == null) {
		}

		Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		context.startActivity(enableBtIntent);

	}

	/**
	 * 
	 */

	public void checkForDevices() {

		//Stop any discovery before starting to discovering for our Brick
		//bluetoothAdapter.cancelDiscovery();

		if (bluetoothAdapter != null) {
			if (bluetoothAdapter.isDiscovering()) {
				bluetoothAdapter.cancelDiscovery();
			}

			bluetoothAdapter.startDiscovery();
		}

		Log.d("FOUND", "Scannvorgang gestartet");

	}
}

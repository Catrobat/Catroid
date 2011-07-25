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
package at.tugraz.ist.catroid.io;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;

/**
 * @author une
 * 
 */
public class BluetoothManager {
	private static final int REQUEST_ENABLE_BT = 2000;
	private BluetoothAdapter bluetoothAdapter;
	private Activity activity;

	public BluetoothManager(Activity activity) {
		this.activity = activity;
		bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

	}

	// Returns -1 if no Bluetooth adapter, 0 if Bluetooth off, 1 if Bluetooth on
	public boolean activateBluetooth() {
		if (bluetoothAdapter == null) {
			return false;
		}

		if (!bluetoothAdapter.isEnabled()) {
			Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			activity.startActivity(enableBtIntent);
		}
		//		while (!bluetoothAdapter.isEnabled()) { // find better solution
		//			;
		//		}
		return true;
	}

}

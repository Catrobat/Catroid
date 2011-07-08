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

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.ArrayAdapter;

/**
 * @author manuelzoderer
 * 
 */
public class BluetoothBroadcastReceiver extends BroadcastReceiver {

	ArrayAdapter<String> btArrayAdapter;

	/**
	 * @param btArrayAdapter
	 */
	public BluetoothBroadcastReceiver(Context context) {
		this.btArrayAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1);
		Log.d("FOUND", "BluetoothBroadcastReceiver aufgerufen");
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		Log.d("FOUND", intent.getAction());
		if (BluetoothDevice.ACTION_FOUND.equals(action)) {
			BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

			btArrayAdapter.add(device.getName() + "\n" + device.getAddress());
			btArrayAdapter.notifyDataSetChanged();
			Log.d("FOUND", device.getName());
		}
	}

}

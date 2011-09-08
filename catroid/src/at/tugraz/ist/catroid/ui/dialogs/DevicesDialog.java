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

package at.tugraz.ist.catroid.ui.dialogs;

import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.content.bricks.SensorBrick;

public class DevicesDialog extends Dialog implements OnClickListener {

	protected Button scanButton;
	protected Context context;
	protected ListView listView;
	protected Button referencedButton;
	private ArrayAdapter<String> mNewDevicesArrayAdapter;
	private BluetoothAdapter bluetoothAdapter;
	private String selectedAddress;
	private String selectedInfo;
	private Button bluetoothButton;
	private SensorBrick sensorBrick;
	private Button exitButton;

	public DevicesDialog(Context context, Button bluetoothButton, SensorBrick sensorBrick) {
		super(context);
		this.context = context;
		this.bluetoothButton = bluetoothButton;
		this.sensorBrick = sensorBrick;
		this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		this.mNewDevicesArrayAdapter = new ArrayAdapter<String>(context, R.layout.dialog_list_items_custom_simple_1);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dialog_bluetooth_bluetooth_devices);
		setCanceledOnTouchOutside(true);

		scanButton = (Button) findViewById(R.id.scan_again_button);
		scanButton.setOnClickListener(this);
		exitButton = (Button) findViewById(R.id.exit_button);
		exitButton.setOnClickListener(this);

		// Find and set up the ListView for paired devices
		listView = (ListView) findViewById(R.id.new_devices);
		listView.setAdapter(mNewDevicesArrayAdapter);
		listView.setOnItemClickListener(listOnClickListener);

		super.onCreate(savedInstanceState);
	}

	public void checkForDevices() {
		mNewDevicesArrayAdapter.clear();

		IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		context.registerReceiver(mReceiver, intentFilter);

		intentFilter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		context.registerReceiver(mReceiver, intentFilter);

		if (bluetoothAdapter.isDiscovering()) {
			bluetoothAdapter.cancelDiscovery();
		}

		// Request discover from BluetoothAdapter
		bluetoothAdapter.startDiscovery();
	}

	private OnItemClickListener listOnClickListener = new OnItemClickListener() {
		public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
			bluetoothAdapter.cancelDiscovery();

			selectedInfo = ((TextView) v).getText().toString();
			selectedAddress = selectedInfo.substring(selectedInfo.length() - 17);
			sensorBrick.setSelectedAddress(selectedAddress);
			dismiss();
		}
	};

	/**
	 * @return the selectedAddress
	 */
	public String getSelectedAddress() {
		return selectedAddress;
	}

	/**
	 * @return the selectedInfo
	 */
	public String getSelectedInfo() {
		return selectedInfo;
	}

	// The BroadcastReceiver that listens for discovered devices and
	// changes the title when discovery is finished
	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {

			String action = intent.getAction();

			// When discovery finds a device
			if (BluetoothDevice.ACTION_FOUND.equals(action)) {

				// Get the BluetoothDevice object from the Intent
				BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				// If it's already paired, skip it, because it's been listed already
				if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
					mNewDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
				}
				// When discovery is finished
			} else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
				if (mNewDevicesArrayAdapter.getCount() == 0) {
					Toast.makeText(context, R.string.error_no_bluetooth_devices_available, Toast.LENGTH_SHORT).show();
				}
			}
		}
	};

	public void onClick(View v) {
		if (v.getId() == bluetoothButton.getId()) {
			Log.d("TAG", "button id");
			checkForDevices();
			show();
		}

		switch (v.getId()) {
			case R.id.scan_again_button:
				Log.d("TAG", "scan");
				checkForDevices();
				break;
			case R.id.exit_button:
				Log.d("TAG", "exit");
				this.dismiss();
				break;
		}
	}
}

/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010-2011 The Catroid Team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://www.catroid.org/catroid_license_additional_term
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *    
 *    This file incorporates work covered by the following copyright and  
 *    permission notice: 
 *    
 *		   	Copyright 2010 Guenther Hoelzl, Shawn Brown
 *
 *		   	This file is part of MINDdroid.
 *
 * 		  	MINDdroid is free software: you can redistribute it and/or modify
 * 		  	it under the terms of the GNU Affero General Public License as
 * 		  	published by the Free Software Foundation, either version 3 of the
 *   		License, or (at your option) any later version.
 *
 *   		MINDdroid is distributed in the hope that it will be useful,
 *   		but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   		MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   		GNU Affero General Public License for more details.
 *
 *   		You should have received a copy of the GNU Affero General Public License
 *   		along with MINDdroid.  If not, see <http://www.gnu.org/licenses/>.
 */

package at.tugraz.ist.catroid.bluetooth;

import java.util.ArrayList;
import java.util.Set;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import at.tugraz.ist.catroid.R;

public class DeviceListActivity extends Activity {
	public static final String PAIRING = "pairing";
	public static final String AUTO_CONNECT = "auto_connect";
	public static final String DEVICE_NAME_AND_ADDRESS = "device_infos";
	public static final String EXTRA_DEVICE_ADDRESS = "device_address";

	private BluetoothAdapter btAdapter;
	private ArrayAdapter<String> pairedDevicesArrayAdapter;
	private ArrayAdapter<String> newDevicesArrayAdapter;
	private boolean autoConnect = true;
	private static ArrayList<String> autoConnectIDs = new ArrayList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (autoConnectIDs.size() == 0) {

			autoConnectIDs.add(BtCommunicator.OUI_LEGO);
		}
		autoConnect = this.getIntent().getExtras().getBoolean(AUTO_CONNECT);
		//Log.i("bto", autoConnect + "");
		if (autoConnect) {
			this.setVisible(false);
		}

		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.device_list);
		setTitle(R.string.select_device);

		setResult(Activity.RESULT_CANCELED);

		Button scanButton = (Button) findViewById(R.id.button_scan);
		scanButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				doDiscovery();
				v.setVisibility(View.GONE);
			}
		});

		pairedDevicesArrayAdapter = new ArrayAdapter<String>(this, R.layout.device_name);
		newDevicesArrayAdapter = new ArrayAdapter<String>(this, R.layout.device_name);

		ListView pairedListView = (ListView) findViewById(R.id.paired_devices);
		pairedListView.setAdapter(pairedDevicesArrayAdapter);
		pairedListView.setOnItemClickListener(mDeviceClickListener);

		ListView newDevicesListView = (ListView) findViewById(R.id.new_devices);
		newDevicesListView.setAdapter(newDevicesArrayAdapter);
		newDevicesListView.setOnItemClickListener(mDeviceClickListener);

		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		this.registerReceiver(mReceiver, filter);

		filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		this.registerReceiver(mReceiver, filter);

		btAdapter = BluetoothAdapter.getDefaultAdapter();

		Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();

		BluetoothDevice legoNXT = null;
		int possibleConnections = 0;
		if (pairedDevices.size() > 0) {
			findViewById(R.id.title_paired_devices).setVisibility(View.VISIBLE);
			for (BluetoothDevice device : pairedDevices) {
				for (String item : autoConnectIDs) {
					if (device.getAddress().startsWith(item)) {
						legoNXT = device;
						possibleConnections++;
						//legoDevicesFound = true;
						//legoDevicesArrayAdapter.add(device.getName() + "-" + device.getAddress());
					}
				}

				pairedDevicesArrayAdapter.add(device.getName() + "-" + device.getAddress());
			}
		}

		if (pairedDevices.size() == 0) {
			String noDevices = getResources().getText(R.string.none_paired).toString();
			pairedDevicesArrayAdapter.add(noDevices);
		}

		if (autoConnect && possibleConnections == 1) {
			//			String info = ((TextView) v).getText().toString();
			//			if (info.lastIndexOf('-') != info.length() - 18) {
			//				return;
			//			}

			btAdapter.cancelDiscovery();
			Intent intent = new Intent();
			Bundle data = new Bundle();
			data.putString(DEVICE_NAME_AND_ADDRESS, legoNXT.getName() + "-" + legoNXT.getAddress());
			data.putString(EXTRA_DEVICE_ADDRESS, legoNXT.getAddress());
			data.putBoolean(PAIRING, false);
			data.putBoolean(AUTO_CONNECT, true);
			intent.putExtras(data);
			setResult(RESULT_OK, intent);
			finish();
			//			this.setVisible(false);
		} else {
			this.setVisible(true);
		}
		autoConnect = true;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		if (btAdapter != null) {
			btAdapter.cancelDiscovery();
		}

		this.unregisterReceiver(mReceiver);
	}

	private void doDiscovery() {

		setProgressBarIndeterminateVisibility(true);
		setTitle(R.string.scanning);

		findViewById(R.id.title_new_devices).setVisibility(View.VISIBLE);

		if (btAdapter.isDiscovering()) {
			btAdapter.cancelDiscovery();
		}

		btAdapter.startDiscovery();
	}

	private OnItemClickListener mDeviceClickListener = new OnItemClickListener() {
		public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {

			String info = ((TextView) v).getText().toString();
			if (info.lastIndexOf('-') != info.length() - 18) {
				return;
			}

			btAdapter.cancelDiscovery();
			String address = info.substring(info.lastIndexOf('-') + 1);
			Intent intent = new Intent();
			Bundle data = new Bundle();
			data.putString(DEVICE_NAME_AND_ADDRESS, info);
			data.putString(EXTRA_DEVICE_ADDRESS, address);
			data.putBoolean(PAIRING, av.getId() == R.id.new_devices);
			data.putBoolean(AUTO_CONNECT, false);
			intent.putExtras(data);
			setResult(RESULT_OK, intent);
			finish();
		}
	};

	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();

			if (BluetoothDevice.ACTION_FOUND.equals(action)) {
				BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				if ((device.getBondState() != BluetoothDevice.BOND_BONDED)) {
					newDevicesArrayAdapter.add(device.getName() + "-" + device.getAddress());
				}
			} else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
				setProgressBarIndeterminateVisibility(false);
				setTitle(R.string.select_device);
				if (newDevicesArrayAdapter.getCount() == 0) {
					String noDevices = getResources().getText(R.string.none_found).toString();
					newDevicesArrayAdapter.add(noDevices);
				}
			}
		}
	};

}
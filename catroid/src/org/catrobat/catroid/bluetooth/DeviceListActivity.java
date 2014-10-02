/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2014 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * An additional term exception under section 7 of the GNU Affero
 * General Public License, version 3, is available at
 * http://developer.catrobat.org/license_additional_term
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.bluetooth;

import android.app.Activity;
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

import org.catrobat.catroid.R;

import java.util.ArrayList;
import java.util.Set;

public class DeviceListActivity extends Activity {
	public static final String PAIRING = "pairing";
	public static final String AUTO_CONNECT = "auto_connect";
	public static final String DEVICE_NAME_AND_ADDRESS = "device_infos";
	public static final String EXTRA_DEVICE_ADDRESS = "device_address";
	public static final String BLUETOOTH_DEVICE = "bluetooth_device";
	public static final int BLUETOOTH_ACTIVATION_CANCELED = -101;
	public static final int BLUETOOTH_NOT_SUPPORTED = -100;
	private static final int LENGTH_OF_FOO = 18; //TODO: figure out the meaning of the value

	private OnItemClickListener deviceClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> av, View view, int position, long id) {

			String info = ((TextView) view).getText().toString();
			if (info.lastIndexOf('-') != info.length() - LENGTH_OF_FOO) {
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
			data.putInt(BLUETOOTH_DEVICE, bluetoothDeviceConstant);
			intent.putExtras(data);
			setResult(RESULT_OK, intent);
			finish();
		}
	};
	private static ArrayList<String> autoConnectIDs = new ArrayList<String>();
	private final BroadcastReceiver receiver = new BroadcastReceiver() {
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
				if (newDevicesArrayAdapter.isEmpty()) {
					String noDevices = getResources().getString(R.string.none_found);
					newDevicesArrayAdapter.add(noDevices);
				}
			}
		}
	};
	private BluetoothAdapter btAdapter;
	private ArrayAdapter<String> pairedDevicesArrayAdapter;
	private ArrayAdapter<String> newDevicesArrayAdapter;
	private int bluetoothDeviceConstant;
	private boolean autoConnect = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (autoConnectIDs.size() == 0) {
			autoConnectIDs.add(BtCommunicator.OUI_LEGO);
		}
		autoConnect = this.getIntent().getExtras().getBoolean(AUTO_CONNECT);
		if (autoConnect) {
			this.setVisible(false);
		}

		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.device_list);
		setTitle(R.string.select_device);

		setResult(Activity.RESULT_CANCELED);

		Button scanButton = (Button) findViewById(R.id.button_scan);
		scanButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				doDiscovery();
				view.setVisibility(View.GONE);
			}
		});

		pairedDevicesArrayAdapter = new ArrayAdapter<String>(this, R.layout.device_name);
		newDevicesArrayAdapter = new ArrayAdapter<String>(this, R.layout.device_name);

		ListView pairedListView = (ListView) findViewById(R.id.paired_devices);
		pairedListView.setAdapter(pairedDevicesArrayAdapter);
		pairedListView.setOnItemClickListener(deviceClickListener);

		ListView newDevicesListView = (ListView) findViewById(R.id.new_devices);
		newDevicesListView.setAdapter(newDevicesArrayAdapter);
		newDevicesListView.setOnItemClickListener(deviceClickListener);

		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		this.registerReceiver(receiver, filter);

		filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		this.registerReceiver(receiver, filter);

		bluetoothDeviceConstant = getIntent().getExtras().getInt(BLUETOOTH_DEVICE);
		Log.d("DeviceListActivity", "bluetoothDeviceConstant = " + bluetoothDeviceConstant);
		activateBluetooth();
		listAndSelectDevices();
	}

	private void listAndSelectDevices() {
		Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();

		BluetoothDevice bluetoothDevice = null;
		int possibleConnections = 0;
		if (pairedDevices.size() > 0) {
			findViewById(R.id.title_paired_devices).setVisibility(View.VISIBLE);
			for (BluetoothDevice device : pairedDevices) {
				for (String item : autoConnectIDs) {
					if (device.getAddress().startsWith(item)) {
						bluetoothDevice = device;
						possibleConnections++;
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
			btAdapter.cancelDiscovery();
			Intent intent = new Intent();
			Bundle data = new Bundle();
			data.putInt(BLUETOOTH_DEVICE, bluetoothDeviceConstant);
			data.putString(DEVICE_NAME_AND_ADDRESS, bluetoothDevice.getName() + "-" + bluetoothDevice.getAddress());
			data.putString(EXTRA_DEVICE_ADDRESS, bluetoothDevice.getAddress());
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
		if (btAdapter != null) {
			btAdapter.cancelDiscovery();
		}

		this.unregisterReceiver(receiver);
		super.onDestroy();
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

	private void activateBluetooth() {
		btAdapter = BluetoothAdapter.getDefaultAdapter();
		if (btAdapter == null) {
			Bundle data = new Bundle();
			data.putInt(BLUETOOTH_DEVICE, bluetoothDeviceConstant);
			Intent intent = new Intent();
			intent.putExtras(data);
			setResult(BLUETOOTH_NOT_SUPPORTED, intent);
			finish();
		}
		if (!btAdapter.isEnabled()) {
			Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBtIntent, 0);
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.i("bt", "Bluetooth activation activity returned");

		switch (resultCode) {
			case Activity.RESULT_OK:
				listAndSelectDevices();
				break;
			case Activity.RESULT_CANCELED:
				Bundle returnData = new Bundle();
				returnData.putInt(BLUETOOTH_DEVICE, bluetoothDeviceConstant);
				Intent intent = new Intent();
				intent.putExtras(returnData);
				setResult(BLUETOOTH_ACTIVATION_CANCELED, intent);
				finish();
				break;
		}
	}
}

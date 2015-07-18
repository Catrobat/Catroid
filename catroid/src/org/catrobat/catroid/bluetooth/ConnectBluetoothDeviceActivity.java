/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2015 The Catrobat Team
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
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.catrobat.catroid.R;
import org.catrobat.catroid.bluetooth.base.BluetoothConnection;
import org.catrobat.catroid.bluetooth.base.BluetoothConnectionFactory;
import org.catrobat.catroid.bluetooth.base.BluetoothDevice;
import org.catrobat.catroid.bluetooth.base.BluetoothDeviceFactory;
import org.catrobat.catroid.bluetooth.base.BluetoothDeviceService;
import org.catrobat.catroid.common.CatroidService;
import org.catrobat.catroid.common.ServiceProvider;
import org.catrobat.catroid.utils.ToastUtil;

import java.util.Set;

public class ConnectBluetoothDeviceActivity extends Activity {

	public static final String TAG = ConnectBluetoothDeviceActivity.class.getSimpleName();

	public static final String DEVICE_TO_CONNECT = "org.catrobat.catroid.bluetooth.DEVICE";

	private static final int DEVICE_MAC_ADDRESS_LENGTH = 18;

	private static BluetoothDeviceFactory btDeviceFactory;
	private static BluetoothConnectionFactory btConnectionFactory;

	protected BluetoothDevice btDevice;

	private BluetoothManager btManager;

	private ArrayAdapter<String> pairedDevicesArrayAdapter;
	private ArrayAdapter<String> newDevicesArrayAdapter;

	private static BluetoothDeviceFactory getDeviceFactory() {
		if (btDeviceFactory == null) {
			btDeviceFactory = new BluetoothDeviceFactoryImpl();
		}

		return btDeviceFactory;
	}

	private static BluetoothConnectionFactory getConnectionFactory() {
		if (btConnectionFactory == null) {
			btConnectionFactory = new BluetoothConnectionFactoryImpl();
		}

		return btConnectionFactory;
	}

	// hooks for testing
	public static void setDeviceFactory(BluetoothDeviceFactory deviceFactory) {
		btDeviceFactory = deviceFactory;
	}

	public static void setConnectionFactory(BluetoothConnectionFactory connectionFactory) {
		btConnectionFactory = connectionFactory;
	}

	public void addPairedDevice(String pairedDevice) {
		if (pairedDevicesArrayAdapter != null) {
			pairedDevicesArrayAdapter.add(pairedDevice);
		}
	}
	// end hooks for testing

	private OnItemClickListener deviceClickListener = new OnItemClickListener() {

		private String getSelectedBluetoothAddress(View view) {
			String info = ((TextView) view).getText().toString();
			if (info.lastIndexOf('-') != info.length() - DEVICE_MAC_ADDRESS_LENGTH) {
				return null;
			}

			return info.substring(info.lastIndexOf('-') + 1);
		}

		@Override
		public void onItemClick(AdapterView<?> av, View view, int position, long id) {

			String address = getSelectedBluetoothAddress(view);
			if (address == null) {
				return;
			}
			connectDevice(address);
		}
	};

	private final BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();

			if (android.bluetooth.BluetoothDevice.ACTION_FOUND.equals(action)) {
				android.bluetooth.BluetoothDevice device = intent.getParcelableExtra(android.bluetooth.BluetoothDevice.EXTRA_DEVICE);
				if ((device.getBondState() != android.bluetooth.BluetoothDevice.BOND_BONDED)) {
					newDevicesArrayAdapter.add(device.getName() + "-" + device.getAddress());
				}
			} else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
				setProgressBarIndeterminateVisibility(false);

				findViewById(R.id.device_list_progress_bar).setVisibility(View.GONE);

				setTitle(getString(R.string.select_device) + " " + btDevice.getName());
				if (newDevicesArrayAdapter.isEmpty()) {
					String noDevices = getResources().getString(R.string.none_found);
					newDevicesArrayAdapter.add(noDevices);
				}
			}
		}
	};

	private class ConnectDeviceTask extends AsyncTask<String, Void, BluetoothConnection.State> {

		BluetoothConnection btConnection;
		private ProgressDialog connectingProgressDialog;

		@Override
		protected void onPreExecute() {
			setVisible(false);
			connectingProgressDialog = ProgressDialog.show(ConnectBluetoothDeviceActivity.this, "",
					getResources().getString(R.string.connecting_please_wait), true);
		}

		@Override
		protected BluetoothConnection.State doInBackground(String... addresses) {
			if (btDevice == null) {
				Log.e(TAG, "Try connect to device which is not implemented!");
				return BluetoothConnection.State.NOT_CONNECTED;
			}
			btConnection = getConnectionFactory().createBTConnectionForDevice(btDevice.getDeviceType(), addresses[0],
					btDevice.getBluetoothDeviceUUID(), ConnectBluetoothDeviceActivity.this.getApplicationContext());

			return btConnection.connect();
		}

		@Override
		protected void onPostExecute(BluetoothConnection.State connectionState) {

			connectingProgressDialog.dismiss();

			int result = RESULT_CANCELED;

			if (connectionState == BluetoothConnection.State.CONNECTED) {
				btDevice.setConnection(btConnection);
				result = RESULT_OK;
				BluetoothDeviceService btDeviceService = ServiceProvider.getService(CatroidService.BLUETOOTH_DEVICE_SERVICE);
				btDeviceService.deviceConnected(btDevice);
			} else {
				ToastUtil.showError(ConnectBluetoothDeviceActivity.this, R.string.bt_connection_failed);
			}

			setResult(result);
			finish();
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		createAndSetDeviceService();

		setContentView(R.layout.device_list);
		setTitle(getString(R.string.select_device) + " " + btDevice.getName());

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

		IntentFilter filter = new IntentFilter(android.bluetooth.BluetoothDevice.ACTION_FOUND);
		this.registerReceiver(receiver, filter);

		filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		this.registerReceiver(receiver, filter);

		int bluetoothState = activateBluetooth();
		if (bluetoothState == BluetoothManager.BLUETOOTH_ALREADY_ON) {
			listAndSelectDevices();
		}
	}

	private void listAndSelectDevices() {

		Set<android.bluetooth.BluetoothDevice> pairedDevices = btManager.getBluetoothAdapter().getBondedDevices();

		if (pairedDevices.size() > 0) {
			findViewById(R.id.title_paired_devices).setVisibility(View.VISIBLE);
			for (android.bluetooth.BluetoothDevice device : pairedDevices) {
				pairedDevicesArrayAdapter.add(device.getName() + "-" + device.getAddress());
			}
		}

		if (pairedDevices.size() == 0) {
			String noDevices = getResources().getText(R.string.none_paired).toString();
			pairedDevicesArrayAdapter.add(noDevices);
		}

		this.setVisible(true);
	}

	protected void createAndSetDeviceService() {
		Class<BluetoothDevice> serviceType = (Class<BluetoothDevice>) getIntent().getSerializableExtra(DEVICE_TO_CONNECT);

		btDevice = getDeviceFactory().createDevice(serviceType, this.getApplicationContext());
	}

	private void connectDevice(String address) {
		btManager.getBluetoothAdapter().cancelDiscovery();
		new ConnectDeviceTask().execute(address);
	}

	@Override
	protected void onDestroy() {
		if (btManager != null && btManager.getBluetoothAdapter() != null) {
			btManager.getBluetoothAdapter().cancelDiscovery();
		}

		this.unregisterReceiver(receiver);
		super.onDestroy();
	}

	private void doDiscovery() {

		setProgressBarIndeterminateVisibility(true);

		findViewById(R.id.title_new_devices).setVisibility(View.VISIBLE);

		findViewById(R.id.device_list_progress_bar).setVisibility(View.VISIBLE);

		if (btManager.getBluetoothAdapter().isDiscovering()) {
			btManager.getBluetoothAdapter().cancelDiscovery();
		}

		btManager.getBluetoothAdapter().startDiscovery();
	}

	private int activateBluetooth() {

		btManager = new BluetoothManager(this);

		int bluetoothState = btManager.activateBluetooth();
		if (bluetoothState == BluetoothManager.BLUETOOTH_NOT_SUPPORTED) {
			ToastUtil.showError(this, R.string.notification_blueth_err);
			setResult(Activity.RESULT_CANCELED);
			finish();
		}

		return bluetoothState;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.i("bt", "Bluetooth activation activity returned");

		switch (resultCode) {
			case Activity.RESULT_OK:
				listAndSelectDevices();
				break;
			case Activity.RESULT_CANCELED:
				ToastUtil.showError(this, R.string.notification_blueth_err);
				setResult(Activity.RESULT_CANCELED);
				finish();
				break;
		}
	}
}

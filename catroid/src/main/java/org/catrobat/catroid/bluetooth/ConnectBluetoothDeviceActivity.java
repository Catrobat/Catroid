/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2022 The Catrobat Team
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

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.catrobat.catroid.R;
import org.catrobat.catroid.bluetooth.base.BluetoothConnection;
import org.catrobat.catroid.bluetooth.base.BluetoothConnectionFactory;
import org.catrobat.catroid.bluetooth.base.BluetoothDevice;
import org.catrobat.catroid.bluetooth.base.BluetoothDeviceFactory;
import org.catrobat.catroid.bluetooth.base.BluetoothDeviceService;
import org.catrobat.catroid.common.CatroidService;
import org.catrobat.catroid.common.ServiceProvider;
import org.catrobat.catroid.devices.mindstorms.MindstormsException;
import org.catrobat.catroid.devices.multiplayer.Multiplayer;
import org.catrobat.catroid.utils.ToastUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import static android.bluetooth.BluetoothDevice.DEVICE_TYPE_CLASSIC;
import static android.bluetooth.BluetoothDevice.DEVICE_TYPE_DUAL;
import static android.bluetooth.BluetoothDevice.DEVICE_TYPE_LE;

import static org.catrobat.catroid.common.SharedPreferenceKeys.SHOW_MULTIPLAYER_BLUETOOTH_DIALOG_KEY;

public class ConnectBluetoothDeviceActivity extends AppCompatActivity {

	public static final String TAG = ConnectBluetoothDeviceActivity.class.getSimpleName();

	public static final String DEVICE_TO_CONNECT = "org.catrobat.catroid.bluetooth.DEVICE";

	private static BluetoothDeviceFactory btDeviceFactory;
	private static BluetoothConnectionFactory btConnectionFactory;

	protected BluetoothDevice btDevice;

	private BluetoothManager btManager;

	private ArrayAdapter<Pair<String, String>> pairedDevicesArrayAdapter;
	private ArrayAdapter<Pair<Pair<String, String>, Integer>> newDevicesArrayAdapter;

	private Handler handler;

	FloatingActionButton scanButton;
	Boolean isDiscovering = false;

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

	private OnItemClickListener deviceClickListener = new OnItemClickListener() {

		private String getSelectedBluetoothAddress(View view) {
			TextView textViewAddresses = view.findViewById(R.id.bluetooth_address);
			return textViewAddresses.getText().toString();
		}

		@Override
		public void onItemClick(AdapterView<?> av, View view, int position, long id) {
			String address = getSelectedBluetoothAddress(view);
			Pair pair = null;

			if (!newDevicesArrayAdapter.isEmpty()) {
				pair = newDevicesArrayAdapter.getItem(position);
			}

			if (pair == null || pair.second.equals(DEVICE_TYPE_CLASSIC)) {
				connectDevice(address);
			}
		}
	};

	private final BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();

			if (android.bluetooth.BluetoothDevice.ACTION_FOUND.equals(action)) {
				android.bluetooth.BluetoothDevice device = intent.getParcelableExtra(android.bluetooth.BluetoothDevice.EXTRA_DEVICE);
				if ((device.getBondState() != android.bluetooth.BluetoothDevice.BOND_BONDED)) {
					if (device.getType() == DEVICE_TYPE_CLASSIC || device.getType() == DEVICE_TYPE_DUAL) {
						Pair<Pair<String, String>, Integer> listElement = new Pair<>(new Pair<>(device.getName(), device.getAddress()), DEVICE_TYPE_CLASSIC);
						if (newDevicesArrayAdapter.getPosition(listElement) < 0) {
							newDevicesArrayAdapter.add(listElement);
						}
					}
					if (device.getType() == DEVICE_TYPE_LE || device.getType() == DEVICE_TYPE_DUAL) {
						String deviceInfoBLE = "BLE" + (device.getName() != null ? " - " + device.getName() : "");
						Pair<Pair<String, String>, Integer> listElement = new Pair<>(new Pair<>(deviceInfoBLE, device.getAddress()), DEVICE_TYPE_LE);
						if (newDevicesArrayAdapter.getPosition(listElement) < 0) {
							newDevicesArrayAdapter.add(listElement);
						}
					}
				}
			} else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
				isDiscovering = true;
				handleScanButtonClicked();
				setProgressBarIndeterminateVisibility(false);

				findViewById(R.id.device_list_progress_bar).setVisibility(View.GONE);

				if (!btManager.getBluetoothAdapter().isEnabled()) {
					initBluetooth();
					newDevicesArrayAdapter.clear();
				} else {
					if (newDevicesArrayAdapter.isEmpty()) {
						String noDevices = getResources().getString(R.string.none_found);
						Pair<Pair<String, String>, Integer> listElement = new Pair<>(new Pair<>(noDevices, ""), 0);
						newDevicesArrayAdapter.add(listElement);
					}
				}
			}
			setDynamicListViewHeight(findViewById(R.id.new_devices));
		}
	};

	private class ConnectDeviceTask extends AsyncTask<String, Void, BluetoothConnection.State> {

		BluetoothConnection btConnection;
		private ProgressDialog connectingProgressDialog;

		@Override
		protected void onPreExecute() {
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

				try {
					setDeviceConnected(btConnection.getInputStream(), btConnection.getOutputStream());
				} catch (IOException exception) {
					Log.e(TAG, exception.getMessage(), exception);
				}
			} else {
				ToastUtil.showError(ConnectBluetoothDeviceActivity.this, R.string.bt_connection_failed);
			}

			setResult(result);
			finish();
		}
	}

	private void setDeviceConnected(InputStream inputStream, OutputStream outputStream) {
		BluetoothDeviceService btDeviceService = ServiceProvider.getService(CatroidService.BLUETOOTH_DEVICE_SERVICE);
		try {
			if (btDevice instanceof Multiplayer) {
				((Multiplayer) btDevice).setStreams(inputStream, outputStream);
			}

			if (btDeviceService != null) {
				btDeviceService.deviceConnected(btDevice);
			}
		} catch (MindstormsException exception) {
			ToastUtil.showError(this, R.string.bt_connection_failed);
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		createAndSetDeviceService();

		setContentView(R.layout.device_list);

		setSupportActionBar(findViewById(R.id.toolbar));
		getSupportActionBar().setTitle(R.string.bluetooth_connection_title);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		setResult(AppCompatActivity.RESULT_CANCELED);

		scanButton = findViewById(R.id.bluetooth_scan);
		scanButton.setOnClickListener(view -> handleScanButtonClicked());

		handler = new Handler();

		pairedDevicesArrayAdapter = new ArrayAdapter<Pair<String, String>>(this, R.layout.bluetooth_connection_screen) {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				if (convertView == null) {
					convertView = LayoutInflater.from(parent.getContext())
							.inflate(R.layout.bluetooth_connection_screen, parent, false);
				}

				TextView textViewDevices = convertView.findViewById(R.id.bluetooth_device);
				TextView textViewAddresses = convertView.findViewById(R.id.bluetooth_address);

				textViewDevices.setText(getItem(position).first);
				textViewAddresses.setText(getItem(position).second);

				return convertView;
			}
		};

		newDevicesArrayAdapter = new ArrayAdapter<Pair<Pair<String, String>, Integer>>(this, R.layout.bluetooth_connection_screen) {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				if (convertView == null) {
					convertView = LayoutInflater.from(parent.getContext())
							.inflate(R.layout.bluetooth_connection_screen, parent, false);
				}

				TextView textViewDevices = convertView.findViewById(R.id.bluetooth_device);
				TextView textViewAddresses = convertView.findViewById(R.id.bluetooth_address);

				textViewDevices.setText(getItem(position).first.first);
				textViewAddresses.setText(getItem(position).first.second);

				return convertView;
			}
		};

		ListView pairedListView = findViewById(R.id.paired_devices);
		pairedListView.setAdapter(pairedDevicesArrayAdapter);
		pairedListView.setOnItemClickListener(deviceClickListener);

		ListView newDevicesListView = findViewById(R.id.new_devices);
		newDevicesListView.setAdapter(newDevicesArrayAdapter);
		newDevicesListView.setOnItemClickListener(deviceClickListener);

		IntentFilter filter = new IntentFilter(android.bluetooth.BluetoothDevice.ACTION_FOUND);
		this.registerReceiver(receiver, filter);

		filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		this.registerReceiver(receiver, filter);

		boolean showMultiplayerInformationDialog = PreferenceManager.getDefaultSharedPreferences(this)
				.getBoolean(SHOW_MULTIPLAYER_BLUETOOTH_DIALOG_KEY, true);

		if (btDevice instanceof Multiplayer && showMultiplayerInformationDialog) {
			showMultiplayerInformationDialog();
		} else {
			initBluetooth();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_bluetooth_connection, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.findItem(R.id.skip_bluetooth).setVisible(true);
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				onBackPressed();
				break;
			case R.id.skip_bluetooth:
				setResult(AppCompatActivity.RESULT_OK);
				finish();
				break;
		}

		return super.onOptionsItemSelected(item);
	}

	private void showMultiplayerInformationDialog() {
		View view = View.inflate(this, R.layout.dialog_multiplayer_bluetooth, null);

		new AlertDialog.Builder(this)
				.setCancelable(false)
				.setNegativeButton(getString(R.string.got_it), (dialog, which) -> {
					PreferenceManager.getDefaultSharedPreferences(this)
							.edit()
							.putBoolean(SHOW_MULTIPLAYER_BLUETOOTH_DIALOG_KEY, false)
							.apply();
					initBluetooth();
				})
				.setView(view)
				.show();
	}

	protected void initBluetooth() {
		int bluetoothState = activateBluetooth();
		if (bluetoothState == BluetoothManager.BLUETOOTH_ALREADY_ON) {
			listAndSelectDevices();
			startAcceptThread();
			activateBluetoothVisibility();
		}
	}

	private void handleScanButtonClicked() {
		if (isDiscovering) {
			scanButton.setImageResource(R.drawable.ic_search);
			isDiscovering = false;
			cancelDiscovery();
		} else {
			scanButton.setImageResource(R.drawable.ic_close);
			isDiscovering = true;
			doDiscovery();
		}
	}

	private void activateBluetoothVisibility() {
		if (btDevice instanceof Multiplayer) {
			Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
			startActivity(intent);
		}
	}

	private void startAcceptThread() {
		if (btDevice instanceof Multiplayer) {
			Thread acceptThread = new AcceptThread();
			acceptThread.start();
		}
	}

	private void listAndSelectDevices() {
		Set<android.bluetooth.BluetoothDevice> pairedDevices = btManager.getBluetoothAdapter().getBondedDevices();

		if (pairedDevices.size() > 0) {
			findViewById(R.id.bluetooth_paired_section).setVisibility(View.VISIBLE);
			for (android.bluetooth.BluetoothDevice device : pairedDevices) {
				Pair<String, String> listElement = new Pair<>(device.getName(), device.getAddress());
				pairedDevicesArrayAdapter.add(listElement);
			}

			setDynamicListViewHeight(findViewById(R.id.paired_devices));
		}
	}

	protected void createAndSetDeviceService() {
		Class<BluetoothDevice> serviceType = (Class<BluetoothDevice>) getIntent().getSerializableExtra(DEVICE_TO_CONNECT);

		btDevice = getDeviceFactory().createDevice(serviceType, this.getApplicationContext());
	}

	private void connectDevice(String address) {
		cancelDiscovery();
		new ConnectDeviceTask().execute(address);
	}

	@Override
	protected void onDestroy() {
		if (btManager != null && btManager.getBluetoothAdapter() != null) {
			cancelDiscovery();
		}

		this.unregisterReceiver(receiver);
		super.onDestroy();
	}

	protected void doDiscovery() {
		newDevicesArrayAdapter.clear();
		setDynamicListViewHeight(findViewById(R.id.new_devices));

		setProgressBarIndeterminateVisibility(true);

		findViewById(R.id.device_list_progress_bar).setVisibility(View.VISIBLE);

		cancelDiscovery();

		btManager.getBluetoothAdapter().startDiscovery();
	}

	private void cancelDiscovery() {
		if (btManager.getBluetoothAdapter().isDiscovering()) {
			btManager.getBluetoothAdapter().cancelDiscovery();
		}
	}

	private int activateBluetooth() {
		btManager = new BluetoothManager(this);

		int bluetoothState = btManager.activateBluetooth();
		if (bluetoothState == BluetoothManager.BLUETOOTH_NOT_SUPPORTED) {
			ToastUtil.showError(this, R.string.notification_blueth_err);
			setResult(AppCompatActivity.RESULT_CANCELED);
			finish();
		}

		return bluetoothState;
	}

	public static void setDynamicListViewHeight(ListView listView) {
		ListAdapter listViewAdapter = listView.getAdapter();
		if (listViewAdapter == null) {
			return;
		}

		int height = 0;
		int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
		for (int element = 0; element < listViewAdapter.getCount(); element++) {
			View listItem = listViewAdapter.getView(element, null, listView);
			listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
			height += listItem.getMeasuredHeight();
		}

		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = height + (listView.getDividerHeight() * (listViewAdapter.getCount() - 1));
		listView.setLayoutParams(params);
		listView.requestLayout();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.i(TAG, "Bluetooth activation activity returned");

		switch (resultCode) {
			case AppCompatActivity.RESULT_OK:
				listAndSelectDevices();
				startAcceptThread();
				activateBluetoothVisibility();
				break;
			case AppCompatActivity.RESULT_CANCELED:
				ToastUtil.showError(this, R.string.notification_blueth_err);
				setResult(AppCompatActivity.RESULT_CANCELED);
				finish();
				break;
		}
	}

	public class AcceptThread extends Thread {
		private BluetoothServerSocket serverSocket;

		AcceptThread() {
			try {
				serverSocket = BluetoothAdapter.getDefaultAdapter()
						.listenUsingRfcommWithServiceRecord(getString(R.string.app_name), btDevice.getBluetoothDeviceUUID());
			} catch (IOException exception) {
				Log.e(TAG, "Creating ServerSocket failed!", exception);
			}

			((Multiplayer) btDevice).setAcceptThread(this);
		}

		public void run() {
			BluetoothSocket socket = null;

			do {
				if (isInterrupted()) {
					return;
				}

				try {
					socket = serverSocket.accept();
					if (serverSocket != null) {
						serverSocket.close();
					}
				} catch (IOException exception) {
					Log.d(TAG, exception.getMessage(), exception);
				}
			} while (socket == null);

			handler.post(() -> {
				setResult(RESULT_OK);
				finish();
			});

			try {
				((Multiplayer) btDevice).setBluetoothSocket(socket);
				setDeviceConnected(socket.getInputStream(), socket.getOutputStream());
			} catch (IOException exception) {
				Log.e(TAG, exception.getMessage(), exception);
			}
		}

		public void cancel() {
			try {
				serverSocket.close();
				serverSocket = null;
			} catch (IOException exception) {
				Log.d(TAG, exception.getMessage(), exception);
			}
		}
	}
}

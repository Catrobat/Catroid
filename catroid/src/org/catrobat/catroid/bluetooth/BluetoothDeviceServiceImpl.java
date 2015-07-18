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
import android.content.Context;
import android.content.Intent;

import org.catrobat.catroid.bluetooth.base.BluetoothDevice;
import org.catrobat.catroid.bluetooth.base.BluetoothDeviceService;

import java.util.HashMap;
import java.util.Map;

public class BluetoothDeviceServiceImpl implements BluetoothDeviceService {

	private Map<Class<? extends BluetoothDevice>, BluetoothDevice> connectedDevices =
			new HashMap<Class<? extends BluetoothDevice>, BluetoothDevice>();

	@Override
	public ConnectDeviceResult connectDevice(Class<? extends BluetoothDevice> deviceToConnect,
			Activity activity, int requestCode) {

		if (isDeviceConnectedAndAlive(deviceToConnect)) {
			return ConnectDeviceResult.ALREADY_CONNECTED;
		}

		Intent intent = createStartIntent(deviceToConnect, activity);
		activity.startActivityForResult(intent, requestCode);

		return ConnectDeviceResult.CONNECTION_REQUESTED;
	}

	@Override
	public ConnectDeviceResult connectDevice(Class<? extends BluetoothDevice> deviceToConnect,
			Context context) {

		if (isDeviceConnectedAndAlive(deviceToConnect)) {
			return ConnectDeviceResult.ALREADY_CONNECTED;
		}

		Intent intent = createStartIntent(deviceToConnect, context);
		context.startActivity(intent);

		return ConnectDeviceResult.CONNECTION_REQUESTED;
	}

	private synchronized boolean isDeviceConnectedAndAlive(Class<? extends BluetoothDevice> deviceToConnect) {
		BluetoothDevice device = connectedDevices.get(deviceToConnect);

		if (device != null) {
			if (device.isAlive()) {
				device.start();
				return true;
			}

			device.disconnect();
			connectedDevices.remove(device);
		}
		return false;
	}

	@Override
	public synchronized void deviceConnected(BluetoothDevice device) {
		connectedDevices.put(device.getDeviceType(), device);
		device.start();
	}

	@Override
	public synchronized void disconnectDevices() {
		for (BluetoothDevice device : connectedDevices.values()) {
			device.disconnect();
		}

		connectedDevices.clear();
	}

	public synchronized <T extends BluetoothDevice> T getDevice(Class<T> btDevice) {
		BluetoothDevice device = connectedDevices.get(btDevice);
		if (device != null) {
			return (T) device;
		}
		return null;
	}

	protected Intent createStartIntent(Class<? extends BluetoothDevice> deviceToConnect,
			Context context) {
		Intent intent = new Intent(context, ConnectBluetoothDeviceActivity.class);
		intent.putExtra(ConnectBluetoothDeviceActivity.DEVICE_TO_CONNECT, deviceToConnect);
		return intent;
	}

	@Override
	public synchronized void initialise() {
		for (BluetoothDevice device : connectedDevices.values()) {
			device.initialise();
		}
	}

	@Override
	public synchronized void start() {
		for (BluetoothDevice device : connectedDevices.values()) {
			device.start();
		}
	}

	@Override
	public synchronized void pause() {
		for (BluetoothDevice device : connectedDevices.values()) {
			device.pause();
		}
	}

	@Override
	public synchronized void destroy() {
		for (BluetoothDevice device : connectedDevices.values()) {
			device.destroy();
		}
	}
}

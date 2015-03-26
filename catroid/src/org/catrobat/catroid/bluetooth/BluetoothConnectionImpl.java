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

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import org.catrobat.catroid.bluetooth.base.BluetoothConnection;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;

public class BluetoothConnectionImpl implements BluetoothConnection {

	private static final String REFLECTION_METHOD_NAME = "createRfcommSocket";
	private static final String TAG = BluetoothConnectionImpl.class.getSimpleName();

	private final String macAddress;
	private final UUID uuid;
	private BluetoothAdapter bluetoothAdapter;
	private BluetoothDevice bluetoothDevice;
	private BluetoothSocket bluetoothSocket;
	private State state;

	public BluetoothConnectionImpl(String macAddress, UUID uuid) {
		this.macAddress = macAddress;
		this.uuid = uuid;
		state = State.NOT_CONNECTED;
	}

	public State connect() {
		bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (bluetoothAdapter == null) {
			return (state = State.ERROR_BLUETOOTH_NOT_SUPPORTED);
		}
		if (bluetoothAdapter.getState() != BluetoothAdapter.STATE_ON) {
			return (state = State.ERROR_ADAPTER);
		}

		Log.d(TAG, "Got Adapter and Adapter was on");

		bluetoothDevice = bluetoothAdapter.getRemoteDevice(macAddress);
		if (bluetoothDevice == null) {
			return (state = State.ERROR_DEVICE);
		}

		Log.d(TAG, "Got remote device");

		try {
			bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(uuid);
		} catch (IOException ioException) {
			if (bluetoothDevice.getBondState() == BluetoothDevice.BOND_NONE) {
				return (state = State.ERROR_NOT_BONDED);
			} else if (bluetoothDevice.getBondState() == BluetoothDevice.BOND_BONDING) {
				return (state = State.ERROR_STILL_BONDING);
			} else {
				return (state = State.ERROR_SOCKET);
			}
		}

		Log.d(TAG, "Socket was created");

		switch (connectSocket(bluetoothSocket)) {
			case CONNECTED:
				Log.d(TAG, "connected");
				return State.CONNECTED;
			case ERROR_SOCKET:
				Log.d(TAG, "error connecting");
				return State.ERROR_SOCKET;
			default:
				Log.wtf(TAG, "This should never happen!");
				return State.NOT_CONNECTED;
		}
	}

	public State connectSocket(BluetoothSocket socket) {
		if (socket == null) {
			return (state = State.NOT_CONNECTED);
		}

		Log.d(TAG, "Connecting");

		bluetoothSocket = socket;
		try {
			this.bluetoothSocket.connect();
		} catch (IOException ioException) {
			try {
				Log.e(TAG, Log.getStackTraceString(ioException));
				Log.d(TAG, "Try connecting again");
				// try another method for connection, this should work on the HTC desire, credits to Michael Biermann
				Method mMethod = bluetoothDevice.getClass()
						.getMethod(REFLECTION_METHOD_NAME, new Class[] { int.class });
				this.bluetoothSocket = (BluetoothSocket) mMethod.invoke(bluetoothDevice, Integer.valueOf(1));
				this.bluetoothSocket.connect();
				return (state = State.CONNECTED);
			} catch (NoSuchMethodException noSuchMethodException) {
				Log.e(TAG, Log.getStackTraceString(noSuchMethodException));
			} catch (InvocationTargetException invocationTargetException) {
				Log.e(TAG, Log.getStackTraceString(invocationTargetException));
			} catch (IllegalAccessException illegalAccessException) {
				Log.e(TAG, Log.getStackTraceString(illegalAccessException));
			} catch (IOException secondIOException) {
				Log.e(TAG, Log.getStackTraceString(secondIOException));
			}
			return (state = State.ERROR_SOCKET);
		}
		return (state = State.CONNECTED);
	}

	public void disconnect() {
		Log.d(TAG, "disconnecting");
		try {
			state = State.NOT_CONNECTED;
			if (bluetoothSocket != null) {
				bluetoothSocket.close();
				bluetoothSocket = null;
			}
		} catch (IOException ioException) {
			Log.e(TAG, Log.getStackTraceString(ioException));
		}
	}

	public BluetoothDevice getBluetoothDevice() {
		return bluetoothDevice;
	}

	public InputStream getInputStream() throws IOException {
		return bluetoothSocket.getInputStream();
	}

	public OutputStream getOutputStream() throws IOException {
		return bluetoothSocket.getOutputStream();
	}

	public State getState() {
		return state;
	}
}

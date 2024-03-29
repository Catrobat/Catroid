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

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;

public class BluetoothManager {

	public static final int REQUEST_ENABLE_BT = 2000;
	public static final int BLUETOOTH_NOT_SUPPORTED = -1;
	public static final int BLUETOOTH_ALREADY_ON = 1;
	public static final int BLUETOOTH_ACTIVATING = 0;
	private BluetoothAdapter bluetoothAdapter;

	private final Activity activity;

	public BluetoothManager(Activity activity) {
		this.activity = activity;
	}

	public int activateBluetooth() {
		if (bluetoothAdapter == null) {
			bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		}

		if (bluetoothAdapter == null) {
			return BLUETOOTH_NOT_SUPPORTED;
		}
		if (!bluetoothAdapter.isEnabled()) {
			Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			activity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
			return BLUETOOTH_ACTIVATING;
		} else {
			return BLUETOOTH_ALREADY_ON;
		}
	}

	public BluetoothAdapter getBluetoothAdapter() {
		return bluetoothAdapter;
	}
}

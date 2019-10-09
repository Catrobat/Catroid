/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2018 The Catrobat Team
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
package org.catrobat.catroid.common.bluetooth;

import android.content.Context;

import junit.framework.Assert;

import org.catrobat.catroid.bluetooth.ConnectBluetoothDeviceActivity;
import org.catrobat.catroid.bluetooth.base.BluetoothConnection;
import org.catrobat.catroid.bluetooth.base.BluetoothConnectionFactory;
import org.catrobat.catroid.bluetooth.base.BluetoothDevice;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.UUID;

public final class BluetoothTestUtils {

	private BluetoothTestUtils() {
	}

	static byte[] intToByteArray(int i) {
		return ByteBuffer.allocate(4).putInt(i).array();
	}

	static byte[] getSubArray(byte[] buffer, int offset) {
		if (buffer == null) {
			return null;
		}

		return Arrays.copyOfRange(buffer, offset, buffer.length);
	}

	static byte[] getSubArray(byte[] buffer, int offset, int count) {
		if (buffer == null) {
			return null;
		}

		Assert.assertTrue("count can't be negative", count >= 0);
		Assert.assertTrue("wrong offset or count", buffer.length - offset >= count);

		return Arrays.copyOfRange(buffer, offset, offset + count);
	}

	static void hookInConnection(final BluetoothConnection connectionProxy) {
		ConnectBluetoothDeviceActivity.setConnectionFactory(new BluetoothConnectionFactory() {
			@Override
			public <T extends BluetoothDevice> BluetoothConnection createBTConnectionForDevice(
					Class<T> bluetoothDeviceType, String address, UUID deviceUUID, Context applicationContext) {
				return connectionProxy;
			}
		});
	}

	static void hookInConnectionFactoryWithBluetoothConnectionProxy(final BluetoothLogger logger) {
		ConnectBluetoothDeviceActivity.setConnectionFactory(new BluetoothConnectionFactory() {
			@Override
			public <T extends BluetoothDevice> BluetoothConnection createBTConnectionForDevice(Class<T> device, String address, UUID deviceUUID, Context applicationContext) {
				return new BluetoothConnectionProxy(address, deviceUUID, logger);
			}
		});
	}

	static void resetConnectionHooks() {
		ConnectBluetoothDeviceActivity.setConnectionFactory(null);
		ConnectBluetoothDeviceActivity.setDeviceFactory(null);
	}
}

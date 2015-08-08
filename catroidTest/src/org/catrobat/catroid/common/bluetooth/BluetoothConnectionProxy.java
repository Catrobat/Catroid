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
package org.catrobat.catroid.common.bluetooth;

import android.bluetooth.BluetoothSocket;

import org.catrobat.catroid.bluetooth.BluetoothConnectionImpl;
import org.catrobat.catroid.bluetooth.base.BluetoothConnection;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

class BluetoothConnectionProxy implements BluetoothConnection {

	private final BluetoothLogger logger;

	BluetoothConnection btConnection;

	ObservedInputStream observedInputStream;
	ObservedOutputStream observedOutputStream;

	BluetoothConnectionProxy(String macAddress, UUID uuid, final BluetoothLogger logger) {

		btConnection = new BluetoothConnectionImpl(macAddress, uuid);
		this.logger = logger;

		logger.loggerAttached(this);
	}

	@Override
	public State connect() {
		return btConnection.connect();
	}

	@Override
	public State connectSocket(BluetoothSocket socket) {
		return btConnection.connectSocket(socket);
	}

	@Override
	public void disconnect() {
		btConnection.disconnect();
	}

	@Override
	public InputStream getInputStream() throws IOException {
		if (observedInputStream == null) {
			observedInputStream = new ObservedInputStream(btConnection.getInputStream(), logger);
		}

		return observedInputStream;
	}

	@Override
	public OutputStream getOutputStream() throws IOException {

		if (observedOutputStream == null) {
			observedOutputStream = new ObservedOutputStream(btConnection.getOutputStream(), logger);
		}

		return observedOutputStream;
	}

	@Override
	public State getState() {
		return btConnection.getState();
	}
}

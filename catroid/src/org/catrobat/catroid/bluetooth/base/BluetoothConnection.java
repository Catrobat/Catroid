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
package org.catrobat.catroid.bluetooth.base;

import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface BluetoothConnection {

	static enum State {
		CONNECTED, NOT_CONNECTED, ERROR_BLUETOOTH_NOT_SUPPORTED, ERROR_BLUETOOTH_NOT_ON,
		ERROR_ADAPTER, ERROR_DEVICE, ERROR_SOCKET, ERROR_STILL_BONDING, ERROR_NOT_BONDED, ERROR_CLOSING
	}

	State connect();

	State connectSocket(BluetoothSocket socket);

	void disconnect();

	InputStream getInputStream() throws IOException;

	OutputStream getOutputStream() throws IOException;

	State getState();
}

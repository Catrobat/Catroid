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

package org.catrobat.catroid.devices.arduino;



import org.catrobat.catroid.bluetooth.base.BluetoothConnection;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStream;

public class ArduinoConnectionImpl implements ArduinoConnection {

	private BluetoothConnection bluetoothConnection;
	private OutputStream arduinoOutputStream = null;
	private DataInputStream arduinoInputStream = null;

	private boolean isConnected = false;

	public ArduinoConnectionImpl(BluetoothConnection btConnection) {
		this.bluetoothConnection = btConnection;
	}

	@Override
	public void init() {

		try {
			arduinoInputStream = new DataInputStream(bluetoothConnection.getInputStream());
			arduinoOutputStream = bluetoothConnection.getOutputStream();
			isConnected = true;
		} catch (IOException e) {
			isConnected = false;
			//throw new ArduinoException(e, "Cannot establish BtConnection");
		}
	}

	@Override
	public boolean isConnected() {
		return isConnected;
	}


	@Override
	public void disconnect() {

		isConnected = false;

		bluetoothConnection.disconnect();

		arduinoInputStream = null;
		arduinoOutputStream = null;

	}

	@Override
	public synchronized byte[] sendAndReceive(byte[] message) {
		send(message);
		return receive();
	}

	@Override
	public synchronized void send(byte[] message) {
		try {
			arduinoOutputStream.write(message, 0, message.length);
			arduinoOutputStream.flush();

		} catch (IOException e) {
			throw new ArduinoException(e, "Error on message send.");
		}
	}

	protected synchronized byte[] receive() {
		byte[] expectedLength = new byte[1];
		byte[] payload;

		try {
			while(arduinoInputStream.readByte() != 126);
			arduinoInputStream.readFully(expectedLength, 0, 1);
			payload = new byte[expectedLength[0] - 48];

			arduinoInputStream.readFully(payload, 0, expectedLength[0] - 48);
		}
		catch (IOException e) {
			throw new ArduinoException(e, "Read Error");
		}

		return payload;
	}
}

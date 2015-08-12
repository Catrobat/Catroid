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
package org.catrobat.catroid.bluetoothtestserver;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.bluetooth.RemoteDevice;
import javax.microedition.io.StreamConnection;

public abstract class BTClientHandler extends Thread {
	private static final String TAG = BTClientHandler.class.getSimpleName();

	private StreamConnection connection;

	public void setConnection(StreamConnection connection) {
		this.connection = connection;
	}

	public void run() {
		try {
			tryHandleClient();
		} catch (IOException ioException) {
			BTServer.logMessage(TAG, "BT Connection closed\n");
		}

		BTServer.writeMessage("Client disconnected!\n");
	}

	private void tryHandleClient() throws IOException {
		RemoteDevice dev = RemoteDevice.getRemoteDevice(this.connection);
		String client = dev.getFriendlyName(true);

		BTServer.writeMessage("Address: " + dev.getBluetoothAddress().replaceAll("(.{2})(?!$)", "$1:") + "\n");
		BTServer.writeMessage("Remote device name: " + client + "\n");

		DataInputStream inStream = new DataInputStream(this.connection.openInputStream());
		OutputStream outStream = this.connection.openOutputStream();

		this.handle(inStream, outStream);

		outStream.close();
		inStream.close();
		this.connection.close();
	}

	public abstract void handle(DataInputStream inStream, OutputStream outStream) throws IOException;
}

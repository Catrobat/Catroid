/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2014 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
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

import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import javax.bluetooth.LocalDevice;
import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;

public final class BTServer {
	private static final String TAG = BTServer.class.getName();

	static StreamConnection connection = null;
	static BTServer btServer;
	private static boolean gui = false;
	private static Writer out = null;
	private boolean run = true;

	// Suppress default constructor for noninstantiability
	private BTServer() {
	}

	public static void writeMessage(String arg) {
		if (gui == false) {
			try {
				out.write(arg);
				out.flush();
			} catch (Exception localException) {
				Log.e(TAG, "Exception in writeMessage!", localException);
			}
		} else {
			GUI.writeMessage(arg);
		}
	}

	public static void main(String[] args) {
		if (args.length == 0) {
			gui = true;
			GUI.startGUI();
			btServer = new BTServer();
			try {
				btServer.startServer();
			} catch (IOException ioException) {
				Log.e(TAG, "IOexception!", ioException);
			}
		} else {
			try {
				out = new OutputStreamWriter(new FileOutputStream(args[0]));

				LocalDevice localDevice = LocalDevice.getLocalDevice();
				writeMessage("Local System:\n");
				writeMessage("Address: " + localDevice.getBluetoothAddress()
						+ "\n");
				writeMessage("Name: " + localDevice.getFriendlyName() + "\n");

				btServer = new BTServer();
				btServer.startServer();
			} catch (IOException ioException) {
				Log.e(TAG, "IOException!", ioException);
			}
		}
	}

	private void startServer() throws IOException {
		UUID uuid = new UUID("1101", true);

		String connectionString = "btspp://localhost:" + uuid
				+ ";name=BT Test Server";

		StreamConnectionNotifier streamConnNotifier = (StreamConnectionNotifier) Connector
				.open(connectionString);

		writeMessage("Bluetooth Server started. Waiting for Bluetooth test clients... \n");

		while (this.run) {
			connection = streamConnNotifier.acceptAndOpen();
			BTClientHandler btc = new BTClientHandler(connection);
			btc.start();
		}

		streamConnNotifier.close();
	}
}

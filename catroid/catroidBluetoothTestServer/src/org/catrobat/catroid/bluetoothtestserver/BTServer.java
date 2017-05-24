/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2017 The Catrobat Team
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

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.LocalDevice;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;

public final class BTServer {
	private static final String TAG = BTServer.class.getSimpleName();
	private static final Logger LOGGER = Logger.getLogger(TAG);

	static BTServer btServer;
	private static boolean gui = false;
	private static Writer out = null;
	private boolean run = true;

	public static final String COMMON_BT_TEST_UUID = "fd2835bb9d8041e097215372b90342da";

	private Collection<Client> supportedClients = new ArrayList<Client>();

	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

	// Suppress default constructor for noninstantiability
	private BTServer() {
		supportedClients.add(new Client("Common BT Test", COMMON_BT_TEST_UUID));
	}

	public static void writeMessage(String arg) {
		if (!gui) {
			try {
				out.write(arg);
				out.flush();
			} catch (Exception localException) {
				LOGGER.log(Level.SEVERE, "BTTestServer: Unable to log messages. Do you have permission to log file?",
						localException);
			}
		} else {
			GUI.writeMessage(arg);
		}
	}

	private static String getTime() {
		return DATE_FORMAT.format(new Date());
	}

	public static void logMessage(String tag, String message) {
		writeMessage(getTime() + " L/" + tag + ": " + message);
	}

	public static void logMessage(String tag, String message, Exception e) {
		logMessage(tag, message + ": " + e.getMessage());
	}

	public static void main(String[] args) {

		try {

			if (args.length == 0) {
				gui = true;
				GUI.startGUI();
			} else {
				out = new OutputStreamWriter(new FileOutputStream(args[0]));
			}

			printSystemConfiguration();

			btServer = new BTServer();
			btServer.startServer();
		} catch (IOException ioException) {
			logMessage(TAG, "IOexception!", ioException);
		}
	}

	private static void printSystemConfiguration()
			throws BluetoothStateException {
		LocalDevice localDevice = LocalDevice.getLocalDevice();
		writeMessage("Local System:\n");
		writeMessage("Address: "
				+ localDevice.getBluetoothAddress().replaceAll("(.{2})(?!$)",
				"$1:") + "\n");
		writeMessage("Name: " + localDevice.getFriendlyName() + "\n");
	}

	private void startServer() throws IOException {

		writeMessage("-----------------------------------------------------\n");
		writeMessage("Bluetooth Server started on " + getTime() + "\n");
		writeMessage("Waiting for Bluetooth test clients.\n");
		writeMessage("Listening for: \n");

		for (Client client : supportedClients) {
			new InputConnectionHandler(client).start();
		}
	}

	private class InputConnectionHandler extends Thread {

		private Client client;

		InputConnectionHandler(Client client) {
			this.client = client;
		}

		@Override
		public void run() {
			try {
				tryHandleInputConnection();
			} catch (IOException e) {
				writeMessage("  -- connectionfailed! " + client.name + " (" + client.uuid + " | " + getTime() + ")\n");
				logMessage(TAG, "IOException", e);
			}
		}

		private void tryHandleInputConnection() throws IOException {
			String connectionString = "btspp://localhost:" + client.uuid
					+ ";name=BT Test Server";

			StreamConnectionNotifier streamConnNotifier = (StreamConnectionNotifier) Connector
					.open(connectionString);

			writeMessage("  - " + client.name + " (" + client.uuid + ")\n");

			while (BTServer.this.run) {
				StreamConnection connection = streamConnNotifier.acceptAndOpen();

				BTServer.writeMessage("\n --> Incomming connection for " + client.name + "  (" + getTime() + ")\n");

				BTClientHandler btc = BluetoothClientHandlerFactory.create(client.uuid);
				btc.setConnection(connection);

				btc.start();
			}

			streamConnNotifier.close();
		}
	}
}

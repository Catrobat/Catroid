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
package org.catrobat.catroid.devices.raspberrypi;

import android.os.AsyncTask;
import android.util.Log;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

public class AsyncRPiTaskRunner {

	private static final String TAG = AsyncRPiTaskRunner.class.getSimpleName();

	private boolean connected;

	private RPiSocketConnection connection = new RPiSocketConnection();

	public boolean connect(String host, int port) {
		try {
			connected = new AsyncConnectTask(connection, host, port).execute()
					.get(2000, TimeUnit.MILLISECONDS);
		} catch (Exception e) {
			Log.e(TAG, "RPi connecting took too long" + e);
			return false;
		}

		return true;
	}

	public RPiSocketConnection getConnection() {
		return connection;
	}

	public void disconnect() {
		if (connected) {
			new AsyncDisconnectTask(connection).execute();
		}
	}

	private static class AsyncConnectTask extends AsyncTask<Void, Void, Boolean> {
		private RPiSocketConnection connection;
		private String host;
		private int port;

		AsyncConnectTask(RPiSocketConnection connection, String host, int port) {
			this.connection = connection;
			this.host = host;
			this.port = port;
		}

		@Override
		protected Boolean doInBackground(Void... args) {
			try {
				connection.connect(host, port);

				for (Integer pin : RaspberryPiService.getInstance().getPinInterrupts()) {
					connection.activatePinInterrupt(pin);
				}
				return true;
			} catch (UnknownHostException e) {
				Log.e(TAG, "RPi: Host not found!");
			} catch (ConnectException e) {
				Log.e(TAG, "RPi: Could not connect!");
			} catch (SocketTimeoutException e) {
				Log.e(TAG, "RPi: Connection timeout!");
			} catch (Exception e) {
				Log.e(TAG, "Exception during connect: " + e);
			}
			return false;
		}
	}

	private static class AsyncDisconnectTask extends AsyncTask<Void, Void, Void> {
		private RPiSocketConnection connection;

		AsyncDisconnectTask(RPiSocketConnection connection) {
			this.connection = connection;
		}

		@Override
		protected Void doInBackground(Void... voids) {
			try {
				connection.disconnect();
			} catch (Exception e) {
				Log.e(TAG, "RPi: Exception during disconnect " + e);
			}
			return null;
		}
	}
}

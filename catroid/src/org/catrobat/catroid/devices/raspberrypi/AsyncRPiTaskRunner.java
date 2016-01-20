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
package org.catrobat.catroid.devices.raspberrypi;

import android.os.AsyncTask;
import android.util.Log;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

public class AsyncRPiTaskRunner {

	private String host;
	private int port;
	private boolean connected;

	private RPiSocketConnection connection;

	public AsyncRPiTaskRunner() {
		connection = new RPiSocketConnection();
	}

	public Boolean connect(String host, int port) {
		this.host = host;
		this.port = port;

		try {
			new AsyncConnectTask().execute().get(2000, TimeUnit.MILLISECONDS);
		} catch (Exception e) {
			Log.e(getClass().getSimpleName(), "RPi connecting took too long" + e);
			return false;
		}

		return connected;
	}

	public RPiSocketConnection getConnection() {
		return connection;
	}

	public void disconnect() {
		if (connected) {
			new AsyncDisconnectTask().execute();
		}
	}

	private class AsyncConnectTask extends AsyncTask<String, Void, Integer> {
		protected Integer doInBackground(String... args) {

			try {
				connection.connect(host, port);

				for (Integer pin : RaspberryPiService.getInstance().getPinInterrupts()) {
					connection.activatePinInterrupt(pin);
				}
			} catch (UnknownHostException e) {
				return 1;
			} catch (ConnectException e) {
				return 2;
			} catch (SocketTimeoutException e) {
				return 3;
			} catch (Exception e) {
				Log.e(getClass().getSimpleName(), "Exception during connect: " + e);
				return 4;
			}
			return 0;
		}

		protected void onPostExecute(Integer progress) {
			if (progress == 1) {
				Log.e(getClass().getSimpleName(), "RPi: Host not found!");
			} else if (progress == 2) {
				Log.e(getClass().getSimpleName(), "RPi: Could not connect!");
			} else if (progress == 3) {
				Log.e(getClass().getSimpleName(), "RPi: Connection timeout!");
			} else if (progress == 4) {
				Log.e(getClass().getSimpleName(), "RPi: Connect unhandled error.");
			} else {
				connected = true;
			}
		}
	}

	private class AsyncDisconnectTask extends AsyncTask<String, Void, Integer> {
		protected Integer doInBackground(String... args) {

			try {
				connection.disconnect();
			} catch (Exception e) {
				Log.e(getClass().getSimpleName(), "Exception during disconnect " + e);
				return 1;
			}
			return 0;
		}

		protected void onPostExecute(Integer error) {
			if (error == 1) {
				Log.e(getClass().getSimpleName(), "RPi: Some error during disconnect.");
			}
		}
	}
}

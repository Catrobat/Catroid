/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2016 The Catrobat Team
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

	private static final int UNKNOWN_HOST = 1;
	private static final int CONNECTION_ERROR = 2;
	private static final int CONNECTION_TIMEOUT = 3;
	private static final int CONNECTION_UNHANDLED_EXCEPTION = 4;

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
			Log.e(TAG, "RPi connecting took too long" + e);
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
				return UNKNOWN_HOST;
			} catch (ConnectException e) {
				return CONNECTION_ERROR;
			} catch (SocketTimeoutException e) {
				return CONNECTION_TIMEOUT;
			} catch (Exception e) {
				Log.e(TAG, "Exception during connect: " + e);
				return CONNECTION_UNHANDLED_EXCEPTION;
			}
			return 0;
		}

		protected void onPostExecute(Integer progress) {
			switch (progress) {
				case UNKNOWN_HOST:
					Log.e(TAG, "RPi: Host not found!");
					break;
				case CONNECTION_ERROR:
					Log.e(TAG, "RPi: Could not connect!");
					break;
				case CONNECTION_TIMEOUT:
					Log.e(TAG, "RPi: Connection timeout!");
					break;
				case CONNECTION_UNHANDLED_EXCEPTION:
					Log.e(TAG, "RPi: Connect unhandled error.");
					break;
				default:
					connected = true;
			}
		}
	}

	private class AsyncDisconnectTask extends AsyncTask<String, Void, Integer> {
		protected Integer doInBackground(String... args) {

			try {
				connection.disconnect();
			} catch (Exception e) {
				Log.e(TAG, "Exception during disconnect " + e);
				return 1;
			}
			return 0;
		}

		protected void onPostExecute(Integer error) {
			if (error == 1) {
				Log.e(TAG, "RPi: Some error during disconnect.");
			}
		}
	}
}

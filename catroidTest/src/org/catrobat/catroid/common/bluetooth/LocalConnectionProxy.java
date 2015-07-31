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
import android.util.Log;

import junit.framework.Assert;

import org.catrobat.catroid.bluetooth.base.BluetoothConnection;
import org.catrobat.catroid.common.bluetooth.models.DeviceModel;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

class LocalConnectionProxy implements BluetoothConnection {

	public static final String TAG = LocalConnectionProxy.class.getSimpleName();

	private InputStream observedInputStream;
	private OutputStream observedOutputStream;

	private State connectionState = State.NOT_CONNECTED;
	private ModelRunner modelRunner;

	LocalConnectionProxy(BluetoothLogger logger) {

		observedInputStream = new ObservedInputStream(new InputStream() {
			@Override
			public int read() throws IOException {
				return 0;
			}
		}, logger);

		observedOutputStream = new ObservedOutputStream(new OutputStream() {
			@Override
			public void write(int i) throws IOException {
			}
		}, logger);

		logger.loggerAttached(this);
	}

	LocalConnectionProxy(BluetoothLogger logger, DeviceModel deviceModel) {

		PipedInputStream serverInputStreamFromClientsOutputStream = new PipedInputStream();
		PipedOutputStream serverOutputStreamToClientsInputStream = new PipedOutputStream();

		PipedInputStream pipedInputStreamForClient = new PipedInputStream();
		PipedOutputStream pipedOutputStreamForClient = new PipedOutputStream();

		try {
			serverInputStreamFromClientsOutputStream.connect(pipedOutputStreamForClient);
			serverOutputStreamToClientsInputStream.connect(pipedInputStreamForClient);

			observedInputStream = new ObservedInputStream(pipedInputStreamForClient, logger);
			observedOutputStream = new ObservedOutputStream(pipedOutputStreamForClient, logger);

			modelRunner = new ModelRunner(deviceModel, serverInputStreamFromClientsOutputStream, serverOutputStreamToClientsInputStream);
			modelRunner.start();
		} catch (IOException e) {
			Assert.fail("Error with ConnectionProxy Stream pipes.");
		}
	}

	@Override
	public BluetoothConnection.State connect() {
		connectionState = BluetoothConnection.State.CONNECTED;
		return connectionState;
	}

	@Override
	public BluetoothConnection.State connectSocket(BluetoothSocket socket) {
		connectionState = BluetoothConnection.State.CONNECTED;
		return connectionState;
	}

	@Override
	public void disconnect() {
		try {
			observedOutputStream.close();
			observedInputStream.close();
		} catch (IOException e) {
			Log.e(TAG, "Error on disconnect while closing streams");
		}

		if (modelRunner != null) {
			modelRunner.stopModelRunner();
		}

		connectionState = State.NOT_CONNECTED;
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return observedInputStream;
	}

	@Override
	public OutputStream getOutputStream() throws IOException {
		return observedOutputStream;
	}

	@Override
	public BluetoothConnection.State getState() {
		return connectionState;
	}
}

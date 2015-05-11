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

package org.catrobat.catroid.devices.albert;

import android.util.Log;

import org.catrobat.catroid.bluetooth.base.BluetoothConnection;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class AlbertConnection {

	public static final byte PACKET_HEADER_1 = (byte) 0xAA;
	public static final byte PACKET_HEADER_2 = 0x55;
	public static final byte PACKET_TAIL_1 = (byte) 0x0D;
	public static final byte PACKET_TAIL_2 = (byte) 0x0A;
	public static final byte COMMAND_SENSOR = 0x06;
	public static final byte COMMAND_EXTERNAL = 0x20;
	private static final String TAG = AlbertConnection.class.getSimpleName();
	private static final int STREAM_ERROR = -1;
	private static final String NOTHING_ON_STREAM_ERROR_STRING = "Nothing on Stream, even tough it was 'available'";
	private final BluetoothConnection connection;
	protected SensorData sensors = SensorData.getInstance();
	private OutputStream outputStream;
	private DataInputStream inputStream;
	private boolean connected = false;

	public AlbertConnection(BluetoothConnection connection) {
		this.connection = connection;
		try {
			outputStream = connection.getOutputStream();
			inputStream = new DataInputStream(connection.getInputStream());
			connected = true;
		} catch (IOException e) {
			Log.d(TAG, "Cannot establish BtConnection");
		}
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (connected) {
					try {
//						inputStream.read();
						receiveMessage(inputStream);
					} catch (IOException e) {
						Log.d(TAG, "Cannot receive albert message");
					}
				}
			}
		}).start();
	}

	public void disconnect() {
		connected = false;
		connection.disconnect();
	}

	public void send(AlbertSendCommands commands) {
		try {
			outputStream.write(commands.getCommandMessage());
		} catch (IOException e) {
			Log.d(TAG, "Cannot send Albert commands");
		}
	}

	public byte[] receiveMessage(InputStream inputStream) throws IOException {
		if (inputStream == null) {
			throw new IOException(" Software caused connection abort ");
		}
		int read;
		byte[] buf = new byte[1];
		int count = 0;
		do {
			do {
//				checkIfDataIsAvailable(1);
				read = inputStream.read(buf);
				if (read == STREAM_ERROR) {
					Log.e(TAG, NOTHING_ON_STREAM_ERROR_STRING);
					return null;
				}
				count++;
				if (count > 400) {
					return null;
				}
			} while (buf[0] != PACKET_HEADER_1);
//			checkIfDataIsAvailable(1);
			read = inputStream.read(buf);
			if (read == STREAM_ERROR) {
				Log.e(TAG, NOTHING_ON_STREAM_ERROR_STRING);
				return null;
			}
		} while (buf[0] != PACKET_HEADER_2);
		byte[] length = new byte[1];
//		checkIfDataIsAvailable(1);
		read = inputStream.read(length);
		if (read == STREAM_ERROR) {
			Log.e(TAG, NOTHING_ON_STREAM_ERROR_STRING);
			return null;
		}
		byte[] buffer = new byte[length[0] - 1];
//		checkIfDataIsAvailable(length[0] - 1);
		read = inputStream.read(buffer);
		if (read == STREAM_ERROR) {
			Log.e(TAG, NOTHING_ON_STREAM_ERROR_STRING);
			return null;
		}

		if (buffer[length[0] - 3] != PACKET_TAIL_1 || buffer[length[0] - 2] != PACKET_TAIL_2) {
			Log.e(TAG, "ERROR: Packet tail not found!");
			return null;
		}

		switch (buffer[0]) {
			case COMMAND_SENSOR:

				int leftDistance = (buffer[11] + buffer[13] + buffer[15] + buffer[17]) / 4;
				int rightDistance = (buffer[10] + buffer[12] + buffer[14] + buffer[16]) / 4;

				sensors.setValueOfLeftDistanceSensor(leftDistance);
				sensors.setValueOfRightDistanceSensor(rightDistance);

				if (AlbertImpl.ALBERT_SENSOR_DEBUG_OUTPUT) {
					Log.d(TAG, "sensor packet found");
					Log.d(TAG, "receiveMessage:  leftDistance=" + leftDistance);
					Log.d(TAG, "receiveMessage: rightDistance=" + rightDistance);
				}

				break;
			case COMMAND_EXTERNAL:
				Log.d(TAG, "External Packet received!");
				break;

			default:
				Log.d(TAG, "Unknown Command! id = " + buffer[0]);
				break;
		}
		return buffer;
	}
}

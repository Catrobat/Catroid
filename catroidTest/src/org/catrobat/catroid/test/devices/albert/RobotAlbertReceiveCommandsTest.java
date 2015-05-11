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

package org.catrobat.catroid.test.devices.albert;


import android.test.AndroidTestCase;
import android.util.Log;

import org.catrobat.catroid.common.bluetooth.ConnectionDataLogger;
import org.catrobat.catroid.common.bluetooth.models.AlbertModel;
import org.catrobat.catroid.devices.albert.AlbertConnection;
import org.catrobat.catroid.devices.albert.SensorData;

import java.io.DataInputStream;
import java.io.IOException;

public class RobotAlbertReceiveCommandsTest extends AndroidTestCase {

	private static final String TAG = RobotAlbertReceiveCommandsTest.class.getSimpleName();
	protected SensorData sensors = SensorData.getInstance();
	ConnectionDataLogger logger;
	AlbertModel model;
	AlbertConnection connection = null;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		this.model = new AlbertModel();
		this.logger = ConnectionDataLogger.createLocalConnectionLoggerWithDeviceModel(model);
	}

	@Override
	protected void tearDown() throws Exception {
		if (connection != null) {
			connection.disconnect();
		}
		logger.disconnect();
		super.tearDown();
	}

	public void testSendCommandsDistance() {
		DataInputStream inputStream;
		try {
			inputStream = new DataInputStream(logger.getConnectionProxy().getInputStream());
			singleSent(model, inputStream, 50, 60, 36);
			singleSent(model, inputStream, 40, 70, 36);
			singleSent(model, inputStream, 80, 30, 28);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void testSensorReceive() {
		int distanceLeft;
		int distanceRight;
		int count = 0;
		this.connection = new AlbertConnection(logger.getConnectionProxy());
		do {
			try {
				model.sendSensorCommands(25, 35, 36);
			} catch (IOException e) {
				Log.d(TAG, "Albert model send failed!");
			}
			distanceLeft = sensors.getValueOfLeftDistanceSensor();
			distanceRight = sensors.getValueOfRightDistanceSensor();
			count++;
			if (count > 40) {
				break;
			}
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} while (distanceLeft == 0);
		assertEquals("Error: Albert distance left wrong value!", 25, distanceLeft);
		assertEquals("Error: Albert distance right wrong value!", 35, distanceRight);
	}

	private void singleSent(AlbertModel model, DataInputStream inputStream, int distanceLeft, int distanceRight, int length) {
		byte[] buf = new byte[length];
		byte[] message;
		try {
			model.sendSensorCommands(distanceLeft, distanceRight, length);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			inputStream.readFully(buf);
		} catch (IOException e) {
			Log.d(TAG, "Albert model send or receive failed!");
		}
		message = logger.getNextReceivedMessage();
		checkSendCommand(distanceLeft, length, message, 14, 16, 18, 20);
		checkSendCommand(distanceRight, length, message, 13, 15, 17, 19);
	}

	private void checkSendCommand(int distance, int length, byte[] send, int... items) {
		assertEquals("Error: Albert test HEADER1 not found!", AlbertConnection.PACKET_HEADER_1, send[0]);
		assertEquals("Error: Albert test HEADER2 not found!", AlbertConnection.PACKET_HEADER_2, send[1]);
		assertEquals("Error: Albert test send command length false!", length, send.length);
		assertEquals("Error: Albert test TAIL1 not found!", AlbertConnection.PACKET_TAIL_1, send[length - 2]);
		assertEquals("Error: Albert test TAIL1 not found!", AlbertConnection.PACKET_TAIL_2, send[length - 1]);
		for (int item : items) {
			assertEquals("Error: Albert test wrong value received!", (byte) distance, send[item]);
		}
	}

}

/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2014 The Catrobat Team
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

import org.catrobat.catroid.bluetooth.base.BluetoothConnection;
import org.catrobat.catroid.bluetooth.base.BluetoothDevice;
import org.catrobat.catroid.common.bluetooth.models.DeviceModel;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public final class ConnectionDataLogger {

	private Queue<byte[]> sentMessages = new LinkedList<byte[]>();
	private Queue<byte[]> receivedMessages = new LinkedList<byte[]>();


	public byte[] getNextSentMessage() {
		return getNextSentMessage(0, 0);
	}

	public byte[] getNextSentMessage(int messageOffset){
		return getNextSentMessage(messageOffset, 0);
	}

	public byte[] getNextSentMessage(int messageOffset, int messageByteOffset) {
		return getNextMessage(sentMessages, messageOffset, messageByteOffset);
	}



	public ArrayList<byte[]> getSentMessages() {
		return getSentMessages(0, true);
	}

	public ArrayList<byte[]> getSentMessages(int messageByteOffset, boolean clearMessageQueue) {
		return getMessages(sentMessages, messageByteOffset, clearMessageQueue);
	}



	public byte[] getNextReceivedMessage() {
		return getNextReceivedMessage(0, 0);
	}

	public byte[] getNextReceivedMessage(int messageOffset) {
		return getNextReceivedMessage(messageOffset, 0);
	}

	public byte[] getNextReceivedMessage(int messageOffset, int messageByteOffset) {
		return getNextMessage(receivedMessages, messageOffset, messageByteOffset);
	}



	public ArrayList<byte[]> getReceivedMessages() {
		return getReceivedMessages(0, true);
	}

	public ArrayList<byte[]> getReceivedMessages(int messageByteOffset, boolean clearMessageQueue) {
		return getMessages(receivedMessages, messageByteOffset, clearMessageQueue);
	}



	private static byte[] getNextMessage(Queue<byte[]> messages, int messageOffset, int messageByteOffset) {
		synchronized (messages) {
			for (int i = 0; i < messageOffset; i++) {
				messages.poll();
			}
			return BluetoothTestUtils.getSubArray(messages.poll(), messageByteOffset);
		}
	}

	private static ArrayList<byte[]> getMessages(Queue<byte[]> messages, int messageByteOffset, boolean clearMessageQueue) {

		ArrayList<byte[]> m = new ArrayList<byte[]>();
		synchronized (messages) {
			for (byte[] message : messages) {
				m.add(BluetoothTestUtils.getSubArray(message, messageByteOffset));
			}

			if (clearMessageQueue) {
				messages.clear();
			}
		}

		return m;
	}



	private BluetoothConnection connectionProxy;

	private Logger logger = new Logger() {

		@Override
		public void logSentData(byte[] b) {
			synchronized (sentMessages) {
				sentMessages.add(b);
			}
		}

		@Override
		public void logReceivedData(byte[] b) {
			synchronized (receivedMessages) {
				receivedMessages.add(b);
			}
		}

		@Override
		public void loggerAttached(BluetoothConnection proxy) {
			connectionProxy = proxy;
		}
	};

	private ConnectionDataLogger(boolean local) {
		if (local) {
			connectionProxy = new LocalConnectionProxy(logger);
			BluetoothTestUtils.hookInConnection(connectionProxy);
		}
		else {
			BluetoothTestUtils.hookInConnectionFactoryWithBluetoothConnectionProxy(logger);
		}
	}

	private ConnectionDataLogger(DeviceModel deviceModel) {
		connectionProxy = new LocalConnectionProxy(logger, deviceModel);
		BluetoothTestUtils.hookInConnection(connectionProxy);
	}

	public static ConnectionDataLogger createLocalConnectionLogger() {
		return new ConnectionDataLogger(true);
	}

	public static ConnectionDataLogger createLocalConnectionLoggerWithTestDevice(BluetoothDevice testDevice) {
		BluetoothTestUtils.hookInTestDevice(testDevice);
		return new ConnectionDataLogger(true);
	}

	public static ConnectionDataLogger createLocalConnectionLoggerWithDeviceModel(DeviceModel deviceModel) {
		return new ConnectionDataLogger(deviceModel);
	}

	public static ConnectionDataLogger createLocalConnectionLoggerWithDeviceModelAndTestDevice(DeviceModel deviceModel, BluetoothDevice testDevice) {
		BluetoothTestUtils.hookInTestDevice(testDevice);
		return new ConnectionDataLogger(deviceModel);
	}

	public static ConnectionDataLogger createBluetoothConnectionLogger() {
		return new ConnectionDataLogger(false);
	}

	public static ConnectionDataLogger createBluetoothConnectionLoggerWithTestDevice(BluetoothDevice testDevice) {
		BluetoothTestUtils.hookInTestDevice(testDevice);
		return new ConnectionDataLogger(false);
	}

	public void disconnect() {
		if (connectionProxy != null) {
			connectionProxy.disconnect();
		}
	}

	public BluetoothConnection getConnectionProxy() {
		return connectionProxy;
	}

	Logger getLogger() {
		return logger;
	}
}

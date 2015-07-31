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

import com.google.common.base.Stopwatch;

import org.catrobat.catroid.bluetooth.base.BluetoothConnection;
import org.catrobat.catroid.bluetooth.base.BluetoothDevice;
import org.catrobat.catroid.common.bluetooth.models.DeviceModel;

import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public final class ConnectionDataLogger {

	private BlockingQueue<byte[]> sentMessages = new LinkedBlockingQueue<byte[]>();
	private BlockingQueue<byte[]> receivedMessages = new LinkedBlockingQueue<byte[]>();

	private static final int TIMEOUT_SECONDS = 15;

	public byte[] getNextSentMessage() {
		return getNextSentMessage(0, 0);
	}

	public byte[] getNextSentMessage(int messageOffset) {
		return getNextSentMessage(messageOffset, 0);
	}

	public byte[] getNextSentMessage(int messageOffset, int messageByteOffset) {
		return getNextMessage(sentMessages, messageOffset, messageByteOffset);
	}

	public ArrayList<byte[]> getSentMessages(int messageCountToWaitFor) {
		return getSentMessages(0, messageCountToWaitFor);
	}

	public ArrayList<byte[]> getSentMessages(int messageByteOffset, int messageCountToWaitFor) {
		return getMessages(sentMessages, messageByteOffset, messageCountToWaitFor);
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

	public ArrayList<byte[]> getReceivedMessages(int messageCountToWaitFor) {
		return getReceivedMessages(0, messageCountToWaitFor);
	}

	public ArrayList<byte[]> getReceivedMessages(int messageByteOffset, int messageCountToWaitFor) {
		return getMessages(receivedMessages, messageByteOffset, messageCountToWaitFor);
	}

	private static byte[] getNextMessage(BlockingQueue<byte[]> messages, int messageOffset, int messageByteOffset) {

		Stopwatch stopWatch = Stopwatch.createStarted();

		for (int i = 0; i < messageOffset; i++) {
			byte[] message = pollMessage(messages, TIMEOUT_SECONDS - (int) stopWatch.elapsed(TimeUnit.SECONDS));
			if (message == null) {
				return null;
			}
		}

		byte[] message = pollMessage(messages, TIMEOUT_SECONDS - (int) stopWatch.elapsed(TimeUnit.SECONDS));
		if (message == null) {
			return null;
		}

		return BluetoothTestUtils.getSubArray(message, messageByteOffset);
	}

	private static ArrayList<byte[]> getMessages(BlockingQueue<byte[]> messages, int messageByteOffset, int messageCountToWaitFor) {

		if (messageCountToWaitFor == 0) {
			return getMessages(messages, messageByteOffset);
		}

		return waitForMessages(messages, messageByteOffset, messageCountToWaitFor);
	}

	private static ArrayList<byte[]> waitForMessages(BlockingQueue<byte[]> messages, int messageByteOffset, int messageCountToWaitFor) {

		ArrayList<byte[]> m = new ArrayList<byte[]>();
		Stopwatch stopWatch = Stopwatch.createStarted();

		do {
			byte[] message = pollMessage(messages, TIMEOUT_SECONDS - (int) stopWatch.elapsed(TimeUnit.SECONDS));
			if (message == null) {
				return m;
			}
			m.add(BluetoothTestUtils.getSubArray(message, messageByteOffset));
		} while (m.size() < messageCountToWaitFor && stopWatch.elapsed(TimeUnit.SECONDS) < TIMEOUT_SECONDS);

		return m;
	}

	private static ArrayList<byte[]> getMessages(BlockingQueue<byte[]> messages, int messageByteOffset) {

		ArrayList<byte[]> m = new ArrayList<byte[]>();

		byte[] message = null;
		while ((message = messages.poll()) != null) {
			m.add(BluetoothTestUtils.getSubArray(message, messageByteOffset));
		}

		return m;
	}

	private static byte[] pollMessage(BlockingQueue<byte[]> messages, int timeoutSeconds) {

		try {
			return messages.poll(timeoutSeconds, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			return null;
		}
	}

	private BluetoothConnection connectionProxy;

	private BluetoothLogger logger = new BluetoothLogger() {

		@Override
		public void logSentData(byte[] b) {
			sentMessages.add(b);
		}

		@Override
		public void logReceivedData(byte[] b) {
			receivedMessages.add(b);
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
		} else {
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

	public void disconnectAndDestroy() {
		if (connectionProxy != null) {
			connectionProxy.disconnect();
		}

		BluetoothTestUtils.resetConnectionHooks();
	}

	public BluetoothConnection getConnectionProxy() {
		return connectionProxy;
	}

	BluetoothLogger getLogger() {
		return logger;
	}
}

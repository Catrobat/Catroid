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
package org.catrobat.catroid.uitest.bluetooth;

import android.content.Intent;
import android.widget.ListView;

import com.robotium.solo.Solo;

import org.catrobat.catroid.R;
import org.catrobat.catroid.bluetooth.ConnectBluetoothDeviceActivity;
import org.catrobat.catroid.bluetooth.base.BluetoothConnection;
import org.catrobat.catroid.bluetooth.base.BluetoothDevice;
import org.catrobat.catroid.bluetooth.base.BluetoothDeviceService;
import org.catrobat.catroid.common.CatroidService;
import org.catrobat.catroid.common.ServiceProvider;
import org.catrobat.catroid.common.bluetooth.BluetoothTestUtils;
import org.catrobat.catroid.common.bluetooth.ConnectionDataLogger;
import org.catrobat.catroid.uitest.annotation.Device;
import org.catrobat.catroid.uitest.util.BaseActivityInstrumentationTestCase;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

public class BluetoothDeviceServiceTest extends BaseActivityInstrumentationTestCase<ConnectBluetoothDeviceActivity> {

	public BluetoothDeviceServiceTest() {
		super(ConnectBluetoothDeviceActivity.class, false);
	}

	private static final String PAIRED_UNAVAILABLE_DEVICE_NAME = "DUMMY DEVICE";

	public static final Class<BluetoothTestDevice> TEST_DEVICE = BluetoothTestDevice.class;
	private ConnectionDataLogger logger;

	private Solo solo;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		Intent intent = new Intent(getInstrumentation().getContext(), CatroidService.BLUETOOTH_DEVICE_SERVICE);
		intent.putExtra(ConnectBluetoothDeviceActivity.DEVICE_TO_CONNECT, TEST_DEVICE);

		setActivityIntent(intent);

		logger = ConnectionDataLogger.createBluetoothConnectionLoggerWithTestDevice(new BluetoothTestDevice());

		BluetoothTestUtils.enableBluetooth();
		solo = new Solo(getInstrumentation(), getActivity());
		solo.unlockScreen();
	}

	@Override
	protected void tearDown() throws Exception {

		ServiceProvider.getService(CatroidService.BLUETOOTH_DEVICE_SERVICE).disconnectDevices();
		logger.disconnectAndDestroy();
		BluetoothTestUtils.disableBluetooth();

		super.tearDown();
	}

	@Device
	public void testBluetoothConnector() throws IOException {

		solo.waitForActivity(ConnectBluetoothDeviceActivity.class);

		ListView deviceList = solo.getCurrentViews(ListView.class).get(0);
		String connectedDeviceName = (String) deviceList.getItemAtPosition(0);

		assertFalse("No bluetooth device is paired, connection failed.",
				connectedDeviceName == null || connectedDeviceName.equals(solo.getString(R.string.none_paired)));

		solo.clickOnText(connectedDeviceName);

		solo.sleep(5000); // wait for connection

		BluetoothDeviceService btService = ServiceProvider.getService(CatroidService.BLUETOOTH_DEVICE_SERVICE);
		BluetoothTestDevice btDevice = btService.getDevice(TEST_DEVICE);

		assertNotNull("Device should be registered now. Bluetooth connection might failed. Is Bluetooth-Server running?", btDevice);
		btDevice.connect();

		byte[] expectedMessage = new byte[] { 1, 2, 3 };

		btDevice.sendTestMessage(expectedMessage);
		solo.sleep(2000); // wait some time till return message arrives
		byte[] receivedMessage = btDevice.receiveTestMessage();
		assertMessageEquals(expectedMessage, receivedMessage);

		assertMessageEquals(expectedMessage, logger.getNextSentMessage(1));
		assertMessageEquals(expectedMessage, logger.getNextReceivedMessage(1));
	}

	private void assertMessageEquals(byte[] expected, byte[] actual) {

		assertEquals("Bluetooth message is not equal, because of different message length.", expected.length, actual.length);

		for (int i = 0; i < expected.length; i++) {
			assertEquals("Bluetooth message is not equal, byte " + i + " is different", expected[i], actual[i]);
		}
	}

	@Device
	public void testConnectNotAvailableBluetoothDevice() throws IOException {
		solo.waitForActivity(ConnectBluetoothDeviceActivity.class);

		BluetoothTestUtils.addPairedDevice(PAIRED_UNAVAILABLE_DEVICE_NAME, getActivity(), getInstrumentation());

		ListView deviceList = solo.getCurrentViews(ListView.class).get(0);
		String connectedDeviceName = null;
		for (int i = 0; i < deviceList.getCount(); i++) {
			String deviceName = (String) deviceList.getItemAtPosition(i);
			if (deviceName.startsWith(PAIRED_UNAVAILABLE_DEVICE_NAME)) {
				connectedDeviceName = deviceName;
				break;
			}
		}
		assertTrue("Bluetooth device '" + PAIRED_UNAVAILABLE_DEVICE_NAME + "' is not paired, so cannot connect.",
				connectedDeviceName != null && solo.searchText(connectedDeviceName));
		solo.clickOnText(connectedDeviceName);

		solo.sleep(15000); //yes, has to be that long! waiting for connection timeout!

		BluetoothDeviceService service = ServiceProvider.getService(CatroidService.BLUETOOTH_DEVICE_SERVICE);

		assertNull("Device should not be registered, because test tries to connect to a device which is not available.",
				service.getDevice(TEST_DEVICE));
	}

	private static class BluetoothTestDevice implements BluetoothDevice {

		private static final UUID COMMON_BT_TEST_UUID = UUID.fromString("fd2835bb-9d80-41e0-9721-5372b90342da");
		private boolean isConnected = false;
		private BluetoothConnection connection;

		private DataInputStream inStream;
		private OutputStream outStream;

		public boolean isConnected() {
			return isConnected;
		}

		@Override
		public String getName() {
			return "BT Test Device";
		}

		@Override
		public Class<? extends BluetoothDevice> getDeviceType() {
			return BluetoothTestDevice.class;
		}

		@Override
		public void setConnection(BluetoothConnection connection) {
			this.connection = connection;
		}

		public void connect() throws IOException {
			inStream = new DataInputStream(connection.getInputStream());
			outStream = connection.getOutputStream();

			isConnected = true;
		}

		@Override
		public void disconnect() {
			connection.disconnect();
			isConnected = false;
		}

		@Override
		public boolean isAlive() {
			return isConnected;
		}

		@Override
		public UUID getBluetoothDeviceUUID() {
			return COMMON_BT_TEST_UUID;
		}

		public void sendTestMessage(byte[] message) throws IOException {

			outStream.write(new byte[] { (byte) (0xFF & message.length) });
			outStream.write(message);
			outStream.flush();
		}

		public byte[] receiveTestMessage() throws IOException {
			byte[] messageLengthBuffer = new byte[1];

			inStream.readFully(messageLengthBuffer, 0, 1);
			int expectedMessageLength = messageLengthBuffer[0];

			byte[] payload = new byte[expectedMessageLength];

			inStream.readFully(payload, 0, expectedMessageLength);

			return payload;
		}

		@Override
		public void initialise() {
		}

		@Override
		public void start() {
		}

		@Override
		public void pause() {
		}

		@Override
		public void destroy() {
		}
	}
}

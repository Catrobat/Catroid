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

package org.catrobat.catroid.test.devices.mindstorms.nxt;

import android.test.AndroidTestCase;

import org.catrobat.catroid.common.bluetooth.ConnectionDataLogger;
import org.catrobat.catroid.common.bluetooth.models.MindstormsNXTTestModel;
import org.catrobat.catroid.devices.mindstorms.MindstormsConnection;
import org.catrobat.catroid.devices.mindstorms.MindstormsConnectionImpl;
import org.catrobat.catroid.devices.mindstorms.nxt.CommandByte;
import org.catrobat.catroid.devices.mindstorms.nxt.sensors.NXTI2CUltraSonicSensor;
import org.catrobat.catroid.devices.mindstorms.nxt.sensors.NXTLightSensor;
import org.catrobat.catroid.devices.mindstorms.nxt.sensors.NXTSensor;
import org.catrobat.catroid.devices.mindstorms.nxt.sensors.NXTSensorMode;
import org.catrobat.catroid.devices.mindstorms.nxt.sensors.NXTSensorType;
import org.catrobat.catroid.devices.mindstorms.nxt.sensors.NXTSoundSensor;
import org.catrobat.catroid.devices.mindstorms.nxt.sensors.NXTTouchSensor;

public class SensorTests extends AndroidTestCase {

	private static final byte DIRECT_COMMAND_WITHOUT_REPLY = (byte) 0x80;
	private static final byte DIRECT_COMMAND_WITH_REPLY = (byte) 0x00;
	private static final byte PORT_NR_0 = 0;
	private static final byte PORT_NR_1 = 1;
	private static final byte PORT_NR_2 = 2;
	private static final byte PORT_NR_3 = 3;

	private static final byte ULTRASONIC_ADDRESS = 0x02;
	private static final byte SENSOR_REGISTER_RESULT1 = 0x42;

	private ConnectionDataLogger logger;
	private MindstormsConnection mindstormsConnection;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		this.logger = ConnectionDataLogger.createLocalConnectionLoggerWithDeviceModel(new MindstormsNXTTestModel());

		this.mindstormsConnection = new MindstormsConnectionImpl(logger.getConnectionProxy());
		mindstormsConnection.init();
	}

	@Override
	protected void tearDown() throws Exception {
		mindstormsConnection.disconnect();
		logger.disconnect();

		super.tearDown();
	}

	public void testSetSensorModeTouch() {
		NXTSensor sensor = new NXTTouchSensor(PORT_NR_0, mindstormsConnection);

		sensor.getValue();

		byte[] command = logger.getNextSentMessage(0, 2);
		assertNotNull("No command", command);

		assertEquals("Incorrect Header", DIRECT_COMMAND_WITH_REPLY, command[0]);
		assertEquals("Wrong CommandByte", CommandByte.SET_INPUT_MODE.getByte(), command[1]);
		assertEquals("Wrong Port", PORT_NR_0, command[2]);
		assertEquals("Wrong sensor type", NXTSensorType.TOUCH.getByte(), command[3]);
		assertEquals("Wrong sensor mode", NXTSensorMode.BOOL.getByte(), command[4]);
		assertEquals("Wrong command length", 5, command.length);
	}

	public void testSetSensorModeSound() {
		NXTSensor sensor = new NXTSoundSensor(PORT_NR_1, mindstormsConnection);

		sensor.getValue();

		byte[] command = logger.getNextSentMessage(0, 2);
		assertNotNull("No command", command);

		assertEquals("Incorrect Header", DIRECT_COMMAND_WITH_REPLY, command[0]);
		assertEquals("Wrong CommandByte", CommandByte.SET_INPUT_MODE.getByte(), command[1]);
		assertEquals("Wrong port", PORT_NR_1, command[2]);
		assertEquals("Wrong sensor Type", NXTSensorType.SOUND_DBA.getByte(), command[3]);
		assertEquals("Wrong sensor mode", NXTSensorMode.Percent.getByte(), command[4]);
		assertEquals("Wrong command length", 5, command.length);
	}

	public void testSetSensorModeLight() {
		NXTSensor sensor = new NXTLightSensor(PORT_NR_2, mindstormsConnection);

		sensor.getValue();

		byte[] command = logger.getNextSentMessage(0, 2);
		assertNotNull("No command", command);

		assertEquals("Incorrect Header", DIRECT_COMMAND_WITH_REPLY, command[0]);
		assertEquals("Wrong CommandByte", CommandByte.SET_INPUT_MODE.getByte(), command[1]);
		assertEquals("Wrong port", PORT_NR_2, command[2]);
		assertEquals("Wrong sensor type", NXTSensorType.LIGHT_INACTIVE.getByte(), command[3]);
		assertEquals("Wrong sensor mode", NXTSensorMode.Percent.getByte(), command[4]);
		assertEquals("Wrong command length", 5, command.length);
	}

	public void testGetSimpleSensorValue() {
		NXTSensor sensor = new NXTTouchSensor(PORT_NR_0, mindstormsConnection);

		sensor.getValue();
		byte[] command = null;
		byte[] firstCommand = logger.getNextSentMessage(0, 2);
		while(firstCommand != null) {
			command = firstCommand;
			firstCommand = logger.getNextSentMessage(0, 2);
		}

		assertNotNull("No command", command);

		assertEquals("Incorrect Header", DIRECT_COMMAND_WITH_REPLY, command[0]);
		assertEquals("Wrong Command Byte", CommandByte.GET_INPUT_VALUES.getByte(), command[1]);
		assertEquals("Wrong port", PORT_NR_0, command[2]);
		assertEquals("Wrong command length", 3, command.length);
	}

	public void testGetSimpleSensorValueFullCommunication() {
		NXTSensor sensor = new NXTTouchSensor(PORT_NR_0, mindstormsConnection);

		sensor.getValue();

		byte[] command = logger.getNextSentMessage(0, 2);
		assertNotNull("No command1", command);

		assertEquals("Incorrect Header", DIRECT_COMMAND_WITH_REPLY, command[0]);
		assertEquals("Wrong CommandByte", CommandByte.SET_INPUT_MODE.getByte(), command[1]);
		assertEquals("Wrong Port", PORT_NR_0, command[2]);
		assertEquals("Wrong sensor type", NXTSensorType.TOUCH.getByte(), command[3]);
		assertEquals("Wrong sensor mode", NXTSensorMode.BOOL.getByte(), command[4]);
		assertEquals("Wrong command length", 5, command.length);

		command = logger.getNextSentMessage(0, 2);
		assertNotNull("No command2", command);

		assertEquals("Incorrect Header2", DIRECT_COMMAND_WITHOUT_REPLY, command[0]);
		assertEquals("Wrong CommandByte2", CommandByte.RESET_INPUT_SCALED_VALUE.getByte(), command[1]);
		assertEquals("Wrong Port2", PORT_NR_0, command[2]);
		assertEquals("Wrong command length2", 3, command.length);

		command = logger.getNextSentMessage(0, 2);
		assertNotNull("No command3", command);

		assertEquals("Incorrect Header3", DIRECT_COMMAND_WITH_REPLY, command[0]);
		assertEquals("Wrong CommandByte3", CommandByte.SET_INPUT_MODE.getByte(), command[1]);
		assertEquals("Wrong Port3", PORT_NR_0, command[2]);
		assertEquals("Wrong sensor type3", NXTSensorType.TOUCH.getByte(), command[3]);
		assertEquals("Wrong sensor mode3", NXTSensorMode.BOOL.getByte(), command[4]);
		assertEquals("Wrong command length3", 5, command.length);

		command = logger.getNextSentMessage(0, 2);
		assertNotNull("No command4", command);

		assertEquals("Incorrect Header4", DIRECT_COMMAND_WITH_REPLY, command[0]);
		assertEquals("Wrong Command Byte4", CommandByte.GET_INPUT_VALUES.getByte(), command[1]);
		assertEquals("Wrong port4", PORT_NR_0, command[2]);
		assertEquals("Wrong command length4", 3, command.length);
	}

	public void testSetSensorModeUltraSonic() {
		NXTI2CUltraSonicSensor sensor = new NXTI2CUltraSonicSensor(mindstormsConnection);

		sensor.getValue();

		byte[] command = logger.getNextSentMessage(0, 2);
		assertNotNull("No command1", command);

		assertEquals("Incorrect Header", DIRECT_COMMAND_WITH_REPLY, command[0]);
		assertEquals("Wrong command byte", CommandByte.SET_INPUT_MODE.getByte(), command[1]);
		assertEquals("Wrong port", PORT_NR_3, command[2]);
		assertEquals("Wrong sensor type", NXTSensorType.LOW_SPEED_9V.getByte(), command[3]);
		assertEquals("Wrong sensor mode", NXTSensorMode.RAW.getByte(), command[4]);
		assertEquals("Wrong command length", 5, command.length);
	}

	public void testGetI2CSensorValueLSReadOnly() {
		NXTI2CUltraSonicSensor sensor = new NXTI2CUltraSonicSensor(mindstormsConnection);

		sensor.getValue();
		byte[] command = null;
		byte[] firstCommand = logger.getNextSentMessage(0, 2);
		while(firstCommand != null) {
			command = firstCommand;
			firstCommand = logger.getNextSentMessage(0, 2);
		}

		assertNotNull("No command", command);

		assertEquals("Incorrect Header", DIRECT_COMMAND_WITH_REPLY, command[0]);
		assertEquals("Wrong command byte", CommandByte.LS_READ.getByte(), command[1]);
		assertEquals("Wrong port", PORT_NR_3, command[2]);
		assertEquals("Wrong command length", 3, command.length);
	}

	public void testGetI2CSensorValue() {
		NXTI2CUltraSonicSensor sensor = new NXTI2CUltraSonicSensor(mindstormsConnection);

		sensor.getValue();
		byte[] command = logger.getNextSentMessage(0, 2);
		assertNotNull("No command1a", command);

		while( (command[1] != CommandByte.LS_WRITE.getByte()) && (command != null) ) {
			command = logger.getNextSentMessage(0, 2);
			assertNotNull("No command1b", command);
		}

		command = logger.getNextSentMessage(0, 2);
		assertNotNull("No command1c", command);

		while( (command[1] != CommandByte.LS_WRITE.getByte()) && (command != null) ) {
			command = logger.getNextSentMessage(0, 2);
			assertNotNull("No command1d", command);
		}

		assertEquals("Incorrect Header", DIRECT_COMMAND_WITHOUT_REPLY, command[0]);
		assertEquals("Wrong command byte", CommandByte.LS_WRITE.getByte(), command[1]);
		assertEquals("Wrong port", PORT_NR_3, command[2]);
		assertEquals("Wrong Tx data length", 2, command[3]);
		assertEquals("Wrong Rx data length", 1, command[4]);
		assertEquals("Wrong Tx address", ULTRASONIC_ADDRESS, command[5]);
		assertEquals("Wrong Tx register", SENSOR_REGISTER_RESULT1, command[6]);
		assertEquals("Wrong command length", 7, command.length);

		command = logger.getNextSentMessage(0, 2);
		assertNotNull("No command2", command);

		assertEquals("Incorrect Header2", DIRECT_COMMAND_WITH_REPLY, command[0]);
		assertEquals("Wrong command byte2", CommandByte.LS_GET_STATUS.getByte(), command[1]);
		assertEquals("Wrong port2", PORT_NR_3, command[2]);
		assertEquals("Wrong command length2", 3, command.length);

		command = logger.getNextSentMessage(0, 2);
		assertNotNull("No command3a", command);

		while(command[1] == CommandByte.LS_GET_STATUS.getByte()) {
			command = logger.getNextSentMessage(0, 2);
			assertNotNull("No command3b", command);
		}

		assertEquals("Incorrect Header3", DIRECT_COMMAND_WITH_REPLY, command[0]);
		assertEquals("Wrong command byte3", CommandByte.LS_READ.getByte(), command[1]);
		assertEquals("Wrong port3", PORT_NR_3, command[2]);
		assertEquals("Wrong command length3", 3, command.length);
	}

	public void testGetI2CSensorValueFullCommunication() {
		NXTI2CUltraSonicSensor sensor = new NXTI2CUltraSonicSensor(mindstormsConnection);

		sensor.getValue();

		byte[] command = logger.getNextSentMessage(0, 2);
		assertNotNull("No command", command);

		assertEquals("Incorrect Header", DIRECT_COMMAND_WITH_REPLY, command[0]);
		assertEquals("Wrong command byte", CommandByte.SET_INPUT_MODE.getByte(), command[1]);
		assertEquals("Wrong port", PORT_NR_3, command[2]);
		assertEquals("Wrong sensor type", NXTSensorType.LOW_SPEED_9V.getByte(), command[3]);
		assertEquals("Wrong sensor mode", NXTSensorMode.RAW.getByte(), command[4]);
		assertEquals("Wrong command length", 5, command.length);

		command = logger.getNextSentMessage(0, 2);
		assertNotNull("No command2", command);

		assertEquals("Incorrect Header2", DIRECT_COMMAND_WITHOUT_REPLY, command[0]);
		assertEquals("Wrong CommandByte2", CommandByte.RESET_INPUT_SCALED_VALUE.getByte(), command[1]);
		assertEquals("Wrong Port2", PORT_NR_3, command[2]);
		assertEquals("Wrong command length2", 3, command.length);

		command = logger.getNextSentMessage(0, 2);
		assertNotNull("No command3", command);

		assertEquals("Incorrect Header3", DIRECT_COMMAND_WITH_REPLY, command[0]);
		assertEquals("Wrong command byte3", CommandByte.SET_INPUT_MODE.getByte(), command[1]);
		assertEquals("Wrong port3", PORT_NR_3, command[2]);
		assertEquals("Wrong sensor type3", NXTSensorType.LOW_SPEED_9V.getByte(), command[3]);
		assertEquals("Wrong sensor mode3", NXTSensorMode.RAW.getByte(), command[4]);
		assertEquals("Wrong command length3", 5, command.length);

		command = logger.getNextSentMessage(0, 2);
		assertNotNull("No command4", command);

		assertEquals("Incorrect Header4", DIRECT_COMMAND_WITHOUT_REPLY, command[0]);
		assertEquals("Wrong command byte4", CommandByte.LS_WRITE.getByte(), command[1]);
		assertEquals("Wrong port4", PORT_NR_3, command[2]);
		assertEquals("Wrong Tx data length4", 2, command[3]);
		assertEquals("Wrong Rx data length4", 1, command[4]);
		assertEquals("Wrong Tx address4", ULTRASONIC_ADDRESS, command[5]);
		assertEquals("Wrong Tx register4", 0x00, command[6]);
		assertEquals("Wrong command length4", 7, command.length);

		command = logger.getNextSentMessage(0, 2);
		assertNotNull("No command5", command);

		assertEquals("Incorrect Header5", DIRECT_COMMAND_WITH_REPLY, command[0]);
		assertEquals("Wrong command byte5", CommandByte.LS_GET_STATUS.getByte(), command[1]);
		assertEquals("Wrong port5", PORT_NR_3, command[2]);
		assertEquals("Wrong command length5", 3, command.length);

		command = logger.getNextSentMessage(0, 2);
		assertNotNull("No command6a", command);

		while(command[1] == CommandByte.LS_GET_STATUS.getByte()) {
			command = logger.getNextSentMessage(0, 2);
			assertNotNull("No command6b", command);
		}

		assertEquals("Incorrect Header6", DIRECT_COMMAND_WITH_REPLY, command[0]);
		assertEquals("Wrong command byte6", CommandByte.LS_READ.getByte(), command[1]);
		assertEquals("Wrong port6", PORT_NR_3, command[2]);
		assertEquals("Wrong command length6", 3, command.length);

		command = logger.getNextSentMessage(0, 2);
		assertNotNull("No command7", command);

		assertEquals("Incorrect Header7", DIRECT_COMMAND_WITHOUT_REPLY, command[0]);
		assertEquals("Wrong command byte7", CommandByte.LS_WRITE.getByte(), command[1]);
		assertEquals("Wrong port7", PORT_NR_3, command[2]);
		assertEquals("Wrong Tx data length7", 2, command[3]);
		assertEquals("Wrong Rx data length7", 1, command[4]);
		assertEquals("Wrong Tx address7", ULTRASONIC_ADDRESS, command[5]);
		assertEquals("Wrong Tx register7", SENSOR_REGISTER_RESULT1, command[6]);
		assertEquals("Wrong command length7", 7, command.length);

		command = logger.getNextSentMessage(0, 2);
		assertNotNull("No command8", command);

		assertEquals("Incorrect Header8", DIRECT_COMMAND_WITH_REPLY, command[0]);
		assertEquals("Wrong command byte8", CommandByte.LS_GET_STATUS.getByte(), command[1]);
		assertEquals("Wrong port8", PORT_NR_3, command[2]);
		assertEquals("Wrong command length8", 3, command.length);

		command = logger.getNextSentMessage(0, 2);
		assertNotNull("No command9a", command);

		while(command[1] == CommandByte.LS_GET_STATUS.getByte()) {
			command = logger.getNextSentMessage(0, 2);
			assertNotNull("No command9b", command);
		}

		assertEquals("Incorrect Header9", DIRECT_COMMAND_WITH_REPLY, command[0]);
		assertEquals("Wrong command byte9", CommandByte.LS_READ.getByte(), command[1]);
		assertEquals("Wrong port9", PORT_NR_3, command[2]);
		assertEquals("Wrong command length9", 3, command.length);
	}
}
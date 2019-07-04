/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2018 The Catrobat Team
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

import org.catrobat.catroid.common.bluetooth.ConnectionDataLogger;
import org.catrobat.catroid.common.bluetooth.models.MindstormsNXTTestModel;
import org.catrobat.catroid.devices.mindstorms.MindstormsConnection;
import org.catrobat.catroid.devices.mindstorms.MindstormsConnectionImpl;
import org.catrobat.catroid.devices.mindstorms.nxt.CommandByte;
import org.catrobat.catroid.devices.mindstorms.nxt.sensors.NXTLightSensor;
import org.catrobat.catroid.devices.mindstorms.nxt.sensors.NXTLightSensorActive;
import org.catrobat.catroid.devices.mindstorms.nxt.sensors.NXTSensor;
import org.catrobat.catroid.devices.mindstorms.nxt.sensors.NXTSensorMode;
import org.catrobat.catroid.devices.mindstorms.nxt.sensors.NXTSensorType;
import org.catrobat.catroid.devices.mindstorms.nxt.sensors.NXTSoundSensor;
import org.catrobat.catroid.devices.mindstorms.nxt.sensors.NXTTouchSensor;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class SensorTests {

	private static final byte DIRECT_COMMAND_WITHOUT_REPLY = (byte) 0x80;
	private static final byte DIRECT_COMMAND_WITH_REPLY = (byte) 0x00;
	private static final byte PORT_NR_0 = 0;
	private static final byte PORT_NR_1 = 1;
	private static final byte PORT_NR_2 = 2;

	@Parameterized.Parameters(name = "{0}")
	public static Iterable<Object[]> data() {
		return Arrays.asList(new Object[][] {
				{"NXTTouchSensor", NXTTouchSensor.class, NXTSensorType.TOUCH, NXTSensorMode.BOOL, PORT_NR_0, 1},
				{"NXTSoundSensor", NXTSoundSensor.class, NXTSensorType.SOUND_DBA, NXTSensorMode.Percent, PORT_NR_1, 42},
				{"NXTLightSensor", NXTLightSensor.class, NXTSensorType.LIGHT_INACTIVE, NXTSensorMode.Percent, PORT_NR_2, 24},
				{"NXTLightSensorActive", NXTLightSensorActive.class, NXTSensorType.LIGHT_ACTIVE, NXTSensorMode.Percent, PORT_NR_2, 33}
		});
	}

	@Parameterized.Parameter
	public String name;

	@Parameterized.Parameter(1)
	public Class<NXTSensor> nxtSensorClass;

	@Parameterized.Parameter(2)
	public NXTSensorType nxtSensorType;

	@Parameterized.Parameter(3)
	public NXTSensorMode nxtSensorMode;

	@Parameterized.Parameter(4)
	public byte portNumber;

	@Parameterized.Parameter(5)
	public int expectedSensorValue;

	private ConnectionDataLogger logger;
	private MindstormsConnection mindstormsConnection;
	private NXTSensor nxtSensor;

	@Before
	public void setUp() throws Exception {
		MindstormsNXTTestModel nxtModel = new MindstormsNXTTestModel();
		logger = ConnectionDataLogger.createLocalConnectionLoggerWithDeviceModel(nxtModel);
		mindstormsConnection = new MindstormsConnectionImpl(logger.getConnectionProxy());
		mindstormsConnection.init();
		nxtSensor = sensorFactoryForTest();
		nxtModel.setSensorValue(expectedSensorValue);
	}

	@After
	public void tearDown() throws Exception {
		mindstormsConnection.disconnect();
		logger.disconnectAndDestroy();
	}

	@Test
	public void testSensorValue() {
		int sensorValue = (int) nxtSensor.getValue();
		assertEquals(expectedSensorValue, sensorValue);
	}

	@Test
	public void testSetInputModeMessageBeginInitialisation() {
		nxtSensor.getValue();
		byte[] message = logger.getNextSentMessage(0, 2);

		assertArrayEquals(new byte[] {DIRECT_COMMAND_WITH_REPLY, CommandByte.SET_INPUT_MODE.getByte(), portNumber,
						nxtSensorType.getByte(), nxtSensorMode.getByte()},
				message);
	}

	@Test
	public void testResetInputScaledValueMessage() {
		nxtSensor.getValue();
		byte[] message = logger.getNextSentMessage(1, 2);

		assertArrayEquals(new byte[] {DIRECT_COMMAND_WITHOUT_REPLY, CommandByte.RESET_INPUT_SCALED_VALUE.getByte(), portNumber},
				message);
	}

	@Test
	public void testSetInputModeMessageEndInitialisation() {
		nxtSensor.getValue();
		byte[] message = logger.getNextSentMessage(2, 2);

		assertArrayEquals(new byte[] {DIRECT_COMMAND_WITH_REPLY, CommandByte.SET_INPUT_MODE.getByte(), portNumber,
						nxtSensorType.getByte(), nxtSensorMode.getByte()},
				message);
	}

	@Test
	public void testGetInputValuesMessage() {
		nxtSensor.getValue();
		byte[] message = logger.getNextSentMessage(3, 2);

		assertArrayEquals(new byte[] {DIRECT_COMMAND_WITH_REPLY, CommandByte.GET_INPUT_VALUES.getByte(), portNumber},
				message);
	}

	private NXTSensor sensorFactoryForTest() {
		String canonicalName = nxtSensorClass.getCanonicalName();
		if (canonicalName.equals(NXTTouchSensor.class.getCanonicalName())) {
			return new NXTTouchSensor(portNumber, mindstormsConnection);
		}
		if (canonicalName.equals(NXTSoundSensor.class.getCanonicalName())) {
			return new NXTSoundSensor(portNumber, mindstormsConnection);
		}
		if (canonicalName.equals(NXTLightSensor.class.getCanonicalName())) {
			return new NXTLightSensor(portNumber, mindstormsConnection);
		}
		if (canonicalName.equals(NXTLightSensorActive.class.getCanonicalName())) {
			return new NXTLightSensorActive(portNumber, mindstormsConnection);
		}
		return null;
	}

}

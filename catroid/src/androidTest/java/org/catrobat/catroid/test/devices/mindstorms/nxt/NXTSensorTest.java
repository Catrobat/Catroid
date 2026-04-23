/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2022 The Catrobat Team
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

import org.catrobat.catroid.devices.mindstorms.MindstormsConnection;
import org.catrobat.catroid.devices.mindstorms.MindstormsException;
import org.catrobat.catroid.devices.mindstorms.nxt.Command;
import org.catrobat.catroid.devices.mindstorms.nxt.CommandByte;
import org.catrobat.catroid.devices.mindstorms.nxt.CommandType;
import org.catrobat.catroid.devices.mindstorms.nxt.NXTException;
import org.catrobat.catroid.devices.mindstorms.nxt.sensors.NXTLightSensor;
import org.catrobat.catroid.devices.mindstorms.nxt.sensors.NXTLightSensorActive;
import org.catrobat.catroid.devices.mindstorms.nxt.sensors.NXTSensor;
import org.catrobat.catroid.devices.mindstorms.nxt.sensors.NXTSensorMode;
import org.catrobat.catroid.devices.mindstorms.nxt.sensors.NXTSensorType;
import org.catrobat.catroid.devices.mindstorms.nxt.sensors.NXTSoundSensor;
import org.catrobat.catroid.devices.mindstorms.nxt.sensors.NXTTouchSensor;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;

import static org.catrobat.catroid.devices.mindstorms.nxt.CommandByte.LS_READ;
import static org.catrobat.catroid.devices.mindstorms.nxt.CommandType.REPLY_COMMAND;
import static org.catrobat.catroid.devices.mindstorms.nxt.NXTReply.INSUFFICIENT_REPLY_LENGTH_EXCEPTION_MESSAGE;
import static org.catrobat.catroid.devices.mindstorms.nxt.NXTReply.INVALID_COMMAND_BYTE_EXCEPTION_MESSAGE;
import static org.catrobat.catroid.devices.mindstorms.nxt.NXTReply.INVALID_FIRST_BYTE_EXCEPTION_MESSAGE;
import static org.catrobat.catroid.devices.mindstorms.nxt.NXTReply.NO_ERROR;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@RunWith(Parameterized.class)
public class NXTSensorTest {

	@Rule
	public final ExpectedException exception = ExpectedException.none();

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
	public NXTSensorType sensorType;

	@Parameterized.Parameter(3)
	public NXTSensorMode sensorMode;

	@Parameterized.Parameter(4)
	public byte port;

	@Parameterized.Parameter(5)
	public int expectedSensorValue;

	private MindstormsConnection mindstormsConnection;
	private NXTSensor nxtSensor;

	@Before
	public void setUp() throws Exception {
		mindstormsConnection = mock(MindstormsConnection.class);
		when(mindstormsConnection.isConnected()).thenReturn(true);
		nxtSensor = nxtSensorClass.getConstructor(int.class, MindstormsConnection.class).newInstance(port, mindstormsConnection);
		nxtSensor.hasInit = true;
	}

	@Test
	public void testSensorGetValueInvalidFirstByteException() throws MindstormsException {
		when(mindstormsConnection.sendAndReceive(any())).thenReturn(new byte[]{
				0, LS_READ.getByte(), NO_ERROR, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
		exception.expect(NXTException.class);
		exception.expectMessage(INVALID_FIRST_BYTE_EXCEPTION_MESSAGE);
		nxtSensor.getValue();
	}

	@Test
	public void testSensorGetValueInvalidCommandByteException() throws MindstormsException {
		when(mindstormsConnection.sendAndReceive(any())).thenReturn(new byte[]{
				REPLY_COMMAND.getByte(), 0, NO_ERROR, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
		exception.expect(NXTException.class);
		exception.expectMessage(INVALID_COMMAND_BYTE_EXCEPTION_MESSAGE);
		nxtSensor.getValue();
	}

	@Test
	public void testSensorGetValueInvalidReplyLengthException() throws MindstormsException {
		when(mindstormsConnection.sendAndReceive(any())).thenReturn(new byte[]{});
		exception.expect(NXTException.class);
		exception.expectMessage(INSUFFICIENT_REPLY_LENGTH_EXCEPTION_MESSAGE);
		nxtSensor.getValue();
	}

	@Test
	public void testGetValueLeastSignificantByte() throws MindstormsException {
		byte expectedNormalizedValue = 1;
		byte expectedRawValue = 2;
		byte expectedScaledValue = 3;
		when(mindstormsConnection.sendAndReceive(any())).thenReturn(new byte[]{
				REPLY_COMMAND.getByte(), LS_READ.getByte(), NO_ERROR, 0, 0, 0, 0, 0,
				expectedRawValue, 0, expectedNormalizedValue, 0, expectedScaledValue, 0,
				0, 0});
		assertEquals(expectedNormalizedValue, nxtSensor.getSensorReadings().normalized);
		assertEquals(expectedRawValue, nxtSensor.getSensorReadings().raw);
		assertEquals(expectedScaledValue, nxtSensor.getSensorReadings().scaled);
		assertEquals((Float) (float) expectedScaledValue, (Float) nxtSensor.getValue());
	}

	@Test
	public void testGetValueMostSignificantByte() throws MindstormsException {
		byte expectedNormalizedValue = 1;
		byte expectedRawValue = 2;
		byte expectedScaledValue = 3;
		when(mindstormsConnection.sendAndReceive(any())).thenReturn(new byte[]{
				REPLY_COMMAND.getByte(), LS_READ.getByte(), NO_ERROR, 0, 0, 0, 0, 0,
				0, expectedRawValue, 0, expectedNormalizedValue, 0, expectedScaledValue,
				0, 0});
		assertEquals(256 * expectedNormalizedValue, nxtSensor.getSensorReadings().normalized);
		assertEquals(256 * expectedRawValue, nxtSensor.getSensorReadings().raw);
		assertEquals(256 * expectedScaledValue, nxtSensor.getSensorReadings().scaled);
		assertEquals((Float) (256f * expectedScaledValue), (Float) nxtSensor.getValue());
	}

	@Test
	public void testResetScaledValue() throws MindstormsException {
		Command command = new Command(CommandType.DIRECT_COMMAND, CommandByte.RESET_INPUT_SCALED_VALUE, false);
		command.append(port);
		nxtSensor.resetScaledValue();
		verify(mindstormsConnection, times(1)).send(eq(command));
	}

	@Test
	public void testUpdateTypeAndMode() throws MindstormsException {
		when(mindstormsConnection.sendAndReceive(any())).thenReturn(new byte[]{REPLY_COMMAND.getByte(),
				LS_READ.getByte(), NO_ERROR});

		Command command = new Command(CommandType.DIRECT_COMMAND, CommandByte.SET_INPUT_MODE, true);
		command.append(port);
		command.append(sensorType.getByte());
		command.append(sensorMode.getByte());
		nxtSensor.updateTypeAndMode();
		verify(mindstormsConnection, times(1)).sendAndReceive(eq(command));
	}
}

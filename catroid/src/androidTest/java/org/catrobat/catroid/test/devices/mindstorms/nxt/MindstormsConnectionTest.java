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

import org.catrobat.catroid.devices.mindstorms.MindstormsConnectionImpl;
import org.catrobat.catroid.devices.mindstorms.nxt.Command;
import org.catrobat.catroid.devices.mindstorms.nxt.CommandByte;
import org.catrobat.catroid.devices.mindstorms.nxt.CommandType;
import org.catrobat.catroid.test.utils.Reflection;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import static junit.framework.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class MindstormsConnectionTest {

	public static final int HEADER_SIZE = 2;

	@Test
	public void testSend() throws Exception {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();

		MindstormsConnectionImpl connection = new MindstormsConnectionImpl(null);
		Reflection.setPrivateField(connection, "legoOutputStream", outStream);

		Command command = new Command(CommandType.DIRECT_COMMAND, CommandByte.SET_OUTPUT_STATE, false);
		command.append((byte) 0x1);
		command.append((byte) 0x2);
		command.append((byte) 0x3);

		connection.send(command);

		byte[] sentBytes = outStream.toByteArray();

		byte[] expectedMessage = command.getRawCommand();

		assertEquals(expectedMessage.length + HEADER_SIZE, sentBytes.length);
		assertEquals((byte) expectedMessage.length, sentBytes[0]);
		assertEquals((byte) (expectedMessage.length >> 8), sentBytes[1]);

		for (int i = 0; i < expectedMessage.length; i++) {
			assertEquals(expectedMessage[i], sentBytes[i + HEADER_SIZE]);
		}
	}

	@Test
	public void testSendAndReceive() throws Exception {

		byte[] inputBuffer = new byte[] {4, 0, 3, 4, 5, 7};
		ByteArrayInputStream inStream = new ByteArrayInputStream(inputBuffer);
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();

		MindstormsConnectionImpl connection = new MindstormsConnectionImpl(null);
		Reflection.setPrivateField(connection, "legoOutputStream", outStream);
		Reflection.setPrivateField(connection, "legoInputStream", new DataInputStream(inStream));

		Command command = new Command(CommandType.DIRECT_COMMAND, CommandByte.SET_OUTPUT_STATE, false);
		command.append((byte) 0x1);
		command.append((byte) 0x2);
		command.append((byte) 0x3);

		byte[] receivedBytes = connection.sendAndReceive(command);

		byte[] sentBytes = outStream.toByteArray();

		byte[] expectedMessage = command.getRawCommand();

		assertEquals(expectedMessage.length + HEADER_SIZE, sentBytes.length);
		assertEquals(expectedMessage.length, sentBytes[0]);
		assertEquals((byte) (expectedMessage.length >> 8), sentBytes[1]);

		for (int i = 0; i < expectedMessage.length; i++) {
			assertEquals(expectedMessage[i], sentBytes[i + HEADER_SIZE]);
		}

		assertEquals(inputBuffer.length - HEADER_SIZE, receivedBytes.length);

		for (int i = 0; i < receivedBytes.length; i++) {
			assertEquals(inputBuffer[i + HEADER_SIZE], receivedBytes[i]);
		}
	}
}

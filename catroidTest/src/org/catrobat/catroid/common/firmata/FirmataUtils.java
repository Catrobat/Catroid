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

package org.catrobat.catroid.common.firmata;

import junit.framework.Assert;

import org.catrobat.catroid.common.bluetooth.ConnectionDataLogger;

import java.nio.ByteBuffer;

import name.antonsmirnov.firmata.BytesHelper;

public final class FirmataUtils {

	private static final int SET_PIN_MODE_COMMAND = 0xF4;
	private static final int REPORT_ANALOG_PIN_COMMAND = 0xC0;
	private static final int ANALOG_MESSAGE_COMMAND = 0xE0;

	private final ConnectionDataLogger logger;

	public static final int ANALOG_MESSAGE_SIZE = 3;
	public static final int SET_PIN_MODE_MESSAGE_SIZE = 3;
	public static final int REPORT_ANALOG_PIN_MESSAGE_SIZE = 2;

	public FirmataUtils(ConnectionDataLogger logger) {

		this.logger = logger;
	}

	public FirmataMessage getAnalogMesageData() {

		int pinAndCommand = getNextMessage();

		int pin = getPinFromHeader(pinAndCommand);
		int command = getCommandFromHeader(pinAndCommand);

		Assert.assertEquals("This is no analog message command", ANALOG_MESSAGE_COMMAND, command);

		int lsb = getNextMessage();
		int msb = getNextMessage();

		int data = BytesHelper.DECODE_BYTE(lsb, msb);

		return new FirmataMessage(command, pin, data);
	}

	public FirmataMessage getSetPinModeMessage() {
		int command = getNextMessage();
		int pin = getNextMessage();
		int mode = getNextMessage();

		Assert.assertEquals("No set pin mode message", SET_PIN_MODE_COMMAND, command);

		return new FirmataMessage(command, pin, mode);
	}

	public FirmataMessage getReportAnalogPinMessage() {

		int pinAndCommand = getNextMessage();

		int pin = getPinFromHeader(pinAndCommand);
		int command = getCommandFromHeader(pinAndCommand);

		Assert.assertEquals("No report analog pin message", REPORT_ANALOG_PIN_COMMAND, command);

		int data = getNextMessage();

		return new FirmataMessage(command, pin, data);
	}

	public int getNextMessage() {
		byte[] message = logger.getNextSentMessage();
		Assert.assertNotNull("There is no message", message);
		ByteBuffer bb = ByteBuffer.wrap(message);
		return bb.getInt();
	}

	private int getPinFromHeader(int header) {
		return header & 15;
	}

	private int getCommandFromHeader(int header) {
		return header & 240;
	}
}

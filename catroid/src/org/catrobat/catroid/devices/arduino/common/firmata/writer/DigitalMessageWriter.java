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

package org.catrobat.catroid.devices.arduino.common.firmata.writer;

import android.util.Log;

import org.catrobat.catroid.devices.arduino.common.firmata.FormatHelper;
import org.catrobat.catroid.devices.arduino.common.firmata.message.DigitalMessage;
import org.catrobat.catroid.devices.arduino.common.firmata.serial.ISerial;
import org.catrobat.catroid.devices.arduino.common.firmata.serial.SerialException;

import static org.catrobat.catroid.devices.arduino.common.firmata.BytesHelper.encodeChannel;
import static org.catrobat.catroid.devices.arduino.common.firmata.BytesHelper.lsb;
import static org.catrobat.catroid.devices.arduino.common.firmata.BytesHelper.msb;

/**
 * MessageWriter for DigitalMessage
 */
public class DigitalMessageWriter implements IMessageWriter<DigitalMessage> {

    public static final int COMMAND = 0x90;

    public void write(DigitalMessage message, ISerial serial) throws SerialException {
//        serial.write(COMMAND | encodeChannel(message.getPort()));
//        serial.write(lsb(message.getValue()));
//        serial.write(msb(message.getValue()));

		int portNumber = (message.getPort() >> 3) & 0x0F;
		int out;
		if (message.getValue() == 0) {
			out = 0 & ~(1 << (message.getPort() & 0x07));
		}
		else {
			out = (1 << (message.getPort() & 0x07));
		}

		serial.write(COMMAND | portNumber);
		serial.write(out & 0x7F);
		serial.write(out >> 7);

		FormatHelper f = new FormatHelper();

		Log.d("DigitalMessag Writer", f.formatBinary(COMMAND | portNumber));
		Log.d("DigitalMessag Writer", f.formatBinary(out & 0x7F));
		Log.d("DigitalMessag Writer", f.formatBinary(out >> 7));

		Log.d("DigitalMessag Writer", f.formatBinary(COMMAND | encodeChannel(message.getPort())));
		Log.d("DigitalMessag Writer", f.formatBinary(lsb(message.getValue())));
		Log.d("DigitalMessag Writer", f.formatBinary(msb(message.getValue())));
    }
}

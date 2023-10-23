/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2023 The Catrobat Team
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

package org.catrobat.catroid.firmata.Firmata.writer;

import org.catrobat.catroid.firmata.Firmata.message.ServoConfigMessage;
import org.catrobat.catroid.firmata.Serial.ISerial;
import org.catrobat.catroid.firmata.Serial.SerialException;

import static org.catrobat.catroid.firmata.Firmata.BytesHelper.LSB;
import static org.catrobat.catroid.firmata.Firmata.BytesHelper.MSB;

/**
 * MessageWriter for ServoConfigMessage
 */
public class ServoConfigMessageWriter extends SysexMessageWriter<ServoConfigMessage> {

    @Override
    protected void writeData(ServoConfigMessage message, ISerial serial) throws SerialException {
        // can not use super.writeData() because it works with String
        writeServoData(message, serial);
    }

    private void writeServoData(ServoConfigMessage message, ISerial serial) throws SerialException {
        byte[] buffer = new byte[7];
        buffer[0] = (byte)message.getPin();

        buffer[1] = (byte)LSB(message.getMinPulse());
        buffer[2] = (byte)MSB(message.getMinPulse());

        buffer[3] = (byte)LSB(message.getMaxPulse());
        buffer[4] = (byte)MSB(message.getMaxPulse());

        buffer[5] = (byte)LSB(message.getAngle());
        buffer[6] = (byte)MSB(message.getAngle());

        serial.write(buffer);
    }
}

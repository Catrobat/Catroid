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

package org.catrobat.catroid.devices.arduino.common.firmata.writer;


import org.catrobat.catroid.devices.arduino.common.firmata.message.I2cRequestMessage;
import org.catrobat.catroid.devices.arduino.common.firmata.serial.ISerial;
import org.catrobat.catroid.devices.arduino.common.firmata.serial.SerialException;

import static org.catrobat.catroid.devices.arduino.common.firmata.BytesHelper.encodeIntArray;
import static org.catrobat.catroid.devices.arduino.common.firmata.BytesHelper.lsb;
import static org.catrobat.catroid.devices.arduino.common.firmata.BytesHelper.msb;
import static org.catrobat.catroid.devices.arduino.common.firmata.BytesHelper.setBit;

/**
 * MessageWriter for I2cRequestMessage
 */
public class I2cRequestMessageWriter<ConcreteRequestMessage extends I2cRequestMessage> extends SysexMessageWriter<ConcreteRequestMessage> {

    @Override
    protected void writeData(ConcreteRequestMessage message, ISerial serial) throws SerialException {
        // can not use super.writeData() because it works with String
        writeI2cRequestData(message, serial);
    }

    private void writeI2cRequestData(ConcreteRequestMessage message, ISerial serial) throws SerialException {
        byte[] buffer = new byte[2];

        buffer[0] = (byte) lsb(message.getSlaveAddress());
        int modeByte = 0;

        modeByte = setBit(modeByte, 7, false); // {7: always 0}
        modeByte = setBit(modeByte, 6, false); // {6: reserved}
        modeByte = setBit(modeByte, 5, message.isTenBitsMode()); // {5: address mode, 1 means 10-bit mode}

        // 4-3 bits are modes
        modeByte |= (byte)(message.getMode().getValue() << 3);  // {4-3: read/write, 00 => write, 01 => read once, 10 => read continuously, 11 => stop reading}

        // 0-2 bits
        if (message.isTenBitsMode()) {
			modeByte |= msb(message.getSlaveAddress() & 7); // {2-0: slave address msb in 10-bit mode, not used in 7-bit mode}
		}

        buffer[1] = (byte)modeByte;
        serial.write(buffer);

        int[] binaryData = message.getBinaryData();
        if (binaryData != null) {
            byte[] dataBuffer = encodeIntArray(binaryData);
            serial.write(dataBuffer);
        }
    }
}

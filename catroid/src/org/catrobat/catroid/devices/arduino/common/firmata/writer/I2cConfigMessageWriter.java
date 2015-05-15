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


import org.catrobat.catroid.devices.arduino.common.firmata.message.I2cConfigMessage;
import org.catrobat.catroid.devices.arduino.common.firmata.serial.ISerial;
import org.catrobat.catroid.devices.arduino.common.firmata.serial.SerialException;

import static org.catrobat.catroid.devices.arduino.common.firmata.BytesHelper.lsb;
import static org.catrobat.catroid.devices.arduino.common.firmata.BytesHelper.msb;

/**
 * MessageWriter for I2cConfigMessage
 */
public class I2cConfigMessageWriter extends SysexMessageWriter<I2cConfigMessage> {

    @Override
    protected void writeData(I2cConfigMessage message, ISerial serial) throws SerialException {
        // can not use super.writeData() because it works with String
        writeI2cConfigData(message, serial);
    }

    private void writeI2cConfigData(I2cConfigMessage message, ISerial serial) throws SerialException {
        byte[] buffer = new byte[3];

        buffer[0] = (byte)(message.isOn() ? 1 : 0);
        buffer[1] = (byte) lsb(message.getDelay());
        buffer[2] = (byte) msb(message.getDelay());

        serial.write(buffer);
    }
}

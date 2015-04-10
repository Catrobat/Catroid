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

package org.catrobat.catroid.devices.arduino.common.firmata.message;

import org.catrobat.catroid.devices.arduino.common.firmata.FormatHelper;

import java.text.MessageFormat;
import java.util.Arrays;

/**
 * I2C reply message
 */
public class I2cReplyMessage extends SysexMessage {

    // Sysex command byte
    public static final int COMMAND = 0x77;

    public I2cReplyMessage() {
        super(COMMAND, null);
    }

    public I2cReplyMessage(int slaveAddress, int register, int[] binaryData) {
        this();
        setSlaveAddress(slaveAddress);
        setRegister(register);
        setBinaryData(binaryData);
    }

    private int slaveAddress;

    public int getSlaveAddress() {
        return slaveAddress;
    }

    public void setSlaveAddress(int slaveAddress) {
        this.slaveAddress = slaveAddress;
    }
    
    private int register;

    public int getRegister() {
        return register;
    }

    public void setRegister(int register) {
        this.register = register;
    }

    private int[] binaryData;

    public int[] getBinaryData() {
        return binaryData.clone();
    }

    public void setBinaryData(int[] binaryData) {
        this.binaryData = binaryData.clone();
    }

	@Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) {
			return false;
		}

        I2cReplyMessage message = (I2cReplyMessage)obj;
        return message != null &&
                message.getSlaveAddress() == getSlaveAddress() &&
                message.getRegister() == getRegister() &&
                (
                    (message.getBinaryData() == null && getBinaryData() == null)
                    ||
                    (Arrays.equals(message.getBinaryData(), getBinaryData()))
                );
    }

    protected static FormatHelper formatHelper = new FormatHelper();

    @Override
    public String toString() {
        return MessageFormat.format("I2cReplyMessage[slaveAddress={0}, register={1}, binaryData={2}]",
            formatHelper.formatBinary(slaveAddress), register, formatHelper.formatBinaryData(binaryData));
    }
}

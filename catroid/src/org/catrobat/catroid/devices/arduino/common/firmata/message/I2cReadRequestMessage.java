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

import java.text.MessageFormat;

/**
 * I2C request message to read data
 */
public class I2cReadRequestMessage extends I2cRequestMessage {

    private Integer slaveRegister;

    public Integer getSlaveRegister() {
        return slaveRegister;
    }

    public void setSlaveRegister(Integer slaveRegister) {
        this.slaveRegister = slaveRegister;
    }

    private Integer bytesToRead;

    public Integer getBytesToRead() {
        return bytesToRead;
    }

    public void setBytesToRead(Integer bytesToRead) {
        this.bytesToRead = bytesToRead;
    }

    @Override
    public int[] getBinaryData() {
        validateBytesToRead();

        MODE mode = getMode();
        switch (mode) {
            case READ_ONCE:
                return getReadOnlyBinaryData();

            case READ_CONTINUOUSLY:
                return getReadContinuouslyBinaryData();

            default:
                throw new RuntimeException("Mode should be ReadOnce or ReadContinuously");
        }
    }

    private void validateBytesToRead() {
        if (bytesToRead == null) {
			throw new RuntimeException("bytesToRead field is not specified and is required");
		}
    }

    private int[] getReadOnlyBinaryData() {
        return slaveRegister != null
            ? new int[] { slaveRegister, bytesToRead }
            : new int[] { bytesToRead };
    }

    private int[] getReadContinuouslyBinaryData() {
        validateSlaveRegister();
        validateBytesToRead();

        return new int[] { slaveRegister, bytesToRead };
    }

    private void validateSlaveRegister() {
        if (slaveRegister == null) {
			throw new RuntimeException("slaveRegister is not specified and in required in ReadContinuously mode");
		}
    }

    @Override
    public String toString() {
        return MessageFormat.format("I2cReadRequestMessage[slaveAddress={0}, 10bitsMode={1}, mode={2}, slaveReg={3}, bytesToRead={4}]",
            formatHelper.formatBinary(getSlaveAddress()), isTenBitsMode(), getMode(),
            (slaveRegister != null ? formatHelper.formatBinary(slaveRegister) : "null"),
            bytesToRead);
    }
}

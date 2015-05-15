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

package org.catrobat.catroid.devices.arduino.common.firmata.message;

import org.catrobat.catroid.devices.arduino.common.firmata.FormatHelper;

import java.text.MessageFormat;

/**
 * I2C request message
 */
public class I2cRequestMessage extends SysexMessage {

    // Sysex command byte
    public static final int COMMAND = 0x76;

    public I2cRequestMessage() {
        super(COMMAND, null);
    }

    private int slaveAddress;

    public int getSlaveAddress() {
        return slaveAddress;
    }

    public void setSlaveAddress(int slaveAddress) {
        this.slaveAddress = slaveAddress;
    }

    private boolean tenBitsMode;

    public boolean isTenBitsMode() {
        return tenBitsMode;
    }

    public void setTenBitsMode(boolean tenBitsMode) {
        this.tenBitsMode = tenBitsMode;
    }

    /**
     * I2C mode
     */
    public enum MODE {

        /**
         * Write mode
         */
        WRITE(0),

        /**
         * Read once mode
         */
        READ_ONCE(1),

        /**
         * Read continuously mode
         */
        READ_CONTINUOUSLY(2),

        /**
         * Stop reading mode
         */
        STOP_READING(3);

        private int value;

        private void setValue(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }


        private MODE(int value) {
            setValue(value);
        }
    }

    private MODE mode;

    public MODE getMode() {
        return mode;
    }

    public void setMode(MODE mode) {
        this.mode = mode;
    }
    
    private int[] binaryData;

    public int[] getBinaryData() {
        return binaryData.clone();
    }

    protected static FormatHelper formatHelper = new FormatHelper();

    public void setBinaryData(int[] binaryData) {
        this.binaryData = binaryData.clone();
    }

    @Override
    public String toString() {
        return MessageFormat.format("I2cRequestMessage[slaveAddress={0}, 10bitsMode={1}, mode={2}, binaryData={3}]",
            formatHelper.formatBinary(slaveAddress), tenBitsMode, mode, (binaryData != null ? formatHelper.formatBinaryData(binaryData) : "null"));
    }
}

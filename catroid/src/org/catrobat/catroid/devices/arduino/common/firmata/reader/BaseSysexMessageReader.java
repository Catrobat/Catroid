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

package org.catrobat.catroid.devices.arduino.common.firmata.reader;


import org.catrobat.catroid.devices.arduino.common.firmata.message.SysexMessage;
import org.catrobat.catroid.devices.arduino.common.firmata.writer.SysexMessageWriter;

import static org.catrobat.catroid.devices.arduino.common.firmata.BytesHelper.decodeIntArray;
import static org.catrobat.catroid.devices.arduino.common.firmata.BytesHelper.decodeString;

/**
 * Base MessageReader for SysexMessage
 */
public abstract class BaseSysexMessageReader<ConcreteSysexMessage extends SysexMessage>
        implements IMessageReader<ConcreteSysexMessage> {

    private Byte sysexCommand;
    
    public BaseSysexMessageReader(Byte sysexCommand) {
        this.sysexCommand = sysexCommand;
    }

    public boolean canRead(byte[] buffer, int bufferLength, int command) {
        return (bufferLength == 1 && buffer[0] == (byte) SysexMessageWriter.COMMAND_START)  // is sysex message?
                ||
               (bufferLength == 2 && (sysexCommand == null || sysexCommand.equals(buffer[1]))) // is needed sysex command
                ||
               (bufferLength == 3 && sysexCommand != null);
    }

    protected boolean isReading;

    public void startReading() {
        isReading = true;
    }

    protected ConcreteSysexMessage message;

    public void read(byte[] buffer, int length) {
        byte incomingByte = buffer[length-1];

        if (incomingByte == (byte) SysexMessageWriter.COMMAND_END) {
            isReading = false;

            message = buildSysexMessage(buffer, length);
        }
    }

    /**
     * Build SysexMessage from incoming buffer
     *
     * @param buffer buffer (start from COMMAND_START byte, ends with COMMAND_END byte)
     * @param bufferLength buffer length
     * @return SysexMessage command or inherited message
     */
    protected abstract ConcreteSysexMessage buildSysexMessage(byte[] buffer, int bufferLength);

    public ConcreteSysexMessage getMessage() {
        return message;
    }

    public boolean finishedReading() {
        return !isReading;
    }

    protected void validateSysexDataLength(int startIndex, int endIndex) {
        if ((endIndex - startIndex + 1) % 2 != 0) {
			throw new RuntimeException("Sysex command data length should be even");
		}
    }

    // extract string from buffer
    protected String extractStringFromBuffer(byte[] buffer, int startIndex, int endIndex) {
        validateSysexDataLength(startIndex, endIndex);
        return decodeString(buffer, startIndex, endIndex);
    }

    // extract integer array from buffer
    protected int[] extractIntArrayFromBuffer(byte[] buffer, int startIndex, int endIndex) {
        validateSysexDataLength(startIndex, endIndex);
        return decodeIntArray(buffer, startIndex, endIndex);
    }
}

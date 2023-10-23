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

package org.catrobat.catroid.firmata.Firmata.reader;

import org.catrobat.catroid.firmata.Firmata.IFirmata;
import org.catrobat.catroid.firmata.Firmata.message.ProtocolVersionMessage;
import org.catrobat.catroid.firmata.Firmata.writer.ReportProtocolVersionMessageWriter;

/**
 * MessageReader for ProtocolVersionMessage
 */
public class ProtocolVersionMessageReader implements IMessageReader<ProtocolVersionMessage> {

    public boolean canRead(byte[] buffer, int bufferLength, int command) {
        return bufferLength == 1
               &&
               buffer[0] == (byte) ReportProtocolVersionMessageWriter.COMMAND;
    }

    private boolean isHandling;

    public void startReading() {
        isHandling = true;
        message = new ProtocolVersionMessage();
    }

    public void read(byte[] buffer, int length) {
        byte incomingByte = buffer[length-1];
        if (length == 2) {
            message.setMajor(incomingByte);
        } else {
            message.setMinor(incomingByte);
            isHandling = false;
        }
    }

    public boolean finishedReading() {
        return !isHandling;
    }

    private ProtocolVersionMessage message;

    public ProtocolVersionMessage getMessage() {
        return message;
    }

    public void fireEvent(IFirmata.Listener listener) {
        listener.onProtocolVersionMessageReceived(getMessage());
    }
}

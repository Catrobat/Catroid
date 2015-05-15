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

import org.catrobat.catroid.devices.arduino.common.firmata.IFirmata;
import org.catrobat.catroid.devices.arduino.common.firmata.message.DigitalMessage;
import org.catrobat.catroid.devices.arduino.common.firmata.writer.DigitalMessageWriter;

import static org.catrobat.catroid.devices.arduino.common.firmata.BytesHelper.decodeByte;
import static org.catrobat.catroid.devices.arduino.common.firmata.BytesHelper.decodeChannel;

/**
 * MessageReader for DigitalMessage
 */
public class DigitalMessageReader implements IMessageReader<DigitalMessage> {

    public boolean canRead(byte[] buffer, int bufferLength, int command) {
        return command == DigitalMessageWriter.COMMAND;
    }

    private boolean isHandling;

    public void startReading() {
        isHandling = true;
        message = new DigitalMessage();
    }

    public void read(byte[] buffer, int length) {
        if (length == 2) {
            message.setPort(decodeChannel(buffer[0]));
        } else {
            message.setValue(decodeByte(buffer[1], buffer[2]));
            isHandling = false;
        }
    }

    public boolean finishedReading() {
        return !isHandling;
    }

    private DigitalMessage message;

    public DigitalMessage getMessage() {
        return message;
    }

    public void fireEvent(IFirmata.Listener listener) {
        listener.onDigitalMessageReceived(getMessage());
    }
}

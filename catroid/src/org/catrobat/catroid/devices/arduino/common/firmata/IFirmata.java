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

package org.catrobat.catroid.devices.arduino.common.firmata;

import org.catrobat.catroid.devices.arduino.common.firmata.message.AnalogMessage;
import org.catrobat.catroid.devices.arduino.common.firmata.message.DigitalMessage;
import org.catrobat.catroid.devices.arduino.common.firmata.message.FirmwareVersionMessage;
import org.catrobat.catroid.devices.arduino.common.firmata.message.I2cReplyMessage;
import org.catrobat.catroid.devices.arduino.common.firmata.message.Message;
import org.catrobat.catroid.devices.arduino.common.firmata.message.ProtocolVersionMessage;
import org.catrobat.catroid.devices.arduino.common.firmata.message.StringSysexMessage;
import org.catrobat.catroid.devices.arduino.common.firmata.message.SysexMessage;
import org.catrobat.catroid.devices.arduino.common.firmata.serial.SerialException;

/**
 * Firmata interface
 */
public interface IFirmata {

    /**
     * Listener for incoming messages from Arduino board
     */
    interface Listener {

        /**
         * AnalogMessage received event
         * @param message
         */
        void onAnalogMessageReceived(AnalogMessage message);

        /**
         * DigitalMessage received event
         * @param message
         */
        void onDigitalMessageReceived(DigitalMessage message);

        /**
         * FirmwareVersionMessage received event
         * @param message
         */
        void onFirmwareVersionMessageReceived(FirmwareVersionMessage message);

        /**
         *  ProtocolVersionMessage received event
         * @param message
         */
        void onProtocolVersionMessageReceived(ProtocolVersionMessage message);

        /**
         * SysexMessage received (NOT KNOWN SysexMessage inherited commands like ReportFirmwareVersionMessage, StringSysexMessage, etc)
         *
         * @param message
         */
        void onSysexMessageReceived(SysexMessage message);

        /**
         * StringSysexMessage received event
         */
        void onStringSysexMessageReceived(StringSysexMessage message);

        /**
         * ReportI2cMessageReceived received event
         */
        void onI2cMessageReceived(I2cReplyMessage message);

        /**
         * Unknown byte received (no active MessageReader)
         * @param byteValue
         */
        void onUnknownByteReceived(int byteValue);
    }

    /**
     * Listener stub
     */
    class StubListener implements Listener {
        public void onAnalogMessageReceived(AnalogMessage message) {}
        public void onDigitalMessageReceived(DigitalMessage message) {}
        public void onFirmwareVersionMessageReceived(FirmwareVersionMessage message) {}
        public void onProtocolVersionMessageReceived(ProtocolVersionMessage message) {}
        public void onSysexMessageReceived(SysexMessage message) {}
        public void onStringSysexMessageReceived(StringSysexMessage message) {}
        public void onI2cMessageReceived(I2cReplyMessage message) {}
        public void onUnknownByteReceived(int byteValue) {}
    }

    /**
     * Add listener
     *
     * @param listener listener
     */
    void addListener(Listener listener);

    /**
     * Remove listener
     *
     * @param listener listener
     */
    void removeListener(Listener listener);

    /**
     * Check if it contains listener
     *
     * @param listener listener
     * @return
     */
    boolean containsListener(Listener listener);

    /**
     * Remove all listeners
     */
    void clearListeners();

    /**
     * Send message to Arduino board
     *
     * @param message concrete outcoming message
     */
    void send(Message message) throws SerialException;


    /**
     * Incoming byte received event
     *
     * @param incomingByte incoming byte
     */
    void onDataReceived(int incomingByte);
}

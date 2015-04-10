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

package org.catrobat.catroid.devices.arduino.common.firmata.serial;

/**
 * Serial interface
 * (methods required for Firmata only)
 */
public interface ISerial {

    /**
     * Set serial events listener
     * @param listener serial events listener
     */
    void addListener(ISerialListener listener);

    /**
     * Remove serial events listener
     * @param listener
     */
    void removeListener(ISerialListener listener);

    /**
     * Start talking to serial
     */
    void start() throws SerialException;

    /**
     * Stop talking to serial
     */
    void stop() throws SerialException;

    /**
     * Serial is stopping
     * @return
     */
    boolean isStopping();

    /**
     * Returns the number of bytes that have been read from serial
     * and are waiting to be dealt with by the user.
     */
    int available() throws SerialException;

    /**
     * Clear buffers
     */
    void clear() throws SerialException;

    /**
     * Read byte from serial
     * (check available() before)
     */
    int read() throws SerialException;

    /**
     * Write byte to serial
     * @param outcomingByte outcoming byte
     */
    void write(int outcomingByte) throws SerialException;

    /**
     * Write outcoming bytes to serial
     * @param outcomingBytes bytes to write
     */
    void write(byte[] outcomingBytes) throws SerialException;
}

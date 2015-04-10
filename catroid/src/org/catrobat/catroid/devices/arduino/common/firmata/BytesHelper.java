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

package org.catrobat.catroid.devices.arduino.common.firmata;

import java.nio.ByteBuffer;

/**
 * Helps to prepare bytes data
 */
public final class BytesHelper {

	private BytesHelper() {}

    /**
     * @param channel command channel
     * @return command channel mask
     */
    public static int encodeChannel(int channel) {
        return channel & 0x0F;
    }

    /**
     * Decode command from byte
     *
     * @param incomingByte
     * @return
     */
    public static int decodeCommand(int incomingByte) {
        return incomingByte < 0xF0
                ? incomingByte & 0xF0
                : incomingByte;
    }

    /**
     * Decode channel from byte
     *
     * @param incomingByte
     * @return
     */
    public static int decodeChannel(int incomingByte) {
        return incomingByte & 0x0F;
    }

    /**
     * Return less significant byte
     *
     * @param value value
     * @return less significant byte
     */
    public static int lsb(int value) {
        return value & 0x7F;
    }

    /**
     * Return most significant byte
     *
     * @param value value
     * @return most significant byte
     */
    public static int msb(int value) {
        return (value >> 7) & 0x7F;
    }

    /**
     * Return byte from lsb and msb
     *
     * @param lsb less significant byte
     * @param msb most significant byte
     * @return byte
     */
    public static int decodeByte(int lsb, int msb) {
        return (msb << 7) + lsb;
    }

    /**
     * Decode string that was encoded using lsb(byte), msb(byte)
     *
     * @param buffer  buffer
     * @param startIndex start index
     * @param endIndex end index
     * @return decoded string
     */
    public static String decodeString(byte[] buffer, int startIndex, int endIndex) {
        StringBuilder sb = new StringBuilder();
        int offset = startIndex;
        int length = (endIndex - startIndex + 1) / 2;
        for (int i=0; i < length; i++) {
            sb.append((char) decodeByte(buffer[offset++], buffer[offset++]));
        }
        return sb.toString();
    }

    /**
     * Decode integer array that was encoded using lsb(byte), msb(byte)
     *
     * @param buffer  buffer
     * @param startIndex start index
     * @param endIndex end index
     * @return decoded string
     */
    public static int[] decodeIntArray(byte[] buffer, int startIndex, int endIndex) {
        int offset = startIndex;
        int length = (endIndex - startIndex + 1) / 2;
        int[] intBuffer = new int[length];
        for (int i=0; i < length; i++) {
            intBuffer[i] = decodeByte(buffer[offset++], buffer[offset++]);
        }
        return intBuffer;
    }

    /**
     * Encode string - every byte goes to lsb(byte), msb(byte)
     *
     * @param data string data
     * @return encoded bytes array
     */
    public static byte[] encodeString(String data) {
        byte[] originalData = data.getBytes();
        byte[] encodedData = new byte[originalData.length * 2];
        encodeString(originalData, ByteBuffer.wrap(encodedData), 0);
        return encodedData;
    }

    /**
     * Encode string - every byte goes to lsb(byte), msb(byte)
     *
     * @param data int array data
     * @return encoded bytes array
     */
    public static byte[] encodeIntArray(int[] data) {
        byte[] encodedData = new byte[data.length * 2];
        encodeString(data, ByteBuffer.wrap(encodedData), 0);
        return encodedData;
    }

    /**
     * Encode string to existing buffer - every byte goes to lsb(byte), msb(byte)
     *
     * @param originalData string data
     * @param buffer existing buffer
     * @param offset offset in buffer
     */
    public static void encodeString(byte[] originalData, ByteBuffer buffer, int offset) {
        for (int i=0; i < originalData.length; i++) {
            buffer.put(offset++, (byte) lsb(originalData[i]));
            buffer.put(offset++, (byte) msb(originalData[i]));
        }
    }

    /**
     * Encode string to existing buffer - every byte goes to lsb(byte), msb(byte)
     *
     * @param originalData string data
     * @param buffer existing buffer
     * @param offset offset in buffer
     */
    public static void encodeString(int[] originalData, ByteBuffer buffer, int offset) {
        for (int i=0; i < originalData.length; i++) {
            buffer.put(offset++, (byte) lsb(originalData[i]));
            buffer.put(offset++, (byte) msb(originalData[i]));
        }
    }

    public static final int BITS_IN_BYTE = 8;

    public static final int BYTE_MAX_VALUE = 255;

    /**
     * Get port for pin
     *
     * @param pin
     * @return
     */
    public static int portByPin(int pin) {
        return pin / BITS_IN_BYTE;
    }

    private static int bitMaskHigh(int bit) {
        return 1 << bit;
    }
    
    /**
     * Get mask for port to set pin in HIGH
     *
     * @param pinInPort = [0,7]
     * @return
     */
    private static int pinMaskHigh(int pinInPort) {
        return bitMaskHigh(pinInPort);
    }

    private static int bitMaskLow(int bit) {
        int mask = 0;
        for (int eachBit = BITS_IN_BYTE-1; eachBit >= 0; eachBit--) {
            mask |= (eachBit == bit ? 0 : 1);
            if (eachBit > 0) {
				mask <<= 1;
			}
        }
        return mask;
    }

    /**
     * Get mask for port to set pin in LOW
     *
     * @param pinInPort = [0,7]
     * @return
     */
    private static int pinMaskLow(int pinInPort) {
        return bitMaskLow(pinInPort);
    }

    /**
     * Get pin in port index using absolute pin index
     *
     * @param pin any
     * @return pin = [0,7]
     */
    public static int pinInPort(int pin) {
        return pin % BITS_IN_BYTE;
    }

    /**
     * Set HIGH or LOW pin value for port values
     *
     * @param portValues
     * @param pinInPort = [0, 7]
     * @param highLevel pin level is High level
     * @return
     */
    public static int setPin(int portValues, int pinInPort, boolean highLevel) {
        if (highLevel) {
            return portValues | pinMaskHigh(pinInPort);
        } else {
            return portValues & pinMaskLow(pinInPort);
        }
    }

    public static int setBit(int byteValue, int bit, boolean highLevel) {
        if (highLevel) {
            return byteValue | pinMaskHigh(bit);
        } else {
            return byteValue & pinMaskLow(bit);
        }
    }

    /**
     * Check if pin level is High
     *
     * @param portValues portValues
     * @param pinInPort = [0,7]
     * @return
     */
    public static boolean getPin(int portValues, int pinInPort) {
        return (portValues & BytesHelper.pinMaskHigh(pinInPort)) > 0;
    }
}

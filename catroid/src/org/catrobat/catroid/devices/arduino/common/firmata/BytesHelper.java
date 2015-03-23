package org.catrobat.catroid.devices.arduino.common.firmata;

import java.nio.ByteBuffer;

/**
 * Helps to prepare bytes data
 */
public class BytesHelper {

    /**
     * @param channel command channel
     * @return command channel mask
     */
    public static int ENCODE_CHANNEL(int channel) {
        return channel & 0x0F;
    }

    /**
     * Decode command from byte
     *
     * @param incomingByte
     * @return
     */
    public static int DECODE_COMMAND(int incomingByte) {
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
    public static int DECODE_CHANNEL(int incomingByte) {
        return incomingByte & 0x0F;
    }

    /**
     * Return less significant byte
     *
     * @param value value
     * @return less significant byte
     */
    public static int LSB(int value) {
        return value & 0x7F;
    }

    /**
     * Return most significant byte
     *
     * @param value value
     * @return most significant byte
     */
    public static int MSB(int value) {
        return (value >> 7) & 0x7F;
    }

    /**
     * Return byte from LSB and MSB
     *
     * @param lsb less significant byte
     * @param msb most significant byte
     * @return byte
     */
    public static int DECODE_BYTE(int lsb, int msb) {
        return (msb << 7) + lsb;
    }

    /**
     * Decode string that was encoded using LSB(byte), MSB(byte)
     *
     * @param buffer  buffer
     * @param startIndex start index
     * @param endIndex end index
     * @return decoded string
     */
    public static String DECODE_STRING(byte[] buffer, int startIndex, int endIndex) {
        StringBuilder sb = new StringBuilder();
        int offset = startIndex;
        int length = (endIndex - startIndex + 1) / 2;
        for (int i=0; i<length; i++) {
            sb.append((char)DECODE_BYTE(buffer[offset++], buffer[offset++]));
        }
        return sb.toString();
    }

    /**
     * Decode integer array that was encoded using LSB(byte), MSB(byte)
     *
     * @param buffer  buffer
     * @param startIndex start index
     * @param endIndex end index
     * @return decoded string
     */
    public static int[] DECODE_INT_ARRAY(byte[] buffer, int startIndex, int endIndex) {
        int offset = startIndex;
        int length = (endIndex - startIndex + 1) / 2;
        int[] intBuffer = new int[length];
        for (int i=0; i<length; i++) {
            intBuffer[i] = DECODE_BYTE(buffer[offset++], buffer[offset++]);
        }
        return intBuffer;
    }

    /**
     * Encode string - every byte goes to LSB(byte), MSB(byte)
     *
     * @param data string data
     * @return encoded bytes array
     */
    public static byte[] ENCODE_STRING(String data) {
        byte[] original_data = data.getBytes();
        byte[] encoded_data = new byte[original_data.length * 2];
        ENCODE_STRING(original_data, ByteBuffer.wrap(encoded_data), 0);
        return encoded_data;
    }

    /**
     * Encode string - every byte goes to LSB(byte), MSB(byte)
     *
     * @param data int array data
     * @return encoded bytes array
     */
    public static byte[] ENCODE_INT_ARRAY(int[] data) {
        byte[] encoded_data = new byte[data.length * 2];
        ENCODE_STRING(data, ByteBuffer.wrap(encoded_data), 0);
        return encoded_data;
    }

    /**
     * Encode string to existing buffer - every byte goes to LSB(byte), MSB(byte)
     *
     * @param original_data string data
     * @param buffer existing buffer
     * @param offset offset in buffer
     */
    public static void ENCODE_STRING(byte[] original_data, ByteBuffer buffer, int offset) {
        for (int i=0; i<original_data.length; i++) {
            buffer.put(offset++, (byte)LSB(original_data[i]));
            buffer.put(offset++, (byte)MSB(original_data[i]));
        }
    }

    /**
     * Encode string to existing buffer - every byte goes to LSB(byte), MSB(byte)
     *
     * @param original_data string data
     * @param buffer existing buffer
     * @param offset offset in buffer
     */
    public static void ENCODE_STRING(int[] original_data, ByteBuffer buffer, int offset) {
        for (int i=0; i<original_data.length; i++) {
            buffer.put(offset++, (byte)LSB(original_data[i]));
            buffer.put(offset++, (byte)MSB(original_data[i]));
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
            if (eachBit > 0)
                mask <<= 1;
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

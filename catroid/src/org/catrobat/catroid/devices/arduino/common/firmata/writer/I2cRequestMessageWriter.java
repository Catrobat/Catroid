package org.catrobat.catroid.devices.arduino.common.firmata.writer;


import org.catrobat.catroid.devices.arduino.common.firmata.message.I2cRequestMessage;
import org.catrobat.catroid.devices.arduino.common.firmata.serial.ISerial;
import org.catrobat.catroid.devices.arduino.common.firmata.serial.SerialException;

import static org.catrobat.catroid.devices.arduino.common.firmata.BytesHelper.*;

/**
 * MessageWriter for I2cRequestMessage
 */
public class I2cRequestMessageWriter<ConcreteRequestMessage extends I2cRequestMessage> extends SysexMessageWriter<ConcreteRequestMessage> {

    @Override
    protected void writeData(ConcreteRequestMessage message, ISerial serial) throws SerialException {
        // can not use super.writeData() because it works with String
        writeI2cRequestData(message, serial);
    }

    private void writeI2cRequestData(ConcreteRequestMessage message, ISerial serial) throws SerialException {
        byte[] buffer = new byte[2];

        buffer[0] = (byte)LSB(message.getSlaveAddress());
        int modeByte = 0;

        modeByte = setBit(modeByte, 7, false); // {7: always 0}
        modeByte = setBit(modeByte, 6, false); // {6: reserved}
        modeByte = setBit(modeByte, 5, message.isTenBitsMode()); // {5: address mode, 1 means 10-bit mode}

        // 4-3 bits are modes
        modeByte |= (byte)(message.getMode().getValue() << 3);  // {4-3: read/write, 00 => write, 01 => read once, 10 => read continuously, 11 => stop reading}

        // 0-2 bits
        if (message.isTenBitsMode())
            modeByte |= MSB(message.getSlaveAddress() & 7); // {2-0: slave address MSB in 10-bit mode, not used in 7-bit mode}

        buffer[1] = (byte)modeByte;
        serial.write(buffer);

        int[] binaryData = message.getBinaryData();
        if (binaryData != null) {
            byte[] dataBuffer = ENCODE_INT_ARRAY(binaryData);
            serial.write(dataBuffer);
        }
    }
}

package org.catrobat.catroid.devices.arduino.common.firmata.writer;


import org.catrobat.catroid.devices.arduino.common.firmata.message.I2cConfigMessage;
import org.catrobat.catroid.devices.arduino.common.firmata.serial.ISerial;
import org.catrobat.catroid.devices.arduino.common.firmata.serial.SerialException;

import static org.catrobat.catroid.devices.arduino.common.firmata.BytesHelper.LSB;
import static org.catrobat.catroid.devices.arduino.common.firmata.BytesHelper.MSB;

/**
 * MessageWriter for I2cConfigMessage
 */
public class I2cConfigMessageWriter extends SysexMessageWriter<I2cConfigMessage> {

    @Override
    protected void writeData(I2cConfigMessage message, ISerial serial) throws SerialException {
        // can not use super.writeData() because it works with String
        writeI2cConfigData(message, serial);
    }

    private void writeI2cConfigData(I2cConfigMessage message, ISerial serial) throws SerialException {
        byte[] buffer = new byte[3];

        buffer[0] = (byte)(message.isOn() ? 1 : 0);
        buffer[1] = (byte)LSB(message.getDelay());
        buffer[2] = (byte)MSB(message.getDelay());

        serial.write(buffer);
    }
}

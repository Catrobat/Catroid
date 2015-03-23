package org.catrobat.catroid.devices.arduino.common.firmata.writer;

import org.catrobat.catroid.devices.arduino.common.firmata.message.ServoConfigMessage;
import org.catrobat.catroid.devices.arduino.common.firmata.serial.ISerial;
import org.catrobat.catroid.devices.arduino.common.firmata.serial.SerialException;

import static org.catrobat.catroid.devices.arduino.common.firmata.BytesHelper.*;

/**
 * MessageWriter for ServoConfigMessage
 */
public class ServoConfigMessageWriter extends SysexMessageWriter<ServoConfigMessage> {

    @Override
    protected void writeData(ServoConfigMessage message, ISerial serial) throws SerialException {
        // can not use super.writeData() because it works with String
        writeServoData(message, serial);
    }

    private void writeServoData(ServoConfigMessage message, ISerial serial) throws SerialException {
        byte[] buffer = new byte[7];
        buffer[0] = (byte)message.getPin();

        buffer[1] = (byte)LSB(message.getMinPulse());
        buffer[2] = (byte)MSB(message.getMinPulse());

        buffer[3] = (byte)LSB(message.getMaxPulse());
        buffer[4] = (byte)MSB(message.getMaxPulse());

        buffer[5] = (byte)LSB(message.getAngle());
        buffer[6] = (byte)MSB(message.getAngle());

        serial.write(buffer);
    }
}

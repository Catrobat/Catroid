package org.catrobat.catroid.devices.arduino.common.firmata.writer;


import org.catrobat.catroid.devices.arduino.common.firmata.message.AnalogMessage;
import org.catrobat.catroid.devices.arduino.common.firmata.serial.ISerial;
import org.catrobat.catroid.devices.arduino.common.firmata.serial.SerialException;

import static org.catrobat.catroid.devices.arduino.common.firmata.BytesHelper.*;

/**
 * MessageWriter for AnalogMessage
 */
public class AnalogMessageWriter implements IMessageWriter<AnalogMessage> {

    public static final int COMMAND = 0xE0;

    public void write(AnalogMessage message, ISerial serial) throws SerialException {
        serial.write(COMMAND | ENCODE_CHANNEL(message.getPin()));
        serial.write(LSB(message.getValue()));
        serial.write(MSB(message.getValue()));
    }
}

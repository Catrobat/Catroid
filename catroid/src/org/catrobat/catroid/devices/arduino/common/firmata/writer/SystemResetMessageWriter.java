package org.catrobat.catroid.devices.arduino.common.firmata.writer;

import org.catrobat.catroid.devices.arduino.common.firmata.message.SystemResetMessage;
import org.catrobat.catroid.devices.arduino.common.firmata.serial.ISerial;
import org.catrobat.catroid.devices.arduino.common.firmata.serial.SerialException;

/**
 * MessageWriter for SystemResetMessage
 */
public class SystemResetMessageWriter implements IMessageWriter<SystemResetMessage> {

    public static final int COMMAND = 0xFF;

    public void write(SystemResetMessage message, ISerial serial) throws SerialException {
        serial.write(COMMAND);
    }
}

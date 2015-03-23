package org.catrobat.catroid.devices.arduino.common.firmata.writer;

import org.catrobat.catroid.devices.arduino.common.firmata.message.ReportProtocolVersionMessage;
import org.catrobat.catroid.devices.arduino.common.firmata.serial.ISerial;
import org.catrobat.catroid.devices.arduino.common.firmata.serial.SerialException;

/**
 * MessageWriter for ReportProtocolVersionMessage
 */
public class ReportProtocolVersionMessageWriter implements IMessageWriter<ReportProtocolVersionMessage> {

    public static final int COMMAND = 0xF9;

    public void write(ReportProtocolVersionMessage message, ISerial serial) throws SerialException {
        serial.write(COMMAND);
    }
}

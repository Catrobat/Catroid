package org.catrobat.catroid.devices.arduino.common.firmata.writer;

import org.catrobat.catroid.devices.arduino.common.firmata.message.ReportDigitalPortMessage;
import org.catrobat.catroid.devices.arduino.common.firmata.serial.ISerial;
import org.catrobat.catroid.devices.arduino.common.firmata.serial.SerialException;

import static org.catrobat.catroid.devices.arduino.common.firmata.BytesHelper.ENCODE_CHANNEL;

/**
 * MessageWriter for ReportDigitalPortMessage
 */
public class ReportDigitalPortMessageWriter implements IMessageWriter<ReportDigitalPortMessage> {

    public static final int COMMAND = 0xD0;

    public void write(ReportDigitalPortMessage message, ISerial serial) throws SerialException {
        serial.write(COMMAND | ENCODE_CHANNEL(message.getPort()));
        serial.write(message.isEnable() ? 1 : 0);
    }
}

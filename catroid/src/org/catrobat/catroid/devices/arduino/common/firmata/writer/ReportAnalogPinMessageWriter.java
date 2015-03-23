package org.catrobat.catroid.devices.arduino.common.firmata.writer;


import org.catrobat.catroid.devices.arduino.common.firmata.message.ReportAnalogPinMessage;
import org.catrobat.catroid.devices.arduino.common.firmata.serial.ISerial;
import org.catrobat.catroid.devices.arduino.common.firmata.serial.SerialException;

import static org.catrobat.catroid.devices.arduino.common.firmata.BytesHelper.ENCODE_CHANNEL;

/**
 * MessageWriter for ReportAnalogPinMessage
 */
public class ReportAnalogPinMessageWriter implements IMessageWriter<ReportAnalogPinMessage> {

    public static final int COMMAND = 0xC0;

    public void write(ReportAnalogPinMessage message, ISerial serial) throws SerialException {
        serial.write(COMMAND | ENCODE_CHANNEL(message.getPin()));
        serial.write(message.isEnable() ? 1 : 0);
    }
}

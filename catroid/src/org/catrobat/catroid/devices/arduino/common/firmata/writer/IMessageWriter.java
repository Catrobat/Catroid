package org.catrobat.catroid.devices.arduino.common.firmata.writer;


import org.catrobat.catroid.devices.arduino.common.firmata.message.Message;
import org.catrobat.catroid.devices.arduino.common.firmata.serial.ISerial;
import org.catrobat.catroid.devices.arduino.common.firmata.serial.SerialException;

/**
 * Message writer
 */
public interface IMessageWriter<ConcreteMessage extends Message> {

    /**
     * Write command to Serial
     */
    void write(ConcreteMessage message, ISerial serial) throws SerialException;
}

package org.catrobat.catroid.devices.arduino.common.firmata.reader;

import org.catrobat.catroid.devices.arduino.common.firmata.IFirmata;
import org.catrobat.catroid.devices.arduino.common.firmata.message.I2cReplyMessage;

import static org.catrobat.catroid.devices.arduino.common.firmata.BytesHelper.*;

/**
 * MessageReader for I2cReplyMessage
 */
public class I2cReplyMessageReader extends BaseSysexMessageReader<I2cReplyMessage> {

    public I2cReplyMessageReader() {
        super((byte) I2cReplyMessage.COMMAND);
    }

    @Override
    protected I2cReplyMessage buildSysexMessage(byte[] buffer, int bufferLength) {
        I2cReplyMessage message = new I2cReplyMessage();
        // skip 2 first bytes - COMMAND_START and sysex command byte
        message.setSlaveAddress(DECODE_BYTE(buffer[2], buffer[3]));
        message.setRegister(DECODE_BYTE(buffer[4], buffer[5]));
        message.setBinaryData(extractIntArrayFromBuffer(buffer, 6, bufferLength - 2));
        // message.getData() is not used
        return message;
    }

    public void fireEvent(IFirmata.Listener listener) {
        listener.onI2cMessageReceived(getMessage());
    }
}

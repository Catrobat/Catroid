package org.catrobat.catroid.devices.arduino.common.firmata.reader;

import org.catrobat.catroid.devices.arduino.common.firmata.IFirmata;
import org.catrobat.catroid.devices.arduino.common.firmata.message.StringSysexMessage;

/**
 * MessageReader for StringSysexMessage
 */
public class StringSysexMessageReader extends BaseSysexMessageReader<StringSysexMessage> {

    public StringSysexMessageReader() {
        super((byte)StringSysexMessage.COMMAND);
    }

    @Override
    protected StringSysexMessage buildSysexMessage(byte[] buffer, int bufferLength) {
        StringSysexMessage message = new StringSysexMessage();
        // skip 2 first bytes - COMMAND_START and sysex command byte
        message.setData(extractStringFromBuffer(buffer, 2, bufferLength - 2));
        return message;
    }

    public void fireEvent(IFirmata.Listener listener) {
        listener.onStringSysexMessageReceived(getMessage());
    }
}

package org.catrobat.catroid.devices.arduino.common.firmata.message;

import java.text.MessageFormat;

/**
 * String sysex message
 */
public class StringSysexMessage extends SysexMessage {

    // Sysex command byte
    public static final int COMMAND = 0x71;

    public StringSysexMessage() {
        super(COMMAND, null);
    }

    public StringSysexMessage(String data) {
        this();
        setData(data);
    }

    @Override
    public String toString() {
        return MessageFormat.format("StringSysexMessage[data=\"{0}\"]", getData());
    }
}

package org.catrobat.catroid.devices.arduino.common.firmata.message;

import java.text.MessageFormat;

/**
 * Response to ReportProtocolVersionMessage
 */
public class ProtocolVersionMessage extends Message {

    private int major;

    public int getMajor() {
        return major;
    }

    public void setMajor(int major) {
        this.major = major;
    }

    private int minor;

    public int getMinor() {
        return minor;
    }

    public void setMinor(int minor) {
        this.minor = minor;
    }

    public ProtocolVersionMessage() {
        super();
    }

    public ProtocolVersionMessage(int major, int minor) {
        this();
        setMajor(major);
        setMinor(minor);
    }

    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj))
            return false;

        ProtocolVersionMessage message = (ProtocolVersionMessage)obj;
        return message != null &&
               message.getMajor() == getMajor() &&
               message.getMinor() == getMinor();
    }

    @Override
    public String toString() {
        return MessageFormat.format("ProtocolVersionMessage[major={0}, minor={1}]", major, minor);
    }
}

package org.catrobat.catroid.devices.arduino.common.firmata.message;

import java.text.MessageFormat;

/**
 * Response to FirmwareVersionMessage
 */
public class FirmwareVersionMessage extends SysexMessage {

    public FirmwareVersionMessage() {
        super();
    }

    public FirmwareVersionMessage(int major, int minor, String name) {
        this();
        setName(name);
        setMajor(major);
        setMinor(minor);
    }

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

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj))
            return false;

        FirmwareVersionMessage message = (FirmwareVersionMessage)obj;
        return message != null &&
               message.getMajor() == getMajor() &&
               message.getMinor() == getMinor() &&
               (
                   (message.getName() == null && getName() == null)
                    ||
                   (message.getName() != null && message.getName().equals(getName()))
               );
    }

    @Override
    public String toString() {
        return MessageFormat.format("FirmwareVersionMessage[major={0}, minor={1}, name=''{2}'']", major, minor, name);
    }
}

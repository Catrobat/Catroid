package org.catrobat.catroid.devices.arduino.common.firmata.message;

import java.text.MessageFormat;

/**
 * Digital I/O message
 *
 * (send this message to set port digital value
 * or receive this message as ReportDigitalPortMessage response)
 */
public class DigitalMessage extends Message {

    public DigitalMessage() {
        super();
    }

    public DigitalMessage(int port, int value) {
        this();
        setPort(port);
        setValue(value);
    }

    private int port;

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    private int value;

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj))
            return false;

        DigitalMessage message = (DigitalMessage)obj;
        return message != null &&
               message.getPort() == getPort() &&
               message.getValue() == getValue();
    }

    @Override
    public String toString() {
        return MessageFormat.format("DigitalMessage[port={0}, value={1}]", port, value);
    }
}

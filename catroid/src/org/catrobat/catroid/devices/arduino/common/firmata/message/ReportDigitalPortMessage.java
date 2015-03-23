package org.catrobat.catroid.devices.arduino.common.firmata.message;

import java.text.MessageFormat;

/**
 * Report digital port message
 */
public class ReportDigitalPortMessage extends Message {

    public ReportDigitalPortMessage() {
        super();
    }

    public ReportDigitalPortMessage(int port, boolean enable) {
        this();
        setPort(port);
        setEnable(enable);
    }

    private int port;

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    private boolean enable;

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj))
            return false;

        ReportDigitalPortMessage message = (ReportDigitalPortMessage)obj;
        return message != null &&
               message.getPort() == getPort() &&
               message.isEnable() == isEnable();
    }

    @Override
    public String toString() {
        return MessageFormat.format("ReportDigitalPortMessage[port={0}, enable={1}]", port, enable);
    }
}

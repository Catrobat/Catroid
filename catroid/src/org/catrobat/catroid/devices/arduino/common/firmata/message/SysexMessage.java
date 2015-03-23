package org.catrobat.catroid.devices.arduino.common.firmata.message;

import java.text.MessageFormat;

/**
 * Sysex message
 *
 */
public class SysexMessage extends Message {

    public SysexMessage() {
        super();
    }

    /**
     * Constructor
     *
     * @param command sysex command (NOT firmata command)
     * @param data sysex command data
     */
    public SysexMessage(int command, String data) {
        this();
        setCommand(command);
        setData(data);
    }

    private int command;

    public int getCommand() {
        return command;
    }

    public void setCommand(int command) {
        this.command = command;
    }

    private String data;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj))
            return false;

        SysexMessage message = (SysexMessage)obj;
        return message != null &&
               message.getCommand() == getCommand() &&
               (
                   (message.getData() == null && getData() == null)
                   ||
                   (message.getData() != null && message.getData().equals(getData()))
               );
    }

    @Override
    public String toString() {
        return MessageFormat.format("SysexMessage[command={0}, data=\"{1}\"]", command, data);
    }

}

package org.catrobat.catroid.devices.arduino.common.firmata.message;

import java.text.MessageFormat;

/**
 * Report analog pin message
 */
public class ReportAnalogPinMessage extends Message {

    public ReportAnalogPinMessage() {
        super();
    }

    public ReportAnalogPinMessage(int pin, boolean enable) {
        this();
        setPin(pin);
        setEnable(enable);
    }

    private int pin;

    public int getPin() {
        return pin;
    }

    public void setPin(int pin) {
        this.pin = pin;
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

        ReportAnalogPinMessage message = (ReportAnalogPinMessage)obj;
        return message != null &&
               message.getPin() == getPin() &&
               message.isEnable() == isEnable();
    }

    @Override
    public String toString() {
        return MessageFormat.format("ReportAnalogPinMessage[pin={0}, enable={1}]", pin, enable);
    }
}

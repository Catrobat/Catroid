package org.catrobat.catroid.devices.arduino.common.firmata.message;

import java.text.MessageFormat;

/**
 * Analog I/O message
 *
 * (send this message to set Analog value
 * or receive this message as ReportAnalogPinMessage response)
 */
public class AnalogMessage extends Message {

    public AnalogMessage() {
        super();
    }

    public AnalogMessage(int pin, int value) {
        this();
        setPin(pin);
        setValue(value);
    }

    private int pin;

    public int getPin() {
        return pin;
    }

    public void setPin(int pin) {
        this.pin = pin;
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

        AnalogMessage message = (AnalogMessage)obj;
        return message != null &&
               message.getPin() == getPin() &&
               message.getValue() == getValue();
    }

    @Override
    public String toString() {
        return MessageFormat.format("AnalogMessage[pin={0}, value={1}]", pin, value);
    }
}

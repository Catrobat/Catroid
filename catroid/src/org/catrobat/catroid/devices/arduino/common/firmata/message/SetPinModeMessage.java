package org.catrobat.catroid.devices.arduino.common.firmata.message;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * Set pin mode message
 */
public class SetPinModeMessage extends Message {

    private static Map<Integer, PIN_MODE> modes = new HashMap<Integer, PIN_MODE>();

    /**
     * Pin mode enumeration
     * (can be used as 'mode' parameter value for type-safety)
     */
    public enum PIN_MODE {

        /**
         * Pin works as input
         */
        INPUT(0),

        /**
         * Pin works as output
         */
        OUTPUT(1),

        /**
         * Pin works as analog input
         */
        ANALOG(2),

        /**
         * Pin works as analog PWM output
         */
        PWM(3),

        /**
         * Ping workds as servo
         */
        SERVO(4);

        private int mode;

        public int getMode() {
            return mode;
        }

        private PIN_MODE(int mode) {
            this.mode = mode;
            modes.put(mode, this);
        }

        public static PIN_MODE find(int mode) {
            return modes.get(mode);
        }
    }

    protected SetPinModeMessage() {
        super();
    }

    /**
     *
     * @param pin
     * @param mode (use PIN_MODE enum for type-safety)
     * @see name.antonsmirnov.firmata.message.SetPinModeMessage.PIN_MODE
     */
    public SetPinModeMessage(int pin, int mode) {
        this();
        setPin(pin);
        setMode(mode);
    }

    private int pin;

    public int getPin() {
        return pin;
    }

    public void setPin(int pin) {
        this.pin = pin;
    }

    private int mode;

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj))
            return false;

        SetPinModeMessage message = (SetPinModeMessage)obj;
        return message != null &&
               message.getPin() == getPin() &&
               message.getMode() == getMode();
    }

    @Override
    public String toString() {
        return MessageFormat.format("SetPinModeMessage[pin={0}, mode={1}]", pin, mode);
    }
}

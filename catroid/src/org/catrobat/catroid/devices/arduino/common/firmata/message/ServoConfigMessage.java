package org.catrobat.catroid.devices.arduino.common.firmata.message;

import java.text.MessageFormat;

/**
 * Servo config message
 */
public class ServoConfigMessage extends SysexMessage {

    // Sysex command byte
    public static final int COMMAND = 0x70;

    public ServoConfigMessage() {
        super(COMMAND, null);
    }

    private int pin;

    public int getPin() {
        return pin;
    }

    public void setPin(int pin) {
        this.pin = pin;
    }

    private int minPulse;

    public int getMinPulse() {
        return minPulse;
    }

    public void setMinPulse(int minPulse) {
        this.minPulse = minPulse;
    }

    private int maxPulse;

    public int getMaxPulse() {
        return maxPulse;
    }

    public void setMaxPulse(int maxPulse) {
        this.maxPulse = maxPulse;
    }

    private int angle;

    public int getAngle() {
        return angle;
    }

    /**
     * @param angle (degrees)
     */
    public void setAngle(int angle) {
        this.angle = angle;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj != null && obj.getClass().equals(getClass())))
            return false;

        ServoConfigMessage message = (ServoConfigMessage)obj;
        return message != null && message.getPin() == this.getPin();
    }

    @Override
    public String toString() {
        return MessageFormat.format("ServoConfigMessage[pin={0}, minPulse={1}, maxPulse={2}, angle={3}]", pin, minPulse, maxPulse, angle);
    }
}

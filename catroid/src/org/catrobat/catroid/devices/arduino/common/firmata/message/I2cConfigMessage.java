package org.catrobat.catroid.devices.arduino.common.firmata.message;

import java.text.MessageFormat;

/**
 * I2C config message
 */
public class I2cConfigMessage extends SysexMessage {

    // Sysex command byte
    public static final int COMMAND = 0x78;

    public I2cConfigMessage() {
        super(COMMAND, null);
    }

    private boolean on;

    public boolean isOn() {
        return on;
    }

    public void setOn(boolean on) {
        this.on = on;
    }

    private int delay;

    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    @Override
    public String toString() {
        return MessageFormat.format("I2cConfigMessage[on={0}, delay={1}]", on, delay);
    }
}

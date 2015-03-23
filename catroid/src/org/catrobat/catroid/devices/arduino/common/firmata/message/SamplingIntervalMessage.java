package org.catrobat.catroid.devices.arduino.common.firmata.message;

import java.text.MessageFormat;

/**
 * Sampling interval message
 */
public class SamplingIntervalMessage extends SysexMessage {

    // Sysex command byte
    public static final int COMMAND = 0x7A;

    public SamplingIntervalMessage() {
        super(COMMAND, null);
    }

    private int interval;

    public int getInterval() {
        return interval;
    }

    /**
     * @param interval (sampling interval on the millisecond time scale)
     */
    public void setInterval(int interval) {
        this.interval = interval;
    }

    @Override
    public String toString() {
        return MessageFormat.format("SamplingIntervalMessage[interval={0}]", interval);
    }
}

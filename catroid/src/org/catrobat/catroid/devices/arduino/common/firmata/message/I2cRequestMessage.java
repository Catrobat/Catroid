package org.catrobat.catroid.devices.arduino.common.firmata.message;

import org.catrobat.catroid.devices.arduino.common.firmata.FormatHelper;

import java.text.MessageFormat;

/**
 * I2C request message
 */
public class I2cRequestMessage extends SysexMessage {

    // Sysex command byte
    public static final int COMMAND = 0x76;

    public I2cRequestMessage() {
        super(COMMAND, null);
    }

    private int slaveAddress;

    public int getSlaveAddress() {
        return slaveAddress;
    }

    public void setSlaveAddress(int slaveAddress) {
        this.slaveAddress = slaveAddress;
    }

    private boolean tenBitsMode;

    public boolean isTenBitsMode() {
        return tenBitsMode;
    }

    public void setTenBitsMode(boolean tenBitsMode) {
        this.tenBitsMode = tenBitsMode;
    }

    /**
     * I2C mode
     */
    public enum MODE {

        /**
         * Write mode
         */
        WRITE(0),

        /**
         * Read once mode
         */
        READ_ONCE(1),

        /**
         * Read continuously mode
         */
        READ_CONTINUOUSLY(2),

        /**
         * Stop reading mode
         */
        STOP_READING(3);

        private int value;

        private void setValue(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }


        private MODE(int value) {
            setValue(value);
        }
    }

    private MODE mode;

    public MODE getMode() {
        return mode;
    }

    public void setMode(MODE mode) {
        this.mode = mode;
    }
    
    private int[] binaryData;

    public int[] getBinaryData() {
        return binaryData;
    }

    protected static FormatHelper formatHelper = new FormatHelper();

    public void setBinaryData(int[] binaryData) {
        this.binaryData = binaryData;
    }

    @Override
    public String toString() {
        return MessageFormat.format("I2cRequestMessage[slaveAddress={0}, 10bitsMode={1}, mode={2}, binaryData={3}]",
            formatHelper.formatBinary(slaveAddress), tenBitsMode, mode, (binaryData != null ? formatHelper.formatBinaryData(binaryData) : "null"));
    }
}

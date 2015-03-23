package org.catrobat.catroid.devices.arduino.common.firmata.message;

import java.text.MessageFormat;

/**
 * I2C request message to read data
 */
public class I2cReadRequestMessage extends I2cRequestMessage {

    private Integer slaveRegister;

    public Integer getSlaveRegister() {
        return slaveRegister;
    }

    public void setSlaveRegister(Integer slaveRegister) {
        this.slaveRegister = slaveRegister;
    }

    private Integer bytesToRead;

    public Integer getBytesToRead() {
        return bytesToRead;
    }

    public void setBytesToRead(Integer bytesToRead) {
        this.bytesToRead = bytesToRead;
    }

    @Override
    public int[] getBinaryData() {
        validateBytesToRead();

        MODE mode = getMode();
        switch (mode) {
            case READ_ONCE:
                return getReadOnlyBinaryData();

            case READ_CONTINUOUSLY:
                return getReadContinuouslyBinaryData();

            default:
                throw new RuntimeException("Mode should be ReadOnce or ReadContinuously");
        }
    }

    private void validateBytesToRead() {
        if (bytesToRead == null)
            throw new RuntimeException("bytesToRead field is not specified and is required");
    }

    private int[] getReadOnlyBinaryData() {
        return slaveRegister != null
            ? new int[] { slaveRegister, bytesToRead }
            : new int[] { bytesToRead };
    }

    private int[] getReadContinuouslyBinaryData() {
        validateSlaveRegister();
        validateBytesToRead();

        return new int[] { slaveRegister, bytesToRead };
    }

    private void validateSlaveRegister() {
        if (slaveRegister == null)
            throw new RuntimeException("slaveRegister is not specified and in required in ReadContinuously mode");
    }

    @Override
    public String toString() {
        return MessageFormat.format("I2cReadRequestMessage[slaveAddress={0}, 10bitsMode={1}, mode={2}, slaveReg={3}, bytesToRead={4}]",
            formatHelper.formatBinary(getSlaveAddress()), isTenBitsMode(), getMode(),
            (slaveRegister != null ? formatHelper.formatBinary(slaveRegister) : "null"),
            bytesToRead);
    }
}

package org.catrobat.catroid.devices.arduino.common.firmata.message;

import org.catrobat.catroid.devices.arduino.common.firmata.FormatHelper;

import java.text.MessageFormat;
import java.util.Arrays;

/**
 * I2C reply message
 */
public class I2cReplyMessage extends SysexMessage {

    // Sysex command byte
    public static final int COMMAND = 0x77;

    public I2cReplyMessage() {
        super(COMMAND, null);
    }

    public I2cReplyMessage(int slaveAddress, int register, int[] binaryData) {
        this();
        setSlaveAddress(slaveAddress);
        setRegister(register);
        setBinaryData(binaryData);
    }

    private int slaveAddress;

    public int getSlaveAddress() {
        return slaveAddress;
    }

    public void setSlaveAddress(int slaveAddress) {
        this.slaveAddress = slaveAddress;
    }
    
    private int register;

    public int getRegister() {
        return register;
    }

    public void setRegister(int register) {
        this.register = register;
    }

    private int[] binaryData;

    public int[] getBinaryData() {
        return binaryData;
    }

    public void setBinaryData(int[] binaryData) {
        this.binaryData = binaryData;
    }

    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj))
            return false;

        I2cReplyMessage message = (I2cReplyMessage)obj;
        return message != null &&
                message.getSlaveAddress() == getSlaveAddress() &&
                message.getRegister() == getRegister() &&
                (
                    (message.getBinaryData() == null && getBinaryData() == null)
                    ||
                    (Arrays.equals(message.getBinaryData(), getBinaryData()))
                );
    }

    protected static FormatHelper formatHelper = new FormatHelper();

    @Override
    public String toString() {
        return MessageFormat.format("I2cReplyMessage[slaveAddress={0}, register={1}, binaryData={2}]",
            formatHelper.formatBinary(slaveAddress), register, formatHelper.formatBinaryData(binaryData));
    }
}

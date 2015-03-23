package org.catrobat.catroid.devices.arduino.common.firmata.message;

/**
 * Firmware name/version
 */
public class ReportFirmwareVersionMessage extends SysexMessage {

    // it's not good idea to use Sysex command to add ReportFirmwareVersion message,
    // because it introduces domain operation in protocol, but it's implemented so be Firmata developers
    public static final int COMMAND = 0x79;

    public ReportFirmwareVersionMessage() {
        super(COMMAND, null);
    }

    @Override
    public String toString() {
        return "ReportFirmwareVersionMessage[]";
    }
}

package org.catrobat.catroid.devices.arduino.common.firmata.reader;

import org.catrobat.catroid.devices.arduino.common.firmata.IFirmata;
import org.catrobat.catroid.devices.arduino.common.firmata.message.ProtocolVersionMessage;
import org.catrobat.catroid.devices.arduino.common.firmata.writer.ReportProtocolVersionMessageWriter;

/**
 * MessageReader for ProtocolVersionMessage
 */
public class ProtocolVersionMessageReader implements IMessageReader<ProtocolVersionMessage> {

    public boolean canRead(byte[] buffer, int bufferLength, int command) {
        return bufferLength == 1
               &&
               buffer[0] == (byte) ReportProtocolVersionMessageWriter.COMMAND;
    }

    private boolean isHandling;

    public void startReading() {
        isHandling = true;
        message = new ProtocolVersionMessage();
    }

    public void read(byte[] buffer, int length) {
        byte incomingByte = buffer[length-1];
        if (length == 2) {
            message.setMajor(incomingByte);
        } else {
            message.setMinor(incomingByte);
            isHandling = false;
        }
    }

    public boolean finishedReading() {
        return !isHandling;
    }

    private ProtocolVersionMessage message;

    public ProtocolVersionMessage getMessage() {
        return message;
    }

    public void fireEvent(IFirmata.Listener listener) {
        listener.onProtocolVersionMessageReceived(getMessage());
    }
}

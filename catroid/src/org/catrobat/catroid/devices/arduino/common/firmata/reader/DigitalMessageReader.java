package org.catrobat.catroid.devices.arduino.common.firmata.reader;

import org.catrobat.catroid.devices.arduino.common.firmata.IFirmata;
import org.catrobat.catroid.devices.arduino.common.firmata.message.DigitalMessage;
import org.catrobat.catroid.devices.arduino.common.firmata.writer.DigitalMessageWriter;

import static org.catrobat.catroid.devices.arduino.common.firmata.BytesHelper.DECODE_BYTE;
import static org.catrobat.catroid.devices.arduino.common.firmata.BytesHelper.DECODE_CHANNEL;

/**
 * MessageReader for DigitalMessage
 */
public class DigitalMessageReader implements IMessageReader<DigitalMessage> {

    public boolean canRead(byte[] buffer, int bufferLength, int command) {
        return command == DigitalMessageWriter.COMMAND;
    }

    private boolean isHandling;

    public void startReading() {
        isHandling = true;
        message = new DigitalMessage();
    }

    public void read(byte[] buffer, int length) {
        if (length == 2) {
            message.setPort(DECODE_CHANNEL(buffer[0]));
        } else {
            message.setValue(DECODE_BYTE(buffer[1], buffer[2]));
            isHandling = false;
        }
    }

    public boolean finishedReading() {
        return !isHandling;
    }

    private DigitalMessage message;

    public DigitalMessage getMessage() {
        return message;
    }

    public void fireEvent(IFirmata.Listener listener) {
        listener.onDigitalMessageReceived(getMessage());
    }
}

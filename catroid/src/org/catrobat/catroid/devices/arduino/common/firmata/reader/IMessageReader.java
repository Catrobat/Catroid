package org.catrobat.catroid.devices.arduino.common.firmata.reader;

import org.catrobat.catroid.devices.arduino.common.firmata.IFirmata;
import org.catrobat.catroid.devices.arduino.common.firmata.message.Message;

/**
 * Message reader
 */
public interface IMessageReader<ConcreteMessage extends Message> {

    /**
     * Can read command
     *
     * @param buffer incoming buffer
     * @param bufferLength current buffer length
     * @return true if it's his command message type
     */
    boolean canRead(byte[] buffer, int bufferLength, int command);

    /**
     * Start handling message
     */
    void startReading();

    /**
     * Read next message byte
     *
     * @param buffer incoming buffer
     * @param length current buffer length
     */
    public void read(byte[] buffer, int length);

    /**
     * Has it finished message handling
     *
     * @return is it has received all the message bytes
     */
    boolean finishedReading();

    /**
     * Message if it finished handling
     * (check finishedReading before)
     * @return
     */
    ConcreteMessage getMessage();

    /**
     * Invoke Firmata listener
     * @param listener
     */
    void fireEvent(IFirmata.Listener listener);
}

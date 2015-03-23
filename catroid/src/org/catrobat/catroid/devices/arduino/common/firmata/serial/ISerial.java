package org.catrobat.catroid.devices.arduino.common.firmata.serial;

/**
 * Serial interface
 * (methods required for Firmata only)
 */
public interface ISerial {

    /**
     * Set serial events listener
     * @param listener serial events listener
     */
    void addListener(ISerialListener listener);

    /**
     * Remove serial events listener
     * @param listener
     */
    void removeListener(ISerialListener listener);

    /**
     * Start talking to serial
     */
    void start() throws SerialException;

    /**
     * Stop talking to serial
     */
    void stop() throws SerialException;

    /**
     * Serial is stopping
     * @return
     */
    boolean isStopping();

    /**
     * Returns the number of bytes that have been read from serial
     * and are waiting to be dealt with by the user.
     */
    int available() throws SerialException;

    /**
     * Clear buffers
     */
    void clear() throws SerialException;

    /**
     * Read byte from serial
     * (check available() before)
     */
    int read() throws SerialException;

    /**
     * Write byte to serial
     * @param outcomingByte outcoming byte
     */
    void write(int outcomingByte) throws SerialException;

    /**
     * Write outcoming bytes to serial
     * @param outcomingBytes bytes to write
     */
    void write(byte[] outcomingBytes) throws SerialException;
}

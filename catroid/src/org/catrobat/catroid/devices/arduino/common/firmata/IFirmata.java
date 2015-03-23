package org.catrobat.catroid.devices.arduino.common.firmata;

import org.catrobat.catroid.devices.arduino.common.firmata.message.*;
import org.catrobat.catroid.devices.arduino.common.firmata.serial.SerialException;

/**
 * Firmata interface
 */
public interface IFirmata {

    /**
     * Listener for incoming messages from Arduino board
     */
    public static interface Listener {

        /**
         * AnalogMessage received event
         * @param message
         */
        void onAnalogMessageReceived(AnalogMessage message);

        /**
         * DigitalMessage received event
         * @param message
         */
        void onDigitalMessageReceived(DigitalMessage message);

        /**
         * FirmwareVersionMessage received event
         * @param message
         */
        void onFirmwareVersionMessageReceived(FirmwareVersionMessage message);

        /**
         *  ProtocolVersionMessage received event
         * @param message
         */
        void onProtocolVersionMessageReceived(ProtocolVersionMessage message);

        /**
         * SysexMessage received (NOT KNOWN SysexMessage inherited commands like ReportFirmwareVersionMessage, StringSysexMessage, etc)
         *
         * @param message
         */
        void onSysexMessageReceived(SysexMessage message);

        /**
         * StringSysexMessage received event
         */
        void onStringSysexMessageReceived(StringSysexMessage message);

        /**
         * ReportI2cMessageReceived received event
         */
        void onI2cMessageReceived(I2cReplyMessage message);

        /**
         * Unknown byte received (no active MessageReader)
         * @param byteValue
         */
        void onUnknownByteReceived(int byteValue);
    }

    /**
     * Listener stub
     */
    public static class StubListener implements Listener {
        public void onAnalogMessageReceived(AnalogMessage message) {}
        public void onDigitalMessageReceived(DigitalMessage message) {}
        public void onFirmwareVersionMessageReceived(FirmwareVersionMessage message) {}
        public void onProtocolVersionMessageReceived(ProtocolVersionMessage message) {}
        public void onSysexMessageReceived(SysexMessage message) {}
        public void onStringSysexMessageReceived(StringSysexMessage message) {}
        public void onI2cMessageReceived(I2cReplyMessage message) {}
        public void onUnknownByteReceived(int byteValue) {}
    }

    /**
     * Add listener
     *
     * @param listener listener
     */
    public void addListener(Listener listener);

    /**
     * Remove listener
     *
     * @param listener listener
     */
    public void removeListener(Listener listener);

    /**
     * Check if it contains listener
     *
     * @param listener listener
     * @return
     */
    public boolean containsListener(Listener listener);

    /**
     * Remove all listeners
     */
    public void clearListeners();

    /**
     * Send message to Arduino board
     *
     * @param message concrete outcoming message
     */
    void send(Message message) throws SerialException;


    /**
     * Incoming byte received event
     *
     * @param incomingByte incoming byte
     */
    void onDataReceived(int incomingByte);
}

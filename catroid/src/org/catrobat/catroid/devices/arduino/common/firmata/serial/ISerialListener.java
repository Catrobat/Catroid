package org.catrobat.catroid.devices.arduino.common.firmata.serial;

/**
 * Serial events listener
 */
public interface ISerialListener<ConcreteSerialImpl> {

    /**
     * Data received from serial event
     *
     * @param serialImpl serial implementation (ucan be used to specify serial)
     */
    void onDataReceived(ConcreteSerialImpl serialImpl);

    /**
     * Exception in serial
     *
     * @param e
     */
    void onException(Throwable e);
}

package org.catrobat.catroid.devices.arduino.common.firmata.message;

import java.io.Serializable;

/**
 * Abstract message (model)
 * (implements Serializable to support persistence)
 */
public abstract class Message implements Serializable {

    @Override
    public boolean equals(Object obj) {
        return (obj != null && obj.getClass().equals(getClass()));
    }
}

/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2015 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * An additional term exception under section 7 of the GNU Affero
 * General Public License, version 3, is available at
 * http://developer.catrobat.org/license_additional_term
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.catroid.devices.arduino.common.firmata.message;

import java.text.MessageFormat;

/**
 * Analog I/O message
 *
 * (send this message to set Analog value
 * or receive this message as ReportAnalogPinMessage response)
 */
public class AnalogMessage extends Message {

    public AnalogMessage() {
        super();
    }

    public AnalogMessage(int pin, int value) {
        this();
        setPin(pin);
        setValue(value);
    }

    private int pin;

    public int getPin() {
        return pin;
    }

    public void setPin(int pin) {
        this.pin = pin;
    }

    private int value;

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

	@Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) {
			return false;
		}

        AnalogMessage message = (AnalogMessage)obj;
        return message != null &&
               message.getPin() == getPin() &&
               message.getValue() == getValue();
    }

    @Override
    public String toString() {
        return MessageFormat.format("AnalogMessage[pin={0}, value={1}]", pin, value);
    }
}

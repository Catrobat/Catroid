/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2014 The Catrobat Team
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
 * Report analog pin message
 */
public class ReportAnalogPinMessage extends Message {

    public ReportAnalogPinMessage() {
        super();
    }

    public ReportAnalogPinMessage(int pin, boolean enable) {
        this();
        setPin(pin);
        setEnable(enable);
    }

    private int pin;

    public int getPin() {
        return pin;
    }

    public void setPin(int pin) {
        this.pin = pin;
    }

    private boolean enable;

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

	@Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) {
			return false;
		}

        ReportAnalogPinMessage message = (ReportAnalogPinMessage)obj;
        return message != null &&
               message.getPin() == getPin() &&
               message.isEnable() == isEnable();
    }

    @Override
    public String toString() {
        return MessageFormat.format("ReportAnalogPinMessage[pin={0}, enable={1}]", pin, enable);
    }
}

/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2023 The Catrobat Team
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

package org.catrobat.catroid.firmata.Firmata.message;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * Set pin mode message
 */
public class SetPinModeMessage extends Message {

    private static Map<Integer, PIN_MODE> modes = new HashMap<Integer, PIN_MODE>();

    /**
     * Pin mode enumeration
     * (can be used as 'mode' parameter value for type-safety)
     */
    public enum PIN_MODE {

        /**
         * Pin works as input
         */
        INPUT(0),

        /**
         * Pin works as output
         */
        OUTPUT(1),

        /**
         * Pin works as analog input
         */
        ANALOG(2),

        /**
         * Pin works as analog PWM output
         */
        PWM(3),

        /**
         * Ping workds as servo
         */
        SERVO(4);

        private int mode;

        public int getMode() {
            return mode;
        }

        private PIN_MODE(int mode) {
            this.mode = mode;
            modes.put(mode, this);
        }

        public static PIN_MODE find(int mode) {
            return modes.get(mode);
        }
    }

    protected SetPinModeMessage() {
        super();
    }

    /**
     *
     * @param pin
     * @param mode (use PIN_MODE enum for type-safety)
     * @see org.catrobat.catroid.firmata.Firmata.message.SetPinModeMessage.PIN_MODE
     */
    public SetPinModeMessage(int pin, int mode) {
        this();
        setPin(pin);
        setMode(mode);
    }

    private int pin;

    public int getPin() {
        return pin;
    }

    public void setPin(int pin) {
        this.pin = pin;
    }

    private int mode;

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj))
            return false;

        SetPinModeMessage message = (SetPinModeMessage)obj;
        return message != null &&
               message.getPin() == getPin() &&
               message.getMode() == getMode();
    }

    @Override
    public String toString() {
        return MessageFormat.format("SetPinModeMessage[pin={0}, mode={1}]", pin, mode);
    }
}

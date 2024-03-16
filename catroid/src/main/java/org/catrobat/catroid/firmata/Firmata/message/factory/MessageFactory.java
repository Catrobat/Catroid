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

package org.catrobat.catroid.firmata.Firmata.message.factory;

import org.catrobat.catroid.firmata.Firmata.message.AnalogMessage;
import org.catrobat.catroid.firmata.Firmata.message.DigitalMessage;
import org.catrobat.catroid.firmata.Firmata.message.ReportAnalogPinMessage;
import org.catrobat.catroid.firmata.Firmata.message.ReportDigitalPortMessage;
import org.catrobat.catroid.firmata.Firmata.message.SetPinModeMessage;

/**
 * Builds Messages
 * (build SAFE messages with pins, modes validation according to hardware features)
 */
public interface MessageFactory {

    /**
     * Read digital value fom the pin
     *
     * @param port port
     * @return firmata message
     */
    ReportDigitalPortMessage digitalRead(int port) throws MessageValidationException;

    /**
     * Read analog value from the pin
     *
     * @param pin pin
     * @return firmata message
     */
    ReportAnalogPinMessage analogRead(int pin) throws MessageValidationException;

    /**
     * Set a digital pin to input or output mode
     *
     * @param pin pin
     * @param mode message
     * @see SetPinModeMessage.PIN_MODE
     * @return firmata message
     */
    SetPinModeMessage pinMode(int pin, int mode) throws MessageValidationException;

    /**
     * Write to a digital pin
     *
     * @param port port
     * @param value pins mask
     * @return firmata message
     */
    DigitalMessage digitalWrite(int port, int value) throws MessageValidationException;

    /**
     * Write an analog value (PWM-wave) to a pin.
     *
     * @param pin pin
     * @return firmata message
     */
    AnalogMessage analogWrite(int pin, int value) throws MessageValidationException;

}

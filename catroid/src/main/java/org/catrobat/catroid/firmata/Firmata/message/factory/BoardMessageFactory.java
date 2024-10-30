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

import java.text.MessageFormat;
import java.util.Arrays;

/**
 * MessageFactory implementation with pins config
 */
public abstract class BoardMessageFactory implements MessageFactory {

    public static final int MIN_PIN = 0;

    protected int minPin;
    protected int maxPin;
    protected int[] analogOutPins;
    protected int[] analogInPins;

    public int getMinPin() {
        return minPin;
    }

    public int getMaxPin() {
        return maxPin;
    }

    public int[] getAnalogInPins() {
        return analogInPins;
    }

    public int[] getAnalogOutPins() {
        return analogOutPins;
    }
    
    protected static int[] union(int[] array1, int[] array2) {
        int[] array = new int[array1.length + array2.length];
        System.arraycopy(array1, 0, array, 0, array1.length);
        System.arraycopy(array2, 0, array, array1.length, array2.length);
        return array;
    }
    
    protected static int[] arrayFromTo(int from, int to) {
        int[] array = new int[to - from + 1];
        for (int i=0; i<array.length; i++) {
            array[i] = from++;
        }
        return array;
    }

    public BoardMessageFactory(int minPin, int maxPin, int[] analogInPins, int[] analogOutPins) {
        this.minPin = minPin;
        this.maxPin = maxPin;
        this.analogInPins = analogInPins;
        this.analogOutPins = analogOutPins;

        // sort() in order to allow use binarySearch()
        Arrays.sort(analogInPins);
        Arrays.sort(analogOutPins);
    }

    protected void validatePin(int pin) throws MessageValidationException {
        if (pin < minPin || pin > maxPin)
            throw new MessageValidationException(
                MessageFormat.format("Allowed pin values are [{0}-{1}]", minPin, maxPin));
    }

    protected void validatePort(int port) throws MessageValidationException {
        int ports = (int)Math.ceil((maxPin + 1) / 8.0);
        if (port < 0 || port > ports)
            throw new MessageValidationException(
                MessageFormat.format("Allowed port values are [{0}-{1}]", 0, ports));
    }

    protected void validateAnalogIn(int pin) throws MessageValidationException {
        int[] array = analogInPins;
        if (Arrays.binarySearch(array, pin) < 0)
            throw new MessageValidationException(
                    MessageFormat.format("Allowed analog in pins are [{0}]", arrayToString(array)));
    }

    public boolean isAnalogIn(int pin) {
        try {
            validateAnalogIn(pin);
            return true;
        } catch (MessageValidationException e) {
            return false;
        }
    }

    protected void validateAnalogOut(int pin) throws MessageValidationException {
        int[] array = analogOutPins;
        if (Arrays.binarySearch(array, pin) < 0)
            throw new MessageValidationException(
                MessageFormat.format("Allowed analog out (PWM) pins are [{0}]", arrayToString(array)));
    }

    public boolean isAnalogOut(int pin) {
        try {
            validateAnalogOut(pin);
            return true;
        } catch (MessageValidationException e) {
            return false;
        }
    }

    protected String arrayToString(int[] array) {
        StringBuilder sb = new StringBuilder();
        for (int i=0; i<array.length; i++) {
            if (i>0)
                sb.append(", ");
            sb.append(array[i]);
        }
        return sb.toString();
    }

    protected void validateMode(int mode) throws MessageValidationException {
        SetPinModeMessage.PIN_MODE enumValue = SetPinModeMessage.PIN_MODE.find(mode);
        if (enumValue == null)
            throw new MessageValidationException(
                MessageFormat.format("Allowed modes are [{0}]", SetPinModeMessage.PIN_MODE.values()));
    }

    protected void validateDigitalValue(int value) throws MessageValidationException {
        if (value != 0 && value != 1)
            throw new MessageValidationException("Allowed digital values are [0; 1]");
    }

    protected void validateDigitalMask(int value) throws MessageValidationException {
        if (value < 0 || value > 255)
            throw new MessageValidationException("Allowed digital mask values are [0-255]");
    }

    private void validateAnalogValue(int value) throws MessageValidationException {
        if (value < 0 || value > 255)
            throw new MessageValidationException("Allowed analog values are [0-255]");
    }

    public ReportDigitalPortMessage digitalRead(int port) throws MessageValidationException {
        validatePort(port);

        return new ReportDigitalPortMessage(port, true);
    }

    public ReportAnalogPinMessage analogRead(int pin) throws MessageValidationException {
        validatePin(pin);
        validateAnalogIn(pin);

        return new ReportAnalogPinMessage(pin, true);
    }

    public SetPinModeMessage pinMode(int pin, int mode) throws MessageValidationException {
        validatePin(pin);
        validateMode(mode);

        // analog in
        if (mode == SetPinModeMessage.PIN_MODE.ANALOG.getMode())
            validateAnalogIn(pin);

        // analog out
        if (mode == SetPinModeMessage.PIN_MODE.PWM.getMode())
            validateAnalogOut(pin);

        return new SetPinModeMessage(pin, mode);
    }

    public DigitalMessage digitalWrite(int port, int value) throws MessageValidationException {
        validatePort(port);
        validateDigitalMask(value);

        return new DigitalMessage(port, value);
    }

    public AnalogMessage analogWrite(int pin, int value) throws MessageValidationException {
        validatePin(pin);
        validateAnalogOut(pin);
        validateAnalogValue(value);

        return new AnalogMessage(pin, value);
    }

}

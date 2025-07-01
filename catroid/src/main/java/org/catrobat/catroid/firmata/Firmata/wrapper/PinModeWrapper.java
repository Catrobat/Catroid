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

package org.catrobat.catroid.firmata.Firmata.wrapper;

import org.catrobat.catroid.firmata.Firmata.IFirmata;
import org.catrobat.catroid.firmata.Firmata.message.Message;
import org.catrobat.catroid.firmata.Firmata.message.SetPinModeMessage;
import org.catrobat.catroid.firmata.Serial.SerialException;

import java.util.HashMap;
import java.util.Map;

/**
 * Wrapper which remembers pin modes
 */
public class PinModeWrapper implements IFirmata {

    /**
     * Set pin mode event listener
     */
    public static interface Listener {
        void onSetPinMode(int pin, int mode);
    }

    private IFirmata firmata;
    private Listener listener;

    public void addListener(IFirmata.Listener listener) {
        firmata.addListener(listener);
    }

    public void removeListener(IFirmata.Listener listener) {
        firmata.removeListener(listener);
    }

    public boolean containsListener(IFirmata.Listener listener) {
        return firmata.containsListener(listener);
    }

    public void clearListeners() {
        firmata.clearListeners();
    }

    // pins configuration
    private Map<Integer, Integer> pinsConfig = new HashMap<Integer, Integer>();

    /**
     * Get remembered pin modes
     * @return pin modes
     */
    public Map<Integer, Integer> getPinsConfig() {
        return pinsConfig;
    }

    public PinModeWrapper(IFirmata firmata) {
        this(firmata, null);
    }

    /**
     * Constructor
     * @param firmata wrapped firmata
     * @param listener set pin mode event listener
     */
    public PinModeWrapper(IFirmata firmata, Listener listener) {
        this.firmata = firmata;
        this.listener = listener;
        
        clear();
    }

    /**
     * Clear pins config
     * (should be invoked on serial.stop())
     */
    public void clear() {
        pinsConfig.clear();
    }

    public void send(Message message) throws SerialException {
        firmata.send(message);

        if (message instanceof SetPinModeMessage) {
            SetPinModeMessage setPinModeMessage = (SetPinModeMessage) message;

            // remember
            pinsConfig.put(setPinModeMessage.getPin(), setPinModeMessage.getMode());

            // fire event
            if (listener != null)
                listener.onSetPinMode(setPinModeMessage.getPin(), setPinModeMessage.getMode());
        }
    }

    public void onDataReceived(int incomingByte) {
        firmata.onDataReceived(incomingByte);
    }
}

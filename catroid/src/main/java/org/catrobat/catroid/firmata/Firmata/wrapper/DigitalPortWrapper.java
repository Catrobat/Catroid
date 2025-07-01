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
import org.catrobat.catroid.firmata.Firmata.message.DigitalMessage;
import org.catrobat.catroid.firmata.Firmata.message.Message;
import org.catrobat.catroid.firmata.Serial.SerialException;

import java.util.HashMap;
import java.util.Map;

public class DigitalPortWrapper implements IFirmata {

    private IFirmata firmata;

    public DigitalPortWrapper(IFirmata firmata) {
        this.firmata = firmata;
    }

    public void addListener(Listener listener) {
        firmata.addListener(listener);
    }

    public void removeListener(Listener listener) {
        firmata.removeListener(listener);
    }

    public boolean containsListener(Listener listener) {
        return firmata.containsListener(listener);
    }

    public void clearListeners() {
        firmata.clearListeners();
    }

    private Map<Integer, Integer> portValues = new HashMap<Integer, Integer>();

    public Map<Integer, Integer> getPortValues() {
        return portValues;
    }

    public void clear() {
        portValues.clear();
    }

    public void send(Message message) throws SerialException {
        firmata.send(message);

        if (message instanceof DigitalMessage) {
            DigitalMessage digitalMessage = (DigitalMessage) message;
            portValues.put(digitalMessage.getPort(), digitalMessage.getValue());
        }
    }

    public void onDataReceived(int incomingByte) {
        firmata.onDataReceived(incomingByte);
    }
}

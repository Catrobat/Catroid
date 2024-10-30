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
import org.catrobat.catroid.firmata.Firmata.message.AnalogMessage;
import org.catrobat.catroid.firmata.Firmata.message.DigitalMessage;
import org.catrobat.catroid.firmata.Firmata.message.Message;
import org.catrobat.catroid.firmata.Firmata.message.ReportAnalogPinMessage;
import org.catrobat.catroid.firmata.Firmata.message.ReportDigitalPortMessage;
import org.catrobat.catroid.firmata.Serial.SerialException;

import java.util.HashSet;
import java.util.Set;

/**
 * Disables pin state reporting after first state message received
 */
public class ReportAutostopWrapper extends IFirmata.StubListener implements IFirmata {
    
    private IFirmata firmata;

    public ReportAutostopWrapper(IFirmata firmata) {
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

    // active reporting pins
    private Set<Integer> digitalReporting = new HashSet<Integer>(); // integer - port
    private Set<Integer> analogReporting = new HashSet<Integer>();  // integer - pin

    public void send(Message message) throws SerialException {
        firmata.send(message);

        // digital
        if (message instanceof ReportDigitalPortMessage) {
            ReportDigitalPortMessage digitalMessage = (ReportDigitalPortMessage) message;
            if (digitalMessage.isEnable())
                digitalReporting.add(digitalMessage.getPort());
        }

        // analog
        if (message instanceof ReportAnalogPinMessage) {
            ReportAnalogPinMessage analogMessage = (ReportAnalogPinMessage) message;
            if (analogMessage.isEnable())
                analogReporting.add(analogMessage.getPin());
        }
    }

    @Override
    public void onAnalogMessageReceived(AnalogMessage message) {
        if (analogReporting.contains(message.getPin())) {
            analogReporting.remove(message.getPin());

            disableAnalogReporting(message);
        }
    }

    private void disableAnalogReporting(AnalogMessage message) {
        try {
            firmata.send(new ReportAnalogPinMessage(message.getPin(), false));
        } catch (SerialException e) {
        // TODO: fix (bad)
        }
    }

    @Override
    public void onDigitalMessageReceived(DigitalMessage message) {
        if (digitalReporting.contains(message.getPort())) {
            digitalReporting.remove(message.getPort());

            disableDigitalReporting(message);
        }
    }

    private void disableDigitalReporting(DigitalMessage message) {
        try {
            firmata.send(new ReportDigitalPortMessage(message.getPort(), false));
        } catch (SerialException e) {
            // TODO: fix (bad)
        }
    }

    public void clear() {
        digitalReporting.clear();
        analogReporting.clear();
    }

    public void onDataReceived(int incomingByte) {
        firmata.onDataReceived(incomingByte);
    }


}

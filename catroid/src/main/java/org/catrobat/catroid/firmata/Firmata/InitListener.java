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

package org.catrobat.catroid.firmata.Firmata;

import org.catrobat.catroid.firmata.Firmata.message.FirmwareVersionMessage;
import org.catrobat.catroid.firmata.Firmata.message.ProtocolVersionMessage;

/**
 * Init Listener
 */
public class InitListener extends IFirmata.StubListener {

    /**
     * Init wrapper listener
     */
    public static interface Listener {
        void onInitialized();
    }

    private Listener listener;

    private FirmwareVersionMessage firmware;

    public FirmwareVersionMessage getFirmware() {
        return firmware;
    }

    private ProtocolVersionMessage protocol;

    public ProtocolVersionMessage getProtocol() {
        return protocol;
    }

    public InitListener(Listener listener) {
        this.listener = listener;
        clear();
    }

    public void clear() {
        firmware = null;
        protocol = null;
    }

    private void checkInitAndFire() {
        if (firmware != null && protocol != null)
            listener.onInitialized();
    }

    public boolean isInitialized() {
        return firmware != null && protocol != null;
    }

    public void onFirmwareVersionMessageReceived(FirmwareVersionMessage message) {
        this.firmware = message;
        checkInitAndFire();
    }

    public void onProtocolVersionMessageReceived(ProtocolVersionMessage message) {
        this.protocol = message;
        checkInitAndFire();
    }
}

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
 * Sampling interval message
 */
public class SamplingIntervalMessage extends SysexMessage {

    // Sysex command byte
    public static final int COMMAND = 0x7A;

    public SamplingIntervalMessage() {
        super(COMMAND, null);
    }

    private int interval;

    public int getInterval() {
        return interval;
    }

    /**
     * @param interval (sampling interval on the millisecond time scale)
     */
    public void setInterval(int interval) {
        this.interval = interval;
    }

    @Override
    public String toString() {
        return MessageFormat.format("SamplingIntervalMessage[interval={0}]", interval);
    }
}

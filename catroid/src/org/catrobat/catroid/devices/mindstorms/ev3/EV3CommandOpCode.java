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

package org.catrobat.catroid.devices.mindstorms.ev3;

import android.util.SparseArray;

public enum EV3CommandOpCode {
	OP_UI_READ(0x81), OP_UI_WRITE(0x82),

	OP_SOUND(0x94), OP_SOUND_TEST(0x95),

	OP_OUTPUT_STEP_SPEED(0xAE), OP_OUTPUT_STEP_POWER(0xAC), OP_OUTPUT_TIME_SPEED(0xAF), OP_OUTPUT_TIME_POWER(0xAD),

	OP_OUTPUT_STOP(0xA3);

	private int commandByteValue;
	private static final SparseArray<EV3CommandOpCode> LOOKUP = new SparseArray<EV3CommandOpCode>();

	static {
		for (EV3CommandOpCode c : EV3CommandOpCode.values()) {
			LOOKUP.put(c.commandByteValue, c);
		}
	}

	private EV3CommandOpCode(int commandByteValue) {
		this.commandByteValue = commandByteValue;
	}

	public byte getByte() {
		return (byte) commandByteValue;
	}

	public static boolean isMember(byte memberToTest) {
		return LOOKUP.get(memberToTest & 0xFF) != null;
	}

	public static EV3CommandOpCode getTypeByValue(byte value) {
		return LOOKUP.get(value & 0xFF);
	}
}

/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2017 The Catrobat Team
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

public class EV3CommandByte {

	public enum EV3CommandParamByteCode {

		PARAM_TYPE_CONSTANT(0x00), PARAM_TYPE_VARIABLE(0x40),

		PARAM_CONST_TYPE_VALUE(0x00), PARAM_CONST_TYPE_LABEL(0x20),

		PARAM_FOLLOW_ONE_BYTE(0x01), PARAM_FOLLOW_TWO_BYTE(0x02),
		PARAM_FOLLOW_FOUR_BYTE(0x03),
		PARAM_FOLLOW_TERMINATED(0x00), PARAM_FOLLOW_TERMINATED2(0x04),

		PARAM_SHORT_MAX(0x1F), PARAM_SHORT_SIGN_POSITIVE(0x00), PARAM_SHORT_SIGN_NEGATIVE(0x20);

		private int commandParamByteCode;

		EV3CommandParamByteCode(int commandParamByteCode) {
			this.commandParamByteCode = commandParamByteCode;
		}

		public byte getByte() {
			return (byte) commandParamByteCode;
		}
	}

	public enum EV3CommandVariableScope {
		PARAM_VARIABLE_SCOPE_LOCAL(0x00), PARAM_VARIABLE_SCOPE_GLOBAL(0x20);

		private int variableScope;

		EV3CommandVariableScope(int variableScope) {
			this.variableScope = variableScope;
		}

		public byte getByte() {
			return (byte) variableScope;
		}
	}

	public enum EV3CommandParamFormat {
		PARAM_FORMAT_SHORT(0x00), PARAM_FORMAT_LONG(0x80);

		private int commandParamFormat;

		EV3CommandParamFormat(int commandParamFormat) {
			this.commandParamFormat = commandParamFormat;
		}

		public byte getByte() {
			return (byte) commandParamFormat;
		}
	}

	public enum EV3CommandByteCode {

		SOUND_PLAY_TONE(0x01),

		UI_WRITE_LED(0x1B),
		UI_READ_GET_VBATT(0x01),

		INPUT_DEVICE_GET_FORMAT(0x02), INPUT_DEVICE_SETUP(0x09), INPUT_DEVICE_GET_RAW(0x0B),
		INPUT_DEVICE_STOP_ALL(0x0D), INPUT_DEVICE_READY_RAW(0x1C), INPUT_DEVICE_READY_SI(0x1D);

		private int commandByteCode;

		EV3CommandByteCode(int commandByteCode) {
			this.commandByteCode = commandByteCode;
		}

		public byte getByte() {
			return (byte) commandByteCode;
		}
	}

	public enum EV3CommandOpCode {
		OP_UI_READ(0x81), OP_UI_WRITE(0x82),

		OP_KEEP_ALIVE(0x90),

		OP_SOUND(0x94), OP_SOUND_TEST(0x95),

		OP_INPUT_DEVICE(0x99), OP_INPUT_READ(0x9A), OP_INPUT_READ_SI(0x9D),

		OP_OUTPUT_POWER(0xA4), OP_OUTPUT_SPEED(0xA5), OP_OUTPUT_START(0xA6),

		OP_OUTPUT_STEP_SPEED(0xAE), OP_OUTPUT_STEP_POWER(0xAC), OP_OUTPUT_TIME_SPEED(0xAF), OP_OUTPUT_TIME_POWER(0xAD),

		OP_OUTPUT_STOP(0xA3);

		private int commandByteValue;
		private static final SparseArray<EV3CommandOpCode> LOOKUP = new SparseArray<EV3CommandOpCode>();
		static {
			for (EV3CommandOpCode c : EV3CommandOpCode.values()) {
				LOOKUP.put(c.commandByteValue, c);
			}
		}
		EV3CommandOpCode(int commandByteValue) {
			this.commandByteValue = commandByteValue;
		}

		public byte getByte() {
			return (byte) commandByteValue;
		}

		public static boolean isMember(byte memberToTest) {
			return LOOKUP.get(memberToTest & 0xFF) != null;
		}

		public static EV3CommandOpCode getOpCodeByValue(byte value) {
			return LOOKUP.get(value & 0xFF);
		}
	}
}

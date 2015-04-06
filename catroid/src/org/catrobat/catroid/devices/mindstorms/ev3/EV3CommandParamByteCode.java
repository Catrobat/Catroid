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

public enum EV3CommandParamByteCode {
	PARAM_FORMAT_SHORT(0x00), PARAM_FORMAT_LONG(0x80),

	PARAM_TYPE_CONSTANT(0x00), PARAM_TYPE_VARIABLE(0x40),

	PARAM_CONST_TYPE_VALUE(0x00), PARAM_CONST_TYPE_LABEL(0x20),

	PARAM_VARIABLE_SCOPE_LOCAL(0x00), PARAM_VARIABLE_SCOPE_GLOBAL(0x20),

	PARAM_FOLLOW_ONE_BYTE(0x01), PARAM_FOLLOW_TWO_BYTE(0x02),
	PARAM_FOLLOW_FOUR_BYTE(0x03),
	PARAM_FOLLOW_TERMINATED(0x00), PARAM_FOLLOW_TERMINATED2(0x04);

	private int commandParamByteCode;

	private EV3CommandParamByteCode(int commandParamByteCode) {
		this.commandParamByteCode = commandParamByteCode;
	}

	public byte getByte() {
		return (byte) commandParamByteCode;
	}
}

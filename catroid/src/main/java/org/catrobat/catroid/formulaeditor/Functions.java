/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2018 The Catrobat Team
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
package org.catrobat.catroid.formulaeditor;

import org.catrobat.catroid.utils.EnumUtils;

import java.util.EnumSet;

public enum Functions {

	SIN, COS, TAN, LN, LOG, SQRT, RAND, ROUND, ABS, PI, MOD, ARCSIN, ARCCOS, ARCTAN, ARCTAN2, EXP, POWER, FLOOR, CEIL,
	MAX,
	MIN, TRUE, FALSE, LENGTH,
	LETTER, JOIN, REGEX, LIST_ITEM, CONTAINS, INDEX_OF_ITEM, NUMBER_OF_ITEMS, ARDUINOANALOG,
	ARDUINODIGITAL, RASPIDIGITAL,
	MULTI_FINGER_X, MULTI_FINGER_Y, MULTI_FINGER_TOUCHED, COLLIDES_WITH_COLOR, COLOR_TOUCHES_COLOR, COLOR_AT_XY,
	TEXT_BLOCK_X, TEXT_BLOCK_Y, TEXT_BLOCK_SIZE, IF_THEN_ELSE;

	private static final String TAG = Functions.class.getSimpleName();
	public static final EnumSet<Functions> TEXT = EnumSet.of(LENGTH, LETTER, JOIN, REGEX);
	public static final EnumSet<Functions> LIST = EnumSet.of(LIST_ITEM, CONTAINS, INDEX_OF_ITEM,
			NUMBER_OF_ITEMS);

	public static boolean isFunction(String value) {
		return EnumUtils.isValidEnum(Functions.class, value);
	}

	public static Functions getFunctionByValue(String value) {
		return EnumUtils.getEnum(Functions.class, value);
	}
}

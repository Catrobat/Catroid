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
package org.catrobat.catroid.formulaeditor;


import android.util.Log;

import org.catrobat.catroid.formulaeditor.FormulaElement.ElementType;

public enum Functions {

	SIN(ElementType.NUMBER), COS(ElementType.NUMBER), TAN(ElementType.NUMBER), LN(ElementType.NUMBER), LOG(
			ElementType.NUMBER), SQRT(ElementType.NUMBER), RAND(ElementType.NUMBER), ROUND(ElementType.NUMBER), ABS(
			ElementType.NUMBER), PI(ElementType.NUMBER), MOD(ElementType.NUMBER), ARCSIN(ElementType.NUMBER), ARCCOS(
			ElementType.NUMBER), ARCTAN(ElementType.NUMBER), EXP(ElementType.NUMBER), MAX(ElementType.NUMBER), MIN(
			ElementType.NUMBER), TRUE(ElementType.NUMBER), FALSE(ElementType.NUMBER), LENGTH(ElementType.NUMBER), LETTER(
			ElementType.STRING), JOIN(ElementType.STRING);

	ElementType returnType;

	Functions(ElementType type) {
		returnType = type;
	}

	private static final String TAG = Functions.class.getSimpleName();

	public static boolean isFunction(String value) {
		if (getFunctionByValue(value) == null) {
			return false;
		}
		return true;
	}

	public static Functions getFunctionByValue(String value) {
		try {
			return valueOf(value);
		} catch (IllegalArgumentException illegalArgumentException) {
			Log.e(TAG, Log.getStackTraceString(illegalArgumentException));
		}
		return null;
	}

}

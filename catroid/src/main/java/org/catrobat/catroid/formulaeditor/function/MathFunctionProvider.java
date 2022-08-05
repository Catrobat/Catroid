/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2022 The Catrobat Team
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

package org.catrobat.catroid.formulaeditor.function;

import org.catrobat.catroid.formulaeditor.Functions;

import java.util.Map;

import static org.catrobat.catroid.formulaeditor.common.Conversions.FALSE;
import static org.catrobat.catroid.formulaeditor.common.Conversions.TRUE;

public class MathFunctionProvider implements FunctionProvider {
	@Override
	public void addFunctionsToMap(Map<Functions, FormulaFunction> formulaFunctions) {
		formulaFunctions.put(Functions.SIN, new UnaryFunction(argument -> Math.sin(Math.toRadians(argument))));
		formulaFunctions.put(Functions.COS, new UnaryFunction(argument -> Math.cos(Math.toRadians(argument))));
		formulaFunctions.put(Functions.TAN, new UnaryFunction(argument -> Math.tan(Math.toRadians(argument))));
		formulaFunctions.put(Functions.ARCTAN2, new BinaryFunction(this::interpretFunctionArcTan2));
		formulaFunctions.put(Functions.LN, new UnaryFunction(Math::log));
		formulaFunctions.put(Functions.LOG, new UnaryFunction(Math::log10));
		formulaFunctions.put(Functions.SQRT, new UnaryFunction(Math::sqrt));
		formulaFunctions.put(Functions.ABS, new UnaryFunction(Math::abs));
		formulaFunctions.put(Functions.ROUND, new UnaryFunction(argument -> (double) Math.round(argument)));
		formulaFunctions.put(Functions.PI, args -> Math.PI);
		formulaFunctions.put(Functions.ARCSIN, new UnaryFunction(argument -> Math.toDegrees(Math.asin(argument))));
		formulaFunctions.put(Functions.ARCCOS, new UnaryFunction(argument -> Math.toDegrees(Math.acos(argument))));
		formulaFunctions.put(Functions.ARCTAN, new UnaryFunction(argument -> Math.toDegrees(Math.atan(argument))));
		formulaFunctions.put(Functions.EXP, new UnaryFunction(Math::exp));
		formulaFunctions.put(Functions.POWER, new BinaryFunction(Math::pow));
		formulaFunctions.put(Functions.FLOOR, new UnaryFunction(Math::floor));
		formulaFunctions.put(Functions.CEIL, new UnaryFunction(Math::ceil));
		formulaFunctions.put(Functions.MAX, new BinaryFunction(Math::max));
		formulaFunctions.put(Functions.MIN, new BinaryFunction(Math::min));
		formulaFunctions.put(Functions.TRUE, args -> TRUE);
		formulaFunctions.put(Functions.FALSE, args -> FALSE);
		formulaFunctions.put(Functions.MOD, new BinaryFunction(this::interpretFunctionMod));
	}

	private double interpretFunctionMod(double dividend, double divisor) {
		if (dividend == 0 || divisor == 0) {
			return dividend;
		}

		if (divisor > 0) {
			while (dividend < 0) {
				dividend += Math.abs(divisor);
			}
		} else {
			if (dividend > 0) {
				return (dividend % divisor) + divisor;
			}
		}

		return dividend % divisor;
	}

	private double interpretFunctionArcTan2(double firstArgument, double secondArgument) {
		if ((firstArgument == 0) && (secondArgument == 0)) {
			return Math.random() * 360 - 180;
		} else {
			return Math.toDegrees(Math.atan2(firstArgument, secondArgument));
		}
	}
}

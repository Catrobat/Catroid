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

package org.catrobat.catroid.formula.function

import org.catrobat.catroid.R
import org.catrobat.catroid.formula.FormulaInterpreter
import org.catrobat.catroid.formula.Token
import org.catrobat.catroid.formula.value.ValueToken

abstract class UnaryFunctionToken(val tokens: List<Token>) : FunctionToken() {

    class Sin(tokens: List<Token>) : UnaryFunctionToken(tokens) {

        override fun getResourceId() = R.string.formula_editor_function_sin

        override fun eval() = ValueToken(Math.sin(FormulaInterpreter().eval(tokens).value))
    }

    class Cos(tokens: List<Token>) : UnaryFunctionToken(tokens) {

        override fun getResourceId() = R.string.formula_editor_function_cos

        override fun eval() = ValueToken(Math.cos(FormulaInterpreter().eval(tokens).value))
    }

    class Tan(tokens: List<Token>) : UnaryFunctionToken(tokens) {

        override fun getResourceId() = R.string.formula_editor_function_tan

        override fun eval() = ValueToken(Math.tan(FormulaInterpreter().eval(tokens).value))
    }

    class Ln(tokens: List<Token>) : UnaryFunctionToken(tokens) {

        override fun getResourceId() = R.string.formula_editor_function_ln

        override fun eval() = ValueToken(Math.log(FormulaInterpreter().eval(tokens).value))
    }

    class Lg(tokens: List<Token>) : UnaryFunctionToken(tokens) {

        override fun getResourceId() = R.string.formula_editor_function_log

        override fun eval() = ValueToken(Math.log10(FormulaInterpreter().eval(tokens).value))
    }

    class Sqrt(tokens: List<Token>) : UnaryFunctionToken(tokens) {

        override fun getResourceId() = R.string.formula_editor_function_sqrt

        override fun eval() = ValueToken(Math.sqrt(FormulaInterpreter().eval(tokens).value))
    }

    class Abs(tokens: List<Token>) : UnaryFunctionToken(tokens) {

        override fun getResourceId() = R.string.formula_editor_function_abs

        override fun eval(): ValueToken = ValueToken(Math.abs(FormulaInterpreter().eval(tokens).value))
    }

    class Asin(tokens: List<Token>) : UnaryFunctionToken(tokens) {

        override fun getResourceId() = R.string.formula_editor_function_arcsin

        override fun eval() = ValueToken(Math.asin(FormulaInterpreter().eval(tokens).value))
    }

    class Acos(tokens: List<Token>) : UnaryFunctionToken(tokens) {

        override fun getResourceId() = R.string.formula_editor_function_arccos

        override fun eval() = ValueToken(Math.acos(FormulaInterpreter().eval(tokens).value))
    }

    class Atan(tokens: List<Token>) : UnaryFunctionToken(tokens) {

        override fun getResourceId() = R.string.formula_editor_function_arctan

        override fun eval() = ValueToken(Math.atan(FormulaInterpreter().eval(tokens).value))
    }

    class Exp(tokens: List<Token>) : UnaryFunctionToken(tokens) {

        override fun getResourceId() = R.string.formula_editor_function_exp

        override fun eval() = ValueToken(Math.exp(FormulaInterpreter().eval(tokens).value))
    }

    class Floor(tokens: List<Token>) : UnaryFunctionToken(tokens) {

        override fun getResourceId() = R.string.formula_editor_function_floor

        override fun eval() = ValueToken(Math.floor(FormulaInterpreter().eval(tokens).value))
    }

    class Ceil(tokens: List<Token>) : UnaryFunctionToken(tokens) {

        override fun getResourceId() = R.string.formula_editor_function_ceil

        override fun eval() = ValueToken(Math.ceil(FormulaInterpreter().eval(tokens).value))
    }

    class Round(tokens: List<Token>) : UnaryFunctionToken(tokens) {

        override fun getResourceId() = R.string.formula_editor_function_round

        override fun eval() = ValueToken(Math.round(FormulaInterpreter().eval(tokens).value).toDouble())
    }
}

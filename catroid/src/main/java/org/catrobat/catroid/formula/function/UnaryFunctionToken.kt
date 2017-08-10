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

import org.catrobat.catroid.formula.FormulaInterpreter
import org.catrobat.catroid.formula.Token
import org.catrobat.catroid.formula.value.ValueToken
import org.catrobat.catroid.formula.value.ValueToken.NumericValueToken

abstract class UnaryFunctionToken<out V : ValueToken>(val tokens: List<Token>) : FunctionToken<V>() {

    class Sin(tokens: List<Token>) : UnaryFunctionToken<NumericValueToken>(tokens) {

        override fun getString(): String {
            var formula = "sin( "

            for (token in tokens) {
                formula += token.getString()
            }

            return formula + ") "
        }

        override fun eval(): NumericValueToken {
            return NumericValueToken(Math.sin(FormulaInterpreter<NumericValueToken>().eval(tokens).value))
        }
    }

    class Cos(tokens: List<Token>) : UnaryFunctionToken<NumericValueToken>(tokens) {

        override fun getString(): String {
            var formula = "cos( "

            for (token in tokens) {
                formula += token.getString()
            }

            return formula + ") "
        }

        override fun eval(): NumericValueToken {
            return NumericValueToken(Math.cos(FormulaInterpreter<NumericValueToken>().eval(tokens).value))
        }
    }

    class Tan(tokens: List<Token>) : UnaryFunctionToken<NumericValueToken>(tokens) {

        override fun getString(): String {
            var formula = "tan( "

            for (token in tokens) {
                formula += token.getString()
            }

            return formula + ") "
        }

        override fun eval(): NumericValueToken {
            return NumericValueToken(Math.tan(FormulaInterpreter<NumericValueToken>().eval(tokens).value))
        }
    }

    class Ln(tokens: List<Token>) : UnaryFunctionToken<NumericValueToken>(tokens) {

        override fun getString(): String {
            var formula = "ln( "

            for (token in tokens) {
                formula += token.getString()
            }

            return formula + ") "
        }

        override fun eval(): NumericValueToken {
            return NumericValueToken(Math.log(FormulaInterpreter<NumericValueToken>().eval(tokens).value))
        }
    }

    class Lg(tokens: List<Token>) : UnaryFunctionToken<NumericValueToken>(tokens) {

        override fun getString(): String {
            var formula = "log( "

            for (token in tokens) {
                formula += token.getString()
            }

            return formula + ") "
        }

        override fun eval(): NumericValueToken {
            return NumericValueToken(Math.log10(FormulaInterpreter<NumericValueToken>().eval(tokens).value))
        }
    }

    class Sqrt(tokens: List<Token>) : UnaryFunctionToken<NumericValueToken>(tokens) {

        override fun getString(): String {
            var formula = "sqrt( "

            for (token in tokens) {
                formula += token.getString()
            }

            return formula + ") "
        }

        override fun eval(): NumericValueToken {
            return NumericValueToken(Math.sqrt(FormulaInterpreter<NumericValueToken>().eval(tokens).value))
        }
    }

    class Abs(tokens: List<Token>) : UnaryFunctionToken<NumericValueToken>(tokens) {

        override fun getString(): String {
            var formula = "abs( "

            for (token in tokens) {
                formula += token.getString()
            }

            return formula + ") "
        }

        override fun eval(): NumericValueToken {
            return NumericValueToken(Math.abs(FormulaInterpreter<NumericValueToken>().eval(tokens).value))
        }
    }

    class Asin(tokens: List<Token>) : UnaryFunctionToken<NumericValueToken>(tokens) {

        override fun getString(): String {
            var formula = "arcsin( "

            for (token in tokens) {
                formula += token.getString()
            }

            return formula + ") "
        }

        override fun eval(): NumericValueToken {
            return NumericValueToken(Math.asin(FormulaInterpreter<NumericValueToken>().eval(tokens).value))
        }
    }

    class Acos(tokens: List<Token>) : UnaryFunctionToken<NumericValueToken>(tokens) {

        override fun getString(): String {
            var formula = "arccos( "

            for (token in tokens) {
                formula += token.getString()
            }

            return formula + ") "
        }

        override fun eval(): NumericValueToken {
            return NumericValueToken(Math.acos(FormulaInterpreter<NumericValueToken>().eval(tokens).value))
        }
    }

    class Atan(tokens: List<Token>) : UnaryFunctionToken<NumericValueToken>(tokens) {

        override fun getString(): String {
            var formula = "arctan( "

            for (token in tokens) {
                formula += token.getString()
            }

            return formula + ") "
        }

        override fun eval(): NumericValueToken {
            return NumericValueToken(Math.atan(FormulaInterpreter<NumericValueToken>().eval(tokens).value))
        }
    }

    class Exp(tokens: List<Token>) : UnaryFunctionToken<NumericValueToken>(tokens) {

        override fun getString(): String {
            var formula = "exp( "

            for (token in tokens) {
                formula += token.getString()
            }

            return formula + ") "
        }

        override fun eval(): NumericValueToken {
            return NumericValueToken(Math.exp(FormulaInterpreter<NumericValueToken>().eval(tokens).value))
        }
    }

    class Floor(tokens: List<Token>) : UnaryFunctionToken<NumericValueToken>(tokens) {

        override fun getString(): String {
            var formula = "floor( "

            for (token in tokens) {
                formula += token.getString()
            }

            return formula + ") "
        }

        override fun eval(): NumericValueToken {
            return NumericValueToken(Math.floor(FormulaInterpreter<NumericValueToken>().eval(tokens).value))
        }
    }

    class Ceil(tokens: List<Token>) : UnaryFunctionToken<NumericValueToken>(tokens) {

        override fun getString(): String {
            var formula = "ceil( "

            for (token in tokens) {
                formula += token.getString()
            }

            return formula + ") "
        }

        override fun eval(): NumericValueToken {
            return NumericValueToken(Math.ceil(FormulaInterpreter<NumericValueToken>().eval(tokens).value))
        }
    }

    class Round(tokens: List<Token>) : UnaryFunctionToken<NumericValueToken>(tokens) {

        override fun getString(): String {
            var formula = "round( "

            for (token in tokens) {
                formula += token.getString()
            }

            return formula + ") "
        }

        override fun eval(): NumericValueToken {
            return NumericValueToken(Math.round(FormulaInterpreter<NumericValueToken>().eval(tokens).value).toDouble())
        }
    }
}

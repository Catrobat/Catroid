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

package org.catrobat.catroid.formula.operator


import org.catrobat.catroid.formula.FormulaInterpreter
import org.catrobat.catroid.formula.value.ValueToken

abstract class BinaryOperatorToken : OperatorToken(Type.OPERATOR) {

    /**
     * The "inverted" parameter order (rightToken, leftToken) is used on purpose because the value stack
     * {@link #eval(List<Token> tokens) FormulaInterpreter} contains the values in this order.
     */
    abstract fun applyTo (rightToken: ValueToken, leftToken: ValueToken) : ValueToken

    class MultOperatorToken : BinaryOperatorToken() {

        override fun getString(): String {
            return "* "
        }

        override fun getPriority(): Int {
            return 2
        }

        override fun applyTo(rightToken: ValueToken, leftToken: ValueToken): ValueToken {
            return ValueToken(leftToken.value * rightToken.value)
        }
    }

    class DivOperatorToken : BinaryOperatorToken() {

        override fun getString(): String {
            return "/ "
        }

        override fun getPriority(): Int {
            return 2
        }

        override fun applyTo(rightToken: ValueToken, leftToken: ValueToken): ValueToken {
            if (rightToken.value == 0.0) throw Exception("DIVIDED BY 0")
            return ValueToken(leftToken.value / rightToken.value)
        }
    }

    class AddOperatorToken : BinaryOperatorToken() {

        override fun getString(): String {
            return "+ "
        }

        override fun getPriority(): Int {
            return 1
        }

        override fun applyTo(rightToken: ValueToken, leftToken: ValueToken): ValueToken {
            return ValueToken(leftToken.value + rightToken.value)
        }
    }

    class SubOperatorToken : BinaryOperatorToken() {

        override fun getString(): String {
            return "- "
        }

        override fun getPriority(): Int {
            return 1
        }

        override fun applyTo(rightToken: ValueToken, leftToken: ValueToken): ValueToken {
            return ValueToken(leftToken.value - rightToken.value)
        }
    }

    class AndOperatorToken : BinaryOperatorToken() {

        override fun getString(): String {
            return "AND "
        }

        override fun getPriority(): Int {
            return 1
        }

        override fun applyTo(rightToken: ValueToken, leftToken: ValueToken) : ValueToken {
            val value = FormulaInterpreter.eval(leftToken.value) && FormulaInterpreter.eval(rightToken.value)
            return ValueToken(FormulaInterpreter.eval(value))
        }
    }

    class OrOperatorToken : BinaryOperatorToken() {

        override fun getString(): String {
            return "OR "
        }

        override fun getPriority(): Int {
            return 0
        }

        override fun applyTo(rightToken: ValueToken, leftToken: ValueToken) : ValueToken {
            val value = FormulaInterpreter.eval(leftToken.value) || FormulaInterpreter.eval(rightToken.value)
            return ValueToken(FormulaInterpreter.eval(value))
        }
    }

    class EqualsOperatorToken : BinaryOperatorToken() {

        override fun getString(): String {
            return "= "
        }

        override fun getPriority(): Int {
            return 0
        }

        override fun applyTo(rightToken: ValueToken, leftToken: ValueToken) : ValueToken {
            return ValueToken(FormulaInterpreter.eval(leftToken.value == rightToken.value))
        }
    }

    class GreaterOperatorToken : BinaryOperatorToken() {

        override fun getString(): String {
            return "> "
        }

        override fun getPriority(): Int {
            return 0
        }

        override fun applyTo(rightToken: ValueToken, leftToken: ValueToken) : ValueToken {
            return ValueToken(FormulaInterpreter.eval(leftToken.value > rightToken.value))
        }
    }

    class GreaterEqualsOperatorToken : BinaryOperatorToken() {

        override fun getString(): String {
            return ">= "
        }

        override fun getPriority(): Int {
            return 0
        }

        override fun applyTo(rightToken: ValueToken, leftToken: ValueToken) : ValueToken {
            return ValueToken(FormulaInterpreter.eval(leftToken.value >= rightToken.value))
        }
    }

    class SmallerOperatorToken : BinaryOperatorToken() {

        override fun getString(): String {
            return "< "
        }

        override fun getPriority(): Int {
            return 0
        }

        override fun applyTo(rightToken: ValueToken, leftToken: ValueToken) : ValueToken {
            return ValueToken(FormulaInterpreter.eval(leftToken.value < rightToken.value))
        }
    }

    class SmallerEqualsOperatorToken : BinaryOperatorToken() {

        override fun getString(): String {
            return "<= "
        }

        override fun getPriority(): Int {
            return 0
        }

        override fun applyTo(rightToken: ValueToken, leftToken: ValueToken) : ValueToken {
            return ValueToken(FormulaInterpreter.eval(leftToken.value <= rightToken.value))
        }
    }

    class NotEqualsOperatorToken : BinaryOperatorToken() {

        override fun getString(): String {
            return "!= "
        }

        override fun getPriority(): Int {
            return 0
        }

        override fun applyTo(rightToken: ValueToken, leftToken: ValueToken) : ValueToken {
            return ValueToken(FormulaInterpreter.eval(leftToken.value != rightToken.value))
        }
    }
}

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


import org.catrobat.catroid.formula.value.ValueToken
import org.catrobat.catroid.formula.value.ValueToken.BooleanValueToken
import org.catrobat.catroid.formula.value.ValueToken.NumericValueToken

abstract class BinaryOperatorToken<in T : ValueToken, out V : ValueToken> : OperatorToken(Type.OPERATOR) {

    /**
     * The "inverted" parameter order (rightToken, leftToken) is used on purpose because the value stack
     * {@link #eval(List<Token> tokens) FormulaInterpreter} contains the values in this order.
     */
    abstract fun applyTo (rightToken: T, leftToken: T) : V

    class MultOperatorToken : BinaryOperatorToken<NumericValueToken, NumericValueToken>() {

        override fun getString(): String {
            return "* "
        }

        override fun getPriority(): Int {
            return 2
        }

        override fun applyTo(rightToken: NumericValueToken, leftToken: NumericValueToken): NumericValueToken {
            return NumericValueToken(leftToken.value * rightToken.value)
        }
    }

    class DivOperatorToken : BinaryOperatorToken<NumericValueToken, NumericValueToken>() {

        override fun getString(): String {
            return "/ "
        }

        override fun getPriority(): Int {
            return 2
        }

        override fun applyTo(rightToken: NumericValueToken, leftToken: NumericValueToken): NumericValueToken {
            if (rightToken.value == 0.0) throw Exception("DIVIDED BY 0")
            return NumericValueToken(leftToken.value / rightToken.value)
        }
    }

    class AddOperatorToken : BinaryOperatorToken<NumericValueToken, NumericValueToken>() {

        override fun getString(): String {
            return "+ "
        }

        override fun getPriority(): Int {
            return 1
        }

        override fun applyTo(rightToken: NumericValueToken, leftToken: NumericValueToken): NumericValueToken {
            return NumericValueToken(leftToken.value + rightToken.value)
        }
    }

    class SubOperatorToken : BinaryOperatorToken<NumericValueToken, NumericValueToken>() {

        override fun getString(): String {
            return "- "
        }

        override fun getPriority(): Int {
            return 1
        }

        override fun applyTo(rightToken: NumericValueToken, leftToken: NumericValueToken): NumericValueToken {
            return NumericValueToken(leftToken.value - rightToken.value)
        }
    }

    class AndOperatorToken : BinaryOperatorToken<BooleanValueToken, BooleanValueToken>() {

        override fun getString(): String {
            return "AND "
        }

        override fun getPriority(): Int {
            return 1
        }

        override fun applyTo(leftToken: BooleanValueToken, rightToken: BooleanValueToken) : BooleanValueToken {
            return BooleanValueToken(leftToken.value && rightToken.value)
        }
    }

    class OrOperatorToken : BinaryOperatorToken<BooleanValueToken, BooleanValueToken>() {

        override fun getString(): String {
            return "OR "
        }

        override fun getPriority(): Int {
            return 0
        }

        override fun applyTo(leftToken: BooleanValueToken, rightToken: BooleanValueToken) : BooleanValueToken {
            return BooleanValueToken(leftToken.value || rightToken.value)
        }
    }

    class EqualsOperatorToken : BinaryOperatorToken<NumericValueToken, BooleanValueToken>() {

        override fun getString(): String {
            return "= "
        }

        override fun getPriority(): Int {
            return 0
        }

        override fun applyTo(rightToken: NumericValueToken, leftToken: NumericValueToken) : BooleanValueToken {
            return BooleanValueToken(leftToken.value == rightToken.value)
        }
    }

    class GreaterOperatorToken : BinaryOperatorToken<NumericValueToken, BooleanValueToken>() {

        override fun getString(): String {
            return "> "
        }

        override fun getPriority(): Int {
            return 0
        }

        override fun applyTo(rightToken: NumericValueToken, leftToken: NumericValueToken) : BooleanValueToken {
            return BooleanValueToken(leftToken.value > rightToken.value)
        }
    }

    class GreaterEqualsOperatorToken : BinaryOperatorToken<NumericValueToken, BooleanValueToken>() {

        override fun getString(): String {
            return ">= "
        }

        override fun getPriority(): Int {
            return 0
        }

        override fun applyTo(rightToken: NumericValueToken, leftToken: NumericValueToken) : BooleanValueToken {
            return BooleanValueToken(leftToken.value >= rightToken.value)
        }
    }

    class SmallerOperatorToken : BinaryOperatorToken<NumericValueToken, BooleanValueToken>() {

        override fun getString(): String {
            return "< "
        }

        override fun getPriority(): Int {
            return 0
        }

        override fun applyTo(rightToken: NumericValueToken, leftToken: NumericValueToken) : BooleanValueToken {
            return BooleanValueToken(leftToken.value < rightToken.value)
        }
    }

    class SmallerEqualsOperatorToken : BinaryOperatorToken<NumericValueToken, BooleanValueToken>() {

        override fun getString(): String {
            return "<= "
        }

        override fun getPriority(): Int {
            return 0
        }

        override fun applyTo(rightToken: NumericValueToken, leftToken: NumericValueToken) : BooleanValueToken {
            return BooleanValueToken(leftToken.value <= rightToken.value)
        }
    }

    class NotEqualsOperatorToken : BinaryOperatorToken<NumericValueToken, BooleanValueToken>() {

        override fun getString(): String {
            return "!= "
        }

        override fun getPriority(): Int {
            return 0
        }

        override fun applyTo(rightToken: NumericValueToken, leftToken: NumericValueToken) : BooleanValueToken {
            return BooleanValueToken(leftToken.value != rightToken.value)
        }
    }
}

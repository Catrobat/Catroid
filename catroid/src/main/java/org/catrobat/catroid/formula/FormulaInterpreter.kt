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

package org.catrobat.catroid.formula

import org.catrobat.catroid.formula.function.FunctionToken
import org.catrobat.catroid.formula.operator.BinaryOperatorToken
import org.catrobat.catroid.formula.operator.OperatorToken
import org.catrobat.catroid.formula.operator.UnaryOperatorToken
import org.catrobat.catroid.formula.value.ValueToken
import java.util.*

class FormulaInterpreter {

    companion object {

        fun eval(value: Boolean): Double = if (value) 1.0 else 0.0
        fun eval(value: Double): Boolean = Math.abs(value) > 0
    }

    fun eval(tokens: List<Token>): ValueToken {

        val operators = Stack<OperatorToken>()
        val values = Stack<ValueToken>()

        for (token in tokens) {
            when (token.type) {
                Token.Type.VALUE -> values.push(token as ValueToken)

                Token.Type.LEFT_BRACKET -> operators.push(token as OperatorToken)

                Token.Type.RIGHT_BRACKET -> {
                    while (!operators.empty() && operators.peek().type != Token.Type.LEFT_BRACKET) {

                        if (operators.peek() is BinaryOperatorToken) {
                            values.push((operators.pop() as BinaryOperatorToken).applyTo(values.pop(), values.pop()))
                        } else if (operators.peek() is UnaryOperatorToken) {
                            values.push((operators.pop() as UnaryOperatorToken).applyTo(values.pop()))
                        }
                    }

                    operators.pop()
                }

                Token.Type.OPERATOR -> {

                    val operator = token as OperatorToken

                    while (!operators.empty() && operators.peek().getPriority() > operator.getPriority()) {

                        if (operators.peek() is BinaryOperatorToken) {
                            values.push((operators.pop() as BinaryOperatorToken).applyTo(values.pop(), values.pop()))
                        } else if (operators.peek() is UnaryOperatorToken) {
                            values.push((operators.pop() as UnaryOperatorToken).applyTo(values.pop()))
                        }
                    }

                    operators.push(operator)
                }

                Token.Type.FUNCTION -> values.push((token as FunctionToken).eval())
            }
        }

        while (!operators.empty()) {

            if (operators.peek() is BinaryOperatorToken) {
                values.push((operators.pop() as BinaryOperatorToken).applyTo(values.pop(), values.pop()))
            } else if (operators.peek() is UnaryOperatorToken) {
                values.push((operators.pop() as UnaryOperatorToken).applyTo(values.pop()))
            }
        }

        return values.pop()
    }
}

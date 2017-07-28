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

import java.util.*

class FormulaInterpreter {

	fun eval(tokens: List<Token>): Double {

		val operators = Stack<OperatorToken>()
		val values = Stack<NumericValueToken>()

		for (token in tokens) {
			when (token.type) {
				Token.Type.VALUE -> values.push(token as NumericValueToken)
				Token.Type.LEFT_BRACKET -> operators.push(token as OperatorToken)
				Token.Type.RIGHT_BRACKET -> {

                    while (!operators.empty() && operators.peek().type != (Token.Type.LEFT_BRACKET)) {
                        values.push((operators.pop() as MathOperatorToken).applyTo(values.pop(), values.pop()))
                    }

					operators.pop()
				}

				Token.Type.OPERATOR -> {

                    val operator = token as OperatorToken

                    while (!operators.empty() && operators.peek().getPriority() > operator.getPriority()) {
                        values.push((operators.pop() as MathOperatorToken).applyTo(values.pop(), values.pop()))
                    }

					operators.push(operator)
				}

                Token.Type.FUNCTION -> values.push((token as FunctionToken).eval())

				else -> return 0.0
			}
		}

		while (!operators.empty()) {
			values.push((operators.pop() as MathOperatorToken).applyTo(values.pop(), values.pop()))
		}

		return (values.pop() as NumericValueToken).value
	}
}

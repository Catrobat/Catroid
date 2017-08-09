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

abstract class LogicOperatorToken : OperatorToken(Type.OPERATOR) {

    abstract fun applyTo(leftToken: BooleanValueToken, rightToken: BooleanValueToken) : BooleanValueToken

    class AndOperatorToken : LogicOperatorToken() {

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

    class OrOperatorToken : LogicOperatorToken() {

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
}

class NotOperatorToken : OperatorToken(Type.OPERATOR) {

    override fun getString(): String {
        return "NOT "
    }

    override fun getPriority(): Int {
        return 2
    }

    fun applyTo(token: BooleanValueToken) : BooleanValueToken {
        return BooleanValueToken(!token.value)
    }
}
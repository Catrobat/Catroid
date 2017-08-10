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

abstract class UnaryOperatorToken<in T : ValueToken, out V : ValueToken> : OperatorToken(Type.OPERATOR) {

    abstract fun applyTo(token: T) : V

    class NotOperatorToken : UnaryOperatorToken<BooleanValueToken, BooleanValueToken>() {

        override fun getString(): String {
            return "NOT "
        }

        override fun getPriority(): Int {
            return 2
        }

        override fun applyTo(token: BooleanValueToken) : BooleanValueToken {
            return BooleanValueToken(!token.value)
        }
    }
}

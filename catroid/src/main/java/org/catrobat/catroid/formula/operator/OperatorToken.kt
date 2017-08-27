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

import org.catrobat.catroid.R
import org.catrobat.catroid.formula.Token

abstract class OperatorToken(type : Type, val PRIORITY: Int) : Token(type) {

    class BracketOperator(type: Type) : OperatorToken(type, 0) {

        override fun getResourceId() = when (type) {
            Type.LEFT_BRACKET -> R.string.formula_editor_bracket_open
            Type.RIGHT_BRACKET -> R.string.formula_editor_bracket_close
            else -> throw Exception("Bracket operator should either have LEFT_BRACKET or RIGHT_BRACKET type")
        }
    }
}

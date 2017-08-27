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

package org.catrobat.catroid.formula.value

import org.catrobat.catroid.formula.Token

open class ValueToken(var value: Double) : Token(Type.VALUE) {

    private var inputString: String = ""

    fun appendDigit(digit: Char) {
        if (inputString.isEmpty() && digit == '.') inputString += 0

        inputString += digit
        try { value = inputString.toDouble() } catch (_: NumberFormatException) {
            inputString = inputString.dropLast(1)
        }
    }

    /**
     * @return true if the token should be removed form the list because it is empty.
     */
    fun removeDigit(): Boolean {
        if (inputString.isEmpty()) inputString = value.toString()

        inputString = inputString.dropLast(1)
        return try { value = inputString.toDouble(); false } catch (_: NumberFormatException) { true }
    }

    override fun getResourceId(): Int {
        throw Exception("NOT Translatable: Numeric Values should not be translated!")
    }

    open fun getString() = if (inputString.isNotEmpty()) inputString else value.toString().removeSuffix(".0")

    class VariableToken(var name: String, value: Double) : ValueToken(value) {

        override fun getString(): String = name
    }
}

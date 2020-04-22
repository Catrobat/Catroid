/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2019 The Catrobat Team
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
package org.catrobat.catroid.formulaeditor.common

import android.graphics.Color
import androidx.annotation.ColorInt

object Conversions {
    const val TRUE = 1.0
    const val FALSE = 0.0

    private fun tryParseDouble(argument: String): Double? {
        return try {
            argument.toDouble()
        } catch (numberFormatException: NumberFormatException) {
            null
        }
    }

    @ColorInt
    @JvmStatic
    @JvmOverloads
    fun tryParseColor(string: String?, defaultValue: Int = Color.BLACK): Int {
        return if (string != null && string.length == 7 && string.matches("^#[0-9a-fA-F]+$".toRegex())) {
            Color.parseColor(string)
        } else {
            defaultValue
        }
    }

    @JvmStatic
    fun convertArgumentToDouble(argument: Any?): Double? {
        return argument?.let {
            when (argument) {
                is String -> tryParseDouble(argument)
                else -> argument as Double?
            }
        }
    }

    @JvmStatic
    fun booleanToDouble(value: Boolean): Double {
        return if (value) TRUE else FALSE
    }
}
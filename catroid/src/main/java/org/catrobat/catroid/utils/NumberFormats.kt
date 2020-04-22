/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2018 The Catrobat Team
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
package org.catrobat.catroid.utils

class NumberFormats private constructor() {
    companion object {
        @JvmStatic
        fun trimTrailingCharacters(value: String?): String {
            value ?: return ""
            if (value.contains(".") && value.matches("[0-9.-]+".toRegex())) {
                return when {
                    !value.contains(".") -> value
                    else -> value.replace("0*$".toRegex(), "").replace("\\.$".toRegex(), "")
                }
            }
            return ""
        }

        @JvmStatic
        fun toMetricUnitRepresentation(number: Int): String {
            return when {
                number < 1000 -> "" + number
                number < 10000 -> "" + number / 1000 + ((number % 1000) / 100).let { if (it > 0) ".$it" else "" } + "k"
                number < 1000000 -> "" + number / 1000 + "k"
                else -> "" + number / 1000000 + "M"
            }
        }
    }
}
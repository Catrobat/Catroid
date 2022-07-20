/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2022 The Catrobat Team
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

import kotlin.math.abs

class NumberFormats private constructor() {
    companion object {
        @JvmStatic
        fun trimTrailingCharacters(value: String?): String {
            value ?: return ""
            if (value.contains(".") && value.matches("(-?[1-9]\\d*|0)\\.(0|\\d*[1-9]0)".toRegex())) {
                return value.replace("0$".toRegex(), "").replace("\\.$".toRegex(), "")
            }
            return value
        }

        @Suppress("MagicNumber")
        @JvmStatic
        fun toMetricUnitRepresentation(number: Int): String {
            var prefix = ""
            var absoluteNumber = number
            if (number < 0) {
                prefix = "-"
                absoluteNumber = abs(number)
            }
            return when {
                absoluteNumber >= 1_000_000 ->
                    "$prefix${absoluteNumber / 1_000_000}M"
                absoluteNumber >= 10_000 && absoluteNumber % 1_000 > 100 ->
                    "$prefix${absoluteNumber / 1_000}.${absoluteNumber % 1_000 / 100}k"
                absoluteNumber >= 1_000 ->
                    "$prefix${absoluteNumber / 1_000}k"
                else ->
                    number.toString()
            }
        }
    }
}

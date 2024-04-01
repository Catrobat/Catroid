/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2023 The Catrobat Team
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

package org.catrobat.catroid.io.catlang.parser.project

class CatrobatLanguageParserHelper private constructor() {
    companion object {
        fun getStringContent(value: String): String = value.substring(1, value.length - 1).replace("\\n", "\n").replace("\\r", "\r")
        fun getStringToDouble(value: String): Double = getStringContent(value).toDouble()
        fun getStringToBoolean(value: String): Boolean = getStringContent(value).toBoolean()
        fun getStringToInt(value: String): Int = getStringContent(value).toInt()

        fun getVariableName(variableReference: String): String = variableReference.substring(1, variableReference.length - 1)
            .replace("\\\"", "\"")

        fun getListName(variableReference: String): String = variableReference.substring(1, variableReference.length - 1)
            .replace("\\*", "*")
    }
}

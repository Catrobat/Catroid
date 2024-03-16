/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2024 The Catrobat Team
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

import org.catrobat.catroid.io.catlang.parser.project.error.CatrobatLanguageParsingException

class CatrobatLanguageParserUtils {
    companion object {
        val variableRegex = Regex("^\"(.*)\"$");
        val listRegex = Regex("^\\*(.*)\\*$");
        val stringRegex = Regex("^'(.*)'$");

        fun getAndValidateStringContent(name: String): String {
            val variableMatch = stringRegex.find(name) ?: throw CatrobatLanguageParsingException("Invalid string: $name. Expected format: 'string content'")
            return variableMatch.groupValues[1]
        }

        fun getAndValidateVariableName(name: String): String {
            val variableMatch = variableRegex.find(name) ?: throw CatrobatLanguageParsingException("Invalid variable name: $name. Expected format: \"variable name\"")
            return variableMatch.groupValues[1]
        }

        fun getAndValidateListName(name: String): String {
            val listMatch = listRegex.find(name) ?: throw CatrobatLanguageParsingException("Invalid list name: $name. Expected format: *list name*")
            return listMatch.groupValues[1]
        }

        fun hexToRgb(hex: String): IntArray? {
            if (!hex.matches(Regex("^#[0-9a-fA-F]{6}$"))) {
                throw CatrobatLanguageParsingException("Invalid hex code: $hex. Expected format: #RRGGBB")
            }
            val red = Integer.parseInt(hex.substring(1, 3), 16)
            val green = Integer.parseInt(hex.substring(3, 5), 16)
            val blue = Integer.parseInt(hex.substring(5, 7), 16)
            return intArrayOf(red, green, blue)
        }
    }
}
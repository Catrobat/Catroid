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

        fun getAndValidateVariableName(name: String): String {
            val variableMatch = variableRegex.find(name) ?: throw CatrobatLanguageParsingException("Invalid variable name: $name. Expected format: \"variable name\"")
            return variableMatch.groupValues[1]
        }

        fun getAndValidateListName(name: String): String {
            val listMatch = listRegex.find(name) ?: throw CatrobatLanguageParsingException("Invalid list name: $name. Expected format: *list name*")
            return listMatch.groupValues[1]
        }
    }
}
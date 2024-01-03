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

package org.catrobat.catroid.io.catlang.serializer

import android.content.Context
import android.content.res.Configuration
import java.util.Locale

object CatrobatLanguageUtils {
    @JvmStatic
    fun getIndention(level: Int): String = " ".repeat(level * 2)

    @JvmStatic
    fun getIndention(level: IndentionLevel): String = getIndention(level.ordinal + 1)

    @JvmStatic
    fun formatSoundName(soundName: String): String = "'${escapeCharacters(soundName, "'\r\n")}'"

    @JvmStatic
    fun formatActorOrObject(actorOrObjectName: String): String = "'${escapeCharacters(actorOrObjectName, "'\r\n")}'"

    @JvmStatic
    fun formatLook(lookName: String): String = "'${escapeCharacters(lookName, "'\r\n")}'"

    @JvmStatic
    fun formatNFCTag(nfcTag: String): String = "'${escapeCharacters(nfcTag, "'\r\n")}'"

    @JvmStatic
    fun formatVariable(variableName: String): String = "\"${escapeCharacters(variableName, "\"\r\n")}\""

    @JvmStatic
    fun formatList(listName: String): String = "*${escapeCharacters(listName, "*\r\n")}*"

    @JvmStatic
    fun formatString(string: String): String = "'${escapeCharacters(string, "'\r\n")}'"

    @JvmStatic
    fun formatUserDefinedBrickLabel(label: String) = escapeCharacters(label, "[]`")

    @JvmStatic
    fun formatUserDefinedBrickParameter(parameter: String) = "[${escapeCharacters(parameter, "[]`")}]"

    @JvmStatic
    fun getEscapedString(string: String): String = "'${escapeCharacters(string, "'\t\r\n")}'"

    @JvmStatic
    fun formatHexColorString(hexColorString: String): String {
        val trimmedString = hexColorString.replace(Regex("^'"), "").replace(Regex("'$"), "").toLowerCase(Locale.ROOT)
        if (trimmedString == "0") {
            return "#000000"
        }

        if (Regex("^([a-f0-9]{6}|[a-f0-9]{3})$").matches(trimmedString)) {
            return "#$trimmedString"
        }

        return hexColorString
    }

    @JvmStatic
    fun getEnglishContextForFormulas(context: Context): Context {
        var configuration = context.resources.configuration
        configuration = Configuration(configuration)
        configuration.setLocale(Locale("en"))
        return context.createConfigurationContext(configuration)
    }

    @JvmStatic
    fun escapeFormula(formula: String): String {
        return escapeCharacters(formula, "\r\n")
    }

    fun escapeCharacters(string: String, charactersToEscape: String): String {
        var escapedString = string
        for (character in charactersToEscape) {
            val replacement = when (character) {
                '\n' -> "\\n"
                '\r' -> "\\r"
                else -> "\\$character"
            }
            escapedString = escapedString.replace(character.toString(), replacement)
        }
        return escapedString
    }
}

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

package org.catrobat.catroid.io.catlang

import android.content.Context
import android.content.res.Configuration
import org.catrobat.catroid.io.catlang.parser.project.error.CatrobatLanguageParsingException
import org.catrobat.catroid.io.catlang.serializer.IndentionLevel
import java.util.AbstractMap
import java.util.Locale

object CatrobatLanguageUtils {
    private val variableRegex = Regex("^\"(.*)\"$")
    private val listRegex = Regex("^\\*(.*)\\*$")
    private val stringRegex = Regex("^'(.*)'$")
    private val userDefinedBrickParameterRegex = Regex("^\\[(.*)\\]$")

    private const val escapedStringChars = "'\n\r"
    private const val escapedVariableChars = "\"\n\r"
    private const val escapedListChars = "*\n\r"
    private const val escapedUserDefinedBrickChars = "[]`\n\r"

    @JvmStatic
    fun getAndValidateStringContent(value: String): String {
        val variableMatch = stringRegex.find(value) ?: throw CatrobatLanguageParsingException("Invalid string: $value. Expected format: 'string content'")
        return replaceEscapedCharacters(variableMatch.groupValues[1], escapedStringChars)
    }

    @JvmStatic
    fun getAndValidateDoubleFromString(value: String): Double {
        return getAndValidateStringContent(value).toDouble()
    }

    @JvmStatic
    fun getAndValidateBooleanFromString(value: String): Boolean {
        return getAndValidateStringContent(value).toBoolean()
    }

    @JvmStatic
    fun getAndValidateIntFromString(value: String): Int {
        return getAndValidateStringContent(value).toInt()
    }

    @JvmStatic
    fun getAndValidateVariableName(value: String): String {
        val variableMatch = variableRegex.find(value) ?: throw CatrobatLanguageParsingException("Invalid variable name: $value. Expected format: \"variable name\"")
        return replaceEscapedCharacters(variableMatch.groupValues[1], escapedVariableChars)
    }

    @JvmStatic
    fun getAndValidateListName(value: String): String {
        val listMatch = listRegex.find(value) ?: throw CatrobatLanguageParsingException("Invalid list name: $value. Expected format: *list name*")
        return replaceEscapedCharacters(listMatch.groupValues[1], escapedListChars)
    }

    @JvmStatic
    fun getAndValidateUserDefinedBrickParameter(value: String): String {
        val listMatch = userDefinedBrickParameterRegex.find(value)
            ?: throw CatrobatLanguageParsingException("Invalid user defined brick parameter name: $value. Expected format: [user defined brick parameter]")
        return replaceEscapedCharacters(listMatch.groupValues[1], escapedUserDefinedBrickChars)
    }

    private fun replaceEscapedCharacters(value: String, escapedChars: String): String {
        var replacedValue = value
        for (escapedChar in escapedChars) {
            val escapedCharForRegex = when (escapedChar) {
                '\n' -> 'n'
                '\r' -> 'r'
                '\t' -> 't'
                else -> escapedChar
            }
            val regexSafeEscapedChar = Regex.escape(escapedCharForRegex.toString())
            val scapeReplaceRegex = Regex("(?<!\\\\)\\\\($regexSafeEscapedChar)")
            replacedValue = scapeReplaceRegex.replace(replacedValue) { escapedChar.toString() }
        }
        return replacedValue.replace("\\\\", "\\")
    }

    @JvmStatic
    fun formatString(string: String?): String = formatAndEscapeString(string, escapedStringChars, "'")

    @JvmStatic
    fun formatVariable(variableName: String?): String = formatAndEscapeString(variableName, escapedVariableChars, "\"")

    @JvmStatic
    fun formatList(listName: String?): String = formatAndEscapeString(listName, escapedListChars, "*")

    @JvmStatic
    fun formatSoundName(soundName: String?): String = this.formatString(soundName)

    @JvmStatic
    fun formatActorOrObject(actorOrObjectName: String?): String = this.formatString(actorOrObjectName)

    @JvmStatic
    fun formatLook(lookName: String?): String = this.formatString(lookName)

    @JvmStatic
    fun formatNFCTag(nfcTag: String?): String = this.formatString(nfcTag)

    @JvmStatic
    fun formatUserDefinedBrickLabel(label: String?) = formatAndEscapeString(label, escapedUserDefinedBrickChars, "")

    @JvmStatic
    fun formatUserDefinedBrickParameter(parameter: String?) = formatAndEscapeString(parameter, escapedUserDefinedBrickChars, "[", "]")

    private fun formatAndEscapeString(
        string: String?,
        charactersToEscape: String,
        enclosingStartString: String,
        enclosingEndString: String? = null): String {
        var escapedString = ""
        if (string != null) {
            escapedString = string.replace("\\", "\\\\")
            for (character in charactersToEscape) {
                escapedString = if (character == '\r') {
                    escapedString.replace("\r", "\\r")
                } else if (character == '\n') {
                    escapedString.replace("\n", "\\n")
                } else if (character == '\t') {
                    escapedString.replace("\t", "\\t")
                } else {
                    val regexSafeEscapedChar = Regex.escape(character.toString())
                    val escapeReplaceRegex = Regex("(?<!\\\\)$regexSafeEscapedChar")
                    escapeReplaceRegex.replace(escapedString) { "\\$character" }
                }
            }
        }
        if (enclosingEndString == null) {
            return "$enclosingStartString$escapedString$enclosingStartString"
        }
        return "$enclosingStartString$escapedString$enclosingEndString"
    }

    @JvmStatic
    fun getEnglishContextForFormulas(context: Context): Context {
        var configuration = context.resources.configuration
        configuration = Configuration(configuration)
        configuration.setLocale(Locale.ROOT)
        return context.createConfigurationContext(configuration)
    }

    @JvmStatic
    fun getCatlangArgumentTuple(name: String, nullableValue: String?): AbstractMap.SimpleEntry<String, String> {
        var value = nullableValue
        if (value == null) {
            value = ""
        }
        return AbstractMap.SimpleEntry(name, value)
    }

    @JvmStatic
    fun getIndention(level: Int): String = " ".repeat(level * 2)

    @JvmStatic
    fun getIndention(level: IndentionLevel): String = getIndention(level.ordinal + 1)

    @JvmStatic
    fun joinString(delimiter: String, strings: List<String>): String {
        val stringBuilder = StringBuilder()
        for (i in strings.indices) {
            stringBuilder.append(strings[i])
            if (i < strings.size - 1) {
                stringBuilder.append(delimiter)
            }
        }
        return stringBuilder.toString()
    }
}

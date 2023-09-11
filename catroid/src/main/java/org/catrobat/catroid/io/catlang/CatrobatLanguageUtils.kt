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

package org.catrobat.catroid.io.catlang

import android.content.Context
import android.os.Build
import org.catrobat.catroid.formulaeditor.Formula
import java.util.Locale

object CatrobatLanguageUtils {
    @JvmStatic
    fun getIndention(level: Int): String = " ".repeat(level * 2)

    @JvmStatic
    fun formatSoundName(soundName: String): String = "'$soundName'"

    @JvmStatic
    fun formatActorOrObject(actorOrObjectName: String): String = "'$actorOrObjectName'"

    @JvmStatic
    fun formatLook(lookName: String): String = "'$lookName'"

    @JvmStatic
    fun formatNFCTag(nfcTag: String): String = "'$nfcTag'"

    @JvmStatic
    fun formatVariable(variableName: String): String = "\"$variableName\""

    @JvmStatic
    fun formatList(listName: String): String = "*$listName*"

    @JvmStatic
    fun formatString(string: String): String = "'$string'"

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
    fun getFormulaString(formula: Formula, context: Context): String {
        val currentLocale = updateLanguage(context, Locale.ENGLISH)
        val formulaString = formula.getTrimmedFormulaString(context)
        currentLocale?.let { updateLanguage(context, it) }
        return formulaString.trim()
    }

    @JvmStatic
    private fun updateLanguage(context: Context, locale: Locale): Locale? {
        val resources = context.resources
        val config = resources.configuration
        val currentLocale: Locale = config.locale

        config.setLocale(locale)
        val dm = resources.displayMetrics
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
            context.applicationContext.createConfigurationContext(config)
        } else {
            resources.updateConfiguration(config, dm)
        }

        return currentLocale
    }
}

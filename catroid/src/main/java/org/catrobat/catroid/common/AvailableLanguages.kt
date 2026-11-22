/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2026 The Catrobat Team
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

package org.catrobat.catroid.common

import android.content.Context
import androidx.annotation.VisibleForTesting
import java.util.Locale

/**
 * Determines which of the app's languages are actually installed on the device.
 *
 * When the app is installed from Google Play as an app bundle, Play only installs the language
 * resources (config splits such as `config.de`) for the device's system languages. Selecting any
 * other language would have no visible effect, so such languages are reported as unavailable.
 *
 * If the app was installed without language splits (e.g. a regular APK), all languages are
 * contained in the base APK and therefore all of them are available.
 */
class AvailableLanguages(installedSplitNames: Array<String>?) {

    /** Installed config split languages, or null if the app is not installed with config splits. */
    private val installedSplitLanguages: Set<String>? =
        installedSplitNames
            ?.filter { it.startsWith(CONFIG_SPLIT_PREFIX) }
            ?.takeIf { it.isNotEmpty() }
            ?.map { it.removePrefix(CONFIG_SPLIT_PREFIX).lowercase(Locale.ROOT) }
            ?.toSet()

    fun isAvailable(languageTag: String): Boolean {
        if (installedSplitLanguages == null ||
            languageTag == SharedPreferenceKeys.DEVICE_LANGUAGE ||
            languageTag == DEFAULT_RESOURCES_LANGUAGE_TAG
        ) {
            return true
        }
        return normalizeLanguage(Locale.forLanguageTag(languageTag).language) in
            installedSplitLanguages
    }

    fun filter(languageTags: Array<String>): Array<String> =
        languageTags.filter { isAvailable(it) }.toTypedArray()

    companion object {
        private const val CONFIG_SPLIT_PREFIX = "config."

        /** The default resources (values/) are English, so plain English is always available. */
        private const val DEFAULT_RESOURCES_LANGUAGE_TAG = "en"

        @VisibleForTesting
        @JvmStatic
        var overrideForTesting: AvailableLanguages? = null

        @JvmStatic
        fun fromContext(context: Context): AvailableLanguages =
            overrideForTesting ?: AvailableLanguages(context.applicationInfo.splitNames)

        /** Language splits use the current ISO 639 codes, Java may still return legacy ones. */
        private fun normalizeLanguage(language: String): String =
            when (val lowercase = language.lowercase(Locale.ROOT)) {
                "iw" -> "he"
                "in" -> "id"
                "ji" -> "yi"
                else -> lowercase
            }
    }
}

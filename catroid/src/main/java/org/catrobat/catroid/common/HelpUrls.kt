/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2025 The Catrobat Team
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
import android.preference.PreferenceManager
import org.catrobat.catroid.CatroidApplication
import java.util.Locale

/**
 * Builds the in-app help links into the documentation on share.catrobat.org.
 */
object HelpUrls {

    private const val UNDETERMINED_LANGUAGE_TAG = "und"

    @JvmStatic
    fun brickHelpUrl(brickClassSimpleName: String): String =
        Constants.SHARE_BRICK_HELP_BASE_URL + brickClassSimpleName

    /**
     * Adds the locale query parameter in front of an optional fragment, e.g.
     * `.../reference#formula` becomes `.../reference?locale=pt-BR#formula`.
     */
    @JvmStatic
    fun withLocale(url: String, locale: Locale?): String {
        val languageTag = toShareLocale(locale) ?: return url
        val fragmentStart = url.indexOf('#')
        val base = if (fragmentStart >= 0) url.substring(0, fragmentStart) else url
        val fragment = if (fragmentStart >= 0) url.substring(fragmentStart) else ""
        val separator = if (base.contains('?')) '&' else '?'
        return "$base$separator${Constants.SHARE_LOCALE_PARAMETER}=$languageTag$fragment"
    }

    /**
     * Share accepts BCP 47 tags (`de`, `pt-BR`, `zh-TW`, `sr-Latn`) and falls back to English for
     * unknown ones. The region has to be kept, otherwise e.g. `zh-TW` would degrade to English.
     */
    @JvmStatic
    fun toShareLocale(locale: Locale?): String? {
        val languageTag = locale?.toLanguageTag()
        return if (languageTag.isNullOrEmpty() || languageTag == UNDETERMINED_LANGUAGE_TAG) {
            null
        } else {
            languageTag
        }
    }

    @JvmStatic
    fun getAppLocale(context: Context): Locale? {
        val languageTag = PreferenceManager.getDefaultSharedPreferences(context.applicationContext)
            .getString(SharedPreferenceKeys.LANGUAGE_TAG_KEY, "")
        return resolveAppLocale(languageTag, CatroidApplication.defaultSystemLanguage)
    }

    @JvmStatic
    fun resolveAppLocale(languageTag: String?, systemLanguageTag: String?): Locale? {
        val isSelectableLanguage = languageTag != SharedPreferenceKeys.DEVICE_LANGUAGE &&
            SharedPreferenceKeys.LANGUAGE_TAGS.contains(languageTag)
        val tag = if (isSelectableLanguage) languageTag else systemLanguageTag
        return tag?.let { Locale.forLanguageTag(it) }
    }
}

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

package org.catrobat.catroid.test.common

import org.catrobat.catroid.common.AvailableLanguages
import org.catrobat.catroid.common.SharedPreferenceKeys.DEVICE_LANGUAGE
import org.catrobat.catroid.common.SharedPreferenceKeys.LANGUAGE_TAGS
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AvailableLanguagesTest {

    private val playInstallWithGermanAndIndonesian = arrayOf(
        "config.arm64_v8a", "config.de", "config.id", "config.xxhdpi"
    )

    @Test
    fun `all languages are available when the app has no splits`() {
        val availableLanguages = AvailableLanguages(null)

        assertArrayEquals(LANGUAGE_TAGS, availableLanguages.filter(LANGUAGE_TAGS))
    }

    @Test
    fun `all languages are available when the app has only non config splits`() {
        val availableLanguages = AvailableLanguages(arrayOf("feature_module"))

        assertArrayEquals(LANGUAGE_TAGS, availableLanguages.filter(LANGUAGE_TAGS))
    }

    @Test
    fun `only installed split languages, english and device language are available`() {
        val availableLanguages = AvailableLanguages(playInstallWithGermanAndIndonesian)

        assertArrayEquals(
            arrayOf(DEVICE_LANGUAGE, "de", "en", "id"),
            availableLanguages.filter(LANGUAGE_TAGS)
        )
    }

    @Test
    fun `filter keeps the order of the given language tags`() {
        val availableLanguages = AvailableLanguages(playInstallWithGermanAndIndonesian)

        assertArrayEquals(
            arrayOf("id", "en", "de"),
            availableLanguages.filter(arrayOf("fr", "id", "en", "de"))
        )
    }

    @Test
    fun `regional variants are available with the split of their language`() {
        val availableLanguages = AvailableLanguages(arrayOf("config.en", "config.pt", "config.zh"))

        assertTrue(availableLanguages.isAvailable("en-GB"))
        assertTrue(availableLanguages.isAvailable("pt-BR"))
        assertTrue(availableLanguages.isAvailable("zh-TW"))
        assertFalse(availableLanguages.isAvailable("sr-SP"))
    }

    @Test
    fun `english variants need the english split`() {
        val availableLanguages = AvailableLanguages(playInstallWithGermanAndIndonesian)

        assertTrue(availableLanguages.isAvailable("en"))
        assertFalse(availableLanguages.isAvailable("en-GB"))
    }

    @Test
    fun `legacy java language codes are matched to the split names`() {
        val availableLanguages = AvailableLanguages(arrayOf("config.he", "config.id"))

        assertTrue(availableLanguages.isAvailable("he"))
        assertTrue(availableLanguages.isAvailable("iw"))
        assertTrue(availableLanguages.isAvailable("id"))
        assertTrue(availableLanguages.isAvailable("in"))
    }

    @Test
    fun `every language tag has a matching split name`() {
        val splitLanguages = arrayOf(
            "af", "ar", "az", "bg", "bn", "bs", "ca", "cs", "da", "de", "el", "en", "es", "fa",
            "fi", "fr", "gl", "gu", "ha", "he", "hi", "hr", "hu", "id", "ig", "it", "ja", "kk",
            "kn", "ko", "lt", "mk", "ml", "ms", "nl", "no", "pl", "ps", "pt", "ro", "ru", "sd",
            "si", "sk", "sl", "sq", "sr", "sv", "sw", "ta", "te", "th", "tr", "tw", "uk", "ur",
            "vi", "zh"
        )
        val availableLanguages = AvailableLanguages(splitLanguages.map { "config.$it" }.toTypedArray())

        assertArrayEquals(LANGUAGE_TAGS, availableLanguages.filter(LANGUAGE_TAGS))
    }
}

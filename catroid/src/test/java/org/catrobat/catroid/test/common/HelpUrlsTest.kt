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

package org.catrobat.catroid.test.common

import org.catrobat.catroid.common.Constants
import org.catrobat.catroid.common.HelpUrls
import org.catrobat.catroid.common.SharedPreferenceKeys
import org.catrobat.catroid.content.bricks.NoteBrick
import org.catrobat.catroid.content.bricks.SetXBrick
import org.catrobat.catroid.content.bricks.WaitBrick
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.util.Locale

@RunWith(JUnit4::class)
class HelpUrlsTest {

    @Test
    fun testBrickHelpUrlUsesClassSimpleName() {
        assertEquals(
            "https://share.catrobat.org/app/docs/reference/bricks/by-class/SetXBrick",
            SetXBrick().getHelpUrl("motion")
        )
        assertEquals(
            "https://share.catrobat.org/app/docs/reference/bricks/by-class/WaitBrick",
            WaitBrick().getHelpUrl("control")
        )
        assertEquals(
            "https://share.catrobat.org/app/docs/reference/bricks/by-class/NoteBrick",
            NoteBrick().getHelpUrl(null)
        )
    }

    @Test
    fun testBrickHelpUrlWithLocale() {
        assertEquals(
            "https://share.catrobat.org/app/docs/reference/bricks/by-class/SetXBrick?locale=de",
            HelpUrls.withLocale(HelpUrls.brickHelpUrl("SetXBrick"), Locale.GERMAN)
        )
        assertEquals(
            "https://share.catrobat.org/app/docs/reference/bricks/by-class/SetXBrick?locale=pt-BR",
            HelpUrls.withLocale(HelpUrls.brickHelpUrl("SetXBrick"), Locale.forLanguageTag("pt-BR"))
        )
    }

    @Test
    fun testFormulaHelpUrls() {
        val formulaReferenceUrl = "https://share.catrobat.org/app/docs/reference#formula"
        assertEquals(formulaReferenceUrl, Constants.CATROBAT_FUNCTIONS_WIKI_URL)
        assertEquals(formulaReferenceUrl, Constants.CATROBAT_LOGIC_WIKI_URL)
        assertEquals(formulaReferenceUrl, Constants.CATROBAT_SENSORS_WIKI_URL)
        assertEquals(formulaReferenceUrl, Constants.CATROBAT_OBJECT_WIKI_URL)
        assertEquals(formulaReferenceUrl, Constants.CATROBAT_FORMULA_WIKI_URL)
    }

    @Test
    fun testWebRequestHelpUrl() {
        assertEquals(
            "https://share.catrobat.org/app/docs/reference/bricks/by-class/WebRequestBrick",
            Constants.WEB_REQUEST_WIKI_URL
        )
    }

    @Test
    fun testLocaleIsInsertedBeforeFragment() {
        assertEquals(
            "https://share.catrobat.org/app/docs/reference?locale=zh-TW#formula",
            HelpUrls.withLocale(Constants.CATROBAT_FUNCTIONS_WIKI_URL, Locale.forLanguageTag("zh-TW"))
        )
    }

    @Test
    fun testLocaleIsAppendedToExistingQuery() {
        assertEquals(
            "https://share.catrobat.org/app/docs?page=1&locale=de",
            HelpUrls.withLocale("https://share.catrobat.org/app/docs?page=1", Locale.GERMAN)
        )
    }

    @Test
    fun testUrlIsUnchangedWithoutLocale() {
        val url = Constants.CATROBAT_FUNCTIONS_WIKI_URL
        assertEquals(url, HelpUrls.withLocale(url, null))
        assertEquals(url, HelpUrls.withLocale(url, Locale.forLanguageTag("")))
    }

    @Test
    fun testToShareLocale() {
        assertEquals("de", HelpUrls.toShareLocale(Locale.forLanguageTag("de")))
        assertEquals("pt-BR", HelpUrls.toShareLocale(Locale.forLanguageTag("pt-BR")))
        assertEquals("sr-Latn", HelpUrls.toShareLocale(Locale.forLanguageTag("sr-Latn")))
        assertNull(HelpUrls.toShareLocale(Locale.ROOT))
        assertNull(HelpUrls.toShareLocale(null))
    }

    @Test
    fun testResolveAppLocale() {
        assertEquals(
            Locale.forLanguageTag("fr"),
            HelpUrls.resolveAppLocale("fr", "de-AT")
        )
        assertEquals(
            Locale.forLanguageTag("de-AT"),
            HelpUrls.resolveAppLocale(SharedPreferenceKeys.DEVICE_LANGUAGE, "de-AT")
        )
        assertEquals(
            Locale.forLanguageTag("de-AT"),
            HelpUrls.resolveAppLocale("", "de-AT")
        )
        assertEquals(
            Locale.forLanguageTag("de-AT"),
            HelpUrls.resolveAppLocale("not-a-supported-tag", "de-AT")
        )
        assertNull(HelpUrls.resolveAppLocale(null, null))
    }
}

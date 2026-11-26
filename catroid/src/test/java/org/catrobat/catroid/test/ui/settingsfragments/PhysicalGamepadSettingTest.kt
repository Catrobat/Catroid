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

package org.catrobat.catroid.test.ui.settingsfragments

import android.content.Context
import android.content.res.XmlResourceParser
import android.os.Build
import android.preference.PreferenceManager
import org.catrobat.catroid.R
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import org.xmlpull.v1.XmlPullParser

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class PhysicalGamepadSettingTest {
    private val context: Context = RuntimeEnvironment.getApplication()
    private val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    @After
    fun tearDown() {
        sharedPreferences.edit().clear().commit()
    }

    @Test
    fun `setting has a stable key`() {
        assertEquals(
            "setting_physical_gamepad_bricks",
            SettingsFragment.SETTINGS_SHOW_PHYSICAL_GAMEPAD_BRICKS
        )
    }

    @Test
    fun `setting is off by default`() {
        assertFalse(SettingsFragment.isPhysicalGamepadSharedPreferenceEnabled(context))
    }

    @Test
    fun `setting is on when enabled`() {
        setPhysicalGamepadSetting(true)

        assertTrue(SettingsFragment.isPhysicalGamepadSharedPreferenceEnabled(context))
    }

    @Test
    fun `setting is off when disabled again`() {
        setPhysicalGamepadSetting(true)
        setPhysicalGamepadSetting(false)

        assertFalse(SettingsFragment.isPhysicalGamepadSharedPreferenceEnabled(context))
    }

    @Test
    fun `setting is independent of the Chromecast setting`() {
        sharedPreferences.edit()
            .putBoolean(SettingsFragment.SETTINGS_CAST_GLOBALLY_ENABLED, true)
            .commit()

        assertFalse(SettingsFragment.isPhysicalGamepadSharedPreferenceEnabled(context))
        assertTrue(SettingsFragment.isCastSharedPreferenceEnabled(context))
    }

    @Test
    fun `setting has its title and summary`() {
        assertEquals(
            "Gamepad extension",
            context.getString(R.string.preference_title_enable_physical_gamepad_bricks)
        )
        assertEquals(
            "Allow the use of a physical gamepad",
            context.getString(R.string.preference_description_physical_gamepad_bricks)
        )
    }

    @Test
    fun `preferences offer the setting as a checkbox that is off by default`() {
        val preference = topLevelPreferences()
            .singleOrNull { it.key == SettingsFragment.SETTINGS_SHOW_PHYSICAL_GAMEPAD_BRICKS }

        assertNotNull(preference)
        assertEquals("CheckBoxPreference", preference?.tag)
        assertEquals("false", preference?.defaultValue)
        assertEquals(R.string.preference_title_enable_physical_gamepad_bricks, preference?.titleResId)
        assertEquals(
            R.string.preference_description_physical_gamepad_bricks,
            preference?.summaryResId
        )
    }

    @Test
    fun `preferences place the setting directly before the Chromecast setting`() {
        val keys = topLevelPreferences().map { it.key }

        val gamepadIndex = keys.indexOf(SettingsFragment.SETTINGS_SHOW_PHYSICAL_GAMEPAD_BRICKS)

        assertTrue(gamepadIndex >= 0)
        assertEquals(gamepadIndex + 1, keys.indexOf(SettingsFragment.SETTINGS_CAST_GLOBALLY_ENABLED))
    }

    private fun setPhysicalGamepadSetting(enabled: Boolean) {
        sharedPreferences.edit()
            .putBoolean(SettingsFragment.SETTINGS_SHOW_PHYSICAL_GAMEPAD_BRICKS, enabled)
            .commit()
    }

    /** Reads the direct children of the root preference screen in R.xml.preferences. */
    private fun topLevelPreferences(): List<PreferenceEntry> {
        val parser = context.resources.getXml(R.xml.preferences)
        val preferences = mutableListOf<PreferenceEntry>()
        var eventType = parser.eventType
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG && parser.depth == TOP_LEVEL_DEPTH) {
                preferences.add(PreferenceEntry.from(parser))
            }
            eventType = parser.next()
        }
        parser.close()
        return preferences
    }

    private data class PreferenceEntry(
        val tag: String,
        val key: String?,
        val defaultValue: String?,
        val titleResId: Int,
        val summaryResId: Int
    ) {
        companion object {
            fun from(parser: XmlResourceParser) = PreferenceEntry(
                tag = parser.name,
                key = parser.getAttributeValue(ANDROID_NAMESPACE, "key"),
                defaultValue = parser.getAttributeValue(ANDROID_NAMESPACE, "defaultValue"),
                titleResId = parser.getAttributeResourceValue(ANDROID_NAMESPACE, "title", 0),
                summaryResId = parser.getAttributeResourceValue(ANDROID_NAMESPACE, "summary", 0)
            )
        }
    }

    private companion object {
        const val ANDROID_NAMESPACE = "http://schemas.android.com/apk/res/android"
        const val TOP_LEVEL_DEPTH = 2
    }
}

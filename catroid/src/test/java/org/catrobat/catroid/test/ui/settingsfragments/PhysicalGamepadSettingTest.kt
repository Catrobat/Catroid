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
import org.catrobat.catroid.ui.settingsfragments.GamepadSettingsFragment
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
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
    fun `preferences offer the gamepad extension as a submenu instead of its checkboxes`() {
        val preferences = topLevelPreferences()
        val submenu = preferences.singleOrNull { it.key == GAMEPAD_SCREEN_KEY }

        assertNotNull(submenu)
        assertEquals("PreferenceScreen", submenu?.tag)
        assertEquals(R.string.preference_title_enable_physical_gamepad_bricks, submenu?.titleResId)
        assertEquals(R.string.preference_description_physical_gamepad_bricks, submenu?.summaryResId)
        assertTrue(
            preferences.none { preference ->
                preference.key == SettingsFragment.SETTINGS_SHOW_PHYSICAL_GAMEPAD_BRICKS ||
                    preference.key == SettingsFragment.SETTINGS_GAMEPAD_SWAP_FACE_BUTTONS
            }
        )
    }

    @Test
    fun `preferences place the gamepad submenu directly before the Chromecast setting`() {
        val keys = topLevelPreferences().map { it.key }

        val gamepadIndex = keys.indexOf(GAMEPAD_SCREEN_KEY)

        assertTrue(gamepadIndex >= 0)
        assertEquals(gamepadIndex + 1, keys.indexOf(SettingsFragment.SETTINGS_CAST_GLOBALLY_ENABLED))
    }

    @Test
    fun `gamepad submenu offers the extension, the swap, its detection and the KeyLinker hint in this order`() {
        assertEquals(
            listOf(
                SettingsFragment.SETTINGS_SHOW_PHYSICAL_GAMEPAD_BRICKS,
                SettingsFragment.SETTINGS_GAMEPAD_SWAP_FACE_BUTTONS,
                GamepadSettingsFragment.DETECT_FACE_BUTTONS_KEY,
                GamepadSettingsFragment.KEYLINKER_HINT_KEY
            ),
            gamepadPreferences().map { it.key }
        )
    }

    @Test
    fun `gamepad submenu offers the extension as a checkbox that is off by default`() {
        val preference = gamepadPreferences()
            .singleOrNull { it.key == SettingsFragment.SETTINGS_SHOW_PHYSICAL_GAMEPAD_BRICKS }

        assertNotNull(preference)
        assertEquals("CheckBoxPreference", preference?.tag)
        assertEquals("false", preference?.defaultValue)
        assertEquals(R.string.preference_title_enable_physical_gamepad_bricks, preference?.titleResId)
        assertEquals("Enable bricks and sensors", preference?.summaryResId?.let(context::getString))
    }

    @Test
    fun `swap setting has a stable key and is off by default`() {
        assertEquals("setting_gamepad_swap_face_buttons", SettingsFragment.SETTINGS_GAMEPAD_SWAP_FACE_BUTTONS)
        assertFalse(SettingsFragment.isGamepadFaceButtonSwapEnabled(context))
    }

    @Test
    fun `swap setting is on when enabled`() {
        sharedPreferences.edit()
            .putBoolean(SettingsFragment.SETTINGS_GAMEPAD_SWAP_FACE_BUTTONS, true)
            .commit()

        assertTrue(SettingsFragment.isGamepadFaceButtonSwapEnabled(context))
    }

    @Test
    fun `gamepad submenu offers the swap setting as a checkbox that depends on the gamepad extension`() {
        val preference = gamepadPreferences()
            .singleOrNull { it.key == SettingsFragment.SETTINGS_GAMEPAD_SWAP_FACE_BUTTONS }

        assertNotNull(preference)
        assertEquals("CheckBoxPreference", preference?.tag)
        assertEquals("false", preference?.defaultValue)
        assertEquals(SettingsFragment.SETTINGS_SHOW_PHYSICAL_GAMEPAD_BRICKS, preference?.dependency)
        assertEquals(R.string.preference_title_gamepad_swap_face_buttons, preference?.titleResId)
        assertEquals(R.string.preference_description_gamepad_swap_face_buttons, preference?.summaryResId)
    }

    @Test
    fun `swap setting has its title and summary`() {
        assertEquals(
            "Swap A/B and X/Y",
            context.getString(R.string.preference_title_gamepad_swap_face_buttons)
        )
        assertEquals(
            "For controllers that report A and B, and X and Y, the other way round",
            context.getString(R.string.preference_description_gamepad_swap_face_buttons)
        )
    }

    @Test
    fun `gamepad submenu offers the detection of the swap as an action that depends on the gamepad extension`() {
        val preference = gamepadPreferences()
            .singleOrNull { it.key == GamepadSettingsFragment.DETECT_FACE_BUTTONS_KEY }

        assertNotNull(preference)
        assertEquals("Preference", preference?.tag)
        assertEquals("false", preference?.persistent)
        assertEquals(SettingsFragment.SETTINGS_SHOW_PHYSICAL_GAMEPAD_BRICKS, preference?.dependency)
        assertEquals(R.string.preference_title_gamepad_detect_face_buttons, preference?.titleResId)
        assertEquals(R.string.preference_description_gamepad_detect_face_buttons, preference?.summaryResId)
    }

    @Test
    fun `detection of the swap has its title, summary and prompt`() {
        assertEquals(
            "Detect button layout",
            context.getString(R.string.preference_title_gamepad_detect_face_buttons)
        )
        assertEquals(
            "Find out with one press of A whether to swap A/B and X/Y",
            context.getString(R.string.preference_description_gamepad_detect_face_buttons)
        )
        assertEquals(
            "Press the button labeled A on your controller, or the lower button if your buttons have symbols.",
            context.getString(R.string.dialog_gamepad_detect_face_buttons)
        )
    }

    @Test
    fun `gamepad submenu offers the KeyLinker hint as text that cannot be tapped`() {
        val preference = gamepadPreferences()
            .singleOrNull { it.key == GamepadSettingsFragment.KEYLINKER_HINT_KEY }

        assertNotNull(preference)
        assertEquals("Preference", preference?.tag)
        assertEquals("false", preference?.selectable)
        assertEquals("false", preference?.persistent)
        assertNull(preference?.dependency)
        assertEquals(R.string.preference_title_gamepad_keylinker, preference?.titleResId)
        assertEquals(R.string.preference_description_gamepad_keylinker, preference?.summaryResId)
    }

    @Test
    fun `gamepad submenu links to no app or web page`() {
        assertTrue(gamepadPreferenceTags().none { it == "intent" })
    }

    @Test
    fun `KeyLinker hint names the app, its maker and what it adjusts`() {
        assertEquals("KeyLinker app", context.getString(R.string.preference_title_gamepad_keylinker))
        assertEquals(
            "Some controllers can be set up with the KeyLinker app by ShenZhen ZhiXu Technology Co., " +
                "Ltd., for example to remap buttons or to adjust the center and the response curve of " +
                "the sticks.",
            context.getString(R.string.preference_description_gamepad_keylinker)
        )
    }

    private fun setPhysicalGamepadSetting(enabled: Boolean) {
        sharedPreferences.edit()
            .putBoolean(SettingsFragment.SETTINGS_SHOW_PHYSICAL_GAMEPAD_BRICKS, enabled)
            .commit()
    }

    /** Reads the direct children of the root preference screen in R.xml.preferences. */
    private fun topLevelPreferences(): List<PreferenceEntry> = topLevelPreferencesOf(R.xml.preferences)

    /** Reads the direct children of the root preference screen of the gamepad submenu. */
    private fun gamepadPreferences(): List<PreferenceEntry> = topLevelPreferencesOf(gamepadPreferencesResId())

    /** Reads the tags of all elements of the gamepad submenu, at any depth. */
    private fun gamepadPreferenceTags(): List<String> {
        val parser = context.resources.getXml(gamepadPreferencesResId())
        val tags = mutableListOf<String>()
        var eventType = parser.eventType
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG) {
                tags.add(parser.name)
            }
            eventType = parser.next()
        }
        parser.close()
        return tags
    }

    private fun gamepadPreferencesResId(): Int {
        val xmlResId = context.resources.getIdentifier("gamepad_preferences", "xml", context.packageName)
        assertTrue("The gamepad submenu has no preference file", xmlResId != 0)
        return xmlResId
    }

    private fun topLevelPreferencesOf(xmlResId: Int): List<PreferenceEntry> {
        val parser = context.resources.getXml(xmlResId)
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
        val dependency: String?,
        val persistent: String?,
        val selectable: String?,
        val titleResId: Int,
        val summaryResId: Int
    ) {
        companion object {
            fun from(parser: XmlResourceParser) = PreferenceEntry(
                tag = parser.name,
                key = parser.getAttributeValue(ANDROID_NAMESPACE, "key"),
                defaultValue = parser.getAttributeValue(ANDROID_NAMESPACE, "defaultValue"),
                dependency = parser.getAttributeValue(ANDROID_NAMESPACE, "dependency"),
                persistent = parser.getAttributeValue(ANDROID_NAMESPACE, "persistent"),
                selectable = parser.getAttributeValue(ANDROID_NAMESPACE, "selectable"),
                titleResId = parser.getAttributeResourceValue(ANDROID_NAMESPACE, "title", 0),
                summaryResId = parser.getAttributeResourceValue(ANDROID_NAMESPACE, "summary", 0)
            )
        }
    }

    private companion object {
        const val ANDROID_NAMESPACE = "http://schemas.android.com/apk/res/android"
        const val TOP_LEVEL_DEPTH = 2
        const val GAMEPAD_SCREEN_KEY = "setting_gamepad_screen"
    }
}

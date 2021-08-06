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

package org.catrobat.catroid.uiespresso.ui.fragment

import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.catrobat.catroid.R
import org.catrobat.catroid.ui.SettingsActivity
import org.catrobat.catroid.ui.settingsfragments.AccessibilityProfile.BEGINNER_BRICKS
import org.catrobat.catroid.ui.settingsfragments.AccessibilityProfile.DRAGNDROP_DELAY
import org.catrobat.catroid.ui.settingsfragments.AccessibilityProfile.ELEMENT_SPACING
import org.catrobat.catroid.ui.settingsfragments.AccessibilityProfile.FONT_STYLE
import org.catrobat.catroid.ui.settingsfragments.AccessibilityProfile.HIGH_CONTRAST
import org.catrobat.catroid.ui.settingsfragments.AccessibilityProfile.ICONS
import org.catrobat.catroid.ui.settingsfragments.AccessibilityProfile.ICON_HIGH_CONTRAST
import org.catrobat.catroid.ui.settingsfragments.AccessibilityProfile.LARGE_ICONS
import org.catrobat.catroid.ui.settingsfragments.AccessibilityProfile.LARGE_TEXT
import org.catrobat.catroid.ui.settingsfragments.AccessibilityProfile.fromCurrentPreferences
import org.catrobat.catroid.ui.settingsfragments.AccessibilityProfilesFragment
import org.catrobat.catroid.ui.settingsfragments.AccessibilitySettingsFragment.Companion.CUSTOM_PROFILE
import org.catrobat.catroid.uiespresso.ui.fragment.settingfragmenttestutils.SettingsFragmentTestUtils.checkFragmentIs
import org.catrobat.catroid.uiespresso.ui.fragment.settingfragmenttestutils.SettingsFragmentTestUtils.clickOnSettingsItem
import org.catrobat.catroid.uiespresso.ui.fragment.settingfragmenttestutils.SettingsFragmentTestUtils.loadSavedBooleanSettings
import org.catrobat.catroid.uiespresso.ui.fragment.settingfragmenttestutils.SettingsFragmentTestUtils.saveBooleanSettings
import org.catrobat.catroid.uiespresso.ui.fragment.settingfragmenttestutils.SettingsFragmentTestUtils.setAllBooleanSettingsTo
import org.catrobat.catroid.uiespresso.ui.fragment.settingfragmenttestutils.SettingsFragmentTestUtils.switchPreference
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityTestRule
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AccessibilitySettingsFragmentTest {
    @get:Rule
    var baseActivityTestRule = BaseActivityTestRule(
        SettingsActivity::class.java, true, false
    )

    private val settingItems: List<String> = listOf(
        LARGE_TEXT,
        HIGH_CONTRAST,
        ICONS,
        LARGE_ICONS,
        ICON_HIGH_CONTRAST,
        ELEMENT_SPACING,
        DRAGNDROP_DELAY,
        BEGINNER_BRICKS
    )

    private val savedSettings = HashMap<String, Boolean>()
    private lateinit var sharedPreferences: SharedPreferences

    @Before
    fun setup() {
        baseActivityTestRule.launchActivity(null)
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(baseActivityTestRule.activity)
        saveBooleanSettings(savedSettings, settingItems)
        setAllBooleanSettingsTo(false, settingItems)
        clickOnSettingsItem(R.string.preference_title_accessibility)
    }

    @After
    fun teardown() {
        pressBack()
        fromCurrentPreferences(sharedPreferences).clearCurrent(sharedPreferences)
        loadSavedBooleanSettings(savedSettings)
        baseActivityTestRule.finishActivity()
    }

    @Test
    fun textAppearanceSettingsTest() {
        switchPreference(R.string.preference_title_accessibility_large_text, LARGE_TEXT)
        switchPreference(R.string.preference_title_accessibility_high_contrast, HIGH_CONTRAST)

        changeFontStyle(R.string.accessibility_font_serif)
        changeFontStyle(R.string.accessibility_font_dyslexic)
        changeFontStyle(R.string.accessibility_font_regular)
    }

    @Test
    fun iconsSettingsTest() {
        switchPreference(R.string.preference_title_accessibility_category_icons, ICONS)
        switchPreference(R.string.preference_title_accessibility_category_icons_big, LARGE_ICONS)
        switchPreference(R.string.preference_title_accessibility_category_icons_high_contrast, ICON_HIGH_CONTRAST)
    }

    @Test
    fun handlingSettingsTest() {
        switchPreference(R.string.preference_title_accessibility_element_spacing, ELEMENT_SPACING)
        switchPreference(R.string.preference_title_accessibility_dragndrop_delay, DRAGNDROP_DELAY)
        switchPreference(R.string.preference_title_accessibility_beginner_bricks, BEGINNER_BRICKS)
    }

    @Test
    fun predefinedProfilesTest() {
        activateAccessibilityProfile(R.id.argus)
        assertPredefinedAccessibilityProfile(R.id.argus)

        activateAccessibilityProfile(R.id.fenrir)
        assertPredefinedAccessibilityProfile(R.id.fenrir)

        activateAccessibilityProfile(R.id.odin)
        assertPredefinedAccessibilityProfile(R.id.odin)

        activateAccessibilityProfile(R.id.tiro)
        assertPredefinedAccessibilityProfile(R.id.tiro)
    }

    @Test
    fun customProfileTest() {
        clickOnSettingsItem(R.string.preference_title_accessibility_large_text)
        clickOnSettingsItem(R.string.preference_title_accessibility_category_icons)

        activateAccessibilityProfile(R.id.custom_profile)

        assertTrue(sharedPreferences.getBoolean(LARGE_TEXT, false))
        assertTrue(sharedPreferences.getBoolean(ICONS, false))
        assertTrue(sharedPreferences.getBoolean(CUSTOM_PROFILE, false))

        clickOnSettingsItem(R.string.preference_title_accessibility)

        activateAccessibilityProfile(R.id.default_profile)

        assertFalse(sharedPreferences.getBoolean(LARGE_TEXT, true))
        assertFalse(sharedPreferences.getBoolean(ICONS, true))
        assertFalse(sharedPreferences.getBoolean(CUSTOM_PROFILE, true))

        activateAccessibilityProfile(R.id.custom_profile)

        assertTrue(sharedPreferences.getBoolean(LARGE_TEXT, false))
        assertTrue(sharedPreferences.getBoolean(ICONS, false))
        assertTrue(sharedPreferences.getBoolean(CUSTOM_PROFILE, false))
    }

    private fun changeFontStyle(text: Int) {
        clickOnSettingsItem(R.string.preference_title_accessibility_font_style)
        onView(withText(text))
            .perform(click())
        pressBack()

        val expectedFont = if (text == R.string.accessibility_font_regular) {
            baseActivityTestRule.activity.getString(R.string.sans_serif_font).toLowerCase()
        } else {
            baseActivityTestRule.activity.getString(text).toLowerCase()
        }

        clickOnSettingsItem(R.string.preference_title_accessibility)
        assertEquals(expectedFont, sharedPreferences.getString(FONT_STYLE, ""))
    }

    private fun activateAccessibilityProfile(id: Int) {
        clickOnSettingsItem(R.string.preference_title_accessibility_profile)

        checkFragmentIs(AccessibilityProfilesFragment::class.java,
            baseActivityTestRule.activity.supportFragmentManager.fragments)

        onView(withId(id))
            .perform(scrollTo())
            .perform(click())
    }

    private fun assertPredefinedAccessibilityProfile(id: Int) {
        when (id) {
            R.id.argus -> {
                assertTrue(sharedPreferences.getBoolean(HIGH_CONTRAST, false))
                assertTrue(sharedPreferences.getBoolean(ICONS, false))
            }
            R.id.fenrir -> {
                assertTrue(sharedPreferences.getBoolean(ELEMENT_SPACING, false))
                assertTrue(sharedPreferences.getBoolean(DRAGNDROP_DELAY, false))
            }
            R.id.odin -> {
                assertTrue(sharedPreferences.getBoolean(LARGE_TEXT, false))
                assertTrue(sharedPreferences.getBoolean(HIGH_CONTRAST, false))
                assertTrue(sharedPreferences.getBoolean(ICONS, false))
                assertTrue(sharedPreferences.getBoolean(LARGE_ICONS, false))
                assertTrue(sharedPreferences.getBoolean(ELEMENT_SPACING, false))
            }
            R.id.tiro -> assertTrue(sharedPreferences.getBoolean(BEGINNER_BRICKS, false))
        }
    }
}

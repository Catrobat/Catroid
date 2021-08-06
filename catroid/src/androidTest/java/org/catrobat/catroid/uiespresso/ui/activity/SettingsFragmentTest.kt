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

package org.catrobat.catroid.uiespresso.ui.activity

import android.content.SharedPreferences
import androidx.preference.PreferenceManager.getDefaultSharedPreferences
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.catrobat.catroid.R
import org.catrobat.catroid.common.SharedPreferenceKeys
import org.catrobat.catroid.ui.SettingsActivity
import org.catrobat.catroid.ui.settingsfragments.AccessibilitySettingsFragment
import org.catrobat.catroid.ui.settingsfragments.ManageExtensionsSettingsFragment
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment.Companion.SETTINGS_CRASH_REPORTS
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment.Companion.SETTINGS_SHOW_HINTS
import org.catrobat.catroid.ui.settingsfragments.webaccess.WebAccessSettingsFragment
import org.catrobat.catroid.uiespresso.ui.fragment.settingfragmenttestutils.SettingsFragmentTestUtils.checkFragmentIs
import org.catrobat.catroid.uiespresso.ui.fragment.settingfragmenttestutils.SettingsFragmentTestUtils.checkToolbarTitleIsDisplayed
import org.catrobat.catroid.uiespresso.ui.fragment.settingfragmenttestutils.SettingsFragmentTestUtils.clickOnSettingsItem
import org.catrobat.catroid.uiespresso.ui.fragment.settingfragmenttestutils.SettingsFragmentTestUtils.loadSavedBooleanSettings
import org.catrobat.catroid.uiespresso.ui.fragment.settingfragmenttestutils.SettingsFragmentTestUtils.saveBooleanSettings
import org.catrobat.catroid.uiespresso.ui.fragment.settingfragmenttestutils.SettingsFragmentTestUtils.setAllBooleanSettingsTo
import org.catrobat.catroid.uiespresso.ui.fragment.settingfragmenttestutils.SettingsFragmentTestUtils.switchPreference
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityTestRule
import org.hamcrest.Matchers
import org.hamcrest.core.Is.`is`
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.Locale.forLanguageTag

@RunWith(AndroidJUnit4::class)
class SettingsFragmentTest {
    @get:Rule
    var baseActivityTestRule = BaseActivityTestRule(
        SettingsActivity::class.java, true, false
    )

    private val settingItems: List<String> = listOf(
        SETTINGS_SHOW_HINTS,
        SETTINGS_CRASH_REPORTS
    )

    private val savedSettings = HashMap<String, Boolean>()
    private lateinit var sharedPreferences: SharedPreferences

    @Before
    fun setUp() {
        baseActivityTestRule.launchActivity(null)
        sharedPreferences = getDefaultSharedPreferences(baseActivityTestRule.activity)
        saveBooleanSettings(savedSettings, settingItems)
        setAllBooleanSettingsTo(false, settingItems)
    }

    @After
    fun teardown() {
        loadSavedBooleanSettings(savedSettings)
    }

    @Test
    fun languageSettingTest() {
        clickOnSettingsItem(R.string.preference_title_language)

        onView(withText(R.string.preference_title_language))
            .check(matches(isDisplayed()))

        onData(`is`(Matchers.instanceOf(String::class.java)))
            .atPosition(0)
            .check(matches(withText(R.string.device_language)))

        for (languageTag in SharedPreferenceKeys.LANGUAGE_TAGS) {
            if (languageTag != SharedPreferenceKeys.DEVICE_LANGUAGE) {
                val rtlLocale = forLanguageTag(languageTag)
                onData(Matchers.hasToString(rtlLocale.getDisplayName(rtlLocale)))
                    .check(matches(isDisplayed()))
            }
        }
        onView(withId(android.R.id.button2))
            .check(matches(isDisplayed()))
    }

    @Test
    fun basicSettingsTest() {
        baseActivityTestRule.finishActivity()
        baseActivityTestRule.launchActivity(null)
        switchPreference(R.string.preference_title_enable_hints, SETTINGS_SHOW_HINTS)
        switchPreference(R.string.preference_title_enable_crash_reports, SETTINGS_CRASH_REPORTS)
    }

    @Test
    fun openAccessibilitySettingsTest() {
        clickOnSettingsItem(R.string.preference_title_accessibility)

        checkToolbarTitleIsDisplayed(R.string.preference_title_accessibility)
        checkFragmentIs(AccessibilitySettingsFragment::class.java,
            baseActivityTestRule.activity.supportFragmentManager.fragments)
    }

    @Test
    fun openWebAccessSettingsTest() {
        clickOnSettingsItem(R.string.preference_title_web_access)

        checkToolbarTitleIsDisplayed(R.string.preference_title_web_access)
        checkFragmentIs(WebAccessSettingsFragment::class.java,
            baseActivityTestRule.activity.supportFragmentManager.fragments)
    }

    @Test
    fun openManageExtensionsSettingsTest() {
        clickOnSettingsItem(R.string.preference_title_manage_extensions)

        checkToolbarTitleIsDisplayed(R.string.preference_title_manage_extensions)
        checkFragmentIs(ManageExtensionsSettingsFragment::class.java,
            baseActivityTestRule.activity.supportFragmentManager.fragments)
    }
}

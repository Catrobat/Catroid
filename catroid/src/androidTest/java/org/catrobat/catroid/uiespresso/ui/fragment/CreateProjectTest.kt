/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2022 The Catrobat Team
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
import android.preference.PreferenceManager.getDefaultSharedPreferences
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.closeSoftKeyboard
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isChecked
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isEnabled
import androidx.test.espresso.matcher.ViewMatchers.isNotChecked
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.catrobat.catroid.R
import org.catrobat.catroid.common.Constants.CATROBAT_TERMS_OF_USE_ACCEPTED
import org.catrobat.catroid.common.SharedPreferenceKeys.AGREED_TO_PRIVACY_POLICY_VERSION
import org.catrobat.catroid.runner.Flaky
import org.catrobat.catroid.ui.MainMenuActivity
import org.catrobat.catroid.ui.ProjectActivity
import org.catrobat.catroid.ui.ProjectListActivity
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment
import org.catrobat.catroid.uiespresso.util.UiTestUtils.Companion.assertCurrentActivityIsInstanceOf
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityTestRule
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.Matchers.not
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CreateProjectTest {
    private var bufferedChromeCastSetting = false
    private var bufferedPrivacyPolicyPreferenceSetting = 0
    private lateinit var sharedPreferences: SharedPreferences
    private val newProjectName = CreateProjectTest::class.simpleName

    @get:Rule
    var baseActivityTestRule = BaseActivityTestRule(
        MainMenuActivity::class.java, false, false
    )

    @Before
    fun setUp() {
        sharedPreferences = getDefaultSharedPreferences(getApplicationContext())

        bufferedChromeCastSetting = sharedPreferences.getBoolean(SettingsFragment.SETTINGS_CAST_GLOBALLY_ENABLED, false)
        bufferedPrivacyPolicyPreferenceSetting = sharedPreferences.getInt(AGREED_TO_PRIVACY_POLICY_VERSION, 0)

        sharedPreferences
            .edit()
            .putBoolean(SettingsFragment.SETTINGS_CAST_GLOBALLY_ENABLED, true)
            .putInt(AGREED_TO_PRIVACY_POLICY_VERSION, CATROBAT_TERMS_OF_USE_ACCEPTED)
            .commit()

        baseActivityTestRule.launchActivity(null)
    }

    @After
    fun tearDown() {
        sharedPreferences
            .edit()
            .putBoolean(SettingsFragment.SETTINGS_CAST_GLOBALLY_ENABLED, bufferedChromeCastSetting)
            .putInt(AGREED_TO_PRIVACY_POLICY_VERSION, bufferedPrivacyPolicyPreferenceSetting)
            .commit()
    }

    @Test
    fun testNewProjectDialogFragment() {
        onView(withId(R.id.newProjectFloatingActionButton))
            .perform(click())

        onView(withText(R.string.new_project_title))
            .check(matches(isDisplayed()))

        onView(withId(R.id.confirm))
            .check(matches(allOf(isDisplayed(), isEnabled())))

        onView(withId(R.id.input))
            .check(matches(isDisplayed()))

        onView(withId(R.id.portrait_radio_button))
            .check(matches(allOf(isDisplayed(), isChecked())))

        onView(withId(R.id.landscape_radio_button))
            .check(matches(allOf(isDisplayed(), isNotChecked())))

        onView(withId(R.id.cast_radio_button))
            .check(matches(allOf(isDisplayed(), isNotChecked())))

        onView(withId(R.id.example_project_switch))
            .check(matches(allOf(isDisplayed(), isNotChecked())))
    }

    @Test
    fun testCastOptionNotShowed() {
        sharedPreferences
            .edit()
            .putBoolean(SettingsFragment.SETTINGS_CAST_GLOBALLY_ENABLED, false)
            .commit()

        onView(withId(R.id.newProjectFloatingActionButton))
            .perform(click())

        onView(withId(R.id.portrait_radio_button))
            .check(matches(allOf(isDisplayed(), isChecked())))

        onView(withId(R.id.landscape_radio_button))
            .check(matches(allOf(isDisplayed(), isNotChecked())))

        onView(withId(R.id.cast_radio_button))
            .check(matches(not(isDisplayed())))
    }

    @Test
    fun testCreateNewCastProject() {
        onView(withId(R.id.newProjectFloatingActionButton))
            .perform(click())

        closeSoftKeyboard()

        onView(withId(R.id.cast_radio_button))
            .perform(click())

        onView(withId(R.id.confirm))
            .perform(click())

        assertCurrentActivityIsInstanceOf(ProjectActivity::class.java)
    }

    @Test
    fun testCreateProjectInProjectList() {
        onView(withId(R.id.myProjectsTextView))
            .perform(click())

        onView(withId(R.id.button_add))
            .perform(click())

        onView(withId(R.id.input_edit_text))
            .perform(replaceText(newProjectName))

        closeSoftKeyboard()

        onView(withId(R.id.example_project_switch))
            .perform(click())

        onView(withId(R.id.confirm))
            .perform(click())

        onView(withText(newProjectName))
            .check(matches(isDisplayed()))

        assertCurrentActivityIsInstanceOf(ProjectActivity::class.java)

        pressBack()

        onView(withText(newProjectName))
            .check(matches(isDisplayed()))

        assertCurrentActivityIsInstanceOf(ProjectListActivity::class.java)
    }

    @Test
    @Flaky
    fun testCreateProjectInMainMenu() {
        onView(withId(R.id.newProjectFloatingActionButton))
            .perform(click())

        onView(withId(R.id.input_edit_text))
            .perform(replaceText(newProjectName))

        closeSoftKeyboard()

        onView(withId(R.id.example_project_switch))
            .perform(click())

        onView(withId(R.id.confirm))
            .perform(click())

        onView(withText(newProjectName))
            .check(matches(isDisplayed()))

        assertCurrentActivityIsInstanceOf(ProjectActivity::class.java)

        pressBack()

        assertCurrentActivityIsInstanceOf(MainMenuActivity::class.java)

        onView(withId(R.id.projectImageView))
            .perform(click())

        onView(withText(newProjectName))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testCreateProjectWithExistingName() {
        onView(withId(R.id.newProjectFloatingActionButton))
            .perform(click())

        onView(withId(R.id.input_edit_text))
            .perform(replaceText(newProjectName))

        closeSoftKeyboard()

        onView(withId(R.id.confirm))
            .perform(click())

        pressBack()

        onView(withId(R.id.newProjectFloatingActionButton))
            .perform(click())

        onView(withId(R.id.input_edit_text))
            .perform(replaceText(newProjectName))

        onView(withText(R.string.name_already_exists))
            .check(matches(isDisplayed()))

        onView(withId(R.id.confirm))
            .check(matches(allOf(isDisplayed(), not(isEnabled()))))

        pressBack()

        assertCurrentActivityIsInstanceOf(MainMenuActivity::class.java)
    }
}

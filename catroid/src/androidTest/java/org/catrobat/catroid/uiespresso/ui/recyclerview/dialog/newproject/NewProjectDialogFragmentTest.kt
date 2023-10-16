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

package org.catrobat.catroid.uiespresso.ui.recyclerview.dialog.newproject

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.view.View
import android.widget.Spinner
import android.widget.TextView
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.BoundedMatcher
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.catrobat.catroid.R
import org.catrobat.catroid.common.Constants.CATROBAT_TERMS_OF_USE_ACCEPTED
import org.catrobat.catroid.common.ScreenValues
import org.catrobat.catroid.common.SharedPreferenceKeys.AGREED_TO_PRIVACY_POLICY_VERSION
import org.catrobat.catroid.ui.MainMenuActivity
import org.catrobat.catroid.ui.recyclerview.dialog.newproject.FrameSize
import org.catrobat.catroid.ui.recyclerview.dialog.newproject.FrameSizeUnit
import org.catrobat.catroid.ui.recyclerview.dialog.newproject.NewProjectUniqueNameProvider
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityTestRule
import org.hamcrest.Description
import org.hamcrest.Matchers
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.test.KoinTest

@RunWith(AndroidJUnit4::class)
class NewProjectDialogFragmentTest : KoinTest {
    private lateinit var applicationContext: Context
    private var bufferedChromeCastSetting = false
    private var bufferedPrivacyPolicyPreferenceSetting = 0
    private lateinit var sharedPreferences: SharedPreferences
    private val uniqueNameProvider = NewProjectUniqueNameProvider()

    @get:Rule
    var baseActivityTestRule = BaseActivityTestRule(
        MainMenuActivity::class.java, false, false
    )

    @Before
    fun setUp() {
        applicationContext = ApplicationProvider.getApplicationContext()
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)

        bufferedChromeCastSetting =
            sharedPreferences.getBoolean(SettingsFragment.SETTINGS_CAST_GLOBALLY_ENABLED, false)
        bufferedPrivacyPolicyPreferenceSetting =
            sharedPreferences.getInt(AGREED_TO_PRIVACY_POLICY_VERSION, 0)

        sharedPreferences.edit().putBoolean(SettingsFragment.SETTINGS_CAST_GLOBALLY_ENABLED, true)
            .putInt(AGREED_TO_PRIVACY_POLICY_VERSION, CATROBAT_TERMS_OF_USE_ACCEPTED).commit()

        baseActivityTestRule.launchActivity(null)
    }

    @After
    fun tearDown() {
        sharedPreferences.edit()
            .putBoolean(SettingsFragment.SETTINGS_CAST_GLOBALLY_ENABLED, bufferedChromeCastSetting)
            .putInt(AGREED_TO_PRIVACY_POLICY_VERSION, bufferedPrivacyPolicyPreferenceSetting)
            .commit()
    }

    @Test
    fun checkDefaultValues() {
        Espresso.onView(ViewMatchers.withId(R.id.newProjectFloatingActionButton))
            .check(matches(ViewMatchers.isDisplayed()))
            .check(matches(ViewMatchers.isClickable()))

        Espresso.onView(ViewMatchers.withId(R.id.newProjectFloatingActionButton))
            .perform(click())

        val uniqueName = uniqueNameProvider.getUniqueName(
            applicationContext.getString(R.string.default_project_name), null
        )

        Espresso.onView(ViewMatchers.withId(R.id.input_edit_text))
            .check(matches(ViewMatchers.withText(uniqueName)))
            .check(matches(ViewMatchers.withHint(R.string.project_name_label)))

        // check orientation

        Espresso.onView(ViewMatchers.withId(R.id.portrait_radio_button))
            .check(matches(ViewMatchers.isDisplayed()))
            .check(matches(ViewMatchers.isChecked()))

        Espresso.onView(ViewMatchers.withId(R.id.landscape_mode_radio_button))
            .check(matches(ViewMatchers.isDisplayed()))
            .check(matches(ViewMatchers.isNotChecked()))

        Espresso.onView(ViewMatchers.withId(R.id.cast_radio_button))
            .check(matches(ViewMatchers.isDisplayed()))
            .check(matches(ViewMatchers.isNotChecked()))

        // check is example project

        Espresso.onView(ViewMatchers.withId(R.id.example_project_switch))
            .check(matches(ViewMatchers.isDisplayed()))
            .check(matches(ViewMatchers.isNotChecked()))
            .check(matches(ViewMatchers.withText(R.string.new_project_dialog_example_project)))

        Espresso.onView(ViewMatchers.withId(R.id.example_project_switch))
            .check(matches(ViewMatchers.isDisplayed()))
            .check(matches(ViewMatchers.isNotChecked()))
            .check(matches(ViewMatchers.withText(R.string.new_project_dialog_example_project)))

        // check unit

        Espresso.onView(ViewMatchers.withId(R.id.cm_radio_button))
            .check(matches(ViewMatchers.isDisplayed()))
            .check(matches(ViewMatchers.isChecked()))
            .check(matches(ViewMatchers.withText(R.string.cm)))

        Espresso.onView(ViewMatchers.withId(R.id.inch_radioButton))
            .check(matches(ViewMatchers.isDisplayed()))
            .check(matches(ViewMatchers.isNotChecked()))
            .check(matches(ViewMatchers.withText(R.string.inch)))

        // check displayed frame size

        val currentFrameTextCM = getCurrentFrameSizeText(false, FrameSizeUnit.CM)
        Espresso.onView(ViewMatchers.withId(R.id.frame_size_spinner))
            .check(matches(ViewMatchers.isDisplayed()))
            .check(matches(withSpinnerFrameSizeAdapterText(currentFrameTextCM)))

        // Check cancel and ok button

        Espresso.onView(ViewMatchers.withText(R.string.cancel))
            .check(matches(ViewMatchers.isDisplayed()))
            .check(matches(ViewMatchers.isEnabled()))

        Espresso.onView(ViewMatchers.withText(R.string.ok))
            .check(matches(ViewMatchers.isDisplayed()))
            .check(matches(ViewMatchers.isEnabled()))
    }

    @Test
    fun checkSpinnerTextChangeOnRadioButtonClick() {
        Espresso.onView(ViewMatchers.withId(R.id.newProjectFloatingActionButton))
            .perform(click())

        val portraitCmText = getCurrentFrameSizeText(false, FrameSizeUnit.CM)
        val portraitInchText = getCurrentFrameSizeText(false, FrameSizeUnit.INCH)
        val landscapeInchText = getCurrentFrameSizeText(true, FrameSizeUnit.INCH)
        val landscapeCmText = getCurrentFrameSizeText(true, FrameSizeUnit.CM)

        val spinner = Espresso.onView(ViewMatchers.withId(R.id.frame_size_spinner))

        spinner.check(matches(withSpinnerFrameSizeAdapterText(portraitCmText)))

        Espresso.onView(ViewMatchers.withId(R.id.inch_radioButton)).perform(click())
        spinner.check(matches(withSpinnerFrameSizeAdapterText(portraitInchText)))

        Espresso.onView(ViewMatchers.withId(R.id.landscape_mode_radio_button)).perform(click())
        spinner.check(matches(withSpinnerFrameSizeAdapterText(landscapeInchText)))

        Espresso.onView(ViewMatchers.withId(R.id.cm_radio_button)).perform(click())
        spinner.check(matches(withSpinnerFrameSizeAdapterText(landscapeCmText)))

        Espresso.onView(ViewMatchers.withId(R.id.portrait_radio_button)).perform(click())
        spinner.check(matches(withSpinnerFrameSizeAdapterText(portraitCmText)))
    }

    private fun getCurrentFrameSizeText(landscape: Boolean, unit: FrameSizeUnit): String {
        val frame = FrameSize(ScreenValues.SCREEN_HEIGHT, ScreenValues.SCREEN_WIDTH)
        val resourceId = when (unit) {
            FrameSizeUnit.CM -> R.string.frame_size_in_cm
            FrameSizeUnit.INCH -> R.string.frame_size_in_inches
            else -> throw IllegalStateException("No implementation for unit pixel")
        }

        val height = frame.getHeight(landscape, unit)
        val width = frame.getWidth(landscape, unit)
        return applicationContext.getString(resourceId, height, width)
    }

    private fun withSpinnerFrameSizeAdapterText(expected: String):
        BoundedMatcher<View, Spinner> {
        val stringMatcher = Matchers.`is`(expected)
        return object : BoundedMatcher<View, Spinner>(Spinner::class.java) {
            override fun describeTo(description: Description?) {
                description!!.appendText("with text: ")
                stringMatcher.describeTo(description)
            }

            override fun matchesSafely(spinner: Spinner?): Boolean {
                val text = (spinner!!.selectedView as TextView).text
                return stringMatcher.matches(text)
            }
        }
    }
}

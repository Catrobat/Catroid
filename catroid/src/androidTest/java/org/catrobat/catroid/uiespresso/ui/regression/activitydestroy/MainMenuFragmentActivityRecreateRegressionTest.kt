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
package org.catrobat.catroid.uiespresso.ui.regression.activitydestroy

import android.content.Context
import android.preference.PreferenceManager.getDefaultSharedPreferences
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import org.catrobat.catroid.R
import org.catrobat.catroid.common.Constants.CATROBAT_TERMS_OF_USE_ACCEPTED
import org.catrobat.catroid.common.SharedPreferenceKeys.AGREED_TO_PRIVACY_POLICY_VERSION
import org.catrobat.catroid.testsuites.annotations.Cat.AppUi
import org.catrobat.catroid.testsuites.annotations.Cat.Quarantine
import org.catrobat.catroid.testsuites.annotations.Level.Smoke
import org.catrobat.catroid.ui.MainMenuActivity
import org.catrobat.catroid.uiespresso.util.rules.DontGenerateDefaultProjectActivityTestRule
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@Category(AppUi::class, Smoke::class, Quarantine::class)
@RunWith(JUnit4::class)
class MainMenuFragmentActivityRecreateRegressionTest {
    var bufferedPrivacyPolicyPreferenceSetting = 0
    val applicationContext: Context = getApplicationContext<Context>()

    @get:Rule
    var baseActivityTestRule = DontGenerateDefaultProjectActivityTestRule(
        MainMenuActivity::class.java, false, false
    )

    @Before
    fun setUp() {
        bufferedPrivacyPolicyPreferenceSetting = getDefaultSharedPreferences(getApplicationContext())
            .getInt(AGREED_TO_PRIVACY_POLICY_VERSION, 0)

        getDefaultSharedPreferences(applicationContext)
            .edit()
            .putInt(AGREED_TO_PRIVACY_POLICY_VERSION, CATROBAT_TERMS_OF_USE_ACCEPTED)
            .commit()

        baseActivityTestRule.launchActivity(null)
    }

    @After
    fun tearDown() {
        getDefaultSharedPreferences(applicationContext)
            .edit()
            .putInt(AGREED_TO_PRIVACY_POLICY_VERSION, bufferedPrivacyPolicyPreferenceSetting)
            .commit()
    }

    @Test
    fun testActivityRecreateOrientation() {
        onView(withId(R.id.newProjectFloatingActionButton))
            .perform(click())

        getInstrumentation().waitForIdleSync()

        onView(withId(R.id.input_edit_text))
            .check(matches(isDisplayed()))

        onView(withId(R.id.input_edit_text))
            .perform(replaceText("TestProject"), closeSoftKeyboard())

        getInstrumentation().waitForIdleSync()

        onView(withId(R.id.confirm))
            .perform(click())

        getInstrumentation().waitForIdleSync()
        getInstrumentation().runOnMainSync { baseActivityTestRule.activity.recreate() }
        getInstrumentation().waitForIdleSync()
    }

    @Test
    fun testActivityRecreateNewProgramDialog() {
        onView(withId(R.id.newProjectFloatingActionButton))
            .perform(click())

        getInstrumentation().waitForIdleSync()

        onView(withId(R.id.input_edit_text))
            .inRoot(isDialog())
            .check(matches(isDisplayed()))

        getInstrumentation().runOnMainSync { baseActivityTestRule.activity.recreate() }
        getInstrumentation().waitForIdleSync()
    }

    @Test
    fun testActivityRecreateTermsOfUseDialog() {
        openActionBarOverflowOrOptionsMenu(applicationContext)

        onView(withText(R.string.main_menu_terms_of_use))
            .perform(click())

        getInstrumentation().waitForIdleSync()

        onView(withId(R.id.dialog_terms_of_use_text_view_info))
            .inRoot(isDialog())
            .check(matches(isDisplayed()))

        getInstrumentation().runOnMainSync { baseActivityTestRule.activity.recreate() }
        getInstrumentation().waitForIdleSync()
    }

    @Test
    fun testActivityRecreateAboutDialog() {
        openActionBarOverflowOrOptionsMenu(applicationContext)

        onView(withText(R.string.main_menu_about))
            .perform(click())

        getInstrumentation().waitForIdleSync()

        onView(withText(R.string.dialog_about_title))
            .inRoot(isDialog())
            .check(matches(isDisplayed()))

        getInstrumentation().runOnMainSync { baseActivityTestRule.activity.recreate() }
        getInstrumentation().waitForIdleSync()
    }
}

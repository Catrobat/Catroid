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

import android.preference.PreferenceManager
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.Press
import androidx.test.espresso.action.Swipe
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.catrobat.catroid.R
import org.catrobat.catroid.common.Constants
import org.catrobat.catroid.common.SharedPreferenceKeys
import org.catrobat.catroid.testsuites.annotations.Cat.AppUi
import org.catrobat.catroid.testsuites.annotations.Level.Smoke
import org.catrobat.catroid.ui.MainMenuActivity
import org.catrobat.catroid.uiespresso.content.brick.utils.CustomSwipeAction
import org.catrobat.catroid.uiespresso.util.UiTestUtils.Companion.isVisibleWithTimeout
import org.catrobat.catroid.uiespresso.util.rules.DontGenerateDefaultProjectActivityTestRule
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.`is`
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class OnBoardingWelcomeActivityTest {

    @get:Rule
    var baseActivityTestRule = DontGenerateDefaultProjectActivityTestRule(
        MainMenuActivity::class.java, true, false
    )

    private var bufferedPrivacyPolicyPreferenceSetting = 0
    private var bufferedOnBoardingWelcomeScreenShownSetting = false

    @Before
    @Throws(Exception::class)
    fun setUp() {
        val sharedPreferences = PreferenceManager
            .getDefaultSharedPreferences(ApplicationProvider.getApplicationContext())
        bufferedPrivacyPolicyPreferenceSetting = sharedPreferences
            .getInt(SharedPreferenceKeys.AGREED_TO_PRIVACY_POLICY_VERSION, 0)
        bufferedOnBoardingWelcomeScreenShownSetting = sharedPreferences
                .getBoolean(SharedPreferenceKeys.ONBOARDING_WELCOME_SCREEN_SHOWN, false)

        sharedPreferences
            .edit()
            .putInt(
                SharedPreferenceKeys.AGREED_TO_PRIVACY_POLICY_VERSION,
                Constants.CATROBAT_TERMS_OF_USE_ACCEPTED
            )
            .putBoolean(
                SharedPreferenceKeys.ONBOARDING_WELCOME_SCREEN_SHOWN,
                false
            )
            .commit()

        baseActivityTestRule.launchActivity(null)
    }

    @After
    fun tearDown() {
        val sharedPreferences = PreferenceManager
            .getDefaultSharedPreferences(ApplicationProvider.getApplicationContext())
        sharedPreferences
            .edit()
            .putInt(
                SharedPreferenceKeys.AGREED_TO_PRIVACY_POLICY_VERSION,
                bufferedPrivacyPolicyPreferenceSetting
            )
            .putBoolean(
                SharedPreferenceKeys.ONBOARDING_WELCOME_SCREEN_SHOWN,
                bufferedOnBoardingWelcomeScreenShownSetting
            )
            .commit()
    }

    @Category(AppUi::class, Smoke::class)
    @Test
    fun onBoardingWelcomeTest() {
        var objectIsVisible: Boolean?

        Espresso.onView(ViewMatchers.withText(R.string.welcome_pocket_code))
            .check(matches(isDisplayed()))
        Espresso.onView(ViewMatchers.withText(R.string.welcome_pocket_code))
            .perform(swipeLeft())

        var interaction =
            Espresso.onView(allOf(ViewMatchers.withText(R.string.complete_control), isDisplayed()))
        objectIsVisible = isVisibleWithTimeout(interaction)
        assertThat(objectIsVisible, `is`(true))
        Espresso.onView(ViewMatchers.withText(R.string.complete_control))
            .perform(swipeLeft())

        interaction =
            Espresso.onView(allOf(ViewMatchers.withText(R.string.join_community), isDisplayed()))
        objectIsVisible = isVisibleWithTimeout(interaction)
        assertThat(objectIsVisible, `is`(true))
        Espresso.onView(ViewMatchers.withText(R.string.login))
            .check(matches(isDisplayed()))
        Espresso.onView(ViewMatchers.withText(R.string.register))
            .check(matches(isDisplayed()))
        Espresso.onView(ViewMatchers.withText("Sign in with Google"))
            .check(matches(isDisplayed()))
        Espresso.onView(ViewMatchers.withText(R.string.join_community))
            .perform(swipeLeft())

        interaction =
            Espresso.onView(allOf(ViewMatchers.withText(R.string.begin), isDisplayed()))
        objectIsVisible = isVisibleWithTimeout(interaction)
        assertThat(objectIsVisible, `is`(true))
        Espresso.onView(ViewMatchers.withText(R.string.tutorial_mode))
            .check(matches(isDisplayed()))
        Espresso.onView(ViewMatchers.withText(R.string.normal_mode))
            .check(matches(isDisplayed()))

        Espresso.onView(ViewMatchers.withText(R.string.normal_mode)).perform(click())
        Espresso.onView(ViewMatchers.withText(R.string.app_name)).check(matches(isDisplayed()))
    }

    private fun swipeLeft(): ViewAction {
        return CustomSwipeAction(
            Swipe.FAST,
            CustomSwipeAction.SwipeAction.SWIPE_LEFT,
            Press.THUMB
        )
    }
}

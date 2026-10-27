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

package org.catrobat.catroid.uiespresso.ui.dialog

import android.preference.PreferenceManager
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.Espresso.pressBackUnconditionally
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import org.catrobat.catroid.R
import org.catrobat.catroid.common.Constants
import org.catrobat.catroid.common.SharedPreferenceKeys
import org.catrobat.catroid.testsuites.annotations.Cat.AppUi
import org.catrobat.catroid.testsuites.annotations.Level.Smoke
import org.catrobat.catroid.ui.MainMenuActivity
import org.catrobat.catroid.uiespresso.util.rules.DontGenerateDefaultProjectActivityTestRule
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PrivacyPolicyAcceptanceDialogTest {

    @get:Rule
    var baseActivityTestRule = DontGenerateDefaultProjectActivityTestRule(
        MainMenuActivity::class.java, false, false
    )

    private var bufferedPrivacyPolicyPreferenceSetting = 0

    @Before
    fun setUp() {
        val sharedPreferences = PreferenceManager
            .getDefaultSharedPreferences(ApplicationProvider.getApplicationContext())
        bufferedPrivacyPolicyPreferenceSetting = sharedPreferences
            .getInt(SharedPreferenceKeys.AGREED_TO_PRIVACY_POLICY_VERSION, 0)
        sharedPreferences
            .edit()
            .putInt(SharedPreferenceKeys.AGREED_TO_PRIVACY_POLICY_VERSION, 0)
            .commit()
        baseActivityTestRule.launchActivity(null)
    }

    @After
    fun tearDown() {
        PreferenceManager.getDefaultSharedPreferences(ApplicationProvider.getApplicationContext())
            .edit()
            .putInt(
                SharedPreferenceKeys.AGREED_TO_PRIVACY_POLICY_VERSION,
                bufferedPrivacyPolicyPreferenceSetting
            )
            .commit()
    }

    @Category(AppUi::class, Smoke::class)
    @Test
    fun acceptStoresPreferenceTest() {
        onView(withText(R.string.disclaimer_privacy_policy_header))
            .check(matches(isDisplayed()))

        onView(withText(R.string.accept)).perform(click())

        onView(withText(R.string.disclaimer_privacy_policy_header))
            .check(doesNotExist())

        val storedVersion = PreferenceManager
            .getDefaultSharedPreferences(ApplicationProvider.getApplicationContext())
            .getInt(SharedPreferenceKeys.AGREED_TO_PRIVACY_POLICY_VERSION, 0)
        assertEquals(Constants.CATROBAT_TERMS_OF_USE_ACCEPTED, storedVersion)
    }

    @Category(AppUi::class, Smoke::class)
    @Test
    fun declineShowsDeclinedDialogAndOkReturnsTest() {
        onView(withText(R.string.decline)).perform(click())

        onView(withText(R.string.declined_terms_of_use_and_service_alert))
            .check(matches(isDisplayed()))
        onView(withId(R.id.share_website_view))
            .check(matches(isDisplayed()))

        onView(withText(R.string.ok)).perform(click())

        onView(withText(R.string.disclaimer_privacy_policy_header))
            .check(matches(isDisplayed()))
    }

    @Category(AppUi::class, Smoke::class)
    @Test
    fun backOnTermsOfUseDialogFinishesActivityTest() {
        onView(withText(R.string.disclaimer_privacy_policy_header))
            .check(matches(isDisplayed()))

        pressBackUnconditionally()
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()

        assertTrue(baseActivityTestRule.activity.isFinishing)
    }

    @Category(AppUi::class, Smoke::class)
    @Test
    fun backOnDeclinedDialogReturnsToTermsOfUseTest() {
        onView(withText(R.string.decline)).perform(click())

        onView(withText(R.string.declined_terms_of_use_and_service_alert))
            .check(matches(isDisplayed()))

        pressBack()

        onView(withText(R.string.disclaimer_privacy_policy_header))
            .check(matches(isDisplayed()))
    }
}

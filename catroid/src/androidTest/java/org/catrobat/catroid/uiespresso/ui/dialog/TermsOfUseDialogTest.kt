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
package org.catrobat.catroid.uiespresso.ui.dialog

import android.preference.PreferenceManager
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.catrobat.catroid.R
import org.catrobat.catroid.common.Constants
import org.catrobat.catroid.common.SharedPreferenceKeys
import org.catrobat.catroid.testsuites.annotations.Cat.AppUi
import org.catrobat.catroid.testsuites.annotations.Level.Smoke
import org.catrobat.catroid.ui.MainMenuActivity
import org.catrobat.catroid.uiespresso.util.rules.DontGenerateDefaultProjectActivityTestRule
import org.hamcrest.Matchers
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TermsOfUseDialogTest {
    @get:Rule
    var baseActivityTestRule = DontGenerateDefaultProjectActivityTestRule(
        MainMenuActivity::class.java, true, false
    )

    private var bufferedPrivacyPolicyPreferenceSetting = 0

    @Before
    @Throws(Exception::class)
    fun setUp() {
        val sharedPreferences = PreferenceManager
            .getDefaultSharedPreferences(ApplicationProvider.getApplicationContext())
        bufferedPrivacyPolicyPreferenceSetting = sharedPreferences
            .getInt(SharedPreferenceKeys.AGREED_TO_PRIVACY_POLICY_VERSION, 0)
        sharedPreferences
            .edit()
            .putInt(
                SharedPreferenceKeys.AGREED_TO_PRIVACY_POLICY_VERSION,
                Constants.CATROBAT_TERMS_OF_USE_ACCEPTED
            )
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
    fun termsOfUseDialogTest() {
        Espresso.openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getInstrumentation().targetContext)

        Espresso.onView(ViewMatchers.withText(R.string.main_menu_terms_of_use))
            .perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.alertTitle))
            .check(ViewAssertions.matches(ViewMatchers.withText(R.string.dialog_terms_of_use_title)))

        Espresso.onView(ViewMatchers.withText(R.string.dialog_terms_of_use_info))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        Espresso.onView(
            Matchers.allOf(
                ViewMatchers.withId(R.id.dialog_terms_of_use_text_view_url),
                ViewMatchers.isDisplayed()
            )
        )
            .check(ViewAssertions.matches(ViewMatchers.withText(R.string.dialog_terms_of_use_link_text)))

        Espresso.onView(ViewMatchers.withText(R.string.ok))
            .perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText(R.string.dialog_terms_of_use_title))
            .check(ViewAssertions.doesNotExist())
    }
}

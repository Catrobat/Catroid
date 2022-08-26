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

import android.content.Context
import android.preference.PreferenceManager
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withContentDescription
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.catrobat.catroid.R
import org.catrobat.catroid.common.Constants
import org.catrobat.catroid.common.Constants.CATROBAT_TERMS_OF_USE_ACCEPTED
import org.catrobat.catroid.common.SharedPreferenceKeys.AGREED_TO_PRIVACY_POLICY_VERSION
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.test.utils.TestUtils
import org.catrobat.catroid.ui.MainMenuActivity
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityTestRule
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.containsString
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NavigateUpTest {
    private var privacyPreferenceSetting: Int = 0
    private lateinit var applicationContext: Context
    private val projectName = "Project"
    private val navigateUpButtonContentDescription = "Navigate up"
    private val sceneIndex = 0
    private val spriteIndex = 1
    private lateinit var project: Project
    private lateinit var spriteName: String

    @get:Rule
    var baseActivityTestRule = BaseActivityTestRule(
        MainMenuActivity::class.java,
        false,
        false
    )

    @Before
    fun setUp() {
        applicationContext = ApplicationProvider.getApplicationContext()
        privacyPreferenceSetting = PreferenceManager
            .getDefaultSharedPreferences(applicationContext)
            .getInt(AGREED_TO_PRIVACY_POLICY_VERSION, 0)

        PreferenceManager.getDefaultSharedPreferences(applicationContext)
            .edit().putInt(
                AGREED_TO_PRIVACY_POLICY_VERSION,
                CATROBAT_TERMS_OF_USE_ACCEPTED
            ).commit()

        project = TestUtils.createProjectWithLanguageVersion(
            Constants.CURRENT_CATROBAT_LANGUAGE_VERSION,
            projectName
        )
        spriteName = project.sceneList[sceneIndex].spriteList[spriteIndex].name
        baseActivityTestRule.launchActivity(null)
    }

    @After
    fun tearDown() {
        TestUtils.deleteProjects(projectName)
        PreferenceManager.getDefaultSharedPreferences(applicationContext)
            .edit()
            .putInt(AGREED_TO_PRIVACY_POLICY_VERSION, privacyPreferenceSetting)
            .commit()
    }

    @Test
    fun testNavigateUp() {
        clickOnText(applicationContext.getString(R.string.main_menu_programs))
        clickOnText(projectName)
        clickOnText(spriteName)

        navigateUp()
        checkIsTextDisplayed(spriteName)
        navigateUp()
        checkIsTextDisplayed(projectName)
        navigateUp()
        checkIsTextDisplayed(applicationContext.getString(R.string.main_menu_programs))
    }

    private fun checkIsTextDisplayed(text: String) =
        onView(withText(text)).check(matches(isDisplayed()))

    private fun clickOnText(text: String) =
        onView(withText(text)).perform(click())

    private fun navigateUp() =
        onView(
            allOf(withContentDescription(containsString(navigateUpButtonContentDescription)))
        ).perform(click())
}

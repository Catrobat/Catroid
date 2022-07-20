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
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import android.widget.EditText
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.R
import org.catrobat.catroid.common.Constants.CATROBAT_TERMS_OF_USE_ACCEPTED
import org.catrobat.catroid.common.SharedPreferenceKeys.AGREED_TO_PRIVACY_POLICY_VERSION
import org.catrobat.catroid.ui.MainMenuActivity
import org.catrobat.catroid.uiespresso.util.actions.CustomActions
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityTestRule
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.CoreMatchers.instanceOf
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.test.KoinTest
import org.koin.test.inject

@RunWith(AndroidJUnit4::class)
class CreateProjectTest : KoinTest {
    private var privacyPreferenceSetting: Int = 0
    private lateinit var applicationContext: Context
    private val projectManager: ProjectManager by inject()

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

        createProject()
        baseActivityTestRule.launchActivity(null)
    }

    @After
    fun tearDown() {
        PreferenceManager.getDefaultSharedPreferences(applicationContext)
            .edit()
            .putInt(AGREED_TO_PRIVACY_POLICY_VERSION, privacyPreferenceSetting)
            .commit()
    }

    @Test
    fun testCheckIfProjectCreated() {
        val newProjectName = "newProjectTest"
        onView(withId(R.id.newProjectFloatingActionButton))
            .check(matches(isDisplayed()))
            .perform(ViewActions.click())
        waitFor()
        onView(allOf(withId(R.id.input_edit_text), isDisplayed(), instanceOf(EditText::class.java)))
            .perform(ViewActions.replaceText(newProjectName))
        onView(withText(R.string.ok))
            .check(matches(isDisplayed()))
            .perform(ViewActions.click())
        pressBack()
        onView(withText(R.string.main_menu_programs))
            .check(matches(isDisplayed()))
            .perform(ViewActions.click())
        onView(withText(newProjectName))
            .check(matches(isDisplayed()))
    }

    private fun createProject() {
        projectManager.createNewEmptyProject(
            javaClass.simpleName,
            false,
            false
        )
    }

    private fun waitFor(time: Int = 1000) {
        onView(ViewMatchers.isRoot()).perform(CustomActions.wait(time))
    }
}

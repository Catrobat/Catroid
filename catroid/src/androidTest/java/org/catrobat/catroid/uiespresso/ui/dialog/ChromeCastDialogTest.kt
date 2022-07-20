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

import android.app.Activity
import android.app.Instrumentation.ActivityResult
import android.content.Intent
import android.preference.PreferenceManager
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.R
import org.catrobat.catroid.WaitForConditionAction.Companion.waitFor
import org.catrobat.catroid.common.Constants
import org.catrobat.catroid.common.defaultprojectcreators.ChromeCastProjectCreator
import org.catrobat.catroid.ui.ProjectActivity
import org.catrobat.catroid.ui.WebViewActivity
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment
import org.catrobat.catroid.uiespresso.util.rules.FragmentActivityTestRule
import org.hamcrest.Matcher
import org.hamcrest.Matchers
import org.hamcrest.core.AllOf
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.java.KoinJavaComponent.inject

@RunWith(AndroidJUnit4::class)
class ChromeCastDialogTest {
    private val projectName = javaClass.simpleName
    private val projectManager: ProjectManager by inject(ProjectManager::class.java)
    private lateinit var expectedIntent: Matcher<Intent>
    private lateinit var expectedWebIntent: Matcher<Intent>
    private val timeout = 5000L

    @get:Rule
    var baseActivityTestRule: FragmentActivityTestRule<ProjectActivity> =
        FragmentActivityTestRule(
            ProjectActivity::class.java,
            ProjectActivity.EXTRA_FRAGMENT_POSITION,
            ProjectActivity.FRAGMENT_SPRITES
        )

    @Before
    @Throws(Exception::class)
    fun setUp() {
        val project = ChromeCastProjectCreator()
            .createDefaultProject(projectName, ApplicationProvider.getApplicationContext(), true)
        projectManager.currentProject = project
        projectManager.currentlyEditedScene = project.defaultScene
        baseActivityTestRule.launchActivity(null)

        Intents.init()

        expectedIntent = AllOf.allOf(
            IntentMatchers.hasComponent(Constants.POCKET_PAINT_INTENT_ACTIVITY_NAME),
            IntentMatchers.hasAction("android.intent.action.MAIN"),
            IntentMatchers.hasCategories(Matchers.hasItem(Matchers.equalTo("android.intent.category.LAUNCHER")))
        )

        expectedWebIntent = AllOf.allOf(
            IntentMatchers.hasComponent(WebViewActivity::class.java.name)
        )

        val resultData = Intent()
        val result = ActivityResult(Activity.RESULT_CANCELED, resultData)

        enableChromeCast(true)

        Intents.intending(expectedIntent).respondWith(result)
    }

    @After
    @Throws(Exception::class)
    fun tearDown() {
        Intents.release()
        baseActivityTestRule.deleteAllProjects()
        enableChromeCast(false)
    }

    @Test
    fun testShowDialogOnEmptySprite() {
        addEmptySprite()
        onView(withText(R.string.ok))
            .perform(click())
        onView(withText(R.string.cast_searching_for_cast_devices))
            .inRoot(isDialog())
            .perform(waitFor(isDisplayed(), timeout))
    }

    @Test
    fun testNotShowDialogOnCancelEmptySprite() {
        addEmptySprite()
        onView(withText(R.string.cancel))
            .perform(click())
        onView(withText(R.string.cast_searching_for_cast_devices))
            .check(doesNotExist())
    }

    @Test
    fun testNotShowDialogOnCancelledSelection() {
        onView(withId(R.id.button_add))
            .perform(click())
        onView(withId(R.id.dialog_new_look_media_library))
            .perform(click())

        Intents.intended(expectedWebIntent)

        pressBack()
        pressBack()
        Espresso.closeSoftKeyboard()

        onView(withText(R.string.cast_searching_for_cast_devices))
            .check(doesNotExist())
    }

    private fun enableChromeCast(enable: Boolean) {
        PreferenceManager.getDefaultSharedPreferences(ApplicationProvider.getApplicationContext())
            .edit()
            .putBoolean(SettingsFragment.SETTINGS_CAST_GLOBALLY_ENABLED, enable)
            .commit()
    }
    private fun addEmptySprite() {
        onView(withId(R.id.button_add))
            .perform(click())
        onView(withId(R.id.dialog_new_look_paintroid))
            .perform(click())
        Intents.intended(expectedIntent)
        Espresso.closeSoftKeyboard()
    }
}

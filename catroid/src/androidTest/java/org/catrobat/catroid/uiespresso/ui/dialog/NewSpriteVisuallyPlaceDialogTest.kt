/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2021 The Catrobat Team
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
import android.util.Log
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isNotChecked
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.R
import org.catrobat.catroid.WaitForConditionAction.Companion.waitFor
import org.catrobat.catroid.common.Constants
import org.catrobat.catroid.common.FlavoredConstants
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.io.StorageOperations
import org.catrobat.catroid.io.XstreamSerializer
import org.catrobat.catroid.testsuites.annotations.Cat.AppUi
import org.catrobat.catroid.testsuites.annotations.Level.Smoke
import org.catrobat.catroid.ui.ProjectActivity
import org.catrobat.catroid.uiespresso.content.brick.utils.BrickDataInteractionWrapper.onBrickAtPosition
import org.catrobat.catroid.uiespresso.ui.fragment.rvutils.RecyclerViewInteractionWrapper.onRecyclerView
import org.catrobat.catroid.uiespresso.util.UiTestUtils
import org.catrobat.catroid.uiespresso.util.rules.FragmentActivityTestRule
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.Matcher
import org.hamcrest.Matchers
import org.hamcrest.core.AllOf
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.runner.RunWith
import java.io.File
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class NewSpriteVisuallyPlaceDialogTest {
    @get:Rule
    var baseActivityTestRule = FragmentActivityTestRule(
        ProjectActivity::class.java, ProjectActivity.EXTRA_FRAGMENT_POSITION,
        ProjectActivity.FRAGMENT_SPRITES
    )
    private val ProjectName = "newProject"
    private lateinit var expectedIntent: Matcher<Intent>

    @Before
    @Throws(Exception::class)
    fun setUp() {
        createProject(ProjectName)
        baseActivityTestRule.launchActivity()
        Intents.init()

        expectedIntent = AllOf.allOf(
            IntentMatchers.hasComponent(Constants.POCKET_PAINT_INTENT_ACTIVITY_NAME),
            IntentMatchers.hasAction("android.intent.action.MAIN"),
            IntentMatchers.hasCategories(Matchers.hasItem(Matchers.equalTo("android.intent.category.LAUNCHER")))
        )
        val result = ActivityResult(Activity.RESULT_OK, null)
        Intents.intending(expectedIntent).respondWith(result)
    }

    @After
    fun tearDown() {
        Intents.release()
        baseActivityTestRule.finishActivity()
        try {
            StorageOperations.deleteDir(File(FlavoredConstants.DEFAULT_ROOT_DIRECTORY, ProjectName))
        } catch (e: IOException) {
            Log.e(javaClass.simpleName, Log.getStackTraceString(e))
        }
    }

    @Category(AppUi::class, Smoke::class)
    @Test
    fun newSpriteVisuallyPlaced() {
        val newSpriteName =
            UiTestUtils.getResourcesString(R.string.default_sprite_name).toString() + " (1)"
        onView(withId(R.id.button_add))
            .perform(click())
        onView(withId(R.id.dialog_new_look_paintroid))
            .perform(click())
        intended(expectedIntent)
        onView(withId(R.id.place_visually_sprite_switch))
            .perform(waitFor(isDisplayed(), 1000))
        onView(withId(R.id.place_visually_sprite_switch))
            .check(matches(isNotChecked())).perform(scrollTo(), click())

        onView(withText(R.string.place_visually_text))
            .check(matches(isDisplayed()))

        onView(allOf(withId(android.R.id.button1), withText(R.string.ok)))
            .perform(click())
        onView(withId(R.id.confirm)).perform(click())
        onRecyclerView().performOnItemWithText(newSpriteName, click())

        onBrickAtPosition(0).checkShowsText("When scene starts")
        onBrickAtPosition(1).checkShowsText("Place at")
        onBrickAtPosition(1).onChildView(withId(R.id.brick_place_at_edit_text_x))
            .check(matches(withText("100 ")))
        onBrickAtPosition(1).onFormulaTextField(R.id.brick_place_at_edit_text_y)
            .check(matches(withText("200 ")))
    }

    private fun createProject(projectName: String) {
        val project = Project(ApplicationProvider.getApplicationContext(), projectName)
        ProjectManager.getInstance().currentProject = project
        ProjectManager.getInstance().currentlyEditedScene = project.defaultScene
        XstreamSerializer.getInstance().saveProject(project)
    }
}

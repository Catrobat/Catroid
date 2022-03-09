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
import android.preference.PreferenceManager.getDefaultSharedPreferences
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.closeSoftKeyboard
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers.isChecked
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isNotChecked
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.R
import org.catrobat.catroid.WaitForConditionAction.Companion.waitFor
import org.catrobat.catroid.common.Constants
import org.catrobat.catroid.common.SharedPreferenceKeys.NEW_SPRITE_VISUAL_PLACEMENT_KEY
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.io.XstreamSerializer
import org.catrobat.catroid.test.utils.TestUtils
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
import org.koin.java.KoinJavaComponent.inject

@RunWith(AndroidJUnit4::class)
class NewSpriteVisuallyPlaceDialogTest {
    @get:Rule
    var baseActivityTestRule = FragmentActivityTestRule(
        ProjectActivity::class.java, ProjectActivity.EXTRA_FRAGMENT_POSITION,
        ProjectActivity.FRAGMENT_SPRITES
    )
    private val projectName = "newProject"
    private lateinit var expectedIntent: Matcher<Intent>
    val newSpriteName = UiTestUtils.getResourcesString(R.string.default_sprite_name).toString()

    @Before
    @Throws(Exception::class)
    fun setUp() {
        createProject(projectName)
        getDefaultSharedPreferences(ApplicationProvider.getApplicationContext())?.edit()
            ?.putBoolean(NEW_SPRITE_VISUAL_PLACEMENT_KEY, true)
            ?.apply()
        baseActivityTestRule.launchActivity()
        Intents.init()

        expectedIntent = AllOf.allOf(
            IntentMatchers.hasComponent(Constants.POCKET_PAINT_INTENT_ACTIVITY_NAME),
            IntentMatchers.hasAction("android.intent.action.MAIN"),
            IntentMatchers.hasCategories(Matchers.hasItem(Matchers.equalTo("android.intent.category.LAUNCHER")))
        )

        val result = ActivityResult(Activity.RESULT_OK, null)
        Intents.intending(expectedIntent).respondWith(result)

        baseActivityTestRule.launchActivity(null)

        onView(withId(R.id.button_add))
            .perform(click())
        onView(withId(R.id.dialog_new_look_paintroid))
            .perform(click())
        intended(expectedIntent)
        closeSoftKeyboard()
        onView(withId(R.id.place_visually_sprite_switch))
            .perform(waitFor(isDisplayed(), 1000))
        onView(withId(R.id.place_visually_sprite_switch))
            .check(matches(isChecked()))
    }

    @After
    fun tearDown() {
        Intents.release()
        getDefaultSharedPreferences(ApplicationProvider.getApplicationContext())?.edit()
            ?.remove(NEW_SPRITE_VISUAL_PLACEMENT_KEY)
            ?.apply()
        TestUtils.deleteProjects(projectName)
    }

    @Category(AppUi::class, Smoke::class)
    @Test
    fun newSpriteVisuallyPlaced() {
        onView(withText(R.string.place_visually_text))
            .check(matches(isDisplayed()))

        onView(allOf(withId(android.R.id.button1), withText(R.string.ok)))
            .perform(click())
        onView(withId(R.id.confirm)).perform(click())
        onRecyclerView().performOnItemWithText(newSpriteName, click())

        onBrickAtPosition(0).checkShowsText(R.string.brick_when_started)
        onBrickAtPosition(1).checkShowsText(R.string.brick_place_at)
        onBrickAtPosition(1).onChildView(withId(R.id.brick_place_at_edit_text_x))
            .check(matches(withText("100 ")))
        onBrickAtPosition(1).onFormulaTextField(R.id.brick_place_at_edit_text_y)
            .check(matches(withText("200 ")))
    }

    @Category(AppUi::class, Smoke::class)
    @Test
    fun newSpriteCancelVisuallyPlacing() {
        onView(allOf(withId(android.R.id.button1), withText(R.string.ok)))
            .perform(click())
        pressBack()
        onRecyclerView().performOnItemWithText(newSpriteName, click())
        onView(withText(R.string.fragment_script_text_description))
            .check(matches(isDisplayed()))
    }

    @Category(AppUi::class, Smoke::class)
    @Test
    fun newSpriteCheckVisualPlacementSettingRetained() {
        onView(withId(R.id.place_visually_sprite_switch))
            .perform(click())
        onView(allOf(withId(android.R.id.button1), withText(R.string.ok)))
            .perform(click())

        onView(withId(R.id.button_add))
            .perform(click())
        onView(withId(R.id.dialog_new_look_paintroid))
            .perform(click())
        intended(expectedIntent, Intents.times(2))
        closeSoftKeyboard()
        onView(withId(R.id.place_visually_sprite_switch))
            .perform(waitFor(isDisplayed(), 1000))
        onView(withId(R.id.place_visually_sprite_switch))
            .check(matches(isNotChecked()))
    }

    private fun createProject(projectName: String) {
        val project = Project(ApplicationProvider.getApplicationContext(), projectName)
        val projectManager: ProjectManager by inject(ProjectManager::class.java)
        projectManager.currentProject = project
        projectManager.currentlyEditedScene = project.defaultScene
        XstreamSerializer.getInstance().saveProject(project)
    }
}

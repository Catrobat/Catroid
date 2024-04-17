/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2024 The Catrobat Team
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

import android.widget.EditText
import androidx.test.espresso.Espresso.closeSoftKeyboard
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.catrobat.catroid.R
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Scene
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.test.utils.TestUtils.clearBackPack
import org.catrobat.catroid.ui.ProjectActivity
import org.catrobat.catroid.ui.controller.BackpackListManager
import org.catrobat.catroid.uiespresso.ui.fragment.rvutils.RecyclerViewInteractionWrapper.onRecyclerView
import org.catrobat.catroid.uiespresso.util.UiTestUtils
import org.catrobat.catroid.uiespresso.util.UiTestUtils.Companion.getResourcesString
import org.catrobat.catroid.uiespresso.util.UiTestUtils.Companion.openActionBarMenu
import org.catrobat.catroid.uiespresso.util.rules.FragmentActivityTestRule
import org.hamcrest.Matchers
import org.hamcrest.Matchers.allOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SceneListFragmentTest {
    private val projectName = javaClass.simpleName
    private val secondSceneName = "secondScene"
    private val defaultSceneCopyName = "Scene (1)"

    private lateinit var project: Project

    @get:Rule
    var baseActivityTestRule = FragmentActivityTestRule(
        ProjectActivity::class.java,
        ProjectActivity.EXTRA_FRAGMENT_POSITION,
        ProjectActivity.FRAGMENT_SCENES
    )

    @Before
    @Throws(Exception::class)
    fun setUp() {
        val backpackListManager = BackpackListManager.getInstance()
        clearBackPack(backpackListManager)
        createProject()
        baseActivityTestRule.launchActivity()
    }

    @Test
    fun testCheckOverFlowMenu() {
        openActionBarMenu()
        onView(withText(R.string.backpack)).check(matches(isDisplayed()))
        onView(withText(R.string.copy)).check(matches(isDisplayed()))
        onView(withText(R.string.delete)).check(matches(isDisplayed()))
        onView(withText(R.string.rename)).check(matches(isDisplayed()))
        onView(withText(R.string.show_details)).check(matches(isDisplayed()))
        onView(withText(R.string.project_options)).check(matches(isDisplayed()))
    }

    @Test
    fun testCheckAddButton() {
        onView(withId(R.id.button_add)).perform(click())

        onView(withId(R.id.dialog_new_blanc_scene)).check(matches(isDisplayed()))
        onView(withId(R.id.dialog_new_scene_backpack)).check(matches(isDisplayed()))
        onView(withId(R.id.dialog_new_scene_local_projects)).check(matches(isDisplayed()))
    }

    @Test
    fun testAddNewBlancScene() {
        onView(withId(R.id.button_add)).perform(click())

        onView(withId(R.id.dialog_new_blanc_scene)).perform(click())

        onView(withText(getResourcesString(R.string.new_scene))).inRoot(isDialog())
            .check(matches(isDisplayed()))

        onView(
            allOf(
                withText(defaultSceneCopyName),
                isDisplayed(),
                Matchers.instanceOf(EditText::class.java)
            )
        ).perform(ViewActions.replaceText("newBlancScene"))

        closeSoftKeyboard()

        onView(
            allOf(
                withId(android.R.id.button2),
                withText(getResourcesString(R.string.cancel))
            )
        ).check(matches(isDisplayed()))

        onView(
            allOf(
                withId(android.R.id.button1),
                withText(getResourcesString(R.string.ok))
            )
        ).perform(click())

        onView(withText("newBlancScene")).check(matches(isDisplayed()))
    }

    @Test
    fun testAddSceneFromBackpack() {
        openActionBarMenu()
        onView(withText(R.string.backpack)).perform(click())

        onRecyclerView().atPosition(0).performCheckItemClick()

        onView(withId(R.id.confirm)).perform(click())

        pressBack()

        onView(withId(R.id.button_add)).perform(click())

        onView(withId(R.id.dialog_new_scene_backpack)).perform(click())

        onView(withText(R.string.default_scene_name)).check(matches(isDisplayed())).perform(click())

        onView(withText(R.string.unpack)).check(matches(isDisplayed())).perform(click())

        onView(withText(R.string.default_scene_name)).check(matches(isDisplayed()))
        onView(withText(secondSceneName)).check(matches(isDisplayed()))
        onView(withText(defaultSceneCopyName)).check(matches(isDisplayed()))
    }

    @Test
    fun testAddSceneFromLocalImport() {
        onView(withId(R.id.button_add)).perform(click())

        onView(withId(R.id.dialog_new_scene_local_projects)).perform(click())

        onView(withText(projectName)).check(matches(isDisplayed())).perform(click())

        onView(withText(R.string.default_scene_name)).check(matches(isDisplayed()))
        onView(withText(secondSceneName)).check(matches(isDisplayed()))

        onView(withText(R.string.default_scene_name)).perform(click())

        onView(withText(R.string.default_scene_name)).check(matches(isDisplayed()))
        onView(withText(secondSceneName)).check(matches(isDisplayed()))
        onView(withText(defaultSceneCopyName)).check(matches(isDisplayed()))
    }

    private fun createProject() {
        project = UiTestUtils.createDefaultTestProject(projectName)
        val secondScene = Scene(secondSceneName, project)
        val sprite = Sprite("Test")
        secondScene.addSprite(sprite)
        project.addScene(secondScene)
    }
}

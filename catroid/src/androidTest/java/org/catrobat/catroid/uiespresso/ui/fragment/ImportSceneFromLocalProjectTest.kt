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

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.catrobat.catroid.R
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Scene
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.formulaeditor.UserList
import org.catrobat.catroid.formulaeditor.UserVariable
import org.catrobat.catroid.io.XstreamSerializer
import org.catrobat.catroid.test.utils.TestUtils
import org.catrobat.catroid.ui.ProjectActivity
import org.catrobat.catroid.ui.controller.BackpackListManager
import org.catrobat.catroid.uiespresso.util.UiTestUtils
import org.catrobat.catroid.uiespresso.util.rules.FragmentActivityTestRule
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ImportSceneFromLocalProjectTest {
    private val projectName = javaClass.simpleName
    private val secondSceneName = "secondScene"
    private val defaultSceneCopyName = "Scene (1)"
    private val importProjectName = "importProject"

    private lateinit var project: Project
    private lateinit var importProject: Project

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
        TestUtils.clearBackPack(backpackListManager)
        createProject()
        baseActivityTestRule.launchActivity()
    }

    @Test
    fun testImportSceneFromProjectWithOneScene() {
        onView(withId(R.id.button_add)).perform(click())

        onView(withId(R.id.dialog_new_scene_local_projects)).perform(click())

        onView(withText(projectName)).check(matches(isDisplayed()))
        onView(withText(importProjectName)).check(matches(isDisplayed()))
            .perform(click())

        onView(withText(R.string.default_scene_name)).check(matches(isDisplayed()))
        onView(withText(secondSceneName)).check(matches(isDisplayed()))
        onView(withText(defaultSceneCopyName)).check(matches(isDisplayed()))
    }

    @Test
    fun testImportSceneFromProjectWithMultipleScenes() {
        onView(withId(R.id.button_add)).perform(click())

        onView(withId(R.id.dialog_new_scene_local_projects)).perform(click())

        onView(withText(importProjectName)).check(matches(isDisplayed()))
        onView(withText(projectName)).check(matches(isDisplayed()))
            .perform(click())

        onView(withText(R.string.default_scene_name)).check(matches(isDisplayed()))
        onView(withText(secondSceneName)).check(matches(isDisplayed())).perform(click())

        onView(withText(R.string.default_scene_name)).check(matches(isDisplayed()))
        onView(withText(secondSceneName)).check(matches(isDisplayed()))
        onView(withText("$secondSceneName (1)")).check(matches(isDisplayed()))
    }

    @Test
    fun testImportSceneFromProjectWithVariableConflict() {
        val globalVariableName = "globalVar"
        val globalVariable = UserVariable(globalVariableName)
        project.userVariables.add(globalVariable)
        XstreamSerializer.getInstance().saveProject(project)
        importProject.userVariables.add(globalVariable)
        XstreamSerializer.getInstance().saveProject(importProject)

        onView(withId(R.id.button_add)).perform(click())

        onView(withId(R.id.dialog_new_scene_local_projects)).perform(click())

        onView(withText(projectName)).check(matches(isDisplayed()))
        onView(withText(importProjectName)).check(matches(isDisplayed()))
            .perform(click())

        onView(withId(R.id.import_conflicting_variables)).check(matches(isDisplayed()))
        onView(withId(R.id.conflicting_variables)).check(matches(isDisplayed()))
        onView(withText(globalVariableName)).check(matches(isDisplayed()))
        onView(withText(R.string.ok)).inRoot(RootMatchers.isDialog()).check(matches(isDisplayed()))
            .perform(click())

        onView(withText(R.string.default_scene_name)).check(matches(isDisplayed()))
        onView(withText(secondSceneName)).check(matches(isDisplayed()))
    }

    @Test
    fun testImportSceneFromProjectWithListConflict() {
        val globalListName = "globalList"
        val globalList = UserList(globalListName)
        project.userLists.add(globalList)
        XstreamSerializer.getInstance().saveProject(project)
        importProject.userLists.add(globalList)
        XstreamSerializer.getInstance().saveProject(importProject)

        onView(withId(R.id.button_add)).perform(click())

        onView(withId(R.id.dialog_new_scene_local_projects)).perform(click())

        onView(withText(projectName)).check(matches(isDisplayed()))
        onView(withText(importProjectName)).check(matches(isDisplayed()))
            .perform(click())

        onView(withId(R.id.import_conflicting_variables)).check(matches(isDisplayed()))
        onView(withId(R.id.conflicting_variables)).check(matches(isDisplayed()))
        onView(withText(globalListName)).check(matches(isDisplayed()))
        onView(withText(R.string.ok)).inRoot(RootMatchers.isDialog()).check(matches(isDisplayed()))
            .perform(click())

        onView(withText(R.string.default_scene_name)).check(matches(isDisplayed()))
        onView(withText(secondSceneName)).check(matches(isDisplayed()))
    }

    private fun createProject() {
        importProject = UiTestUtils.createDefaultTestProject(importProjectName)
        XstreamSerializer.getInstance().saveProject(importProject)

        project = UiTestUtils.createDefaultTestProject(projectName)
        val secondScene = Scene(secondSceneName, project)
        val sprite = Sprite("Test")
        secondScene.addSprite(sprite)
        project.addScene(secondScene)
        XstreamSerializer.getInstance().saveProject(project)
    }
}

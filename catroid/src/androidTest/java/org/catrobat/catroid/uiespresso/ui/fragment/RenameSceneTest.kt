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
package org.catrobat.catroid.uiespresso.ui.fragment

import android.content.Context
import android.widget.EditText
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.closeSoftKeyboard
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.R
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Scene
import org.catrobat.catroid.io.asynctask.saveProjectSerial
import org.catrobat.catroid.test.utils.TestUtils
import org.catrobat.catroid.testsuites.annotations.Cat.AppUi
import org.catrobat.catroid.testsuites.annotations.Level.Smoke
import org.catrobat.catroid.ui.ProjectActivity
import org.catrobat.catroid.uiespresso.ui.fragment.rvutils.RecyclerViewInteractionWrapper.onRecyclerView
import org.catrobat.catroid.uiespresso.util.UiTestUtils.openActionBar
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityTestRule
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.instanceOf
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RenameSceneTest {
    private lateinit var applicationContext: Context
    private lateinit var project: Project
    private val projectName = "RenameSceneTest"
    private val newSceneName = "newSceneName"
    private val otherSceneName = "otherScene"

    @get:Rule
    var baseActivityTestRule = BaseActivityTestRule(
        ProjectActivity::class.java, false, false
    )

    @Before
    fun setUp() {
        applicationContext = ApplicationProvider.getApplicationContext()
        createProject()
        baseActivityTestRule.launchActivity(null)
    }

    @Category(AppUi::class, Smoke::class)
    @Test
    fun testRenameScene() {
        val renameSceneString = applicationContext.getString(R.string.rename)
        val cancelString = applicationContext.getString(R.string.cancel)
        val okString = applicationContext.getString(R.string.ok)
        val renameSceneDialogString = applicationContext.getString(R.string.rename_scene_dialog)
        val oldSceneName = applicationContext.getString(R.string.default_scene_name)

        openActionBar()
        onView(withText(renameSceneString))
            .perform(click())
        onRecyclerView().atPosition(0)
            .perform(click())
        onView(withText(renameSceneDialogString))
            .inRoot(isDialog())
            .check(matches(isDisplayed()))
        onView(allOf(withText(oldSceneName), isDisplayed(), instanceOf(EditText::class.java)))
            .perform(replaceText(newSceneName))
        closeSoftKeyboard()
        onView(allOf(withId(android.R.id.button2), withText(cancelString)))
            .check(matches(isDisplayed()))
        onView(allOf(withId(android.R.id.button1), withText(okString)))
            .perform(click())
        onView(withText(newSceneName))
            .check(matches(isDisplayed()))
        assertEquals(newSceneName, project.defaultScene.name)
    }

    @After
    fun tearDown() {
        TestUtils.deleteProjects(projectName)
    }

    private fun createProject() {
        project = Project(applicationContext, projectName)
        val otherScene = Scene(otherSceneName, project)
        project.addScene(otherScene)
        ProjectManager.getInstance().currentProject = project
        saveProjectSerial(project, applicationContext)
    }
}

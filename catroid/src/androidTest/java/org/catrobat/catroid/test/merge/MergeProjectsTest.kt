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

package org.catrobat.catroid.test.merge

import androidx.test.InstrumentationRegistry
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.R
import org.catrobat.catroid.common.DefaultProjectHandler
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.formulaeditor.UserVariable
import org.catrobat.catroid.io.XstreamSerializer
import org.catrobat.catroid.test.utils.TestUtils
import org.catrobat.catroid.ui.ProjectListActivity
import org.catrobat.catroid.uiespresso.ui.fragment.rvutils.RecyclerViewInteractionWrapper
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityTestRule
import org.hamcrest.core.AllOf.allOf
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.java.KoinJavaComponent.inject

class MergeProjectsTest {
    private lateinit var project: Project
    private lateinit var testProject: Project
    private val projectManager = inject(ProjectManager::class.java).value

    @get:Rule
    var baseActivityTestRule = BaseActivityTestRule(
        ProjectListActivity::class.java, true, false
    )

    @After
    fun tearDown() {
        baseActivityTestRule.finishActivity()
        TestUtils.deleteProjects(project.name, testProject.name)
    }

    @Before
    fun setUp() {
        project = DefaultProjectHandler.createAndSaveDefaultProject(
            "base",
            ApplicationProvider.getApplicationContext(),
            false
        )
        project.userVariables.add(UserVariable("test"))
        XstreamSerializer.getInstance().saveProject(project)
    }

    @Test
    fun testMergeDefaultProjects() {
        testProject = DefaultProjectHandler.createAndSaveDefaultProject(
            "local",
            ApplicationProvider.getApplicationContext(),
            false
        )
        XstreamSerializer.getInstance().saveProject(testProject)
        baseActivityTestRule.launchActivity(null)
        Espresso.openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getInstrumentation().targetContext)
        Espresso.onView(withText(R.string.merge)).perform(ViewActions.click())

        RecyclerViewInteractionWrapper.onRecyclerView().atPosition(0)
            .performCheckItem()

        RecyclerViewInteractionWrapper.onRecyclerView().atPosition(1)
            .performCheckItem()

        Espresso.onView(withId(R.id.confirm)).perform(ViewActions.click())
        Espresso.onView(withText(R.string.new_merge_project_dialog_title))
            .inRoot(RootMatchers.isDialog())
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        Espresso.onView(allOf(withId(android.R.id.button2), withText(R.string.cancel)))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        Espresso.onView(allOf(withId(android.R.id.button1), withText(R.string.ok)))
            .perform(ViewActions.click())
        MergeTestUtils().assertSuccessfulProjectMerge(
            project,
            testProject,
            projectManager.currentProject
        )
    }

    @Test
    fun testAbortMergeWithLandscapeConflict() {
        testProject = DefaultProjectHandler.createAndSaveDefaultProject(
            "horizontal",
            ApplicationProvider.getApplicationContext(),
            true
        )
        XstreamSerializer.getInstance().saveProject(testProject)
        baseActivityTestRule.launchActivity(null)
        Espresso.openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getInstrumentation().targetContext)
        Espresso.onView(withText(R.string.merge)).perform(ViewActions.click())

        RecyclerViewInteractionWrapper.onRecyclerView().atPosition(0)
            .performCheckItem()

        RecyclerViewInteractionWrapper.onRecyclerView().atPosition(1)
            .performCheckItem()
        Espresso.onView(withId(R.id.confirm)).perform(ViewActions.click())
        Espresso.onView(withText(R.string.import_conflicting_screen_orientation_try_again))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(withText(R.string.import_conflicting_screen_orientation_reason))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(withText(R.string.ok)).inRoot(RootMatchers.isDialog()).check(
            ViewAssertions.matches(ViewMatchers.isDisplayed())
        )
            .perform(ViewActions.click())
    }

    @Test
    fun testAbortMergeWithVariableConflicts() {
        testProject = DefaultProjectHandler.createAndSaveDefaultProject(
            "variableConflict",
            ApplicationProvider.getApplicationContext(),
            false
        )
        testProject.defaultScene.spriteList[1].addUserVariable(UserVariable("test"))
        XstreamSerializer.getInstance().saveProject(testProject)
        Espresso.openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getInstrumentation().targetContext)
        Espresso.onView(withText(R.string.merge)).perform(ViewActions.click())

        RecyclerViewInteractionWrapper.onRecyclerView().atPosition(0)
            .performCheckItem()

        RecyclerViewInteractionWrapper.onRecyclerView().atPosition(1)
            .performCheckItem()

        Espresso.onView(withId(R.id.confirm)).perform(ViewActions.click())
        Espresso.onView(withId(R.id.import_conflicting_variables)).check(
            ViewAssertions.matches(
                ViewMatchers.isDisplayed()
            )
        )
        Espresso.onView(withText(R.string.import_conflicting_variables_try_again))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(withId(R.id.conflicting_variables)).check(
            ViewAssertions.matches(
                ViewMatchers.isDisplayed()
            )
        )
        Espresso.onView(withText(R.string.import_conflicting_variables_reason))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(withText(R.string.ok)).inRoot(RootMatchers.isDialog()).check(
            ViewAssertions.matches(ViewMatchers.isDisplayed())
        )
            .perform(ViewActions.click())
    }
}
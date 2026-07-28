/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2026 The Catrobat Team
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

import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.catrobat.catroid.R
import org.catrobat.catroid.common.BrickValues
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Script
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.StartScript
import org.catrobat.catroid.content.bricks.SetXBrick
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.io.XstreamSerializer
import org.catrobat.catroid.testsuites.annotations.Cat.AppUi
import org.catrobat.catroid.testsuites.annotations.Level.Smoke
import org.catrobat.catroid.ui.ProjectListActivity
import org.catrobat.catroid.uiespresso.ui.fragment.rvutils.RecyclerViewInteractionWrapper
import org.catrobat.catroid.uiespresso.util.UiTestUtils.Companion.resources
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityTestRule
import org.hamcrest.Matchers
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DeleteProjectTest {

    @get:Rule
    var baseActivityTestRule: BaseActivityTestRule<ProjectListActivity?> =
        BaseActivityTestRule(ProjectListActivity::class.java, true, false)

    private val projectToDelete = "firstProject"

    @Before
    @Throws(Exception::class)
    fun setUp() {
        createProject(projectToDelete)
    }

    @Category(AppUi::class, Smoke::class)
    @Test
    fun deleteProjectMultipleElementsListTest() {
        createProject("secondProject")
        baseActivityTestRule.launchActivity(null)

        Espresso.openActionBarOverflowOrOptionsMenu(
            InstrumentationRegistry.getInstrumentation().targetContext
        )
        Espresso.onView(ViewMatchers.withText(R.string.delete)).perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withText(projectToDelete)).perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withId(R.id.confirm)).perform(ViewActions.click())

        Espresso.onView(
            ViewMatchers.withText(
                resources.getQuantityString(
                    R.plurals.delete_projects,
                    1
                )
            )
        )
            .inRoot(RootMatchers.isDialog())
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        Espresso.onView(
            Matchers.allOf(
                ViewMatchers.withId(android.R.id.button2), ViewMatchers.withText(
                    R.string.cancel
                )
            )
        ).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        Espresso.onView(
            Matchers.allOf(
                ViewMatchers.withId(android.R.id.button1), ViewMatchers.withText(
                    R.string.delete
                )
            )
        ).perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withText(projectToDelete)).check(ViewAssertions.doesNotExist())
    }

    @Category(AppUi::class, Smoke::class)
    @Test
    fun deleteProjectSingleElementListTest() {
        baseActivityTestRule.launchActivity(null)

        Espresso.openActionBarOverflowOrOptionsMenu(
            InstrumentationRegistry.getInstrumentation().targetContext
        )
        Espresso.onView(ViewMatchers.withText(R.string.delete)).perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withText(projectToDelete)).check(ViewAssertions.doesNotExist())
    }

    @Category(AppUi::class, Smoke::class)
    @Test
    fun selectFragmentToDeleteTest() {
        createProject("secondProject")
        baseActivityTestRule.launchActivity(null)

        Espresso.openActionBarOverflowOrOptionsMenu(
            InstrumentationRegistry.getInstrumentation().targetContext
        )
        Espresso.onView(ViewMatchers.withText(R.string.delete)).perform(ViewActions.click())

        RecyclerViewInteractionWrapper.onRecyclerView().atPosition(0).perform(ViewActions.click())
        RecyclerViewInteractionWrapper.onRecyclerView().atPosition(0).performCheckItemCheck()
    }

    @Category(AppUi::class, Smoke::class)
    @Test
    fun cancelDeleteProjectTest() {
        createProject("secondProject")
        baseActivityTestRule.launchActivity(null)

        Espresso.openActionBarOverflowOrOptionsMenu(
            InstrumentationRegistry.getInstrumentation().targetContext
        )
        Espresso.onView(ViewMatchers.withText(R.string.delete)).perform(ViewActions.click())

        RecyclerViewInteractionWrapper.onRecyclerView().atPosition(1).performCheckItemClick()

        Espresso.onView(ViewMatchers.withId(R.id.confirm)).perform(ViewActions.click())

        Espresso.onView(
            ViewMatchers.withText(
                resources.getQuantityString(
                    R.plurals.delete_projects,
                    1
                )
            )
        )
            .inRoot(RootMatchers.isDialog())
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        Espresso.onView(
            Matchers.allOf(
                ViewMatchers.withId(android.R.id.button1), ViewMatchers.withText(
                    R.string.delete
                )
            )
        ).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        Espresso.onView(
            Matchers.allOf(
                ViewMatchers.withId(android.R.id.button2), ViewMatchers.withText(
                    R.string.cancel
                )
            )
        ).perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withText(projectToDelete))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Category(AppUi::class, Smoke::class)
    @Test
    fun deleteProjectShowUndoButtonTest() {
        baseActivityTestRule.launchActivity(null)

        Espresso.openActionBarOverflowOrOptionsMenu(
            InstrumentationRegistry.getInstrumentation().targetContext
        )
        Espresso.onView(ViewMatchers.withText(R.string.delete)).perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withText(projectToDelete)).check(ViewAssertions.doesNotExist())
        Espresso.onView(ViewMatchers.withId(R.id.menu_undo))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Category(AppUi::class, Smoke::class)
    @Test
    @Throws(InterruptedException::class)
    fun undoDeletedProjectTest() {
        baseActivityTestRule.launchActivity(null)

        Espresso.openActionBarOverflowOrOptionsMenu(
            InstrumentationRegistry.getInstrumentation().targetContext
        )
        Espresso.onView(ViewMatchers.withText(R.string.delete)).perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withText(projectToDelete)).check(ViewAssertions.doesNotExist())
        Espresso.onView(ViewMatchers.withId(R.id.menu_undo))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        Espresso.onView(ViewMatchers.withId(R.id.menu_undo)).perform(ViewActions.click())
        Thread.sleep(1000)

        Espresso.onView(ViewMatchers.withText(projectToDelete))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.menu_undo)).check(ViewAssertions.doesNotExist())
    }

    @Category(AppUi::class, Smoke::class)
    @Test
    @Throws(InterruptedException::class)
    fun undoOptionNotVisibleAfterActivityChangeTest() {
        val secondProject = "secondProject"
        createProject(secondProject)
        baseActivityTestRule.launchActivity(null)

        Espresso.openActionBarOverflowOrOptionsMenu(
            InstrumentationRegistry.getInstrumentation().targetContext
        )
        Espresso.onView(ViewMatchers.withText(R.string.delete)).perform(ViewActions.click())

        RecyclerViewInteractionWrapper.onRecyclerView().atPosition(1).performCheckItemClick()

        Espresso.onView(ViewMatchers.withId(R.id.confirm)).perform(ViewActions.click())
        Espresso.onView(
            Matchers.allOf(
                ViewMatchers.withId(android.R.id.button1), ViewMatchers.withText(
                    R.string.delete
                )
            )
        ).perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withText(secondProject)).perform(ViewActions.click())
        Thread.sleep(1000)
        Espresso.pressBack()

        Espresso.onView(ViewMatchers.withId(R.id.menu_undo)).check(ViewAssertions.doesNotExist())
        Espresso.onView(ViewMatchers.withText(projectToDelete)).check(ViewAssertions.doesNotExist())
    }

    private fun createProject(projectName: String?) {
        val project = Project(ApplicationProvider.getApplicationContext(), projectName)
        val sprite = Sprite("firstSprite")

        val script: Script = StartScript()
        script.addBrick(SetXBrick(Formula(BrickValues.X_POSITION)))
        script.addBrick(SetXBrick(Formula(BrickValues.X_POSITION)))
        sprite.addScript(script)

        project.defaultScene.addSprite(sprite)

        XstreamSerializer.getInstance().saveProject(project)
    }
}

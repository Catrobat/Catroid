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

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isEnabled
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.R
import org.catrobat.catroid.WaitForConditionAction
import org.catrobat.catroid.content.bricks.IfLogicBeginBrick
import org.catrobat.catroid.content.bricks.SetXBrick
import org.catrobat.catroid.io.XstreamSerializer
import org.catrobat.catroid.test.utils.TestUtils
import org.catrobat.catroid.ui.ProjectActivity
import org.catrobat.catroid.uiespresso.content.brick.utils.BrickDataInteractionWrapper.onBrickAtPosition
import org.catrobat.catroid.uiespresso.util.UiTestUtils
import org.catrobat.catroid.uiespresso.util.rules.FragmentActivityTestRule
import org.hamcrest.Matchers.not
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.util.Arrays

@RunWith(Parameterized::class)
class UndoTest {

    private val waitThreshold = 5000L

    @get:Rule
    val baseActivityTestRule = FragmentActivityTestRule(
        ProjectActivity::class.java,
        ProjectActivity.EXTRA_FRAGMENT_POSITION,
        ProjectActivity.FRAGMENT_SPRITES
    )

    @Parameterized.Parameter
    @JvmField
    var name: String = ""

    @Parameterized.Parameter(1)
    @JvmField
    var brickPosition: Int = 0

    @Parameterized.Parameter(2)
    @JvmField
    var brickText: Int = 0

    private lateinit var initialProject: String

    @After
    fun tearDown() {
        TestUtils.deleteProjects(UndoTest::class.java.simpleName)
    }

    @Before
    fun setUp() {
        createProject()
        baseActivityTestRule.launchActivity()
        onView(withText("testSprite"))
            .perform(click())
    }

    @Test
    fun testUndoSpinnerActionVisible() {
        onBrickAtPosition(brickPosition)
            .performDeleteBrick()

        onView(withId(R.id.menu_undo))
            .perform(WaitForConditionAction.waitFor(isDisplayed(), waitThreshold))
    }

    @Test
    fun testUndo() {
        onBrickAtPosition(brickPosition).performDeleteBrick()

        onView(withId(R.id.menu_undo))
            .perform(click())

        onView(withId(R.id.menu_redo))
            .perform(WaitForConditionAction.waitFor(isEnabled(), waitThreshold))

        val projectAfterUndo = getProjectAsXmlString()
        assertEquals(initialProject, projectAfterUndo)
    }

    @Test
    fun checkScriptAfterUndo() {
        onBrickAtPosition(brickPosition).performDeleteBrick()

        onView(withId(R.id.menu_undo))
            .perform(click())

        onView(withId(R.id.menu_redo))
            .perform(WaitForConditionAction.waitFor(isEnabled(), waitThreshold))

        pressBack()

        onView(withText("testSprite"))
            .perform(click())

        onBrickAtPosition(brickPosition).checkShowsText(brickText)
    }

    fun getProjectAsXmlString(): String {
        return XstreamSerializer.getInstance()
            .getXmlAsStringFromProject(ProjectManager.getInstance().currentProject)
    }

    private fun createProject() {
        val script = UiTestUtils.createProjectAndGetStartScript(UndoTest::class.java.simpleName)
        val compositeBrick = IfLogicBeginBrick()
        compositeBrick.addBrickToIfBranch(SetXBrick())
        compositeBrick.addBrickToElseBranch(SetXBrick())
        script.addBrick(compositeBrick)

        XstreamSerializer.getInstance()
            .saveProject(ProjectManager.getInstance().currentProject)
        initialProject = getProjectAsXmlString()
    }

    @Test
    fun testMultiStepUndo() {
        // 1. Delete first brick
        onBrickAtPosition(brickPosition).performDeleteBrick()
        onView(withId(R.id.menu_undo)).check(matches(isEnabled()))

        // 2. Delete second brick
        onBrickAtPosition(brickPosition).performDeleteBrick()

        // 3. Undo first time
        onView(withId(R.id.menu_undo)).perform(click())
        onView(withId(R.id.menu_undo)).perform(WaitForConditionAction.waitFor(isEnabled(), waitThreshold))
        onView(withId(R.id.menu_undo)).check(matches(isEnabled()))
        onView(withId(R.id.menu_redo)).check(matches(isEnabled()))

        // 4. Undo second time
        onView(withId(R.id.menu_undo)).perform(click())
        onView(withId(R.id.menu_redo)).perform(WaitForConditionAction.waitFor(isEnabled(), waitThreshold))
        onView(withId(R.id.menu_undo)).check(matches(not(isEnabled())))
        onView(withId(R.id.menu_redo)).check(matches(isEnabled()))

        // 5. Redo first time
        onView(withId(R.id.menu_redo)).perform(click())
        onView(withId(R.id.menu_undo)).perform(WaitForConditionAction.waitFor(isEnabled(), waitThreshold))
        onView(withId(R.id.menu_undo)).check(matches(isEnabled()))

        // 6. Redo second time
        onView(withId(R.id.menu_redo)).perform(click())
        onView(withId(R.id.menu_undo)).perform(WaitForConditionAction.waitFor(isEnabled(), waitThreshold))
        onView(withId(R.id.menu_redo)).check(matches(not(isEnabled())))
    }

    @Test
    fun testConcurrentUndoRedo() {
        onBrickAtPosition(brickPosition).performDeleteBrick()

        // Attempt double click to simulate rapid interaction
        onView(withId(R.id.menu_undo)).perform(click(), click())

        // Wait for async undo processing to complete before checking final state
        onView(withId(R.id.menu_redo)).perform(WaitForConditionAction.waitFor(isEnabled(), waitThreshold))

        // Verify that it still works and didn't crash
        onView(withId(R.id.menu_redo)).check(matches(isEnabled()))
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun data(): Collection<Array<Any>> {
            return Arrays.asList(
                arrayOf<Any>("SingleScript", 0, R.string.brick_when_started),
                arrayOf<Any>("CompositeBrick", 1, R.string.brick_if_begin),
                arrayOf<Any>("SingleBrick", 2, R.string.brick_set_x)
            )
        }
    }
}

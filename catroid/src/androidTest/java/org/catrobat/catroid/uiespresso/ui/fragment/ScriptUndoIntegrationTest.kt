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

import android.view.View
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isEnabled
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.R
import org.catrobat.catroid.WaitForConditionAction.Companion.waitFor
import org.catrobat.catroid.content.bricks.IfLogicBeginBrick
import org.catrobat.catroid.content.bricks.SetXBrick
import org.catrobat.catroid.io.XstreamSerializer
import org.catrobat.catroid.test.utils.TestUtils
import org.catrobat.catroid.ui.SpriteActivity
import org.catrobat.catroid.ui.dragndrop.BrickListView
import org.catrobat.catroid.uiespresso.content.brick.utils.BrickDataInteractionWrapper.onBrickAtPosition
import org.catrobat.catroid.uiespresso.util.UiTestUtils
import org.catrobat.catroid.uiespresso.util.rules.FragmentActivityTestRule
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.not
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Integration-style Espresso tests that exercise the full production wiring
 * for each undo-able script action: copy, move, comment out, comment in,
 * delete, and cancel-move.
 *
 * Unlike [ScriptUndoRegressionTest][org.catrobat.catroid.test.ui.ScriptUndoRegressionTest]
 * (which drives ProjectUndoManager directly via pushState / popUndo), these tests
 * go through the real UI layer and verify that the undo button becomes enabled
 * through the actual ScriptFragment / BrickListView / ProjectUndoManager chain.
 *
 * Project layout after setUp (brick positions):
 *   0: WhenStartedBrick (script header)
 *   1: IfLogicBeginBrick
 *   2: SetXBrick (inside if-branch)
 *   3: IfLogicElseBrick
 *   4: SetXBrick (inside else-branch)
 *   5: IfLogicEndBrick
 *   6: SetXBrick (standalone, after the if)
 */
@RunWith(AndroidJUnit4::class)
class ScriptUndoIntegrationTest {

    private val waitThreshold = 5000L

    @get:Rule
    val baseActivityTestRule = FragmentActivityTestRule(
        SpriteActivity::class.java,
        SpriteActivity.EXTRA_FRAGMENT_POSITION,
        SpriteActivity.FRAGMENT_SCRIPTS
    )

    private lateinit var initialProject: String

    @Before
    fun setUp() {
        createProject()
        baseActivityTestRule.launchActivity()
    }

    @After
    fun tearDown() {
        TestUtils.deleteProjects(ScriptUndoIntegrationTest::class.java.simpleName)
    }

    @Test
    fun testCopyBrickUndoRedo() {
        onBrickAtPosition(6).performClick()
        onView(withText(R.string.brick_context_dialog_copy_brick))
            .perform(click())

        // Commit the hovering brick by calling stopMoving on the BrickListView
        dropHoveringBrick()

        onView(withId(R.id.menu_undo))
            .perform(waitFor(isEnabled(), waitThreshold))
        onView(withId(R.id.menu_undo))
            .check(matches(isEnabled()))

        onView(withId(R.id.menu_undo)).perform(click())
        onView(withId(R.id.menu_redo))
            .perform(waitFor(isEnabled(), waitThreshold))

        val projectAfterUndo = getProjectAsXmlString()
        assertEquals(initialProject, projectAfterUndo)
    }

    @Test
    fun testAddBrickUndoRedo() {
        onView(withId(R.id.button_add)).perform(click())
        onView(withText(R.string.category_motion)).perform(click())
        onView(withText(R.string.brick_place_at)).perform(click())

        // Adding a brick via AddBrickFragment triggers startMoving() in ScriptFragment
        dropHoveringBrick()

        onView(withId(R.id.menu_undo))
            .perform(waitFor(isEnabled(), waitThreshold))
        onView(withId(R.id.menu_undo))
            .check(matches(isEnabled()))

        onView(withId(R.id.menu_undo)).perform(click())
        onView(withId(R.id.menu_redo))
            .perform(waitFor(isEnabled(), waitThreshold))

        val projectAfterUndo = getProjectAsXmlString()
        assertEquals(initialProject, projectAfterUndo)
    }

    @Test
    fun testCopyControlStructureUndoRedo() {
        onBrickAtPosition(0).performClick()
        onView(withText(R.string.brick_context_dialog_copy_script))
            .perform(click())

        dropHoveringBrick()

        onView(withId(R.id.menu_undo))
            .perform(waitFor(isEnabled(), waitThreshold))

        onView(withId(R.id.menu_undo)).perform(click())
        onView(withId(R.id.menu_redo))
            .perform(waitFor(isEnabled(), waitThreshold))

        val projectAfterUndo = getProjectAsXmlString()
        assertEquals(initialProject, projectAfterUndo)
    }

    @Test
    fun testCommentOutBrickUndoRedo() {
        onBrickAtPosition(6).performClick()
        onView(withText(R.string.brick_context_dialog_comment_out))
            .perform(click())

        onView(withId(R.id.menu_undo))
            .perform(waitFor(isEnabled(), waitThreshold))
        onView(withId(R.id.menu_undo))
            .check(matches(isEnabled()))

        onView(withId(R.id.menu_undo)).perform(click())
        onView(withId(R.id.menu_redo))
            .perform(waitFor(isEnabled(), waitThreshold))

        val projectAfterUndo = getProjectAsXmlString()
        assertEquals(initialProject, projectAfterUndo)
    }

    @Test
    fun testCommentInScriptUndoRedo() {
        onBrickAtPosition(0).performClick()
        onView(withText(R.string.brick_context_dialog_comment_out_script))
            .perform(click())

        onView(withId(R.id.menu_undo))
            .perform(waitFor(isEnabled(), waitThreshold))

        onBrickAtPosition(0).performClick()
        onView(withText(R.string.brick_context_dialog_comment_in_script))
            .perform(click())

        onView(withId(R.id.menu_undo))
            .perform(waitFor(isEnabled(), waitThreshold))

        // Undo the comment-in
        onView(withId(R.id.menu_undo)).perform(click())
        onView(withId(R.id.menu_undo))
            .perform(waitFor(isEnabled(), waitThreshold))

        // Undo the comment-out
        onView(withId(R.id.menu_undo)).perform(click())
        onView(withId(R.id.menu_redo))
            .perform(waitFor(isEnabled(), waitThreshold))

        val projectAfterUndo = getProjectAsXmlString()
        assertEquals(initialProject, projectAfterUndo)
    }

    @Test
    fun testDeleteBrickUndoRedo() {
        onBrickAtPosition(6).performDeleteBrick()

        onView(withId(R.id.menu_undo))
            .perform(waitFor(isDisplayed(), waitThreshold))

        onView(withId(R.id.menu_undo)).perform(click())

        onView(withId(R.id.menu_redo))
            .perform(waitFor(isEnabled(), waitThreshold))

        val projectAfterUndo = getProjectAsXmlString()
        assertEquals(initialProject, projectAfterUndo)
    }

    @Test
    fun testMoveBrickUndoRedo() {
        onBrickAtPosition(6).performClick()
        onView(withText(R.string.brick_context_dialog_move_brick))
            .perform(click())

        dropHoveringBrick()

        onView(withId(R.id.menu_undo))
            .perform(waitFor(isEnabled(), waitThreshold))
        onView(withId(R.id.menu_undo))
            .check(matches(isEnabled()))

        onView(withId(R.id.menu_undo)).perform(click())
        onView(withId(R.id.menu_redo))
            .perform(waitFor(isEnabled(), waitThreshold))

        val projectAfterUndo = getProjectAsXmlString()
        assertEquals(initialProject, projectAfterUndo)
    }

    @Test
    fun testMoveControlStructureUndoRedo() {
        onBrickAtPosition(0).performClick()
        onView(withText(R.string.brick_context_dialog_move_script))
            .perform(click())

        dropHoveringBrick()

        onView(withId(R.id.menu_undo))
            .perform(waitFor(isEnabled(), waitThreshold))
        onView(withId(R.id.menu_undo))
            .check(matches(isEnabled()))

        onView(withId(R.id.menu_undo)).perform(click())
        onView(withId(R.id.menu_redo))
            .perform(waitFor(isEnabled(), waitThreshold))

        val projectAfterUndo = getProjectAsXmlString()
        assertEquals(initialProject, projectAfterUndo)
    }

    @Test
    fun testCancelMoveDoesNotCreateUndoEntry() {
        onBrickAtPosition(6).performClick()
        onView(withText(R.string.brick_context_dialog_move_brick))
            .perform(click())

        // Cancel the move via back-press instead of dropping
        pressBack()

        onView(withId(R.id.menu_undo))
            .check(matches(not(isEnabled())))
    }

    /**
     * Commits a hovering brick by calling [BrickListView.stopMoving] directly.
     * This is the same production path triggered when the user lifts their finger
     * (ACTION_UP) during a drag-and-drop operation.
     *
     * We use a custom [ViewAction] that finds the [BrickListView] and calls
     * stopMoving() on it, which is safe because Espresso runs ViewActions on the
     * main thread.
     */
    private fun dropHoveringBrick() {
        onView(allOf(isAssignableFrom(BrickListView::class.java), isDisplayed()))
            .perform(object : ViewAction {
                override fun getConstraints(): Matcher<View> =
                    allOf(isAssignableFrom(BrickListView::class.java), isDisplayed())

                override fun getDescription(): String = "call stopMoving() on BrickListView"

                override fun perform(uiController: UiController, view: View) {
                    (view as BrickListView).stopMoving()
                    uiController.loopMainThreadUntilIdle()
                }
            })
    }

    private fun getProjectAsXmlString(): String =
        XstreamSerializer.getInstance()
            .getXmlAsStringFromProject(ProjectManager.getInstance().currentProject)

    private fun createProject() {
        val script = UiTestUtils.createProjectAndGetStartScript(
            ScriptUndoIntegrationTest::class.java.simpleName
        )

        val ifBrick = IfLogicBeginBrick()
        ifBrick.addBrickToIfBranch(SetXBrick())
        ifBrick.addBrickToElseBranch(SetXBrick())
        script.addBrick(ifBrick)

        script.addBrick(SetXBrick())

        XstreamSerializer.getInstance()
            .saveProject(ProjectManager.getInstance().currentProject)
        initialProject = getProjectAsXmlString()
    }
}

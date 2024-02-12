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

import androidx.test.espresso.DataInteraction
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.openContextualActionModeOverflowMenu
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isChecked
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isEnabled
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.R
import org.catrobat.catroid.content.Script
import org.catrobat.catroid.content.bricks.ForeverBrick
import org.catrobat.catroid.content.bricks.IfLogicBeginBrick
import org.catrobat.catroid.content.bricks.SetXBrick
import org.catrobat.catroid.content.bricks.SetYBrick
import org.catrobat.catroid.ui.SpriteActivity
import org.catrobat.catroid.uiespresso.content.brick.utils.BrickDataInteractionWrapper.onBrickAtPosition
import org.catrobat.catroid.uiespresso.formulaeditor.FormulaEditorMultiplayerVariablesTest
import org.catrobat.catroid.uiespresso.util.UiTestUtils.Companion.createProjectAndGetStartScript
import org.catrobat.catroid.uiespresso.util.rules.FragmentActivityTestRule
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.java.KoinJavaComponent.inject

class CopyCompositeBrickTest {

    val projectManager: ProjectManager by inject(ProjectManager::class.java)

    var firstIndexIfComposite = 1
    var elseIndexIfComposite = 3
    var lastIndexIfComposite = 5

    var firstIndexForeverBrick = 6
    var lastIndexForeverBrick = 10

    @JvmField
    @Rule
    var baseActivityTestRule = FragmentActivityTestRule(
        SpriteActivity::class.java,
        SpriteActivity.EXTRA_FRAGMENT_POSITION,
        SpriteActivity.FRAGMENT_SCRIPTS
    )

    @Before
    fun setUp() {
        val script = createProjectAndGetStartScript(
            FormulaEditorMultiplayerVariablesTest::class.java.simpleName
        )
        addTestBricks(script)
        baseActivityTestRule.launchActivity()
    }

    @Test
    fun testEnabledInnerBricksOfCompositeBricks() {
        openContextualActionModeOverflowMenu()
        onView(withText(R.string.copy)).perform(click())

        getCheckbox(firstIndexIfComposite)?.perform(click())

        getCheckbox(firstIndexIfComposite + 1)?.check(matches(isEnabled()))
            ?.check(matches(isChecked()))

        getCheckbox(elseIndexIfComposite)?.check(matches(not(isEnabled())))
            ?.check(matches(isChecked()))

        getCheckbox(elseIndexIfComposite + 1)?.check(matches(isEnabled()))
            ?.check(matches(isChecked()))

        getCheckbox(lastIndexIfComposite)?.check(matches(isEnabled()))?.check(matches(isChecked()))
    }

    @Test
    fun testEnabledInnerBricksOfCompositeBrickInCompositeBrick() {
        openContextualActionModeOverflowMenu()
        onView(withText(R.string.copy)).perform(click())

        getCheckbox(firstIndexForeverBrick)?.perform(click())

        getCheckbox(firstIndexForeverBrick + 1)?.check(matches(isEnabled()))
            ?.check(matches(isChecked()))

        getCheckbox(firstIndexForeverBrick + 2)?.check(matches(isEnabled()))
            ?.check(matches(isChecked()))

        getCheckbox(firstIndexForeverBrick + 3)?.check(matches(not(isEnabled())))
            ?.check(matches(isChecked()))

        getCheckbox(lastIndexForeverBrick)?.check(matches(isEnabled()))?.check(matches(isChecked()))

        getCheckbox(firstIndexForeverBrick + 1)?.perform(click())

        getCheckbox(firstIndexForeverBrick + 1)?.check(matches(isEnabled()))
            ?.check(matches(not(isChecked())))

        getCheckbox(firstIndexForeverBrick + 2)?.check(matches(not(isEnabled())))
            ?.check(matches(not(isChecked())))

        getCheckbox(firstIndexForeverBrick + 3)?.check(matches(not(isEnabled())))
            ?.check(matches(not(isChecked())))
    }

    @Test
    fun testCopyCompositeBrickWithoutInnerBrick() {
        openContextualActionModeOverflowMenu()
        onView(withText(R.string.copy)).perform(click())

        getCheckbox(firstIndexIfComposite)?.perform(click())

        getCheckbox(firstIndexIfComposite + 1)?.perform(click())

        getCheckbox(lastIndexIfComposite - 1)?.perform(click())

        onView(withId(R.id.confirm)).perform(click())

        onBrickAtPosition(lastIndexForeverBrick + 1).checkShowsText(R.string.brick_if_begin)

        onBrickAtPosition(lastIndexForeverBrick + 2).checkShowsText(R.string.brick_if_else)

        onBrickAtPosition(lastIndexForeverBrick + 3).checkShowsText(R.string.brick_if_end)
    }

    @Test
    fun testCopyCompositeBrickWithoutInnerCompositeBrick() {
        openContextualActionModeOverflowMenu()
        onView(withText(R.string.copy)).perform(click())

        getCheckbox(firstIndexForeverBrick)?.perform(click())

        getCheckbox(firstIndexForeverBrick + 1)?.perform(click())

        onView(withId(R.id.confirm)).perform(click())

        onBrickAtPosition(lastIndexForeverBrick + 1).checkShowsText(R.string.brick_forever)

        onBrickAtPosition(lastIndexForeverBrick + 2).checkShowsText(R.string.brick_loop_end)
    }

    @Test
    fun testCopyCompositeBrickWithoutInnerBrickInInnerCompositeBrick() {
        openContextualActionModeOverflowMenu()
        onView(withText(R.string.copy)).perform(click())

        getCheckbox(firstIndexForeverBrick)?.perform(click())

        getCheckbox(firstIndexForeverBrick + 2)?.perform(click())

        onView(withId(R.id.confirm)).perform(click())

        onBrickAtPosition(lastIndexForeverBrick + 1).checkShowsText(R.string.brick_forever)

        onBrickAtPosition(lastIndexForeverBrick + 2).checkShowsText(R.string.brick_forever)

        onBrickAtPosition(lastIndexForeverBrick + 3).checkShowsText(R.string.brick_loop_end)

        onBrickAtPosition(lastIndexForeverBrick + 4).checkShowsText(R.string.brick_loop_end)
    }

    @Test
    fun testClickCopyInBrickContextDialogOfCompositeBrick() {

        onBrickAtPosition(firstIndexIfComposite).performClick()

        onView(withText(R.string.brick_context_dialog_copy_brick)).perform(click())

        getCheckbox(firstIndexIfComposite)?.check(matches(isEnabled()))?.check(matches(isChecked()))

        getCheckbox(firstIndexIfComposite + 1)?.check(matches(isEnabled()))
            ?.check(matches(isChecked()))

        getCheckbox(elseIndexIfComposite)?.check(matches(not(isEnabled())))
            ?.check(matches(isChecked()))

        getCheckbox(elseIndexIfComposite + 1)?.check(matches(isEnabled()))
            ?.check(matches(isChecked()))

        getCheckbox(lastIndexIfComposite)?.check(matches(isEnabled()))?.check(matches(isChecked()))
    }

    @Test
    fun testCopyCompositeBrickWithoutInnerBricksThroughBrickContextDialog() {

        onBrickAtPosition(firstIndexIfComposite).performClick()

        onView(withText(R.string.brick_context_dialog_copy_brick)).perform(click())

        getCheckbox(firstIndexIfComposite + 1)?.perform(click())

        getCheckbox(lastIndexIfComposite - 1)?.perform(click())

        onView(withId(R.id.confirm)).perform(click())

        onBrickAtPosition(lastIndexForeverBrick + 1).checkShowsText(R.string.brick_if_begin)

        onBrickAtPosition(lastIndexForeverBrick + 2).checkShowsText(R.string.brick_if_else)

        onBrickAtPosition(lastIndexForeverBrick + 3).checkShowsText(R.string.brick_if_end)
    }

    private fun getCheckbox(brickIndex: Int): DataInteraction? {
        return onBrickAtPosition(brickIndex).onChildView(
                allOf(
                    withId(R.id.brick_checkbox),
                    isDisplayed()
                )
            )
    }

    private fun addTestBricks(script: Script) {
        val ifBrick = IfLogicBeginBrick()
        ifBrick.addBrickToIfBranch(SetXBrick())
        ifBrick.addBrickToElseBranch(SetYBrick())
        script.addBrick(ifBrick)
        val foreverBrick = ForeverBrick()
        val innerForeverbrick = ForeverBrick()
        innerForeverbrick.addBrick(SetXBrick())
        foreverBrick.addBrick(innerForeverbrick)
        script.addBrick(foreverBrick)
    }
}
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
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isChecked
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isEnabled
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import org.catrobat.catroid.R
import org.catrobat.catroid.content.bricks.ForeverBrick
import org.catrobat.catroid.content.bricks.GlideToBrick
import org.catrobat.catroid.content.bricks.SetFrictionBrick
import org.catrobat.catroid.content.bricks.SetXBrick
import org.catrobat.catroid.content.bricks.SetYBrick
import org.catrobat.catroid.test.utils.TestUtils
import org.catrobat.catroid.testsuites.annotations.Cat
import org.catrobat.catroid.testsuites.annotations.Level
import org.catrobat.catroid.ui.SpriteActivity
import org.catrobat.catroid.uiespresso.content.brick.utils.BrickDataInteractionWrapper.onBrickAtPosition
import org.catrobat.catroid.uiespresso.util.UiTestUtils
import org.catrobat.catroid.uiespresso.util.rules.FragmentActivityTestRule
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.not
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.experimental.categories.Category
import java.io.IOException

@Category(Cat.AppUi::class, Level.Smoke::class)
class MoveBrickTest {

    val brickEndIndex = 6

    val setXBrick = SetXBrick()
    val setYBrick = SetYBrick()
    val glideBrick = GlideToBrick()
    val setFrictionBrick = SetFrictionBrick()
    val forBrick = ForeverBrick()

    @get:Rule
    var baseActivityTestRule = FragmentActivityTestRule(
        SpriteActivity::class.java,
        SpriteActivity.EXTRA_FRAGMENT_POSITION,
        SpriteActivity.FRAGMENT_SCRIPTS
    )

    @Before
    @Throws(IOException::class)
    fun setUp() {
        createProject(CopySpriteTest::class.java.simpleName)
        baseActivityTestRule.launchActivity()
    }

    @After
    @Throws(IOException::class)
    fun tearDown() {
        TestUtils.deleteProjects(CopySpriteTest::class.java.simpleName)
    }

    @Test
    fun testOpenMoveMenu() {
        UiTestUtils.openActionBarMenu()

        onView(withText(R.string.move)).check(matches(isDisplayed()))

        onView(withText(R.string.move)).perform(click())

        onView(withText(R.string.am_move)).check(matches(isDisplayed()))
    }

    @Test
    fun testNoItemSelected() {
        UiTestUtils.openActionBarMenu()
        onView(withText(R.string.move)).perform(click())

        for (brickIndex in 0..brickEndIndex) {
            getCheckbox(brickIndex).check(matches(isEnabled())).check(matches(not(isChecked())))
        }
    }

    @Test
    fun testSelectBricks() {
        UiTestUtils.openActionBarMenu()
        onView(withText(R.string.move)).perform(click())

        for (brickIndex in 1..brickEndIndex) {
            getCheckbox(brickIndex).check(matches(isEnabled())).check(matches(not(isChecked())))
        }

        getCheckbox(1).perform(click()).check(matches(isChecked())).check(matches(isEnabled()))

        for (brickIndex in 3..brickEndIndex) {
            getCheckbox(brickIndex).check(matches(not(isEnabled())))
                .check(matches(not(isChecked())))
        }

        getCheckbox(2).perform(click()).check(matches(isChecked())).check(matches(isEnabled()))

        for (brickIndex in 4..brickEndIndex) {
            getCheckbox(brickIndex).check(matches(not(isEnabled())))
                .check(matches(not(isChecked())))
        }

        getCheckbox(3).perform(click()).check(matches(isChecked())).check(matches(not(isEnabled())))

        for (brickIndex in 3..4) {
            getCheckbox(brickIndex).check(matches(not(isEnabled()))).check(matches(isChecked()))
        }

        getCheckbox(6).perform(click()).check(matches(isChecked())).check(matches(isEnabled()))
    }

    @Test
    fun testSelectSingleBrick() {
        UiTestUtils.openActionBarMenu()
        onView(withText(R.string.move)).perform(click())

        getCheckbox(1).perform(click()).check(matches(isChecked())).check(matches(isEnabled()))

        onBrickAtPosition(1).check(matches(isDisplayed()))

        onView(withId(R.id.confirm)).perform(click())
    }

    @Test
    fun testSelectCompositeBrick() {
        UiTestUtils.openActionBarMenu()
        onView(withText(R.string.move)).perform(click())

        getCheckbox(3).perform(click()).check(matches(isChecked())).check(matches(isEnabled()))

        onView(withText(R.string.brick_forever)).check(matches(isDisplayed()))
        onView(withText(R.string.brick_set_friction)).check(matches(isDisplayed()))
        onView(withText(R.string.brick_loop_end)).check(matches(isDisplayed()))

        onView(withId(R.id.confirm)).perform(click())

        onView(withText(R.string.brick_set_friction)).check(doesNotExist())
        onView(withText(R.string.brick_loop_end)).check(doesNotExist())

        onView(withText(R.string.brick_set_x)).perform(click())

        onView(withText(R.string.brick_forever)).check(matches(isDisplayed()))
        onView(withText(R.string.brick_set_friction)).check(matches(isDisplayed()))
        onView(withText(R.string.brick_loop_end)).check(matches(isDisplayed()))
    }

    private fun getCheckbox(brickIndex: Int): DataInteraction {
        return onBrickAtPosition(brickIndex).onChildView(
                allOf(
                    withId(R.id.brick_checkbox),
                    isDisplayed()
                )
            )
    }

    private fun createProject(projectName: String) {
        val script = UiTestUtils.createProjectAndGetStartScript(projectName)

        script.addBrick(setXBrick)
        script.addBrick(setYBrick)

        forBrick.addBrick(setFrictionBrick)
        script.addBrick(forBrick)

        script.addBrick(glideBrick)
    }
}

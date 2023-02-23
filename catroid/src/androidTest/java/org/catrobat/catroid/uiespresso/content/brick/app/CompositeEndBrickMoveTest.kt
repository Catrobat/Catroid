/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2023 The Catrobat Team
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

package org.catrobat.catroid.uiespresso.content.brick.app

import android.view.View
import android.view.ViewConfiguration
import androidx.annotation.NonNull
import androidx.appcompat.widget.AppCompatTextView
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.GeneralLocation
import androidx.test.espresso.action.MotionEvents
import androidx.test.espresso.action.Press
import org.catrobat.catroid.R
import androidx.test.espresso.matcher.ViewMatchers.withId
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Script
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.StartScript
import org.catrobat.catroid.content.bricks.ChangeSizeByNBrick
import org.catrobat.catroid.content.bricks.IfThenLogicBeginBrick
import org.catrobat.catroid.content.bricks.RepeatBrick
import org.catrobat.catroid.test.utils.TestUtils
import org.catrobat.catroid.ui.SpriteActivity
import org.catrobat.catroid.uiespresso.util.rules.FragmentActivityTestRule
import org.hamcrest.Matcher
import org.hamcrest.core.IsAnything
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class CompositeEndBrickMoveTest {
    private lateinit var project: Project
    private val oneBlockDown: Float = -310f
    private val oneBlockUp: Float = 360f
    private lateinit var ifBrick: IfThenLogicBeginBrick

    @Rule
    @JvmField
    var baseActivityTestRule = FragmentActivityTestRule(
        SpriteActivity::class.java,
        SpriteActivity.EXTRA_FRAGMENT_POSITION,
        SpriteActivity.FRAGMENT_SCRIPTS
    )

    @Before
    @Throws(Exception::class)
    fun setUp() {
        createProject(this.javaClass.simpleName)
        baseActivityTestRule.launchActivity()
    }

    @Test
    fun testMoveEndBrick() {
        Assert.assertEquals(ifBrick.nestedBricks.size, 1)
        onView(
            withId(R.id.brick_if_end_if_label)
        ).perform(
            DragAndDrop(oneBlockUp*3)
        )
        Assert.assertEquals(ifBrick.nestedBricks.size, 1)
        onView(
            withId(R.id.brick_if_end_if_label)
        ).perform(
            DragAndDrop(oneBlockDown)
        )

        Assert.assertEquals(ifBrick.nestedBricks.size, 2)
        onView(
            withId(R.id.brick_if_end_if_label)
        ).perform(
            DragAndDrop(oneBlockUp)
        )
        Assert.assertEquals(ifBrick.nestedBricks.size, 1)
        onView(
            withId(R.id.brick_if_end_if_label)
        ).perform(
            DragAndDrop(oneBlockUp)
        )
        Assert.assertEquals(ifBrick.nestedBricks.size, 0)

        onView(
            withId(R.id.brick_if_end_if_label)
        ).perform(
            DragAndDrop(oneBlockDown)
        )
        onView(
            withId(R.id.brick_if_end_if_label)
        ).perform(
            DragAndDrop(oneBlockDown)
        )
        Assert.assertEquals(ifBrick.nestedBricks.size, 2)
        onView(
            withId(R.id.brick_if_end_if_label)
        ).perform(
            DragAndDrop(oneBlockDown)
        )
        Assert.assertTrue(ifBrick.parent is RepeatBrick)
    }

    @After
    @Throws(Exception::class)
    fun tearDown() {
        TestUtils.deleteProjects(this.javaClass.simpleName)
    }

    private fun createProject(projectName: String) {
        project = Project(ApplicationProvider.getApplicationContext(), projectName)
        val sprite = Sprite("testSprite")
        val script: Script = StartScript()
        ifBrick = IfThenLogicBeginBrick()
        val changeSizeBrick = ChangeSizeByNBrick(2.0)
        val changeSizeBrick2 = ChangeSizeByNBrick(3.0)
        ifBrick.addBrick(changeSizeBrick)
        script.addBrick(ifBrick)
        script.addBrick(changeSizeBrick2)
        script.addBrick(RepeatBrick())
        sprite.addScript(script)

        project.defaultScene.addSprite(sprite)
        ProjectManager.getInstance().currentProject = project
        ProjectManager.getInstance().currentSprite = sprite
    }
}

// from https://stackoverflow.com/questions/35297442/drag-drop-espresso and slightly adjusted
class DragAndDrop(private val offset: Float) : ViewAction {
    @NonNull
    private fun anyView(): Matcher<View> = IsAnything()

    override fun getConstraints(): Matcher<View> = anyView()

    override fun getDescription(): String = "DragAndDrop"

    override fun perform(uiController: UiController, view: View) {
        val recyclerView: AppCompatTextView = view as AppCompatTextView

        uiController.loopMainThreadUntilIdle()

        val sourceViewCenter = GeneralLocation.VISIBLE_CENTER.calculateCoordinates(recyclerView)
        val targetViewCenter = sourceViewCenter.clone()
        targetViewCenter[1] = targetViewCenter[1] - offset
        val fingerPrecision = Press.FINGER.describePrecision()
        val downEvent = MotionEvents.sendDown(uiController, sourceViewCenter, fingerPrecision).down
        try {
            // Factor 1.5 is needed, otherwise a long press is not safely detected.
            val longPressTimeout = (ViewConfiguration.getLongPressTimeout() * 2f).toLong()
            uiController.loopMainThreadForAtLeast(longPressTimeout)
            // Drag to the position
            uiController.loopMainThreadUntilIdle()

            val steps = interpolate(sourceViewCenter, targetViewCenter)

            for (element in steps) {
                if (!MotionEvents.sendMovement(uiController, downEvent, element)) {
                    MotionEvents.sendCancel(uiController, downEvent)
                }
            }
            // Release
            if (!MotionEvents.sendUp(uiController, downEvent, targetViewCenter)) {
                MotionEvents.sendCancel(uiController, downEvent)
            }
        } finally {
            downEvent.recycle()
        }
    }

    private val SWIPE_EVENT_COUNT = 10

    private fun interpolate(start: FloatArray, end: FloatArray): Array<FloatArray> {
        val res = Array(SWIPE_EVENT_COUNT) { FloatArray(2) }

        for (i in 1..SWIPE_EVENT_COUNT) {
            res[i - 1][0] = start[0] + (end[0] - start[0]) * i / SWIPE_EVENT_COUNT
            res[i - 1][1] = start[1] + (end[1] - start[1]) * i / SWIPE_EVENT_COUNT
        }
        return res
    }
}

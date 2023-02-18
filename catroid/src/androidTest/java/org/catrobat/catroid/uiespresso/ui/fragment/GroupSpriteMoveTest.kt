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

package org.catrobat.catroid.uiespresso.ui.fragment

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
import androidx.test.espresso.matcher.ViewMatchers.withSubstring
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.content.GroupItemSprite
import org.catrobat.catroid.content.GroupSprite
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.test.utils.TestUtils
import org.catrobat.catroid.ui.ProjectActivity
import org.catrobat.catroid.uiespresso.util.rules.FragmentActivityTestRule
import org.hamcrest.Matcher
import org.hamcrest.core.IsAnything
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GroupSpriteMoveTest {

    private lateinit var project: Project
    var projectName = "GroupSpriteMoveTest"
    private val sprite1 = "1"
    private val sprite2 = "3"
    private val sprite3 = "5"
    private val group1 = "2"
    private val group2 = "4"
    private val listGroupItems = listOf("3", "5")
    private val listNotGroupItems = listOf("1")
    private val listGroups = listOf("2", "4")
    private val listAfterNoMove = listOf(1, 2, 3, 4, 5)
    private val listAfterFirstMove = listOf(2, 3, 1, 4, 5)
    private val listAfterSecondMove = listOf(1, 2, 3, 4, 5)
    private val listAfterThirdMove = listOf(1, 4, 5, 2, 3)
    private val listAfterFourthMove = listOf(1, 2, 3, 4, 5)
    private val oneBlock: Float = 350f
    private val listAfterMove = listOf(listAfterNoMove, listAfterFirstMove,
        listAfterSecondMove, listAfterThirdMove, listAfterFourthMove)

    @Rule
    @JvmField
    var baseActivityTestRule = FragmentActivityTestRule(
        ProjectActivity::class.java, ProjectActivity.EXTRA_FRAGMENT_POSITION,
        ProjectActivity.FRAGMENT_SPRITES
    )

    @Before
    fun setUp() {
        createProject(projectName)
        baseActivityTestRule.launchActivity()
    }

    @After
    fun tearDown() {
        baseActivityTestRule.finishActivity()
        TestUtils.deleteProjects(projectName)
    }

    private fun checkList(indexMove: Int) {
        val list = project.defaultScene.spriteList
        val checkList = listAfterMove[indexMove]

        for ((index, item) in list.withIndex()) {
            if (index == 0) {
                continue
            }
            val name = checkList[index - 1].toString()
            Assert.assertEquals(item.name, name)
            when (item.name) {
                in listGroups -> Assert.assertTrue(item is GroupSprite)
                in listGroupItems -> Assert.assertTrue(item is GroupItemSprite)
                in listNotGroupItems -> Assert.assertTrue(item is Sprite)
            }
        }
    }

    @Test
    fun testMoveGroup() {
        var index = 0
        checkList(index++)
        onView(
                withSubstring("2")
            ).perform(
            DragAndDrop(oneBlock)
        )
        checkList(index++)
        onView(
            withSubstring("2")
        ).perform(
            DragAndDrop(-2f*oneBlock)
        )
        checkList(index++)
        onView(
            withSubstring("2")
        ).perform(
            DragAndDrop(-2f*oneBlock)
        )
        checkList(index++)
        onView(
            withSubstring("2")
        ).perform(
            DragAndDrop(2f*oneBlock)
        )
        checkList(index)
    }

    fun createProject(projectName: String?) {
        project = Project(ApplicationProvider.getApplicationContext(), projectName)

        ProjectManager.getInstance().currentProject = project
        ProjectManager.getInstance().currentlyEditedScene = project.defaultScene

        project.defaultScene.addSprite(Sprite(sprite1))

        val groupSprite1 = GroupSprite(group1)
        project.defaultScene.addSprite(groupSprite1)

        var sprite = Sprite(sprite2)
        sprite.setConvertToGroupItemSprite(true)
        sprite = sprite.convert()
        project.defaultScene.addSprite(sprite)

        val groupSprite2 = GroupSprite(group2)
        project.defaultScene.addSprite(groupSprite2)

        sprite = Sprite(sprite3)
        sprite.setConvertToGroupItemSprite(true)
        sprite = sprite.convert()
        project.defaultScene.addSprite(sprite)

        groupSprite1.isCollapsed = false
        groupSprite2.isCollapsed = false
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

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
import org.catrobat.catroid.content.Scene
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
import org.koin.java.KoinJavaComponent.inject

@RunWith(AndroidJUnit4::class)
class GroupSpriteMoveTest {

    private lateinit var project: Project
    private lateinit var scene: Scene
    private val projectManager: ProjectManager by inject(ProjectManager::class.java)

    private val oneBlock: Float = 150f

    private lateinit var groupSprite1: GroupSprite
    private lateinit var groupSprite2: GroupSprite
    private lateinit var groupSprite3: GroupSprite
    private lateinit var groupSprite4: GroupSprite
    private lateinit var groupSprite5: GroupSprite

    private lateinit var groupItemSprite1: GroupItemSprite
    private lateinit var groupItemSprite2: GroupItemSprite
    private lateinit var groupItemSprite3: GroupItemSprite
    private lateinit var groupItemSprite4: GroupItemSprite
    private lateinit var groupItemSprite5: GroupItemSprite
    private lateinit var groupItemSprite6: GroupItemSprite
    private lateinit var groupItemSprite7: GroupItemSprite
    private lateinit var groupItemSprite8: GroupItemSprite
    private lateinit var groupItemSprite9: GroupItemSprite
    private lateinit var groupItemSprite10: GroupItemSprite

    private lateinit var basicList: List<Sprite>

    @Rule
    @JvmField
    var baseActivityTestRule = FragmentActivityTestRule(
        ProjectActivity::class.java, ProjectActivity.EXTRA_FRAGMENT_POSITION,
        ProjectActivity.FRAGMENT_SPRITES
    )

    @Before
    fun setUp() {
        project = Project(
            ApplicationProvider.getApplicationContext(),
            GroupSpriteMoveTest::class.java.simpleName
        )

        projectManager.currentProject = project
        projectManager.currentlyEditedScene = project.defaultScene

        scene = project.defaultScene

        groupSprite1 = GroupSprite("group sprite (1)")
        groupItemSprite1 = GroupItemSprite("child (1)")
        groupItemSprite2 = GroupItemSprite("child (2)")
        groupSprite1.addToChildrenSpriteList(groupItemSprite1)
        groupSprite1.addToChildrenSpriteList(groupItemSprite2)

        groupSprite2 = GroupSprite("group sprite (2)")
        groupItemSprite3 = GroupItemSprite("child (3)")
        groupItemSprite4 = GroupItemSprite("child (4)")
        groupSprite2.addToChildrenSpriteList(groupItemSprite3)
        groupSprite2.addToChildrenSpriteList(groupItemSprite4)

        groupSprite3 = GroupSprite("group sprite (3)")
        groupItemSprite5 = GroupItemSprite("child (5)")
        groupItemSprite6 = GroupItemSprite("child (6)")
        groupSprite3.addToChildrenSpriteList(groupItemSprite5)
        groupSprite3.addToChildrenSpriteList(groupItemSprite6)

        groupSprite4 = GroupSprite("group sprite (4)")
        groupItemSprite7 = GroupItemSprite("child (7)")
        groupItemSprite8 = GroupItemSprite("child (8)")
        groupSprite4.addToChildrenSpriteList(groupItemSprite7)
        groupSprite4.addToChildrenSpriteList(groupItemSprite8)

        groupSprite5 = GroupSprite("group sprite (5)")
        groupItemSprite9 = GroupItemSprite("child (9)")
        groupItemSprite10 = GroupItemSprite("child (10)")
        groupSprite5.addToChildrenSpriteList(groupItemSprite9)
        groupSprite5.addToChildrenSpriteList(groupItemSprite10)

        basicList = listOf(
            groupSprite1, groupItemSprite1, groupItemSprite2,
            groupSprite2, groupItemSprite3, groupItemSprite4,
            groupSprite3, groupItemSprite5, groupItemSprite6,
            groupSprite4, groupItemSprite7, groupItemSprite8,
            groupSprite5, groupItemSprite9, groupItemSprite10
        )

        scene.addSprite(groupSprite1)
        scene.addSprite(groupItemSprite1)
        scene.addSprite(groupItemSprite2)
        scene.addSprite(groupSprite2)
        scene.addSprite(groupItemSprite3)
        scene.addSprite(groupItemSprite4)
        scene.addSprite(groupSprite3)
        scene.addSprite(groupItemSprite5)
        scene.addSprite(groupItemSprite6)
        scene.addSprite(groupSprite4)
        scene.addSprite(groupItemSprite7)
        scene.addSprite(groupItemSprite8)
        scene.addSprite(groupSprite5)
        scene.addSprite(groupItemSprite9)
        scene.addSprite(groupItemSprite10)

        baseActivityTestRule.launchActivity()
    }

    @After
    fun tearDown() {
        baseActivityTestRule.finishActivity()
        TestUtils.deleteProjects(GroupSpriteMoveTest::class.java.simpleName)
    }

    private fun checkList(checkList: List<Sprite>) {
        val list = project.defaultScene.spriteList

        for ((index, item) in list.withIndex()) {
            if (index == 0) {
                continue
            }
            val checkItem = checkList[index - 1]
            Assert.assertEquals(item, checkItem)
        }
    }

    @Test
    fun testDragAndDropUp() {
        checkList(basicList)
        onView(withSubstring("group sprite (4)")).perform(DragAndDrop(oneBlock))
        checkList(
            listOf(
                groupSprite1, groupItemSprite1, groupItemSprite2,
                groupSprite2, groupItemSprite3, groupItemSprite4,
                groupSprite4, groupItemSprite7, groupItemSprite8,
                groupSprite3, groupItemSprite5, groupItemSprite6,
                groupSprite5, groupItemSprite9, groupItemSprite10
            )
        )
        onView(withSubstring("group sprite (5)")).perform(DragAndDrop(oneBlock))
        checkList(
            listOf(
                groupSprite1, groupItemSprite1, groupItemSprite2,
                groupSprite2, groupItemSprite3, groupItemSprite4,
                groupSprite4, groupItemSprite7, groupItemSprite8,
                groupSprite5, groupItemSprite9, groupItemSprite10,
                groupSprite3, groupItemSprite5, groupItemSprite6
            )
        )
        onView(withSubstring("group sprite (2)")).perform(DragAndDrop(oneBlock))
        checkList(
            listOf(
                groupSprite2, groupItemSprite3, groupItemSprite4,
                groupSprite1, groupItemSprite1, groupItemSprite2,
                groupSprite4, groupItemSprite7, groupItemSprite8,
                groupSprite5, groupItemSprite9, groupItemSprite10,
                groupSprite3, groupItemSprite5, groupItemSprite6
            )
        )
        onView(withSubstring("group sprite (3)")).perform(DragAndDrop(oneBlock))
        checkList(
            listOf(
                groupSprite2, groupItemSprite3, groupItemSprite4,
                groupSprite1, groupItemSprite1, groupItemSprite2,
                groupSprite4, groupItemSprite7, groupItemSprite8,
                groupSprite3, groupItemSprite5, groupItemSprite6,
                groupSprite5, groupItemSprite9, groupItemSprite10
            )
        )
    }

    @Test
    fun testDragAndDropDown() {
        checkList(basicList)
        onView(withSubstring("group sprite (1)")).perform(DragAndDrop(-oneBlock))
        checkList(
            listOf(
                groupSprite2, groupItemSprite3, groupItemSprite4,
                groupSprite1, groupItemSprite1, groupItemSprite2,
                groupSprite3, groupItemSprite5, groupItemSprite6,
                groupSprite4, groupItemSprite7, groupItemSprite8,
                groupSprite5, groupItemSprite9, groupItemSprite10
            )
        )
        onView(withSubstring("group sprite (4)")).perform(DragAndDrop(-oneBlock))
        checkList(
            listOf(
                groupSprite2, groupItemSprite3, groupItemSprite4,
                groupSprite1, groupItemSprite1, groupItemSprite2,
                groupSprite3, groupItemSprite5, groupItemSprite6,
                groupSprite5, groupItemSprite9, groupItemSprite10,
                groupSprite4, groupItemSprite7, groupItemSprite8
            )
        )
        onView(withSubstring("group sprite (3)")).perform(DragAndDrop(-oneBlock))
        checkList(
            listOf(
                groupSprite2, groupItemSprite3, groupItemSprite4,
                groupSprite1, groupItemSprite1, groupItemSprite2,
                groupSprite5, groupItemSprite9, groupItemSprite10,
                groupSprite3, groupItemSprite5, groupItemSprite6,
                groupSprite4, groupItemSprite7, groupItemSprite8
            )
        )
        onView(withSubstring("group sprite (1)")).perform(DragAndDrop(-oneBlock))
        checkList(
            listOf(
                groupSprite2, groupItemSprite3, groupItemSprite4,
                groupSprite5, groupItemSprite9, groupItemSprite10,
                groupSprite1, groupItemSprite1, groupItemSprite2,
                groupSprite3, groupItemSprite5, groupItemSprite6,
                groupSprite4, groupItemSprite7, groupItemSprite8
            )
        )
    }
}

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
            val longPressTimeout = (ViewConfiguration.getLongPressTimeout() * 2f).toLong()
            uiController.loopMainThreadForAtLeast(longPressTimeout)

            uiController.loopMainThreadUntilIdle()

            val steps = interpolate(sourceViewCenter, targetViewCenter)

            for (element in steps) {
                if (!MotionEvents.sendMovement(uiController, downEvent, element)) {
                    MotionEvents.sendCancel(uiController, downEvent)
                }
            }

            if (!MotionEvents.sendUp(uiController, downEvent, targetViewCenter)) {
                MotionEvents.sendCancel(uiController, downEvent)
            }
        } finally {
            downEvent.recycle()
        }
    }

    private val swipeEventCount = 5

    private fun interpolate(start: FloatArray, end: FloatArray): Array<FloatArray> {
        val res = Array(swipeEventCount) { FloatArray(2) }

        for (i in 1..swipeEventCount) {
            res[i - 1][0] = start[0] + (end[0] - start[0]) * i / swipeEventCount
            res[i - 1][1] = start[1] + (end[1] - start[1]) * i / swipeEventCount
        }
        return res
    }
}

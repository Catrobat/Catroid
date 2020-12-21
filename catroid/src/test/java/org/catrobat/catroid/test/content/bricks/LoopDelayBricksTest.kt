/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2020 The Catrobat Team
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

package org.catrobat.catroid.test.content.bricks

import com.badlogic.gdx.utils.GdxNativesLoader
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.common.BrickValues
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Scene
import org.catrobat.catroid.content.Script
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.StartScript
import org.catrobat.catroid.content.actions.RepeatParameterizedAction
import org.catrobat.catroid.content.actions.RepeatUntilAction
import org.catrobat.catroid.content.actions.ScriptSequenceAction
import org.catrobat.catroid.content.bricks.Brick
import org.catrobat.catroid.content.bricks.Brick.BrickData
import org.catrobat.catroid.content.bricks.ChangeBrightnessByNBrick
import org.catrobat.catroid.content.bricks.ChangeColorByNBrick
import org.catrobat.catroid.content.bricks.ChangeSizeByNBrick
import org.catrobat.catroid.content.bricks.ChangeTransparencyByNBrick
import org.catrobat.catroid.content.bricks.ChangeXByNBrick
import org.catrobat.catroid.content.bricks.ChangeYByNBrick
import org.catrobat.catroid.content.bricks.ForItemInUserListBrick
import org.catrobat.catroid.content.bricks.ForVariableFromToBrick
import org.catrobat.catroid.content.bricks.GoToBrick
import org.catrobat.catroid.content.bricks.IfOnEdgeBounceBrick
import org.catrobat.catroid.content.bricks.MoveNStepsBrick
import org.catrobat.catroid.content.bricks.NextLookBrick
import org.catrobat.catroid.content.bricks.ParameterizedBrick
import org.catrobat.catroid.content.bricks.ParameterizedEndBrick
import org.catrobat.catroid.content.bricks.PlaceAtBrick
import org.catrobat.catroid.content.bricks.PointInDirectionBrick
import org.catrobat.catroid.content.bricks.PointToBrick
import org.catrobat.catroid.content.bricks.PreviousLookBrick
import org.catrobat.catroid.content.bricks.RepeatBrick
import org.catrobat.catroid.content.bricks.RepeatUntilBrick
import org.catrobat.catroid.content.bricks.SetBackgroundBrick
import org.catrobat.catroid.content.bricks.SetBackgroundByIndexBrick
import org.catrobat.catroid.content.bricks.SetBrightnessBrick
import org.catrobat.catroid.content.bricks.SetColorBrick
import org.catrobat.catroid.content.bricks.SetLookBrick
import org.catrobat.catroid.content.bricks.SetLookByIndexBrick
import org.catrobat.catroid.content.bricks.SetSizeToBrick
import org.catrobat.catroid.content.bricks.SetTransparencyBrick
import org.catrobat.catroid.content.bricks.SetXBrick
import org.catrobat.catroid.content.bricks.SetYBrick
import org.catrobat.catroid.content.bricks.TurnLeftBrick
import org.catrobat.catroid.content.bricks.TurnRightBrick
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.formulaeditor.UserList
import org.catrobat.catroid.formulaeditor.UserVariable
import org.catrobat.catroid.test.MockUtil
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.mockito.Mockito
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import org.powermock.modules.junit4.PowerMockRunnerDelegate

@RunWith(PowerMockRunner::class)
@PrepareForTest(GdxNativesLoader::class, ParameterizedBrick::class)
@PowerMockRunnerDelegate(Parameterized::class)
internal class LoopDelayBricksTest(private val brick: Brick?) {

    private lateinit var script: Script
    private lateinit var sprite: Sprite
    private lateinit var project: Project
    private lateinit var scriptSequenceAction: ScriptSequenceAction
    private lateinit var scene: Scene
    private val REPEAT_TIMES = 3
    private val delta = 0.001f
    private val endBrickMock = PowerMockito.mock(ParameterizedEndBrick::class.java)

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun data(): List<Array<out Any?>> {
            return listOf(
                arrayOf(PlaceAtBrick(0, 0)),
                arrayOf(SetXBrick(0)),
                arrayOf(SetYBrick(0)),
                arrayOf(ChangeXByNBrick(0)),
                arrayOf(ChangeYByNBrick(0)),
                arrayOf(GoToBrick()),
                arrayOf(IfOnEdgeBounceBrick()),
                arrayOf(MoveNStepsBrick(0.0)),
                arrayOf(TurnLeftBrick(0.0)),
                arrayOf(TurnRightBrick(0.0)),
                arrayOf(PointInDirectionBrick(0.0)),
                arrayOf(PointToBrick(Sprite())),
                arrayOf(SetLookBrick()),
                arrayOf(SetLookByIndexBrick(0)),
                arrayOf(NextLookBrick()),
                arrayOf(PreviousLookBrick()),
                arrayOf(SetSizeToBrick(0.0)),
                arrayOf(ChangeSizeByNBrick(0.0)),
                arrayOf(SetTransparencyBrick(0.0)),
                arrayOf(ChangeTransparencyByNBrick(0.0)),
                arrayOf(SetBrightnessBrick(0.0)),
                arrayOf(ChangeBrightnessByNBrick(0.0)),
                arrayOf(SetColorBrick(0.0)),
                arrayOf(ChangeColorByNBrick(0.0)),
                arrayOf(SetBackgroundBrick()),
                arrayOf(SetBackgroundByIndexBrick(0))
            )
        }
    }

    @Before
    fun setUp() {
        scene = Scene()
        project = Project(MockUtil.mockContextForProject(), "testProject")
        project.addScene(scene)
        sprite = Sprite("testSprite")
        scene.addSprite(sprite)

        ProjectManager.getInstance().currentProject = project
        ProjectManager.getInstance().currentSprite = sprite

        script = StartScript()
        scriptSequenceAction = ScriptSequenceAction(script)
        sprite.addScript(script)
        PowerMockito.mockStatic(GdxNativesLoader::class.java)
        PowerMockito.whenNew(ParameterizedEndBrick::class.java).withAnyArguments().thenReturn(endBrickMock)
    }

    private fun testLoopDelayForSpecialBrick(loopBrick: Brick) {
        script.addBrick(loopBrick)
        val loopAction = scriptSequenceAction.actions[0]

        repeat(REPEAT_TIMES + 1) {
            assert(!loopAction.act(delta))
        }
    }

    @Test
    fun testRepeatBrickDelay() {
        if (brick is GoToBrick) {
            brick.onItemSelected(BrickValues.GO_TO_RANDOM_POSITION, null)
        }
        val repeatBrick = RepeatBrick(Formula(REPEAT_TIMES))
        repeatBrick.addBrick(brick)
        repeatBrick.addActionToSequence(sprite, scriptSequenceAction)
        testLoopDelayForSpecialBrick(repeatBrick)
    }

    @Test
    fun testRepeatUntilBrickDelay() {
        if (brick is GoToBrick) {
            brick.onItemSelected(BrickValues.GO_TO_RANDOM_POSITION, null)
        }
        val repeatUntilBrick = RepeatUntilBrick()
        repeatUntilBrick.addBrick(brick)
        repeatUntilBrick.addActionToSequence(sprite, scriptSequenceAction)
        val loopAction = scriptSequenceAction.actions[0] as RepeatUntilAction
        loopAction.repeatCondition = Formula(0.0)
        loopAction.act(delta)
        loopAction.repeatCondition = Formula(1.0)
        testLoopDelayForSpecialBrick(repeatUntilBrick)
    }

    @Test
    fun testForVariableFromToBrickDelay() {
        if (brick is GoToBrick) {
            brick.onItemSelected(BrickValues.GO_TO_RANDOM_POSITION, null)
        }
        val forVariableFromToBrick = ForVariableFromToBrick(0, REPEAT_TIMES)
        forVariableFromToBrick.addBrick(brick)
        forVariableFromToBrick.addActionToSequence(sprite, scriptSequenceAction)
        testLoopDelayForSpecialBrick(forVariableFromToBrick)
    }

    @Test
    fun testForItemInUserListBrickDelay() {
        if (brick is GoToBrick) {
            brick.onItemSelected(BrickValues.GO_TO_RANDOM_POSITION, null)
        }
        val userVariable = UserVariable()
        userVariable.value = "testValue"
        val userList = UserList()
        repeat(REPEAT_TIMES) {
            userList.addListItem("testValue")
        }
        val forItemInUserListBrick = ForItemInUserListBrick()
        val forItemInUserListBrickSpy = PowerMockito.spy(forItemInUserListBrick)

        Mockito.doReturn(userVariable).`when`(forItemInUserListBrickSpy).getUserVariableWithBrickData(
            BrickData.FOR_ITEM_IN_USERLIST_VARIABLE)
        Mockito.doReturn(userList).`when`(forItemInUserListBrickSpy).getUserListWithBrickData(
            BrickData.FOR_ITEM_IN_USERLIST_LIST)

        forItemInUserListBrickSpy.addBrick(brick)
        forItemInUserListBrickSpy.addActionToSequence(sprite, scriptSequenceAction)
        testLoopDelayForSpecialBrick(forItemInUserListBrickSpy)
    }

    @Test
    fun testParameterizedBrickDelay() {
        if (brick is GoToBrick) {
            brick.onItemSelected(BrickValues.GO_TO_RANDOM_POSITION, null)
        }

        val userVariable = UserVariable("testName")
        userVariable.value = "testValue"
        sprite.addUserVariable(userVariable)
        val userList = UserList("testName")
        repeat(REPEAT_TIMES) {
            userList.addListItem("testValue")
        }

        val parameterizedBrick = ParameterizedBrick()
        parameterizedBrick.userLists.add(userList)
        brick?.let { parameterizedBrick.addBrick(it) }
        parameterizedBrick.addActionToSequence(sprite, scriptSequenceAction)
        script.addBrick(parameterizedBrick)

        val parameterizedData = parameterizedBrick.parameterizedData
        parameterizedData.listSize = 3

        val loopAction = scriptSequenceAction.actions[0] as RepeatParameterizedAction
        repeat(REPEAT_TIMES + 1) {
            assert(!loopAction.act(delta))
            parameterizedData.currentPosition++
        }
    }
}

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
package org.catrobat.catroid.test.content.bricks

import junit.framework.Assert
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.StartScript
import org.catrobat.catroid.content.bricks.AskBrick
import org.catrobat.catroid.content.bricks.AskSpeechBrick
import org.catrobat.catroid.content.bricks.ChangeVariableBrick
import org.catrobat.catroid.content.bricks.HideTextBrick
import org.catrobat.catroid.content.bricks.ReadVariableFromDeviceBrick
import org.catrobat.catroid.content.bricks.SetVariableBrick
import org.catrobat.catroid.content.bricks.ShowTextBrick
import org.catrobat.catroid.content.bricks.ShowTextColorSizeAlignmentBrick
import org.catrobat.catroid.content.bricks.UserVariableBrickInterface
import org.catrobat.catroid.content.bricks.WebRequestBrick
import org.catrobat.catroid.content.bricks.WriteVariableOnDeviceBrick
import org.catrobat.catroid.formulaeditor.UserVariable
import org.catrobat.catroid.test.MockUtil
import org.catrobat.catroid.ui.recyclerview.controller.SpriteController
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.util.Arrays

@RunWith(Parameterized::class)
class CloneBrickUpdateVariableTest {
    @JvmField
    @Parameterized.Parameter
    var name: String? = null
    @JvmField
    @Parameterized.Parameter(1)
    var brick: UserVariableBrickInterface? = null
    private var sprite: Sprite? = null
    private var clonedSprite: Sprite? = null
    @Before
    @Throws(Exception::class)
    fun setUp() {
        val project = Project(MockUtil.mockContextForProject(), "testProject")
        ProjectManager.getInstance().currentProject = project
        sprite = Sprite("testSprite")
        project.defaultScene.addSprite(sprite)
        val script = StartScript()
        sprite!!.addScript(script)
        sprite!!.addUserVariable(UserVariable(VARIABLE_NAME))
        script.addBrick(brick)
        brick!!.userVariable = USER_VARIABLE
        clonedSprite = SpriteController().copy(sprite, project, project.defaultScene)
    }

    @Test
    fun testClonedSpriteAndBrickVariableSame() {
        val clonedBrick = clonedSprite!!.getScript(0).getBrick(0) as UserVariableBrickInterface
        val clonedVariable = clonedSprite!!.getUserVariable(VARIABLE_NAME)
        val clonedVariableFromBrick = clonedBrick.userVariable
        Assert.assertNotNull(clonedVariable)
        org.junit.Assert.assertSame(clonedVariable, clonedVariableFromBrick)
    }

    @Test
    fun testOriginalAndClonedVariableEquals() {
        val spriteVariable = sprite!!.getUserVariable(VARIABLE_NAME)
        val clonedBrick = clonedSprite!!.getScript(0).getBrick(0) as UserVariableBrickInterface
        val clonedVariableFromBrick = clonedBrick.userVariable
        Assert.assertEquals(spriteVariable, clonedVariableFromBrick)
    }

    @Test
    fun testOriginalAndClonedVariableNotSame() {
        val spriteVariable = sprite!!.getUserVariable(VARIABLE_NAME)
        val clonedVariable = clonedSprite!!.getUserVariable(VARIABLE_NAME)
        val clonedBrick = clonedSprite!!.getScript(0).getBrick(0) as UserVariableBrickInterface
        val clonedVariableFromBrick = clonedBrick.userVariable
        Assert.assertNotSame(spriteVariable, clonedVariable)
        Assert.assertNotSame(spriteVariable, clonedVariableFromBrick)
    }

    companion object {
        private const val VARIABLE_NAME = "test_variable"
        private val USER_VARIABLE = UserVariable(VARIABLE_NAME)
        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun data(): Iterable<Array<Any>> {
            return Arrays.asList(
                *arrayOf(
                    arrayOf("SetVariableBrick", SetVariableBrick()),
                    arrayOf("ChangeVariableBrick", ChangeVariableBrick()),
                    arrayOf("AskBrick", AskBrick()),
                    arrayOf("AskSpeechBrick", AskSpeechBrick()),
                    arrayOf("HideTextBrick", HideTextBrick()),
                    arrayOf("ShowTextBrick", ShowTextBrick()),
                    arrayOf(
                        "ShowTextColorSizeAlignmentBrick",
                        ShowTextColorSizeAlignmentBrick()
                    ),
                    arrayOf("WebRequestBrick", WebRequestBrick()),
                    arrayOf(
                        "ReadVariableFromDeviceBrick",
                        ReadVariableFromDeviceBrick()
                    ),
                    arrayOf(
                        "WriteVariableOnDeviceBrick",
                        WriteVariableOnDeviceBrick()
                    )
                )
            )
        }
    }
}

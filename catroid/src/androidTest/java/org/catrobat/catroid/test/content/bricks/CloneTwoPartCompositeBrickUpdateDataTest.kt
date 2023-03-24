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

import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.catrobat.catroid.content.bricks.CompositeBrick
import org.junit.Before
import kotlin.Throws
import org.catrobat.catroid.content.StartScript
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Scene
import org.catrobat.catroid.content.Script
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.test.content.bricks.CloneTwoPartCompositeBrickUpdateDataTest
import org.catrobat.catroid.content.bricks.UserVariableBrickWithFormula
import org.catrobat.catroid.content.bricks.SetVariableBrick
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.ui.recyclerview.controller.SpriteController
import org.catrobat.catroid.content.bricks.UserListBrick
import org.catrobat.catroid.content.bricks.AddItemToUserListBrick
import org.catrobat.catroid.formulaeditor.UserVariable
import org.catrobat.catroid.formulaeditor.UserList
import org.catrobat.catroid.content.bricks.IfThenLogicBeginBrick
import org.catrobat.catroid.content.bricks.ForeverBrick
import org.catrobat.catroid.content.bricks.RepeatBrick
import org.catrobat.catroid.content.bricks.RepeatUntilBrick
import org.junit.Assert
import org.junit.Test
import java.util.Arrays

@RunWith(Parameterized::class)
class CloneTwoPartCompositeBrickUpdateDataTest {
    @JvmField
    @Parameterized.Parameter
    var name: String? = null
    @JvmField
    @Parameterized.Parameter(1)
    var compositeBrickClass: Class<CompositeBrick>? = null
    private var sprite: Sprite? = null
    private var compositeBrick: CompositeBrick? = null
    @Before
    @Throws(IllegalAccessException::class, InstantiationException::class)
    fun setUp() {
        val project = Project()
        val scene = Scene()
        sprite = Sprite("test_sprite")
        scene.addSprite(sprite)
        val script: Script = StartScript()
        sprite!!.addScript(script)
        compositeBrick = compositeBrickClass!!.newInstance()
        script.addBrick(compositeBrick)
        project.addScene(scene)
        ProjectManager.getInstance().currentProject = project
    }

    @Test
    fun testUpdateUserVariable() {
        sprite!!.addUserVariable(USER_VARIABLE)
        val variableBrick: UserVariableBrickWithFormula =
            SetVariableBrick(Formula(0), USER_VARIABLE)
        compositeBrick!!.nestedBricks.add(variableBrick)
        val cloneSprite = SpriteController().copyForCloneBrick(sprite)
        val clonedCompositeBrick = cloneSprite.getScript(0).getBrick(0) as CompositeBrick
        val clonedUserVariableBrick =
            clonedCompositeBrick.nestedBricks[0] as UserVariableBrickWithFormula
        Assert.assertEquals(USER_VARIABLE, clonedUserVariableBrick.userVariable)
        Assert.assertNotSame(USER_VARIABLE, clonedUserVariableBrick.userVariable)
    }

    @Test
    fun testUpdateUserList() {
        sprite!!.addUserList(USER_LIST)
        val listBrick: UserListBrick = AddItemToUserListBrick(0.0)
        listBrick.userList = USER_LIST
        compositeBrick!!.nestedBricks.add(listBrick)
        val cloneSprite = SpriteController().copyForCloneBrick(sprite)
        val clonedCompositeBrick = cloneSprite.getScript(0).getBrick(0) as CompositeBrick
        val clonedUserVariableBrick = clonedCompositeBrick.nestedBricks[0] as UserListBrick
        Assert.assertEquals(USER_LIST, clonedUserVariableBrick.userList)
        Assert.assertNotSame(USER_LIST, clonedUserVariableBrick.userList)
    }

    companion object {
        private val USER_VARIABLE = UserVariable("variable")
        private val USER_LIST = UserList("list", Arrays.asList(*arrayOf<Any>("a", "b", "c")))
        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun data(): Iterable<Array<Any>> {
            return Arrays.asList(
                *arrayOf(
                    arrayOf(
                        IfThenLogicBeginBrick::class.java.simpleName,
                        IfThenLogicBeginBrick::class.java
                    ), arrayOf(
                        ForeverBrick::class.java.simpleName, ForeverBrick::class.java
                    ), arrayOf(
                        RepeatBrick::class.java.simpleName, RepeatBrick::class.java
                    ), arrayOf(
                        RepeatUntilBrick::class.java.simpleName, RepeatUntilBrick::class.java
                    )
                )
            )
        }
    }
}
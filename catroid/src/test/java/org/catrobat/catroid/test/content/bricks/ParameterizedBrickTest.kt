/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2018 The Catrobat Team
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

import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Scene
import org.catrobat.catroid.content.Script
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.WhenScript
import org.catrobat.catroid.content.bricks.ParameterizedBrick
import org.catrobat.catroid.formulaeditor.UserData
import org.catrobat.catroid.formulaeditor.UserList
import org.catrobat.catroid.formulaeditor.UserVariable
import org.catrobat.catroid.test.MockUtil
import org.catrobat.catroid.utils.UserDataUtil.renameUserData
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.util.ArrayList

class ParameterizedBrickTest {
    private var userList: UserList? = null
    private var userVariable: UserVariable? = null
    private var parameterizedBrick: ParameterizedBrick? = null

    @Before
    @Throws(IllegalAccessException::class, InstantiationException::class)
    fun setUp() {
        val project = Project(
            MockUtil.mockContextForProject(),
            "testProject"
        )
        userVariable = UserVariable()
        userList = UserList()
        val scene = Scene()
        val sprite = Sprite()
        val script: Script = WhenScript()
        parameterizedBrick = ParameterizedBrick()
        userVariable?.name = VARIABLE_NAME
        userList?.name = VARIABLE_NAME
        parameterizedBrick?.userLists?.add(userList!!)
        project.addUserVariable(userVariable)
        project.addUserList(userList)
        project.addScene(scene)
        scene.addSprite(sprite)
        sprite.addScript(script)
        script.addBrick(parameterizedBrick)
        ProjectManager.getInstance().currentProject = project
    }

    @Test
    fun testRenamingLinkedVariable() {
        renameUserData(userVariable as UserData<*>, NEW_VARIABLE_NAME)
        Assert.assertEquals(userList?.name, NEW_VARIABLE_NAME)
    }

    @Test
    fun testRenamingLinkedList() {
        renameUserData(userList as UserData<*>, NEW_VARIABLE_NAME)
        Assert.assertEquals(userVariable?.name, NEW_VARIABLE_NAME)
    }

    @Test
    fun testRemovingLinkedVariable() {
        val elements: MutableList<UserData<*>?> = ArrayList()
        elements.add(userVariable)
        ProjectManager.getInstance().currentProject.deselectElements(elements)
        Assert.assertFalse(parameterizedBrick?.userLists?.contains(userList) ?: true)
    }

    @Test
    fun testRemovingLinkedList() {
        val elements: MutableList<UserData<*>?> = ArrayList()
        elements.add(userList)
        ProjectManager.getInstance().currentProject.deselectElements(elements)
        Assert.assertFalse(parameterizedBrick?.userLists?.contains(userList) ?: true)
    }

    companion object {
        private const val VARIABLE_NAME = "Test"
        private const val NEW_VARIABLE_NAME = "NewName"
    }
}

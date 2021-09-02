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
package org.catrobat.catroid.test.content.actions

import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.content.ActionFactory
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.formulaeditor.UserList
import org.catrobat.catroid.test.MockUtil
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.util.ArrayList

@RunWith(JUnit4::class)
class AddItemToUserListActionTest {

    private lateinit var testSprite: Sprite
    private lateinit var project: Project
    private lateinit var userList: UserList
    private lateinit var actionFactory: ActionFactory

    companion object {
        private const val TEST_USERLIST_NAME = "testUserList"
        private const val DOUBLE_VALUE_ITEM_TO_ADD = 3.0
    }

    @Before
    @Throws(Exception::class)
    fun setUp() {
        actionFactory = ActionFactory()

        createProject()

        userList = UserList(TEST_USERLIST_NAME, getInitialUserListItems())
        project.addUserList(userList)
    }

    private fun createProject() {
        testSprite = Sprite("testSprite")
        project = Project(MockUtil.mockContextForProject(), "testProject")
        ProjectManager.getInstance().currentProject = project
        ProjectManager.getInstance().currentlyEditedScene = project.defaultScene
    }

    private fun getInitialUserListItems(): MutableList<Any> {
        val initialList: MutableList<Any> = ArrayList()
        initialList.add(1.0)
        initialList.add(2.0)
        return initialList
    }

    @Test
    fun testAddNumericalValueToUserList() {
        actionFactory.createAddItemToUserListAction(
            testSprite,
            SequenceAction(),
            Formula(DOUBLE_VALUE_ITEM_TO_ADD),
            userList
        ).act(1f)

        assertEquals(3, userList.value.size)
        assertEquals("3.0", userList.value.last())
    }

    @Test
    fun testAddItemWithInvalidUserList() {
        actionFactory.createAddItemToUserListAction(
            testSprite,
            SequenceAction(),
            Formula(DOUBLE_VALUE_ITEM_TO_ADD),
            null
        ).act(1f)

        assertEquals(2, userList.value.size)
    }

    @Test
    fun testAddNullFormula() {
        actionFactory.createAddItemToUserListAction(
            testSprite,
            SequenceAction(),
            null,
            userList
        ).act(1f)

        assertEquals(3, userList.value.size)
        assertEquals(0.0, userList.value.last())
    }

    @Test
    fun testNotANumberFormula() {
        actionFactory.createAddItemToUserListAction(
            testSprite,
            SequenceAction(),
            Formula(Double.NaN),
            userList
        ).act(1f)

        assertEquals(3, userList.value.size)
        assertEquals(Double.NaN.toString(), userList.value.last())
    }
}

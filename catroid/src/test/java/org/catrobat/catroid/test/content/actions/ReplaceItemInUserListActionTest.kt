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
import junit.framework.Assert
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.content.ActionFactory
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.formulaeditor.UserList
import org.catrobat.catroid.test.MockUtil
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class ReplaceItemInUserListActionTest {
    private lateinit var testSprite: Sprite
    private lateinit var project: Project
    private lateinit var userList: UserList
    private lateinit var actionFactory: ActionFactory

    @Before
    @Throws(Exception::class)
    fun setUp() {
        actionFactory = ActionFactory()
        testSprite = Sprite("testSprite")
        project = Project(MockUtil.mockContextForProject(), "testProject")
        ProjectManager.getInstance().currentProject = project
        INITIALIZED_LIST_VALUES.clear()
        INITIALIZED_LIST_VALUES.add(1.0)
        INITIALIZED_LIST_VALUES.add(2.0)
        INITIALIZED_LIST_VALUES.add(3.0)
        userList = UserList(TEST_USERLIST_NAME, INITIALIZED_LIST_VALUES)
        project.addUserList(userList)
    }

    @Test
    fun testReplaceNumericalValueInUserList() {
        actionFactory.createReplaceItemInUserListAction(
            testSprite, SequenceAction(), Formula(1), Formula(
                DOUBLE_VALUE_ITEM_TO_REPLACE_WITH
            ), userList
        ).act(1f)
        val firstItemOfUserList = userList.value[0]
        Assert.assertEquals(3, userList.value.size)
        Assert.assertEquals(DOUBLE_VALUE_ITEM_TO_REPLACE_WITH.toString(), firstItemOfUserList)
    }

    @Test
    fun testReplaceNumericalValueInUserListAtLastPosition() {
        actionFactory.createReplaceItemInUserListAction(
            testSprite, SequenceAction(), Formula(3), Formula(
                DOUBLE_VALUE_ITEM_TO_REPLACE_WITH
            ), userList
        ).act(1f)
        val lastItemOfUserList = userList.value[userList.value.size - 1]
        Assert.assertEquals(3, userList.value.size)
        Assert.assertEquals(DOUBLE_VALUE_ITEM_TO_REPLACE_WITH.toString(), lastItemOfUserList)
    }

    @Test
    fun testReplaceNumericalValueInUserListOutOfUserListBounds() {
        actionFactory.createReplaceItemInUserListAction(
            testSprite, SequenceAction(), Formula(4), Formula(
                DOUBLE_VALUE_ITEM_TO_REPLACE_WITH
            ), userList
        ).act(1f)
        Assert.assertEquals(3, userList.value.size)
        Assert.assertEquals(1.0, userList.value[0])
        Assert.assertEquals(2.0, userList.value[1])
        Assert.assertEquals(3.0, userList.value[2])
    }

    @Test
    fun testReplaceItemWithInvalidUserList() {
        actionFactory.createReplaceItemInUserListAction(
            testSprite, SequenceAction(), Formula(1), Formula(
                DOUBLE_VALUE_ITEM_TO_REPLACE_WITH
            ), null
        ).act(1f)
        Assert.assertEquals(3, userList.value.size)
    }

    @Test
    fun testReplaceNullFormula() {
        actionFactory.createReplaceItemInUserListAction(
            testSprite,
            SequenceAction(),
            Formula(1),
            null,
            userList
        ).act(1f)
        val firstItemOfUserList = userList.value[0]
        Assert.assertEquals(3, userList.value.size)
        Assert.assertEquals(0.0, firstItemOfUserList)
    }

    @Test
    fun testNotANumberFormula() {
        actionFactory.createReplaceItemInUserListAction(
            testSprite, SequenceAction(), Formula(1), Formula(
                Double.NaN
            ), userList
        ).act(1f)
        val firstItemOfUserList = userList.value[0]
        Assert.assertEquals(Double.NaN.toString(), firstItemOfUserList)
    }

    companion object {
        private const val TEST_USERLIST_NAME = "testUserList"
        private const val DOUBLE_VALUE_ITEM_TO_REPLACE_WITH = 4.0
        private val INITIALIZED_LIST_VALUES: MutableList<Any> = ArrayList()
    }
}
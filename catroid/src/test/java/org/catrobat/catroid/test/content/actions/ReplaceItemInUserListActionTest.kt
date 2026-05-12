/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2026 The Catrobat Team
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

@RunWith(JUnit4::class)
class ReplaceItemInUserListActionTest {

    private lateinit var testSprite: Sprite
    private lateinit var userList: UserList
    private lateinit var actionFactory: ActionFactory

    companion object {
        private const val DOUBLE_VALUE_ITEM_TO_REPLACE_WITH = 4.0
    }

    @Before
    @Throws(Exception::class)
    fun setUp() {
        actionFactory = ActionFactory()
        testSprite = Sprite("testSprite")
        val project = Project(MockUtil.mockContextForProject(), "testProject")
        ProjectManager.getInstance().currentProject = project

        val initializedListValues = mutableListOf<Any>(1.0, 2.0, 3.0)
        userList = UserList("testUserList", initializedListValues)
        project.addUserList(userList)
    }

    @Test
    fun testReplaceNumericalValueInUserList() {
        actionFactory.createReplaceItemInUserListAction(
            testSprite,
            SequenceAction(),
            Formula(1),
            Formula(DOUBLE_VALUE_ITEM_TO_REPLACE_WITH),
            userList
        ).act(1f)

        assertEquals(3, userList.value.size)
        assertEquals(DOUBLE_VALUE_ITEM_TO_REPLACE_WITH.toString(), userList.value[0])
        assertEquals(2.0, userList.value[1])
        assertEquals(3.0, userList.value[2])
    }

    @Test
    fun testReplaceNumericalValueInUserListAtLastPosition() {
        actionFactory.createReplaceItemInUserListAction(
            testSprite,
            SequenceAction(),
            Formula(3),
            Formula(DOUBLE_VALUE_ITEM_TO_REPLACE_WITH),
            userList
        ).act(1f)

        assertEquals(3, userList.value.size)
        assertEquals(1.0, userList.value[0])
        assertEquals(2.0, userList.value[1])
        assertEquals(DOUBLE_VALUE_ITEM_TO_REPLACE_WITH.toString(), userList.value[2])
    }

    @Test
    fun testReplaceNumericalValueInUserListOutOfUserListBounds() {
        actionFactory.createReplaceItemInUserListAction(
            testSprite,
            SequenceAction(),
            Formula(4),
            Formula(DOUBLE_VALUE_ITEM_TO_REPLACE_WITH),
            userList
        ).act(1f)

        assertEquals(3, userList.value.size)
        assertEquals(1.0, userList.value[0])
        assertEquals(2.0, userList.value[1])
        assertEquals(3.0, userList.value[2])
    }

    @Test
    fun testReplaceItemWithInvalidUserList() {
        actionFactory.createReplaceItemInUserListAction(
            testSprite,
            SequenceAction(),
            Formula(1),
            Formula(DOUBLE_VALUE_ITEM_TO_REPLACE_WITH),
            null
        ).act(1f)

        assertEquals(3, userList.value.size)
        assertEquals(1.0, userList.value[0])
        assertEquals(2.0, userList.value[1])
        assertEquals(3.0, userList.value[2])
    }

    @Test
    fun testReplaceNullFormula() {
        actionFactory.createReplaceItemInUserListAction(
            testSprite, SequenceAction(), Formula(1), null, userList
        ).act(1f)

        assertEquals(3, userList.value.size)
        assertEquals(0.0, userList.value[0])
        assertEquals(2.0, userList.value[1])
        assertEquals(3.0, userList.value[2])
    }

    @Test
    fun testNotANumberFormula() {
        val valueToReplaceWith = Double.NaN

        actionFactory.createReplaceItemInUserListAction(
            testSprite, SequenceAction(), Formula(1), Formula(valueToReplaceWith), userList
        ).act(1f)

        assertEquals(3, userList.value.size)
        assertEquals(valueToReplaceWith.toString(), userList.value[0])
        assertEquals(2.0, userList.value[1])
        assertEquals(3.0, userList.value[2])
    }

    @Test
    fun testReplaceFormulaAtNull() {
        actionFactory.createReplaceItemInUserListAction(
            testSprite, SequenceAction(), null, Formula(DOUBLE_VALUE_ITEM_TO_REPLACE_WITH), userList
        ).act(1f)

        assertEquals(3, userList.value.size)
        assertEquals(DOUBLE_VALUE_ITEM_TO_REPLACE_WITH.toString(), userList.value[0])
        assertEquals(2.0, userList.value[1])
        assertEquals(3.0, userList.value[2])
    }

    @Test
    fun testReplaceFormulaInterpretationException() {
        actionFactory.createReplaceItemInUserListAction(
            testSprite,
            SequenceAction(),
            Formula("Test"),
            Formula(DOUBLE_VALUE_ITEM_TO_REPLACE_WITH),
            userList
        ).act(1f)

        assertEquals(3, userList.value.size)
        assertEquals(DOUBLE_VALUE_ITEM_TO_REPLACE_WITH.toString(), userList.value[0])
        assertEquals(2.0, userList.value[1])
        assertEquals(3.0, userList.value[2])
    }
}

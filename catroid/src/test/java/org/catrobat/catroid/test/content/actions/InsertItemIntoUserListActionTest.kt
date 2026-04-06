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
class InsertItemIntoUserListActionTest {

    private lateinit var testSprite: Sprite
    private lateinit var userList: UserList
    private lateinit var actionFactory: ActionFactory

    companion object {
        private const val DOUBLE_VALUE_ITEM_TO_ADD = 3.0
    }

    @Before
    @Throws(Exception::class)
    fun setUp() {
        actionFactory = ActionFactory()
        testSprite = Sprite("testSprite")
        val project = Project(MockUtil.mockContextForProject(), "testProject")
        ProjectManager.getInstance().currentProject = project

        val initializedListValues = mutableListOf<Any>(1.0, 2.0)
        userList = UserList("testUserList", initializedListValues)
        project.addUserList(userList)
    }

    @Test
    fun testInsertNumericalValueToUserList() {
        actionFactory.createInsertItemIntoUserListAction(
            testSprite, SequenceAction(), Formula(1), Formula(DOUBLE_VALUE_ITEM_TO_ADD), userList
        ).act(1f)

        assertEquals(3, userList.value.size)
        assertEquals(DOUBLE_VALUE_ITEM_TO_ADD.toString(), userList.value[0])
        assertEquals(1.0, userList.value[1])
        assertEquals(2.0, userList.value[2])
    }

    @Test
    fun testInsertNumericalValueToUserListAtLastPosition() {
        actionFactory.createInsertItemIntoUserListAction(
            testSprite, SequenceAction(), Formula(3), Formula(DOUBLE_VALUE_ITEM_TO_ADD), userList
        ).act(1f)

        assertEquals(3, userList.value.size)
        assertEquals(1.0, userList.value[0])
        assertEquals(2.0, userList.value[1])
        assertEquals(DOUBLE_VALUE_ITEM_TO_ADD.toString(), userList.value[userList.value.size - 1])
    }

    @Test
    fun testInsertItemWithInvalidUserList() {
        actionFactory.createInsertItemIntoUserListAction(
            testSprite, SequenceAction(), Formula(1), Formula(DOUBLE_VALUE_ITEM_TO_ADD), null
        ).act(1f)

        assertEquals(2, userList.value.size)
        assertEquals(1.0, userList.value[0])
        assertEquals(2.0, userList.value[1])
    }

    @Test
    fun testInsertNullFormula() {
        actionFactory.createInsertItemIntoUserListAction(
            testSprite, SequenceAction(), Formula(1), null, userList
        ).act(1f)

        assertEquals(3, userList.value.size)
        assertEquals(0.0, userList.value[0])
        assertEquals(1.0, userList.value[1])
        assertEquals(2.0, userList.value[2])
    }

    @Test
    fun testNotANumberFormula() {
        actionFactory.createInsertItemIntoUserListAction(
            testSprite, SequenceAction(), Formula(1), Formula(Double.NaN), userList
        ).act(1f)

        assertEquals(3, userList.value.size)
        assertEquals(Double.NaN.toString(), userList.value[0])
        assertEquals(1.0, userList.value[1])
        assertEquals(2.0, userList.value[2])
    }

    @Test
    fun testInsertItemOutOfBoundsNegativeIndex() {
        actionFactory.createInsertItemIntoUserListAction(
            testSprite, SequenceAction(), Formula(-1), Formula(DOUBLE_VALUE_ITEM_TO_ADD), userList
        ).act(1f)

        assertEquals(2, userList.value.size)
        assertEquals(1.0, userList.value[0])
        assertEquals(2.0, userList.value[1])
    }

    @Test
    fun testInsertItemOutOfBoundsTooLargeIndex() {
        actionFactory.createInsertItemIntoUserListAction(
            testSprite, SequenceAction(), Formula(4), Formula(DOUBLE_VALUE_ITEM_TO_ADD), userList
        ).act(1f)

        assertEquals(2, userList.value.size)
        assertEquals(1.0, userList.value[0])
        assertEquals(2.0, userList.value[1])
    }

    @Test
    fun testInsertNullIndexFormula() {
        actionFactory.createInsertItemIntoUserListAction(
            testSprite, SequenceAction(), null, Formula(DOUBLE_VALUE_ITEM_TO_ADD), userList
        ).act(1f)

        assertEquals(3, userList.value.size)
        assertEquals(DOUBLE_VALUE_ITEM_TO_ADD.toString(), userList.value[0])
        assertEquals(1.0, userList.value[1])
        assertEquals(2.0, userList.value[2])
    }

    @Test
    fun testInsertFormulaInterpretationException() {
        actionFactory.createInsertItemIntoUserListAction(
            testSprite, SequenceAction(), Formula("invalidText"), Formula(DOUBLE_VALUE_ITEM_TO_ADD), userList
        ).act(1f)

        assertEquals(3, userList.value.size)
        assertEquals(DOUBLE_VALUE_ITEM_TO_ADD.toString(), userList.value[0])
        assertEquals(1.0, userList.value[1])
        assertEquals(2.0, userList.value[2])
    }

    @Test
    fun testInsertPreservesExistingItems() {
        actionFactory.createInsertItemIntoUserListAction(
            testSprite, SequenceAction(), Formula(2), Formula(DOUBLE_VALUE_ITEM_TO_ADD), userList
        ).act(1f)

        assertEquals(3, userList.value.size)
        assertEquals(1.0, userList.value[0])
        assertEquals(DOUBLE_VALUE_ITEM_TO_ADD.toString(), userList.value[1])
        assertEquals(2.0, userList.value[2])
    }
}

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
import org.catrobat.catroid.content.ActionFactory
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Scope
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.actions.DeleteItemOfUserListAction
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.formulaeditor.UserList
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
class DeleteItemOfUserListActionTest(
    private val testName: String,
    private val listIndexToDelete: List<Formula>,
    private var initList: List<Any>,
    private var expectedList: List<Any>
) {

    private lateinit var userList: UserList
    private lateinit var actionFactory: ActionFactory
    private lateinit var deleteaction: DeleteItemOfUserListAction

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun parameters() = listOf(
            arrayOf("DeleteFirstEntry", listOf(Formula(1)), mutableListOf(1, 2, 3), listOf(2, 3)),
            arrayOf("DeleteMiddleEntry", listOf(Formula(2)), mutableListOf(1, 2, 3), listOf(1, 3)),
            arrayOf("DeleteMultipleEntries", listOf(Formula(1), Formula(2)), mutableListOf(1, 2, 3), listOf(2)),
            arrayOf("DeleteAllEntries", listOf(Formula(1), Formula(1)), mutableListOf("First", 2), emptyList<Any>()),
            arrayOf("DeleteInvalidEntries", listOf(Formula(3), Formula(0), Formula(-1)), mutableListOf(1, 2), listOf(1, 2)),
            arrayOf("DeleteNotANumberEntry", listOf(Formula(Double.NaN)), mutableListOf(1, 2), listOf(1, 2)),
            arrayOf("DeleteEmptyList", listOf(Formula(1)), emptyList<Any>(), emptyList<Any>()))
    }

    @Before
    @Throws(Exception::class)
    fun setUp() {
        actionFactory = ActionFactory()
        userList = UserList(testName, initList)
        deleteaction = DeleteItemOfUserListAction()
    }

    @Test
    fun testDeleteItemOfUserList() {
        deleteaction.userList = userList
        for (indexToDelete in listIndexToDelete) {
            deleteaction.formulaIndexToDelete = indexToDelete
            deleteaction.scope = Scope(Project(), Sprite(), SequenceAction())
            deleteaction.act(1f)
            deleteaction.reset()
        }
        Assert.assertEquals(userList.value, UserList(testName, expectedList).value)
    }
}

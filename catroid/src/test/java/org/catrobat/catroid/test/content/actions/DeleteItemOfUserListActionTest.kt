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
package org.catrobat.catroid.test.content.actions

import junit.framework.Assert.assertEquals
import org.catrobat.catroid.content.actions.DeleteItemOfUserListAction
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.formulaeditor.UserList
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.util.ArrayList

@RunWith(Parameterized::class)
class DeleteItemOfUserListActionTest(
    private val name: String,
    private val deleteList: List<Formula?>?,
    private val expectedListEntries: Int?
) {
    private lateinit var action: DeleteItemOfUserListAction
    private lateinit var userList: UserList
    private lateinit var collection: ArrayList<Any>

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun parameters() = listOf(
            arrayOf("DELETE_SINGLE", listOf(Formula(1)), 3),
            arrayOf("DELETE_MULTIPLE", listOf(Formula(4), Formula(1)), 2),
            arrayOf("DELETE_ALL", listOf(Formula(4), Formula(1), Formula(2), Formula(1)), 0),
            arrayOf("NUMERICAL_STRING", listOf(Formula("1")), 3),
            arrayOf("NUMBER_NEGATIVE", listOf(Formula(-1)), 4),
            arrayOf("NUMBER_ZERO", listOf(Formula(0)), 4),
            arrayOf("NUMBER_TOO_LARGE", listOf(Formula(5)), 4),
            arrayOf("NON_NUMERICAL_STRING", listOf(Formula("Test")), 4),
            arrayOf("NULL", listOf(null), 4),
            arrayOf("NAN", listOf(Formula(Double.NaN)), 4)
        )
    }

    @Before
    fun setUp() {
        action = DeleteItemOfUserListAction()
        collection = arrayListOf(1.0, "2.0", "test", 4.0)
        userList = UserList("userList", collection)
        action.userList = userList
    }

    @Test
    fun testDeleteItemOfUserList() {
        for (idx in deleteList!!) {
            action.formulaIndexToDelete = idx
            action.act(1.0f)
            action.reset()
        }
        assertEquals(expectedListEntries, userList.value.size)
    }
}

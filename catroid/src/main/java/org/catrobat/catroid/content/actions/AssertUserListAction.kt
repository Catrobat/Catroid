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
package org.catrobat.catroid.content.actions

import org.catrobat.catroid.formulaeditor.UserList
import org.catrobat.catroid.formulaeditor.UserVariable
import kotlin.math.exp

class AssertUserListAction : AssertAction() {
    var actualUserList: Any? = null
    var expectedUserList: Any? = null
    var message: String = ""

    override fun act(delta: Float): Boolean {
        assertTitle = "\nAssertUserListError\n"
        message = ""

        if (actualUserList == null) {
            failWith("Actual list is null")
            return false
        }
        if (expectedUserList == null) {
            failWith("Expected list is null")
            return false
        }

        validateLists()

        return if (message.isEmpty()) {
            true
        } else {
            failWith(message)
            false
        }
    }

    private fun validateLists() {
        var actual: UserVariable? = null
        var expected: UserVariable? = null

        if (actualUserList is UserList) {
            actual = actualUserList as UserList
        } else if (actualUserList is UserVariable && (actualUserList as UserVariable).isList) {
            actual = actualUserList as UserVariable
        }
        if (expectedUserList is UserList) {
            expected = expectedUserList as UserList
        } else if (expectedUserList is UserVariable && (expectedUserList as UserVariable).isList) {
            expected = expectedUserList as UserVariable
        }

        if (actual == null || expected == null) {
            message = "The type of the variables is wrong\n" +
                "expected: ${expectedUserList}\n" +
                "actual: ${actualUserList}\n"
            return
        }

        if (actual.listSize != expected.listSize) {
            message = "The number of list elements are not equal\n" +
                "expected: ${expected.listSize} element/s\n" +
                "actual:   ${actual.listSize} element/s\n"
        }

        val actualList: ArrayList<Any> = actual.value as ArrayList<Any>
        val expectedList: ArrayList<Any> = expected.value as ArrayList<Any>

        for (listPosition in 0..minOf(expected.listSize - 1, actual.listSize - 1)) {
            if (!equalValues(
                    actualList[listPosition].toString(),
                    expectedList[listPosition].toString()
                )
            ) {
                message += formattedAssertError(
                    listPosition,
                    actualList[listPosition],
                    expectedList[listPosition]
                )
            }
        }
    }

    private fun formattedAssertError(
        listPosition: Int,
        actual: Any,
        expected: Any
    ): String {
        val indicator = generateIndicator(actual, expected)
        return "position: ${listPosition + 1}\n" +
            "expected: <$expected>\n" +
            "actual:   <$actual>\n" +
            "deviation: $indicator\n\n"
    }
}

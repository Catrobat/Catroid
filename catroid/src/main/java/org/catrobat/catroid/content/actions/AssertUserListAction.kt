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

class AssertUserListAction : AssertAction() {
    var actualUserList: UserList? = null
    var expectedUserList: UserList? = null
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
        val actualList = actualUserList!!.value
        val expectedList = expectedUserList!!.value
        if (actualList.size != expectedList.size) {
            message = "The number of list elements are not equal\n" +
                "expected: ${expectedList.size} element/s\n" +
                "actual:   ${actualList.size} element/s\n"
        }
        for (listPosition in 0..minOf(expectedList.size - 1, actualList.size - 1)) {
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

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

import org.catrobat.catroid.common.ParameterizedData
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.formulaeditor.UserList

class ParameterizedAssertAction : AssertAction() {
    var actualFormula: Formula? = null
    var expectedList: UserList? = null
    var parameterizedData: ParameterizedData? = null

    init {
        assertTitle = "\nParameterizedAssertError\n"
    }

    override fun act(delta: Float): Boolean {
        parameterizedData?.listSize = expectedList?.value?.size ?: 0

        if (actualFormula == null) {
            failWith("Actual is null")
            return false
        }

        if (expectedList == null) {
            failWith("Expected is null")
            return false
        }

        val actualValue = actualFormula?.interpretObject(scope).toString()
        val expectedValue =
            expectedList?.value?.get(parameterizedData?.currentPosition ?: 0) ?: "null"

        parameterizedData?.let { data ->
            if (!equalValues(actualValue, expectedValue.toString())) {
                data.failMessages.append("\n${data.currentParameters}\n").append(
                    formattedAssertEqualsError(actualValue, expectedValue))
            } else {
                data.successMessages.append("\n${data.currentParameters}\n").append(
                    formattedSuccessMessage(actualValue, expectedValue))
            }

            data.currentPosition++
            data.currentParameters = ""

            if (data.failMessages.isNotEmpty() &&
                data.currentPosition >= expectedList?.value?.size ?: 0) {
                    failWith("Failed Tests:\n${data.failMessages}\n\nSucceeded Tests:\n${data.successMessages}")
            }
        }

        return true
    }

    private fun formattedAssertEqualsError(actual: Any, expected: Any): String {
        val indicator = generateIndicator(actual, expected)
        return "expected: <$expected>\nactual:   <$actual>\ndeviation: $indicator\n"
    }

    private fun formattedSuccessMessage(actual: Any?, expected: Any?): String = "$actual == $expected\n"
}

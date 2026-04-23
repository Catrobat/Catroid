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

import com.badlogic.gdx.scenes.scene2d.Action
import org.catrobat.catroid.content.Scope
import org.catrobat.catroid.stage.StageActivity
import org.catrobat.catroid.stage.TestResult

abstract class AssertAction : Action() {
    var position: String? = null
    var scope: Scope? = null
    var assertTitle = "\nAssertError\n"

    protected fun failWith(message: String) {
        StageActivity.finishTestWithResult(
            TestResult(
                formattedPosition() +
                    assertTitle + message,
                TestResult.STAGE_ACTIVITY_TEST_FAIL
            )
        )
    }

    protected fun equalValues(actual: String, expected: String): Boolean {
        return try {
            actual == expected || actual.toDouble() == expected.toDouble()
        } catch (numberFormatException: NumberFormatException) {
            false
        }
    }

    private fun indexOfDifference(
        actual: CharSequence?,
        expected: CharSequence?
    ): Int {
        if (actual == null || expected == null) {
            return 0
        }
        var position = 0
        while (position < actual.length && position < expected.length) {
            if (actual[position] != expected[position]) {
                break
            }
            ++position
        }
        return position
    }

    protected fun generateIndicator(actual: Any, expected: Any): String {
        val errorPosition = indexOfDifference(actual.toString(), expected.toString())
        return String(CharArray(errorPosition)).replace('\u0000', '-') + "^"
    }

    private fun formattedPosition(): String {
        return "on sprite \"${scope?.sprite?.name}\"\n" +
            "$position\n"
    }
}

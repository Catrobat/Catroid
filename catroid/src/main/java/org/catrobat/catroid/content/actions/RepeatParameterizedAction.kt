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
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.formulaeditor.UserList
import org.catrobat.catroid.formulaeditor.UserVariable
import org.catrobat.catroid.stage.StageActivity
import org.catrobat.catroid.stage.TestResult

class RepeatParameterizedAction : LoopAction() {
    private var assertTitle = "ParameterizedInitialisationError"
    private var isCurrentLoopInitialized = false
    var position = ""
    var sprite: Sprite? = null
    var parameterizedData: ParameterizedData? = null
    var parameters: List<Pair<UserList, UserVariable>> = emptyList()

    override fun delegate(delta: Float): Boolean {
        if (parameters.isNullOrEmpty()) {
            fail("Input was not selected")
            return true
        }

        if (!isCurrentLoopInitialized) {
            if (parameterizedData?.currentPosition ?: 1 >= parameterizedData?.listSize ?: 0) {
                return true
            }
            if (initParameter()) {
                return true
            }

            currentTime = 0f
            isCurrentLoopInitialized = true
        }

        currentTime += delta
        if (action.act(delta) && !isLoopDelayNeeded()) {
            if (parameterizedData?.currentPosition ?: 1 >= parameterizedData?.listSize ?: 0) {
                return true
            }

            isCurrentLoopInitialized = false
            action?.restart()
        }
        return false
    }

    override fun restart() {
        isCurrentLoopInitialized = false
        parameterizedData?.reset()
        super.restart()
    }

    private fun fail(error: String) {
        StageActivity.finishTestWithResult(
            TestResult(
                "${formattedPosition()}\n\n$assertTitle\n$error",
                TestResult.STAGE_ACTIVITY_TEST_FAIL
            )
        )
    }

    private fun initParameter(): Boolean = parameterizedData?.let {
        it.currentParameters = "[${it.currentPosition + 1}] "

        for ((userList, userVariable) in parameters) {
            val data = userList.value
            if (data.size <= it.currentPosition) {
                fail(
                    "Input list is missing elements\n" +
                        "Failed Tests:\n${it.failMessages}\n\n" +
                        "Succeeded Tests:\n${it.successMessages}"
                )
                return@let true
            } else {
                userVariable.value = data[it.currentPosition]
                it.currentParameters += "${userVariable.name} = ${userVariable.value} | "
            }
        }
        it.currentParameters = it.currentParameters.removeSuffix(" | ")
        false
    } ?: true

    private fun formattedPosition(): String = "on sprite ${sprite?.name}\n$position"
}

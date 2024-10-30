/* Catroid: An on-device visual programming system for Android devices
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

package org.catrobat.catroid

import android.view.View
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import org.hamcrest.Matcher

class WaitForConditionAction(private val condition: Matcher<View>, private val timeoutMs: Long) :
    ViewAction {
    private val description: String = "WaitConditionAction"
    private val loopCount: Long = 100

    override fun getDescription(): String = this.description
    override fun getConstraints(): Matcher<View> = this.condition

    override fun perform(uiController: UiController?, view: View?) {
        uiController?.loopMainThreadUntilIdle()
        val endTime: Long = System.currentTimeMillis() + timeoutMs

        while (System.currentTimeMillis() < endTime) {
            if (condition.matches(view)) {
                return
            }

            uiController?.loopMainThreadForAtLeast(loopCount)
        }
    }

    companion object {
        @JvmStatic
        fun waitFor(condition: Matcher<View>, timeoutMs: Long): ViewAction =
            WaitForConditionAction(condition, timeoutMs)
    }
}

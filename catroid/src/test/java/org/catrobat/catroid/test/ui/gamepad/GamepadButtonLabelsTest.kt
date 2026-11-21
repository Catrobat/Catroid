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

package org.catrobat.catroid.test.ui.gamepad

import android.content.Context
import android.os.Build
import org.catrobat.catroid.gamepad.GamepadButton
import org.catrobat.catroid.ui.gamepad.labelResId
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class GamepadButtonLabelsTest {
    private val context: Context = RuntimeEnvironment.getApplication()

    @Test
    fun `every button has its label in button order`() {
        val labels = GamepadButton.entries.map { button -> context.getString(button.labelResId) }

        assertEquals(
            listOf(
                "A", "B", "X", "Y",
                "Up", "Down", "Left", "Right",
                "Start", "Select",
                "L1", "L2", "R1", "R2"
            ),
            labels
        )
    }
}

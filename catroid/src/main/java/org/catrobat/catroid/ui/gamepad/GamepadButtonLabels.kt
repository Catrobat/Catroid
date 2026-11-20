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
@file:JvmName("GamepadButtonLabels")

package org.catrobat.catroid.ui.gamepad

import androidx.annotation.StringRes
import org.catrobat.catroid.R
import org.catrobat.catroid.gamepad.GamepadButton

/** The string resource of the label that the user interface shows for this button. */
@get:StringRes
val GamepadButton.labelResId: Int
    get() = when (this) {
        GamepadButton.A -> R.string.gamepad_button_a
        GamepadButton.B -> R.string.gamepad_button_b
        GamepadButton.X -> R.string.gamepad_button_x
        GamepadButton.Y -> R.string.gamepad_button_y
        GamepadButton.UP -> R.string.gamepad_button_up
        GamepadButton.DOWN -> R.string.gamepad_button_down
        GamepadButton.LEFT -> R.string.gamepad_button_left
        GamepadButton.RIGHT -> R.string.gamepad_button_right
        GamepadButton.START -> R.string.gamepad_button_start
        GamepadButton.SELECT -> R.string.gamepad_button_select
        GamepadButton.L1 -> R.string.gamepad_button_l1
        GamepadButton.L2 -> R.string.gamepad_button_l2
        GamepadButton.R1 -> R.string.gamepad_button_r1
        GamepadButton.R2 -> R.string.gamepad_button_r2
    }

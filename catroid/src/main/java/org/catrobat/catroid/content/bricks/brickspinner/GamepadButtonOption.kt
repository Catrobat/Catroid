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

package org.catrobat.catroid.content.bricks.brickspinner

import android.content.Context
import org.catrobat.catroid.R
import org.catrobat.catroid.common.Nameable
import org.catrobat.catroid.gamepad.GamepadButton
import org.catrobat.catroid.ui.gamepad.labelResId

/**
 * A [BrickSpinner] entry that shows a localized label and carries the stable [buttonId] of a gamepad
 * button or of the choice "any button", so bricks can store the ID instead of the label.
 */
class GamepadButtonOption(private var label: String, val buttonId: String) : Nameable {

    override fun getName(): String = label

    override fun setName(name: String) {
        label = name
    }

    companion object {
        /** Creates one option per gamepad button, in the order in which buttons are offered. */
        @JvmStatic
        fun createAll(context: Context): List<GamepadButtonOption> =
            GamepadButton.entries.map { button ->
                GamepadButtonOption(context.getString(button.labelResId), button.name)
            }

        /** Creates the option "any button", followed by one option per gamepad button. */
        @JvmStatic
        fun createAllWithAny(context: Context): List<GamepadButtonOption> =
            listOf(GamepadButtonOption(context.getString(R.string.gamepad_button_any), GamepadButton.ANY_ID)) +
                createAll(context)
    }
}

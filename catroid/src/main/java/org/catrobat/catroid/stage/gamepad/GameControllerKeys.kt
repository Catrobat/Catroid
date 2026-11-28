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

package org.catrobat.catroid.stage.gamepad

import android.view.InputDevice
import android.view.KeyEvent
import org.catrobat.catroid.gamepad.GamepadButton

/**
 * Returns the [GamepadButton] that a game controller reports with the key of [event], before any
 * swap of A/B and X/Y, or `null` for a key without a [GamepadButton] and for a key that does not
 * come from a game controller. A key comes from a game controller if the event or its input
 * device has the gamepad or the joystick source.
 */
internal fun reportedGamepadButton(event: KeyEvent): GamepadButton? {
    val button = KEY_CODE_BUTTONS[event.keyCode] ?: return null
    return button.takeIf { isFromGameController(event) }
}

private fun isFromGameController(event: KeyEvent): Boolean {
    if (GAME_CONTROLLER_SOURCES.any(event::isFromSource)) {
        return true
    }
    val device = event.device ?: return false
    return GAME_CONTROLLER_SOURCES.any(device::supportsSource)
}

private val GAME_CONTROLLER_SOURCES = intArrayOf(InputDevice.SOURCE_GAMEPAD, InputDevice.SOURCE_JOYSTICK)

private val KEY_CODE_BUTTONS = mapOf(
    KeyEvent.KEYCODE_BUTTON_A to GamepadButton.A,
    KeyEvent.KEYCODE_BUTTON_B to GamepadButton.B,
    KeyEvent.KEYCODE_BUTTON_X to GamepadButton.X,
    KeyEvent.KEYCODE_BUTTON_Y to GamepadButton.Y,
    KeyEvent.KEYCODE_DPAD_UP to GamepadButton.UP,
    KeyEvent.KEYCODE_DPAD_DOWN to GamepadButton.DOWN,
    KeyEvent.KEYCODE_DPAD_LEFT to GamepadButton.LEFT,
    KeyEvent.KEYCODE_DPAD_RIGHT to GamepadButton.RIGHT,
    KeyEvent.KEYCODE_BUTTON_START to GamepadButton.START,
    KeyEvent.KEYCODE_BUTTON_SELECT to GamepadButton.SELECT,
    KeyEvent.KEYCODE_BUTTON_L1 to GamepadButton.L1,
    KeyEvent.KEYCODE_BUTTON_L2 to GamepadButton.L2,
    KeyEvent.KEYCODE_BUTTON_R1 to GamepadButton.R1,
    KeyEvent.KEYCODE_BUTTON_R2 to GamepadButton.R2
)

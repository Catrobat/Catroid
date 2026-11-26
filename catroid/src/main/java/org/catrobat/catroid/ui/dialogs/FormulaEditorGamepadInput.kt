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

package org.catrobat.catroid.ui.dialogs

import android.hardware.input.InputManager
import android.view.KeyEvent
import android.view.MotionEvent
import org.catrobat.catroid.formulaeditor.formulaEditorGamepadInput
import org.catrobat.catroid.gamepad.GamepadButton
import org.catrobat.catroid.gamepad.GamepadInput
import org.catrobat.catroid.stage.gamepad.AndroidGamepadInputAdapter

/**
 * Receives the input of a physical game controller for the formula editor's Compute dialog, so the
 * gamepad button sensors show live values without a running stage.
 *
 * Only the input of [usedButtons] is consumed. Android handles all other controller input as usual,
 * so an unused B still closes the dialog like Back. While started, the gamepad button sensors read
 * this input, see [formulaEditorGamepadInput]. Call all methods from the main thread.
 *
 * @param onChanged is called after controller input may have changed a pressed button
 */
class FormulaEditorGamepadInput(
    usedButtons: Set<GamepadButton>,
    swapsFaceButtons: Boolean = false,
    pressBack: () -> Unit = {},
    private val onChanged: () -> Unit
) : InputManager.InputDeviceListener {

    private val input = GamepadInput()
    private val adapter = AndroidGamepadInputAdapter(
        gamepadInput = { input },
        isButtonHandled = { button -> button in usedButtons },
        swapsFaceButtons = swapsFaceButtons,
        pressBack = pressBack
    )
    private var inputManager: InputManager? = null

    /** Lets the gamepad button sensors read this input and listens for removed controllers. */
    fun start(inputManager: InputManager?) {
        formulaEditorGamepadInput = input
        this.inputManager = inputManager
        inputManager?.registerInputDeviceListener(this, null)
    }

    /** Releases every button, stops listening and lets the sensors read the stage input again. */
    fun stop() {
        inputManager?.unregisterInputDeviceListener(this)
        inputManager = null
        input.reset()
        if (formulaEditorGamepadInput === input) {
            formulaEditorGamepadInput = null
        }
    }

    /** @return true if the event is consumed, false if Android should handle it */
    fun dispatchKeyEvent(event: KeyEvent): Boolean =
        adapter.dispatchKeyEvent(event).also { consumed -> if (consumed) applyInput() }

    /** @return true if the event is consumed, false if Android should handle it */
    fun dispatchGenericMotionEvent(event: MotionEvent): Boolean =
        adapter.dispatchGenericMotionEvent(event).also { consumed -> if (consumed) applyInput() }

    /** Releases every button, for example when the dialog loses the focus. */
    fun releaseAll() {
        input.releaseAll()
        applyInput()
    }

    override fun onInputDeviceAdded(deviceId: Int) = Unit

    override fun onInputDeviceChanged(deviceId: Int) = Unit

    /**
     * Releases every button. The removed device can no longer be identified as a controller, and
     * with a single supported controller any removal may be the controller.
     */
    override fun onInputDeviceRemoved(deviceId: Int) = releaseAll()

    private fun applyInput() {
        input.applyPendingInput { }
        onChanged()
    }
}

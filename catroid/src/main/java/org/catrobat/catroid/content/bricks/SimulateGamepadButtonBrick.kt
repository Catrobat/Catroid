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

package org.catrobat.catroid.content.bricks

import android.content.Context
import android.view.View
import androidx.annotation.IdRes
import org.catrobat.catroid.R
import org.catrobat.catroid.common.Nameable
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.actions.ScriptSequenceAction
import org.catrobat.catroid.content.bricks.brickspinner.BrickSpinner
import org.catrobat.catroid.content.bricks.brickspinner.GamepadButtonOption
import org.catrobat.catroid.gamepad.GamepadButton

/**
 * Testing brick that simulates pressing or releasing a physical gamepad button, so tests can
 * drive gamepad scripts and sensors without a controller.
 *
 * @property button the stable ID of the simulated button, see [GamepadButton.fromId]
 * @property pressed whether the button is pressed (true) or released (false)
 */
class SimulateGamepadButtonBrick(
    var button: String = GamepadButton.A.name,
    var pressed: Boolean = true
) : BrickBaseType(), BrickSpinner.OnItemSelectedListener<Nameable> {

    override fun getViewResource(): Int = R.layout.brick_simulate_gamepad_button

    override fun getView(context: Context): View {
        super.getView(context)

        val buttonOptions = GamepadButtonOption.createAll(context)
        createSpinner(
            R.id.brick_simulate_gamepad_button_spinner,
            buttonOptions,
            buttonOptions.firstOrNull { it.button.name == button }
        )

        val stateOptions = listOf(
            PressedStateOption(context.getString(R.string.gamepad_button_state_pressed), true),
            PressedStateOption(context.getString(R.string.gamepad_button_state_released), false)
        )
        createSpinner(
            R.id.brick_simulate_gamepad_button_state_spinner,
            stateOptions,
            stateOptions.firstOrNull { it.pressed == pressed }
        )

        return view
    }

    private fun createSpinner(@IdRes spinnerId: Int, options: List<Nameable>, selection: Nameable?) {
        BrickSpinner<Nameable>(spinnerId, view, options.toMutableList()).apply {
            setOnItemSelectedListener(this@SimulateGamepadButtonBrick)
            setSelection(selection)
        }
    }

    override fun addActionToSequence(sprite: Sprite, sequence: ScriptSequenceAction) {
        sequence.addAction(sprite.actionFactory.createSimulateGamepadButtonAction(button, pressed))
    }

    override fun onItemSelected(spinnerId: Int?, item: Nameable?) {
        when (item) {
            is GamepadButtonOption -> button = item.button.name
            is PressedStateOption -> pressed = item.pressed
        }
    }

    override fun onNewOptionSelected(spinnerId: Int?) = Unit

    override fun onEditOptionSelected(spinnerId: Int?) = Unit

    override fun onStringOptionSelected(spinnerId: Int?, string: String?) = Unit

    private class PressedStateOption(private var label: String, val pressed: Boolean) : Nameable {

        override fun getName(): String = label

        override fun setName(name: String) {
            label = name
        }
    }

    companion object {
        private const val serialVersionUID = 1L
    }
}

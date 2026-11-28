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
import org.catrobat.catroid.R
import org.catrobat.catroid.common.Nameable
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.WhenGamepadButtonPressedScript
import org.catrobat.catroid.content.actions.ScriptSequenceAction
import org.catrobat.catroid.content.bricks.brickspinner.BrickSpinner
import org.catrobat.catroid.content.bricks.brickspinner.GamepadButtonOption
import org.catrobat.catroid.gamepad.GamepadButton

/**
 * Device event brick "When gamepad button [A] pressed" that represents a
 * [WhenGamepadButtonPressedScript]. Its spinner offers "any" and the localized labels of all gamepad
 * buttons and stores the stable ID of the selected choice in the script.
 */
class WhenGamepadButtonPressedBrick(
    private var script: WhenGamepadButtonPressedScript
) : ScriptBrickBaseType(), BrickSpinner.OnItemSelectedListener<GamepadButtonOption> {

    /** Creates the brick together with a new script for button A. */
    constructor() : this(WhenGamepadButtonPressedScript())

    init {
        script.scriptBrick = this
        commentedOut = script.isCommentedOut
    }

    override fun getScript(): WhenGamepadButtonPressedScript = script

    override fun clone(): Brick {
        val clone = super.clone() as WhenGamepadButtonPressedBrick
        val clonedScript = script.clone() as WhenGamepadButtonPressedScript
        clonedScript.scriptBrick = clone
        clone.script = clonedScript
        return clone
    }

    override fun getViewResource(): Int = R.layout.brick_when_gamepad_button_pressed

    override fun getView(context: Context): View {
        super.getView(context)
        val buttonOptions = GamepadButtonOption.createAllWithAny(context)
        BrickSpinner<GamepadButtonOption>(
            R.id.brick_when_gamepad_button_pressed_spinner,
            view,
            buttonOptions.toMutableList<Nameable>()
        ).apply {
            setOnItemSelectedListener(this@WhenGamepadButtonPressedBrick)
            setSelection(
                buttonOptions.firstOrNull { it.buttonId == script.button }
                    ?: buttonOptions.firstOrNull { it.buttonId == GamepadButton.A.name }
            )
        }
        return view
    }

    override fun onItemSelected(spinnerId: Int?, item: GamepadButtonOption?) {
        item?.let { script.button = it.buttonId }
    }

    override fun onNewOptionSelected(spinnerId: Int?) = Unit

    override fun onEditOptionSelected(spinnerId: Int?) = Unit

    override fun onStringOptionSelected(spinnerId: Int?, string: String?) = Unit

    override fun addActionToSequence(sprite: Sprite, sequence: ScriptSequenceAction) = Unit

    companion object {
        private const val serialVersionUID = 1L
    }
}

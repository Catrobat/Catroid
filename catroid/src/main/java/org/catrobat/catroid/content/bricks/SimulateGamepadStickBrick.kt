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
import org.catrobat.catroid.content.actions.ScriptSequenceAction
import org.catrobat.catroid.content.bricks.Brick.BrickField
import org.catrobat.catroid.content.bricks.brickspinner.BrickSpinner
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.gamepad.GamepadStick

/**
 * Testing brick "Simulate gamepad stick [left] x: [0] y: [0]": moves an analog stick of the running
 * stage to a position, like a real controller, so that CLTs can check the stick sensors. Each axis
 * goes from -100 to 100, right and up positive; larger values are limited. It stores the stable ID
 * of the stick, see [GamepadStick.fromId].
 */
class SimulateGamepadStickBrick @JvmOverloads constructor(
    var stick: String = GamepadStick.LEFT.name,
    x: Formula = Formula(0),
    y: Formula = Formula(0)
) : FormulaBrick(), BrickSpinner.OnItemSelectedListener<Nameable> {

    init {
        addAllowedBrickField(BrickField.X_POSITION, R.id.brick_simulate_gamepad_stick_edit_text_x)
        addAllowedBrickField(BrickField.Y_POSITION, R.id.brick_simulate_gamepad_stick_edit_text_y)
        setFormulaWithBrickField(BrickField.X_POSITION, x)
        setFormulaWithBrickField(BrickField.Y_POSITION, y)
    }

    override fun getViewResource(): Int = R.layout.brick_simulate_gamepad_stick

    override fun getDefaultBrickField(): Brick.FormulaField = BrickField.X_POSITION

    override fun getView(context: Context): View {
        super.getView(context)
        val options = listOf(
            StickOption(context.getString(R.string.gamepad_stick_left), GamepadStick.LEFT.name),
            StickOption(context.getString(R.string.gamepad_stick_right), GamepadStick.RIGHT.name)
        )
        BrickSpinner<Nameable>(R.id.brick_simulate_gamepad_stick_spinner, view, options.toMutableList<Nameable>()).apply {
            setOnItemSelectedListener(this@SimulateGamepadStickBrick)
            setSelection(options.firstOrNull { it.stickId == stick } ?: options.first())
        }
        return view
    }

    override fun addActionToSequence(sprite: Sprite, sequence: ScriptSequenceAction) {
        sequence.addAction(
            sprite.actionFactory.createSimulateGamepadStickAction(
                sprite, sequence, stick,
                getFormulaWithBrickField(BrickField.X_POSITION),
                getFormulaWithBrickField(BrickField.Y_POSITION)
            )
        )
    }

    override fun onItemSelected(spinnerId: Int?, item: Nameable?) {
        (item as? StickOption)?.let { stick = it.stickId }
    }

    override fun onNewOptionSelected(spinnerId: Int?) = Unit

    override fun onEditOptionSelected(spinnerId: Int?) = Unit

    override fun onStringOptionSelected(spinnerId: Int?, string: String?) = Unit

    private class StickOption(private var label: String, val stickId: String) : Nameable {

        override fun getName(): String = label

        override fun setName(name: String) {
            label = name
        }
    }

    companion object {
        private const val serialVersionUID = 1L
    }
}

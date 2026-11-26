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

package org.catrobat.catroid.content.actions

import android.util.Log
import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction
import org.catrobat.catroid.content.Scope
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.formulaeditor.InterpretationException
import org.catrobat.catroid.gamepad.GamepadStick
import org.catrobat.catroid.stage.StageActivity

/**
 * Moves an analog stick of the running stage to the position of its formulas, see
 * [org.catrobat.catroid.content.bricks.SimulateGamepadStickBrick]. The position takes effect at
 * once. A formula that cannot be interpreted counts as 0.
 */
class SimulateGamepadStickAction : TemporalAction() {

    var scope: Scope? = null

    /** The stable ID of the stick, see [GamepadStick.fromId]. */
    var stick: String? = null

    var x: Formula? = null

    var y: Formula? = null

    override fun update(percent: Float) {
        val gamepadStick = GamepadStick.fromId(stick) ?: return
        val gamepadInput = StageActivity.stageListener?.gamepadInput ?: return
        gamepadInput.setStickPosition(gamepadStick, interpret(x), interpret(y))
    }

    private fun interpret(formula: Formula?): Double =
        try {
            formula?.interpretDouble(scope) ?: 0.0
        } catch (exception: InterpretationException) {
            Log.d(TAG, "Formula interpretation for a gamepad stick failed.", exception)
            0.0
        }

    private companion object {
        val TAG: String = SimulateGamepadStickAction::class.java.simpleName
    }
}

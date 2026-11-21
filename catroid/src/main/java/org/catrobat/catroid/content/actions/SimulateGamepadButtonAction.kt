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

import com.badlogic.gdx.scenes.scene2d.Action
import org.catrobat.catroid.gamepad.GamepadButton
import org.catrobat.catroid.gamepad.GamepadInput
import org.catrobat.catroid.gamepad.GamepadInputSource
import org.catrobat.catroid.stage.StageActivity

/**
 * Simulates that a gamepad [button] is pressed or released, like a physical controller would.
 *
 * The first [act] queues the change on the stage's [GamepadInput]. The action finishes once the
 * stage has applied the change at the next frame boundary, so a script that waits for the action
 * already sees the new button state and the started gamepad scripts. Without a stage or for an
 * unknown button, the action finishes immediately without any effect. [restart] lets a looped
 * action queue its change again.
 */
class SimulateGamepadButtonAction : Action() {

    /** The stable ID of the button, see [GamepadButton.fromId]. */
    var button: String? = null

    var pressed: Boolean = true

    private var queuedChange: QueuedChange? = null

    override fun act(delta: Float): Boolean {
        val change = queuedChange ?: queueChange() ?: return true
        queuedChange = change
        return change.isApplied()
    }

    override fun restart() {
        queuedChange = null
    }

    private fun queueChange(): QueuedChange? {
        val gamepadButton = GamepadButton.fromId(button) ?: return null
        val gamepadInput = StageActivity.stageListener?.gamepadInput ?: return null
        val sequence =
            gamepadInput.setButtonState(gamepadButton, GamepadInputSource.SIMULATED, pressed)
        return QueuedChange(gamepadInput, sequence)
    }

    private class QueuedChange(private val gamepadInput: GamepadInput, private val sequence: Long) {
        fun isApplied(): Boolean = gamepadInput.isApplied(sequence)
    }
}

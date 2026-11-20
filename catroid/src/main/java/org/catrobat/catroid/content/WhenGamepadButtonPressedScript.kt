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

package org.catrobat.catroid.content

import org.catrobat.catroid.content.bricks.ScriptBrick
import org.catrobat.catroid.content.bricks.WhenGamepadButtonPressedBrick
import org.catrobat.catroid.content.eventids.EventId
import org.catrobat.catroid.content.eventids.GamepadButtonPressedEventId
import org.catrobat.catroid.gamepad.GamepadButton

/**
 * Script that starts whenever [button] of a physical gamepad changes from released to pressed.
 *
 * @property button the stable ID of the button, see [GamepadButton.fromId]. An unknown ID, for
 * example from a newer Catroid version, is kept as it is, but never starts the script.
 */
class WhenGamepadButtonPressedScript(
    var button: String = GamepadButton.A.name
) : Script() {

    /** Returns the linked brick. A new brick links itself to this script when it is created. */
    override fun getScriptBrick(): ScriptBrick =
        scriptBrick ?: WhenGamepadButtonPressedBrick(this)

    /** Returns the pressed event id of [button], or an id that no fired event matches. */
    override fun createEventId(sprite: Sprite): EventId =
        GamepadButton.fromId(button)?.let { GamepadButtonPressedEventId(it) } ?: NeverFiredEventId()

    /** An event id that only equals itself, so no fired event ever starts the script. */
    private class NeverFiredEventId : EventId() {
        override fun equals(other: Any?): Boolean = this === other

        override fun hashCode(): Int = System.identityHashCode(this)
    }

    companion object {
        private const val serialVersionUID = 1L
    }
}

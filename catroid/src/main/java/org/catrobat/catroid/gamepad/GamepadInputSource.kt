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

package org.catrobat.catroid.gamepad

/**
 * The kinds of input that can hold a [GamepadButton] down.
 *
 * A button counts as pressed while at least one source holds it. This way a D-pad that reports
 * both key events and hat axis motion, or a trigger that reports both a key and an axis, still
 * produces a single press.
 */
enum class GamepadInputSource {
    /** A key event of the controller, for example the A button or a D-pad direction. */
    KEY,

    /** The hat axis motion that some controllers report for their D-pad. */
    HAT_AXIS,

    /** The analog axis of a trigger that is pulled beyond the press threshold. */
    TRIGGER_AXIS,

    /** A press or release simulated by the project itself, for example by a testing brick. */
    SIMULATED
}

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
 * The logical buttons of a single physical gamepad, independent of any platform input API.
 *
 * The entry [name] is the stable ID that projects store, so entries must never be renamed. The
 * declaration order is the order in which the buttons are offered to the user.
 */
enum class GamepadButton {
    A, B, X, Y,
    UP, DOWN, LEFT, RIGHT,
    START, SELECT,
    L1, L2, R1, R2;

    companion object {
        /**
         * Returns the button whose stable ID is exactly [id] (case-sensitive, not trimmed), or
         * `null` if [id] is `null` or unknown.
         */
        @JvmStatic
        fun fromId(id: String?): GamepadButton? = entries.firstOrNull { it.name == id }
    }
}

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

/** An analog stick of a physical gamepad. Its name is the stable ID stored in projects. */
enum class GamepadStick {
    LEFT, RIGHT;

    companion object {
        /**
         * Returns the stick whose stable ID is exactly [id] (case-sensitive, not trimmed), or `null`
         * if [id] is `null` or unknown.
         */
        @JvmStatic
        fun fromId(id: String?): GamepadStick? = entries.firstOrNull { it.name == id }
    }
}

/**
 * The position of an analog stick: each axis from -[MAX_DEFLECTION] to [MAX_DEFLECTION], with
 * right and up positive like the coordinates of the stage.
 */
data class GamepadStickPosition(val x: Double, val y: Double) {

    companion object {
        const val MAX_DEFLECTION = 100.0

        @JvmField
        val CENTER = GamepadStickPosition(0.0, 0.0)

        /** Returns the position, with each axis limited to the range and a missing number as 0. */
        @JvmStatic
        fun limited(x: Double, y: Double): GamepadStickPosition =
            GamepadStickPosition(limit(x), limit(y))

        private fun limit(value: Double): Double =
            if (value.isNaN()) 0.0 else value.coerceIn(-MAX_DEFLECTION, MAX_DEFLECTION) + 0.0
    }
}

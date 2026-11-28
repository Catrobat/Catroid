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

package org.catrobat.catroid.stage.gamepad

import android.view.KeyEvent
import androidx.annotation.StringRes
import org.catrobat.catroid.R
import org.catrobat.catroid.gamepad.GamepadButton

/**
 * How a game controller reports its face buttons, found from one press of the button labeled A.
 * A controller that reports this button as B reports A and B, and X and Y, the other way round,
 * like a controller with Nintendo labels that reports the button positions of an Xbox
 * controller, so it needs the swap of A/B and X/Y.
 *
 * @property swapsFaceButtons whether the controller needs the swap of A/B and X/Y
 * @property detectedMessage tells the user what was detected
 */
enum class GamepadFaceButtonLayout(
    val swapsFaceButtons: Boolean,
    @StringRes val detectedMessage: Int
) {
    /** The button labeled A reports A. */
    AS_LABELED(false, R.string.gamepad_face_buttons_detected_as_labeled),

    /** The button labeled A reports B. */
    SWAPPED(true, R.string.gamepad_face_buttons_detected_swapped);

    companion object {
        /**
         * Returns the layout that [event] shows as the release of the button labeled A:
         * [AS_LABELED] for a reported A, [SWAPPED] for a reported B, and `null` for every other
         * event, such as a key down, another button or a key that does not come from a game
         * controller.
         */
        @JvmStatic
        fun detectedBy(event: KeyEvent): GamepadFaceButtonLayout? {
            if (event.action != KeyEvent.ACTION_UP) {
                return null
            }
            return when (reportedGamepadButton(event)) {
                GamepadButton.A -> AS_LABELED
                GamepadButton.B -> SWAPPED
                else -> null
            }
        }
    }
}

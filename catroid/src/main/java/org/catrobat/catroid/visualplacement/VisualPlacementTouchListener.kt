/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2023 The Catrobat Team
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
package org.catrobat.catroid.visualplacement

import android.view.MotionEvent
import android.view.MotionEvent.ACTION_CANCEL
import android.view.MotionEvent.ACTION_DOWN
import android.view.MotionEvent.ACTION_MOVE
import android.view.MotionEvent.ACTION_UP
import org.catrobat.catroid.utils.AndroidCoordinates

class VisualPlacementTouchListener {
    private var mode: Mode? = null
    private var previousCoordinates: AndroidCoordinates? = null

    fun onTouch(
        caller: VisualPlacementViewModel,
        currentPosition: AndroidCoordinates,
        event: MotionEvent,
    ): Boolean {

        if (event.getPointerId(0) != 0) return false
        val action = event.action
        val eventCoordinates = AndroidCoordinates(event.rawX, event.rawY)

        val newPosition: AndroidCoordinates = when {
            action == ACTION_DOWN -> {
                mode = Mode.TAP
                currentPosition
            }

            mode == Mode.TAP && (action == ACTION_CANCEL || action == ACTION_UP) -> eventCoordinates

            action == ACTION_MOVE || mode == Mode.MOVE && (action == ACTION_CANCEL || action == ACTION_UP) -> {
                mode = Mode.MOVE
                currentPosition + (eventCoordinates - (previousCoordinates ?: eventCoordinates))
            }

            else -> throw IllegalArgumentException("Unexpected Touch Event")
        }

        previousCoordinates = eventCoordinates
        caller.setCoordinates(newPosition)
        return true
    }

    private enum class Mode {
        MOVE, TAP
    }
}

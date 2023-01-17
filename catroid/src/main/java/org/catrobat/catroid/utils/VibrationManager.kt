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
package org.catrobat.catroid.utils

import android.os.SystemClock
import android.os.Vibrator

class VibrationManager {
    lateinit var vibration: Vibrator
    var startTime: Long = 0L
    var timeToVibrate: Long = 0L

    fun vibrateFor(duration: Long) {
        if (newVibrationLongerThanCurrent(duration)) {
            cancelVibrationIfHappening()
            startTime = SystemClock.uptimeMillis()
            timeToVibrate = duration
            vibration.vibrate(timeToVibrate)
        }
    }

    private fun cancelVibrationIfHappening() {
        if (SystemClock.uptimeMillis() < startTime + timeToVibrate) {
            vibration.cancel()
        }
    }

    private fun newVibrationLongerThanCurrent(duration: Long) =
        SystemClock.uptimeMillis() + duration > startTime + timeToVibrate

    private fun calcRemainingTime(): Long = startTime + timeToVibrate - SystemClock.uptimeMillis()

    fun hasActiveVibration(): Boolean = calcRemainingTime() > 0.0

    fun pause() {
        if (hasActiveVibration()) {
            timeToVibrate = calcRemainingTime()
            vibration.cancel()
            startTime = -1
        } else {
            reset()
        }
    }

    fun resume() {
        if (timeToVibrate > 0) {
            startTime = SystemClock.uptimeMillis()
            vibration.vibrate(timeToVibrate)
        }
    }

    fun reset() {
        startTime = 0
        timeToVibrate = 0
    }
}

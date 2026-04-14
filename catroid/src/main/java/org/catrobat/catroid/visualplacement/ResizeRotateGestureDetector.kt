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

package org.catrobat.catroid.visualplacement

import android.view.MotionEvent
import kotlin.math.atan2
import kotlin.math.sqrt

class ResizeRotateGestureDetector(private val listener: OnTransformGestureListener) {

    interface OnTransformGestureListener {
        fun onScale(scaleFactor: Float)
        fun onRotate(rotationDegrees: Float)
        fun onPan(dx: Float, dy: Float)
    }

    var isTransforming: Boolean = false
        private set

    var cumulativeScale: Float = 1.0f
    var cumulativeRotation: Float = 0.0f

    private var initialDistance: Float = 0f
    private var initialAngle: Float = 0f
    private var previousMidpointX: Float = 0f
    private var previousMidpointY: Float = 0f

    private var lastRotationTime: Long = 0
    private var lastRotationAngle: Float = 0f

    fun onTouchEvent(event: MotionEvent): Boolean {
        val pointerCount = event.pointerCount

        if (pointerCount < 2) {
            if (isTransforming) {
                isTransforming = false
            }
            return false
        }

        val x0 = event.getX(0)
        val y0 = event.getY(0)
        val x1 = event.getX(1)
        val y1 = event.getY(1)

        val currentDistance = calculateDistance(x0, y0, x1, y1)
        val currentAngle = calculateAngle(x0, y0, x1, y1)
        val currentMidpointX = (x0 + x1) / 2f
        val currentMidpointY = (y0 + y1) / 2f

        when (event.actionMasked) {
            MotionEvent.ACTION_POINTER_DOWN -> {
                isTransforming = true
                initialDistance = currentDistance
                initialAngle = currentAngle
                previousMidpointX = currentMidpointX
                previousMidpointY = currentMidpointY
                lastRotationTime = System.currentTimeMillis()
                lastRotationAngle = currentAngle
                return true
            }

            MotionEvent.ACTION_MOVE ->
                if (isTransforming && initialDistance > 0) {
                    val scaleFactor = currentDistance / initialDistance
                    val newScale = (cumulativeScale * scaleFactor)
                        .coerceIn(MIN_SCALE, MAX_SCALE)
                    listener.onScale(newScale)

                    val angleDelta = normalizeAngle(currentAngle - initialAngle)
                    var totalRotation = cumulativeRotation + angleDelta

                    val currentTime = System.currentTimeMillis()
                    val deltaTime = currentTime - lastRotationTime
                    if (deltaTime > 0) {
                        val rotationVelocity = Math.abs(currentAngle - lastRotationAngle) / deltaTime
                        if (rotationVelocity > SNAP_VELOCITY_THRESHOLD) {
                            totalRotation = getSnappedAngle(totalRotation)
                        }
                    }
                    lastRotationTime = currentTime
                    lastRotationAngle = currentAngle

                    listener.onRotate(totalRotation)

                    val dx = currentMidpointX - previousMidpointX
                    val dy = currentMidpointY - previousMidpointY
                    previousMidpointX = currentMidpointX
                    previousMidpointY = currentMidpointY
                    listener.onPan(dx, dy)

                    true
                } else {
                    false
                }

            MotionEvent.ACTION_POINTER_UP -> {
                if (isTransforming && initialDistance > 0) {
                    val finalScaleFactor = currentDistance / initialDistance
                    cumulativeScale = (cumulativeScale * finalScaleFactor)
                        .coerceIn(MIN_SCALE, MAX_SCALE)

                    val finalAngleDelta = normalizeAngle(currentAngle - initialAngle)
                    cumulativeRotation += finalAngleDelta
                }
                isTransforming = false
                return true
            }
        }
        return false
    }

    private fun getSnappedAngle(angle: Float): Float {
        val normalizedRotation = (angle % 360 + 360) % 360
        val snapAngles = floatArrayOf(0f, 90f, 180f, 270f, 360f)
        for (snapAngle in snapAngles) {
            if (Math.abs(normalizedRotation - snapAngle) < SNAP_ANGLE_THRESHOLD) {
                return snapAngle
            }
        }
        return angle
    }

    companion object {
        private const val MIN_SCALE = 0.1f
        private const val MAX_SCALE = 5.0f
        private const val HALF_CIRCLE = 180f
        private const val FULL_CIRCLE = 360f
        private const val SNAP_VELOCITY_THRESHOLD = 1.5f // degrees per ms
        private const val SNAP_ANGLE_THRESHOLD = 10f // degrees

        /**
         * Wraps an angle delta into the [-180, 180] range so that
         * crossing the atan2 ±180° boundary never produces a ~360° jump.
         */
        @JvmStatic
        fun normalizeAngle(angle: Float): Float {
            var a = angle % FULL_CIRCLE
            if (a > HALF_CIRCLE) a -= FULL_CIRCLE
            if (a < -HALF_CIRCLE) a += FULL_CIRCLE
            return a
        }

        @JvmStatic
        fun calculateDistance(x0: Float, y0: Float, x1: Float, y1: Float): Float {
            val dx = x1 - x0
            val dy = y1 - y0
            return sqrt(dx * dx + dy * dy)
        }

        @JvmStatic
        fun calculateAngle(x0: Float, y0: Float, x1: Float, y1: Float): Float =
            Math.toDegrees(atan2((y1 - y0).toDouble(), (x1 - x0).toDouble())).toFloat()
    }
}

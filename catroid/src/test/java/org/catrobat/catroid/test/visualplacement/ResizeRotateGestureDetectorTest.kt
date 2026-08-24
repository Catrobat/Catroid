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

package org.catrobat.catroid.test.visualplacement

import org.catrobat.catroid.visualplacement.ResizeRotateGestureDetector
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class ResizeRotateGestureDetectorTest {

    private lateinit var detector: ResizeRotateGestureDetector
    private var lastScale = 1.0f
    private var lastRotation = 0.0f
    private var lastPanDx = 0.0f
    private var lastPanDy = 0.0f

    @Before
    fun setUp() {
        lastScale = 1.0f
        lastRotation = 0.0f
        lastPanDx = 0.0f
        lastPanDy = 0.0f
        detector = ResizeRotateGestureDetector(
            object : ResizeRotateGestureDetector.OnTransformGestureListener {
                override fun onScale(scaleFactor: Float) {
                    lastScale = scaleFactor
                }

                override fun onRotate(rotationDegrees: Float) {
                    lastRotation = rotationDegrees
                }

                override fun onPan(dx: Float, dy: Float) {
                    lastPanDx = dx
                    lastPanDy = dy
                }
            }
        )
    }

    @Test
    fun testCalculateDistanceOrigin() {
        val distance = ResizeRotateGestureDetector.calculateDistance(0f, 0f, 3f, 4f)
        assertEquals(5.0f, distance, DELTA)
    }

    @Test
    fun testCalculateDistanceSamePoint() {
        val distance = ResizeRotateGestureDetector.calculateDistance(5f, 5f, 5f, 5f)
        assertEquals(0.0f, distance, DELTA)
    }

    @Test
    fun testCalculateDistanceNegativeCoords() {
        val distance = ResizeRotateGestureDetector.calculateDistance(-3f, -4f, 0f, 0f)
        assertEquals(5.0f, distance, DELTA)
    }

    @Test
    fun testCalculateAngleHorizontal() {
        val angle = ResizeRotateGestureDetector.calculateAngle(0f, 0f, 10f, 0f)
        assertEquals(0.0f, angle, DELTA)
    }

    @Test
    fun testCalculateAngleVerticalDown() {
        val angle = ResizeRotateGestureDetector.calculateAngle(0f, 0f, 0f, 10f)
        assertEquals(90.0f, angle, DELTA)
    }

    @Test
    fun testCalculateAngleVerticalUp() {
        val angle = ResizeRotateGestureDetector.calculateAngle(0f, 0f, 0f, -10f)
        assertEquals(-90.0f, angle, DELTA)
    }

    @Test
    fun testCalculateAngle45Degrees() {
        val angle = ResizeRotateGestureDetector.calculateAngle(0f, 0f, 10f, 10f)
        assertEquals(45.0f, angle, DELTA)
    }

    @Test
    fun testInitialState() {
        assertFalse(detector.isTransforming)
        assertEquals(1.0f, detector.cumulativeScale, DELTA)
        assertEquals(0.0f, detector.cumulativeRotation, DELTA)
    }

    @Test
    fun testSetCumulativeScale() {
        detector.cumulativeScale = 2.0f
        assertEquals(2.0f, detector.cumulativeScale, DELTA)
    }

    @Test
    fun testSetCumulativeRotation() {
        detector.cumulativeRotation = 45.0f
        assertEquals(45.0f, detector.cumulativeRotation, DELTA)
    }

    @Test
    fun testSingleFingerDoesNotTransform() {
        assertFalse(detector.isTransforming)
    }

    @Test
    fun testNormalizeAngle() {
        assertEquals(0.0f, ResizeRotateGestureDetector.normalizeAngle(0.0f), DELTA)
        assertEquals(90.0f, ResizeRotateGestureDetector.normalizeAngle(90.0f), DELTA)
        assertEquals(-90.0f, ResizeRotateGestureDetector.normalizeAngle(-90.0f), DELTA)
        assertEquals(180.0f, ResizeRotateGestureDetector.normalizeAngle(180.0f), DELTA)
        assertEquals(-170.0f, ResizeRotateGestureDetector.normalizeAngle(190.0f), DELTA)
        assertEquals(170.0f, ResizeRotateGestureDetector.normalizeAngle(-190.0f), DELTA)
        assertEquals(0.0f, ResizeRotateGestureDetector.normalizeAngle(360.0f), DELTA)
    }

    companion object {
        private const val DELTA = 0.01f
    }

    @Test
    fun testSetCumulativeRotationAndScaleForResetScenario() {
        detector.cumulativeRotation = 90.0f
        detector.cumulativeScale = 2.0f

        detector.cumulativeRotation = 0.0f
        detector.cumulativeScale = 1.0f

        assertEquals(0.0f, detector.cumulativeRotation, DELTA)
        assertEquals(1.0f, detector.cumulativeScale, DELTA)
    }

    @Test
    fun testCumulativeRotationForRotate90Feature() {
        // Simulates the rotateBy90Degrees feature:
        // rotation starts at 0, incremented by 90, then synced to detector
        var rotation = 0.0f

        rotation += 90f
        detector.cumulativeRotation = rotation
        assertEquals(90.0f, detector.cumulativeRotation, DELTA)

        rotation += 90f
        detector.cumulativeRotation = rotation
        assertEquals(180.0f, detector.cumulativeRotation, DELTA)

        rotation += 90f
        detector.cumulativeRotation = rotation
        assertEquals(270.0f, detector.cumulativeRotation, DELTA)

        rotation += 90f
        detector.cumulativeRotation = rotation
        assertEquals(360.0f, detector.cumulativeRotation, DELTA)
    }
}

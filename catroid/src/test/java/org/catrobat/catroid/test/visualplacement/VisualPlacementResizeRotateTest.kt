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

import android.view.MotionEvent
import android.widget.ImageView
import org.catrobat.catroid.visualplacement.BoundingBoxOverlay
import org.catrobat.catroid.visualplacement.CoordinateInterface
import org.catrobat.catroid.visualplacement.ResizeRotateGestureDetector
import org.catrobat.catroid.visualplacement.VisualPlacementTouchListener
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import kotlin.math.abs

@RunWith(MockitoJUnitRunner::class)
class VisualPlacementResizeRotateTest {

    @Mock
    lateinit var imageView: ImageView

    @Mock
    lateinit var motionEvent: MotionEvent

    @Mock
    lateinit var coordinateInterface: CoordinateInterface

    @Mock
    lateinit var boundingBoxOverlay: BoundingBoxOverlay

    private lateinit var listener: VisualPlacementTouchListener
    private lateinit var detector: ResizeRotateGestureDetector
    private var lastScale = 1.0f
    private var lastRotation = 0.0f

    @Before
    fun setUp() {
        lastScale = 1.0f
        lastRotation = 0.0f
        listener = VisualPlacementTouchListener()
        detector = ResizeRotateGestureDetector(
            object : ResizeRotateGestureDetector.OnTransformGestureListener {
                override fun onScale(scaleFactor: Float) {
                    lastScale = scaleFactor
                }

                override fun onRotate(rotationDegrees: Float) {
                    lastRotation = rotationDegrees
                }
            }
        )
        listener.setResizeRotateDetector(detector)
        listener.setBoundingBoxOverlay(boundingBoxOverlay)
    }

    @Test
    fun testSingleTouchDragStillWorks() {
        `when`(motionEvent.getPointerId(0)).thenReturn(0)
        `when`(motionEvent.action).thenReturn(MotionEvent.ACTION_DOWN)
        `when`(motionEvent.pointerCount).thenReturn(1)
        `when`(imageView.x).thenReturn(0f)
        `when`(imageView.y).thenReturn(0f)

        val result = listener.onTouch(imageView, motionEvent, coordinateInterface)
        assertTrue(result)
        verify(coordinateInterface).setXCoordinate(0f)
        verify(coordinateInterface).setYCoordinate(-0f)
    }

    @Test
    fun testBoundingBoxOverlayUpdatedOnDrag() {
        `when`(motionEvent.getPointerId(0)).thenReturn(0)
        `when`(motionEvent.action).thenReturn(MotionEvent.ACTION_DOWN)
        `when`(motionEvent.pointerCount).thenReturn(1)
        `when`(imageView.x).thenReturn(10f)
        `when`(imageView.y).thenReturn(20f)

        listener.onTouch(imageView, motionEvent, coordinateInterface)
        verify(boundingBoxOverlay).updateOverlay()
    }

    @Test
    fun testDetectorNotTransformingWithSingleTouch() {
        `when`(motionEvent.getPointerId(0)).thenReturn(0)
        `when`(motionEvent.action).thenReturn(MotionEvent.ACTION_DOWN)
        `when`(motionEvent.pointerCount).thenReturn(1)
        `when`(imageView.x).thenReturn(0f)
        `when`(imageView.y).thenReturn(0f)

        listener.onTouch(imageView, motionEvent, coordinateInterface)
        assertFalse(detector.isTransforming)
    }

    @Test
    fun testDetectorInitialScaleIsOne() {
        assertTrue(abs(detector.cumulativeScale - 1.0f) < DELTA)
    }

    @Test
    fun testDetectorInitialRotationIsZero() {
        assertTrue(abs(detector.cumulativeRotation) < DELTA)
    }

    companion object {
        private const val DELTA = 0.001f
    }
}

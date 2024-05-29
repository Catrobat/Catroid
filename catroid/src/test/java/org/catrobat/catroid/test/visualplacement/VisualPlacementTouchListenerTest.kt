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
package org.catrobat.catroid.test.visualplacement

import android.view.MotionEvent
import android.widget.ImageView
import org.catrobat.catroid.utils.AndroidCoordinates
import org.catrobat.catroid.visualplacement.VisualPlacementTouchListener
import org.catrobat.catroid.visualplacement.VisualPlacementViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.kotlin.mock
import org.mockito.kotlin.refEq
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import org.mockito.kotlin.verifyZeroInteractions
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.times

@RunWith(MockitoJUnitRunner::class)
class VisualPlacementTouchListenerTest {

    @Mock
    lateinit var viewModel: VisualPlacementViewModel

    @Mock
    lateinit var imageView: ImageView

    @Test
    fun testTouchDownSetCurrentCoordinates() {
        // given
        val initialX = 2f
        val initialY = 3f
        val currentPosition = AndroidCoordinates(initialX, initialY)
        val event = mock<MotionEvent> {
            on { action } doReturn MotionEvent.ACTION_DOWN
            on { getPointerId(0) } doReturn 0
        }
        val listener = VisualPlacementTouchListener()

        // when
        listener.onTouch(viewModel, imageView, currentPosition, event)

        // then
        verify(viewModel).setCoordinates(
            refEq(
                AndroidCoordinates(
                    initialX,
                    initialY
                )
            )
            , refEq(imageView))
        verifyNoMoreInteractions(imageView)
        verifyNoMoreInteractions(viewModel)
    }

    @Test
    fun testTouchUpSetNewCoordinates() {
        // given
        val initialX = 2f
        val initialY = 3f
        val newX = 4f
        val newY = 5f
        val event1 = mock<MotionEvent> {
            on { action } doReturn MotionEvent.ACTION_DOWN
            on { rawX } doReturn newX
            on { rawY } doReturn newY
            on { getPointerId(0) } doReturn 0
        }
        val event2 = mock<MotionEvent> {
            on { action } doReturn MotionEvent.ACTION_UP
            on { rawX } doReturn newX
            on { rawY } doReturn newY
            on { getPointerId(0) } doReturn 0
        }

        val currentPosition = AndroidCoordinates(initialX, initialY)
        val listener = VisualPlacementTouchListener()
        val positionCaptor = argumentCaptor<AndroidCoordinates>()

        // when
        listener.onTouch(viewModel, imageView, currentPosition, event1)
        listener.onTouch(viewModel, imageView, currentPosition, event2)

        // then
        verify(viewModel, times(2)).setCoordinates(positionCaptor.capture(), refEq(imageView))
        assertEquals(newX, positionCaptor.secondValue.x)
        assertEquals(newY, positionCaptor.secondValue.y)
        verifyNoMoreInteractions(imageView)
        verifyNoMoreInteractions(viewModel)
    }

    @Test
    fun testTouchMoveSetCoordinates() {
        // given
        val initialPictureX = 2f
        val initialPictureY = 3f
        val startMoveX = 4f
        val startMoveY = 5f
        val endMoveX = 5f
        val endMoveY = 6f
        val endPictureX = 3f
        val endPictureY = 4f
        val event1 = mock<MotionEvent> {
            on { action } doReturn MotionEvent.ACTION_MOVE
            on { rawX } doReturn startMoveX
            on { rawY } doReturn startMoveY
            on { getPointerId(0) } doReturn 0
        }
        val event2 = mock<MotionEvent> {
            on { action } doReturn MotionEvent.ACTION_MOVE
            on { rawX } doReturn endMoveX
            on { rawY } doReturn endMoveY
            on { getPointerId(0) } doReturn 0
        }

        val currentPosition = AndroidCoordinates(initialPictureX, initialPictureY)
        val listener = VisualPlacementTouchListener()
        val positionCaptor = argumentCaptor<AndroidCoordinates>()

        // when
        listener.onTouch(viewModel, imageView, currentPosition, event1)
        listener.onTouch(viewModel, imageView, currentPosition, event2)

        // then
        verify(viewModel, times(2)).setCoordinates(positionCaptor.capture(), refEq(imageView))
        assertEquals(endPictureX, positionCaptor.secondValue.x)
        assertEquals(endPictureY, positionCaptor.secondValue.y)
        verifyNoMoreInteractions(imageView)
        verifyNoMoreInteractions(viewModel)
    }

    @Test
    fun testReturnFalseOnWrongPointer() {
        // given
        val event = mock<MotionEvent> {
            on { getPointerId(0) } doReturn 1
        }
        val listener = VisualPlacementTouchListener()

        // when & then
        assertFalse(listener.onTouch(viewModel, imageView, AndroidCoordinates(2f, 3f), event))
        verifyZeroInteractions(imageView)
        verifyZeroInteractions(viewModel)
    }
}

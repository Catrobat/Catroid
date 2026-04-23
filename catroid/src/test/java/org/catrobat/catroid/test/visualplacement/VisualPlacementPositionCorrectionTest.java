/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2022 The Catrobat Team
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

package org.catrobat.catroid.test.visualplacement;

import android.view.MotionEvent;
import android.widget.ImageView;

import org.catrobat.catroid.visualplacement.CoordinateInterface;
import org.catrobat.catroid.visualplacement.VisualPlacementTouchListener;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.ArgumentMatchers.anyFloat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class VisualPlacementPositionCorrectionTest {

	@Mock
	ImageView imageView;

	@Mock
	MotionEvent motionEvent;

	@Mock
	CoordinateInterface coordinateInterface;

	@InjectMocks
	VisualPlacementTouchListener listener;

	private final Float initalPositionX = 1.f;
	private final Float initalPositionY = 1.f;
	private final Float firstMovementPositionX = 5.f;
	private final Float firstMovementPositionY = 5.f;
	private final Float secondMovementPositionX = 6.f;
	private final Float secondMovementPositionY = 6.f;
	private final Float actionUpMovementPositionX = 7.f;
	private final Float actionUpMovementPositionY = 7.f;
	private final long firstMovementTimestamp = 150;
	private final long secondMovementTimestamp = 170;
	private final long actionUpTimestamp = 175;

	@Before
	public void setUp() {
		when(motionEvent.getDownTime()).thenReturn((long) 0);
	}

	public void triggerTouchDownEvent(float startPositionX, float startPositionY) {
		when(motionEvent.getAction()).thenReturn(MotionEvent.ACTION_DOWN);
		when(imageView.getX()).thenReturn(startPositionX);
		when(imageView.getY()).thenReturn(startPositionY);
		when(motionEvent.getRawX()).thenReturn(startPositionX);
		when(motionEvent.getRawY()).thenReturn(startPositionY);
		listener.onTouch(imageView, motionEvent, coordinateInterface);
	}

	public void triggerMovementEvent(long timestamp, float positionX, float positionY) {
		when(motionEvent.getAction()).thenReturn(MotionEvent.ACTION_MOVE);
		when(motionEvent.getRawX()).thenReturn(positionX);
		when(motionEvent.getRawY()).thenReturn(positionY);
		when(motionEvent.getEventTime()).thenReturn(timestamp);

		listener.onTouch(imageView, motionEvent, coordinateInterface);
		when(imageView.getX()).thenReturn(positionX);
		when(imageView.getY()).thenReturn(positionY);
	}

	public void triggerTouchUpEvent(long timestamp) {
		when(motionEvent.getEventTime()).thenReturn(timestamp);
		when(motionEvent.getAction()).thenReturn(MotionEvent.ACTION_UP);
		when(motionEvent.getRawX()).thenReturn(actionUpMovementPositionX);
		when(motionEvent.getRawY()).thenReturn(actionUpMovementPositionY);
		listener.onTouch(imageView, motionEvent, coordinateInterface);
	}

	@Test
	public void testTouchCorrection() {
		triggerTouchDownEvent(initalPositionX, initalPositionY);

		triggerMovementEvent(firstMovementTimestamp, firstMovementPositionX, firstMovementPositionY);
		verify(imageView, times(1)).setX(firstMovementPositionX);

		triggerMovementEvent(secondMovementTimestamp, secondMovementPositionX, secondMovementPositionY);
		verify(imageView, times(1)).setX(secondMovementPositionX);

		triggerTouchUpEvent(actionUpTimestamp);
		verify(imageView, times(2)).setX(firstMovementPositionX);

		verify(imageView, times(3)).setX(anyFloat());
	}

	@Test
	public void testTouchCorrectionWithInvalidDelayBetweenMovements() {
		long tooLateTimestamp = secondMovementTimestamp + 15;

		triggerTouchDownEvent(initalPositionX, initalPositionY);

		triggerMovementEvent(firstMovementTimestamp, firstMovementPositionX, firstMovementPositionY);
		verify(imageView, times(1)).setX(firstMovementPositionX);

		triggerMovementEvent(secondMovementTimestamp, secondMovementPositionX, secondMovementPositionY);
		verify(imageView, times(1)).setX(secondMovementPositionX);

		triggerTouchUpEvent(tooLateTimestamp);
		verify(imageView, times(1)).setX(actionUpMovementPositionX);

		verify(imageView, times(3)).setX(anyFloat());
	}

	@Test
	public void testTouchCorrectionWithTooBigDelayBeforeUP() {
		long muchLaterTimestamp = 220;

		triggerTouchDownEvent(initalPositionX, initalPositionY);

		triggerMovementEvent(firstMovementTimestamp, firstMovementPositionX, firstMovementPositionY);
		verify(imageView, times(1)).setX(firstMovementPositionX);

		triggerMovementEvent(secondMovementTimestamp, secondMovementPositionX, secondMovementPositionY);
		verify(imageView, times(1)).setX(secondMovementPositionX);

		triggerTouchUpEvent(muchLaterTimestamp);
		verify(imageView, times(1)).setX(actionUpMovementPositionX);

		verify(imageView, times(3)).setX(anyFloat());
	}

	@Test
	public void testTouchCorrectionWithDistanceAboveJitterThreshold() {
		float farAwayPositionX = 20.0f;
		float farAwayPositionY = 20.0f;

		triggerTouchDownEvent(initalPositionX, initalPositionY);

		triggerMovementEvent(firstMovementTimestamp, farAwayPositionX, farAwayPositionY);
		verify(imageView, times(1)).setX(farAwayPositionX);

		triggerMovementEvent(secondMovementTimestamp, secondMovementPositionX, secondMovementPositionY);
		verify(imageView, times(1)).setX(secondMovementPositionX);

		triggerTouchUpEvent(actionUpTimestamp);
		verify(imageView, times(1)).setX(actionUpMovementPositionX);

		verify(imageView, times(3)).setX(anyFloat());
	}
}

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
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class VisualPlacementTouchListenerTest {

	@Mock
	ImageView imageView;

	@Mock
	MotionEvent firstEvent;

	@Mock
	MotionEvent secondEvent;

	@Mock
	CoordinateInterface coordinateInterface;

	@InjectMocks
	VisualPlacementTouchListener listener;

	@Test
	public void testTouchDownSetCoordinates() {
		when(firstEvent.getAction()).thenReturn(MotionEvent.ACTION_DOWN);
		when(imageView.getX()).thenReturn(13f);
		when(imageView.getY()).thenReturn(17f);
		listener.onTouch(imageView, firstEvent, coordinateInterface);
		verify(coordinateInterface).setXCoordinate(13f);
		verify(coordinateInterface).setYCoordinate(-17f);
	}

	@Test
	public void testTouchMoveSetCoordinates() {
		when(secondEvent.getAction()).thenReturn(MotionEvent.ACTION_MOVE);
		when(imageView.getX()).thenReturn(10f);
		when(imageView.getY()).thenReturn(21f);

		listener.onTouch(imageView, secondEvent, coordinateInterface);

		verify(coordinateInterface).setXCoordinate(10f);
		verify(coordinateInterface).setYCoordinate(-21f);
	}

	@Test
	public void testOnTouchReturnFalse() {
		when(firstEvent.getPointerId(0)).thenReturn(1);
		boolean returnValue = listener.onTouch(imageView, firstEvent, coordinateInterface);
		assertFalse(returnValue);
		verifyZeroInteractions(coordinateInterface);
	}

	@Test
	public void testOnTouchReturnTrue() {
		when(firstEvent.getPointerId(0)).thenReturn(0);
		boolean returnValue = listener.onTouch(imageView, firstEvent, coordinateInterface);
		assertTrue(returnValue);
	}
}

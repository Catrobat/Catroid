/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2018 The Catrobat Team
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

package org.catrobat.catroid.test.embroidery;

import com.badlogic.gdx.graphics.Color;

import org.catrobat.catroid.embroidery.DSTHeader;
import org.catrobat.catroid.embroidery.DSTStitchPoint;
import org.catrobat.catroid.embroidery.DSTStream;
import org.catrobat.catroid.embroidery.EmbroideryHeader;
import org.catrobat.catroid.embroidery.EmbroideryStream;
import org.catrobat.catroid.embroidery.StitchPoint;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import java.util.ArrayList;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyFloat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(AndroidJUnit4.class)
public class DSTStreamTest {
	private EmbroideryStream stream;
	private EmbroideryHeader header;

	@Before
	public void setUp() {
		header = Mockito.mock(DSTHeader.class);
		stream = new DSTStream(header);
	}

	@Test
	public void testAddJumpStitchPoint() {
		stream.addJump();
		stream.addStitchPoint(0, 0, Color.BLACK);

		assertEquals(1, stream.getPointList().size());
		assertTrue(stream.getPointList().get(0).isJumpPoint());
	}

	@Test
	public void testAddColorChangeStitchPoint() {
		stream.addColorChange();
		stream.addStitchPoint(0, 0, Color.BLACK);

		assertEquals(1, stream.getPointList().size());
		assertTrue(stream.getPointList().get(0).isColorChangePoint());
		verify(header, times(1)).addColorChange();
	}

	private boolean pointAtPositionEqualsCoordinates(int index, int x, int y) {
		StitchPoint pointAtPosition = stream.getPointList().get(index);
		return x == pointAtPosition.getX() && y == pointAtPosition.getY();
	}

	@Test
	public void testInterpolatedStitchPoints() {
		stream.addStitchPoint(0, 0, Color.BLACK);
		stream.addStitchPoint(80, 80, Color.BLACK);

		assertEquals(5, stream.getPointList().size());
		assertTrue(pointAtPositionEqualsCoordinates(0, 0, 0));
		assertTrue(pointAtPositionEqualsCoordinates(1, 0, 0));
		assertTrue(stream.getPointList().get(1).isJumpPoint());
		assertTrue(pointAtPositionEqualsCoordinates(2, 40, 40));
		assertTrue(stream.getPointList().get(2).isJumpPoint());
		assertTrue(pointAtPositionEqualsCoordinates(3, 80, 80));
		assertTrue(stream.getPointList().get(3).isJumpPoint());
		assertTrue(pointAtPositionEqualsCoordinates(4, 80, 80));
		verify(header, times(1)).initialize(0, 0);
		verify(header, times(4)).update(anyFloat(), anyFloat());
	}

	@Test
	public void testAddAllStitchPoints() {
		StitchPoint stitchPoint1 = new DSTStitchPoint(0, 0, Color.BLACK);
		stitchPoint1.setColorChange(true);
		StitchPoint stitchPoint2 = new DSTStitchPoint(80, 80, Color.BLACK);
		stitchPoint2.setJump(true);
		StitchPoint stitchPoint3 = new DSTStitchPoint(100, 100, Color.BLACK);

		ArrayList<StitchPoint> stitchPoints = new ArrayList<>();
		stitchPoints.add(stitchPoint1);
		stitchPoints.add(stitchPoint2);
		stitchPoints.add(stitchPoint3);

		stream.addAllStitchPoints(stitchPoints);

		assertEquals(6, stream.getPointList().size());
		assertTrue(stream.getPointList().get(0).isColorChangePoint());
		assertTrue(stream.getPointList().get(1).isJumpPoint());
		assertTrue(stream.getPointList().get(2).isJumpPoint());
		assertTrue(stream.getPointList().get(3).isJumpPoint());
		assertTrue(stream.getPointList().get(4).isJumpPoint());
		assertTrue(stream.getPointList().get(5).isConnectingPoint());

		verify(header, times(1)).initialize(0, 0);
		verify(header, times(5)).update(anyFloat(), anyFloat());
	}
}

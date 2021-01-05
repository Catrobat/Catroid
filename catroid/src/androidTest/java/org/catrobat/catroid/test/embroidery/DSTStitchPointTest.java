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

import org.catrobat.catroid.embroidery.DSTStitchPoint;
import org.catrobat.catroid.embroidery.StitchPoint;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import java.io.FileOutputStream;
import java.io.IOException;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(AndroidJUnit4.class)
public class DSTStitchPointTest {

	private FileOutputStream fileOutputStream;

	@Before
	public void setUo() {
		fileOutputStream = Mockito.mock(FileOutputStream.class);
	}

	@Test
	public void testisConnectingPoint() {
		StitchPoint stitchPoint = new DSTStitchPoint(0, 0, Color.BLACK);

		assertTrue(stitchPoint.isConnectingPoint());
		assertFalse(stitchPoint.isColorChangePoint());
		assertFalse(stitchPoint.isJumpPoint());
	}

	@Test
	public void testJumpStitchPoint() {
		StitchPoint stitchPoint = new DSTStitchPoint(0, 0, Color.BLACK);
		stitchPoint.setJump(true);

		assertTrue(stitchPoint.isJumpPoint());
		assertFalse(stitchPoint.isConnectingPoint());
	}

	@Test
	public void testColorChangeStitchPoint() {
		StitchPoint stitchPoint = new DSTStitchPoint(0, 0, Color.BLACK);
		stitchPoint.setColorChange(true);

		assertTrue(stitchPoint.isColorChangePoint());
		assertFalse(stitchPoint.isConnectingPoint());
	}

	@Test
	public void testStitchBytesZeroDifference() throws IOException {
		final byte[] expectedStitchBytes = new byte[] {0, 0, (byte) 0x3};

		StitchPoint stitchPoint = new DSTStitchPoint(0, 0, Color.BLACK);
		stitchPoint.setRelativeCoordinatesToPreviousPoint(0, 0);

		stitchPoint.appendToStream(fileOutputStream);
		verify(fileOutputStream, times(1)).write(expectedStitchBytes);
	}

	@Test
	public void testStitchBytesDifference() throws IOException {
		final byte[] expectedStitchBytes = new byte[] {(byte) 0x5A, (byte) 0x5A, (byte) 0x03};

		StitchPoint stitchPoint = new DSTStitchPoint(0, 0, Color.BLACK);
		stitchPoint.setRelativeCoordinatesToPreviousPoint(20, 20);

		stitchPoint.appendToStream(fileOutputStream);
		verify(fileOutputStream, times(1)).write(expectedStitchBytes);
	}

	@Test
	public void testStitchBytesWithColorChange() throws IOException {
		final byte[] expectedStitchBytes = new byte[] {(byte) 0xA5, (byte) 0xA5, (byte) 0xC3};

		StitchPoint stitchPoint = new DSTStitchPoint(0, 0, Color.BLACK);
		stitchPoint.setColorChange(true);
		stitchPoint.setRelativeCoordinatesToPreviousPoint(-20, -20);

		stitchPoint.appendToStream(fileOutputStream);
		verify(fileOutputStream, times(1)).write(expectedStitchBytes);
	}

	@Test
	public void testStitchBytesWithJump() throws IOException {
		final byte[] expectedStitchBytes = new byte[] {(byte) 0xAA, (byte) 0xAA, (byte) 0x83};

		StitchPoint stitchPoint = new DSTStitchPoint(0, 0, Color.BLACK);
		stitchPoint.setJump(true);
		stitchPoint.setRelativeCoordinatesToPreviousPoint(20, -20);

		stitchPoint.appendToStream(fileOutputStream);
		verify(fileOutputStream, times(1)).write(expectedStitchBytes);
	}

	@Test
	public void testStitchPointsEquals() {
		StitchPoint stitchPoint1 = new DSTStitchPoint(0, 0, Color.BLACK);
		StitchPoint stitchPoint2 = new DSTStitchPoint(1, 1, Color.BLACK);
		StitchPoint stitchPoint3 = new DSTStitchPoint(0, 0, Color.BLACK);
		stitchPoint3.setColorChange(true);
		StitchPoint stitchPoint4 = new DSTStitchPoint(0, 0, Color.BLACK);
		stitchPoint4.setJump(true);
		StitchPoint stitchPoint5 = new DSTStitchPoint(0, 0, Color.BLACK);

		assertNotEquals(stitchPoint1, stitchPoint2);
		assertNotEquals(stitchPoint1, stitchPoint3);
		assertNotEquals(stitchPoint1, stitchPoint4);
		assertNotEquals(stitchPoint3, stitchPoint4);
		assertEquals(stitchPoint1, stitchPoint5);
	}

	@Test
	public void testStitchPointsHashCode() {
		StitchPoint stitchPoint1 = new DSTStitchPoint(1, 2, Color.BLACK);
		StitchPoint stitchPoint2 = new DSTStitchPoint(0, 0, Color.BLACK);
		stitchPoint2.setJump(true);
		StitchPoint stitchPoint3 = new DSTStitchPoint(0, 0, Color.BLACK);
		stitchPoint3.setColorChange(true);
		StitchPoint stitchPoint4 = new DSTStitchPoint(2, 1, Color.BLACK);
		StitchPoint stitchPoint5 = new DSTStitchPoint(1, 2, Color.BLACK);

		assertNotEquals(stitchPoint1.hashCode(), stitchPoint2.hashCode());
		assertNotEquals(stitchPoint1.hashCode(), stitchPoint3.hashCode());
		assertNotEquals(stitchPoint1.hashCode(), stitchPoint4.hashCode());
		assertEquals(stitchPoint1.hashCode(), stitchPoint5.hashCode());
	}
}

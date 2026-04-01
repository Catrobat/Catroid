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

package org.catrobat.catroid.test.note.trackgrid;

import org.catrobat.catroid.pocketmusic.note.trackgrid.GridRow;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static junit.framework.Assert.assertEquals;

import static org.junit.Assert.assertNotEquals;

@RunWith(JUnit4.class)
public class GridRowTest {

	@Test
	public void testEquals1() {
		GridRow gridRow1 = GridRowTestDataFactory.createGridRowWithOnePosition();
		GridRow gridRow2 = GridRowTestDataFactory.createGridRowWithOnePosition();

		assertEquals(gridRow1, gridRow2);
	}

	@Test
	public void testEquals2() {
		GridRow gridRow1 = GridRowTestDataFactory.createGridRowWithOnePosition();
		GridRow gridRow2 = GridRowTestDataFactory.createGridRowWithDuplicatePositions();

		assertNotEquals(gridRow2, gridRow1);
	}

	@Test
	public void testEquals3() {
		GridRow gridRow1 = GridRowTestDataFactory.createGridRowWithDifferentPositions();
		GridRow gridRow2 = GridRowTestDataFactory.createGridRowWithDuplicatePositions();

		assertNotEquals(gridRow2, gridRow1);
	}

	@Test
	public void testEquals4() {
		GridRow gridRow1 = GridRowTestDataFactory.createGridRowWithDifferentPositions();
		GridRow gridRow2 = GridRowTestDataFactory.createGridRowWithDifferentPositions();

		assertEquals(gridRow1, gridRow2);
	}

	@Test
	public void testHashCode1() {
		GridRow gridRow1 = GridRowTestDataFactory.createGridRowWithOnePosition();
		GridRow gridRow2 = GridRowTestDataFactory.createGridRowWithOnePosition();

		assertEquals(gridRow1.hashCode(), gridRow2.hashCode());
	}

	@Test
	public void testHashCode2() {
		GridRow gridRow1 = GridRowTestDataFactory.createGridRowWithDifferentPositions();
		GridRow gridRow2 = GridRowTestDataFactory.createGridRowWithOnePosition();

		assertNotEquals(gridRow2.hashCode(), gridRow1.hashCode());
	}
}

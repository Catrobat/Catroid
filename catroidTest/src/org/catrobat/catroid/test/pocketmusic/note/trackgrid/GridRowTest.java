/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2016 The Catrobat Team
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

package org.catrobat.catroid.test.pocketmusic.note.trackgrid;

import android.test.AndroidTestCase;

import org.catrobat.catroid.pocketmusic.note.trackgrid.GridRow;

public class GridRowTest extends AndroidTestCase {

	public void testEquals1() {
		GridRow gridRow1 = GridRowTestDataFactory.createGridRowWithOnePosition();
		GridRow gridRow2 = GridRowTestDataFactory.createGridRowWithOnePosition();

		assertTrue("GridRows are not equal", gridRow1.equals(gridRow2));
	}

	public void testEquals2() {
		GridRow gridRow1 = GridRowTestDataFactory.createGridRowWithOnePosition();
		GridRow gridRow2 = GridRowTestDataFactory.createGridRowWithDuplicatePositions();

		assertFalse("GridRows are equal", gridRow1.equals(gridRow2));
	}

	public void testEquals3() {
		GridRow gridRow1 = GridRowTestDataFactory.createGridRowWithDifferentPositions();
		GridRow gridRow2 = GridRowTestDataFactory.createGridRowWithDuplicatePositions();

		assertFalse("GridRows are equal", gridRow1.equals(gridRow2));
	}

	public void testEquals4() {
		GridRow gridRow1 = GridRowTestDataFactory.createGridRowWithDifferentPositions();
		GridRow gridRow2 = GridRowTestDataFactory.createGridRowWithDifferentPositions();

		assertTrue("GridRows are not equal", gridRow1.equals(gridRow2));
	}

	public void testHashCode1() {
		GridRow gridRow1 = GridRowTestDataFactory.createGridRowWithOnePosition();
		GridRow gridRow2 = GridRowTestDataFactory.createGridRowWithOnePosition();

		assertTrue("GridRow hashcode error", gridRow1.hashCode() == gridRow2.hashCode());
	}

	public void testHashCode2() {
		GridRow gridRow1 = GridRowTestDataFactory.createGridRowWithDifferentPositions();
		GridRow gridRow2 = GridRowTestDataFactory.createGridRowWithOnePosition();

		assertFalse("GridRow hashcode error", gridRow1.hashCode() == gridRow2.hashCode());
	}
}

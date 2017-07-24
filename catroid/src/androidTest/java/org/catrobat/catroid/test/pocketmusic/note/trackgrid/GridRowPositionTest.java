/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2017 The Catrobat Team
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

import org.catrobat.catroid.pocketmusic.note.trackgrid.GridRowPosition;

public class GridRowPositionTest extends AndroidTestCase {

	public void testEquals1() {
		GridRowPosition gridRowPosition = GridRowPositionTestDataFactory.createSimpleGridRowPosition();
		GridRowPosition secondGridRowPosition = GridRowPositionTestDataFactory.createSimpleGridRowPosition();

		assertTrue("GridRowPositions are not equal.", gridRowPosition.equals(secondGridRowPosition));
	}

	public void testEquals2() {
		GridRowPosition gridRowPosition = GridRowPositionTestDataFactory.createSimpleGridRowPosition();
		GridRowPosition secondGridRowPosition = GridRowPositionTestDataFactory.createGridRowPositionWithOffset();

		assertFalse("GridRowPositions are equal.", gridRowPosition.equals(secondGridRowPosition));
	}

	public void testEquals3() {
		GridRowPosition gridRowPosition = GridRowPositionTestDataFactory.createGridRowPositionWithHalf();
		GridRowPosition secondGridRowPosition = GridRowPositionTestDataFactory.createGridRowPositionWithOffset();

		assertFalse("GridRowPositions are equal.", gridRowPosition.equals(secondGridRowPosition));
	}

	public void testHashCodeEquals() {
		GridRowPosition gridRowPosition = GridRowPositionTestDataFactory.createSimpleGridRowPosition();
		GridRowPosition secondGridRowPosition = GridRowPositionTestDataFactory.createSimpleGridRowPosition();

		assertTrue("HashCodes are equal", gridRowPosition.hashCode() == secondGridRowPosition.hashCode());
	}
}

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

import org.catrobat.catroid.pocketmusic.note.trackgrid.TrackGrid;

public class TrackGridTest extends AndroidTestCase {

	public void testEquals1() {
		TrackGrid trackGrid1 = TrackGridTestDataFactory.createSimpleTrackGrid();
		TrackGrid trackGrid2 = TrackGridTestDataFactory.createSimpleTrackGrid();

		assertTrue("TrackGrid not equal", trackGrid1.equals(trackGrid2));
	}

	public void testEquals2() {
		TrackGrid trackGrid1 = TrackGridTestDataFactory.createTrackGridWithSeveralBreaks();
		TrackGrid trackGrid2 = TrackGridTestDataFactory.createTrackGridWithSeveralBreaks();

		assertTrue("TrackGrid not equal", trackGrid1.equals(trackGrid2));
	}

	public void testEquals3() {
		TrackGrid trackGrid1 = TrackGridTestDataFactory.createSemiComplexTrackGrid();
		TrackGrid trackGrid2 = TrackGridTestDataFactory.createSimpleTrackGrid();

		assertFalse("TrackGrid equal", trackGrid1.equals(trackGrid2));
	}

	public void testHashCode1() {
		TrackGrid trackGrid1 = TrackGridTestDataFactory.createSimpleTrackGrid();
		TrackGrid trackGrid2 = TrackGridTestDataFactory.createSimpleTrackGrid();

		assertTrue("HashCode not equal", trackGrid1.hashCode() == trackGrid2.hashCode());
	}

	public void testHashCode2() {
		TrackGrid trackGrid1 = TrackGridTestDataFactory.createTrackGridWithSeveralBreaks();
		TrackGrid trackGrid2 = TrackGridTestDataFactory.createSimpleTrackGrid();

		assertFalse("HashCode equal", trackGrid1.hashCode() == trackGrid2.hashCode());
	}

	public void testHashCode3() {
		TrackGrid trackGrid1 = TrackGridTestDataFactory.createSemiComplexTrackGrid();
		TrackGrid trackGrid2 = TrackGridTestDataFactory.createSimpleTrackGrid();

		assertFalse("HashCode equal", trackGrid1.hashCode() == trackGrid2.hashCode());
	}
}

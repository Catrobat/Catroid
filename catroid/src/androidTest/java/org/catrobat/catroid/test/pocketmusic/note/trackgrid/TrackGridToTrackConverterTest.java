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

import org.catrobat.catroid.pocketmusic.note.MusicalBeat;
import org.catrobat.catroid.pocketmusic.note.Project;
import org.catrobat.catroid.pocketmusic.note.Track;
import org.catrobat.catroid.pocketmusic.note.trackgrid.TrackGrid;
import org.catrobat.catroid.pocketmusic.note.trackgrid.TrackGridToTrackConverter;
import org.catrobat.catroid.pocketmusic.note.trackgrid.TrackToTrackGridConverter;

public class TrackGridToTrackConverterTest extends AndroidTestCase {

	private int beatsPerMinute = Project.DEFAULT_BEATS_PER_MINUTE;

	public TrackGridToTrackConverterTest() {
	}

	public void testConvertSimpleTrackGrid() {
		TrackGrid simpleTrackGrid = TrackGridTestDataFactory.createFirstOctaveOnlyTrackGrid();

		Track track = TrackGridToTrackConverter.convertTrackGridToTrack(simpleTrackGrid, beatsPerMinute);

		TrackGrid newTrackGrid = TrackToTrackGridConverter.convertTrackToTrackGrid(track, MusicalBeat.BEAT_4_4,
				beatsPerMinute);

		assertTrue("Conversion from Track to Trackgrid failed for simple track grid.",
				newTrackGrid.equals(simpleTrackGrid));
	}

	public void testConvertTrackGridWithSeveralBreaks() {
		TrackGrid trackWithSeveralBreaks = TrackGridTestDataFactory.createTrackGridWithSeveralBreaks();

		Track track = TrackGridToTrackConverter.convertTrackGridToTrack(trackWithSeveralBreaks, beatsPerMinute);

		TrackGrid convertedTrackGrid = TrackToTrackGridConverter.convertTrackToTrackGrid(track,
				MusicalBeat.BEAT_16_16, beatsPerMinute);

		assertTrue("Conversion from Track to Trackgrid failed for track grid with several breaks.", convertedTrackGrid
				.equals(trackWithSeveralBreaks));
	}

	public void testConvertSemiComplexTrackGrid() {
		TrackGrid semiComplexTrackGrid = TrackGridTestDataFactory.createSemiComplexTrackGrid();

		Track track = TrackGridToTrackConverter.convertTrackGridToTrack(semiComplexTrackGrid, beatsPerMinute);

		TrackGrid convertedTrackGrid = TrackToTrackGridConverter.convertTrackToTrackGrid(track,
				MusicalBeat.BEAT_16_16, beatsPerMinute);

		assertTrue("Conversion from Track to Trackgrid failed for semi complex track grid.",
				convertedTrackGrid.equals(semiComplexTrackGrid));
	}
}

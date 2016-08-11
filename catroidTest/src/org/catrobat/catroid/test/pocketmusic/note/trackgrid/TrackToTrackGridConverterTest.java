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
import org.catrobat.catroid.pocketmusic.note.MusicalInstrument;
import org.catrobat.catroid.pocketmusic.note.Project;
import org.catrobat.catroid.pocketmusic.note.Track;
import org.catrobat.catroid.pocketmusic.note.trackgrid.TrackGrid;
import org.catrobat.catroid.pocketmusic.note.trackgrid.TrackToTrackGridConverter;
import org.catrobat.catroid.test.pocketmusic.note.TrackTestDataFactory;

public class TrackToTrackGridConverterTest extends AndroidTestCase {

	public void testConvertSimpleTrack() {
		Track track = TrackTestDataFactory.createSimpleTrack();
		TrackGrid simpleTrackGrid = TrackGridTestDataFactory.createSimpleTrackGrid();

		TrackGrid convertedTrackGrid = TrackToTrackGridConverter.convertTrackToTrackGrid(track, Project.DEFAULT_BEAT, Project
				.DEFAULT_BEATS_PER_MINUTE);

		assertTrue("Failed to convert simple Track.", convertedTrackGrid.equals(simpleTrackGrid));
	}

	public void testConvertTrackWithSeveralBreaks() {
		Track track = TrackTestDataFactory.createTrackWithSeveralBreaks();
		TrackGrid trackWithSeveralBreaks = TrackGridTestDataFactory.createTrackGridWithSeveralBreaks();

		TrackGrid convertedTrackGrid = TrackToTrackGridConverter.convertTrackToTrackGrid(track, MusicalBeat.BEAT_16_16, Project
				.DEFAULT_BEATS_PER_MINUTE);

		assertTrue("Failed to convert Track with several breaks", convertedTrackGrid.equals(trackWithSeveralBreaks));
	}

	public void testConvertTrack() {
		Track track = TrackTestDataFactory.createSemiComplexTrack(MusicalInstrument.ACOUSTIC_GRAND_PIANO);
		TrackGrid semiComplexTrack = TrackGridTestDataFactory.createSemiComplexTrackGrid(MusicalInstrument.ACOUSTIC_GRAND_PIANO);

		TrackGrid convertedTrackGrid = TrackToTrackGridConverter.convertTrackToTrackGrid(track, MusicalBeat.BEAT_16_16, Project
				.DEFAULT_BEATS_PER_MINUTE);

		assertTrue("Failed to convert a more complex Track", convertedTrackGrid.equals(semiComplexTrack));
	}
}

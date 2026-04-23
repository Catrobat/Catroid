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

import org.catrobat.catroid.pocketmusic.mididriver.MidiNotePlayer;
import org.catrobat.catroid.pocketmusic.note.MusicalBeat;
import org.catrobat.catroid.pocketmusic.note.Project;
import org.catrobat.catroid.pocketmusic.note.Track;
import org.catrobat.catroid.pocketmusic.note.trackgrid.TrackGrid;
import org.catrobat.catroid.pocketmusic.note.trackgrid.TrackGridToTrackConverter;
import org.catrobat.catroid.pocketmusic.note.trackgrid.TrackToTrackGridConverter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static junit.framework.Assert.assertEquals;

@RunWith(PowerMockRunner.class)
@PrepareForTest({MidiNotePlayer.class, TrackGrid.class})
public class TrackGridToTrackConverterTest {

	private int beatsPerMinute = Project.DEFAULT_BEATS_PER_MINUTE;

	@Before
	public void setUp() throws Exception {
		PowerMockito.whenNew(MidiNotePlayer.class).withAnyArguments().thenReturn(Mockito.mock(MidiNotePlayer.class));
	}

	@Test
	public void testConvertSimpleTrackGrid() {
		TrackGrid simpleTrackGrid = TrackGridTestDataFactory.createFirstOctaveOnlyTrackGrid();

		Track track = TrackGridToTrackConverter.convertTrackGridToTrack(simpleTrackGrid, beatsPerMinute);

		TrackGrid newTrackGrid = TrackToTrackGridConverter.convertTrackToTrackGrid(track, MusicalBeat.BEAT_4_4,
				beatsPerMinute);

		assertEquals(newTrackGrid, simpleTrackGrid);
	}

	@Test
	public void testConvertTrackGridWithSeveralBreaks() {
		TrackGrid trackWithSeveralBreaks = TrackGridTestDataFactory.createTrackGridWithSeveralBreaks();

		Track track = TrackGridToTrackConverter.convertTrackGridToTrack(trackWithSeveralBreaks, beatsPerMinute);

		TrackGrid convertedTrackGrid = TrackToTrackGridConverter.convertTrackToTrackGrid(track,
				MusicalBeat.BEAT_4_4, beatsPerMinute);

		assertEquals(convertedTrackGrid, trackWithSeveralBreaks);
	}

	@Test
	public void testConvertSemiComplexTrackGrid() {
		TrackGrid semiComplexTrackGrid = TrackGridTestDataFactory.createSemiComplexTrackGrid();

		Track track = TrackGridToTrackConverter.convertTrackGridToTrack(semiComplexTrackGrid, beatsPerMinute);

		TrackGrid convertedTrackGrid = TrackToTrackGridConverter.convertTrackToTrackGrid(track,
				MusicalBeat.BEAT_4_4, beatsPerMinute);

		assertEquals(convertedTrackGrid, semiComplexTrackGrid);
	}
}

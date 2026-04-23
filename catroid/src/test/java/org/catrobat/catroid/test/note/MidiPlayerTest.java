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
package org.catrobat.catroid.test.note;

import org.catrobat.catroid.pocketmusic.mididriver.MidiNotePlayer;
import org.catrobat.catroid.pocketmusic.mididriver.MidiPlayer;
import org.catrobat.catroid.pocketmusic.note.MusicalBeat;
import org.catrobat.catroid.pocketmusic.note.Project;
import org.catrobat.catroid.pocketmusic.note.Track;
import org.catrobat.catroid.pocketmusic.note.midi.MidiException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;

import static junit.framework.Assert.assertEquals;

@RunWith(PowerMockRunner.class)
@PrepareForTest({MidiNotePlayer.class, MidiPlayer.class})
public class MidiPlayerTest {

	@Before
	public void setUp() throws Exception {
		PowerMockito.whenNew(MidiNotePlayer.class).withAnyArguments().thenReturn(Mockito.mock(MidiNotePlayer.class));
	}

	@Test
	public void testCreateNewMidiPlayer() throws MidiException {
		MidiPlayer.resetChannelCounter();
		MidiPlayer player = new MidiPlayer();
		assertEquals(0, player.getChannel());
	}

	@Test
	public void testCreateMultipleNewMidiPlayers() throws MidiException {
		MidiPlayer.resetChannelCounter();
		for (int i = 0; i < MidiPlayer.MAX_CHANNELS; i++) {
			MidiPlayer player = new MidiPlayer();
			if (i < MidiPlayer.DRUM_CHANNEL) {
				assertEquals(i, player.getChannel());
			} else {
				assertEquals(i + 1, player.getChannel());
			}
		}
	}

	@Test
	public void testSeekTo() throws MidiException, IOException {
		MidiPlayer.resetChannelCounter();

		Project project = new Project("testProject", MusicalBeat.BEAT_4_4, Project.DEFAULT_BEATS_PER_MINUTE);
		Track track = TrackTestDataFactory.createSimpleTrackWithoutOffEvents();
		project.putTrack("testTrack", track);

		MidiPlayer player = new MidiPlayer();
		player.setProject(project);
		long startTimeInMilliseconds = 500;
		player.seekTo(startTimeInMilliseconds);
		assertEquals(startTimeInMilliseconds, player.getStartTimeOffset());
	}

	@Test
	public void testStart() throws MidiException, IOException {
		MidiPlayer.resetChannelCounter();

		Project project = new Project("testProject", MusicalBeat.BEAT_4_4, Project.DEFAULT_BEATS_PER_MINUTE);
		Track track = TrackTestDataFactory.createSimpleTrackWithoutOffEvents();
		project.putTrack("testTrack", track);

		MidiPlayer player = new MidiPlayer();
		player.setProject(project);
		player.start();
		assertEquals(track.size(), player.getPlayRunnables().size());
	}
}

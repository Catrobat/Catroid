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

package org.catrobat.catroid.pocketmusic;

import android.os.Bundle;

import org.catrobat.catroid.R;
import org.catrobat.catroid.pocketmusic.note.MusicalBeat;
import org.catrobat.catroid.pocketmusic.note.MusicalInstrument;
import org.catrobat.catroid.pocketmusic.note.MusicalKey;
import org.catrobat.catroid.pocketmusic.note.NoteEvent;
import org.catrobat.catroid.pocketmusic.note.NoteLength;
import org.catrobat.catroid.pocketmusic.note.NoteName;
import org.catrobat.catroid.pocketmusic.note.Project;
import org.catrobat.catroid.pocketmusic.note.Track;
import org.catrobat.catroid.pocketmusic.note.trackgrid.TrackToTrackGridConverter;
import org.catrobat.catroid.ui.BaseActivity;

public class PocketMusicActivity extends BaseActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_pocketmusic);

		createDummySong();
	}

	private void createDummySong() {
		int bpm = 60;

		Project project = new Project("Dummy Project", MusicalBeat.BEAT_4_4, bpm);

		Track track = new Track(MusicalKey.VIOLIN, MusicalInstrument.VIOLIN);
		NoteName c1 = NoteName.C1;
		NoteEvent c1On = new NoteEvent(c1, true);
		NoteEvent c1Off = new NoteEvent(c1, false);

		track.addNoteEvent(0, c1On);
		track.addNoteEvent(NoteLength.QUARTER.toTicks(bpm), c1Off);
		track.addNoteEvent(NoteLength.QUARTER.toTicks(bpm) * 2, c1On);
		track.addNoteEvent(NoteLength.QUARTER.toTicks(bpm) * 3, c1Off);

		project.addTrack("Track 1", track);

		TrackToTrackGridConverter.convertTrackToTrackGrid(track, MusicalBeat.BEAT_4_4, bpm);
	}
}

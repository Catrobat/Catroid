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
import android.util.Log;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.SoundInfo;
import org.catrobat.catroid.pocketmusic.note.MusicalBeat;
import org.catrobat.catroid.pocketmusic.note.MusicalInstrument;
import org.catrobat.catroid.pocketmusic.note.MusicalKey;
import org.catrobat.catroid.pocketmusic.note.NoteEvent;
import org.catrobat.catroid.pocketmusic.note.NoteLength;
import org.catrobat.catroid.pocketmusic.note.NoteName;
import org.catrobat.catroid.pocketmusic.note.Project;
import org.catrobat.catroid.pocketmusic.note.Track;
import org.catrobat.catroid.pocketmusic.note.midi.MidiException;
import org.catrobat.catroid.pocketmusic.note.midi.ProjectToMidiConverter;
import org.catrobat.catroid.pocketmusic.note.trackgrid.TrackGridToTrackConverter;
import org.catrobat.catroid.pocketmusic.ui.TrackView;
import org.catrobat.catroid.ui.BaseActivity;
import org.catrobat.catroid.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.util.Random;

public class PocketMusicActivity extends BaseActivity {

	private static final String TAG = PocketMusicActivity.class.getSimpleName();

	private Project project;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		project = createDummyProject();

		setContentView(R.layout.activity_pocketmusic);

		TrackView trackView = (TrackView) findViewById(R.id.musicdroid_note_grid);
		trackView.setTrack(project.getTrack("Track 1"), project.getBeatsPerMinute());
	}

	@Override
	public void finish() {

		TrackView tv = (TrackView) findViewById(R.id.musicdroid_note_grid);
		Track track = TrackGridToTrackConverter.convertTrackGridToTrack(tv.getTrackGrid(), Project.DEFAULT_BEATS_PER_MINUTE);

		for (String trackName : project.getTrackNames()) {
			project.putTrack(trackName, track);
		}

		SoundInfo soundInfo = new SoundInfo();
		soundInfo.setTitle(getString(R.string.pocketmusic_recorded_filename));

		Random randomGenerator = new Random();
		soundInfo.setSoundFileName("MUS-" + Math.abs(randomGenerator.nextInt()) + ".midi");

		ProjectToMidiConverter projectToMidiConverter = new ProjectToMidiConverter();
		File initialFile = new File(soundInfo.getAbsolutePath());
		try {
			projectToMidiConverter.writeProjectAsMidi(project, initialFile);
		} catch (IOException | MidiException e) {
			Log.e(TAG, "Couldn't save midi file (" + soundInfo.getSoundFileName() + ").", e);
		}

		soundInfo.setSoundFileName(Utils.md5Checksum(soundInfo.getAbsolutePath()) + "_" + soundInfo.getSoundFileName());
		File rename = new File(soundInfo.getAbsolutePath());
		initialFile.renameTo(rename);

		ProjectManager.getInstance().getCurrentSprite().getSoundList().add(soundInfo);

		super.finish();
	}

	public Project createDummyProject() {
		int bpm = 60;

		Project project = new Project("Dummy Project", MusicalBeat.BEAT_4_4, bpm);

		Track track = new Track(MusicalKey.VIOLIN, MusicalInstrument.VIOLIN);
		NoteName c1 = NoteName.C1;
		NoteEvent c1On = new NoteEvent(c1, true);
		NoteEvent c1Off = new NoteEvent(c1, false);

		NoteName e1 = NoteName.E1;
		NoteEvent e1On = new NoteEvent(e1, true);
		NoteEvent e1Off = new NoteEvent(e1, false);

		NoteName g1 = NoteName.G1;
		NoteEvent g1On = new NoteEvent(g1, true);
		NoteEvent g1Off = new NoteEvent(g1, false);

		track.addNoteEvent(0, c1On);
		track.addNoteEvent(NoteLength.QUARTER.toTicks(bpm), c1Off);
		track.addNoteEvent(NoteLength.QUARTER.toTicks(bpm) * 2, c1On);
		track.addNoteEvent(NoteLength.QUARTER.toTicks(bpm) * 4, c1Off);

		track.addNoteEvent(0, e1On);
		track.addNoteEvent(NoteLength.QUARTER.toTicks(bpm), e1Off);
		track.addNoteEvent(NoteLength.QUARTER.toTicks(bpm) * 2, e1On);
		track.addNoteEvent(NoteLength.QUARTER.toTicks(bpm) * 3, e1Off);

		track.addNoteEvent(0, g1On);
		track.addNoteEvent(NoteLength.QUARTER.toTicks(bpm), g1Off);
		track.addNoteEvent(NoteLength.QUARTER.toTicks(bpm), g1On);
		track.addNoteEvent(NoteLength.QUARTER.toTicks(bpm) * 2, g1Off);

		project.putTrack("Track 1", track);

		return project;
	}
}

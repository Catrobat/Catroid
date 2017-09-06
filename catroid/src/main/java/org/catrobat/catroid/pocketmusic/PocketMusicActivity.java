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

package org.catrobat.catroid.pocketmusic;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.ViewGroup;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.SoundInfo;
import org.catrobat.catroid.pocketmusic.fastscroll.FastScroller;
import org.catrobat.catroid.pocketmusic.mididriver.MidiNotePlayer;
import org.catrobat.catroid.pocketmusic.note.MusicalBeat;
import org.catrobat.catroid.pocketmusic.note.MusicalKey;
import org.catrobat.catroid.pocketmusic.note.Project;
import org.catrobat.catroid.pocketmusic.note.Track;
import org.catrobat.catroid.pocketmusic.note.midi.MidiException;
import org.catrobat.catroid.pocketmusic.note.midi.MidiToProjectConverter;
import org.catrobat.catroid.pocketmusic.note.midi.ProjectToMidiConverter;
import org.catrobat.catroid.pocketmusic.note.trackgrid.TrackGridToTrackConverter;
import org.catrobat.catroid.pocketmusic.ui.TactScrollRecyclerView;
import org.catrobat.catroid.ui.BaseActivity;
import org.catrobat.catroid.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.util.Random;

public class PocketMusicActivity extends BaseActivity {

	private static final String TAG = PocketMusicActivity.class.getSimpleName();

	private Project project;
	private TactScrollRecyclerView tactScroller;

	private MidiNotePlayer midiDriver;

	private FastScroller fastScroller;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		midiDriver = new MidiNotePlayer();

		String fileName = getIntent().getStringExtra("FILENAME");
		String title = getIntent().getStringExtra("TITLE");

		if (null != fileName) {
			MidiToProjectConverter converter = new MidiToProjectConverter();
			try {
				SoundInfo soundInfo = new SoundInfo();
				soundInfo.setSoundFileName(fileName);

				if (null != ProjectManager.getInstance().getCurrentProject()) {
					project = converter.convertMidiFileToProject(new File(soundInfo.getAbsolutePath()));
					project.setFileName(fileName);
					project.setName(title);
				}
			} catch (MidiException | IOException ignored) {
			}
		}
		if (project == null) {
			project = createEmptyProject();
		}

		setContentView(R.layout.activity_pocketmusic);
		ViewGroup content = (ViewGroup) findViewById(android.R.id.content);

		tactScroller = (TactScrollRecyclerView) findViewById(R.id.tact_scroller);
		tactScroller.setTrack(project.getTrack("Track 1"), project.getBeatsPerMinute());

		fastScroller = (FastScroller) findViewById(R.id.fastscroll);
		fastScroller.setRecyclerView(tactScroller);

		new ScrollController(content, tactScroller, project.getBeatsPerMinute());
	}

	public SoundInfo getSoundInfoForTrack(boolean fileExists) {
		SoundInfo soundInfo = new SoundInfo();

		if (fileExists) {
			soundInfo.setTitle(project.getName());
			soundInfo.setSoundFileName(project.getFileName());
		} else {
			soundInfo.setTitle(getString(R.string.music_recorded_filename));

			Random randomGenerator = new Random();
			soundInfo.setSoundFileName("MUS-" + Math.abs(randomGenerator.nextInt()) + ".midi");
		}
		return soundInfo;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				finish();
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void finish() {

		if (null != project) {

			boolean fileExists = project.getFileName() != null;

			Track track = TrackGridToTrackConverter.convertTrackGridToTrack(tactScroller.getTrackGrid(), Project.DEFAULT_BEATS_PER_MINUTE);

			if (track.isEmpty() && fileExists) {
				new File(project.getFileName()).delete();
				SoundInfo soundInfo = getSoundInfoForTrack(true);
				ProjectManager.getInstance().getCurrentSprite().getSoundList().remove(soundInfo);
			} else if (!track.isEmpty()) {
				for (String trackName : project.getTrackNames()) {
					project.putTrack(trackName, track);
				}

				SoundInfo soundInfo = getSoundInfoForTrack(fileExists);

				ProjectToMidiConverter projectToMidiConverter = new ProjectToMidiConverter();

				File initialFile = new File(soundInfo.getAbsolutePath());
				try {
					initialFile.getParentFile().mkdirs();
					projectToMidiConverter.writeProjectAsMidi(project, initialFile);
				} catch (IOException | MidiException e) {
					Log.e(TAG, "Couldn't save midi file (" + soundInfo.getSoundFileName() + ").", e);
				}

				if (!fileExists) {
					soundInfo.setSoundFileName(Utils.md5Checksum(soundInfo.getAbsolutePath()) + "_" + soundInfo.getSoundFileName());
					File rename = new File(soundInfo.getAbsolutePath());
					initialFile.renameTo(rename);

					ProjectManager.getInstance().getCurrentSprite().getSoundList().add(soundInfo);
				}
			}
		}
		super.finish();
	}

	private Project createEmptyProject() {
		Project project = new Project("Untitled song", MusicalBeat.BEAT_4_4, Project.DEFAULT_BEATS_PER_MINUTE);
		Track track = new Track(MusicalKey.VIOLIN, Project.DEFAULT_INSTRUMENT);
		project.putTrack("Track 1", track);

		return project;
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (midiDriver != null) {
			midiDriver.start();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (midiDriver != null) {
			midiDriver.stop();
		}
	}
}

/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2020 The Catrobat Team
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

package org.catrobat.catroid.pocketmusic.mididriver;

import android.os.Handler;
import android.os.Looper;

import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.pocketmusic.note.MusicalInstrument;
import org.catrobat.catroid.pocketmusic.note.NoteEvent;
import org.catrobat.catroid.pocketmusic.note.NoteLength;
import org.catrobat.catroid.pocketmusic.note.Project;
import org.catrobat.catroid.pocketmusic.note.Track;
import org.catrobat.catroid.pocketmusic.note.midi.MidiException;
import org.catrobat.catroid.pocketmusic.note.midi.MidiToProjectConverter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MidiPlayer {
	private static MidiNotePlayer notePlayer = new MidiNotePlayer();
	public static final byte MAX_CHANNELS = 15;
	private static byte channelCounter = 0;

	private Handler handler;
	private List<MidiRunnable> playRunnables = new ArrayList<>();
	private Project project;
	private long startTimeOffset;
	private byte channel;
	private long startTime;
	private String sourceFilePath;

	private MusicalInstrument instrument;
	private int tempo;
	private float volume;
	private Sprite startedBySprite;

	private boolean paused;

	public MidiPlayer() throws MidiException {
		if (channelCounter > MAX_CHANNELS) {
			throw new MidiException("Number of midi channels exceeded");
		}
		if (channelCounter == 10) {
			channelCounter++;
		}

		handler = new Handler(Looper.getMainLooper());

		reset();

		channel = channelCounter;
		channelCounter++;
	}

	public void reset() {
		playRunnables.clear();
		project = null;
		startedBySprite = null;
		sourceFilePath = null;
		startTimeOffset = 0;
		instrument = Project.DEFAULT_INSTRUMENT;
		tempo = 100;
		volume = 70f;
		paused = false;
	}

	public void setPathToSoundFile(String sourceFilePath) throws IOException, MidiException {
		this.sourceFilePath = sourceFilePath;
		File file = new File(sourceFilePath);
		MidiToProjectConverter converter = new MidiToProjectConverter();
		project = converter.convertMidiFileToProject(file);
	}

	public void setStartedBySprite(Sprite startedBySprite) {
		this.startedBySprite = startedBySprite;
	}

	public boolean seekTo(long startTimeInMilSeconds) {
		if (project == null) {
			return false;
		}
		startTimeOffset = startTimeInMilSeconds;
		return true;
	}

	public boolean start() {
		return start(instrument);
	}
	private boolean start(MusicalInstrument instrument) {
		if (project == null) {
			return false;
		}

		stopPlaying();

		if (!MidiNotePlayer.isInitialized()) {
			notePlayer.start();
		}
		notePlayer.setInstrument(channel, instrument);
		notePlayer.setVolume(channel, (int) volume);

		long playLength = NoteLength.QUARTER.toMilliseconds(Project.DEFAULT_BEATS_PER_MINUTE);
		playLength /= tempo / 100;

		startTime = System.currentTimeMillis();

		Set<String> trackNames = project.getTrackNames();
		for (String trackName : trackNames) {
			Track track = project.getTrack(trackName);

			for (long tick : track.getSortedTicks()) {
				if (tick < startTimeOffset) {
					continue;
				}
				List<NoteEvent> noteEventList = track.getNoteEventsForTick(tick);
				for (NoteEvent noteEvent : noteEventList) {

					MidiRunnable runnable;
					if (noteEvent.isNoteOn()) {
						runnable = new MidiRunnable(MidiSignals.NOTE_ON, noteEvent.getNoteName(),
								playLength, handler, notePlayer, null, channel);
						runnable.setManualNoteOff(true);
					} else {
						runnable = new MidiRunnable(MidiSignals.NOTE_OFF, noteEvent.getNoteName(),
								playLength, handler, notePlayer, null, channel);
					}
					runnable.setScheduledTime(startTime + tick / (tempo / 100));
					handler.postDelayed(runnable, tick / (tempo / 100));
					playRunnables.add(runnable);
				}
			}
		}
		return true;
	}

	public void stopPlaying() {
		for (MidiRunnable r : playRunnables) {
			handler.removeCallbacks(r);
			handler.post(new MidiRunnable(MidiSignals.NOTE_OFF, r.getNoteName(), 0, handler,
					notePlayer, null, channel));
		}
	}

	public void pause() {
		long currentTime = System.currentTimeMillis();
		for (MidiRunnable r : playRunnables) {
			if (r.getScheduledTime() > currentTime) {
				handler.removeCallbacks(r);
			} else {
				playRunnables.remove(r);
			}
		}
		paused = true;
	}

	public void resume() {
		if (paused) {
			long currentTime = System.currentTimeMillis();
			long referenceTime = playRunnables.get(0).getScheduledTime();
			for (MidiRunnable r : playRunnables) {
				long tick = r.getScheduledTime() - referenceTime;
				handler.postDelayed(r, tick);
				r.setScheduledTime(currentTime + tick);
			}
			paused = false;
		}
	}

	public boolean isPlaying() {
		return handler.hasMessages(0);
	}

	public boolean isPaused() {
		return paused;
	}

	public void setInstrument(MusicalInstrument instrument) {
		this.instrument = instrument;
	}

	public void setTempo(int tempo) {
		this.tempo = tempo;
	}

	public void setVolume(float volume) {
		this.volume = volume;
	}

	public String getPathToSoundFile() {
		return sourceFilePath;
	}

	public Sprite getStartedBySprite() {
		return startedBySprite;
	}

	public static MidiNotePlayer getNotePlayer() {
		return notePlayer;
	}

	public MusicalInstrument getInstrument() {
		return instrument;
	}

	public int getTempo() {
		return tempo;
	}

	public float getVolume() {
		return volume;
	}

	public long getCurrentPosition() {
		return System.currentTimeMillis() - startTime;
	}
}

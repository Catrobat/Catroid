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

package org.catrobat.catroid.pocketmusic.mididriver;

import android.util.Log;

import org.catrobat.catroid.content.SoundFilePathWithSprite;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.pocketmusic.note.Drum;
import org.catrobat.catroid.pocketmusic.note.MusicalInstrument;
import org.catrobat.catroid.pocketmusic.note.Project;
import org.catrobat.catroid.pocketmusic.note.midi.MidiException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import androidx.annotation.VisibleForTesting;

public class MidiSoundManager {
	private MusicalInstrument instrument = Project.DEFAULT_INSTRUMENT;
	private float tempo = 60.0f;
	private float volume = 70.0f;

	public static final int MAX_MIDI_PLAYERS = 15;

	private static final String TAG = MidiSoundManager.class.getSimpleName();
	private static final MidiSoundManager INSTANCE = new MidiSoundManager();

	private final List<MidiPlayer> midiPlayers = new ArrayList<>(MAX_MIDI_PLAYERS);

	private Set<SoundFilePathWithSprite> startedSoundfilePaths = new HashSet<>();

	private static final int MIN_TEMPO_PERCENT = 20;
	private static final int MAX_TEMPO_PERCENT = 500;

	long pausedUntil = 0;

	@VisibleForTesting
	public MidiSoundManager() {
	}

	public static MidiSoundManager getInstance() {
		return INSTANCE;
	}

	public void playSoundFile(String soundFilePath, Sprite sprite) {
		playSoundFileWithStartTime(soundFilePath, sprite, 0);
	}

	public void playSoundFileWithStartTime(String soundFilePath, Sprite sprite, int startTimeInMilSeconds) {
		MidiPlayer midiPlayer = getAvailableMidiPlayer();
		if (midiPlayer != null) {
			try {
				midiPlayer.setStartedBySprite(sprite);
				midiPlayer.setPathToSoundFile(soundFilePath);
				midiPlayer.setInstrument(instrument);
				midiPlayer.setTempo(tempo);
				midiPlayer.setVolume(this.volume * 127 / 100);
				if (pausedUntil > System.currentTimeMillis() + startTimeInMilSeconds) {
					midiPlayer.seekTo(pausedUntil - System.currentTimeMillis() + startTimeInMilSeconds);
				} else {
					midiPlayer.seekTo(startTimeInMilSeconds);
				}
				midiPlayer.start();
			} catch (Exception exception) {
				Log.e(TAG, "Couldn't play sound file '" + soundFilePath + "'", exception);
			}
			startedSoundfilePaths.add(new SoundFilePathWithSprite(soundFilePath, sprite));
		}
	}

	public void playDrumForBeats(Drum drum, float beats, Sprite sprite) {
		MidiPlayer midiPlayer = getAvailableMidiPlayer();
		if (midiPlayer != null) {
			try {
				midiPlayer.setStartedBySprite(sprite);
				midiPlayer.setTempo(tempo);
				midiPlayer.setVolume(this.volume * 127 / 100);
				midiPlayer.playDrumForBeats(drum, beats);
			} catch (Exception exception) {
				Log.e(TAG, "Couldn't play drums", exception);
			}
		}
	}

	private MidiPlayer getAvailableMidiPlayer() {
		for (MidiPlayer midiPlayer : midiPlayers) {
			if (!midiPlayer.isPlaying()) {
				midiPlayer.reset();
				return midiPlayer;
			}
		}

		if (midiPlayers.size() < MAX_MIDI_PLAYERS) {
			MidiPlayer midiPlayer;
			try {
				midiPlayer = new MidiPlayer();
			} catch (MidiException exception) {
				Log.e(TAG, "No midi channels available", exception);
				return null;
			}

			midiPlayers.add(midiPlayer);
			return midiPlayer;
		}
		Log.d(TAG, "All MidiPlayer instances in use");
		return null;
	}

	public void stopSameSoundInSprite(String pathToSoundFile, Sprite sprite) {
		for (MidiPlayer midiPlayer : midiPlayers) {
			if (midiPlayer.isPlaying() && midiPlayer.getStartedBySprite() == sprite
					&& midiPlayer.getPathToSoundFile().equals(pathToSoundFile)) {
				midiPlayer.stopPlaying();
			}
		}
	}

	public void stopAllSounds() {
		for (MidiPlayer midiPlayer : midiPlayers) {
			if (midiPlayer.isPlaying()) {
				midiPlayer.stopPlaying();
			}
		}
	}

	public void pause() {
		for (MidiPlayer midiPlayer : midiPlayers) {
			if (midiPlayer.isPlaying()) {
				midiPlayer.pause();
			} else {
				midiPlayer.reset();
			}
		}
	}

	public void playNoteForBeats(int midiValue, float beats) {
		MidiPlayer midiPlayer = getAvailableMidiPlayer();
		if (midiPlayer != null) {
			midiPlayer.setInstrument(instrument);
			midiPlayer.setTempo(tempo);
			midiPlayer.setVolume(this.volume * 127 / 100);
			midiPlayer.playNoteForBeats(midiValue, beats);
		}
	}

	public long getDurationForBeats(float beats) {
		return (long) (60000 / tempo * beats);
	}

	public void resume() {
		for (MidiPlayer midiPlayer : midiPlayers) {
			if (!midiPlayer.isPlaying()) {
				midiPlayer.resume();
			}
		}
	}

	public boolean isSoundInSpritePlaying(Sprite sprite, String soundFilePath) {
		for (MidiPlayer midiPlayer : midiPlayers) {
			if (midiPlayer.isPlaying() && midiPlayer.getStartedBySprite() == sprite
					&& midiPlayer.getPathToSoundFile().equals(soundFilePath)) {
				return true;
			}
		}
		return false;
	}

	public void setInstrument(MusicalInstrument instrumentParam) {
		instrument = instrumentParam;
	}

	public MusicalInstrument getInstrument() {
		return instrument;
	}

	public void setTempo(float tempo) {
		if (tempo > MAX_TEMPO_PERCENT) {
			this.tempo = MAX_TEMPO_PERCENT;
		} else if (tempo < MIN_TEMPO_PERCENT) {
			this.tempo = MIN_TEMPO_PERCENT;
		} else {
			this.tempo = tempo;
		}
	}

	public float getTempo() {
		return tempo;
	}

	public void setVolume(float volume) {
		if (volume > 100) {
			this.volume = 100;
		} else if (volume < 0) {
			this.volume = 0;
		} else {
			this.volume = volume;
		}
		for (MidiPlayer midiPlayer : midiPlayers) {
			midiPlayer.setVolume(this.volume * 127 / 100);
		}
	}

	public void reset() {
		setTempo(60);
	}

	public float getVolume() {
		return volume;
	}

	public List<MidiPlayer> getMidiPlayers() {
		return midiPlayers;
	}

	public Set<SoundFilePathWithSprite> getStartedSoundfilePaths() {
		return startedSoundfilePaths;
	}
}

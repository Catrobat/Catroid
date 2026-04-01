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
package org.catrobat.catroid.io;

import android.media.MediaPlayer;
import android.util.Log;

import org.catrobat.catroid.content.MediaPlayerWithSoundDetails;
import org.catrobat.catroid.content.SoundBackup;
import org.catrobat.catroid.content.SoundFilePathWithSprite;
import org.catrobat.catroid.content.Sprite;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import androidx.annotation.VisibleForTesting;

/**
 * As long as both OpenGL render() and StageDialog access the SoundManager, the public methods have to stay
 * synchronized.
 */
public class SoundManager {
	public static final int MAX_MEDIA_PLAYERS = 7;

	private static final String TAG = SoundManager.class.getSimpleName();
	private static final SoundManager INSTANCE = new SoundManager();

	private final List<MediaPlayerWithSoundDetails> mediaPlayers = new ArrayList<>(MAX_MEDIA_PLAYERS);
	private float volume = 70.0f;

	private final Set<SoundFilePathWithSprite> recentlyStoppedSoundfilePaths = new HashSet<>();

	@VisibleForTesting
	public SoundManager() {
	}

	public static SoundManager getInstance() {
		return INSTANCE;
	}

	public synchronized void playSoundFile(String soundFilePath, Sprite sprite) {
		playSoundFileWithStartTime(soundFilePath, sprite, 0);
	}

	public synchronized void playSoundFileWithStartTime(String soundFilePath,
			Sprite sprite, int startTimeInMilSeconds) {
		stopSameSoundInSprite(soundFilePath, sprite);
		MediaPlayerWithSoundDetails mediaPlayer = getAvailableMediaPlayer();
		if (mediaPlayer != null) {
			try {
				mediaPlayer.setStartedBySprite(sprite);
				mediaPlayer.setPathToSoundFile(soundFilePath);
				mediaPlayer.setDataSource(soundFilePath);
				mediaPlayer.prepare();
				mediaPlayer.seekTo(startTimeInMilSeconds);
				mediaPlayer.start();
			} catch (Exception exception) {
				Log.e(TAG, "Couldn't play sound file '" + soundFilePath + "'", exception);
			}
		}
	}

	public synchronized void stopSameSoundInSprite(String pathToSoundFile, Sprite sprite) {
		for (MediaPlayerWithSoundDetails mediaPlayer : mediaPlayers) {
			if (mediaPlayer.isPlaying() && mediaPlayer.getStartedBySprite() == sprite
					&& mediaPlayer.getPathToSoundFile().equals(pathToSoundFile)) {
				mediaPlayer.stop();
				recentlyStoppedSoundfilePaths.add(new SoundFilePathWithSprite(
						mediaPlayer.getPathToSoundFile(), sprite));
			}
		}
	}

	public synchronized Set<SoundFilePathWithSprite> getRecentlyStoppedSoundfilePaths() {
		return recentlyStoppedSoundfilePaths;
	}

	public synchronized float getDurationOfSoundFile(String pathToSoundfile) {
		MediaPlayer mediaPlayer = getAvailableMediaPlayer();
		float duration = 0f;
		if (mediaPlayer != null) {
			try {
				mediaPlayer.setDataSource(pathToSoundfile);
				mediaPlayer.prepare();
				duration = mediaPlayer.getDuration();
				mediaPlayer.stop();
			} catch (Exception exception) {
				Log.e(TAG, "Couldn't play sound file '" + pathToSoundfile + "'", exception);
			}
		}
		return duration;
	}

	private MediaPlayerWithSoundDetails getAvailableMediaPlayer() {
		for (MediaPlayerWithSoundDetails mediaPlayer : mediaPlayers) {
			if (!mediaPlayer.isPlaying()) {
				mediaPlayer.reset();
				return mediaPlayer;
			}
		}

		if (mediaPlayers.size() < MAX_MEDIA_PLAYERS) {
			MediaPlayerWithSoundDetails mediaPlayer = new MediaPlayerWithSoundDetails();
			mediaPlayers.add(mediaPlayer);
			setVolume(volume);
			return mediaPlayer;
		}
		Log.d(TAG, "All MediaPlayer instances in use");
		return null;
	}

	public synchronized void setVolume(float volume) {
		if (volume > 100.0f) {
			volume = 100.0f;
		} else if (volume < 0.0f) {
			volume = 0.0f;
		}

		this.volume = volume;
		float volumeScalar = volume * 0.01f;
		for (MediaPlayer mediaPlayer : mediaPlayers) {
			mediaPlayer.setVolume(volumeScalar, volumeScalar);
		}
	}

	public synchronized float getVolume() {
		return this.volume;
	}

	public synchronized void clear() {
		for (MediaPlayerWithSoundDetails mediaPlayer : mediaPlayers) {
			mediaPlayer.reset();
			mediaPlayer.release();
		}
		mediaPlayers.clear();
		recentlyStoppedSoundfilePaths.clear();
	}

	public synchronized void pause() {
		for (MediaPlayer mediaPlayer : mediaPlayers) {
			if (mediaPlayer.isPlaying()) {
				mediaPlayer.pause();
			} else {
				mediaPlayer.reset();
			}
		}
	}

	public synchronized void resume() {
		for (MediaPlayer mediaPlayer : mediaPlayers) {
			if (!mediaPlayer.isPlaying()) {
				mediaPlayer.start();
			}
		}
	}

	public synchronized void stopAllSounds() {
		for (MediaPlayer mediaPlayer : mediaPlayers) {
			if (mediaPlayer.isPlaying()) {
				mediaPlayer.stop();
			}
		}
	}

	public List<SoundBackup> getPlayingSoundBackups() {
		List<SoundBackup> backupList = new ArrayList<>();
		for (MediaPlayerWithSoundDetails mediaPlayer : mediaPlayers) {
			if (mediaPlayer.isPlaying()) {
				backupList.add(new SoundBackup(mediaPlayer.getPathToSoundFile(), mediaPlayer.getStartedBySprite(), mediaPlayer.getCurrentPosition()));
			}
		}
		return backupList;
	}

	public List<MediaPlayerWithSoundDetails> getMediaPlayers() {
		return mediaPlayers;
	}
}

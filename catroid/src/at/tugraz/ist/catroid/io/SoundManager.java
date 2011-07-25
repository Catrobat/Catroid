/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010  Catroid development team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package at.tugraz.ist.catroid.io;

import java.io.IOException;
import java.util.ArrayList;

import android.media.MediaPlayer;

public class SoundManager {
	private ArrayList<MediaPlayer> mediaPlayers;

	private transient double volume = 70.0;

	public static final int MAX_MEDIA_PLAYERS = 7;
	private static SoundManager soundManager = null;

	private SoundManager() {
		mediaPlayers = new ArrayList<MediaPlayer>();
	}

	public synchronized static SoundManager getInstance() {
		if (soundManager == null) {
			soundManager = new SoundManager();
		}
		return soundManager;
	}

	public MediaPlayer getMediaPlayer() {
		for (MediaPlayer mediaPlayer : mediaPlayers) {
			if (!mediaPlayer.isPlaying()) {
				mediaPlayer.reset();
				return mediaPlayer;
			}
		}
		if (mediaPlayers.size() < MAX_MEDIA_PLAYERS) {
			MediaPlayer mediaPlayer = new MediaPlayer();
			mediaPlayers.add(mediaPlayer);
			return mediaPlayer;
		} else {
			return null;
		}
	}

	public synchronized MediaPlayer playSoundFile(String pathToSoundfile) {
		MediaPlayer mediaPlayer = getMediaPlayer();
		if (mediaPlayer != null) {
			try {
				mediaPlayer.setDataSource(pathToSoundfile);
				mediaPlayer.prepare();
				mediaPlayer.start();
			} catch (IOException e) {
				throw new IllegalArgumentException("IO error", e);
			}
		}
		return mediaPlayer;
	}

	public synchronized void setVolume(double volume) {
		this.volume = volume;
		float vol;
		vol = (float) (volume * 0.01);
		for (MediaPlayer mediaPlayer : mediaPlayers) {
			mediaPlayer.setVolume(vol, vol);
		}
	}

	public double getVolume() {
		return this.volume;
	}

	public synchronized void clear() {
		for (MediaPlayer mediaPlayer : mediaPlayers) {
			mediaPlayer.release();
		}
		mediaPlayers.clear();
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

}

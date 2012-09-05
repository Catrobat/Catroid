/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010-2011 The Catroid Team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://www.catroid.org/catroid_license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *   
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package at.tugraz.ist.catroid.io;

import java.io.IOException;
import java.util.ArrayList;

import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import at.tugraz.ist.catroid.stage.NativeAppActivity;

public class SoundManager {
	private ArrayList<MediaPlayer> mediaPlayers;

	private transient float volume = 70.0f;

	public static final int MAX_MEDIA_PLAYERS = 7;
	private static SoundManager soundManager;

	private SoundManager() {
		mediaPlayers = new ArrayList<MediaPlayer>(MAX_MEDIA_PLAYERS);
	}

	public synchronized static SoundManager getInstance() {
		if (soundManager == null) {
			soundManager = new SoundManager();
		}
		return soundManager;
	}

	public MediaPlayer getMediaPlayer() {
		for (int i = 0; i < mediaPlayers.size(); i++) {
			if (!mediaPlayers.get(i).isPlaying()) {
				mediaPlayers.get(i).reset();
				setVolume(volume);
				return mediaPlayers.get(i);
			}
		}
		if (mediaPlayers.size() < MAX_MEDIA_PLAYERS) {
			MediaPlayer mediaPlayer = new MediaPlayer();
			mediaPlayers.add(mediaPlayer);
			setVolume(volume);
			return mediaPlayer;
		} else {
			return null;
		}
	}

	public synchronized MediaPlayer playSoundFile(String pathToSoundfile) {
		MediaPlayer mediaPlayer = getMediaPlayer();
		if (mediaPlayer != null) {
			try {
				if (!NativeAppActivity.isRunning()) {
					mediaPlayer.setDataSource(pathToSoundfile);
				} else {
					AssetFileDescriptor afd = NativeAppActivity.getContext().getAssets().openFd(pathToSoundfile);
					mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
				}
				mediaPlayer.prepare();
				mediaPlayer.start();
			} catch (IOException e) {
				throw new IllegalArgumentException("IO error", e);
			}
		}
		return mediaPlayer;
	}

	public synchronized void setVolume(float volume) {
		this.volume = volume;
		float volumeScalar = volume * 0.01f;
		for (int i = 0; i < mediaPlayers.size(); i++) {
			mediaPlayers.get(i).setVolume(volumeScalar, volumeScalar);
		}
	}

	public float getVolume() {
		return this.volume;
	}

	public synchronized void clear() {
		for (int i = 0; i < mediaPlayers.size(); i++) {
			mediaPlayers.get(i).release();
		}
		mediaPlayers.clear();
	}

	public synchronized void pause() {
		for (int i = 0; i < mediaPlayers.size(); i++) {
			if (mediaPlayers.get(i).isPlaying()) {
				mediaPlayers.get(i).pause();
			} else {
				mediaPlayers.get(i).reset();
			}
		}
	}

	public synchronized void resume() {
		for (int i = 0; i < mediaPlayers.size(); i++) {
			if (!mediaPlayers.get(i).isPlaying()) {
				mediaPlayers.get(i).start();
			}
		}
	}

	public synchronized void stopAllSounds() {
		for (int i = 0; i < mediaPlayers.size(); i++) {
			if (mediaPlayers.get(i).isPlaying()) {
				mediaPlayers.get(i).stop();
			}
		}
	}
}

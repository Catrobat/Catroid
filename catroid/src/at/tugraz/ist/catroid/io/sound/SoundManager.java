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

package at.tugraz.ist.catroid.io.sound;

import java.util.ArrayList;

import android.media.MediaPlayer;
import android.util.Log;

public class SoundManager {
	private ArrayList<MediaPlayer> mediaPlayers;

	public static final int MAX_MEDIA_PLAYERS = 10;
	private static SoundManager mSoundManager = null;

	private SoundManager() {
		mediaPlayers = new ArrayList<MediaPlayer>();
	}

	public synchronized static SoundManager getInstance() {
		if (mSoundManager == null) {
			mSoundManager = new SoundManager();
		}
		return mSoundManager;
	}

	public synchronized MediaPlayer getMediaPlayer() {
		for (MediaPlayer mediaPlayer : mediaPlayers) {
			if (!mediaPlayer.isPlaying()) {
				Log.d("SoundManager", "Recycling MediaPlayer. Number of players: " + mediaPlayers.size());
				mediaPlayer.reset();
				return mediaPlayer;
			}
		}

		if (mediaPlayers.size() < MAX_MEDIA_PLAYERS) {
			MediaPlayer mediaPlayer = new MediaPlayer();
			mediaPlayers.add(mediaPlayer);
			Log.d("SoundManager", "Created new MediaPlayer. New number of players: " + mediaPlayers.size());
			return mediaPlayer;
		} else {
			return null;
		}
	}

	public synchronized void clear() {
		for (MediaPlayer mediaPlayer : mediaPlayers)
			mediaPlayer.release();
		mediaPlayers.clear();
	}

	public synchronized void pause() {
		for (MediaPlayer mediaPlayer : mediaPlayers)
			if (mediaPlayer.isPlaying())
				mediaPlayer.pause();
	}

	public synchronized void resume() {
		for (MediaPlayer mediaPlayer : mediaPlayers)
			if (!mediaPlayer.isPlaying())
				mediaPlayer.start();
	}
}

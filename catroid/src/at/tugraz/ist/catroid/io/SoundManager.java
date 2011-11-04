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

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;

public class SoundManager {
	private ArrayList<Music> mediaPlayers;

	private transient double volume = 70.0;

	public static final int MAX_MEDIA_PLAYERS = 7;
	private static SoundManager soundManager = null;

	private SoundManager() {
		mediaPlayers = new ArrayList<Music>();
	}

	public synchronized static SoundManager getInstance() {
		if (soundManager == null) {
			soundManager = new SoundManager();
		}
		return soundManager;
	}

	public void getMediaPlayer() {
		for (Music mediaPlayer : new ArrayList<Music>(mediaPlayers)) {
			if (!mediaPlayer.isPlaying()) {
				mediaPlayer.dispose();
				mediaPlayers.remove(mediaPlayer);
				//return mediaPlayer;
			}
		}
		//		if (mediaPlayers.size() < MAX_MEDIA_PLAYERS) {
		//			MediaPlayer mediaPlayer = new MediaPlayer();
		//			mediaPlayers.add(mediaPlayer);
		//			return mediaPlayer;
		//		} else {
		//			return null;
		//		}
	}

	public synchronized void playSoundFile(String pathToSoundfile) {
		getMediaPlayer();
		Music music = Gdx.audio.newMusic(Gdx.files.internal(pathToSoundfile));
		mediaPlayers.add(music);
		music.play();

		//		//if (mediaPlayer != null) {
		//			try {
		//				if (!NativeAppActivity.isRunning()) {
		//					mediaPlayer.setDataSource(pathToSoundfile);
		//				} else {
		//					AssetFileDescriptor afd = NativeAppActivity.getContext().getAssets().openFd(pathToSoundfile);
		//					mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
		//				}
		//				mediaPlayer.prepare();
		//				mediaPlayer.start();
		//			} catch (IOException e) {
		//				throw new IllegalArgumentException("IO error", e);
		//			}
		//		//}
		//		return mediaPlayer;
	}

	public synchronized void setVolume(double volume) {
		//		this.volume = volume;
		//		float vol;
		//		vol = (float) (volume * 0.01);
		//		for (MediaPlayer mediaPlayer : mediaPlayers) {
		//			mediaPlayer.setVolume(vol, vol);
		//		}
	}

	public double getVolume() {
		return this.volume;
	}

	public synchronized void clear() {
		for (Music mediaPlayer : mediaPlayers) {
			mediaPlayer.dispose();
		}
		mediaPlayers.clear();
	}

	public synchronized void pause() {
		for (Music mediaPlayer : mediaPlayers) {
			if (mediaPlayer.isPlaying()) {
				mediaPlayer.pause();
			} else {
				mediaPlayer.dispose();
			}
		}
	}

	public synchronized void resume() {
		for (Music mediaPlayer : mediaPlayers) {
			if (!mediaPlayer.isPlaying()) {
				mediaPlayer.play();
			}
		}
	}

	public synchronized void stopAllSounds() {
		for (Music mediaPlayer : mediaPlayers) {
			if (mediaPlayer.isPlaying()) {
				mediaPlayer.stop();
			}
		}
	}

}

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

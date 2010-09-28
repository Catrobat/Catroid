package at.tugraz.ist.catroid.stage;

import java.util.ArrayList;

import android.media.MediaPlayer;

/**
 * 
 * Handles multiple Media Players 
 * creates and deletes them when needed
 * 
 * @author Thomas Holzmann
 *
 */
public class SoundManager {
	
	ArrayList<MediaPlayer> mMediaPlayerList;
	
	private static SoundManager mSoundManager = null;
	
	private SoundManager(){
		super();
	}
	
	public static SoundManager getInstance(){
		if (mSoundManager == null){
			mSoundManager = new SoundManager();
		}
		return mSoundManager;
	}
	
	public MediaPlayer getMediaPlayer(){
		// try to find one player that has already finished playing
		for (int i=0; i <mMediaPlayerList.size(); i++){
			if (!mMediaPlayerList.get(i).isPlaying()){
				MediaPlayer player = mMediaPlayerList.get(i);
				player.reset();
				return player;
			}
		}
		
		//else create a new one
		MediaPlayer player = new MediaPlayer();
		mMediaPlayerList.add(player);
		return player;
	}

	public void release(){
		for (int i=0; i <mMediaPlayerList.size(); i++)
			mMediaPlayerList.get(i).release();
	}

	public void pause(){
		for (int i=0; i <mMediaPlayerList.size(); i++)
			mMediaPlayerList.get(i).pause();
	}
	
	public void resume(){
		for (int i=0; i <mMediaPlayerList.size(); i++)
			mMediaPlayerList.get(i).start();
	}
}

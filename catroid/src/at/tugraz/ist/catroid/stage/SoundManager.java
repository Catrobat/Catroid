package at.tugraz.ist.catroid.stage;

import java.util.ArrayList;

import android.media.MediaPlayer;
import android.util.Log;

/**
 * 
 * Handles multiple Media Players 
 * creates and deletes them when needed
 * 
 * @author Thomas Holzmann
 *
 */
public class SoundManager {
	//TODO release MediaPlayers if too much are unused
	
	private boolean mIsReleased = false; 
	private ArrayList<MediaPlayer> mMediaPlayerList;
	// NOTE I have tested this with up to 15 MediaPlayers and it worked without problems
	
	private static SoundManager mSoundManager = null;
	
	private SoundManager(){
		super();
		mMediaPlayerList = new ArrayList<MediaPlayer>();
	}
	
	public static SoundManager getInstance(){
		if (mSoundManager == null){
			mSoundManager = new SoundManager();
		}
		return mSoundManager;
	}
	
	public MediaPlayer getMediaPlayer(){
		if (mIsReleased){
			// quick fix for media players which still exist after restarting activity
			Log.i("SoundManager", "isReleased");
			mMediaPlayerList = new ArrayList<MediaPlayer>();
			mIsReleased = false;
		}
		
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
		mIsReleased = true;
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

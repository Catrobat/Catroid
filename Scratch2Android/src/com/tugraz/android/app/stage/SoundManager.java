package com.tugraz.android.app.stage;

import java.util.HashMap;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

public class SoundManager {
	private  SoundPool mSoundPool;
	private  HashMap<Integer,Integer> mSoundPoolMap;
	private  AudioManager  mAudioManager;
	private  Context mContext;
	
	public SoundManager() {
	    //mContext = theContext;
	    mSoundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 0);
//	    mSoundPoolMap = new HashMap<Integer,Integer>();
//	    mAudioManager = (AudioManager)mContext.getSystemService(Context.AUDIO_SERVICE);
	}
	
//	public void addSound(int index, int SoundID)
//	{
//	    mSoundPoolMap.put(index, mSoundPool.load(mContext, SoundID, 1));
//	}
	
	public void playSound(String path)
	{
//	float streamVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
//	streamVolume = streamVolume / mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
	int soundId = mSoundPool.load(path, 1);
	    mSoundPool.play(soundId, 1, 1, 1, 0, 1f);
	}
	 
//	public void playLoopedSound(int index)
//	{
//	    float streamVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
//	    streamVolume = streamVolume / mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
//	    mSoundPool.play(mSoundPoolMap.get(index), streamVolume, streamVolume, 1, -1, 1f);
//	}
	
	public void stopAndRelease() {
		//TODO implement
	}
	
	public boolean isPlaying() {
		//TODO implement
		return false;
	}
	
	//TODO ich will dem soundmanager nur einen pfad geben, den er abspielen soll
	
	
}

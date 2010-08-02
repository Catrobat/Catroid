package com.tugraz.android.app.stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

import com.tugraz.android.app.BrickDefine;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.util.Log;

public class Sprite extends Thread implements Observer, OnCompletionListener{
	
	private String mSpriteName;
	private StageView mStage;
	private ArrayList<HashMap<String, String>> mCommandList;
	private MediaPlayer mMediaPlayer; 
	private int mCommandCount = 0;
	private int mCurrentXPosition = 0;
	private int mCurrentYPosition = 0;
	private String mCurrentImage = "";
	
	public Sprite(StageView view, ArrayList<HashMap<String, String>> commandList, String name){
		mStage = view;
		mCommandList = commandList;
		mMediaPlayer = new MediaPlayer();
		mMediaPlayer.setOnCompletionListener(this);
		mSpriteName = name;
		
	}

	@Override
	public void run() {
		doNextCommand();
	}
	
	@Override
	public void update(Observable observable, Object data) {
		doNextCommand();

	}
	
	/**
	 * executes the next command from the contentArrayList of the contentManager
	 */
	private synchronized void doNextCommand() {
		if (mCommandList.size() <= mCommandCount) { 
			// abort if mCommandCount has run through all commands to execute															
			mCommandCount = 0;
			return;
		}
		

		HashMap<String, String> map = mCommandList.get(mCommandCount);

		int type = Integer.parseInt(map.get(BrickDefine.BRICK_TYPE));
		switch (type) {
		case BrickDefine.SET_BACKGROUND:
			mCurrentImage = map.get(BrickDefine.BRICK_VALUE);
			mStage.getThread().setBackground(map.get(BrickDefine.BRICK_VALUE));
			mCommandCount++;
			doNextCommand();
			break;
			
		case BrickDefine.SET_COSTUME:
			mCurrentImage = map.get(BrickDefine.BRICK_VALUE);
			mStage.getThread().addBitmapToDraw(mSpriteName, map.get(BrickDefine.BRICK_VALUE), mCurrentXPosition, mCurrentYPosition);
			mCommandCount++;
			doNextCommand();
			break;

		case BrickDefine.PLAY_SOUND: 
			try {
				mMediaPlayer.reset();
				mMediaPlayer.setDataSource(map.get(BrickDefine.BRICK_VALUE));
				mMediaPlayer.prepare();
				mMediaPlayer.start();

			} catch (IOException e) {
				Log.w("Sprite", "Could not play sound file");
			} catch (IllegalArgumentException e) {
				Log.w("Sprite", "Could not play sound file");
			}

			mCommandCount++;
			doNextCommand();
			break;

		case BrickDefine.WAIT:
			mCommandCount++;
			brickWait(Float.parseFloat(map.get(BrickDefine.BRICK_VALUE)));
			break;
			
		case BrickDefine.GO_TO:
			mCurrentXPosition = Integer.parseInt(map.get(BrickDefine.BRICK_VALUE));
			mCurrentYPosition = Integer.parseInt(map.get(BrickDefine.BRICK_VALUE_1));
			mStage.getThread().changeBitmapPosition(mSpriteName, mCurrentXPosition, mCurrentYPosition);
			mCommandCount++;
			doNextCommand();
			break;
			
		case BrickDefine.HIDE:
			mStage.getThread().removeBitmapToDraw(mSpriteName);
			mCommandCount++;
			doNextCommand();
			break;
			
		case BrickDefine.SHOW:
			mStage.getThread().addBitmapToDraw(mSpriteName, mCurrentImage, mCurrentXPosition, mCurrentYPosition);
			mCommandCount++;
			doNextCommand();
			break;
		}
		


	}
	
	/**
	 * a convenient method to call brickWait(0) calling this forces the
	 * observable to notify the observer within 0 seconds and therefore a new
	 * command will be executed
	 */
	private void toNextCommand() {
		brickWait(0);
	}

	/**
	 * forces the program to wait until sec seconds are over
	 * 
	 * @param sec
	 *            the seconds to wait
	 */
	private void brickWait(float sec) {
		BrickWait wait = new BrickWait(); // TODO sicher schlechte performance
											// da jedes mal neues objekt erzeugt
											// wird
		wait.mWaitTime = sec;

		wait.addObserver(this);

		Thread thread = new Thread(wait);
		thread.setName("waitingThread");
		thread.start();
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		mp.release();

	}
	
	public void stopAndReleaseMediaPlayer(){
		if (mMediaPlayer.isPlaying()){
			mMediaPlayer.stop();
			mMediaPlayer.release();
		}
	}


}

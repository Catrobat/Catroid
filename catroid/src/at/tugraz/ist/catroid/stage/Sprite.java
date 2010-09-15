package at.tugraz.ist.catroid.stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.util.Log;
import android.util.Pair;
import at.tugraz.ist.catroid.constructionSite.content.BrickDefine;
import at.tugraz.ist.catroid.utils.Utils;

public class Sprite extends Thread implements Observer, OnCompletionListener {

	private String mSpriteName;
	private StageView mStage;
	private ArrayList<HashMap<String, String>> mCommandList;
	private MediaPlayer mMediaPlayer;
	private int mCommandCount = 0;
	private int mCurrentXPosition = 0;
	private int mCurrentYPosition = 0;
	private float mScalingFactor = 1;
	private String mCurrentImage = "";
	private boolean mWasPlaying = false;
	private BrickWait mBrickWait;
	private boolean mIsActive; //TODO quick workaround for wait problem on stage

	public Sprite(StageView view,
			Pair<String, ArrayList<HashMap<String, String>>> spriteInfo) {
		mStage = view;
		mCommandList = spriteInfo.second;
		mMediaPlayer = new MediaPlayer();
		mMediaPlayer.setOnCompletionListener(this);
		mSpriteName = spriteInfo.first;
		mBrickWait = new BrickWait();

	}

	@Override
	public void run() {
		mIsActive = true;
		doNextCommand();
	}

	
	public void update(Observable observable, Object data) {
		if (mIsActive)
			doNextCommand();

	}

	public synchronized void doNextCommand() {
		if ((mCommandList.size() <= mCommandCount) || (StageActivity.mDoNextCommands == false)){
			// abort if mCommandCount has run through all commands to execute
			return;
		}
		HashMap<String, String> map = mCommandList.get(mCommandCount);

		int type = Integer.parseInt(map.get(BrickDefine.BRICK_TYPE));
		switch (type) {
		case BrickDefine.SET_BACKGROUND:
			mCurrentImage = map.get(Utils.concatPaths(StageActivity.ROOT_IMAGES, BrickDefine.BRICK_VALUE));
			mStage.getThread().setBackground(Utils.concatPaths(StageActivity.ROOT_IMAGES,map.get(BrickDefine.BRICK_VALUE)));
			mCommandCount++;
			doNextCommand();
			break;

		case BrickDefine.SET_COSTUME:
			mCurrentImage = Utils.concatPaths(StageActivity.ROOT_IMAGES, map.get(BrickDefine.BRICK_VALUE));
			mStage.getThread().addBitmapToDraw(mSpriteName,
					Utils.concatPaths(StageActivity.ROOT_IMAGES, map.get(BrickDefine.BRICK_VALUE)), mCurrentXPosition,
					mCurrentYPosition, mScalingFactor);
			mCommandCount++;
			doNextCommand();
			break;

		case BrickDefine.PLAY_SOUND:
			try {
				mMediaPlayer.reset();
				mMediaPlayer.setDataSource(Utils.concatPaths(StageActivity.ROOT_SOUNDS, map.get(BrickDefine.BRICK_VALUE)));
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
			mCurrentXPosition = Integer.parseInt(map
					.get(BrickDefine.BRICK_VALUE));
			mCurrentYPosition = Integer.parseInt(map
					.get(BrickDefine.BRICK_VALUE_1));
			mStage.getThread().changeBitmapPosition(mSpriteName,
					mCurrentXPosition, mCurrentYPosition);
			mCommandCount++;
			doNextCommand();
			break;

		case BrickDefine.HIDE:
			mStage.getThread().removeBitmapToDraw(mSpriteName);
			mCommandCount++;
			doNextCommand();
			break;

		case BrickDefine.SHOW:
			mStage.getThread().addBitmapToDraw(mSpriteName, mCurrentImage,
					mCurrentXPosition, mCurrentYPosition, mScalingFactor);
			mCommandCount++;
			doNextCommand();
			break;
			
		case BrickDefine.SCALE_COSTUME:
			mScalingFactor = ((float)Integer.parseInt(map.get(BrickDefine.BRICK_VALUE)))/100;
			mStage.getThread().changeScalingFactor(mSpriteName, mScalingFactor);
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

	private void brickWait(float sec) {
		mBrickWait.mWaitTime = sec;
		mBrickWait.addObserver(this);
		Thread thread = new Thread(mBrickWait);
		
		thread.setName("waitingThread");
		thread.start();
	}

	
	public void onCompletion(MediaPlayer mp) {
//		mp.release(); //we don't want to release the media player here because we may need it again

	}

	public void stopAndReleaseMediaPlayer() {
		if ((mMediaPlayer != null) && (mMediaPlayer.isPlaying()))
			mMediaPlayer.stop();
			mMediaPlayer.release();
		mWasPlaying = false;
		mIsActive = false;
	}
	
	public void pauseMediaPlayer(){
		if ((mMediaPlayer != null) && (mMediaPlayer.isPlaying())){
			mMediaPlayer.pause();
			mWasPlaying = true;
		}
		mIsActive = false;
	}
	
	public void startMediaPlayer(){
		if (mWasPlaying) {
			mMediaPlayer.start();
			mWasPlaying = false;
		}
	}
}

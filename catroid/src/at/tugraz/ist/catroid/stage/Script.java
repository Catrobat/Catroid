package at.tugraz.ist.catroid.stage;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

import android.media.MediaPlayer;
import android.util.Log;
import android.util.Pair;
import at.tugraz.ist.catroid.constructionSite.content.BrickDefine;
import at.tugraz.ist.catroid.utils.Utils;

/**
 * 
 * @author Thomas Holzmann
 * 
 */
public class Script extends Thread implements Observer {

	public ArrayList<HashMap<String, String>> mScriptData;
	private DrawObject mDrawObject;
	private SoundManager mSoundManager;
	private BrickWait mBrickWait;
	private int mCommandCount = 0;
	private boolean mIsRunning;
	private boolean mWasWaiting = false;
	public boolean mIsTouchScript;
	private boolean mFirstRun = true;

	public Script(DrawObject drawObject,
			ArrayList<HashMap<String, String>> scriptData) {
		super();
		mScriptData = scriptData;
		mDrawObject = drawObject;
		mSoundManager = SoundManager.getInstance();
		mBrickWait = new BrickWait();
		mIsRunning = true;
	}

	@Override
	public void run() {
		if (mIsTouchScript && mFirstRun) {
			Log.i("Touchzeugs", "Touch Thread gestartet: " + this.getId());
			mFirstRun = false;
		}
		if (mIsRunning) {
			if (mIsTouchScript)
				Log.i("Touchzeugs", "Touch Thread arbeitet command ab: "
						+ this.getId());
			doNextCommand();
		}
	}

	public void update(Observable observable, Object data) {
		mCommandCount++;

		doNextCommand();
	}

	public void pause() {
		if (mBrickWait.mIsWaiting) {
			mBrickWait.pause();
			mWasWaiting = true;
		}
		mIsRunning = false;

	}

	public void endPause() {
		mIsRunning = true;
		if (mWasWaiting) {
			mBrickWait.start();
			mWasWaiting = false;
		} else {
			doNextCommand();
		}

	}

	public synchronized void doNextCommand() {
		if ((mScriptData.size() <= mCommandCount) || (mIsRunning == false)) {
			// abort if mCommandCount has run through all commands to execute
			return;
		}
		HashMap<String, String> map = mScriptData.get(mCommandCount);

		String imagePath;
		int type = Integer.parseInt(map.get(BrickDefine.BRICK_TYPE));
		switch (type) {
		case BrickDefine.SET_BACKGROUND:
		case BrickDefine.SET_COSTUME:
			imagePath = Utils.concatPaths(StageActivity.ROOT_IMAGES, (map
					.get(BrickDefine.BRICK_VALUE)));
			try {
				mDrawObject.setBitmap(imagePath);
				Log.i("Script", "Bitmap set");
			} catch (Exception e) {
				Log.e("Script", "Image " + imagePath + " does not exist!");
				e.printStackTrace();
			}
			mCommandCount++;
			doNextCommand();
			break;

		case BrickDefine.PLAY_SOUND:
			MediaPlayer mediaPlayer = mSoundManager.getMediaPlayer();
			try {
				mediaPlayer.setDataSource(Utils.concatPaths(
						StageActivity.ROOT_SOUNDS, map
								.get(BrickDefine.BRICK_VALUE)));
				mediaPlayer.prepare();
				mediaPlayer.start();

			} catch (IOException e) {
				Log.w("Sprite", "Could not play sound file");
			} catch (IllegalArgumentException e) {
				Log.w("Sprite", "Could not play sound file");
			}
			mCommandCount++;
			doNextCommand();
			break;

		case BrickDefine.WAIT:
			mBrickWait.mWaitTime = (int) (Float.parseFloat(map
					.get(BrickDefine.BRICK_VALUE)) * 1000f);
			mBrickWait.addObserver(this);
			mBrickWait.start();
			break;

		case BrickDefine.GO_TO:
			mDrawObject.setmPositionRel(new Pair<Integer, Integer>(Integer
					.parseInt(map.get(BrickDefine.BRICK_VALUE)), Integer
					.parseInt(map.get(BrickDefine.BRICK_VALUE_1))));
			mCommandCount++;
			doNextCommand();
			break;

		case BrickDefine.HIDE:
			mDrawObject.setHidden(true);
			mCommandCount++;
			doNextCommand();
			break;

		case BrickDefine.SHOW:
			mDrawObject.setHidden(false);
			mCommandCount++;
			doNextCommand();
			break;

		case BrickDefine.SCALE_COSTUME:
			mDrawObject.scaleBitmap(Integer.parseInt(map
					.get(BrickDefine.BRICK_VALUE)));
			mCommandCount++;
			doNextCommand();
			break;
		
		case BrickDefine.TOUCHED:
			mCommandCount++;
			doNextCommand();
			break;
		default:
			break;
		}

	}
}

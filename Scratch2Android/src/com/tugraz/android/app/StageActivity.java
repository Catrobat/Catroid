package com.tugraz.android.app;

import java.io.IOException;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

import com.tugraz.android.app.stage.BrickWait;
import com.tugraz.android.app.stage.StageView;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Path.FillType;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.SoundPool;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup.LayoutParams;

public class StageActivity extends Activity implements OnCompletionListener,
		Observer {

	private static StageView mStage;
	private ContentManager mContentManager;
	protected boolean isWaiting = false;

	private int mCommandCount = 0;
	MediaPlayer mMediaPlayer;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mStage = new StageView(this);

		mContentManager = new ContentManager();
		mContentManager.setContext(this); 										
		mContentManager.loadContent();
		
		mMediaPlayer = new MediaPlayer();

		LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT); 
		setContentView(R.layout.stage);
		addContentView(mStage, params);

		// we only want portrait mode atm
		// setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.stagemenu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.stagemenuStart:
			start();
			break;
		case R.id.stagemenuConstructionSite:
			toMainActivity();
			break;
		}
		return true;
	}
	
	@Override
	public void onCompletion(MediaPlayer mp) {
		mp.release();

	}

	@Override
	public void update(Observable observable, Object data) {
		doNextCommand();

	}

	@Override
	protected void onPause() {
		super.onPause();
		mMediaPlayer.stop();
		mMediaPlayer.release();
	}

	/**
	 * starts the StageViewThread
	 */
	private void start() { // TODO funktioniert beim erneuten ausfuehren, wenn
							// die stage schon laueft nicht
		if (mStage.getThread().isRunning()) {
			mStage.getThread().setRunning(false);
		}

		mStage.getThread().setRunning(true); // TODO gehoert das hier her??
		mStage.getThread().start();

		doNextCommand();

	}

	/**
	 * executes the next command from the contentArrayList of the contentManager
	 */
	private void doNextCommand() {
		if (mContentManager.mContentArrayList.size() <= mCommandCount) { 
			// abort if mCommandCount has run through all commands to execute															
			mCommandCount = 0;
			return;
		}
		mMediaPlayer.setOnCompletionListener(this);

		HashMap<String, String> map = mContentManager.mContentArrayList
				.get(mCommandCount);

		int type = Integer.parseInt(map.get(BrickDefine.BRICK_TYPE));
		switch (type) {
		case BrickDefine.SET_BACKGROUND:
			mStage.getThread().setBackgroundBitmap(
					map.get(BrickDefine.BRICK_VALUE));
			mStage.getThread().mIsDraw = true;
			mCommandCount++;
			toNextCommand();
			break;

		case BrickDefine.PLAY_SOUND: //TODO funktioniert abspielen von mehreren sounds gleichzeitig
			try {
				mMediaPlayer.reset();
				mMediaPlayer.setDataSource(map.get(BrickDefine.BRICK_VALUE));
				mMediaPlayer.prepare();
				mMediaPlayer.start();

			} catch (IOException e) {
				Log.w("StageActivity", "Could not play sound file");
			} catch (IllegalArgumentException e) {
				Log.w("StageActivity", "Could not play sound file");
			}

			mCommandCount++;
			toNextCommand();
			break;

		case BrickDefine.WAIT:
			mCommandCount++;
			brickWait(Integer.parseInt(map.get(BrickDefine.BRICK_VALUE)));
			break;
		}

	}

	/**
	 * closes the StageActivity
	 */
	private void toMainActivity() {
		finish(); 
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
	private void brickWait(int sec) {
		BrickWait wait = new BrickWait(); // TODO sicher schlechte performance
											// da jedes mal neues objekt erzeugt
											// wird
		wait.mWaitTime = sec;

		wait.addObserver(this);

		Thread thread = new Thread(wait);
		thread.setName("waitingThread");
		thread.start();
	}

}

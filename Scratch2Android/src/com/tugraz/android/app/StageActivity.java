package com.tugraz.android.app;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

import com.tugraz.android.app.stage.BrickWait;
import com.tugraz.android.app.stage.SoundManager;
import com.tugraz.android.app.stage.StageView;
import com.tugraz.android.app.stage.Sprite;

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

public class StageActivity extends Activity implements OnCompletionListener {

	private static StageView mStage;
	private ContentManager mContentManager;
	private ArrayList<Sprite> mSpritesList;
	protected boolean isWaiting = false;

	private int mCommandCount = 0;
	//MediaPlayer mMediaPlayer;
	private SoundManager mSoundManager;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mStage = new StageView(this);

		mContentManager = new ContentManager();
		mContentManager.setContext(this); 										
		mContentManager.loadContent();
		
		//mMediaPlayer = new MediaPlayer();
		mSoundManager = new SoundManager();
		
		mSpritesList = new ArrayList<Sprite>();

		LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT); 
		setContentView(R.layout.stage);
		addContentView(mStage, params);

		// we only want portrait mode atm, otherwise the program crashes
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
		//mMediaPlayer.setOnCompletionListener(this); //TODO auch das für SoundManager machen

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
	
	
	public void onCompletion(MediaPlayer mp) {
		mp.release();

	}


	@Override
	protected void onPause() {
		super.onPause();
//		mMediaPlayer.stop();
//		mMediaPlayer.release();
		mSoundManager.stopAndRelease();
	}
	
	private void toMainActivity() {
		finish(); 
	}

	/**
	 * starts the StageViewThread
	 */
	private void start() { // TODO funktioniert beim erneuten ausfuehren, wenn
							// die stage schon laueft nicht
		if (mStage.getThread().isRunning()) {
			mStage.getThread().setRunning(false);

//			if (mMediaPlayer.isPlaying()){
//				mMediaPlayer.stop();
//				mMediaPlayer.release();
//			}
			if (mSoundManager.isPlaying()){
				mSoundManager.stopAndRelease();
			}
		}

		mStage.getThread().setRunning(true); // TODO gehoert das hier her??
		mStage.getThread().start();

		ArrayList<String> allSpriteNames = mContentManager.getAllSprites();
		for (int i=0; i < allSpriteNames.size(); i++) {
			Sprite sprite = new Sprite(mStage, mContentManager.getSpritesAndBackground().get(allSpriteNames.get(i)), allSpriteNames.get(i), mSoundManager);
			mSpritesList.add(sprite);
		}
		
		for (int i=0; i<mSpritesList.size(); i++) {
			mSpritesList.get(i).start();
		}

	}


}

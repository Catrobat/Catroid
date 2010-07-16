package com.tugraz.android.app;

import java.io.Closeable;
import java.util.HashMap;

import com.tugraz.android.app.stage.StageView;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Path.FillType;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup.LayoutParams;

public class StageActivity extends Activity {

	private static StageView mStage;
	private ContentManager mContentManager;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);


		mStage = new StageView(this);
		
		mContentManager = new ContentManager();
		mContentManager.setContext(this); //TODO funktioniert das mit diesem context?
		mContentManager.loadContent();

		LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT); //TODO change!!
		setContentView(R.layout.stage);
		addContentView(mStage, params);
		
		// we only want portrait mode atm
		//setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
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
	
	/**
	 * starts the StageViewThread
	 */
	private void start(){ //TODO funktioniert beim erneuten ausfuehren, wenn die stage schon laueft nicht
		if (mStage.getThread().isRunning()){
			mStage.getThread().setRunning(false); 		
		}
		
		mStage.getThread().setRunning(true); //TODO gehoert das hier her??
		mStage.getThread().start();
		
		for (int i = 0; i<mContentManager.mContentArrayList.size(); i++){
			HashMap<String,String> map = mContentManager.mContentArrayList.get(i);
			int type = Integer.parseInt(map.get(BrickDefine.BRICK_TYPE));
			switch (type){
				case BrickDefine.SET_BACKGROUND:
					mStage.getThread().setBackgroundBitmap(map.get(BrickDefine.BRICK_VALUE));					
				case BrickDefine.PLAY_SOUND:
					
					
					//TODO play sound using the MediaPlayer
				case BrickDefine.WAIT:
				try {
					wait(Integer.parseInt(map.get(BrickDefine.BRICK_VALUE))*1000);
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}//TODO how to wait!?
			}
					
		}
		
		
	}
	
	/**
	 * closes the StageActivity
	 */
	private void toMainActivity(){
		finish(); //TODO kommt man dann richtig zur baustelle zurueck?
	}
}

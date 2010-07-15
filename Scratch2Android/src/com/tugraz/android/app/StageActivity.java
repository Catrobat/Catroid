package com.tugraz.android.app;

import java.io.Closeable;

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
		mStage.getThread().setRunning(true);
		mStage.getThread().start();
	}
	
	/**
	 * closes the StageActivity
	 */
	private void toMainActivity(){
		finish(); //TODO kommt man dann richtig zur baustelle zurueck?
	}
}

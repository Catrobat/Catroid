package com.tugraz.android.app;




import com.tugraz.android.app.stage.StageView;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup.LayoutParams;

public class StageActivity extends Activity {

	private static StageView stage;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);


		stage = new StageView(this);

		setContentView(R.layout.stage);
		addContentView(stage, null);
		
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
	
	private void start(){
		//TODO implement
	}
	
	private void stop(){
		//TODO implement
	}
	
	private void toMainActivity(){
		//TODO implement
	}
}

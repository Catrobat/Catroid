package com.scratchPlayer.app.stage;

import com.scratchPlayer.app.R;
import com.scratchPlayer.app.gui.ProjectFileParams;
import com.scratchPlayer.app.objectReader.ProjectFile;
import com.scratchPlayer.app.stage.draw.StageView;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup.LayoutParams;


/**
 * Executes the project
 * 
 * @author Thomas Holzmann
 * 
 */

// TODO start, stop and start again doesn't work yet

public class RunProject extends Activity {

	private ProjectFile projectFile; // the project file in which all the
										// scratch object information is stored
	private static StageView stage;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		projectFile = ProjectFileParams.project; // TODO nicht schoen, mglw
													// aendern in
													// ProjectFileParams??

		stage = new StageView(this);
		LayoutParams params = new LayoutParams(480, 360); // set the correct
															// size for the
															// stage

		setContentView(R.layout.run);
		addContentView(stage, params);
		
		// we only want portrait mode atm
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
        // Locate the SensorManager using Activity.getSystemService
        SensorManager sm;
        sm = (SensorManager) getSystemService(SENSOR_SERVICE);
         
        // Register your SensorListener
        sm.registerListener(sl, SensorManager.SENSOR_ORIENTATION, SensorManager.SENSOR_DELAY_NORMAL);
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
			render();
			break;

		case R.id.stagemenuStop:
			stop();
			break;
		}
		return true;
	}

	/**
	 * renders the stage and reacts on user input??
	 */
	private void render() {
		stage.getThread().setRunning(true);
		stage.getThread().start();

	}

	/**
	 * stops rendering of the stage
	 */
	private void stop() {
		boolean retry = true;
		stage.getThread().setRunning(false);
		while (retry) {
			try {
				stage.getThread().join();
				retry = false;
			} catch (InterruptedException e) {
				// TODO exception handling??
			}
		}

	}
	
    private final SensorListener sl = new SensorListener(){
    	public void onSensorChanged(int sensor, float[] values){
    		if (values[2] < (-15)) {
    			stage.onKeyDown(KeyEvent.KEYCODE_DPAD_LEFT, null);
    		}
    		else if (values[2] > 15) {
    			stage.onKeyDown(KeyEvent.KEYCODE_DPAD_RIGHT, null);
    		}
    		if (values[1] < (-15)){
    			stage.onKeyDown(KeyEvent.KEYCODE_DPAD_DOWN, null);
    		}
    		else if (values[1] > 15){
    			stage.onKeyDown(KeyEvent.KEYCODE_DPAD_UP, null);
    		}
    		
    		if ((-3 <=values[1]) && (values[1]<= 3)){
    			//vor/hinter ruhig setzen
    			stage.onKeyUp(KeyEvent.KEYCODE_DPAD_DOWN, null);
    			stage.onKeyUp(KeyEvent.KEYCODE_DPAD_UP, null);
    		}
    		if ((-3 <=values[2]) && (values[2]<= 3)){
    			//links/rechts ruhig setzen
    			stage.onKeyUp(KeyEvent.KEYCODE_DPAD_LEFT, null);
    			stage.onKeyUp(KeyEvent.KEYCODE_DPAD_RIGHT, null);
    		}
    			
    			
    	}

		@Override
		public void onAccuracyChanged(int sensor, int accuracy) {
			// TODO Auto-generated method stub
			
		}

    };

}

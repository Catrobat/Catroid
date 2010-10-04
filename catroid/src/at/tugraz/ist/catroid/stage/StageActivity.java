package at.tugraz.ist.catroid.stage;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;
import at.tugraz.ist.catroid.ConstructionSiteActivity;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.utils.Utils;

public class StageActivity extends Activity {

	public static SurfaceView mStage;
	protected boolean isWaiting = false;
	private SoundManager mSoundManager;
	private StageManager mStageManager;
	private GestureListener mGestureListener;
	private GestureDetector mGestureDetector;

	public static String ROOT_IMAGES;
	public static String ROOT_SOUNDS;
	public static String ROOT;
	public static String SPF_FILE;

	// public static boolean mDoNextCommands = true;
	private boolean mStagePlaying = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (Utils.checkForSdCard(this)) {
			Window window = getWindow();
			window.requestFeature(Window.FEATURE_NO_TITLE);
			window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

			ROOT = (String) getIntent().getExtras().get(ConstructionSiteActivity.INTENT_EXTRA_ROOT);
			ROOT_IMAGES = (String) getIntent().getExtras().get(ConstructionSiteActivity.INTENT_EXTRA_ROOT_IMAGES);
			ROOT_SOUNDS = (String) getIntent().getExtras().get(ConstructionSiteActivity.INTENT_EXTRA_ROOT_SOUNDS);
			SPF_FILE = (String) getIntent().getExtras().get(ConstructionSiteActivity.INTENT_EXTRA_SPF_FILE_NAME);

			setContentView(R.layout.stage);
			mStage = (SurfaceView) findViewById(R.id.stageView);

			// we only want portrait mode atm, otherwise the program crashes
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			mSoundManager = SoundManager.getInstance();
			mStageManager = new StageManager(this, SPF_FILE);
			mGestureListener = new GestureListener(this);
			mGestureDetector = new GestureDetector(this, mGestureListener);
			mStageManager.start();
			mStagePlaying = true;
		}

	}

	 public boolean onTouchEvent(MotionEvent event) {  
	     return mGestureDetector.onTouchEvent(event);  
	 } 
	 
	 public void processOnTouch(int coordX, int coordY) {
			
		 coordX = coordX + mStage.getTop();
		 coordY = coordY + mStage.getLeft();
		 
		 	mStageManager.processOnTouch(coordX, coordY);
			}
		
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.stage_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.stagemenuStart:
			pauseOrContinue();
			break;
		case R.id.stagemenuConstructionSite:
			toMainActivity();
			break;
		}
		return true;
	}

	protected void onStop() {
		super.onStop();
		mSoundManager.pause();
		mStageManager.pause(false);
		mStagePlaying = false;
	}

	protected void onRestart() {
		super.onRestart();
		mStageManager.unPause();
		mSoundManager.resume();
		mStagePlaying = true;
	}

	protected void onDestroy() {
		super.onDestroy();
		mSoundManager.release();
	}

	public void onBackPressed() {
		finish();
	}

	private void toMainActivity() {
		finish();
	}

	private void pauseOrContinue() {
		if (mStagePlaying) {
			mStageManager.pause(true);
			mSoundManager.pause();
			mStagePlaying = false;

		} else {
			mStageManager.unPause();
			mSoundManager.resume();
			mStagePlaying = true;
		}
	}
}

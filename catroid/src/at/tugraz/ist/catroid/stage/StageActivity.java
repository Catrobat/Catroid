package at.tugraz.ist.catroid.stage;

import java.util.ArrayList;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.ViewGroup.LayoutParams;
import at.tugraz.ist.catroid.ConstructionSiteActivity;
import at.tugraz.ist.catroid.constructionSite.content.ContentManager;
import at.tugraz.ist.catroid.utils.Utils;
import at.tugraz.ist.catroid.R;

public class StageActivity extends Activity {

	public static SurfaceView mStage;
	private ContentManager mContentManager;
	protected boolean isWaiting = false;
	private SoundManager mSoundManager;
	private StageManager mStageManager;

	public static String ROOT_IMAGES;
	public static String ROOT_SOUNDS;
	public static String ROOT;
	public static String SPF_FILE;

	// public static boolean mDoNextCommands = true;
	private boolean mStagePlaying = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ROOT = (String) getIntent().getExtras().get(
				ConstructionSiteActivity.INTENT_EXTRA_ROOT);
		ROOT_IMAGES = (String) getIntent().getExtras().get(
				ConstructionSiteActivity.INTENT_EXTRA_ROOT_IMAGES);
		ROOT_SOUNDS = (String) getIntent().getExtras().get(
				ConstructionSiteActivity.INTENT_EXTRA_ROOT_SOUNDS);
		SPF_FILE = (String) getIntent().getExtras().get(
				ConstructionSiteActivity.INTENT_EXTRA_SPF_FILE_NAME);

		setContentView(R.layout.stage);
		mStage = (SurfaceView) findViewById(R.id.stageView);

		mSoundManager = SoundManager.getInstance();
		// we only want portrait mode atm, otherwise the program crashes
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		mStageManager = new StageManager(this, ROOT_IMAGES, ROOT_SOUNDS, ROOT,
				SPF_FILE);
		mStageManager.start();
		mStagePlaying = true;

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

	protected void onPause() {
		super.onPause();
		mSoundManager.pause();
		finish();
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

	private void start() {
		// if (!mStage.getThread().isAlive()) {
		// mStage.getThread().setRunning(true);
		// mStage.getThread().start();
		// }
		//
		// for (int i = 0; i < mContentManager.getAllContentArrayList().size();
		// i++) {
		// Sprite_old sprite = new Sprite_old(mStage,
		// mContentManager.getAllContentArrayList().get(i));
		// mSpritesList.add(sprite);
		// }
		//
		// for (int i = 0; i < mSpritesList.size(); i++) {
		// mSpritesList.get(i).start();
		// }
	}

	private void pauseOrContinue() {
		if (mStagePlaying) {
			mStageManager.pause();
			mSoundManager.pause();
			mStagePlaying = false;

		} else {
			mStageManager.unPause();
			mSoundManager.resume();
			mStagePlaying = true;
		}
	}
}

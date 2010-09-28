package at.tugraz.ist.catroid.stage;

import java.util.ArrayList;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import at.tugraz.ist.catroid.ConstructionSiteActivity;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.constructionSite.content.ContentManager;
import at.tugraz.ist.catroid.utils.Utils;

public class StageActivity extends Activity {

	private static StageView mStage;
	private ContentManager mContentManager;
	private ArrayList<Sprite> mSpritesList;
	protected boolean isWaiting = false;

	public static String ROOT_IMAGES;
	public static String ROOT_SOUNDS;
	public static String ROOT;
	public static String SPF_FILE;

	public static boolean mDoNextCommands = true;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (Utils.checkForSdCard(this)) {
			ROOT = (String) getIntent().getExtras().get(ConstructionSiteActivity.INTENT_EXTRA_ROOT);
			ROOT_IMAGES = (String) getIntent().getExtras().get(ConstructionSiteActivity.INTENT_EXTRA_ROOT_IMAGES);
			ROOT_SOUNDS = (String) getIntent().getExtras().get(ConstructionSiteActivity.INTENT_EXTRA_ROOT_SOUNDS);
			SPF_FILE = (String) getIntent().getExtras().get(ConstructionSiteActivity.INTENT_EXTRA_SPF_FILE_NAME);

			mStage = new StageView(this);

			setContentView(mStage);

			// we only want portrait mode atm, otherwise the program crashes
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			mContentManager = new ContentManager(this);
			mContentManager.loadContent(SPF_FILE);

			mSpritesList = new ArrayList<Sprite>();
		}
	}

	private boolean STARTED = false;

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (!STARTED) {
			STARTED = true;
			mStage.setBackgroundResource(0);
			start();
		} else if (!mDoNextCommands)
			pauseOrContinue();

		return super.onTouchEvent(event);
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
		for (int i = 0; i < mSpritesList.size(); i++)
			mSpritesList.get(i).pauseMediaPlayer();
		finish();
	}

	protected void onDestroy() {
		super.onDestroy();
		for (int i = 0; i < mSpritesList.size(); i++)
			mSpritesList.get(i).stopAndReleaseMediaPlayer();
	}

	public void onBackPressed() {
		finish();
	}

	private void toMainActivity() {
		finish();
	}

	private void start() {
		if (!mStage.getThread().isAlive()) {
			mStage.getThread().setRunning(true);
			mStage.getThread().start();
		}

		for (int i = 0; i < mContentManager.getAllContentArrayList().size(); i++) {
			Sprite sprite = new Sprite(mStage, mContentManager.getAllContentArrayList().get(i));
			mSpritesList.add(sprite);
		}

		for (int i = 0; i < mSpritesList.size(); i++) {
			mSpritesList.get(i).start();
		}
	}

	private void pauseOrContinue() {
		if (mDoNextCommands) {
			mStage.setBackgroundResource(R.drawable.play_splash);
			mDoNextCommands = false;
			for (int i = 0; i < mSpritesList.size(); i++) {
				mSpritesList.get(i).pauseMediaPlayer();
			}
		} else {
			mStage.setBackgroundResource(0);
			mDoNextCommands = true;
			for (int i = 0; i < mSpritesList.size(); i++) {
				mSpritesList.get(i).startMediaPlayer();
				mSpritesList.get(i).doNextCommand(); // TODO problem mit wait??
			}
		}
	}

}

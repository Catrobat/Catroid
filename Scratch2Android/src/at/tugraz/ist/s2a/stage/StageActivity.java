package at.tugraz.ist.s2a.stage;

import java.util.ArrayList;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup.LayoutParams;
import at.tugraz.ist.s2a.ConstructionSiteActivity;
import at.tugraz.ist.s2a.R;
import at.tugraz.ist.s2a.R.id;
import at.tugraz.ist.s2a.R.layout;
import at.tugraz.ist.s2a.R.menu;
import at.tugraz.ist.s2a.constructionSite.content.ContentManager;

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
		
		ROOT = (String) getIntent().getExtras().get(ConstructionSiteActivity.INTENT_EXTRA_ROOT);
		ROOT_IMAGES = (String) getIntent().getExtras().get(ConstructionSiteActivity.INTENT_EXTRA_ROOT_IMAGES);
		ROOT_SOUNDS = (String) getIntent().getExtras().get(ConstructionSiteActivity.INTENT_EXTRA_ROOT_SOUNDS);
		SPF_FILE = (String) getIntent().getExtras().get(ConstructionSiteActivity.INTENT_EXTRA_SPF_FILE_NAME);
		
		mStage = new StageView(this);

		mContentManager = new ContentManager(this);
		mContentManager.loadContent();


		mSpritesList = new ArrayList<Sprite>();

		LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT);
		setContentView(R.layout.stage);
		addContentView(mStage, params);

		// we only want portrait mode atm, otherwise the program crashes
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		start();

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

		ArrayList<String> allSpriteNames = mContentManager.getSpritelist();
		for (int i = 0; i < allSpriteNames.size(); i++) {
			Sprite sprite = new Sprite(mStage, mContentManager
					.getSpritesAndBackground().get(allSpriteNames.get(i)),
					allSpriteNames.get(i));
			mSpritesList.add(sprite);
		}

		for (int i = 0; i < mSpritesList.size(); i++) {
			mSpritesList.get(i).start();
		}
	}
	
	private void pauseOrContinue() {
		if (mDoNextCommands){
			mDoNextCommands = false;
			for (int i = 0; i < mSpritesList.size(); i++) {
				mSpritesList.get(i).pauseMediaPlayer();
			}
		}
		else {
			mDoNextCommands = true;
			for (int i = 0; i < mSpritesList.size(); i++) {
				mSpritesList.get(i).startMediaPlayer();
				mSpritesList.get(i).doNextCommand(); //TODO problem mit wait??
			}
		}
	}

}

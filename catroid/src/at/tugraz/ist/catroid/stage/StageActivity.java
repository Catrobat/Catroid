/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010  Catroid development team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package at.tugraz.ist.catroid.stage;

import java.util.Locale;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.io.SoundManager;
import at.tugraz.ist.catroid.stage.SimpleGestureFilter.SimpleGestureListener;
import at.tugraz.ist.catroid.utils.Utils;

public class StageActivity extends Activity implements SimpleGestureListener, OnInitListener {

	public static SurfaceView stage;
	private SoundManager soundManager;
	private StageManager stageManager;
	private boolean stagePlaying = false;
	private SimpleGestureFilter detector;
	private TextToSpeech talker;
	private String text;
	private final static String TAG = StageActivity.class.getSimpleName();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (Utils.checkForSdCard(this)) {
			Window window = getWindow();
			window.requestFeature(Window.FEATURE_NO_TITLE);
			window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

			setContentView(R.layout.activity_stage);
			stage = (SurfaceView) findViewById(R.id.stageView);

			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			Utils.updateScreenWidthAndHeight(this);

			soundManager = SoundManager.getInstance();
			stageManager = new StageManager(this);
			stageManager.start();
			stagePlaying = true;
		}
		detector = new SimpleGestureFilter(this, this);
		talker = new TextToSpeech(this, this);
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent e) {
		this.detector.onTouchEvent(e);
		return super.dispatchTouchEvent(e);
	}

	public void processOnTouch(int coordX, int coordY, String act) {
		Log.v(TAG, "2 this is the function called!!!" + act);
		coordX = coordX + stage.getTop();
		coordY = coordY + stage.getLeft();

		stageManager.processOnTouch(coordX, coordY, act);
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
				manageLoadAndFinish(); //calls finish
				break;
		}
		return true;
	}

	@Override
	protected void onStop() {
		super.onStop();
		soundManager.pause();
		stageManager.pause(false);
		stagePlaying = false;
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		stageManager.resume();
		soundManager.resume();
		stagePlaying = true;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		stageManager.finish();
		soundManager.clear();
		if (talker != null) {
			talker.stop();
			talker.shutdown();
		}
	}

	@Override
	public void onBackPressed() {
		manageLoadAndFinish();
	}

	private void manageLoadAndFinish() {
		ProjectManager projectManager = ProjectManager.getInstance();
		int currentSpritePos = projectManager.getCurrentSpritePosition();
		int currentScriptPos = projectManager.getCurrentScriptPosition();
		projectManager.loadProject(projectManager.getCurrentProject().getName(), this, false);
		projectManager.setCurrentSpriteWithPosition(currentSpritePos);
		projectManager.setCurrentScriptWithPosition(currentScriptPos);
		finish();
	}

	private void pauseOrContinue() {
		if (stagePlaying) {
			stageManager.pause(true);
			soundManager.pause();
			stagePlaying = false;
		} else {
			stageManager.resume();
			soundManager.resume();
			stagePlaying = true;
		}
	}

	@Override
	protected void onResume() {
		if (!Utils.checkForSdCard(this)) {
			return;
		}
		super.onResume();
	}

	public void onSwipe(int direction) {
		String str = "";

		switch (direction) {
			case SimpleGestureFilter.SWIPE_RIGHT:
				str = "Swipe Right";
				break;
			case SimpleGestureFilter.SWIPE_LEFT:
				str = "Swipe Left";
				break;
			case SimpleGestureFilter.SWIPE_DOWN:
				str = "Swipe Down";
				break;
			case SimpleGestureFilter.SWIPE_UP:
				str = "Swipe Up";
				break;

		}
		Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
	}

	public void onDoubleTap() {
		Toast.makeText(this, "Double Tap", Toast.LENGTH_SHORT).show();
	}

	public void onSingleTouch() {
		Toast.makeText(this, "Tapped", Toast.LENGTH_SHORT).show();

	}

	public void onLongPress() {
		Toast.makeText(this, "Long Press", Toast.LENGTH_SHORT).show();

	}

	public void say(String text2say) {
		talker.speak(text2say, TextToSpeech.QUEUE_FLUSH, null);
	}

	public void onInit(int status) {
		// status can be either TextToSpeech.SUCCESS or TextToSpeech.ERROR.
		if (status == TextToSpeech.SUCCESS) {
			// Set preferred language to US english.
			// Note that a language may not be available, and the result will indicate this.
			int result = talker.setLanguage(Locale.US);
			// Try this someday for some interesting results.
			// int result mTts.setLanguage(Locale.FRANCE);
			if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
				// Lanuage data is missing or the language is not supported.
				Log.e(TAG, "Language is not available.");
			} else {
				// Check the documentation for other possible result codes.
				// For example, the language may be available for the locale,
				// but not for the specified country and variant.
				say("hello");
			}
		}
	}
}

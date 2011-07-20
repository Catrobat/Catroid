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

import java.util.HashMap;
import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
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
	private final static String TAG = StageActivity.class.getSimpleName();
	private int MY_DATA_CHECK_CODE = 0;
	public static TextToSpeech tts;
	public String text;
	public boolean flag = true;

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
		Intent checkIntent = new Intent();
		checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
		startActivityForResult(checkIntent, MY_DATA_CHECK_CODE);
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent e) {
		this.detector.onTouchEvent(e);
		return super.dispatchTouchEvent(e);
	}

	public void processOnTouch(int coordX, int coordY, String action) {
		Log.v(TAG, "2 this is the function called!!!" + action);
		coordX = coordX + stage.getTop();
		coordY = coordY + stage.getLeft();

		stageManager.processOnTouch(coordX, coordY, action);
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
		if (tts != null) {
			tts.stop();
			tts.shutdown();
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
		String toastText = "";

		switch (direction) {
			case SimpleGestureFilter.SWIPE_RIGHT:
				toastText = "Swipe Right";
				break;
			case SimpleGestureFilter.SWIPE_LEFT:
				toastText = "Swipe Left";
				break;
			case SimpleGestureFilter.SWIPE_DOWN:
				toastText = "Swipe Down";
				break;
			case SimpleGestureFilter.SWIPE_UP:
				toastText = "Swipe Up";
				break;

		}
		Toast.makeText(this, toastText, Toast.LENGTH_SHORT).show();
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

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == MY_DATA_CHECK_CODE) {
			if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
				// success, create the TTS instance
				tts = new TextToSpeech(this, this);
			} else {
				// missing data, install it
				Intent installIntent = new Intent();
				installIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
				startActivity(installIntent);
			}
		}

	}

	public void onInit(int status) {
		if (status == TextToSpeech.SUCCESS) {
			Toast.makeText(StageActivity.this, "Text-To-Speech engine is initialized", Toast.LENGTH_LONG).show();
		} else if (status == TextToSpeech.ERROR) {
			Toast.makeText(StageActivity.this, "Error occurred while initializing Text-To-Speech engine",
					Toast.LENGTH_LONG).show();
		}
	}

	public static void textToSpeech(String Text) {
		HashMap<String, String> myHashAlarm = new HashMap<String, String>();
		myHashAlarm.put(TextToSpeech.Engine.KEY_PARAM_STREAM, String.valueOf(AudioManager.STREAM_MUSIC));
		tts.setSpeechRate(1);
		tts.setPitch(1);
		int result = tts.setLanguage(Locale.getDefault());
		if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
			Log.e(TAG, "Language is not available.");
		} else {
			tts.speak(Text, TextToSpeech.QUEUE_FLUSH, myHashAlarm);
		}
	}
}

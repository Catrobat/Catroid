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
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.LegoNXT.LegoNXT;
import at.tugraz.ist.catroid.LegoNXT.LegoNXTBtCommunicator;
import at.tugraz.ist.catroid.bluetooth.BluetoothManager;
import at.tugraz.ist.catroid.bluetooth.DeviceListActivity;
import at.tugraz.ist.catroid.content.bricks.Brick;
import at.tugraz.ist.catroid.io.SoundManager;
import at.tugraz.ist.catroid.stage.SimpleGestureFilter.SimpleGestureListener;
import at.tugraz.ist.catroid.ui.dialogs.StageDialog;
import at.tugraz.ist.catroid.utils.Utils;

public class StageActivity extends Activity implements SimpleGestureListener, OnInitListener {

	private static final int REQUEST_ENABLE_BT = 2000;
	private static final int REQUEST_CONNECT_DEVICE = 1000;
	private static final int MY_DATA_CHECK_CODE = 0;
	public static SurfaceView stage;
	private SoundManager soundManager;
	private StageManager stageManager;
	private StageDialog stageDialog;
	private boolean stagePlaying = false;
	private LegoNXT legoNXT;
	private BluetoothManager bluetoothManager;
	private ProgressDialog connectingProgressDialog;
	private SimpleGestureFilter detector;
	private final static String TAG = StageActivity.class.getSimpleName();
	public static TextToSpeech textToSpeechEngine;
	public String text;
	public boolean flag = true;
	private int spritePositionOnStageStart;
	private int scriptPositionOnStageStart;
	private int requiredResourceCounter = 0;

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

			stageManager = new StageManager(this);
			int required_resources = stageManager.getRequiredResources();
			stageDialog = new StageDialog(this, stageManager, R.style.stage_dialog);
			detector = new SimpleGestureFilter(this, this);

			ProjectManager projectManager = ProjectManager.getInstance();
			spritePositionOnStageStart = projectManager.getCurrentSpritePosition();
			scriptPositionOnStageStart = projectManager.getCurrentScriptPosition();

			boolean noResources = true;
			int mask = 0x1;
			int value = required_resources;
			while (value > 0) {
				if ((mask & required_resources) > 0) {
					Log.i("bt", "res required: " + mask);
					requiredResourceCounter++; //EVERY Resource must call start stage once!
					noResources = false;
				}
				value = value >> 1;
				mask = mask << 1;
			}

			if ((required_resources & Brick.SOUND_MANAGER) > 0) {
				soundManager = SoundManager.getInstance();
				startStage();
			}
			if ((required_resources & Brick.TEXT_TO_SPEECH) > 0) {
				Intent checkIntent = new Intent();
				checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
				startActivityForResult(checkIntent, MY_DATA_CHECK_CODE);
			}
			if ((required_resources & Brick.BLUETOOTH_LEGO_NXT) > 0) {
				bluetoothManager = new BluetoothManager(this);
				legoNXT = new LegoNXT(this, recieveHandler);
				int bluetoothState = bluetoothManager.activateBluetooth();
				if (bluetoothState == -1) {
					Toast.makeText(StageActivity.this, R.string.notification_blueth_err, Toast.LENGTH_LONG).show();
					finish();
				} else if (bluetoothState == 1) {
					startBTComm();
				}
			}
			if (noResources == true) {
				Log.i("bt", "no resource start");
				startStage();
			}
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.i("bt", "requestcode " + requestCode + " result code" + resultCode);
		switch (requestCode) {

			case REQUEST_ENABLE_BT:
				switch (resultCode) {
					case Activity.RESULT_OK:
						startBTComm();
						break;

					case Activity.RESULT_CANCELED:
						Toast.makeText(StageActivity.this, R.string.notification_blueth_err, Toast.LENGTH_LONG).show();
						manageLoadAndFinish();
						break;
				}
				break;

			case REQUEST_CONNECT_DEVICE:
				switch (resultCode) {
					case Activity.RESULT_OK:
						String address = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
						//pairing = data.getExtras().getBoolean(DeviceListActivity.PAIRING);
						legoNXT.startBTCommunicator(address);
						break;

					case Activity.RESULT_CANCELED:
						connectingProgressDialog.dismiss();
						manageLoadAndFinish();
						break;
				}
				break;

			case MY_DATA_CHECK_CODE:
				if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
					textToSpeechEngine = new TextToSpeech(this, this);
					textToSpeechEngine.setSpeechRate(1);
					textToSpeechEngine.setPitch(1);
					//startStage(); //=> init listener
				} else {
					Intent installIntent = new Intent();
					installIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
					startActivity(installIntent);
					manageLoadAndFinish();
				}

		}
	}

	private void startBTComm() {
		connectingProgressDialog = ProgressDialog.show(this, "",
				getResources().getString(R.string.connecting_please_wait), true);

		Intent serverIntent = new Intent(this, DeviceListActivity.class);
		this.startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
	}

	public synchronized void startStage() {
		requiredResourceCounter--;
		if (requiredResourceCounter > 0) {
			return;
		}

		stageManager.startScripts();
		stageManager.start();
		stagePlaying = true;
	}

	//messages from Lego NXT device can be handled here
	final Handler recieveHandler = new Handler() {
		@Override
		public void handleMessage(Message myMessage) {

			Log.i("bt", "message" + myMessage.getData().getInt("message"));
			switch (myMessage.getData().getInt("message")) {
				case LegoNXTBtCommunicator.STATE_CONNECTED:
					connectingProgressDialog.dismiss();
					requiredResourceCounter--;
					startStage();
					break;
				case LegoNXTBtCommunicator.STATE_CONNECTERROR:
					Toast.makeText(StageActivity.this, R.string.bt_connection_failed, Toast.LENGTH_SHORT);
					connectingProgressDialog.dismiss();
					manageLoadAndFinish();
					break;
				default:

					//Toast.makeText(StageActivity.this, myMessage.getData().getString("toastText"), Toast.LENGTH_SHORT);
					break;

			}
		}
	};

	@Override
	public boolean dispatchTouchEvent(MotionEvent e) {
		this.detector.onTouchEvent(e);
		return super.dispatchTouchEvent(e);
	}

	public void processOnTouch(int xCoordinate, int yCoordinate, String action) {
		xCoordinate = xCoordinate + stage.getTop();
		yCoordinate = yCoordinate + stage.getLeft();

		stageManager.processOnTouch(xCoordinate, yCoordinate, action);
		Log.v(TAG, action);
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (soundManager != null) {
			soundManager.pause();
		}
		stageManager.pause(false);
		stagePlaying = false;
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		stageManager.resume();
		if (soundManager != null) {
			soundManager.resume();
		}
		stagePlaying = true;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		stageManager.finish();
		if (soundManager != null) {
			soundManager.clear();
		}
		if (textToSpeechEngine != null) {
			textToSpeechEngine.stop();
			textToSpeechEngine.shutdown();
		}
		if (legoNXT != null) {
			legoNXT.destroyCommunicator();
		}

	}

	@Override
	public void onBackPressed() {
		pauseOrContinue();
	}

	// StageDialog takes care of manageLoadAndFinish()
	public void manageLoadAndFinish() {
		if (soundManager != null) {
			soundManager.stopAllSounds();
		}
		if (textToSpeechEngine != null) {
			textToSpeechEngine.stop();
			textToSpeechEngine.shutdown();
		}
		ProjectManager projectManager = ProjectManager.getInstance();
		projectManager.loadProject(projectManager.getCurrentProject().getName(), this, false);
		projectManager.setCurrentSpriteWithPosition(spritePositionOnStageStart);
		projectManager.setCurrentScriptWithPosition(scriptPositionOnStageStart);

		finish();
		if (legoNXT != null) {
			legoNXT.destroyCommunicator();
		}
	}

	//changed to public so that StageDialog can use it
	public void pauseOrContinue() {
		if (stagePlaying) {
			stageManager.pause(true);
			if (soundManager != null) {
				soundManager.pause();
			}
			stagePlaying = false;
			stageDialog.show();
		} else {
			stageManager.resume();
			if (soundManager != null) {
				soundManager.resume();
			}
			stagePlaying = true;
			stageDialog.dismiss();
		}
	}

	@Override
	protected void onResume() {
		if (!Utils.checkForSdCard(this)) {
			return;
		}
		super.onResume();
	}

	//this method is called by text to speech engine once it finished initializing!
	public void onInit(int status) {
		if (status == TextToSpeech.ERROR) {
			Toast.makeText(StageActivity.this, R.string.text_to_speech_error, Toast.LENGTH_LONG).show();
			manageLoadAndFinish();
		} else {
			startStage();
		}
	}

	public static void textToSpeech(String Text) {

		HashMap<String, String> myHashAlarm = new HashMap<String, String>();
		myHashAlarm.put(TextToSpeech.Engine.KEY_PARAM_STREAM, String.valueOf(AudioManager.STREAM_MUSIC));
		int result = textToSpeechEngine.setLanguage(Locale.getDefault());
		if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
			Log.e(TAG, "Language is not available.");
		} else {
			textToSpeechEngine.speak(Text, TextToSpeech.QUEUE_FLUSH, myHashAlarm);
		}
	}

	public void onSwipe(int direction) {
	}

	public void onDoubleTap() {
	}

	public void onSingleTouch() {
	}

	public void onLongPress() {
	}

	public void reload() {
		if (soundManager != null) {
			soundManager.stopAllSounds();
		}
		if (textToSpeechEngine != null) {
			textToSpeechEngine.stop();
		}
		ProjectManager projectManager = ProjectManager.getInstance();
		projectManager.loadProject(projectManager.getCurrentProject().getName(), this, false);
		projectManager.setCurrentSpriteWithPosition(spritePositionOnStageStart);
		projectManager.setCurrentScriptWithPosition(scriptPositionOnStageStart);

		stageManager = new StageManager(this);
		stageDialog = new StageDialog(this, stageManager, R.style.stage_dialog);

		projectManager = ProjectManager.getInstance();
		spritePositionOnStageStart = projectManager.getCurrentSpritePosition();
		scriptPositionOnStageStart = projectManager.getCurrentScriptPosition();

		startStage();

	}
}

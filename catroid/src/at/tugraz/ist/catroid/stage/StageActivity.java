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
import android.view.Menu;
import android.view.MenuItem;
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
import at.tugraz.ist.catroid.io.SoundManager;
import at.tugraz.ist.catroid.stage.SimpleGestureFilter.SimpleGestureListener;
import at.tugraz.ist.catroid.utils.Utils;

public class StageActivity extends Activity implements SimpleGestureListener, OnInitListener {

	private static final int REQUEST_ENABLE_BT = 2000;
	private static final int REQUEST_CONNECT_DEVICE = 1000;
	private static final int MY_DATA_CHECK_CODE = 0;
	public static SurfaceView stage;
	private SoundManager soundManager;
	private StageManager stageManager;
	private boolean stagePlaying = false;
	//private Arduino arduino;
	private LegoNXT legoNXT;
	private BluetoothManager bluetoothManager;
	private ProgressDialog connectingProgressDialog;
	private static boolean simulatorMode = false;
	private SimpleGestureFilter detector;
	private final static String TAG = StageActivity.class.getSimpleName();
	public static TextToSpeech textToSpeechEngine;
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

			detector = new SimpleGestureFilter(this, this);

			if (stageManager.getTTSNeeded()) {
				Intent checkIntent = new Intent();
				checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
				startActivityForResult(checkIntent, MY_DATA_CHECK_CODE);
			}

			//startStage();
			if (!stageManager.getBluetoothNeeded()) {
				startStage();
			} else if (simulatorMode) {
				legoNXT = new LegoNXT(this, recieveHandler, simulatorMode);
				legoNXT.startSimCommunicator();
				startStage();
			} else {
				bluetoothManager = new BluetoothManager(this);
				//arduino = new Arduino(this, recieveHandler);
				legoNXT = new LegoNXT(this, recieveHandler, simulatorMode);
				int bluetoothState = bluetoothManager.activateBluetooth();
				if (bluetoothState == -1) {
					Toast.makeText(StageActivity.this, R.string.notification_blueth_err, Toast.LENGTH_LONG).show();
					finish();
				} else if (bluetoothState == 1) {
					startBTComm();
				}
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
						//arduino.startConnection(address);
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

	private void startBTComm() {
		connectingProgressDialog = ProgressDialog.show(this, "",
				getResources().getString(R.string.connecting_please_wait), true);

		Intent serverIntent = new Intent(this, DeviceListActivity.class);
		this.startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
	}

	public static void setSimulatorMode(boolean sim) {
		simulatorMode = sim;
	}

	public void startStage() {
		stageManager.startScripts();
		stageManager.start();
		stagePlaying = true;
	}

	//messages from Lego NXT device can be handled here
	final Handler recieveHandler = new Handler() {
		@Override
		public void handleMessage(Message myMessage) {

			//TODO ...
			if (simulatorMode) {
				return;
			}
			Log.i("bt", "message" + myMessage.getData().getInt("message"));
			switch (myMessage.getData().getInt("message")) {
				case LegoNXTBtCommunicator.STATE_CONNECTED:
					connectingProgressDialog.dismiss();
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
				manageLoadAndFinish();
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
		if (textToSpeechEngine != null) {
			textToSpeechEngine.stop();
			textToSpeechEngine.shutdown();
		}
		//arduino.destroyBTCommunicator();
		if (legoNXT != null) {
			legoNXT.destroyCommunicator();
		}
	}

	@Override
	public void onBackPressed() {
		soundManager.stopAllSounds();
		if (textToSpeechEngine != null) {
			textToSpeechEngine.stop();
			textToSpeechEngine.shutdown();
		}
		manageLoadAndFinish();
		//arduino.destroyBTCommunicator();
		if (legoNXT != null) {
			legoNXT.destroyCommunicator();
		}

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

	public void onInit(int status) {
		if (status == TextToSpeech.ERROR) {
			Toast.makeText(StageActivity.this, R.string.text_to_speech_error, Toast.LENGTH_LONG).show();
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
}

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

import java.util.ArrayList;
import java.util.Locale;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.LegoNXT.LegoNXT;
import at.tugraz.ist.catroid.LegoNXT.LegoNXTBtCommunicator;
import at.tugraz.ist.catroid.bluetooth.BluetoothManager;
import at.tugraz.ist.catroid.bluetooth.DeviceListActivity;
import at.tugraz.ist.catroid.common.Consts;
import at.tugraz.ist.catroid.common.Values;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.bricks.Brick;
import at.tugraz.ist.catroid.ui.dialogs.StageDialog;

import com.badlogic.gdx.backends.android.AndroidApplication;

/**
 * @author Johannes Iber
 * 
 */
public class StageActivity extends AndroidApplication {

	private static final int REQUEST_ENABLE_BT = 2000;
	private static final int REQUEST_CONNECT_DEVICE = 1000;

	private boolean stagePlaying = true;
	public static StageListener stageListener;
	private boolean resizePossible;
	private StageDialog stageDialog;
	private LegoNXT legoNXT;
	private ProgressDialog connectingProgressDialog;
	public static TextToSpeech textToSpeech;
	private int requiredResourceCounter = 0;
	Object mutex = new Object();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		int required_resources = getRequiredRessources();
		int mask = 0x1;
		int value = required_resources;
		boolean noResources = true;

		while (value > 0) {
			if ((mask & required_resources) > 0) {
				Log.i("bt", "res required: " + mask);
				requiredResourceCounter++;
				noResources = false;
			}
			value = value >> 1;
			mask = mask << 1;
		}
		if ((required_resources & Brick.TEXT_TO_SPEECH) > 0) {
			textToSpeech = new TextToSpeech(this, new OnInitListener() {
				public void onInit(int status) {
					if (status == TextToSpeech.ERROR) {
						Toast.makeText(StageActivity.this, "Error occurred while initializing Text-To-Speech engine",
								Toast.LENGTH_LONG).show();
					}
				}
			});

			if (textToSpeech.isLanguageAvailable(Locale.getDefault()) == TextToSpeech.LANG_MISSING_DATA) {
				Intent installIntent = new Intent();
				installIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
				startActivity(installIntent);
			}
			;
		}
		if ((required_resources & Brick.BLUETOOTH_LEGO_NXT) > 0) {
			BluetoothManager bluetoothManager = new BluetoothManager(this);
			legoNXT = new LegoNXT(this, recieveHandler);
			int bluetoothState = bluetoothManager.activateBluetooth();
			if (bluetoothState == -1) {
				Toast.makeText(StageActivity.this, R.string.notification_blueth_err, Toast.LENGTH_LONG).show();
				finish();
			} else if (bluetoothState == 1) {
				startBTComm();
			}
		}

		//		while (requiredResourceCounter > 0) {
		//			synchronized (mutex) {
		//				try {
		//					Log.i("bt", "wait");
		//					mutex.wait();
		//					
		//				} catch (InterruptedException e) {
		//					// TODO Auto-generated catch block
		//					e.printStackTrace();
		//				}
		//			}
		//		}

		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		stageListener = new StageListener();
		stageDialog = new StageDialog(this, stageListener, R.style.stage_dialog);
		this.calculateScreenSizes();
		initialize(stageListener, true);

	}

	private void startStage() {
		Log.i("bt", "ione");
		synchronized (mutex) {
			Log.i("bt", "notify");
			requiredResourceCounter = 0;
			mutex.notifyAll();
		}
	}

	private void startBTComm() {
		Log.i("bt", "lololol 1");
		connectingProgressDialog = ProgressDialog.show(this, "",
				getResources().getString(R.string.connecting_please_wait), true);
		Log.i("bt", "lololol 2");
		Intent serverIntent = new Intent(this, DeviceListActivity.class);
		this.startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
		Log.i("bt", "lololol 3");
	}

	private int getRequiredRessources() {

		ArrayList<Sprite> spriteList = (ArrayList<Sprite>) ProjectManager.getInstance().getCurrentProject()
				.getSpriteList();
		int ressources = Brick.NO_RESOURCES;

		for (Sprite sprite : spriteList) {
			ressources |= sprite.getRequiredResources();
		}
		return ressources;
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
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.stage_menu, menu);
		if (!resizePossible) {
			menu.removeItem(R.id.stagemenuScreenSize);
		}

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
			case R.id.stagemenuScreenSize:
				changeScreenSize();
				break;
			case R.id.stagemenuAxes:
				toggleAxes();
				break;
			case R.id.stagemenuScreenshot:
				if (stageListener.makeScreenshot()) {
					Toast.makeText(this, this.getString(R.string.notification_screenshot_ok), Toast.LENGTH_SHORT)
							.show();
				} else {
					Toast.makeText(this, this.getString(R.string.error_screenshot_failed), Toast.LENGTH_SHORT).show();
				}
				break;
		}
		return true;
	}

	@Override
	public void onBackPressed() {
		pauseOrContinue();
		stageDialog.show();
		//manageLoadAndFinish();
	}

	@Override
	protected void onDestroy() {
		if (stagePlaying) {
			this.manageLoadAndFinish();
		}
		super.onDestroy();
	}

	public void manageLoadAndFinish() {
		stageListener.pause();
		stageListener.finish();
		ProjectManager projectManager = ProjectManager.getInstance();
		int currentSpritePos = projectManager.getCurrentSpritePosition();
		int currentScriptPos = projectManager.getCurrentScriptPosition();
		projectManager.loadProject(projectManager.getCurrentProject().getName(), this, false);
		projectManager.setCurrentSpriteWithPosition(currentSpritePos);
		projectManager.setCurrentScriptWithPosition(currentScriptPos);
		stagePlaying = false;
		textToSpeech.shutdown();
		finish();
	}

	private void changeScreenSize() {
		switch (stageListener.screenMode) {
			case Consts.MAXIMIZE:
				stageListener.screenMode = Consts.STRETCH;
				break;
			case Consts.STRETCH:
				stageListener.screenMode = Consts.MAXIMIZE;
				break;
		}
	}

	public void toggleAxes() {
		if (stageListener.axesOn) {
			stageListener.axesOn = false;
		} else {
			stageListener.axesOn = true;
		}
	}

	public void pauseOrContinue() {
		if (stagePlaying) {
			stageListener.menuPause();
			stagePlaying = false;
		} else {
			stageListener.menuResume();
			stagePlaying = true;
		}
	}

	private void calculateScreenSizes() {
		int virtualScreenWidth = ProjectManager.getInstance().getCurrentProject().VIRTUAL_SCREEN_WIDTH;
		int virtualScreenHeight = ProjectManager.getInstance().getCurrentProject().VIRTUAL_SCREEN_HEIGHT;
		if (virtualScreenWidth == Values.SCREEN_WIDTH && virtualScreenHeight == Values.SCREEN_HEIGHT) {
			resizePossible = false;
			return;
		}
		resizePossible = true;
		stageListener.maximizeViewPortWidth = Values.SCREEN_WIDTH + 1;
		do {
			stageListener.maximizeViewPortWidth--;
			stageListener.maximizeViewPortHeight = (int) (((float) stageListener.maximizeViewPortWidth / (float) virtualScreenWidth) * virtualScreenHeight);
		} while (stageListener.maximizeViewPortHeight > Values.SCREEN_HEIGHT);

		stageListener.maximizeViewPortX = (Values.SCREEN_WIDTH - stageListener.maximizeViewPortWidth) / 2;
		stageListener.maximizeViewPortY = (Values.SCREEN_HEIGHT - stageListener.maximizeViewPortHeight) / 2;
	}

	public static void textToSpeech(String text) {
		textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null);
	}

	public void makeToast(String text) {
		Toast.makeText(this.getApplicationContext(), text, Toast.LENGTH_SHORT).show();
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

}

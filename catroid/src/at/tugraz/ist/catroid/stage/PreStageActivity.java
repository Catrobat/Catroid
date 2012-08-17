/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010-2011 The Catroid Team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://www.catroid.org/catroid_license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *   
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package at.tugraz.ist.catroid.stage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.speech.tts.TextToSpeech.OnUtteranceCompletedListener;
import android.util.Log;
import android.widget.Toast;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.LegoNXT.LegoNXT;
import at.tugraz.ist.catroid.LegoNXT.LegoNXTBtCommunicator;
import at.tugraz.ist.catroid.bluetooth.BluetoothManager;
import at.tugraz.ist.catroid.bluetooth.DeviceListActivity;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.bricks.Brick;

public class PreStageActivity extends Activity {

	private static final int REQUEST_ENABLE_BT = 2000;
	private static final int REQUEST_CONNECT_DEVICE = 1000;
	public static final int REQUEST_RESOURCES_INIT = 0101;
	public static final int MY_DATA_CHECK_CODE = 0;

	public static StageListener stageListener;
	private static LegoNXT legoNXT;
	private ProgressDialog connectingProgressDialog;
	private static TextToSpeech textToSpeech;
	private int requiredResourceCounter;

	private boolean autoConnect = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		int required_resources = getRequiredRessources();
		int mask = 0x1;
		int value = required_resources;
		boolean noResources = true;

		while (value > 0) {
			if ((mask & required_resources) > 0) {
				requiredResourceCounter++;
				noResources = false;
			}
			value = value >> 1;
			mask = mask << 1;
		}
		if ((required_resources & Brick.TEXT_TO_SPEECH) > 0) {
			Intent checkIntent = new Intent();
			checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
			startActivityForResult(checkIntent, MY_DATA_CHECK_CODE);
		}
		if ((required_resources & Brick.BLUETOOTH_LEGO_NXT) > 0) {
			BluetoothManager bluetoothManager = new BluetoothManager(this);

			int bluetoothState = bluetoothManager.activateBluetooth();
			if (bluetoothState == BluetoothManager.BLUETOOTH_NOT_SUPPORTED) {

				Toast.makeText(PreStageActivity.this, R.string.notification_blueth_err, Toast.LENGTH_LONG).show();
				resourceFailed();
			} else if (bluetoothState == BluetoothManager.BLUETOOTH_ALREADY_ON) {
				if (legoNXT == null) {
					startBTComm(true);
				} else {
					resourceInitialized();
				}

			}
		}
		if (noResources == true) {
			startStage();
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		if (requiredResourceCounter == 0) {
			finish();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

	}

	//all resources that should be reinitialized with every stage start
	public static void shutdownResources() {
		if (textToSpeech != null) {
			textToSpeech.stop();
			textToSpeech.shutdown();
		}
		if (legoNXT != null) {
			legoNXT.pauseCommunicator();
		}
	}

	//all resources that should not have to be reinitialized every stage start
	public static void shutdownPersistentResources() {

		if (legoNXT != null) {
			legoNXT.destroyCommunicator();
			legoNXT = null;
		}
	}

	private void resourceFailed() {
		Intent intent = this.getIntent();
		this.setResult(RESULT_CANCELED, intent);
		finish();
	}

	private synchronized void resourceInitialized() {
		//Log.i("res", "Resource initialized: " + requiredResourceCounter);

		requiredResourceCounter--;
		if (requiredResourceCounter <= 0) {
			startStage();

		}
	}

	public void startStage() {
		Intent intent = this.getIntent();
		this.setResult(RESULT_OK, intent);
		finish();
	}

	private void startBTComm(boolean autoConnect) {
		connectingProgressDialog = ProgressDialog.show(this, "",
				getResources().getString(R.string.connecting_please_wait), true);
		Intent serverIntent = new Intent(this, DeviceListActivity.class);
		serverIntent.putExtra(DeviceListActivity.AUTO_CONNECT, autoConnect);
		this.startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
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
						startBTComm(true);
						break;

					case Activity.RESULT_CANCELED:
						Toast.makeText(PreStageActivity.this, R.string.notification_blueth_err, Toast.LENGTH_LONG)
								.show();
						//finish();
						resourceFailed();
						break;
				}
				break;

			case REQUEST_CONNECT_DEVICE:
				switch (resultCode) {
					case Activity.RESULT_OK:
						legoNXT = new LegoNXT(this, recieveHandler);
						String address = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
						autoConnect = data.getExtras().getBoolean(DeviceListActivity.AUTO_CONNECT);
						legoNXT.startBTCommunicator(address);
						break;

					case Activity.RESULT_CANCELED:
						connectingProgressDialog.dismiss();
						Toast.makeText(PreStageActivity.this, R.string.bt_connection_failed, Toast.LENGTH_LONG).show();
						//finish();
						resourceFailed();
						break;
				}
				break;

			case MY_DATA_CHECK_CODE:
				if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
					// success, create the TTS instance
					textToSpeech = new TextToSpeech(getApplicationContext(), new OnInitListener() {
						@Override
						public void onInit(int status) {
							resourceInitialized();
							if (status == TextToSpeech.ERROR) {
								Toast.makeText(PreStageActivity.this,
										"Error occurred while initializing Text-To-Speech engine", Toast.LENGTH_LONG)
										.show();
								resourceFailed();
							}
						}
					});
					if (textToSpeech.isLanguageAvailable(Locale.getDefault()) == TextToSpeech.LANG_MISSING_DATA) {
						Intent installIntent = new Intent();
						installIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
						startActivity(installIntent);
						resourceFailed();
					}
					;
				} else {
					// missing data, install it
					AlertDialog.Builder builder = new AlertDialog.Builder(this);
					builder.setMessage(getString(R.string.text_to_speech_engine_not_installed)).setCancelable(false)
							.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int id) {
									Intent installIntent = new Intent();
									installIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
									startActivity(installIntent);
									resourceFailed();
								}
							}).setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int id) {
									dialog.cancel();
									resourceFailed();
								}
							});
					AlertDialog alert = builder.create();
					alert.show();

				}
				break;
			default:
				resourceFailed();
				break;
		}
	}

	@SuppressWarnings("deprecation")
	public static void textToSpeech(String text, OnUtteranceCompletedListener listener,
			HashMap<String, String> speakParameter) {
		textToSpeech.setOnUtteranceCompletedListener(listener);
		textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, speakParameter);
	}

	//messages from Lego NXT device can be handled here
	final Handler recieveHandler = new Handler() {
		@Override
		public void handleMessage(Message myMessage) {

			Log.i("bt", "message" + myMessage.getData().getInt("message"));
			switch (myMessage.getData().getInt("message")) {
				case LegoNXTBtCommunicator.STATE_CONNECTED:
					//autoConnect = false;
					connectingProgressDialog.dismiss();
					resourceInitialized();
					break;
				case LegoNXTBtCommunicator.STATE_CONNECTERROR:
					Toast.makeText(PreStageActivity.this, R.string.bt_connection_failed, Toast.LENGTH_SHORT).show();
					connectingProgressDialog.dismiss();
					legoNXT.destroyCommunicator();
					legoNXT = null;
					if (autoConnect) {
						startBTComm(false);
					} else {
						resourceFailed();
					}
					break;
			}
		}
	};
}

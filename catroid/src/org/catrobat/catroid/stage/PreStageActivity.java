/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2013 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://developer.catrobat.org/license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *  
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.stage;

import android.annotation.SuppressLint;
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

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.bluetooth.BluetoothManager;
import org.catrobat.catroid.bluetooth.DeviceListActivity;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.legonxt.LegoNXT;
import org.catrobat.catroid.legonxt.LegoNXTBtCommunicator;
import org.catrobat.catroid.robot.albert.RobotAlbert;
import org.catrobat.catroid.robot.albert.RobotAlbertBtCommunicator;
import org.catrobat.catroid.robot.albert.SensorRobotAlbert;
import org.catrobat.catroid.ui.dialogs.CustomAlertDialogBuilder;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

@SuppressWarnings("deprecation")
public class PreStageActivity extends Activity {
	private static final String TAG = PreStageActivity.class.getSimpleName();

	private static final int REQUEST_ENABLE_BLUETOOTH = 2000;
	private static final int REQUEST_ENABLE_BLUETOOTH_WITH_TEXT = 2001;
	private String bluetoothDeviceName;
	private String bluetoothDeviceWaitingText;

	private static final int REQUEST_CONNECT_DEVICE = 1000;
	public static final int REQUEST_RESOURCES_INIT = 101;
	public static final int REQUEST_TEXT_TO_SPEECH = 10;

	private int requiredResourceCounter;
	private static LegoNXT legoNXT;
	private ProgressDialog connectingProgressDialog;
	private static TextToSpeech textToSpeech;
	private static OnUtteranceCompletedListenerContainer onUtteranceCompletedListenerContainer;

	private boolean autoConnect = false;

	private static RobotAlbert robotAlbert;

	private boolean nxtActive = false;
	private boolean robotAlbertActive = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		int requiredResources = getRequiredRessources();
		requiredResourceCounter = Integer.bitCount(requiredResources);
		BluetoothManager bluetoothManager = null;

		setContentView(R.layout.activity_prestage);

		if ((requiredResources & Brick.TEXT_TO_SPEECH) > 0) {
			Intent checkIntent = new Intent();
			checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
			startActivityForResult(checkIntent, REQUEST_TEXT_TO_SPEECH);
		}
		if ((requiredResources & Brick.BLUETOOTH_LEGO_NXT) > 0) {
			bluetoothManager = new BluetoothManager(this);

			int bluetoothState = bluetoothManager.activateBluetooth();
			if (bluetoothState == BluetoothManager.BLUETOOTH_NOT_SUPPORTED) {

				Toast.makeText(PreStageActivity.this, R.string.notification_blueth_err, Toast.LENGTH_LONG).show();
				resourceFailed();
			} else if (bluetoothState == BluetoothManager.BLUETOOTH_ALREADY_ON) {
				nxtActive = true;

				if (legoNXT == null) {
					startBluetoothCommunication(true);
				} else {
					resourceInitialized();
				}

			}
		}
		if ((requiredResources & Brick.BLUETOOTH_ROBOT_ALBERT) > 0) {
			Log.d("RobotAlbert", "Albert-Brick recognized");
			if (bluetoothManager == null) {
				bluetoothManager = new BluetoothManager(this);
			}

			//set flag to start thread to update distance sensor values in formula editor
			SensorRobotAlbert sensor = SensorRobotAlbert.getSensorRobotAlbertInstance();
			sensor.setBooleanAlbertBricks(true);

			String waitingText = getResources().getString(R.string.robot_albert_connecting_please_wait);
			String title = getResources().getString(R.string.robot_albert_select_device);
			bluetoothDeviceName = title;
			bluetoothDeviceWaitingText = waitingText;
			int bluetoothState = bluetoothManager.activateBluetooth(title, waitingText);
			if (bluetoothState == BluetoothManager.BLUETOOTH_NOT_SUPPORTED) {
				Log.d("PreStageActivity", "Bluetooth not supported");
				Toast.makeText(PreStageActivity.this, R.string.notification_blueth_err, Toast.LENGTH_LONG).show();
				resourceFailed();
			} else if (bluetoothState == BluetoothManager.BLUETOOTH_ALREADY_ON) {
				Log.d("PrestageActivity", "Bluetooth already on");
				robotAlbertActive = true;
				if (robotAlbert == null) {
					startBluetoothCommunication(true, title, waitingText);
				} else {
					resourceInitialized();
				}
			}
		} else {
			//set flag not to start a thread to update distance sensor values in formula editor
			//this is necessary, because if after running a project with this sensor and then edit it, not to use them anymore, then change the flag
			SensorRobotAlbert sensor = SensorRobotAlbert.getSensorRobotAlbertInstance();
			sensor.setBooleanAlbertBricks(false);
		}
		if (requiredResourceCounter == Brick.NO_RESOURCES) {
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

	//all resources that should be reinitialized with every stage start
	public static void shutdownResources() {
		if (textToSpeech != null) {
			textToSpeech.stop();
			textToSpeech.shutdown();
		}
		if (legoNXT != null) {
			legoNXT.pauseCommunicator();
		}
		if (robotAlbert != null) {
			robotAlbert.pauseCommunicator();
		}
	}

	//all resources that should not have to be reinitialized every stage start
	public static void shutdownPersistentResources() {
		if (legoNXT != null) {
			legoNXT.destroyCommunicator();
			legoNXT = null;
		}
		if (robotAlbert != null) {
			robotAlbert.destroyCommunicator();
			robotAlbert = null;
		}
		deleteSpeechFiles();
	}

	private static void deleteSpeechFiles() {
		File pathToSpeechFiles = new File(Constants.TEXT_TO_SPEECH_TMP_PATH);
		if (pathToSpeechFiles.isDirectory()) {
			for (File file : pathToSpeechFiles.listFiles()) {
				file.delete();
			}
		}
	}

	private void resourceFailed() {
		setResult(RESULT_CANCELED, getIntent());
		finish();
	}

	private synchronized void resourceInitialized() {
		//Log.i("res", "Resource initialized: " + requiredResourceCounter);

		requiredResourceCounter--;
		if (requiredResourceCounter == 0) {
			startStage();
		}
	}

	public void startStage() {
		setResult(RESULT_OK, getIntent());
		finish();
	}

	private void startBluetoothCommunication(boolean autoConnect) {
		connectingProgressDialog = ProgressDialog.show(this, "",
				getResources().getString(R.string.connecting_please_wait), true);
		Intent serverIntent = new Intent(this, DeviceListActivity.class);
		serverIntent.putExtra(DeviceListActivity.AUTO_CONNECT, autoConnect);
		this.startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
	}

	private void startBluetoothCommunication(boolean autoConnect, String title, String waitingText) {
		Log.d("PreStageActivity", "startBluetoothCommunication with custom Title");
		connectingProgressDialog = ProgressDialog.show(this, "", waitingText, true);
		Intent serverIntent = new Intent(this, DeviceListActivity.class);
		serverIntent.putExtra(DeviceListActivity.AUTO_CONNECT, autoConnect);
		serverIntent.putExtra(DeviceListActivity.OTHER_DEVICE_TITLE, title);
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
			case REQUEST_ENABLE_BLUETOOTH:
				switch (resultCode) {
					case Activity.RESULT_OK:
						nxtActive = true;
						startBluetoothCommunication(true);
						break;
					case Activity.RESULT_CANCELED:
						Toast.makeText(PreStageActivity.this, R.string.notification_blueth_err, Toast.LENGTH_LONG)
								.show();
						resourceFailed();
						break;
				}
				break;

			case REQUEST_ENABLE_BLUETOOTH_WITH_TEXT:
				switch (resultCode) {
					case Activity.RESULT_OK:
						Log.d("test", "test data=" + data);
						robotAlbertActive = true;
						startBluetoothCommunication(true, bluetoothDeviceName, bluetoothDeviceWaitingText);
						break;
					case Activity.RESULT_CANCELED:
						Toast.makeText(PreStageActivity.this, R.string.notification_blueth_err, Toast.LENGTH_LONG)
								.show();
						resourceFailed();
						break;
				}
				break;

			case REQUEST_CONNECT_DEVICE:
				switch (resultCode) {
					case Activity.RESULT_OK:
						if (nxtActive == true) {
							Log.d("LegoNXT", "PreStageActivity:onActivityResult: NXT recognized");
							legoNXT = new LegoNXT(this, recieveHandler);
							String address = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
							autoConnect = data.getExtras().getBoolean(DeviceListActivity.AUTO_CONNECT);
							legoNXT.startBTCommunicator(address);
						}
						if (robotAlbertActive == true) {
							Log.d("RobotAlbert", "PreStageActivity:onActivityResult: Albert recognized");
							robotAlbert = new RobotAlbert(this, recieveHandler);
							String address = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
							autoConnect = data.getExtras().getBoolean(DeviceListActivity.AUTO_CONNECT);
							robotAlbert.startBTCommunicator(address);
						}
						break;

					case Activity.RESULT_CANCELED:
						connectingProgressDialog.dismiss();
						Toast.makeText(PreStageActivity.this, R.string.bt_connection_failed, Toast.LENGTH_LONG).show();
						resourceFailed();
						break;
				}
				break;

			case REQUEST_TEXT_TO_SPEECH:
				if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
					textToSpeech = new TextToSpeech(getApplicationContext(), new OnInitListener() {
						@Override
						public void onInit(int status) {
							onUtteranceCompletedListenerContainer = new OnUtteranceCompletedListenerContainer();
							textToSpeech.setOnUtteranceCompletedListener(onUtteranceCompletedListenerContainer);
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
				} else {
					AlertDialog.Builder builder = new CustomAlertDialogBuilder(this);
					builder.setMessage(R.string.text_to_speech_engine_not_installed).setCancelable(false)
							.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int id) {
									Intent installIntent = new Intent();
									installIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
									startActivity(installIntent);
									resourceFailed();
								}
							}).setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
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

	public static void textToSpeech(String text, File speechFile, OnUtteranceCompletedListener listener,
			HashMap<String, String> speakParameter) {
		if (text == null) {
			text = "";
		}

		if (onUtteranceCompletedListenerContainer.addOnUtteranceCompletedListener(speechFile, listener,
				speakParameter.get(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID))) {
			int status = textToSpeech.synthesizeToFile(text, speakParameter, speechFile.getAbsolutePath());
			if (status == TextToSpeech.ERROR) {
				Log.e(TAG, "File synthesizing failed");
			}
		}
	}

	//messages from Lego NXT device can be handled here
	// TODO should be fixed - could lead to problems
	@SuppressLint("HandlerLeak")
	final Handler recieveHandler = new Handler() {
		@Override
		public void handleMessage(Message myMessage) {

			Log.i("bt", "message" + myMessage.getData().getInt("message"));

			if (nxtActive == true) {
				Log.d("LegoNXT", "recieveHandler:handleMessage: NXT recognized");
				switch (myMessage.getData().getInt("message")) {
					case LegoNXTBtCommunicator.STATE_CONNECTED:
						//autoConnect = false;
						connectingProgressDialog.dismiss();
						resourceInitialized();
						break;
					case LegoNXTBtCommunicator.STATE_CONNECTERROR:
						Toast.makeText(PreStageActivity.this, R.string.bt_connection_failed, Toast.LENGTH_LONG).show();
						connectingProgressDialog.dismiss();
						legoNXT.destroyCommunicator();
						legoNXT = null;
						if (autoConnect) {
							startBluetoothCommunication(false);
						} else {
							resourceFailed();
						}
						break;
				}
			}
			if (robotAlbertActive == true) {

				Log.d("RobotAlbert", "recieveHandler:handleMessage: Albert recognized");

				switch (myMessage.getData().getInt("message")) {
					case RobotAlbertBtCommunicator.STATE_CONNECTED:
						//autoConnect = false;
						connectingProgressDialog.dismiss();
						resourceInitialized();
						break;
					case RobotAlbertBtCommunicator.STATE_CONNECTERROR:
						Toast.makeText(PreStageActivity.this, R.string.bt_connection_failed, Toast.LENGTH_SHORT).show();
						connectingProgressDialog.dismiss();
						if (robotAlbert != null) {
							robotAlbert.destroyCommunicator(); //exception thrown when program exit
						}
						robotAlbert = null;
						if (autoConnect) {
							String waitingText = getResources().getString(R.string.robot_albert_connecting_please_wait);
							String title = getResources().getString(R.string.robot_albert_select_device);
							startBluetoothCommunication(false, title, waitingText);
						} else {
							resourceFailed();
						}
						break;
				}
			}

		}
	};
}

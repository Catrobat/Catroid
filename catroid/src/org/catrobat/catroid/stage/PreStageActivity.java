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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.LegoNXT.LegoNXT;
import org.catrobat.catroid.LegoNXT.LegoNXTBtCommunicator;
import org.catrobat.catroid.bluetooth.BluetoothManager;
import org.catrobat.catroid.bluetooth.DeviceListActivity;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.robot.albert.RobotAlbert;
import org.catrobat.catroid.robot.albert.RobotAlbertBtCommunicator;

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

public class PreStageActivity extends Activity {

	private static final int REQUEST_ENABLE_BLUETOOTH = 2000;
	private static final int REQUEST_ENABLE_BLUETOOTH_WITH_TEXT = 2001;
	private String bluetoothDeviceName;
	private String bluetoothDeviceWaitingText;

	private static final int REQUEST_CONNECT_DEVICE = 1000;
	public static final int REQUEST_RESOURCES_INIT = 0101;
	public static final int REQUEST_TEXT_TO_SPEECH = 0;

	private int requiredResourceCounter;
	private static LegoNXT legoNXT;
	private ProgressDialog connectingProgressDialog;
	private static TextToSpeech textToSpeech;
	private static OnUtteranceCompletedListenerContainer onUtteranceCompletedListenerContainer;

	private boolean autoConnect = false;

	private static RobotAlbert robotAlbert;

	private boolean nxt_active = false;
	private boolean robot_albert_active = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		int required_resources = getRequiredRessources();
		requiredResourceCounter = Integer.bitCount(required_resources);
		BluetoothManager bluetoothManager = null;

		if ((required_resources & Brick.TEXT_TO_SPEECH) > 0) {
			Intent checkIntent = new Intent();
			checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
			startActivityForResult(checkIntent, REQUEST_TEXT_TO_SPEECH);
		}
		if ((required_resources & Brick.BLUETOOTH_LEGO_NXT) > 0) {
			Log.d("LegoNXT", "LegoNXT-Brick recognized");
			bluetoothManager = new BluetoothManager(this);

			int bluetoothState = bluetoothManager.activateBluetooth();
			if (bluetoothState == BluetoothManager.BLUETOOTH_NOT_SUPPORTED) {

				Toast.makeText(PreStageActivity.this, R.string.notification_blueth_err, Toast.LENGTH_LONG).show();
				resourceFailed();
			} else if (bluetoothState == BluetoothManager.BLUETOOTH_ALREADY_ON) {
				nxt_active = true;

				if (legoNXT == null) {
					startBluetoothCommunication(true);
				} else {
					resourceInitialized();
				}

			}
		}
		if ((required_resources & Brick.BLUETOOTH_ROBOT_ALBERT) > 0) {
			Log.d("RobotAlbert", "Albert-Brick recognized");
			if (bluetoothManager == null) {
				bluetoothManager = new BluetoothManager(this);
			}
			String waiting_text = getResources().getString(R.string.connecting_please_wait_robot_albert);
			String title = getResources().getString(R.string.select_device_robot_albert);
			bluetoothDeviceName = title;
			bluetoothDeviceWaitingText = waiting_text;
			int bluetoothState = bluetoothManager.activateBluetooth(title, waiting_text);
			if (bluetoothState == BluetoothManager.BLUETOOTH_NOT_SUPPORTED) {
				Log.d("PreStageActivity", "Bluetooth not supported");
				Toast.makeText(PreStageActivity.this, R.string.notification_blueth_err, Toast.LENGTH_LONG).show();
				resourceFailed();
			} else if (bluetoothState == BluetoothManager.BLUETOOTH_ALREADY_ON) {
				Log.d("PrestageActivity", "Bluetooth already on");
				robot_albert_active = true;
				if (robotAlbert == null) {
					startBluetoothCommunication(true, title, waiting_text);
				} else {
					resourceInitialized();
				}
			} /*
			 * else if (bluetoothState == BluetoothManager.BLUETOOTH_ACTIVATING) {
			 * 
			 * Log.d("PrestageActivity", "Bluetooth activating");
			 * 
			 * robot_albert_active = true;
			 * if (robotAlbert == null) {
			 * startBluetoothCommunication(true, title, waiting_text);
			 * } else {
			 * resourceInitialized();
			 * }
			 * }
			 */
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
		Log.d("hiiii", "hiiii");
		connectingProgressDialog = ProgressDialog.show(this, "",
				getResources().getString(R.string.connecting_please_wait), true);
		Intent serverIntent = new Intent(this, DeviceListActivity.class);
		serverIntent.putExtra(DeviceListActivity.AUTO_CONNECT, autoConnect);
		this.startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
	}

	private void startBluetoothCommunication(boolean autoConnect, String title, String waiting_text) {
		Log.d("PreStageActivity", "startBluetoothCommunication with custom Title");
		connectingProgressDialog = ProgressDialog.show(this, "", waiting_text, true);
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

	@SuppressWarnings("deprecation")
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.i("bt", "requestcode " + requestCode + " result code" + resultCode);

		switch (requestCode) {
			case REQUEST_ENABLE_BLUETOOTH:
				switch (resultCode) {
					case Activity.RESULT_OK:
						nxt_active = true;
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
						robot_albert_active = true;
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
						if (nxt_active == true) {
							Log.d("LegoNXT", "PreStageActivity:onActivityResult: NXT recognized");
							legoNXT = new LegoNXT(this, recieveHandler);
							String address = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
							autoConnect = data.getExtras().getBoolean(DeviceListActivity.AUTO_CONNECT);
							legoNXT.startBTCommunicator(address);
						}
						if (robot_albert_active == true) {
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

	public static void textToSpeech(String text, OnUtteranceCompletedListener listener,
			HashMap<String, String> speakParameter) {
		if (text == null) {
			text = "";
		}

		onUtteranceCompletedListenerContainer.addOnUtteranceCompletedListener(listener,
				speakParameter.get(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID));
		textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, speakParameter);
	}

	//messages from Lego NXT device can be handled here
	final Handler recieveHandler = new Handler() {
		@Override
		public void handleMessage(Message myMessage) {

			Log.i("bt", "message" + myMessage.getData().getInt("message"));

			if (nxt_active == true) {
				Log.d("LegoNXT", "recieveHandler:handleMessage: NXT recognized");
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
							startBluetoothCommunication(false);
						} else {
							resourceFailed();
						}
						break;
				}
			}
			if (robot_albert_active == true) {

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
						robotAlbert.destroyCommunicator();
						robotAlbert = null;
						if (autoConnect) {
							String waiting_text = getResources()
									.getString(R.string.connecting_please_wait_robot_albert);
							String title = getResources().getString(R.string.select_device_robot_albert);
							startBluetoothCommunication(false, title, waiting_text);
						} else {
							resourceFailed();
						}
						break;
				}
			}

		}
	};
}

/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2015 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * An additional term exception under section 7 of the GNU Affero
 * General Public License, version 3, is available at
 * http://developer.catrobat.org/license_additional_term
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.stage;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.speech.tts.TextToSpeech.OnUtteranceCompletedListener;
import android.util.Log;
import android.widget.Toast;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.bluetooth.BluetoothManager;
import org.catrobat.catroid.bluetooth.DeviceListActivity;
import org.catrobat.catroid.camera.CameraManager;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.drone.DroneInitializer;
import org.catrobat.catroid.facedetection.FaceDetectionHandler;
import org.catrobat.catroid.legonxt.LegoNXT;
import org.catrobat.catroid.legonxt.LegoNXTBtCommunicator;
import org.catrobat.catroid.ui.BaseActivity;
import org.catrobat.catroid.ui.dialogs.CustomAlertDialogBuilder;
import org.catrobat.catroid.utils.LedUtil;
import org.catrobat.catroid.utils.VibratorUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

@SuppressWarnings("deprecation")
public class PreStageActivity extends BaseActivity {

	private static final String TAG = PreStageActivity.class.getSimpleName();
	private static final int REQUEST_ENABLE_BLUETOOTH = 2000;
	private static final int REQUEST_CONNECT_DEVICE = 1000;
	public static final int REQUEST_RESOURCES_INIT = 101;
	public static final int REQUEST_TEXT_TO_SPEECH = 10;

	private int resources = Brick.NO_RESOURCES;
	private int requiredResourceCounter;

	private static LegoNXT legoNXT;
	private boolean autoConnect = false;
	private ProgressDialog connectingProgressDialog;
	private static TextToSpeech textToSpeech;
	private static OnUtteranceCompletedListenerContainer onUtteranceCompletedListenerContainer;

	private DroneInitializer droneInitializer = null;

	private Intent returnToActivityIntent = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		returnToActivityIntent = new Intent();

		if (isFinishing()) {
			return;
		}

		setContentView(R.layout.activity_prestage);

		int requiredResources = getRequiredRessources();
		requiredResourceCounter = Integer.bitCount(requiredResources);

		if ((requiredResources & Brick.TEXT_TO_SPEECH) > 0) {
			Intent checkIntent = new Intent();
			checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
			startActivityForResult(checkIntent, REQUEST_TEXT_TO_SPEECH);
		}

		if ((requiredResources & Brick.BLUETOOTH_LEGO_NXT) > 0) {
			BluetoothManager bluetoothManager = new BluetoothManager(this);

			int bluetoothState = bluetoothManager.activateBluetooth();
			if (bluetoothState == BluetoothManager.BLUETOOTH_NOT_SUPPORTED) {

				Toast.makeText(PreStageActivity.this, R.string.notification_blueth_err, Toast.LENGTH_LONG).show();
				resourceFailed();
			} else if (bluetoothState == BluetoothManager.BLUETOOTH_ALREADY_ON) {
				if (legoNXT == null) {
					startBluetoothCommunication(true);
				} else {
					resourceInitialized();
				}
			}
		}

		if ((requiredResources & Brick.ARDRONE_SUPPORT) > 0) {
			droneInitializer = getDroneInitializer();
			droneInitializer.initialise();
		}

		FaceDetectionHandler.resetFaceDedection();
		if ((requiredResources & Brick.FACE_DETECTION) > 0) {
			boolean success = FaceDetectionHandler.startFaceDetection(this);
			if (success) {
				resourceInitialized();
			} else {
				resourceFailed();
			}
		}

		if ((requiredResources & Brick.CAMERA_LED ) > 0) {
			if (!CameraManager.getInstance().isFacingBack()) {
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setMessage(getString(R.string.led_and_front_camera_warning)).setCancelable(false)
						.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int id) {
								ledInitialize();
							}
						});
				AlertDialog alert = builder.create();
				alert.show();
			} else {
				ledInitialize();
			}
		}

		if ((requiredResources & Brick.VIBRATOR) > 0) {
			Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
			if (vibrator != null) {
				requiredResourceCounter--;
				VibratorUtil.setContext(this.getBaseContext());
				VibratorUtil.activateVibratorThread();
			} else {
				Toast.makeText(PreStageActivity.this, R.string.no_vibrator_available, Toast.LENGTH_LONG).show();
				resourceFailed();
			}
		}

		if (requiredResourceCounter == Brick.NO_RESOURCES) {
			startStage();
		}
	}

	public DroneInitializer getDroneInitializer() {
		if (droneInitializer == null) {
			droneInitializer = new DroneInitializer(this, returnToActivityIntent);
		}
		return droneInitializer;
	}

	protected boolean hasFlash() {
		boolean hasCamera = getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
		boolean hasLed = getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);

		if (!hasCamera || !hasLed) {
			return false;
		}

		Camera camera = CameraManager.getInstance().getCamera();

		try {
			if (camera == null) {
				camera = CameraManager.getInstance().getCamera();
			}
		} catch (Exception exception) {
			Log.e(getString(R.string.app_name), "failed to open Camera", exception);
		}

		if (camera == null) {
			return false;
		}

		Camera.Parameters parameters = camera.getParameters();

		if (parameters.getFlashMode() == null) {
			return false;
		}

		List<String> supportedFlashModes = parameters.getSupportedFlashModes();
		if (supportedFlashModes == null || supportedFlashModes.isEmpty() ||
				supportedFlashModes.size() == 1 && supportedFlashModes.get(0).equals(Camera.Parameters.FLASH_MODE_OFF)) {
			return false;
		}

		return true;
	}

	@Override
	public void onResume() {
		if (droneInitializer != null) {
			droneInitializer.onPrestageActivityResume();
		}

		super.onResume();
		if (requiredResourceCounter == 0) {
			finish();
		}
	}

	@Override
	protected void onPause() {
		if (droneInitializer != null) {
			droneInitializer.onPrestageActivityPause();
		}

		super.onPause();
	}

	@Override
	protected void onDestroy() {
		if (droneInitializer != null) {
			droneInitializer.onPrestageActivityDestroy();
		}

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
        if (FaceDetectionHandler.isFaceDetectionRunning()) {
            FaceDetectionHandler.stopFaceDetection();
        }
	}

	//all resources that should not have to be reinitialized every stage start
	public static void shutdownPersistentResources() {
		if (legoNXT != null) {
			legoNXT.destroyCommunicator();
			legoNXT = null;
		}
		deleteSpeechFiles();
		if (LedUtil.isActive()) {
			LedUtil.destroy();
		}
		if (VibratorUtil.isActive()) {
			VibratorUtil.destroy();
		}
	}

	private static void deleteSpeechFiles() {
		File pathToSpeechFiles = new File(Constants.TEXT_TO_SPEECH_TMP_PATH);
		if (pathToSpeechFiles.isDirectory()) {
			for (File file : pathToSpeechFiles.listFiles()) {
				file.delete();
			}
		}
	}

	public void resourceFailed() {
		setResult(RESULT_CANCELED, returnToActivityIntent);
		finish();
	}

	public synchronized void resourceInitialized() {
		requiredResourceCounter--;
		if (requiredResourceCounter == 0) {
			Log.d(TAG, "Start Stage");

			startStage();
		}
	}

	public void startStage() {
		setResult(RESULT_OK, returnToActivityIntent);
		finish();
	}

	private void startBluetoothCommunication(boolean autoConnect) {
		connectingProgressDialog = ProgressDialog.show(this, "",
				getResources().getString(R.string.connecting_please_wait), true);
		Intent serverIntent = new Intent(this, DeviceListActivity.class);
		serverIntent.putExtra(DeviceListActivity.AUTO_CONNECT, autoConnect);
		this.startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
	}

	private int getRequiredRessources() {
		ArrayList<Sprite> spriteList = (ArrayList<Sprite>) ProjectManager.getInstance().getCurrentProject()
				.getSpriteList();

		resources = Brick.NO_RESOURCES;
		for (Sprite sprite : spriteList) {
			resources |= sprite.getRequiredResources();
		}
		return resources;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.i("bt", "requestcode " + requestCode + " result code" + resultCode);

		switch (requestCode) {
			case REQUEST_ENABLE_BLUETOOTH:
				switch (resultCode) {
					case Activity.RESULT_OK:
						startBluetoothCommunication(true);
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
						legoNXT = new LegoNXT(this, recieveHandler);
						String address = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
						autoConnect = data.getExtras().getBoolean(DeviceListActivity.AUTO_CONNECT);
						legoNXT.startBTCommunicator(address);
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
				default:
					return;
			}
		}
	};

	private void ledInitialize() {
		if ( hasFlash() ) {
			resourceInitialized();
			LedUtil.activateLedThread();
		} else {
			Toast.makeText(PreStageActivity.this, R.string.no_flash_led_available, Toast.LENGTH_LONG).show();
			resourceFailed();
		}
	}

}

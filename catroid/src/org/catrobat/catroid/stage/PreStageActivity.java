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

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.speech.tts.TextToSpeech.OnUtteranceCompletedListener;
import android.util.Log;

import org.catrobat.catroid.CatroidApplication;
import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.bluetooth.base.BluetoothDevice;
import org.catrobat.catroid.bluetooth.base.BluetoothDeviceService;
import org.catrobat.catroid.camera.CameraManager;
import org.catrobat.catroid.common.CatroidService;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.ServiceProvider;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.devices.raspberrypi.RaspberryPiImpl;
import org.catrobat.catroid.devices.raspberrypi.RaspberryPiService;
import org.catrobat.catroid.drone.DroneInitializer;
import org.catrobat.catroid.drone.DroneServiceWrapper;
import org.catrobat.catroid.facedetection.FaceDetectionHandler;
import org.catrobat.catroid.ui.BaseActivity;
import org.catrobat.catroid.ui.SettingsActivity;
import org.catrobat.catroid.ui.dialogs.CustomAlertDialogBuilder;
import org.catrobat.catroid.utils.FlashUtil;
import org.catrobat.catroid.utils.ToastUtil;
import org.catrobat.catroid.utils.VibratorUtil;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;

@SuppressWarnings("deprecation")
public class PreStageActivity extends BaseActivity {

	private static final String TAG = PreStageActivity.class.getSimpleName();
	private static final int REQUEST_CONNECT_DEVICE = 1000;
	public static final int REQUEST_RESOURCES_INIT = 101;
	public static final int REQUEST_TEXT_TO_SPEECH = 10;

	private int requiredResourceCounter;

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

		int requiredResources = ProjectManager.getInstance().getCurrentProject().getRequiredResources();
		requiredResourceCounter = Integer.bitCount(requiredResources);

		if ((requiredResources & Brick.TEXT_TO_SPEECH) > 0) {
			Intent checkIntent = new Intent();
			checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
			startActivityForResult(checkIntent, REQUEST_TEXT_TO_SPEECH);
		}

		if ((requiredResources & Brick.BLUETOOTH_LEGO_NXT) > 0) {
			connectBTDevice(BluetoothDevice.LEGO_NXT);
		}

		if ((requiredResources & Brick.BLUETOOTH_PHIRO) > 0) {
			connectBTDevice(BluetoothDevice.PHIRO);
		}

		if ((requiredResources & Brick.BLUETOOTH_SENSORS_ARDUINO) > 0) {
			connectBTDevice(BluetoothDevice.ARDUINO);
		}

		if (DroneServiceWrapper.checkARDroneAvailability()) {
			CatroidApplication.loadNativeLibs();
			if (CatroidApplication.parrotLibrariesLoaded) {
				droneInitializer = getDroneInitialiser();
				droneInitializer.initialise();
			}
		}

		if ((requiredResources & Brick.CAMERA_BACK) > 0) {
			if (CameraManager.getInstance().hasBackCamera()) {
				resourceInitialized();
			} else {
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setMessage(getString(R.string.no_back_camera_available)).setCancelable(false)
						.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int id) {
								resourceFailed();
							}
						});
				AlertDialog alert = builder.create();
				alert.show();
			}
		}

		if ((requiredResources & Brick.CAMERA_FRONT) > 0) {
			if (CameraManager.getInstance().hasFrontCamera()) {
				resourceInitialized();
			} else {
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setMessage(getString(R.string.no_front_camera_available)).setCancelable(false)
						.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int id) {
								resourceFailed();
							}
						});
				AlertDialog alert = builder.create();
				alert.show();
			}
		}

		if ((requiredResources & Brick.CAMERA_FLASH) > 0) {
			flashInitialize();
		}

		if ((requiredResources & Brick.VIBRATOR) > 0) {
			Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
			if (vibrator != null) {
				requiredResourceCounter--;
				VibratorUtil.setContext(this.getBaseContext());
				VibratorUtil.activateVibratorThread();
			} else {
				ToastUtil.showError(PreStageActivity.this, R.string.no_vibrator_available);
				resourceFailed();
			}
		}

		FaceDetectionHandler.resetFaceDedection();
		if ((requiredResources & Brick.FACE_DETECTION) > 0) {
			boolean success = FaceDetectionHandler.startFaceDetection();
			if (success) {
				resourceInitialized();
			} else {
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setMessage(getString(R.string.no_camera_available)).setCancelable(false)
						.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int id) {
								resourceFailed();
							}
						});
				AlertDialog alert = builder.create();
				alert.show();
			}
		}

		if (requiredResourceCounter == Brick.NO_RESOURCES) {
			startStage();
		}

		if ((requiredResources & Brick.SOCKET_RASPI) > 0) {
			connectRaspberrySocket();
		}
	}

	private void connectBTDevice(Class<? extends BluetoothDevice> service) {
		BluetoothDeviceService btService = ServiceProvider.getService(CatroidService.BLUETOOTH_DEVICE_SERVICE);

		if (btService.connectDevice(service, this, REQUEST_CONNECT_DEVICE)
				== BluetoothDeviceService.ConnectDeviceResult.ALREADY_CONNECTED) {
			resourceInitialized();
		}
	}

	private void connectRaspberrySocket() {
		String host = SettingsActivity.getRaspiHost(this.getBaseContext());
		int port = SettingsActivity.getRaspiPort(this.getBaseContext());

		if (RaspberryPiService.getInstance().connect(host, port)) {
			resourceInitialized();
		} else {
			ToastUtil.showError(PreStageActivity.this, "Error: connecting to " + host + ":" + port + " failed");
			resourceFailed();
		}
	}


	public DroneInitializer getDroneInitialiser() {
		if (droneInitializer == null) {
			droneInitializer = new DroneInitializer(this);
		}
		return droneInitializer;
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

		ServiceProvider.getService(CatroidService.BLUETOOTH_DEVICE_SERVICE).pause();

		if (FaceDetectionHandler.isFaceDetectionRunning()) {
			FaceDetectionHandler.stopFaceDetection();
		}

		RaspberryPiService.getInstance().disconnect();
	}

	//all resources that should not have to be reinitialized every stage start
	public static void shutdownPersistentResources() {

		ServiceProvider.getService(CatroidService.BLUETOOTH_DEVICE_SERVICE).disconnectDevices();

		deleteSpeechFiles();
		if (FlashUtil.isAvailable()) {
			FlashUtil.destroy();
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

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.i("bt", "requestcode " + requestCode + " result code" + resultCode);

		switch (requestCode) {

			case REQUEST_CONNECT_DEVICE:
				switch (resultCode) {
					case Activity.RESULT_OK:
						resourceInitialized();
						break;

					case Activity.RESULT_CANCELED:
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
								ToastUtil.showError(PreStageActivity.this, "Error occurred while initializing Text-To-Speech engine");
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
							})
							.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
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

	private void flashInitialize() {
		if (CameraManager.getInstance().hasFlash()) {
			CameraManager.getInstance().setToBackCamera();
			FlashUtil.initializeFlash();
			resourceInitialized();
		} else {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(getString(R.string.no_flash_available)).setCancelable(false)
					.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int id) {
							resourceFailed();
						}
					});
			AlertDialog alert = builder.create();
			alert.show();
		}
	}
}


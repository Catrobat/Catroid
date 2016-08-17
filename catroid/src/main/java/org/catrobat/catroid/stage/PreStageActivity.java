/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2016 The Catrobat Team
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
import android.nfc.NfcAdapter;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.Settings;
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
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.devices.raspberrypi.RaspberryPiService;
import org.catrobat.catroid.drone.DroneInitializer;
import org.catrobat.catroid.drone.DroneServiceWrapper;
import org.catrobat.catroid.drone.JumpingSumoInitializer;
import org.catrobat.catroid.drone.JumpingSumoServiceWrapper;
import org.catrobat.catroid.facedetection.FaceDetectionHandler;
import org.catrobat.catroid.formulaeditor.SensorHandler;
import org.catrobat.catroid.sensing.GatherCollisionInformationTask;
import org.catrobat.catroid.ui.BaseActivity;
import org.catrobat.catroid.ui.SettingsActivity;
import org.catrobat.catroid.ui.dialogs.CustomAlertDialogBuilder;
import org.catrobat.catroid.utils.FlashUtil;
import org.catrobat.catroid.utils.ToastUtil;
import org.catrobat.catroid.utils.TouchUtil;
import org.catrobat.catroid.utils.VibratorUtil;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;

@SuppressWarnings("deprecation")
public class PreStageActivity extends BaseActivity implements GatherCollisionInformationTask.OnPolygonLoadedListener {

	private static final String TAG = PreStageActivity.class.getSimpleName();
	private static final int REQUEST_CONNECT_DEVICE = 1000;
	public static final int REQUEST_RESOURCES_INIT = 101;
	public static final int REQUEST_TEXT_TO_SPEECH = 10;
	public static final int REQUEST_GPS = 1;
	private int requiredResourceCounter;
	private Set<Integer> failedResources;

	private static TextToSpeech textToSpeech;
	private static OnUtteranceCompletedListenerContainer onUtteranceCompletedListenerContainer;

	private DroneInitializer droneInitializer = null;
	private JumpingSumoInitializer jumpingSumoInitializer = null;

	private Intent returnToActivityIntent = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		returnToActivityIntent = new Intent();

		if (isFinishing()) {
			return;
		}

		setContentView(R.layout.activity_prestage);

		TouchUtil.reset();

		int requiredResources = ProjectManager.getInstance().getCurrentProject().getRequiredResources();
		requiredResourceCounter = Integer.bitCount(requiredResources);
		failedResources = new HashSet<>();

		SensorHandler sensorHandler = SensorHandler.getInstance(getApplicationContext());

		if ((requiredResources & Brick.SENSOR_ACCELERATION) > 0) {
			if (sensorHandler.accelerationAvailable()) {
				resourceInitialized();
			} else {
				resourceFailed(Brick.SENSOR_ACCELERATION);
			}
		}

		if ((requiredResources & Brick.SENSOR_INCLINATION) > 0) {
			if (sensorHandler.inclinationAvailable()) {
				resourceInitialized();
			} else {
				resourceFailed(Brick.SENSOR_INCLINATION);
			}
		}

		if ((requiredResources & Brick.SENSOR_COMPASS) > 0) {
			if (sensorHandler.compassAvailable()) {
				resourceInitialized();
			} else {
				resourceFailed(Brick.SENSOR_COMPASS);
			}
		}

		if ((requiredResources & Brick.SENSOR_GPS) > 0) {
			if (SensorHandler.gpsAvailable()) {
				resourceInitialized();
			} else {
				Intent checkIntent = new Intent();
				checkIntent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				startActivityForResult(checkIntent, REQUEST_GPS);
			}
		}

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

		if (JumpingSumoServiceWrapper.checkJumpingSumoAvailability()) {
			CatroidApplication.loadSDKLib();
			if (CatroidApplication.parrotJSLibrariesLoaded) {
				JumpingSumoServiceWrapper.initJumpingSumo(PreStageActivity.this);
			}
		}

		if ((requiredResources & Brick.CAMERA_BACK) > 0) {
			if (CameraManager.getInstance().hasBackCamera()) {
				resourceInitialized();
			} else {
				resourceFailed(Brick.CAMERA_BACK);
			}
		}

		if ((requiredResources & Brick.CAMERA_FRONT) > 0) {
			if (CameraManager.getInstance().hasFrontCamera()) {
				resourceInitialized();
			} else {
				resourceFailed(Brick.CAMERA_FRONT);
			}
		}

		if ((requiredResources & Brick.VIDEO) > 0) {
			if (CameraManager.getInstance().hasFrontCamera()
					|| CameraManager.getInstance().hasBackCamera()) {
				resourceInitialized();
			} else {
				resourceFailed(Brick.VIDEO);
			}
		}

		if ((requiredResources & Brick.CAMERA_FLASH) > 0) {
			flashInitialize();
		}

		if ((requiredResources & Brick.VIBRATOR) > 0) {
			Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
			if (vibrator != null) {
				VibratorUtil.setContext(this.getBaseContext());
				VibratorUtil.activateVibratorThread();
				resourceInitialized();
			} else {
				resourceFailed(Brick.VIBRATOR);
			}
		}

		FaceDetectionHandler.resetFaceDedection();
		if ((requiredResources & Brick.FACE_DETECTION) > 0) {
			boolean success = FaceDetectionHandler.startFaceDetection();
			if (success) {
				resourceInitialized();
			} else {
				resourceFailed(Brick.FACE_DETECTION);
			}
		}
		if ((requiredResources & Brick.NFC_ADAPTER) > 0) {
			if ((requiredResources & Brick.FACE_DETECTION) > 0) {
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setMessage(getString(R.string.nfc_facedetection_support)).setCancelable(false)
						.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int id) {
								nfcInitialize();
							}
						});
				AlertDialog alert = builder.create();
				alert.show();
			} else {
				nfcInitialize();
			}
		}

		if ((requiredResources & Brick.COLLISION) > 0) {
			GatherCollisionInformationTask task = new GatherCollisionInformationTask(this);
			task.execute();
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

	public JumpingSumoInitializer getJumpingSumoInitialiser() {
		if (jumpingSumoInitializer == null) {
			jumpingSumoInitializer = new JumpingSumoInitializer(this);
		}
		return jumpingSumoInitializer;
	}

	@Override
	public void onResume() {
		if (droneInitializer != null) {
			droneInitializer.onPrestageActivityResume();
		}
		super.onResume();
		if (requiredResourceCounter == 0 && failedResources.isEmpty()) {
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

		if (VibratorUtil.isActive()) {
			VibratorUtil.pauseVibrator();
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

	public void showResourceFailedErrorDialog() {
		String failedResourcesMessage = getString(R.string.prestage_resource_not_available_text);
		Iterator resourceIter = failedResources.iterator();
		while (resourceIter.hasNext()) {
			switch ((int) resourceIter.next()) {
				case Brick.SENSOR_ACCELERATION:
					failedResourcesMessage = failedResourcesMessage + getString(R.string
							.prestage_no_acceleration_sensor_available);
					break;
				case Brick.SENSOR_INCLINATION:
					failedResourcesMessage = failedResourcesMessage + getString(R.string
							.prestage_no_inclination_sensor_available);
					break;
				case Brick.SENSOR_COMPASS:
					failedResourcesMessage = failedResourcesMessage + getString(R.string
							.prestage_no_compass_sensor_available);
					break;
				case Brick.SENSOR_GPS:
					failedResourcesMessage = failedResourcesMessage + getString(R.string
							.prestage_no_gps_sensor_available);
					break;
				case Brick.TEXT_TO_SPEECH:
					failedResourcesMessage = failedResourcesMessage + getString(R.string
							.prestage_text_to_speech_error);
					break;
				case Brick.CAMERA_BACK:
					failedResourcesMessage = failedResourcesMessage + getString(R.string
							.prestage_no_back_camera_available);
					break;
				case Brick.CAMERA_FRONT:
					failedResourcesMessage = failedResourcesMessage + getString(R.string
							.prestage_no_front_camera_available);
					break;
				case Brick.CAMERA_FLASH:
					failedResourcesMessage = failedResourcesMessage + getString(R.string
							.prestage_no_flash_available);
					break;
				case Brick.VIBRATOR:
					failedResourcesMessage = failedResourcesMessage + getString(R.string
							.prestage_no_vibrator_available);
					break;
				case Brick.FACE_DETECTION:
					failedResourcesMessage = failedResourcesMessage + getString(R.string
							.prestage_no_camera_available);
					break;
				default:
					failedResourcesMessage = failedResourcesMessage + getString(R.string
							.prestage_default_resource_not_available);
					break;
			}
		}

		AlertDialog.Builder failedResourceAlertBuilder = new AlertDialog.Builder(this);
		failedResourceAlertBuilder.setTitle(R.string.prestage_resource_not_available_title);
		failedResourceAlertBuilder.setMessage(failedResourcesMessage).setCancelable(false)
				.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						resourceFailed();
					}
				});
		AlertDialog alert = failedResourceAlertBuilder.create();
		alert.show();
	}

	public synchronized void resourceFailed(int failedResource) {
		Log.d(TAG, "resourceFailed: " + failedResource);
		failedResources.add(failedResource);
		resourceInitialized();
	}

	public synchronized void resourceInitialized() {
		requiredResourceCounter--;
		if (requiredResourceCounter == 0) {
			if (failedResources.isEmpty()) {
				startStage();
			} else {
				showResourceFailedErrorDialog();
			}
		}
	}

	public void startStage() {
		for (Scene scene : ProjectManager.getInstance().getCurrentProject().getSceneList()) {
			scene.firstStart = true;
			scene.getDataContainer().resetAllDataObjects();
		}
		setResult(RESULT_OK, returnToActivityIntent);
		finish();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

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
								resourceFailed(Brick.TEXT_TO_SPEECH);
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
					builder.setMessage(R.string.prestage_text_to_speech_engine_not_installed).setCancelable(false)
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
			case REQUEST_GPS:
				if (resultCode == RESULT_CANCELED && SensorHandler.gpsAvailable()) {
					resourceInitialized();
				} else {
					resourceFailed(Brick.SENSOR_GPS);
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
		if (CameraManager.getInstance().switchToCameraWithFlash()) {
			FlashUtil.initializeFlash();
			resourceInitialized();
		} else {
			resourceFailed(Brick.CAMERA_FLASH);
		}
	}

	private void nfcInitialize() {
		NfcAdapter adapter = NfcAdapter.getDefaultAdapter(getApplicationContext());
		if (adapter != null && !adapter.isEnabled()) {
			ToastUtil.showError(PreStageActivity.this, R.string.nfc_not_activated);
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
				Intent intent = new Intent(Settings.ACTION_NFC_SETTINGS);
				startActivity(intent);
			} else {
				Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
				startActivity(intent);
			}
		} else if (adapter == null) {
			ToastUtil.showError(PreStageActivity.this, R.string.no_nfc_available);
			// TODO: resourceFailed() & startActivityForResult(), if behaviour needed
		}
		resourceInitialized();
	}

	@Override
	public void onFinished() {
		resourceInitialized();
	}
}

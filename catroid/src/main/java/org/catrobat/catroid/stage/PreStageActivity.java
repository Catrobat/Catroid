/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2018 The Catrobat Team
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

import android.content.Context;
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
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.parrot.arsdk.arcontroller.ARCONTROLLER_DEVICE_STATE_ENUM;
import com.parrot.arsdk.arcontroller.ARControllerException;
import com.parrot.arsdk.arcontroller.ARDeviceController;

import org.catrobat.catroid.BuildConfig;
import org.catrobat.catroid.CatroidApplication;
import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.bluetooth.base.BluetoothDevice;
import org.catrobat.catroid.bluetooth.base.BluetoothDeviceService;
import org.catrobat.catroid.camera.CameraManager;
import org.catrobat.catroid.cast.CastManager;
import org.catrobat.catroid.common.CatroidService;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.ServiceProvider;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.AskSpeechBrick;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.devices.raspberrypi.RaspberryPiService;
import org.catrobat.catroid.drone.ardrone.DroneInitializer;
import org.catrobat.catroid.drone.ardrone.DroneServiceWrapper;
import org.catrobat.catroid.drone.jumpingsumo.JumpingSumoDeviceController;
import org.catrobat.catroid.drone.jumpingsumo.JumpingSumoInitializer;
import org.catrobat.catroid.drone.jumpingsumo.JumpingSumoServiceWrapper;
import org.catrobat.catroid.facedetection.FaceDetectionHandler;
import org.catrobat.catroid.formulaeditor.SensorHandler;
import org.catrobat.catroid.sensing.GatherCollisionInformationTask;
import org.catrobat.catroid.ui.BaseActivity;
import org.catrobat.catroid.ui.recyclerview.dialog.NetworkAlertDialog;
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment;
import org.catrobat.catroid.utils.FlashUtil;
import org.catrobat.catroid.utils.ToastUtil;
import org.catrobat.catroid.utils.TouchUtil;
import org.catrobat.catroid.utils.Utils;
import org.catrobat.catroid.utils.VibratorUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

@SuppressWarnings("deprecation")
public class PreStageActivity extends BaseActivity implements GatherCollisionInformationTask.OnPolygonLoadedListener {

	private static final String TAG = PreStageActivity.class.getSimpleName();
	private static final int REQUEST_CONNECT_DEVICE = 1000;
	public static final int REQUEST_RESOURCES_INIT = 101;
	public static final int REQUEST_GPS = 1;
	private int requiredResourceCounter;
	private Set<Integer> failedResources;

	private static TextToSpeech textToSpeech;
	private static OnUtteranceCompletedListenerContainer onUtteranceCompletedListenerContainer;

	private DroneInitializer droneInitializer = null;
	private JumpingSumoInitializer jumpingSumoInitializer = null;

	private Intent returnToActivityIntent = null;

	private Brick.ResourcesSet requiredResourcesSet;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		returnToActivityIntent = new Intent();

		if (isFinishing()) {
			return;
		}

		setContentView(R.layout.activity_prestage);

		TouchUtil.reset();
		SensorHandler sensorHandler = SensorHandler.getInstance(getApplicationContext());
		failedResources = new HashSet<>();
		requiredResourcesSet = ProjectManager.getInstance().getCurrentProject().getRequiredResources();
		requiredResourceCounter = requiredResourcesSet.size();

		if (requiredResourcesSet.contains(Brick.SENSOR_ACCELERATION)) {
			if (sensorHandler.accelerationAvailable()) {
				resourceInitialized();
			} else {
				resourceFailed(Brick.SENSOR_ACCELERATION);
			}
		}

		if (requiredResourcesSet.contains(Brick.SENSOR_INCLINATION)) {
			if (sensorHandler.inclinationAvailable()) {
				resourceInitialized();
			} else {
				resourceFailed(Brick.SENSOR_INCLINATION);
			}
		}

		if (requiredResourcesSet.contains(Brick.SENSOR_COMPASS)) {
			if (sensorHandler.compassAvailable()) {
				resourceInitialized();
			} else {
				resourceFailed(Brick.SENSOR_COMPASS);
			}
		}

		if (requiredResourcesSet.contains(Brick.SENSOR_GPS)) {
			if (SensorHandler.gpsAvailable()) {
				resourceInitialized();
			} else {
				Intent checkIntent = new Intent();
				checkIntent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				startActivityForResult(checkIntent, REQUEST_GPS);
			}
		}

		if (requiredResourcesSet.contains(Brick.TEXT_TO_SPEECH)) {
			textToSpeech = new TextToSpeech(this, new OnInitListener() {
				@Override
				public void onInit(int status) {
					if (status == TextToSpeech.SUCCESS) {
						onUtteranceCompletedListenerContainer = new OnUtteranceCompletedListenerContainer();
						textToSpeech.setOnUtteranceCompletedListener(onUtteranceCompletedListenerContainer);
						resourceInitialized();
					} else {
						AlertDialog.Builder builder = new AlertDialog.Builder(PreStageActivity.this);
						builder.setMessage(R.string.prestage_text_to_speech_engine_not_installed).setCancelable(false)
								.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int id) {
										Intent installIntent = new Intent();
										installIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
										startActivity(installIntent);
										resourceFailed(Brick.TEXT_TO_SPEECH);
									}
								})
								.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int id) {
										dialog.cancel();
										resourceFailed(Brick.TEXT_TO_SPEECH);
									}
								});
						AlertDialog alert = builder.create();
						alert.show();
					}
				}
			});
		}

		if (requiredResourcesSet.contains(Brick.BLUETOOTH_LEGO_NXT)) {
			connectBTDevice(BluetoothDevice.LEGO_NXT);
		}

		if (requiredResourcesSet.contains(Brick.BLUETOOTH_LEGO_EV3)) {
			connectBTDevice(BluetoothDevice.LEGO_EV3);
		}

		if (requiredResourcesSet.contains(Brick.BLUETOOTH_PHIRO)) {
			connectBTDevice(BluetoothDevice.PHIRO);
		}

		if (requiredResourcesSet.contains(Brick.BLUETOOTH_SENSORS_ARDUINO)) {
			connectBTDevice(BluetoothDevice.ARDUINO);
		}

		if (DroneServiceWrapper.checkARDroneAvailability()) {
			CatroidApplication.loadNativeLibs();
			if (CatroidApplication.parrotLibrariesLoaded) {
				droneInitializer = getDroneInitialiser();
				droneInitializer.initialise();
			}
		}

		if (BuildConfig.FEATURE_PARROT_JUMPING_SUMO_ENABLED && requiredResourcesSet.contains(Brick.JUMPING_SUMO)) {
			CatroidApplication.loadSDKLib();
			if (CatroidApplication.parrotJSLibrariesLoaded) {
				JumpingSumoServiceWrapper.initJumpingSumo(this);
			}
		}

		if (requiredResourcesSet.contains(Brick.CAMERA_BACK)) {
			if (CameraManager.getInstance().hasBackCamera()) {
				resourceInitialized();
			} else {
				resourceFailed(Brick.CAMERA_BACK);
			}
		}

		if (requiredResourcesSet.contains(Brick.CAMERA_FRONT)) {
			if (CameraManager.getInstance().hasFrontCamera()) {
				resourceInitialized();
			} else {
				resourceFailed(Brick.CAMERA_FRONT);
			}
		}

		if (requiredResourcesSet.contains(Brick.VIDEO)) {
			if (CameraManager.getInstance().hasFrontCamera()
					|| CameraManager.getInstance().hasBackCamera()) {
				resourceInitialized();
			} else {
				resourceFailed(Brick.VIDEO);
			}
		}

		if (requiredResourcesSet.contains(Brick.CAMERA_FLASH)) {
			flashInitialize();
		}

		if (requiredResourcesSet.contains(Brick.VIBRATOR)) {
			Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
			if (vibrator != null) {
				VibratorUtil.setContext(this.getBaseContext());
				VibratorUtil.activateVibratorThread();
				resourceInitialized();
			} else {
				resourceFailed(Brick.VIBRATOR);
			}
		}

		if (requiredResourcesSet.contains(Brick.NFC_ADAPTER)) {
			if (requiredResourcesSet.contains(Brick.FACE_DETECTION)) {
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

		FaceDetectionHandler.resetFaceDedection();
		if (requiredResourcesSet.contains(Brick.FACE_DETECTION)) {
			boolean success = FaceDetectionHandler.startFaceDetection();
			if (success) {
				resourceInitialized();
			} else {
				resourceFailed(Brick.FACE_DETECTION);
			}
		}

		if (requiredResourcesSet.contains(Brick.CAST_REQUIRED)) {

			if (CastManager.getInstance().isConnected()) {
				resourceInitialized();
			} else {

				if (!SettingsFragment.isCastSharedPreferenceEnabled(this)) {
					ToastUtil.showError(this, getString(R.string.cast_enable_cast_feature));
				} else if (ProjectManager.getInstance().getCurrentProject().isCastProject()) {
					ToastUtil.showError(this, getString(R.string.cast_error_not_connected_msg));
				} else {
					ToastUtil.showError(this, getString(R.string.cast_error_cast_bricks_in_no_cast_project));
				}
				resourceFailed();
			}
		}

		if (requiredResourcesSet.contains(Brick.COLLISION)) {
			GatherCollisionInformationTask task = new GatherCollisionInformationTask(this);
			task.execute();
		}

		if (requiredResourcesSet.contains(Brick.NETWORK_CONNECTION)) {
			final Context finalBaseContext = this.getBaseContext();
			if (!Utils.isNetworkAvailable(finalBaseContext)) {

				List<Brick> networkBrickList = getNetworkBricks();
				networkBrickList = Utils.distinctListByClassOfObjects(networkBrickList);
				NetworkAlertDialog networkDialog = new NetworkAlertDialog(this, networkBrickList);
				networkDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
					@Override
					public void onCancel(DialogInterface dialogInterface) {
						resourceFailed();
					}
				});
				networkDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
					@Override
					public void onDismiss(DialogInterface dialogInterface) {
						if (Utils.isNetworkAvailable(finalBaseContext)) {
							resourceInitialized();
						} else {
							resourceFailed();
						}
					}
				});
				networkDialog.show();
			} else {
				resourceInitialized();
			}
		}

		if (requiredResourceCounter == 0) {
			startStage();
		}

		if (requiredResourcesSet.contains(Brick.SOCKET_RASPI)) {
			Project currentProject = ProjectManager.getInstance().getCurrentProject();
			RaspberryPiService.getInstance().enableRaspberryInterruptPinsForProject(currentProject);
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
		String host = SettingsFragment.getRaspiHost(this.getBaseContext());
		int port = SettingsFragment.getRaspiPort(this.getBaseContext());

		if (RaspberryPiService.getInstance().connect(host, port)) {
			resourceInitialized();
		} else {
			ToastUtil.showError(this, getString(R.string.error_connecting_to, host, port));
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
			jumpingSumoInitializer = JumpingSumoInitializer.getInstance();
			jumpingSumoInitializer.setPreStageActivity(this);
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
			Log.d(TAG, "onResume()");
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
				case Brick.JUMPING_SUMO:
					failedResourcesMessage = failedResourcesMessage + getString(R.string
							.prestage_no_jumping_sumo_available);
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

	public void showResourceInUseErrorDialog() {
		String failedResourcesMessage = getString(R.string.prestage_resource_in_use_text);
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
				Log.d(TAG, "Start Stage");
				if (BuildConfig.FEATURE_PARROT_JUMPING_SUMO_ENABLED
						&& requiredResourcesSet.contains(Brick.JUMPING_SUMO)
						&& !verifyJSConnection()) {
					return;
				}
				startStage();
			} else {
				showResourceFailedErrorDialog();
			}
		}
	}

	public void startStage() {
		for (Scene scene : ProjectManager.getInstance().getCurrentProject().getSceneList()) {
			scene.firstStart = true;
			scene.getDataContainer().resetUserData();
		}
		setResult(RESULT_OK, returnToActivityIntent);
		finish();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.i(TAG, "requestcode " + requestCode + " result code" + resultCode);

		switch (requestCode) {

			case REQUEST_CONNECT_DEVICE:
				switch (resultCode) {
					case AppCompatActivity.RESULT_OK:
						resourceInitialized();
						break;

					case AppCompatActivity.RESULT_CANCELED:
						resourceFailed();
						break;
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
			ToastUtil.showError(this, R.string.nfc_not_activated);
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
				Intent intent = new Intent(Settings.ACTION_NFC_SETTINGS);
				startActivity(intent);
			} else {
				Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
				startActivity(intent);
			}
		} else if (adapter == null) {
			ToastUtil.showError(this, R.string.no_nfc_available);
			// TODO: resourceFailed() & startActivityForResult(), if behaviour needed
		}
		resourceInitialized();
	}

	private boolean verifyJSConnection() {
		boolean connected;
		ARCONTROLLER_DEVICE_STATE_ENUM state = ARCONTROLLER_DEVICE_STATE_ENUM
				.eARCONTROLLER_DEVICE_STATE_UNKNOWN_ENUM_VALUE;
		try {
			JumpingSumoDeviceController controller = JumpingSumoDeviceController.getInstance();
			ARDeviceController deviceController = controller.getDeviceController();
			state = deviceController.getState();
		} catch (ARControllerException e) {
			Log.e(TAG, "Error could not connect to drone", e);
		}
		if (state != ARCONTROLLER_DEVICE_STATE_ENUM.ARCONTROLLER_DEVICE_STATE_RUNNING) {
			resourceFailed(Brick.JUMPING_SUMO);
			showResourceInUseErrorDialog();
			connected = false;
		} else {
			connected = true;
		}
		return connected;
	}

	private static List<Brick> getNetworkBricks() {
		List<Brick> networkBricksList = new ArrayList<Brick>();

		for (Scene scene : ProjectManager.getInstance().getCurrentProject().getSceneList()) {
			for (Sprite sprite : scene.getSpriteList()) {
				for (Brick brick : sprite.getAllBricks()) {
					if (brick instanceof AskSpeechBrick) {
						networkBricksList.add(brick);
					}
				}
			}
		}
		return networkBricksList;
	}

	@Override
	public void onFinished() {
		resourceInitialized();
	}
}

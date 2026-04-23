/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2022 The Catrobat Team
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
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.nfc.NfcAdapter;
import android.os.Build;
import android.os.Vibrator;
import android.provider.Settings;
import android.util.Log;
import android.view.ContextThemeWrapper;

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
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.devices.mindstorms.MindstormsException;
import org.catrobat.catroid.devices.raspberrypi.RaspberryPiService;
import org.catrobat.catroid.formulaeditor.SensorHandler;
import org.catrobat.catroid.formulaeditor.SensorLoudness;
import org.catrobat.catroid.sensing.GatherCollisionInformationTask;
import org.catrobat.catroid.ui.runtimepermissions.BrickResourcesToRuntimePermissions;
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment;
import org.catrobat.catroid.utils.MobileServiceAvailability;
import org.catrobat.catroid.utils.ToastUtil;
import org.catrobat.catroid.utils.TouchUtil;
import org.catrobat.catroid.utils.Utils;
import org.catrobat.catroid.utils.VibrationManager;

import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import androidx.annotation.VisibleForTesting;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static android.content.Context.VIBRATOR_SERVICE;

import static org.koin.java.KoinJavaComponent.get;

public class StageResourceHolder implements GatherCollisionInformationTask.OnPolygonLoadedListener {
	private static final String TAG = StageResourceHolder.class.getSimpleName();

	private static final int REQUEST_CONNECT_DEVICE = 1000;
	private static final int REQUEST_GPS = 1;

	private Brick.ResourcesSet requiredResourcesSet;
	private int requiredResourceCounter;
	private Set<Integer> failedResources;

	private StageActivity stageActivity;
	private final SpeechRecognitionHolderFactory speechRecognitionHolderFactory = get(SpeechRecognitionHolderFactory.class);

	StageResourceHolder(final StageActivity stageActivity) {
		this.stageActivity = stageActivity;
		TouchUtil.reset();
	}

	@VisibleForTesting(otherwise = VisibleForTesting.PACKAGE_PRIVATE)
	public static List<String> getProjectsRuntimePermissionList() {
		return BrickResourcesToRuntimePermissions.translate(
				ProjectManager.getInstance().getCurrentProject().getRequiredResources());
	}

	public void initResources() {
		failedResources = new HashSet<>();
		requiredResourcesSet = ProjectManager.getInstance().getCurrentProject().getRequiredResources();
		requiredResourceCounter = requiredResourcesSet.size();

		SensorHandler sensorHandler = SensorHandler.getInstance(stageActivity);
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

		if (requiredResourcesSet.contains(Brick.MICROPHONE)) {
			sensorHandler.setSensorLoudness(new SensorLoudness());
			resourceInitialized();
		}

		if (requiredResourcesSet.contains(Brick.SENSOR_GPS)) {
			sensorHandler.setLocationManager((LocationManager) stageActivity.getSystemService(Context.LOCATION_SERVICE));
			if (SensorHandler.gpsAvailable()) {
				resourceInitialized();
			} else {
				Intent checkIntent = new Intent();
				checkIntent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				stageActivity.startActivityForResult(checkIntent, REQUEST_GPS);
			}
		}

		if (requiredResourcesSet.contains(Brick.TEXT_TO_SPEECH)) {
			MobileServiceAvailability mobileServiceAvailability = get(MobileServiceAvailability.class);
			if (mobileServiceAvailability.isGmsAvailable(stageActivity)) {
				TextToSpeechHolder.Companion.getInstance().initTextToSpeech(stageActivity, this);
			} else if (mobileServiceAvailability.isHmsAvailable(stageActivity)) {
				HuaweiTextToSpeechHolder.Companion.getInstance().initTextToSpeech(stageActivity, this);
			}
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

		if (ProjectManager.getInstance().getCurrentProject().hasMultiplayerVariables()) {
			requiredResourceCounter++;
			connectBTDevice(BluetoothDevice.MULTIPLAYER);
		}

		if (requiredResourcesSet.contains(Brick.CAMERA_BACK)) {
			if (getCameraManager().getHasBackCamera()) {
				resourceInitialized();
			} else {
				resourceFailed(Brick.CAMERA_BACK);
			}
		}

		if (requiredResourcesSet.contains(Brick.CAMERA_FRONT)) {
			if (getCameraManager().getHasFrontCamera()) {
				resourceInitialized();
			} else {
				resourceFailed(Brick.CAMERA_FRONT);
			}
		}

		if (requiredResourcesSet.contains(Brick.VIDEO)) {
			if (getCameraManager().getHasFrontCamera() || getCameraManager().getHasBackCamera()) {
				resourceInitialized();
			} else {
				resourceFailed(Brick.VIDEO);
			}
		}

		if (requiredResourcesSet.contains(Brick.CAMERA_FLASH)) {
			if (getCameraManager().getHasFlash()) {
				resourceInitialized();
			} else {
				resourceFailed(Brick.CAMERA_FLASH);
			}
		}

		if (requiredResourcesSet.contains(Brick.VIBRATION)) {
			Vibrator vibration = (Vibrator) stageActivity.getSystemService(VIBRATOR_SERVICE);
			if (vibration != null) {
				getVibrationManager().setVibration(vibration);
				resourceInitialized();
			} else {
				resourceFailed(Brick.VIBRATION);
			}
		}

		if (requiredResourcesSet.contains(Brick.NFC_ADAPTER)) {
			if (requiredResourcesSet.contains(Brick.FACE_DETECTION)) {
				AlertDialog.Builder builder = new AlertDialog.Builder(stageActivity);
				builder.setMessage(stageActivity.getString(R.string.nfc_facedetection_support)).setCancelable(false)
						.setPositiveButton(stageActivity.getString(R.string.ok), new DialogInterface.OnClickListener() {
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

		if (requiredResourcesSet.contains(Brick.FACE_DETECTION)) {
			if (getCameraManager().startDetection()) {
				resourceInitialized();
			} else {
				resourceFailed(Brick.FACE_DETECTION);
			}
		}

		if (requiredResourcesSet.contains(Brick.OBJECT_DETECTION)) {
			if (getCameraManager().startDetection()) {
				resourceInitialized();
			} else {
				resourceFailed(Brick.OBJECT_DETECTION);
			}
		}

		if (requiredResourcesSet.contains(Brick.POSE_DETECTION)) {
			if (getCameraManager().startDetection()) {
				resourceInitialized();
			} else {
				resourceFailed(Brick.POSE_DETECTION);
			}
		}

		if (requiredResourcesSet.contains(Brick.TEXT_DETECTION)) {
			if (getCameraManager().startDetection()) {
				resourceInitialized();
			} else {
				resourceFailed(Brick.TEXT_DETECTION);
			}
		}

		if (requiredResourcesSet.contains(Brick.CAST_REQUIRED)) {
			if (CastManager.getInstance().isConnected()) {
				resourceInitialized();
			} else {
				if (!SettingsFragment.isCastSharedPreferenceEnabled(stageActivity)) {
					ToastUtil.showError(stageActivity, stageActivity.getString(R.string.cast_enable_cast_feature));
				} else if (ProjectManager.getInstance().getCurrentProject().isCastProject()) {
					ToastUtil.showError(stageActivity, stageActivity.getString(R.string.cast_error_not_connected_msg));
				} else {
					ToastUtil.showError(stageActivity, stageActivity.getString(R.string.cast_error_cast_bricks_in_no_cast_project));
				}
				endStageActivity();
			}
		}

		if (requiredResourcesSet.contains(Brick.COLLISION)) {
			GatherCollisionInformationTask task = new GatherCollisionInformationTask(this);
			task.execute();
		}

		if (requiredResourcesSet.contains(Brick.NETWORK_CONNECTION)) {
			if (!Utils.isNetworkAvailable(stageActivity)) {
				new AlertDialog.Builder(new ContextThemeWrapper(stageActivity, R.style.Theme_AppCompat_Dialog))
					.setTitle(R.string.error_no_network_title)
					.setPositiveButton(R.string.preference_title, (dialog, whichButton) -> {
						stageActivity.startActivity(new Intent(Settings.ACTION_SETTINGS));
					})
					.setNegativeButton(R.string.cancel, (dialog, whichButton) -> {
						endStageActivity();
					})
					.setOnDismissListener(dialog -> {
						endStageActivity();
					})
					.create()
					.show();
			} else {
				resourceInitialized();
			}
		}

		if (requiredResourcesSet.contains(Brick.SOCKET_RASPI)) {
			Project currentProject = ProjectManager.getInstance().getCurrentProject();
			RaspberryPiService.getInstance().enableRaspberryInterruptPinsForProject(currentProject);
			connectRaspberrySocket();
		}

		if (requiredResourcesSet.contains(Brick.STORAGE_WRITE)) {
			File directory = Constants.EXTERNAL_STORAGE_ROOT_EXPORT_DIRECTORY;

			if (directory.exists() || directory.mkdirs()) {
				resourceInitialized();
			} else {
				resourceFailed(Brick.STORAGE_WRITE);
			}
		}

		if (requiredResourcesSet.contains(Brick.STORAGE_READ)) {
			resourceInitialized();
		}

		if (requiredResourcesSet.contains(Brick.SPEECH_RECOGNITION)) {
			if (speechRecognitionHolderFactory.isRecognitionAvailable(stageActivity)) {
				resourceInitialized();
			} else {
				resourceFailed(Brick.SPEECH_RECOGNITION);
			}
		}

		if (initFinished()) {
			initFinishedRunStage();
		}
	}

	public boolean initFinished() {
		return requiredResourceCounter == 0 && failedResources.isEmpty();
	}

	public void initFinishedRunStage() {
		try {
			ServiceProvider.getService(CatroidService.BLUETOOTH_DEVICE_SERVICE).initialise();
		} catch (MindstormsException e) {
			Log.e(TAG, e.getMessage());
		}
		stageActivity.setupAskHandler();
		speechRecognitionHolderFactory.getInstance().initSpeechRecognition(stageActivity, this);
		Intent intent = new Intent(stageActivity, getClass());
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
			int flags = PendingIntent.FLAG_IMMUTABLE | Intent.FLAG_ACTIVITY_SINGLE_TOP;
			intent.addFlags(PendingIntent.FLAG_IMMUTABLE | Intent.FLAG_ACTIVITY_SINGLE_TOP);
			stageActivity.pendingIntent = PendingIntent.getActivity(stageActivity, 0, intent,
					flags);
		} else {
			intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			stageActivity.pendingIntent = PendingIntent.getActivity(stageActivity, 0,
					new Intent(stageActivity, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), PendingIntent.FLAG_IMMUTABLE);
		}

		stageActivity.nfcAdapter = NfcAdapter.getDefaultAdapter(stageActivity);
		StageActivity.stageListener.setPaused(false);
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
				initFinishedRunStage();
			} else {
				showResourceFailedErrorDialog();
			}
		}
	}

	private CameraManager getCameraManager() {
		if (stageActivity.cameraManager == null) {
			stageActivity.cameraManager = new CameraManager(stageActivity);
		}
		return stageActivity.cameraManager;
	}

	public VibrationManager getVibrationManager() {
		if (stageActivity.vibrationManager == null) {
			stageActivity.vibrationManager = new VibrationManager();
		}
		return stageActivity.vibrationManager;
	}

	public void endStageActivity() {
		Intent returnToActivityIntent = new Intent();
		stageActivity.setResult(RESULT_CANCELED, returnToActivityIntent);
		stageActivity.finish();
	}

	private void showResourceFailedErrorDialog() {
		StringBuilder failedResourcesMessage = new StringBuilder(stageActivity.getString(R.string.prestage_resource_not_available_text));
		Iterator resourceIter = failedResources.iterator();
		while (resourceIter.hasNext()) {
			switch ((int) resourceIter.next()) {
				case Brick.SENSOR_ACCELERATION:
					failedResourcesMessage.append(stageActivity.getString(R.string
							.prestage_no_acceleration_sensor_available));
					break;
				case Brick.SENSOR_INCLINATION:
					failedResourcesMessage.append(stageActivity.getString(R.string
							.prestage_no_inclination_sensor_available));
					break;
				case Brick.SENSOR_COMPASS:
					failedResourcesMessage.append(stageActivity.getString(R.string
							.prestage_no_compass_sensor_available));
					break;
				case Brick.SENSOR_GPS:
					failedResourcesMessage.append(stageActivity.getString(R.string
							.prestage_no_gps_sensor_available));
					break;
				case Brick.TEXT_TO_SPEECH:
					failedResourcesMessage.append(stageActivity.getString(R.string
							.prestage_text_to_speech_error));
					break;
				case Brick.CAMERA_BACK:
					failedResourcesMessage.append(stageActivity.getString(R.string
							.prestage_no_back_camera_available));
					break;
				case Brick.CAMERA_FRONT:
					failedResourcesMessage.append(stageActivity.getString(R.string
							.prestage_no_front_camera_available));
					break;
				case Brick.VIDEO:
					failedResourcesMessage.append(stageActivity.getString(R.string
							.prestage_no_camera_available));
					break;
				case Brick.CAMERA_FLASH:
					failedResourcesMessage.append(stageActivity.getString(R.string
							.prestage_no_flash_available));
					break;
				case Brick.VIBRATION:
					failedResourcesMessage.append(stageActivity.getString(R.string
							.prestage_no_vibration_available));
					break;
				case Brick.FACE_DETECTION:
					failedResourcesMessage.append(stageActivity.getString(R.string
							.prestage_no_face_detection_available));
					break;
				case Brick.OBJECT_DETECTION:
					failedResourcesMessage.append(stageActivity.getString(R.string
							.prestage_no_object_detection_available));
					break;
				case Brick.SPEECH_RECOGNITION:
					failedResourcesMessage.append(stageActivity.getString(R.string
							.speech_recognition_not_available));
					break;
				case Brick.TEXT_DETECTION:
					failedResourcesMessage.append(stageActivity.getString(R.string
							.prestage_no_text_detection_available));
					break;
				case Brick.POSE_DETECTION:
					failedResourcesMessage.append(stageActivity.getString(R.string
							.prestage_no_pose_detection_available));
					break;
				default:
					failedResourcesMessage.append(stageActivity.getString(R.string
							.prestage_default_resource_not_available));
					break;
			}
		}

		new AlertDialog.Builder(new ContextThemeWrapper(stageActivity, R.style.Theme_AppCompat_Dialog))
				.setTitle(R.string.prestage_resource_not_available_title)
				.setMessage(failedResourcesMessage).setCancelable(false)
				.setPositiveButton(stageActivity.getString(R.string.ok), (dialog, id) -> endStageActivity())
				.create()
				.show();
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
			case REQUEST_CONNECT_DEVICE:
				switch (resultCode) {
					case RESULT_OK:
						resourceInitialized();
						break;

					case RESULT_CANCELED:
						endStageActivity();
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
				endStageActivity();
				break;
		}
	}

	private void connectBTDevice(Class<? extends BluetoothDevice> service) {
		BluetoothDeviceService btService = ServiceProvider.getService(CatroidService.BLUETOOTH_DEVICE_SERVICE);

		if (btService.connectDevice(service, stageActivity, REQUEST_CONNECT_DEVICE)
				== BluetoothDeviceService.ConnectDeviceResult.ALREADY_CONNECTED) {
			resourceInitialized();
		}
	}

	private void connectRaspberrySocket() {
		String host = SettingsFragment.getRaspiHost(stageActivity);
		int port = SettingsFragment.getRaspiPort(stageActivity);

		if (RaspberryPiService.getInstance().connect(host, port)) {
			resourceInitialized();
		} else {
			ToastUtil.showError(stageActivity, stageActivity.getString(R.string.error_connecting_to, host, port));
			endStageActivity();
		}
	}

	private void nfcInitialize() {
		NfcAdapter adapter = NfcAdapter.getDefaultAdapter(stageActivity);
		if (adapter != null && !adapter.isEnabled()) {
			ToastUtil.showError(stageActivity, R.string.nfc_not_activated);
			Intent intent = new Intent(Settings.ACTION_NFC_SETTINGS);
			stageActivity.startActivity(intent);
		} else if (adapter == null) {
			ToastUtil.showError(stageActivity, R.string.no_nfc_available);
			// TODO: resourceFailed() & startActivityForResult(), if behaviour needed
		}
		resourceInitialized();
	}

	// for GatherCollisionInformationTask.OnPolygonLoadedListener, this is NOT any Activity or Lifecycle event
	@Override
	public void onFinished() {
		resourceInitialized();
	}
}

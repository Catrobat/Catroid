/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2017 The Catrobat Team
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
package org.catrobat.catroid.drone.jumpingsumo;

import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;

import com.parrot.arsdk.arcontroller.ARCONTROLLER_DEVICE_STATE_ENUM;
import com.parrot.arsdk.arcontroller.ARCONTROLLER_DICTIONARY_KEY_ENUM;
import com.parrot.arsdk.arcontroller.ARCONTROLLER_ERROR_ENUM;
import com.parrot.arsdk.arcontroller.ARControllerArgumentDictionary;
import com.parrot.arsdk.arcontroller.ARControllerCodec;
import com.parrot.arsdk.arcontroller.ARControllerDictionary;
import com.parrot.arsdk.arcontroller.ARControllerException;
import com.parrot.arsdk.arcontroller.ARDeviceController;
import com.parrot.arsdk.arcontroller.ARDeviceControllerListener;
import com.parrot.arsdk.arcontroller.ARDeviceControllerStreamListener;
import com.parrot.arsdk.arcontroller.ARFeatureCommon;
import com.parrot.arsdk.arcontroller.ARFrame;
import com.parrot.arsdk.ardiscovery.ARDISCOVERY_PRODUCT_ENUM;
import com.parrot.arsdk.ardiscovery.ARDiscoveryDevice;
import com.parrot.arsdk.ardiscovery.ARDiscoveryDeviceNetService;
import com.parrot.arsdk.ardiscovery.ARDiscoveryDeviceService;
import com.parrot.arsdk.ardiscovery.ARDiscoveryException;

import org.catrobat.catroid.CatroidApplication;
import org.catrobat.catroid.R;
import org.catrobat.catroid.stage.PreStageActivity;
import org.catrobat.catroid.ui.dialogs.CustomAlertDialogBuilder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.catrobat.catroid.CatroidApplication.getAppContext;

public class JumpingSumoInitializer {

	private static final List<ARDiscoveryDeviceService> DRONELIST = new ArrayList<>();
	public JumpingSumoDiscoverer jsDiscoverer;

	private ARDeviceController deviceController;
	private static JumpingSumoInitializer instance;

	private final Handler handler = new Handler(getAppContext().getMainLooper());

	private static final String TAG = JumpingSumoInitializer.class.getSimpleName();

	private PreStageActivity prestageStageActivity;
	private StageActivity stageActivity = null;
	private ARCONTROLLER_DEVICE_STATE_ENUM deviceState = ARCONTROLLER_DEVICE_STATE_ENUM.ARCONTROLLER_DEVICE_STATE_STOPPED;

	private static final int JUMPING_SUMO_BATTERY_THRESHOLD = 3;
	private static final int CONNECTION_TIME = 10000;
	private static int jumpingSumoCount = 0;
	private boolean messageShown = false;

	public JumpingSumoInitializer() {
	}

	public static JumpingSumoInitializer getInstance() {
		if (instance == null) {
			instance = new JumpingSumoInitializer();
		}
		return instance;
	}

	public void setPreStageActivity(PreStageActivity prestageStageActivity) {
		this.prestageStageActivity = prestageStageActivity;
	}

	public boolean disconnect() {
		boolean success = false;
		jsDiscoverer.removeListener(discovererListener);
		if (deviceController != null) {
			ARCONTROLLER_ERROR_ENUM error = deviceController.stop();
			if (error == ARCONTROLLER_ERROR_ENUM.ARCONTROLLER_OK) {
				success = true;
				JumpingSumoDeviceController controller = JumpingSumoDeviceController.getInstance();
				controller.setDeviceController(null);
			}
		}
		return success;
	}

	public void initialise() {
		jsDiscoverer = new JumpingSumoDiscoverer();
		if (checkRequirements()) {
			jsDiscoverer.setup();
			jsDiscoverer.addListener(discovererListener);
			jsDiscoverer.addListenerPicture(pictureListener);
		}
	}

	public void checkJumpingSumoAvailability(PreStageActivity prestageStageActivityNow) {
		setPreStageActivity(prestageStageActivityNow);
		Log.d(TAG, "JumpSumo Count: " + jumpingSumoCount);

		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			public void run() {
				if (jumpingSumoCount == 0) {
					showUnCancellableErrorDialog(prestageStageActivity,
							prestageStageActivity.getString(R.string.error_no_jumpingsumo_connected_title),
							prestageStageActivity.getString(R.string.error_no_jumpingsumo_connected));
				} else {
					prestageStageActivity.resourceInitialized();
				}
			}
		}, CONNECTION_TIME);
	}

	private void notifyConfigureDecoder(ARControllerCodec codec) {
		//configure codec?
		Log.d(TAG, "Codec " + codec.getType());
	}

	public void setStageActivity(StageActivity stageActivity) {
		this.stageActivity = stageActivity;
	}

	private void notifyBatteryChanged(int battery) {
		Log.d(TAG, "Jumping Sumo Battery: " + battery);

		if (battery < JUMPING_SUMO_BATTERY_THRESHOLD && !messageShown) {
			messageShown = true;
			if (stageActivity instanceof StageActivity && !(stageActivity == null)) {
				showUnCancellableErrorDialog(stageActivity,
						stageActivity.getString(R.string.error_jumpingsumo_battery_title),
						stageActivity.getString(R.string.error_jumpingsumo_battery));
				Log.e(TAG, "Jumping Sumo Battery too low");
			} else {
				checkJumpingSumoAvailability(prestageStageActivity);
				Log.e(TAG, "Jumping Sumo Battery too low");
			}
		}
	}

	private final JumpingSumoDiscoverer.Listener discovererListener = new JumpingSumoDiscoverer.Listener() {

		@Override
		public void onDronesListUpdated(List<ARDiscoveryDeviceService> dronesList) {
			JumpingSumoInitializer.DRONELIST.clear();
			JumpingSumoInitializer.DRONELIST.addAll(dronesList);
			Log.d(TAG, "JumpingSumo: " + dronesList.size() + " Drones found");
			jumpingSumoCount = dronesList.size();
			if (jumpingSumoCount > 0) {
				ARDiscoveryDeviceService service = dronesList.get(0);
				ARDiscoveryDevice discoveryDevice = createDiscoveryDevice(service, ARDISCOVERY_PRODUCT_ENUM.ARDISCOVERY_PRODUCT_JS);
				deviceController = createDeviceController(discoveryDevice);
				ARCONTROLLER_DEVICE_STATE_ENUM state = ARCONTROLLER_DEVICE_STATE_ENUM
						.eARCONTROLLER_DEVICE_STATE_UNKNOWN_ENUM_VALUE;
				ARCONTROLLER_ERROR_ENUM error = deviceController.start();
				try {
					state = deviceController.getState();
				} catch (ARControllerException e) {
					Log.e(TAG, "Exception " + e);
				}
				if (error != ARCONTROLLER_ERROR_ENUM.ARCONTROLLER_OK) {
					Log.e(TAG, "Exception " + error);
					Log.d(TAG, "State: " + state);
				}

				JumpingSumoDeviceController controller = JumpingSumoDeviceController.getInstance();
				controller.setDeviceController(deviceController);
				jsDiscoverer.getInfoDevice(service);
			}
		}
	};

	public final JumpingSumoDiscoverer.ListenerPicture pictureListener = new JumpingSumoDiscoverer.ListenerPicture() {
		@Override
		public void onPictureCount(int pictureCount) {
		}

		@Override
		public void onMatchingMediasFound(int matchingMedias) {
		}
		@Override
		public void onDownloadProgressed(String mediaName, int progress) {
		}

		@Override
		public void onDownloadComplete(String mediaName) {
			Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
			File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/JumpingSumo/" +
					mediaName);
			Uri contentUri = Uri.fromFile(file);
			mediaScanIntent.setData(contentUri);
			getAppContext().sendBroadcast(mediaScanIntent);
		}
	};

	private ARDeviceController createDeviceController(@NonNull ARDiscoveryDevice discoveryDevice) {
		ARDeviceController deviceController = null;
		try {
			deviceController = new ARDeviceController(discoveryDevice);

			deviceController.addListener(deviceControllerListener);
			deviceController.addStreamListener(streamListener);
		} catch (ARControllerException e) {
			Log.e(TAG, "Exception", e);
		}

		return deviceController;
	}

	private ARDiscoveryDevice createDiscoveryDevice(@NonNull ARDiscoveryDeviceService service, ARDISCOVERY_PRODUCT_ENUM productType) {
		ARDiscoveryDevice device = null;
		try {
			device = new ARDiscoveryDevice();

			ARDiscoveryDeviceNetService netDeviceService = (ARDiscoveryDeviceNetService) service.getDevice();
			device.initWifi(productType, netDeviceService.getName(), netDeviceService.getIp(), netDeviceService.getPort());
		} catch (ARDiscoveryException e) {
			Log.e(TAG, "Exception", e);
			Log.e(TAG, "Error: " + e.getError());
		}
		return device;
	}

	public boolean checkRequirements() {

		if (!CatroidApplication.loadSDKLib()) {
			showUnCancellableErrorDialog(prestageStageActivity,
					prestageStageActivity.getString(R.string.error_jumpingsumo_wrong_platform_title),
					prestageStageActivity.getString(R.string.error_jumpingsumo_wrong_platform));
			return false;
		}

		return true;
	}

	private void onConnectionLost(final StageActivity context) {
		if (stageActivity instanceof StageActivity && !(stageActivity == null)) {
			context.jsDestroy();
		}
	}

	public static void showUnCancellableErrorDialog(final StageActivity context, String title, String message) {
		Builder builder = new CustomAlertDialogBuilder(context);

		builder.setTitle(title);
		builder.setCancelable(false);
		builder.setMessage(message);
		builder.setNeutralButton(R.string.close, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				context.jumpingSumoDisconnect();
				context.jsDestroy();
			}
		});
		builder.show();
	}

	public void takePicture() {
		if (deviceController != null) {
			// JumpingSumo (not evo) are still using old deprecated command
			deviceController.getFeatureJumpingSumo().sendMediaRecordPicture((byte) 0);
		}
	}

	public void getLastFlightMedias() {
		jsDiscoverer.notifyPic();
		jsDiscoverer.download();
	}

	public static void showUnCancellableErrorDialog(final PreStageActivity context, String title, String message) {
		Builder builder = new CustomAlertDialogBuilder(context);

		builder.setTitle(title);
		builder.setCancelable(false);
		builder.setMessage(message);
		builder.setNeutralButton(R.string.close, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				context.resourceFailed();
			}
		});
		builder.show();
	}

	private final ARDeviceControllerListener deviceControllerListener = new ARDeviceControllerListener() {
		@Override
		public void onStateChanged(ARDeviceController deviceController, ARCONTROLLER_DEVICE_STATE_ENUM newState, ARCONTROLLER_ERROR_ENUM error) {
			deviceState = newState;
			if (deviceState.equals(ARCONTROLLER_DEVICE_STATE_ENUM.ARCONTROLLER_DEVICE_STATE_RUNNING)) {
				jsDiscoverer.removeListener(discovererListener);
			} else if (deviceState.equals(ARCONTROLLER_DEVICE_STATE_ENUM.ARCONTROLLER_DEVICE_STATE_STOPPED)) {
				Log.e(TAG, "Jumping Sumo Connection Lost");
				onConnectionLost(stageActivity);
			}
		}

		@Override
		public void onExtensionStateChanged(ARDeviceController deviceController, ARCONTROLLER_DEVICE_STATE_ENUM newState, ARDISCOVERY_PRODUCT_ENUM product, String name, ARCONTROLLER_ERROR_ENUM error) {
		}

		@Override
		public void onCommandReceived(ARDeviceController deviceController, ARCONTROLLER_DICTIONARY_KEY_ENUM commandKey, ARControllerDictionary elementDictionary) {
			// if event received is the battery update

			if ((commandKey == ARCONTROLLER_DICTIONARY_KEY_ENUM.ARCONTROLLER_DICTIONARY_KEY_COMMON_COMMONSTATE_BATTERYSTATECHANGED) && (elementDictionary != null)) {
				ARControllerArgumentDictionary<Object> args = elementDictionary.get(ARControllerDictionary.ARCONTROLLER_DICTIONARY_SINGLE_KEY);
				if (args != null) {
					final int battery = (Integer) args.get(ARFeatureCommon.ARCONTROLLER_DICTIONARY_KEY_COMMON_COMMONSTATE_BATTERYSTATECHANGED_PERCENT);
					handler.post(new Runnable() {
						@Override
						public void run() {
							notifyBatteryChanged(battery);
						}
					});
				}
			}
		}
	};

	private final ARDeviceControllerStreamListener streamListener = new ARDeviceControllerStreamListener() {
		@Override
		public ARCONTROLLER_ERROR_ENUM configureDecoder(ARDeviceController deviceController, final ARControllerCodec codec) {
			notifyConfigureDecoder(codec);
			return ARCONTROLLER_ERROR_ENUM.ARCONTROLLER_OK;
		}

		@Override
		public ARCONTROLLER_ERROR_ENUM onFrameReceived(ARDeviceController deviceController, final ARFrame frame) {
			return ARCONTROLLER_ERROR_ENUM.ARCONTROLLER_OK;
		}

		@Override
		public void onFrameTimeout(ARDeviceController deviceController) {
		}
	};
}

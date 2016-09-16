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
package org.catrobat.catroid.drone.jumpingsumo;

import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Handler;
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

import java.util.ArrayList;
import java.util.List;

import static org.catrobat.catroid.CatroidApplication.getAppContext;

public class JumpingSumoInitializer {

	private static final List<ARDiscoveryDeviceService> DRONELIST = new ArrayList<>();
	public JumpingSumoDiscoverer jsDiscoverer;

	private ARDeviceController deviceController;

	private final Handler handler = new Handler(getAppContext().getMainLooper());

	private final List<Listener> listeners = new ArrayList<>();

	private static final String TAG = JumpingSumoInitializer.class.getSimpleName();

	private PreStageActivity prestageStageActivity;
	private ARCONTROLLER_DEVICE_STATE_ENUM deviceState = ARCONTROLLER_DEVICE_STATE_ENUM.ARCONTROLLER_DEVICE_STATE_STOPPED;

	private static final int JUMPING_SUMO_BATTERY_THRESHOLD = 2;
	private static final int CONNECTION_TIME = 4000;
	private static int jumpingSumoCount = 0;

	public interface Listener {
		/**
		 * Called when the battery charge changes
		 * Called in the main thread
		 * @param batteryPercentage the battery remaining (in percent)
		 */
		void onBatteryChargeChanged(int batteryPercentage);

		/**
		 * Called when the video decoder should be configured
		 * Called on a separate thread
		 * @param codec the codec to configure the decoder with
		 */
		void configureDecoder(ARControllerCodec codec);

		/**
		 * Called when a video frame has been received
		 * Called on a separate thread
		 * @param frame the video frame
		 */
		void onFrameReceived(ARFrame frame);

		/**
		 * Called before medias will be downloaded
		 * Called in the main thread
		 * @param nbMedias the number of medias that will be downloaded
		 */
	}

	public JumpingSumoInitializer(PreStageActivity prestageStageActivity) {
		this.prestageStageActivity = prestageStageActivity;
	}

	private  void setPreStageActivity(PreStageActivity prestageStageActivity) {
		this.prestageStageActivity = prestageStageActivity;
	}

	public boolean disconnect() {
		boolean success = false;
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
		jsDiscoverer = new JumpingSumoDiscoverer(getAppContext());
		if (checkRequirements()) {
			jsDiscoverer.setup();
			jsDiscoverer.addListener(discovererListener);
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
		List<Listener> listenersCpy = new ArrayList<>(listeners);
		for (Listener listener : listenersCpy) {
			listener.configureDecoder(codec);
		}
	}

	private void notifyBatteryChanged(int battery) {
		List<Listener> listenersCpy = new ArrayList<>(listeners);
		for (Listener listener : listenersCpy) {
			listener.onBatteryChargeChanged(battery);
		}
		Log.d(TAG, "Jumping Sumo Battery: " + battery);
		JumpingSumoDataContainer batteryStatus = JumpingSumoDataContainer.getInstance();

		Object value = prestageStageActivity.getString(R.string.user_variable_name_battery_status) + " " + battery;
		batteryStatus.setBatteryStatus(value);
		if (battery < JUMPING_SUMO_BATTERY_THRESHOLD) {
			disconnect();
			showUnCancellableErrorDialog(prestageStageActivity,
					prestageStageActivity.getString(R.string.error_jumpingsumo_battery_title),
					prestageStageActivity.getString(R.string.error_jumpingsumo_battery));
			Log.e(TAG, "Jumping Sumo Battery too low");
		}
	}

	private void notifyFrameReceived(ARFrame frame) {
		List<Listener> listenersCpy = new ArrayList<>(listeners);
		for (Listener listener : listenersCpy) {
			listener.onFrameReceived(frame);
		}
	}

	private final JumpingSumoDiscoverer.Listener discovererListener = new  JumpingSumoDiscoverer.Listener() {

		@Override
		public void onDronesListUpdated(List<ARDiscoveryDeviceService> dronesList) {
			JumpingSumoInitializer.DRONELIST.clear();
			JumpingSumoInitializer.DRONELIST.addAll(dronesList);
			Log.d(TAG, "JumpingSumo: " + dronesList.size() + " Drones found");
			jumpingSumoCount = dronesList.size();
			if (jumpingSumoCount > 0) {
				Log.i(TAG, "The Name of the first JumpingSumo is: " + dronesList.get(0));
				ARDiscoveryDeviceService service = dronesList.get(0);
				ARDiscoveryDevice discoveryDevice = createDiscoveryDevice(service, ARDISCOVERY_PRODUCT_ENUM.ARDISCOVERY_PRODUCT_JS);
				deviceController = createDeviceController(discoveryDevice);

				ARCONTROLLER_ERROR_ENUM error = deviceController.start();
				if (error != ARCONTROLLER_ERROR_ENUM.ARCONTROLLER_OK) {
					Log.e(TAG, "Exception" + error);
				}

				JumpingSumoDeviceController controller = JumpingSumoDeviceController.getInstance();
				controller.setDeviceController(deviceController);
			}
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

			if ((deviceState.equals(ARCONTROLLER_DEVICE_STATE_ENUM.ARCONTROLLER_DEVICE_STATE_RUNNING))) {
				jsDiscoverer.removeListener(discovererListener);
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
			notifyFrameReceived(frame);
			return ARCONTROLLER_ERROR_ENUM.ARCONTROLLER_OK;
		}

		@Override
		public void onFrameTimeout(ARDeviceController deviceController) {
		}
	};
}

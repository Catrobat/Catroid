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
package org.catrobat.catroid.drone;

import android.app.AlertDialog.Builder;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
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
import com.parrot.arsdk.ardiscovery.ARDiscoveryService;
import com.parrot.freeflight.service.DroneControlService;

import org.catrobat.catroid.CatroidApplication;
import org.catrobat.catroid.R;
import org.catrobat.catroid.stage.PreStageActivity;
import org.catrobat.catroid.ui.dialogs.CustomAlertDialogBuilder;
import org.catrobat.catroid.ui.dialogs.TermsOfUseDialogFragment;

import java.util.ArrayList;
import java.util.List;

import static org.catrobat.catroid.CatroidApplication.getAppContext;


public class JumpingSumoInitializer {


	private static DroneControlService droneControlService = null;

	private static final List<ARDiscoveryDeviceService> DRONELIST = new ArrayList<>();
	public JumpingSumoDiscoverer jsDiscoverer;

	private ARDeviceController deviceController;

	private final Handler handler = new Handler(getAppContext().getMainLooper());

	private final List<Listener> listeners = new ArrayList<>();

	private static final String TAG = JumpingSumoInitializer.class.getSimpleName();

	private PreStageActivity prestageStageActivity;
	private ARCONTROLLER_DEVICE_STATE_ENUM mstate = ARCONTROLLER_DEVICE_STATE_ENUM.ARCONTROLLER_DEVICE_STATE_STOPPED;

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

	private void showTermsOfUseDialog() {
		Bundle args = new Bundle();
		args.putBoolean(TermsOfUseDialogFragment.DIALOG_ARGUMENT_TERMS_OF_USE_ACCEPT, true);
		TermsOfUseDialogFragment termsOfUseDialog = new TermsOfUseDialogFragment();
		termsOfUseDialog.setArguments(args);
		termsOfUseDialog.show(prestageStageActivity.getFragmentManager(),
				TermsOfUseDialogFragment.DIALOG_FRAGMENT_TAG);
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

		Log.d(TAG, "Jumping Sumo init start");
		//if (SettingsActivity.areTermsOfServiceAgreedPermanently(prestageStageActivity.getApplicationContext())) {
		jsDiscoverer = new JumpingSumoDiscoverer(getAppContext());

		if (checkRequirements()) {
			//checkDroneConnectivity();
			jsDiscoverer.setup();
			jsDiscoverer.addListener(discovererListener);
			Log.d(TAG, "Jumping Sumo jsDiscoverer started!!!");
		}
		//} else {
		//	showTermsOfUseDialog();
		//}
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
		//TODO TGr 3???
		if (battery < 3) {
			disconnect();
			JumpingSumoDeviceController controller = JumpingSumoDeviceController.getInstance();
			controller.setDeviceController(null);
			Log.i(TAG, "Jumping Sumo Battery too low");
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
			if (dronesList.size() > 0) {
				Log.i(TAG, "The Name of the first JumpingSumo is: " + dronesList.get(0));

				ARDiscoveryDeviceService service = dronesList.get(0);
				Log.d(TAG, "Jumping Sumo service name is: " + service.getName());
				Log.d(TAG, "Jumping Sumo service ProductID is: " + service.getProductID());
				ARDISCOVERY_PRODUCT_ENUM product = ARDiscoveryService.getProductFromProductID(service.getProductID());
				Log.d(TAG, "Jumping Sumo product name is: " + product.name());

				ARDiscoveryDevice discoveryDevice = createDiscoveryDevice(service, ARDISCOVERY_PRODUCT_ENUM.ARDISCOVERY_PRODUCT_JS);
				deviceController = createDeviceController(discoveryDevice);

				ARCONTROLLER_ERROR_ENUM error = deviceController.start();
				Log.d(TAG, "arccontroller error: " + error);

				JumpingSumoDeviceController controller = JumpingSumoDeviceController.getInstance();
				controller.setDeviceController(deviceController);

				Log.d(TAG, "mstate: " + mstate);
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







	private void onDroneServiceConnected(IBinder service) {
		Log.d(TAG, "onDroneServiceConnected");
		droneControlService = ((DroneControlService.LocalBinder) service).getService();
		DroneServiceWrapper.getInstance().setDroneService(droneControlService);
		droneControlService.resume();
		droneControlService.requestDroneStatus();
	}

	private ServiceConnection droneServiceConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			onDroneServiceConnected(service);
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			droneControlService = null;
			DroneServiceWrapper.getInstance().setDroneService(droneControlService);
		}
	};


	public void onPrestageActivityDestroy() {
		if (droneControlService != null) {
			prestageStageActivity.unbindService(this.droneServiceConnection);
			droneControlService = null;
		}
	}


	private final ARDeviceControllerListener deviceControllerListener = new ARDeviceControllerListener() {
		@Override
		public void onStateChanged(ARDeviceController deviceController, ARCONTROLLER_DEVICE_STATE_ENUM newState, ARCONTROLLER_ERROR_ENUM error) {
			Log.i(TAG, "new State is " + newState);
			mstate = newState;

			if ((mstate.equals(ARCONTROLLER_DEVICE_STATE_ENUM.ARCONTROLLER_DEVICE_STATE_RUNNING))) {
				jsDiscoverer.removeListener(discovererListener);
			}

/*
			if((deviceController != null) && (mstate.equals(ARCONTROLLER_DEVICE_STATE_ENUM.ARCONTROLLER_DEVICE_STATE_RUNNING))){
				deviceController.getFeatureJumpingSumo().sendPilotingPosture(ARCOMMANDS_JUMPINGSUMO_PILOTING_POSTURE_TYPE_ENUM.ARCOMMANDS_JUMPINGSUMO_PILOTING_POSTURE_TYPE_KICKER);
				Log.i(TAG, "State Changer: Jumping Sumo Command send");
			}
*/
		}


		@Override
		public void onExtensionStateChanged(ARDeviceController deviceController, ARCONTROLLER_DEVICE_STATE_ENUM newState, ARDISCOVERY_PRODUCT_ENUM product, String name, ARCONTROLLER_ERROR_ENUM error) {

		}

		@Override
		public void onCommandReceived(ARDeviceController deviceController, ARCONTROLLER_DICTIONARY_KEY_ENUM commandKey, ARControllerDictionary elementDictionary) {
			// if event received is the battery update

			if ((commandKey == ARCONTROLLER_DICTIONARY_KEY_ENUM.ARCONTROLLER_DICTIONARY_KEY_COMMON_COMMONSTATE_BATTERYSTATECHANGED) && (elementDictionary != null)) {
				Log.i(TAG, "commandKey battery");
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

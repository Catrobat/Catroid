/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2015 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
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
package org.catrobat.catroid.drone;

import android.annotation.SuppressLint;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.parrot.freeflight.receivers.DroneAvailabilityDelegate;
import com.parrot.freeflight.receivers.DroneAvailabilityReceiver;
import com.parrot.freeflight.receivers.DroneConnectionChangeReceiverDelegate;
import com.parrot.freeflight.receivers.DroneConnectionChangedReceiver;
import com.parrot.freeflight.receivers.DroneReadyReceiver;
import com.parrot.freeflight.receivers.DroneReadyReceiverDelegate;
import com.parrot.freeflight.service.DroneControlService;
import com.parrot.freeflight.service.intents.DroneStateManager;
import com.parrot.freeflight.tasks.CheckDroneNetworkAvailabilityTask;

import org.catrobat.catroid.CatroidApplication;
import org.catrobat.catroid.R;
import org.catrobat.catroid.stage.PreStageActivity;
import org.catrobat.catroid.ui.SettingsActivity;
import org.catrobat.catroid.ui.dialogs.CustomAlertDialogBuilder;
import org.catrobat.catroid.ui.dialogs.TermsOfUseDialogFragment;

public class DroneInitializer implements DroneReadyReceiverDelegate, DroneConnectionChangeReceiverDelegate,
		DroneAvailabilityDelegate {

	public static final String INIT_DRONE_STRING_EXTRA = "STRING_EXTRA_INIT_DRONE";
	public static final int DRONE_BATTERY_THRESHOLD = 10;


	private static DroneControlService droneControlService = null;
	private BroadcastReceiver droneReadyReceiver = null;
	private BroadcastReceiver droneStateReceiver = null;
	private CheckDroneNetworkAvailabilityTask checkDroneConnectionTask;
	private DroneConnectionChangedReceiver droneConnectionChangeReceiver;

	private static final String TAG = DroneInitializer.class.getSimpleName();

	private PreStageActivity prestageStageActivity;
	private Intent returnToActivityIntent = null;

	public DroneInitializer(PreStageActivity prestageStageActivity, Intent returnToActivityIntent) {
		this.prestageStageActivity = prestageStageActivity;
		this.returnToActivityIntent = returnToActivityIntent;
	}

	private void showTermsOfUseDialog() {
		Bundle args = new Bundle();
		args.putBoolean(TermsOfUseDialogFragment.DIALOG_ARGUMENT_TERMS_OF_USE_ACCEPT, true);
		TermsOfUseDialogFragment termsOfUseDialog = new TermsOfUseDialogFragment();
		termsOfUseDialog.setArguments(args);
		termsOfUseDialog.show(prestageStageActivity.getSupportFragmentManager(),
				TermsOfUseDialogFragment.DIALOG_FRAGMENT_TAG);
	}

	public void initialise() {
		if (SettingsActivity.areTermsOfServiceAgreedPermanently(prestageStageActivity.getApplicationContext())) {

			if (checkRequirements()) {
				checkDroneConnectivity();
			}
		} else {
			showTermsOfUseDialog();
		}
	}

	public boolean checkRequirements() {
		if (!CatroidApplication.OS_ARCH.startsWith("arm")) {
			showUnCancellableErrorDialog(prestageStageActivity,
					prestageStageActivity.getString(R.string.error_drone_wrong_platform_title),
					prestageStageActivity.getString(R.string.error_drone_wrong_platform));
			return false;
		}

		if (!CatroidApplication.loadNativeLibs()) {
			showUnCancellableErrorDialog(prestageStageActivity,
					prestageStageActivity.getString(R.string.error_drone_wrong_platform_title),
					prestageStageActivity.getString(R.string.error_drone_wrong_platform));
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

	public static void addDroneSupportExtraToNewIntentIfPresentInOldIntent(Intent oldIntent, Intent newIntent) {
		if (newIntent == null || oldIntent == null) {
			return;
		}

		Boolean isDroneRequired = oldIntent.getBooleanExtra(INIT_DRONE_STRING_EXTRA, false);
		newIntent.putExtra(INIT_DRONE_STRING_EXTRA, isDroneRequired);
	}

	@Override
	public void onDroneReady() {
		Log.d(TAG, "onDroneReady -> check battery -> go to stage");
		int droneBatteryCharge = droneControlService.getDroneNavData().batteryStatus;
		if(droneControlService != null) {
			if (droneBatteryCharge < DRONE_BATTERY_THRESHOLD) {
				String dialogTitle = String.format(prestageStageActivity.getString(R.string.error_drone_low_battery_title),
						droneBatteryCharge);
				showUnCancellableErrorDialog(prestageStageActivity, dialogTitle,
						prestageStageActivity.getString(R.string.error_drone_low_battery));
				return;
			}

			// test navdata getter
			Log.d("navdata", String.format("BatterieStatus = %d", droneControlService.getDroneNavData().batteryStatus));
			Log.d("navdata", "Flying = " + Boolean.toString(droneControlService.getDroneNavData().flying));
			Log.d("navdata", "Recording = " + Boolean.toString(droneControlService.getDroneNavData().recording));
			Log.d("navdata", "Camera ready = " + Boolean.toString(droneControlService.getDroneNavData().cameraReady));
			Log.d("navdata", "initialized = " + Boolean.toString(droneControlService.getDroneNavData().initialized));
			Log.d("navdata", String.format("emergency state = %d", droneControlService.getDroneNavData().emergencyState));
			Log.d("navdata", "recording ready = " + Boolean.toString(droneControlService.getDroneNavData().recordReady));
			Log.d("navdata", "Camera ready = " + Boolean.toString(droneControlService.getDroneNavData().usbActive));
			Log.d("navdata", String.format("usb remaining = %d", droneControlService.getDroneNavData().usbRemainingTime));
			Log.d("navdata", "recording = " + Boolean.toString(droneControlService.getDroneNavData().recording));
			Log.d("navdata", String.format("num frames = %d", droneControlService.getDroneNavData().numFrames));


			DroneConfigManager.getInstance().setDefaultConfig();
			droneControlService.flatTrim();

			prestageStageActivity.resourceInitialized();
		}
	}

	@Override
	public void onDroneConnected() {
		Log.d(getClass().getSimpleName(), "onDroneConnected()");
		droneControlService.requestConfigUpdate();
	}

	@Override
	public void onDroneDisconnected() {
		Log.d(getClass().getSimpleName(), "onDroneDisconnected()");
	}

	@Override
	public void onDroneAvailabilityChanged(boolean isDroneOnNetwork) {
		// Here we know that the drone is on the network
		if (isDroneOnNetwork) {
			Intent startService = new Intent(prestageStageActivity, DroneControlService.class);
			prestageStageActivity.startService(startService);

			prestageStageActivity.bindService(new Intent(prestageStageActivity, DroneControlService.class),
					this.droneServiceConnection, Context.BIND_AUTO_CREATE);
		} else {
			showUnCancellableErrorDialog(prestageStageActivity,
					prestageStageActivity.getString(R.string.error_no_drone_connected_title),
					prestageStageActivity.getString(R.string.error_no_drone_connected));
		}
	}


	public void onPrestageActivityDestroy() {
		if (droneControlService != null) {
			prestageStageActivity.unbindService(this.droneServiceConnection);
			droneControlService = null;
		}
	}

	public void onPrestageActivityResume() {

			droneReadyReceiver = new DroneReadyReceiver(this);
			droneStateReceiver = new DroneAvailabilityReceiver(this);
			droneConnectionChangeReceiver = new DroneConnectionChangedReceiver(this);

			LocalBroadcastManager manager = LocalBroadcastManager.getInstance(prestageStageActivity
					.getApplicationContext());
			manager.registerReceiver(droneReadyReceiver, new IntentFilter(DroneControlService.DRONE_STATE_READY_ACTION));
			manager.registerReceiver(droneConnectionChangeReceiver, new IntentFilter(
					DroneControlService.DRONE_CONNECTION_CHANGED_ACTION));
			manager.registerReceiver(droneStateReceiver, new IntentFilter(DroneStateManager.ACTION_DRONE_STATE_CHANGED));

	}

	@SuppressLint("NewApi")
	public void checkDroneConnectivity() {

		returnToActivityIntent.putExtra(INIT_DRONE_STRING_EXTRA, true);

		if (checkDroneConnectionTask != null && checkDroneConnectionTask.getStatus() != Status.FINISHED) {
			checkDroneConnectionTask.cancel(true);
		}

		checkDroneConnectionTask = new CheckDroneNetworkAvailabilityTask() {
			@Override
			protected void onPostExecute(Boolean result) {
				onDroneAvailabilityChanged(result);
			}
		};

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			checkDroneConnectionTask.executeOnExecutor(CheckDroneNetworkAvailabilityTask.THREAD_POOL_EXECUTOR,
					prestageStageActivity);
		} else {
			checkDroneConnectionTask.execute(prestageStageActivity);
		}
	}

	public void onPrestageActivityPause() {

			if (droneControlService != null) {
				droneControlService.pause();
			}

			LocalBroadcastManager manager = LocalBroadcastManager.getInstance(prestageStageActivity
					.getApplicationContext());
			manager.unregisterReceiver(droneReadyReceiver);
			manager.unregisterReceiver(droneConnectionChangeReceiver);
			manager.unregisterReceiver(droneStateReceiver);

			if (taskRunning(checkDroneConnectionTask)) {
				checkDroneConnectionTask.cancelAnyFtpOperation();
			}
	}

	private boolean taskRunning(AsyncTask<?, ?, ?> checkMediaTask2) {
		return !(checkMediaTask2 == null || checkMediaTask2.getStatus() == Status.FINISHED);
	}
}

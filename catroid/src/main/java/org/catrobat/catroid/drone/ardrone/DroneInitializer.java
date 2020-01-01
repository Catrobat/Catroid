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
package org.catrobat.catroid.drone.ardrone;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.AsyncTask.Status;
import android.os.IBinder;

import com.parrot.freeflight.receivers.DroneAvailabilityDelegate;
import com.parrot.freeflight.receivers.DroneAvailabilityReceiver;
import com.parrot.freeflight.receivers.DroneConnectionChangeReceiverDelegate;
import com.parrot.freeflight.receivers.DroneConnectionChangedReceiver;
import com.parrot.freeflight.receivers.DroneReadyReceiver;
import com.parrot.freeflight.receivers.DroneReadyReceiverDelegate;
import com.parrot.freeflight.service.DroneControlService;
import com.parrot.freeflight.service.intents.DroneStateManager;
import com.parrot.freeflight.tasks.CheckDroneNetworkAvailabilityTask;

import org.catrobat.catroid.R;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.stage.StageResourceHolder;

import androidx.appcompat.app.AlertDialog;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import static org.catrobat.catroid.CatroidApplication.getAppContext;
import static org.catrobat.catroid.ui.settingsfragments.SettingsFragment.getDronePreferenceMapping;

public class DroneInitializer implements DroneConnectionChangeReceiverDelegate,
		DroneReadyReceiverDelegate,
		DroneAvailabilityDelegate {

	private static final int DRONE_BATTERY_THRESHOLD = 10;

	private DroneConnectionChangedReceiver droneConnectionChangedReceiver;
	private BroadcastReceiver droneReadyReceiver;
	private BroadcastReceiver droneAvailabilityReceiver;

	private CheckDroneNetworkAvailabilityTask checkDroneConnectionTask;

	private DroneControlService droneControlService;

	private StageActivity stageActivity;
	private StageResourceHolder stageResourceHolder;

	private ServiceConnection droneServiceConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			droneControlService = ((DroneControlService.LocalBinder) service).getService();
			droneControlService.resume();
			droneControlService.requestDroneStatus();
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			droneControlService = null;
		}
	};

	public DroneInitializer(StageActivity stageActivity, StageResourceHolder stageResourceHolder) {
		this.stageActivity = stageActivity;
		this.stageResourceHolder = stageResourceHolder;
	}

	private boolean isCheckDroneNetworkAvailabilityTaskRunning() {
		return checkDroneConnectionTask != null && checkDroneConnectionTask.getStatus() != Status.FINISHED;
	}

	public void startDroneNetworkAvailabilityTask() {
		if (isCheckDroneNetworkAvailabilityTaskRunning()) {
			checkDroneConnectionTask.cancel(true);
		}

		checkDroneConnectionTask = new CheckDroneNetworkAvailabilityTask() {
			@Override
			protected void onPostExecute(Boolean result) {
				onDroneAvailabilityChanged(result);
			}
		};

		checkDroneConnectionTask
				.executeOnExecutor(CheckDroneNetworkAvailabilityTask.THREAD_POOL_EXECUTOR, stageActivity);
	}

	@Override
	public void onDroneConnected() {
		droneControlService.requestConfigUpdate();
	}

	@Override
	public void onDroneDisconnected() {
	}

	@Override
	public void onDroneAvailabilityChanged(boolean isDroneOnNetwork) {
		if (isDroneOnNetwork) {
			Intent serviceIntent = new Intent(stageActivity, DroneControlService.class);
			stageActivity.startService(serviceIntent);
			stageActivity.bindService(serviceIntent, droneServiceConnection, Context.BIND_AUTO_CREATE);
		} else {
			new AlertDialog.Builder(stageActivity)
					.setTitle(R.string.error_no_drone_connected_title)
					.setTitle(R.string.error_no_drone_connected)
					.setNeutralButton(R.string.close, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							stageResourceHolder.onDroneInitFailed();
						}
					})
					.setCancelable(false)
					.show();
		}
	}

	@Override
	public void onDroneReady() {
		if (droneControlService == null) {
			return;
		}

		int batteryStatus = droneControlService.getDroneNavData().batteryStatus;
		if (batteryStatus < DRONE_BATTERY_THRESHOLD) {
			String dialogTitle = String.format(stageActivity
					.getString(R.string.error_drone_low_battery_title), batteryStatus);
			new AlertDialog.Builder(stageActivity)
					.setTitle(dialogTitle)
					.setTitle(R.string.error_drone_low_battery)
					.setNeutralButton(R.string.close, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							stageResourceHolder.onDroneInitFailed();
						}
					})
					.setCancelable(false)
					.show();
			return;
		}

		DroneConfigManager.getInstance().setDroneConfig(getDronePreferenceMapping(getAppContext()));
		droneControlService.flatTrim();
		stageResourceHolder.onDroneInitialized();
	}

	public void onResume() {
		droneConnectionChangedReceiver = new DroneConnectionChangedReceiver(this);
		droneAvailabilityReceiver = new DroneAvailabilityReceiver(this);
		droneReadyReceiver = new DroneReadyReceiver(this);

		LocalBroadcastManager manager = LocalBroadcastManager.getInstance(stageActivity);
		manager.registerReceiver(droneConnectionChangedReceiver, new IntentFilter(DroneControlService.DRONE_CONNECTION_CHANGED_ACTION));
		manager.registerReceiver(droneAvailabilityReceiver, new IntentFilter(DroneStateManager.ACTION_DRONE_STATE_CHANGED));
		manager.registerReceiver(droneReadyReceiver, new IntentFilter(DroneControlService.DRONE_STATE_READY_ACTION));
	}

	public void onPause() {
		if (droneControlService != null) {
			droneControlService.pause();
		}

		LocalBroadcastManager manager = LocalBroadcastManager.getInstance(stageActivity);
		manager.unregisterReceiver(droneConnectionChangedReceiver);
		manager.unregisterReceiver(droneAvailabilityReceiver);
		manager.unregisterReceiver(droneReadyReceiver);

		if (isCheckDroneNetworkAvailabilityTaskRunning()) {
			checkDroneConnectionTask.cancelAnyFtpOperation();
		}
	}

	public void onDestroy() {
		if (droneControlService != null) {
			stageActivity.unbindService(droneServiceConnection);
			droneControlService = null;
		}
	}
}

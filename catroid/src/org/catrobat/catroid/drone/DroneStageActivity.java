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

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.parrot.freeflight.drone.NavData;
import com.parrot.freeflight.receivers.DroneBatteryChangedReceiver;
import com.parrot.freeflight.receivers.DroneBatteryChangedReceiverDelegate;
import com.parrot.freeflight.receivers.DroneEmergencyChangeReceiver;
import com.parrot.freeflight.receivers.DroneEmergencyChangeReceiverDelegate;
import com.parrot.freeflight.service.DroneControlService;

import org.catrobat.catroid.R;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.utils.ToastUtil;

public class DroneStageActivity extends StageActivity implements DroneBatteryChangedReceiverDelegate, DroneEmergencyChangeReceiverDelegate {

	private DroneConnection droneConnection = null;
	private DroneBatteryChangedReceiver droneBatteryReceiver;
	private DroneEmergencyChangeReceiver droneEmergencyReceiver;
	private boolean droneBatteryMessageShown = false;

	private enum EmergencyMethod {
		NOTHING,
		TOAST,
		ALERT
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (droneConnection == null && DroneServiceWrapper.checkARDroneAvailability()) {
			droneConnection = new DroneConnection(this);

			try {
				droneConnection.initialise();
				droneBatteryReceiver = new DroneBatteryChangedReceiver(this);
				droneEmergencyReceiver = new DroneEmergencyChangeReceiver(this);
			} catch (RuntimeException runtimeException) {
				Log.e(TAG, "Failure during drone service startup", runtimeException);
				ToastUtil.showError(this, R.string.error_no_drone_connected);
				this.finish();
			}
		}
	}

	@Override
	public void onPause() {
		super.onPause();

		DroneControlService droneControlService = DroneServiceWrapper.getInstance().getDroneService();
		if (droneControlService != null) {
			boolean flyingMode = droneControlService.getDroneNavData().flying;
			if (flyingMode) {
				droneControlService.triggerTakeOff();
			}
		}

		if (droneControlService != null) {
			//wait until drone is landed
			for (int i = 0; i < 30; i++) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					Log.e(getClass().getSimpleName(), "Error in Thread.sleep method!");
				}

				if (!droneControlService.getDroneNavData().flying) {
					break;
				}
			}
		}

		if (droneConnection != null) {
			droneConnection.pause();
		}

		unregisterReceivers();
	}

	@Override
	public void onResume() {
		super.onResume();
		if (droneConnection != null) {
			droneConnection.start();
		}

		registerReceivers();
	}

	@Override
	protected void onDestroy() {
		Log.d(getClass().getSimpleName(), "DroneStageActivity: onDestroy() wurde aufgerufen");
		if (droneConnection != null) {
			droneConnection.destroy();
		}

		droneBatteryMessageShown = false;
		super.onDestroy();
	}

	private void registerReceivers() {
		LocalBroadcastManager manager = LocalBroadcastManager.getInstance(this.getApplicationContext());
		manager.registerReceiver(droneBatteryReceiver, new IntentFilter(DroneControlService.DRONE_BATTERY_CHANGED_ACTION));
		manager.registerReceiver(droneEmergencyReceiver, new IntentFilter(DroneControlService.DRONE_EMERGENCY_STATE_CHANGED_ACTION));
	}

	private void unregisterReceivers() {
		LocalBroadcastManager manager = LocalBroadcastManager.getInstance(this.getApplicationContext());
		manager.unregisterReceiver(droneBatteryReceiver);
		manager.unregisterReceiver(droneEmergencyReceiver);
	}

	@Override
	public void onDroneBatteryChanged(int value) {

		Log.d(TAG, "Battery Status = " + Integer.toString(value));

		DroneControlService dcs = DroneServiceWrapper.getInstance().getDroneService();
		if (dcs != null && (value < DroneInitializer.DRONE_BATTERY_THRESHOLD) && dcs.getDroneNavData().flying && !droneBatteryMessageShown) {
			ToastUtil.showError(this, R.string.notification_low_battery + " " + Integer.toString(value));
			droneBatteryMessageShown = true;
		}
	}

	@Override
	public void onDroneEmergencyChanged(int code) {
		EmergencyMethod method = EmergencyMethod.NOTHING;

		Log.d(getClass().getSimpleName(), "message code integer value: " + Integer.toString(code));
		if (code == NavData.ERROR_STATE_NONE || code == NavData.ERROR_STATE_START_NOT_RECEIVED) {
			return;
		}

		int messageID;

		switch (code) {
			case NavData.ERROR_STATE_EMERGENCY_VBAT_LOW:
				messageID = R.string.drone_emergency_battery_low;
				method = EmergencyMethod.ALERT;
				Log.d(getClass().getSimpleName(), "message code: " + getResources().getString(R.string.drone_emergency_battery_low));
				break;
			case NavData.ERROR_STATE_ALERT_VBAT_LOW:
				messageID = R.string.drone_alert_battery_low;
				method = EmergencyMethod.TOAST;
				Log.d(getClass().getSimpleName(), "message code: " + getResources().getString(R.string.drone_alert_battery_low));
				break;
			case NavData.ERROR_STATE_ALERT_CAMERA:
			case NavData.ERROR_STATE_EMERGENCY_CAMERA:
				messageID = R.string.drone_emergency_camera;
				method = EmergencyMethod.TOAST;
				Log.d(getClass().getSimpleName(), "message code: " + getResources().getString(R.string.drone_emergency_camera));
				break;
			case NavData.ERROR_STATE_EMERGENCY_ULTRASOUND:
				messageID = R.string.drone_emergency_ultrasound;
				method = EmergencyMethod.ALERT;
				Log.d(getClass().getSimpleName(), "message code: " + getResources().getString(R.string.drone_emergency_ultrasound));
				break;
			case NavData.ERROR_STATE_ALERT_ULTRASOUND:
				messageID = R.string.drone_alert_ultrasound;
				method = EmergencyMethod.NOTHING;
				Log.d(getClass().getSimpleName(), "message code: " + getResources().getString(R.string.drone_alert_ultrasound));
				break;
			case NavData.ERROR_STATE_ALERT_VISION:
				messageID = R.string.drone_alert_vision;
				method = EmergencyMethod.TOAST;
				Log.d(getClass().getSimpleName(), "message code: " + getResources().getString(R.string.drone_alert_vision));
				break;
			case NavData.ERROR_STATE_EMERGENCY_ANGLE_OUT_OF_RANGE:
				messageID = R.string.drone_emergency_angle;
				method = EmergencyMethod.ALERT;
				Log.d(getClass().getSimpleName(), "message code: " + getResources().getString(R.string.drone_emergency_angle));
				break;
			case NavData.ERROR_STATE_EMERGENCY_CUTOUT:
				messageID = R.string.drone_emergency_cutout;
				method = EmergencyMethod.ALERT;
				Log.d(getClass().getSimpleName(), "message code: " + getResources().getString(R.string.drone_emergency_cutout));
				break;
			default:
				Log.d(getClass().getSimpleName(), "message code (number): " + code);
				return;
		}

		switch (method) {
			case ALERT:
				new AlertDialog.Builder(this)
						.setTitle(R.string.drone_emergency_title)
						.setMessage(messageID)
						.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialogInterface, int id) {
							}
						})
						.setCancelable(false)
						.show();
				break;

			case TOAST:
				ToastUtil.showError(this, messageID);
				break;
		}
	}
}

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

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import com.parrot.freeflight.drone.NavData;
import com.parrot.freeflight.receivers.DroneBatteryChangedReceiver;
import com.parrot.freeflight.receivers.DroneBatteryChangedReceiverDelegate;
import com.parrot.freeflight.receivers.DroneConnectionChangeReceiverDelegate;
import com.parrot.freeflight.receivers.DroneConnectionChangedReceiver;
import com.parrot.freeflight.receivers.DroneEmergencyChangeReceiver;
import com.parrot.freeflight.receivers.DroneEmergencyChangeReceiverDelegate;
import com.parrot.freeflight.service.DroneControlService;

import org.catrobat.catroid.R;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.utils.ToastUtil;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import androidx.annotation.IntDef;
import androidx.appcompat.app.AlertDialog;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class DroneController implements DroneConnectionChangeReceiverDelegate,
		DroneBatteryChangedReceiverDelegate,
		DroneEmergencyChangeReceiverDelegate {

	public static final String TAG = DroneController.class.getSimpleName();

	private static final int DRONE_BATTERY_THRESHOLD = 10;

	@Retention(RetentionPolicy.SOURCE)
	@IntDef({NOTHING, TOAST, ALERT})
	private @interface EmergencyMethod {}
	private static final int NOTHING = 0;
	private static final int TOAST = 1;
	private static final int ALERT = 2;

	private DroneConnectionChangedReceiver droneConnectionChangedReceiver;
	private DroneBatteryChangedReceiver droneBatteryReceiver;
	private DroneEmergencyChangeReceiver droneEmergencyReceiver;

	private boolean droneBatteryMessageShown = false;

	private StageActivity stageActivity;

	private ServiceConnection droneServiceConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			DroneServiceWrapper.setDroneService(((DroneControlService.LocalBinder) service).getService());
			DroneServiceWrapper.getDroneService().resume();
			DroneServiceWrapper.getDroneService().requestDroneStatus();
			DroneServiceWrapper.getDroneService().requestConfigUpdate();
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			DroneServiceWrapper.setDroneService(null);
		}
	};

	public DroneController(StageActivity stageActivity) {
		this.stageActivity = stageActivity;
	}

	@Override
	public void onDroneConnected() {
		DroneServiceWrapper.getDroneService().requestConfigUpdate();
	}

	@Override
	public void onDroneDisconnected() {
	}

	public void onCreate() {
		if (DroneServiceWrapper.getDroneService() == null) {
			Intent serviceIntent = new Intent(stageActivity, DroneControlService.class);
			boolean bindServiceSuccessful = stageActivity
					.bindService(serviceIntent, droneServiceConnection, Context.BIND_AUTO_CREATE);
			if (bindServiceSuccessful) {
				droneConnectionChangedReceiver = new DroneConnectionChangedReceiver(this);
				droneBatteryReceiver = new DroneBatteryChangedReceiver(this);
				droneEmergencyReceiver = new DroneEmergencyChangeReceiver(this);
			} else {
				ToastUtil.showError(stageActivity, R.string.error_no_drone_connected);
				stageActivity.finish();
			}
		}
	}

	public void onResume() {
		if (DroneServiceWrapper.getDroneService() != null) {
			DroneServiceWrapper.getDroneService().resume();
		}

		LocalBroadcastManager manager = LocalBroadcastManager.getInstance(stageActivity);
		manager.registerReceiver(droneConnectionChangedReceiver, new IntentFilter(DroneControlService.DRONE_CONNECTION_CHANGED_ACTION));
		manager.registerReceiver(droneBatteryReceiver, new IntentFilter(DroneControlService.DRONE_BATTERY_CHANGED_ACTION));
		manager.registerReceiver(droneEmergencyReceiver, new IntentFilter(DroneControlService.DRONE_EMERGENCY_STATE_CHANGED_ACTION));
	}

	public void onPause() {
		if (DroneServiceWrapper.getDroneService() != null) {

			if (DroneServiceWrapper.getDroneService().getDroneNavData().flying) {
				DroneServiceWrapper.getDroneService().triggerTakeOff();
			}

			for (int i = 0; i <= 30 && DroneServiceWrapper.getDroneService().getDroneNavData().flying; i++) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					Log.e(TAG, e.getMessage());
				}
			}

			DroneServiceWrapper.getDroneService().pause();
			DroneServiceWrapper.setDroneService(null);
		}

		LocalBroadcastManager manager = LocalBroadcastManager.getInstance(stageActivity);
		manager.unregisterReceiver(droneConnectionChangedReceiver);
		manager.unregisterReceiver(droneBatteryReceiver);
		manager.unregisterReceiver(droneEmergencyReceiver);
	}

	public void onDestroy() {
		if (DroneServiceWrapper.getDroneService() != null) {
			stageActivity.unbindService(droneServiceConnection);
			DroneServiceWrapper.setDroneService(null);
		}
		droneBatteryMessageShown = false;
	}

	@Override
	public void onDroneBatteryChanged(int value) {
		if (DroneServiceWrapper.getDroneService() != null) {
			int batteryStatus = DroneServiceWrapper.getDroneService().getDroneNavData().batteryStatus;
			boolean isFlying = DroneServiceWrapper.getDroneService().getDroneNavData().flying;

			if (batteryStatus < DRONE_BATTERY_THRESHOLD && isFlying && !droneBatteryMessageShown) {
				ToastUtil.showError(stageActivity, stageActivity.getString(R.string.notification_low_battery_with_value, value));
				droneBatteryMessageShown = true;
			}
		}
	}

	@Override
	public void onDroneEmergencyChanged(int code) {
		@EmergencyMethod
		int method;

		if (code == NavData.ERROR_STATE_NONE || code == NavData.ERROR_STATE_START_NOT_RECEIVED) {
			return;
		}

		int messageID;

		switch (code) {
			case NavData.ERROR_STATE_EMERGENCY_VBAT_LOW:
				messageID = R.string.drone_emergency_battery_low;
				method = ALERT;
				break;
			case NavData.ERROR_STATE_ALERT_VBAT_LOW:
				messageID = R.string.drone_alert_battery_low;
				method = TOAST;
				break;
			case NavData.ERROR_STATE_ALERT_CAMERA:
			case NavData.ERROR_STATE_EMERGENCY_CAMERA:
				messageID = R.string.drone_emergency_camera;
				method = TOAST;
				break;
			case NavData.ERROR_STATE_EMERGENCY_ULTRASOUND:
				messageID = R.string.drone_emergency_ultrasound;
				method = ALERT;
				break;
			case NavData.ERROR_STATE_ALERT_ULTRASOUND:
				messageID = R.string.drone_alert_ultrasound;
				method = NOTHING;
				break;
			case NavData.ERROR_STATE_ALERT_VISION:
				messageID = R.string.drone_alert_vision;
				method = TOAST;
				break;
			case NavData.ERROR_STATE_EMERGENCY_ANGLE_OUT_OF_RANGE:
				messageID = R.string.drone_emergency_angle;
				method = ALERT;
				break;
			case NavData.ERROR_STATE_EMERGENCY_CUTOUT:
				messageID = R.string.drone_emergency_cutout;
				method = ALERT;
				break;
			default:
				Log.d(getClass().getSimpleName(), "unmapped message code (number): " + code);
				return;
		}

		switch (method) {
			case ALERT:
				new AlertDialog.Builder(stageActivity)
						.setTitle(R.string.drone_emergency_title)
						.setMessage(messageID)
						.setPositiveButton(android.R.string.ok, null)
						.setCancelable(false)
						.show();
				break;
			case TOAST:
				ToastUtil.showError(stageActivity, messageID);
				break;
			case NOTHING:
				break;
		}
	}
}

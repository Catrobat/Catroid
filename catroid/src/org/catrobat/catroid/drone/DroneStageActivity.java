/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2015 The Catrobat Team
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
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.parrot.freeflight.drone.NavData;
import com.parrot.freeflight.receivers.DroneBatteryChangedReceiver;
import com.parrot.freeflight.receivers.DroneBatteryChangedReceiverDelegate;
import com.parrot.freeflight.receivers.DroneEmergencyChangeReceiver;
import com.parrot.freeflight.receivers.DroneEmergencyChangeReceiverDelegate;
import com.parrot.freeflight.service.DroneControlService;

import org.catrobat.catroid.R;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.ui.ProjectActivity;

/**
 * Created by Lukas on 16.02.2015.
 */
public class DroneStageActivity extends StageActivity implements DroneBatteryChangedReceiverDelegate, DroneEmergencyChangeReceiverDelegate {

	private DroneConnection droneConnection = null;
	private DroneBatteryChangedReceiver droneBatteryReceiver;
	private DroneEmergencyChangeReceiver droneEmergencyReceiver;
	private boolean DroneBatteryMessageShown = false;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		if (getIntent().getBooleanExtra(DroneInitializer.INIT_DRONE_STRING_EXTRA, false)) {
			droneConnection = new DroneConnection(this);
		}

		if (droneConnection != null) {
			try {
				droneConnection.initialise();
				droneBatteryReceiver = new DroneBatteryChangedReceiver(this);
				droneEmergencyReceiver = new DroneEmergencyChangeReceiver(this);
			} catch (RuntimeException runtimeException) {
				Log.e(TAG, "Failure during drone service startup", runtimeException);
				Toast.makeText(this, R.string.error_no_drone_connected, Toast.LENGTH_LONG).show();
				this.finish();
			}
		}
	}

	@Override
	public void onPause()
	{
		super.onPause();

		if (droneConnection != null) {
			droneConnection.pause();
		}

		unregisterReceivers();
	}

	@Override
	public void onResume()
	{
		super.onResume();
		if (droneConnection != null) {
			droneConnection.start();
		}

		registerReceivers();

	}

	@Override
	protected void onDestroy()
	{
		if (droneConnection != null) {
			droneConnection.destroy();
		}

		DroneBatteryMessageShown = false;
		super.onDestroy();
	}

	private void registerReceivers()
	{
		LocalBroadcastManager manager = LocalBroadcastManager.getInstance(this.getApplicationContext());
		manager.registerReceiver(droneBatteryReceiver, new IntentFilter(DroneControlService.DRONE_BATTERY_CHANGED_ACTION));
		manager.registerReceiver(droneEmergencyReceiver, new IntentFilter(DroneControlService.DRONE_EMERGENCY_STATE_CHANGED_ACTION));
	}

	private void unregisterReceivers()
	{
		LocalBroadcastManager manager = LocalBroadcastManager.getInstance(this.getApplicationContext());
		manager.unregisterReceiver(droneBatteryReceiver);
		manager.unregisterReceiver(droneEmergencyReceiver);
	}

	@Override
	public void onDroneBatteryChanged(int value) {

		Log.d("battery", "Battery Status = " + Integer.toString(value));

		if (value < DroneInitializer.DRONE_BATTERY_THRESHOLD && DroneInitializer.droneControlService.getDroneNavData().flying && !DroneBatteryMessageShown)
		{
			Toast.makeText(this, R.string.notification_low_battery + " " + Integer.toString(value) , Toast.LENGTH_LONG).show();
			DroneBatteryMessageShown = true;
		}
	}

	@Override
	public void onDroneEmergencyChanged(int code)
	{
		Log.d(getClass().getSimpleName(), "message code integer value: " + Integer.toString(code));
		if (code == NavData.ERROR_STATE_NONE || code == NavData.ERROR_STATE_START_NOT_RECEIVED)
			return;
		
		int messageId;

		switch (code) {
			case NavData.ERROR_STATE_EMERGENCY_VBAT_LOW:
				messageId = R.string.drone_emergency_battery_low;
				Log.d(getClass().getSimpleName(), "message code: "+getResources().getString(R.string.drone_emergency_battery_low));
				break;
			case NavData.ERROR_STATE_ALERT_VBAT_LOW:
				messageId = R.string.drone_alert_battery_low;
				Log.d(getClass().getSimpleName(), "message code: "+getResources().getString(R.string.drone_alert_battery_low));
				break;
			case NavData.ERROR_STATE_ALERT_CAMERA:
			case NavData.ERROR_STATE_EMERGENCY_CAMERA:
				messageId = R.string.drone_emergency_camera;
				Log.d(getClass().getSimpleName(), "message code: "+getResources().getString(R.string.drone_emergency_camera));
				break;
			case NavData.ERROR_STATE_EMERGENCY_ULTRASOUND:
				messageId = R.string.drone_emergency_ultrasound;
				Log.d(getClass().getSimpleName(), "message code: "+getResources().getString(R.string.drone_emergency_ultrasound));
				break;
			case NavData.ERROR_STATE_ALERT_ULTRASOUND:
				messageId = R.string.drone_alert_ultrasound;
				Log.d(getClass().getSimpleName(), "message code: "+getResources().getString(R.string.drone_alert_ultrasound));
				break;
			case NavData.ERROR_STATE_ALERT_VISION:
				messageId = R.string.drone_alert_vision;
				Log.d(getClass().getSimpleName(), "message code: "+getResources().getString(R.string.drone_alert_vision));
				break;
			case NavData.ERROR_STATE_EMERGENCY_ANGLE_OUT_OF_RANGE:
				messageId = R.string.drone_emergency_angle;
				Log.d(getClass().getSimpleName(), "message code: "+getResources().getString(R.string.drone_emergency_angle));
				break;
			case NavData.ERROR_STATE_EMERGENCY_CUTOUT:
				messageId = R.string.drone_emergency_cutout;
				Log.d(getClass().getSimpleName(), "message code: "+getResources().getString(R.string.drone_emergency_cutout));
				break;
			default: {
				Log.d(getClass().getSimpleName(), "message code (number): "+ code);
				return;
			}
		}

		new AlertDialog.Builder(this)
				.setTitle(R.string.drone_emergency_title)
				.setMessage(messageId)
				.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int id) {
					}
				})
				.setCancelable(false)
				.show();

	}

}

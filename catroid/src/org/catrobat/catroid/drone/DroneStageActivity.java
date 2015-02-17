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

import android.content.Context;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.parrot.freeflight.receivers.DroneBatteryChangedReceiver;
import com.parrot.freeflight.receivers.DroneBatteryChangedReceiverDelegate;
import com.parrot.freeflight.service.DroneControlService;

import org.catrobat.catroid.R;
import org.catrobat.catroid.stage.StageActivity;

/**
 * Created by Lukas on 16.02.2015.
 */
public class DroneStageActivity extends StageActivity implements DroneBatteryChangedReceiverDelegate {

	private DroneConnection droneConnection = null;
	private DroneBatteryChangedReceiver droneBatteryReceiver;
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
		droneConnection.pause();

		unregisterReceivers();
	}

	@Override
	public void onResume()
	{
		super.onResume();
		droneConnection.start();

		registerReceivers();

	}

	@Override
	protected void onDestroy()
	{
		droneConnection.destroy();
		DroneBatteryMessageShown = false;
		super.onDestroy();
	}

	private void registerReceivers()
	{
		droneBatteryReceiver = new DroneBatteryChangedReceiver(this);
		LocalBroadcastManager manager = LocalBroadcastManager.getInstance(this.getApplicationContext());
		manager.registerReceiver(droneBatteryReceiver, new IntentFilter(DroneControlService.DRONE_BATTERY_CHANGED_ACTION));
	}

	private void unregisterReceivers()
	{
		LocalBroadcastManager manager = LocalBroadcastManager.getInstance(this.getApplicationContext());
		manager.unregisterReceiver(droneBatteryReceiver);
	}

	@Override
	public void onDroneBatteryChanged(int value) {

		Log.d("asdf", "Battery Status = " + Integer.toString(value));

		if (value < DroneInitializer.DRONE_BATTERY_THRESHOLD && DroneInitializer.droneControlService.getDroneNavData().flying && !DroneBatteryMessageShown)
		{
			Toast.makeText(this, R.string.notification_low_battery + " " + Integer.toString(value) , Toast.LENGTH_LONG).show();
			DroneBatteryMessageShown = true;
		}
	}

}

package com.parrot.freeflight.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.parrot.freeflight.service.DroneControlService;

public class DroneBatteryChangedReceiver extends BroadcastReceiver {

	private DroneBatteryChangedReceiverDelegate delegate;

	public DroneBatteryChangedReceiver(DroneBatteryChangedReceiverDelegate delegate) {
		this.delegate = delegate;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		int value = intent.getIntExtra(DroneControlService.EXTRA_DRONE_BATTERY, 0);

		if (delegate != null) {
			delegate.onDroneBatteryChanged(value);
		}
	}
}

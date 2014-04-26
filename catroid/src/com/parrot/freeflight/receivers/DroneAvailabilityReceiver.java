package com.parrot.freeflight.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.parrot.freeflight.service.intents.DroneStateManager;

public class DroneAvailabilityReceiver extends BroadcastReceiver {

	private DroneAvailabilityDelegate delegate;

	public DroneAvailabilityReceiver(DroneAvailabilityDelegate delegate) {
		this.delegate = delegate;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		if (delegate != null) {
			boolean available = intent.getBooleanExtra(DroneStateManager.DRONE_AVAILABLE_ON_NETWORK, false);
			delegate.onDroneAvailabilityChanged(available);
		} else {
			Log.w("DroneAvailabilityReceiver", "Delegate was not set");
		}
	}

}

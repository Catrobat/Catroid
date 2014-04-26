package com.parrot.freeflight.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.parrot.freeflight.service.DroneControlService;

public class DroneConnectionChangedReceiver extends BroadcastReceiver {
	private DroneConnectionChangeReceiverDelegate delegate;

	public DroneConnectionChangedReceiver(DroneConnectionChangeReceiverDelegate delegate) {
		this.delegate = delegate;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		if (delegate != null) {
			String connectionState = intent.getStringExtra(DroneControlService.EXTRA_CONNECTION_STATE);

			if (connectionState != null && connectionState.equals("connected")) {
				delegate.onDroneConnected();
			} else {
				delegate.onDroneDisconnected();
			}
		}
	}

}

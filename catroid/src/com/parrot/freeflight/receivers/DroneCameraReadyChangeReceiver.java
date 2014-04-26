package com.parrot.freeflight.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.parrot.freeflight.service.DroneControlService;

public class DroneCameraReadyChangeReceiver extends BroadcastReceiver {
	private DroneCameraReadyActionReceiverDelegate delegate;

	public DroneCameraReadyChangeReceiver(DroneCameraReadyActionReceiverDelegate delegate) {
		this.delegate = delegate;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		if (delegate != null) {
			boolean ready = intent.getBooleanExtra(DroneControlService.EXTRA_CAMERA_READY, false);
			delegate.onCameraReadyChanged(ready);
		}
	}

}

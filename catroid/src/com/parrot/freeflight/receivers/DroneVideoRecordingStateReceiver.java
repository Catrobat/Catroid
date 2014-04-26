package com.parrot.freeflight.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.parrot.freeflight.service.DroneControlService;

public class DroneVideoRecordingStateReceiver extends BroadcastReceiver {

	private DroneVideoRecordStateReceiverDelegate delegate;

	public DroneVideoRecordingStateReceiver(DroneVideoRecordStateReceiverDelegate delegate) {
		this.delegate = delegate;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		boolean recording = intent.getBooleanExtra(DroneControlService.EXTRA_RECORDING_STATE, false);
		int remaining = intent.getIntExtra(DroneControlService.EXTRA_USB_REMAINING_TIME, 0);
		boolean usbActive = intent.getBooleanExtra(DroneControlService.EXTRA_USB_ACTIVE, false);

		if (delegate != null) {
			delegate.onDroneRecordVideoStateChanged(recording, usbActive, remaining);
		}
	}

}

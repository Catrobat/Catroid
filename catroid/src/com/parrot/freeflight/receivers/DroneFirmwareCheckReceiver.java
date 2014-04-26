package com.parrot.freeflight.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.parrot.freeflight.service.DroneControlService;

public class DroneFirmwareCheckReceiver extends BroadcastReceiver {

	private static final String TAG = DroneFirmwareCheckReceiver.class.getSimpleName();

	private DroneFirmwareCheckReceiverDelegate delegate;

	public DroneFirmwareCheckReceiver(DroneFirmwareCheckReceiverDelegate delegate) {
		this.delegate = delegate;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		boolean updateRequired = intent.getBooleanExtra(DroneControlService.EXTRA_FIRMWARE_UPDATE_REQUIRED, false);

		if (updateRequired) {
			Log.d(TAG, "Drone firmware update required");
		}

		if (delegate != null) {
			delegate.onFirmwareChecked(updateRequired);
		}
	}
}

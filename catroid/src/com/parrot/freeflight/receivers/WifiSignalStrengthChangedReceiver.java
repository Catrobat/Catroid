package com.parrot.freeflight.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;

public class WifiSignalStrengthChangedReceiver extends BroadcastReceiver {

	private WifiSignalStrengthReceiverDelegate delegate;

	public WifiSignalStrengthChangedReceiver(WifiSignalStrengthReceiverDelegate delegate) {
		this.delegate = delegate;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		if (delegate != null) {
			int wifiSignal = WifiManager.calculateSignalLevel(intent.getIntExtra(WifiManager.EXTRA_NEW_RSSI, 0), 4);
			delegate.onWifiSignalStrengthChanged(wifiSignal);
		}
	}

}

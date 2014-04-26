package com.parrot.freeflight.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;

public class NetworkChangeReceiver extends BroadcastReceiver {

	private NetworkChangeReceiverDelegate delegate;

	public NetworkChangeReceiver(NetworkChangeReceiverDelegate delegate) {
		this.delegate = delegate;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		if (delegate != null) {
			NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
			this.delegate.onNetworkChanged(info);
		}
	}

}

package com.parrot.freeflight.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public class MediaStorageReceiver extends BroadcastReceiver {

	private MediaStorageReceiverDelegate delegate;

	public MediaStorageReceiver(MediaStorageReceiverDelegate delegate) {
		this.delegate = delegate;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		if (delegate != null) {
			String action = intent.getAction();
			if (action.equals(Intent.ACTION_MEDIA_SCANNER_FINISHED) || action.equals(Intent.ACTION_MEDIA_MOUNTED)) {
				delegate.onMediaStorageMounted();
			} else if (action.equals(Intent.ACTION_MEDIA_UNMOUNTED)) {
				delegate.onMediaStorageUnmounted();
			} else if (action.equals(Intent.ACTION_MEDIA_EJECT)) {
				delegate.onMediaEject();
			}
		}
	}

	public void registerForEvents(Context context) {
		context.registerReceiver(this, createIntentFilter());
	}

	public void unregisterFromEvents(Context context) {
		context.unregisterReceiver(this);
	}

	protected IntentFilter createIntentFilter() {
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Intent.ACTION_MEDIA_SCANNER_FINISHED);
		intentFilter.addAction(Intent.ACTION_MEDIA_MOUNTED);
		intentFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
		intentFilter.addAction(Intent.ACTION_MEDIA_EJECT);
		intentFilter.addDataScheme("file");

		return intentFilter;
	}

}

package com.parrot.freeflight.drone;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class DroneProxyConfigChangedReceiver extends BroadcastReceiver
{

    public DroneProxyConfigChangedReceiverDelegate delegate;
    
    public DroneProxyConfigChangedReceiver(DroneProxyConfigChangedReceiverDelegate delegate) 
    {
        this.delegate = delegate;
    }
    
    @Override
    public void onReceive(Context context, Intent intent)
    {
        if (delegate != null) {
            delegate.onConfigChanged();
        }
    }

}

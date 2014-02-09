package com.parrot.freeflight.drone;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class DroneProxyConnectionFailedReceiver extends BroadcastReceiver
{
    
    private DroneProxyConnectionFailedReceiverDelegate delegate;

    public DroneProxyConnectionFailedReceiver(DroneProxyConnectionFailedReceiverDelegate delegate) 
    {
        this.delegate = delegate;
    }
    
    @Override
    public void onReceive(Context context, Intent intent)
    {
        int errorCode = 0;
        
        if (delegate != null) {
            delegate.onToolConnectionFailed(errorCode);
        }

    }

}

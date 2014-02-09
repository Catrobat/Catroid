package com.parrot.freeflight.drone;

public interface DroneProxyConnectionFailedReceiverDelegate
{
    void onToolConnectionFailed(int errorCode);
}

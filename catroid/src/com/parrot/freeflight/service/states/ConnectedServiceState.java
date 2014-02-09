/*
 * ConnectedServiceState
 * 
 * Created on: May 5, 2011
 * Author: Dmytro Baryskyy
 */

package com.parrot.freeflight.service.states;

import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.parrot.freeflight.drone.DroneProxy;
import com.parrot.freeflight.drone.DroneProxyConfigChangedReceiver;
import com.parrot.freeflight.drone.DroneProxyConfigChangedReceiverDelegate;
import com.parrot.freeflight.drone.DroneProxyConnectionFailedReceiver;
import com.parrot.freeflight.drone.DroneProxyConnectionFailedReceiverDelegate;
import com.parrot.freeflight.drone.DroneProxyDisconnectedReceiver;
import com.parrot.freeflight.drone.DroneProxyDisconnectedReceiverDelegate;
import com.parrot.freeflight.service.DroneControlService;
import com.parrot.freeflight.service.ServiceStateBase;
import com.parrot.freeflight.service.commands.DisconnectCommand;
import com.parrot.freeflight.service.commands.DroneServiceCommand;
import com.parrot.freeflight.service.commands.PauseCommand;
import com.parrot.freeflight.service.commands.ResumeCommand;

public class ConnectedServiceState
        extends ServiceStateBase
        implements
        DroneProxyDisconnectedReceiverDelegate,
        DroneProxyConnectionFailedReceiverDelegate,
        DroneProxyConfigChangedReceiverDelegate
{
    private Object lock = new Object();

    private LocalBroadcastManager bm;

    private DroneProxyDisconnectedReceiver disconnectedReceiver;
    private DroneProxyConnectionFailedReceiver connFailedReceiver;
    private DroneProxyConfigChangedReceiver configChangeReceiver;

    boolean disconnected;


    public ConnectedServiceState(DroneControlService context)
    {
        super(context);

        bm = LocalBroadcastManager.getInstance(context.getApplicationContext());

        disconnectedReceiver = new DroneProxyDisconnectedReceiver(this);
        connFailedReceiver = new DroneProxyConnectionFailedReceiver(this);
        configChangeReceiver = new DroneProxyConfigChangedReceiver(this);
    }


    @Override
    protected void onPrepare()
    {
        bm.registerReceiver(disconnectedReceiver, new IntentFilter(DroneProxy.DRONE_PROXY_DISCONNECTED_ACTION));
        bm.registerReceiver(connFailedReceiver, new IntentFilter(DroneProxy.DRONE_PROXY_CONNECTION_FAILED_ACTION));
        bm.registerReceiver(configChangeReceiver, new IntentFilter(DroneProxy.DRONE_PROXY_CONFIG_CHANGED_ACTION));
    }


    @Override
    protected void onFinalize()
    {
        bm.unregisterReceiver(disconnectedReceiver);
        bm.unregisterReceiver(connFailedReceiver);
        bm.unregisterReceiver(configChangeReceiver);
    }


    @Override
    public void connect()
    {
        Log.w(getStateName(), "Already connected. Skipped.");
    }


    @Override
    public void disconnect()
    {
        Log.d(getStateName(), "Disconnect");
        disconnected = false;
        startCommand(new DisconnectCommand(context));

        // synchronized (lock) {
        // try {
        // if (!disconnected) {
        // lock.wait(5000);
        // }
        // } catch (InterruptedException e) {
        // e.printStackTrace();
        // }
        // }
    }


    @Override
    public void resume()
    {
        startCommand(new ResumeCommand(context));
    }


    @Override
    public void pause()
    {
        startCommand(new PauseCommand(context));
    }


    public void onToolConnectionFailed(int reason)
    {
        setState(new DisconnectedServiceState(context));

        onDisconnected();
    }


    public void onToolDisconnected()
    {
        disconnected = true;

        synchronized (lock) {
            lock.notify();
        }

        setState(new DisconnectedServiceState(context));

        onDisconnected();
    }


    @Override
    public void onCommandFinished(DroneServiceCommand command)
    {
        if (command instanceof ResumeCommand) {
            onResumed();
        } else if (command instanceof PauseCommand) {
            setState(new PausedServiceState(context));

            onPaused();
        }
    }


    public void onConfigChanged()
    {
        context.onConfigStateChanged();
    }
}

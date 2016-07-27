package org.catrobat.catroid.drone;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.parrot.arsdk.ardiscovery.ARDiscoveryDeviceService;
import com.parrot.arsdk.ardiscovery.ARDiscoveryService;
import com.parrot.arsdk.ardiscovery.receivers.ARDiscoveryServicesDevicesListUpdatedReceiver;
import com.parrot.arsdk.ardiscovery.receivers.ARDiscoveryServicesDevicesListUpdatedReceiverDelegate;

import java.util.ArrayList;
import java.util.List;

public class JumpingSumoDiscoverer
{
    private static final String TAG = "DroneDiscoverer";

    public interface Listener {
        /**
         * Called when the list of seen drones is updated
         * Called in the main thread
         * @param dronesList list of ARDiscoveryDeviceService which represents all available drones
         *                   Content of this list respect the drone types given in startDiscovery
         */
        void onDronesListUpdated(List<ARDiscoveryDeviceService> dronesList);
    }

    private final List<Listener> mListeners;


    private final Context mCtx;

    private ARDiscoveryService mArdiscoveryService;
    private ServiceConnection mArdiscoveryServiceConnection;
    private final ARDiscoveryServicesDevicesListUpdatedReceiver mArdiscoveryServicesDevicesListUpdatedReceiver;

    private final List<ARDiscoveryDeviceService> mMatchingDrones;

    private boolean mStartDiscoveryAfterConnection;

    public JumpingSumoDiscoverer(Context ctx) {
        mCtx = ctx;

        mListeners = new ArrayList<>();

        mMatchingDrones = new ArrayList<>();

        mArdiscoveryServicesDevicesListUpdatedReceiver = new ARDiscoveryServicesDevicesListUpdatedReceiver(mDiscoveryListener);
    }

    /**
     * Add a listener
     * All callbacks of the interface Listener will be called within this function
     * Should be called in the main thread
     * @param listener an object that implements the {@link Listener} interface
     */
    public void addListener(Listener listener) {
        mListeners.add(listener);

        notifyServiceDiscovered(mMatchingDrones);
    }

    /**
     * remove a listener from the listener list
     * @param listener an object that implements the {@link Listener} interface
     */
    public void removeListener(Listener listener) {
        mListeners.remove(listener);
    }

    /**
     * Setup the drone discoverer
     * Should be called before starting discovering
     */
    public void setup() {
        // registerReceivers
        LocalBroadcastManager localBroadcastMgr = LocalBroadcastManager.getInstance(mCtx);
        localBroadcastMgr.registerReceiver(mArdiscoveryServicesDevicesListUpdatedReceiver,
                new IntentFilter(ARDiscoveryService.kARDiscoveryServiceNotificationServicesDevicesListUpdated));

        // create the service connection
        if (mArdiscoveryServiceConnection == null) {
            mArdiscoveryServiceConnection = new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName name, IBinder service) {
                    mArdiscoveryService = ((ARDiscoveryService.LocalBinder) service).getService();

                    if (mStartDiscoveryAfterConnection) {
                        startDiscovering();
                        mStartDiscoveryAfterConnection = false;
                    }
                }

                @Override
                public void onServiceDisconnected(ComponentName name) {
                    mArdiscoveryService = null;
                }
            };
        }

        if (mArdiscoveryService == null) {
            // if the discovery service doesn't exists, bind to it
            Intent i = new Intent(mCtx, ARDiscoveryService.class);
            mCtx.bindService(i, mArdiscoveryServiceConnection, Context.BIND_AUTO_CREATE);
        }
    }

    /**
     * Cleanup the object
     * Should be called when the object is not used anymore
     */
    public void cleanup() {
        stopDiscovering();
        //close discovery service
        Log.d(TAG, "closeServices ...");

        if (mArdiscoveryService != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    mArdiscoveryService.stop();

                    mCtx.unbindService(mArdiscoveryServiceConnection);
                    mArdiscoveryService = null;
                }
            }).start();
        }

        // unregister receivers
        LocalBroadcastManager localBroadcastMgr = LocalBroadcastManager.getInstance(mCtx);
        localBroadcastMgr.unregisterReceiver(mArdiscoveryServicesDevicesListUpdatedReceiver);
    }

    /**
     * Start discovering Parrot drones
     * For Wifi drones, the device should be on the drone's network
     * When drones will be discovered, you will be notified through {@link Listener#onDronesListUpdated(List)}
     */
    public void startDiscovering() {
        if (mArdiscoveryService != null) {
            Log.i(TAG, "Start discovering");
            mDiscoveryListener.onServicesDevicesListUpdated();
            mArdiscoveryService.start();
            mStartDiscoveryAfterConnection = false;
        } else {
            mStartDiscoveryAfterConnection = true;
        }
    }

    /**
     * Stop discovering Parrot drones
     */
    public void stopDiscovering() {
        if (mArdiscoveryService != null) {
            Log.i(TAG, "Stop discovering");
            mArdiscoveryService.stop();
        }
        mStartDiscoveryAfterConnection = false;
    }

    private void notifyServiceDiscovered(List<ARDiscoveryDeviceService> dronesList) {
        List<Listener> listenersCpy = new ArrayList<>(mListeners);
        for (Listener listener : listenersCpy) {
            listener.onDronesListUpdated(dronesList);
        }
    }

    private final ARDiscoveryServicesDevicesListUpdatedReceiverDelegate mDiscoveryListener =
            new ARDiscoveryServicesDevicesListUpdatedReceiverDelegate() {
                @Override
                public void onServicesDevicesListUpdated() {
                    if (mArdiscoveryService != null) {
                        // clear current list
                        mMatchingDrones.clear();
                        List<ARDiscoveryDeviceService> deviceList = mArdiscoveryService.getDeviceServicesArray();

                        if (deviceList != null)
                        {
                            for (ARDiscoveryDeviceService service : deviceList)
                            {
                                mMatchingDrones.add(service);
                            }
                        }
                        notifyServiceDiscovered(mMatchingDrones);
                    }
                }
            };
}

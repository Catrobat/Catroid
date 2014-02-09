/*
 * DroneProxy
 *
 *  Created on: May 5, 2011
 *      Author: Dmytro Baryskyy
 */

package com.parrot.freeflight.drone;

import java.io.File;

import android.content.Context;
import android.content.Intent;
import android.os.StatFs;
import android.provider.Settings.Secure;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.parrot.freeflight.settings.ApplicationSettings;
import com.parrot.freeflight.utils.FileUtils;

public class DroneProxy 
{

	public enum DroneProgressiveCommandFlag
	{
		ARDRONE_PROGRESSIVE_CMD_ENABLE,              // 1: use progressive commands - 0: try hovering
		ARDRONE_PROGRESSIVE_CMD_COMBINED_YAW_ACTIVE, // 1: activate combined yaw - 0: Deactivate combined yaw
		ARDRONE_MAGNETO_CMD_ENABLE,	               // 1: activate the magneto piloting mode - 0: desactivate the mode
	};
	
	// This enum should match the typedef VIDEO_RECORDING_CAPABILITY in jni/API/common.h
	public enum EVideoRecorderCapability 
	{
		NOT_SUPPORTED,		// device can not record video in 360p
		VIDEO_360P,     	// device can record video in 360p
		VIDEO_720P			// device can record video in 720p
	}

	/**
	 * Broadcast action: This will be sent in response to {@link doConnect()} when drone proxy is connected to the ARDroneLib and ARDrone lib is fully initialized
	 * and ready.
	 */
	public static final String DRONE_PROXY_CONNECTED_ACTION        = "drone.proxy.connected.action";
	
	/**
	 * Broadcast action: Will be sent in response to {@link doDisconnect()} when drone proxy is disconnected and ARDroneLib is released.
	 */
	public static final String DRONE_PROXY_DISCONNECTED_ACTION     = "drone.proxy.disconnected.action";
	
	/**
	 * Broadcast action: Will be sent in response to {@link setDefaultConfigurationNative()} or {@link triggerConfigUpdateNative()} if drone configuration has changed.
	 */
	public static final String DRONE_PROXY_CONFIG_CHANGED_ACTION   = "drone.proxy.config.changed.action";
	
	/**
	 * Broadcast action: Will be sent if error occured during connection to the AR.Drone.
	 */
	public static final String DRONE_PROXY_CONNECTION_FAILED_ACTION  = "drone.proxy.connection.failed";
		
	
	private static final String TAG = "DroneProxy";

	private volatile static DroneProxy instance;

	private NavData navdata;
	private DroneConfig config;
	
	private Context applicationContext;

	private DroneAcademyMediaListener academyMediaListener;
	private String DCIMdirPath;

	public static DroneProxy getInstance(Context appContext)
	{
		if (instance == null) {
			instance = new DroneProxy(appContext);
		} else {
		    instance.setAppContext(appContext);
		}

		return instance;
	}


    public DroneProxy(Context appContext)
	{
		navdata = new NavData();
		config = new DroneConfig();
		DCIMdirPath = null;
	}


	public void doConnect(final Context context, EVideoRecorderCapability recordVideoResolution)
	{
		try {
			Log.d(TAG, "Connecting...");

			String uid = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
			String packageName = this.getClass().getPackage().getName();
			Log.d(TAG, "AppName: " + packageName + ", UserID: " + uid);

			File dcimDir = FileUtils.getMediaFolder(context);
			
			if (dcimDir != null) {
			    DCIMdirPath = dcimDir.getAbsolutePath();
			}
			
			// Get cache dir
			File cacheDir = context.getExternalCacheDir();
			File mediaDir = dcimDir;

			if (cacheDir == null) {
				cacheDir = context.getCacheDir();
			}

			if (mediaDir == null) {
				// This is really bad. We can't get directory to cache media files.
				Log.w(TAG, "Cache/Media dir is unavailable.");
				connect(packageName.trim(), uid.trim(), cacheDir.getAbsolutePath(), cacheDir.getAbsolutePath(), 0, EVideoRecorderCapability.NOT_SUPPORTED.ordinal());
			} else {
				StatFs stat = new StatFs(cacheDir.getPath());
				long blockSize = stat.getBlockSize();
				long availableBlocks = stat.getAvailableBlocks();		   
				long usableSpace = blockSize * availableBlocks;

				// Calculating the space that we can use in megabytes.
				int spaceToUse = (int)Math.round(usableSpace * ((double)ApplicationSettings.MEMORY_USAGE / 100.0) / 1048576.0);

				connect(packageName.trim(), uid.trim(), cacheDir.getAbsolutePath(),
						mediaDir.getAbsolutePath(),
						spaceToUse, recordVideoResolution.ordinal());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public void doDisconnect()
	{
		disconnect();
	}


	public NavData getNavdata()
	{
		return navdata;
	}


	public void doPause()
	{
		pause();
	}


	public void doResume()
	{
		resume();
	}


	public void updateNavdata()
	{
		this.navdata = takeNavDataSnapshot(navdata);
	}


	public void onConnected()
	{
        Intent connected = new Intent(DRONE_PROXY_CONNECTED_ACTION);
        LocalBroadcastManager mgr = LocalBroadcastManager.getInstance(applicationContext);
        mgr.sendBroadcast(connected);
	}


	public void onDisconnected()
	{
        Intent disconnected = new Intent(DRONE_PROXY_DISCONNECTED_ACTION);
        LocalBroadcastManager mgr = LocalBroadcastManager.getInstance(applicationContext);
        mgr.sendBroadcast(disconnected);
	}


	public void onConnectionFailed(final int reason)
	{
		Log.w(TAG, "OnConnectionFailed. Reason: " + reason);
		
        Intent connFailed = new Intent(DRONE_PROXY_CONNECTION_FAILED_ACTION);
        LocalBroadcastManager mgr = LocalBroadcastManager.getInstance(applicationContext);
        mgr.sendBroadcast(connFailed);
	}


	public void onConfigChanged()
	{
        Intent configChanged = new Intent(DRONE_PROXY_CONFIG_CHANGED_ACTION);
        LocalBroadcastManager mgr = LocalBroadcastManager.getInstance(applicationContext);
        mgr.sendBroadcast(configChanged);
	}


	public DroneConfig getConfig()
	{
		config = takeConfigSnapshot(config);
		return config;
	}


	public void setAcademyMediaListener(DroneAcademyMediaListener listener)
	{
		this.academyMediaListener = listener;
	}

	
	private static void onConnectedStatic()
	{
		Log.d(TAG, "onConnectedStatic called");
	}

	
	/*
	 * Rename with caution! This method is called from native code. Rename will require rebuild of the native libraries!
	 */
	public void onAcademyNewMediaReady(String path, boolean addToQueue)
	{
		if (null == path || 0 == path.length())
		{
			if (academyMediaListener != null)
			{
				academyMediaListener.onQueueComplete();
			}
		}
		else
		{
			Log.d(TAG, "New media file available: " + path);
			boolean isFileOk = false;
			File file = new File (path);

			if (file.exists() && file.isFile())
			{
				long fSize = file.length();
				if (0 < fSize)
				{
					isFileOk = true;
				}
				else
				{
					Log.d (TAG, "New media has a size of zero --> delete it");
					file.delete();
				}
    		} else {
	    	    if (!file.exists()) {
	    	        Log.w(TAG, "File " + path + " doesn't exists but reported as new media");
	    	    }
	    	}

			if (isFileOk && academyMediaListener != null) {
				if (addToQueue) {
					academyMediaListener.onNewMediaToQueue(path);
				} else {
					academyMediaListener.onNewMediaIsAvailable(path);
				}
			}
		}
	}


    private void setAppContext(Context appContext)
    {
        this.applicationContext = appContext;
    }

   
	// Native methods

	public native void initNavdata();

	public native void triggerTakeOff();
	public native void triggerEmergency();
	public native void setControlValue(int control, float value);
	public native void setMagnetoEnabled(boolean absoluteControlEnabled);
	public native void setCommandFlag(int flag, boolean enable);
	public native void setDeviceOrientation(int heading, int headingAccuracy);
	public native void switchCamera();
	public native void triggerConfigUpdateNative();
	public native void flatTrimNative();
	public native void setDefaultConfigurationNative();
	public native void resetConfigToDefaults();
	public native void takePhoto();
	public native void record();
	public native void calibrateMagneto();
	public native void doFlip();
	public native void setLocation(double latitude, double longitude, double altitude);

	private native void connect(String appName, String username, String rootDir, String flightDir, int flightSize, int recordingCapabilities);
	private native void disconnect();
	private native void pause();
	private native void resume();

	private native NavData takeNavDataSnapshot(NavData navdata);
	private native DroneConfig takeConfigSnapshot(DroneConfig settings);
}

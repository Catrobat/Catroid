/*
 * DroneControlService
 *
 *  Created on: May 5, 2011
 *      Author: Dmytro Baryskyy
 */

package com.parrot.freeflight.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.media.ExifInterface;
import android.os.Binder;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.parrot.freeflight.drone.DroneAcademyMediaListener;
import com.parrot.freeflight.drone.DroneConfig;
import com.parrot.freeflight.drone.DroneConfig.EDroneVersion;
import com.parrot.freeflight.drone.DroneProxy;
import com.parrot.freeflight.drone.DroneProxy.DroneProgressiveCommandFlag;
import com.parrot.freeflight.drone.NavData;
import com.parrot.freeflight.service.commands.DroneServiceCommand;
import com.parrot.freeflight.service.intents.DroneStateManager;
import com.parrot.freeflight.service.listeners.DroneDebugListener;
import com.parrot.freeflight.service.states.ConnectedServiceState;
import com.parrot.freeflight.service.states.DisconnectedServiceState;
import com.parrot.freeflight.service.states.PausedServiceState;
import com.parrot.freeflight.tasks.MoveFileTask;
import com.parrot.freeflight.utils.ARDroneMediaGallery;
import com.parrot.freeflight.utils.FTPUtils;
import com.parrot.freeflight.utils.FileUtils;
import com.parrot.freeflight.utils.GPSHelper;

public class DroneControlService extends Service implements Runnable,
		DroneAcademyMediaListener, LocationListener {
	public static final String VIDEO_RECORDING_STATE_CHANGED_ACTION = "com.parrot.recording.changed";
	public static final String DRONE_EMERGENCY_STATE_CHANGED_ACTION = "com.parrot.emergency.changed";
	public static final String DRONE_FLYING_STATE_CHANGED_ACTION = "com.parrot.flying.changed";
	public static final String DRONE_BATTERY_CHANGED_ACTION = "com.parrot.battery.changed";
	public static final String DRONE_FIRMWARE_CHECK_ACTION = "com.parrot.firmware.checked";
	public static final String DRONE_STATE_READY_ACTION = "com.parrot.drone.ready";
	public static final String DRONE_CONNECTION_CHANGED_ACTION = "com.parrot.drone.connection.changed";
	public static final String NEW_MEDIA_IS_AVAILABLE_ACTION = "com.parrot.controlservice.media.available";
	public static final String DRONE_CONFIG_STATE_CHANGED_ACTION = "com.parrot.config.changed";
	public static final String RECORD_READY_CHANGED_ACTION = "com.parrot.record.ready.changed";
	public static final String CAMERA_READY_CHANGED_ACTION = "com.parrot.camera.ready.changed";

	public static final String EXTRA_RECORDING_STATE = "com.parrot.recording.extra.state";
	public static final String EXTRA_USB_REMAINING_TIME = "com.parrot.extra.usbremaining";
	public static final String EXTRA_USB_ACTIVE = "com.parrot.extra.usbactive";
	public static final String EXTRA_EMERGENCY_CODE = "com.parrot.emergency.extra.code";
	public static final String EXTRA_DRONE_BATTERY = "com.parrot.battery.extra.value";
	public static final String EXTRA_DRONE_FLYING = "com.parrot.flying.extra";
	public static final String EXTRA_FIRMWARE_UPDATE_REQUIRED = "updateRequired";
	public static final String EXTRA_CONNECTION_STATE = "connection.state";
	public static final String EXTRA_MEDIA_PATH = "controlservice.media.path";
	public static final String EXTRA_RECORD_READY = "com.parrot.extra.record.ready";
	public static final String EXTRA_CAMERA_READY = "com.parrot.extra.camera.ready";

	private static final String TAG = "DroneControlService";

	// These constants should match the enum defined in jni/common.h
	private static final int CONTROL_SET_YAW = 0;
	private static final int CONTROL_SET_GAZ = 1;
	private static final int CONTROL_SET_PITCH = 2;
	private static final int CONTROL_SET_ROLL = 3;

	private final IBinder binder = new LocalBinder();
	private DroneProxy droneProxy;

	private Thread navdataUpdateThread;
	private Thread workerThread;
	private boolean stopThreads;

	private Queue<DroneServiceCommand> commandQueue;
	private Object commandQueueLock = new Object();

	private ServiceStateBase currState;

	private DroneDebugListener debugListener;

	private NavData prevNavData;

	private EDroneVersion droneVersion;

	private Object configLock;
	private Object workerThreadLock;
	private Object navdataThreadLock;
	private WakeLock wakeLock;

	private ARDroneMediaGallery gallery;

	private HashMap<String, Intent> intentCache;

	private boolean usbActive;
	// DEBUG
	private long startTime;
	private long prevVideoFrames;

	private ArrayList<String> mediaDownloaded;

	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}

	public class LocalBinder extends Binder {
		public DroneControlService getService() {
			return DroneControlService.this;
		}
	}

	@Override
	public void onCreate() {
		super.onCreate();
		droneVersion = EDroneVersion.UNKNOWN;
		configLock = new Object();
		workerThreadLock = new Object();
		navdataThreadLock = new Object();

		droneProxy = DroneProxy.getInstance(getApplicationContext());
		// Preventing device from sleep
		PowerManager service = (PowerManager) getSystemService(POWER_SERVICE);
		wakeLock = service.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK,
				"DimWakeLock");
		wakeLock.acquire();

		stopThreads = false;
		prevNavData = new NavData();
		// prevBatteryState = 0;

		workerThread = new Thread(this, "Drone Worker Thread");
		navdataUpdateThread = new Thread(navdataUpdateRunnable,
				"Navdata Update Thread");

		commandQueue = new LinkedList<DroneServiceCommand>();

		// Setting current state as disconnected
		setState(new DisconnectedServiceState(this));

		droneProxy.setAcademyMediaListener(this);
		workerThread.start();

		gallery = new ARDroneMediaGallery(this);

		GPSHelper gpsHelper = GPSHelper.getInstance(this);

		if (GPSHelper.isGpsOn(this)) {
			gpsHelper.startListening(this);
			Log.d(TAG, "GPS [OK]");
		} else {
			Log.d(TAG, "GPS [DISABLED]. Video will not be tagged.");
		}

		mediaDownloaded = new ArrayList<String>();

		initIntents();

		connect();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		disconnect();

		if (wakeLock != null && wakeLock.isHeld()) {
			wakeLock.release();
		}

		Log.d(TAG, "All threads have been stopped");

		stopWorkerThreads();

		// TODO: This is not correct but without killing our process
		// ArDrone lib will hang on next launch. Should be removed when
		// library is fixed.x`x
		android.os.Process.killProcess(android.os.Process.myPid());
	}

	private void initIntents() {
		intentCache = new HashMap<String, Intent>(11);
		intentCache.put(VIDEO_RECORDING_STATE_CHANGED_ACTION, new Intent(
				VIDEO_RECORDING_STATE_CHANGED_ACTION));
		intentCache.put(DRONE_EMERGENCY_STATE_CHANGED_ACTION, new Intent(
				DRONE_EMERGENCY_STATE_CHANGED_ACTION));
		intentCache.put(DRONE_FLYING_STATE_CHANGED_ACTION, new Intent(
				DRONE_FLYING_STATE_CHANGED_ACTION));
		intentCache.put(DRONE_BATTERY_CHANGED_ACTION, new Intent(
				DRONE_BATTERY_CHANGED_ACTION));
		intentCache.put(DRONE_FIRMWARE_CHECK_ACTION, new Intent(
				DRONE_FIRMWARE_CHECK_ACTION));
		intentCache.put(DRONE_STATE_READY_ACTION, new Intent(
				DRONE_STATE_READY_ACTION));
		intentCache.put(DRONE_CONNECTION_CHANGED_ACTION, new Intent(
				DRONE_CONNECTION_CHANGED_ACTION));
		intentCache.put(NEW_MEDIA_IS_AVAILABLE_ACTION, new Intent(
				NEW_MEDIA_IS_AVAILABLE_ACTION));
		intentCache.put(DRONE_CONFIG_STATE_CHANGED_ACTION, new Intent(
				DRONE_CONFIG_STATE_CHANGED_ACTION));
		intentCache.put(RECORD_READY_CHANGED_ACTION, new Intent(
				RECORD_READY_CHANGED_ACTION));
		intentCache.put(CAMERA_READY_CHANGED_ACTION, new Intent(
				CAMERA_READY_CHANGED_ACTION));
		intentCache.put(DroneStateManager.ACTION_DRONE_STATE_CHANGED,
				new Intent(DroneStateManager.ACTION_DRONE_STATE_CHANGED));
	}

	/*
	 * Connection methods
	 */

	/**
	 * Connects to the ArDrone
	 */
	protected void connect() {
		currState.connect();
	}

	/**
	 * Disconnects from the ArDrone
	 */
	protected void disconnect() {
		currState.disconnect();
	}

	/**
	 * Pauses data exchange between ArDrone and the service.
	 */
	public void pause() {
		currState.pause();
	}

	/**
	 * Restores data exchange between ArDrone and the service.
	 */
	public void resume() {
		currState.resume();
		setPitch(0);
		setRoll(0);
		setGaz(0);
		setYaw(0);
		setDeviceOrientation(0, 0);
	}

	/*
	 * Drone control methods
	 */

	/**
	 * Makes ArDrone fly or land
	 */
	public void triggerTakeOff() {
		droneProxy.triggerTakeOff();
	}

	/**
	 * Makes the drone to turn around in order to calibrate its compass
	 */
	public void calibrateMagneto() {
		droneProxy.calibrateMagneto();
	}

	/**
	 * Makes the drone to do left flip
	 */
	public void doLeftFlip() {
		droneProxy.doFlip();
	}

	/**
	 * Makes ArDrone to stop engines and fall
	 */
	public void triggerEmergency() {
		droneProxy.triggerEmergency();
	}

	/**
	 * Gets config from the drone. When config is updated -
	 * DRONE_CONFIG_STATE_CHANGED_ACTION will be fired.
	 */
	public void triggerConfigUpdate() {
		droneProxy.triggerConfigUpdateNative();
	}

	/**
	 * Sends ArDrone the command to turn left
	 * 
	 * @param power
	 *            - value between 0 and 1
	 */
	public void turnLeft(final float power) {
		droneProxy.setControlValue(CONTROL_SET_YAW, -power);
	}

	/**
	 * Sends ArDrone the command to turn right
	 * 
	 * @param power
	 *            - value between 0 and 1
	 */
	public void turnRight(final float power) {
		droneProxy.setControlValue(CONTROL_SET_YAW, power);
	}

	/**
	 * Sends ArDrone the command to move forward
	 * 
	 * @param power
	 *            - value between 0 and 1
	 */
	public void moveForward(final float power) {
		droneProxy.setControlValue(CONTROL_SET_PITCH, -power);
	}

	/**
	 * Sends ArDrone the command to move backward
	 * 
	 * @param power
	 *            - value between 0 and 1
	 */
	public void moveBackward(final float power) {
		droneProxy.setControlValue(CONTROL_SET_PITCH, power);
	}

	/**
	 * Sends ArDrone the command to move up
	 * 
	 * @param power
	 *            - value between 0 and 1
	 */
	public void moveUp(final float power) {
		droneProxy.setControlValue(CONTROL_SET_GAZ, power);
	}

	/**
	 * Sends ArDrone the command to move down
	 * 
	 * @param power
	 *            - value between 0 and 1
	 */
	public void moveDown(final float power) {
		droneProxy.setControlValue(CONTROL_SET_GAZ, -power);
	}

	/**
	 * Sends ArDrone the command to move left
	 * 
	 * @param power
	 *            - value between 0 and 1
	 */
	public void moveLeft(final float power) {
		droneProxy.setControlValue(CONTROL_SET_ROLL, -power);
	}

	/**
	 * Sends ArDrone the command to move right
	 * 
	 * @param power
	 *            - value between 0 and 1
	 */
	public void moveRight(final float power) {
		droneProxy.setControlValue(CONTROL_SET_ROLL, power);
	}

	/**
	 * Switches the camera view
	 */
	public void switchCamera() {
		droneProxy.switchCamera();
	}

	/**
	 * Returns copy of current drone's config.
	 * 
	 * @return instance of DroneConfig.
	 */
	public DroneConfig getDroneConfig() {
		DroneConfig config = null;

		synchronized (configLock) {
			config = droneProxy.getConfig();
		}

		return new DroneConfig(config);
	}

	/**
	 * Will reset all drone configuration to it's default values.
	 * DRONE_CONFIG_STATE_CHANGED_ACTION will be fired.
	 */
	public void resetConfigToDefaults() {
		droneProxy.resetConfigToDefaults();
	}

	public void requestConfigUpdate() {
		droneProxy.triggerConfigUpdateNative();
	}

	/**
	 * Capture the photo from the drone camera. Will trigger
	 * NEW_MEDIA_IS_AVAILABLE_ACTION action to be sent.
	 */
	public void takePhoto() {
		droneProxy.takePhoto();
	}

	/**
	 * Will start video recording if it is stopped or stop video recording if it
	 * was started. Will trigger NEW_MEDIA_IS_AVAILABLE_ACTION action to be sent
	 * if new video file become available.
	 */
	public void record() {
		droneProxy.record();
	}

	public boolean isMediaStorageAvailable() {
		return Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED);
	}

	/**
	 * Performs flat trim
	 */
	public void flatTrim() {
		droneProxy.flatTrimNative();
	}

	/*
	 * Methods for controlling the gaz, roll, pitch and yaw directly
	 */

	/**
	 * Sends ArDrone the gaz value. Makes drone to move up or down
	 * 
	 * @param power
	 *            - value between -1 and 1.
	 */
	public void setGaz(final float value) {
		droneProxy.setControlValue(CONTROL_SET_GAZ, value);
	}

	/**
	 * Sends ArDrone the Roll value. Makes drone to move left or right
	 * 
	 * @param power
	 *            - value between -1 and 1.
	 */
	public void setRoll(final float value) {
		droneProxy.setControlValue(CONTROL_SET_ROLL, value);
	}

	/**
	 * Sends ArDrone the Pitch value. Makes drone to move forward or backward
	 * 
	 * @param power
	 *            - value between -1 and 1.
	 */
	public void setPitch(final float value) {
		droneProxy.setControlValue(CONTROL_SET_PITCH, value);
	}

	/**
	 * Sends ArDrone the gaz value. Makes drone to turn left or right
	 * 
	 * @param power
	 *            - value between -1 and 1.
	 */
	public void setYaw(final float value) {
		droneProxy.setControlValue(CONTROL_SET_YAW, value);
	}

	/**
	 * Method that sends all control values in one call.
	 * 
	 * @param pitch
	 * @param roll
	 * @param gaz
	 * @param yaw
	 * @param heading
	 * @param accuracy
	 */
	public native void setControls(final float pitch, final float roll,
			final float gaz, final float yaw, final int heading,
			final int accuracy);

	/**
	 * Notifies the drone about device orientation
	 * 
	 * @param heading
	 *            - heading of the device relative to magnetic North pole in
	 *            degrees.
	 * @param accuracy
	 *            - accuracy of heading in degrees between magnetic north and
	 *            current heading.
	 */
	public void setDeviceOrientation(int heading, int accuracy) {
		// Log.d(TAG, "Heading: " + heading);
		droneProxy.setDeviceOrientation(heading, accuracy);
	}

	// ===================================

	public void setDroneDebugListener(final DroneDebugListener listener) {
		this.debugListener = listener;
	}

	public void onCommandFinished(DroneServiceCommand command) {
		// Log.d(TAG, "Command " + command.getClass().getSimpleName() +
		// " has finished.");
		currState.onCommandFinished(command);
	}

	@Override
	public void onLowMemory() {
		Log.w(TAG, "Low memory alert!");
		super.onLowMemory();
	}

	/*
	 * Protected methods
	 */

	protected void setState(ServiceStateBase state) {
		if (this.currState != null && state != null) {
			Log.d(TAG, "== PREV STATE: " + this.currState.getStateName()
					+ " NEW STATE: " + state.getStateName());
		}

		if (currState != null) {
			currState.onFinalize();
		}

		this.currState = state;

		if (state != null) {
			state.onPrepare();
		}
	}

	protected void onConnected() {
		Log.d(TAG, "====>>> DRONE CONTROL SERVICE CONNECTED");

		if (droneVersion == EDroneVersion.UNKNOWN) {
			droneVersion = getDroneVersion();
		}

		droneProxy.setDefaultConfigurationNative();

		if (navdataUpdateThread != null && !navdataUpdateThread.isAlive()) {
			navdataUpdateThread.start();
		}

		Intent intent = intentCache.get(DRONE_CONNECTION_CHANGED_ACTION);
		intent.putExtra(EXTRA_CONNECTION_STATE, "connected");
		LocalBroadcastManager.getInstance(getApplicationContext())
				.sendBroadcast(intent);
	}

	protected void onDisconnected() {
		synchronized (navdataThreadLock) {
			navdataThreadLock.notify();
		}

		Log.d(TAG, "====>>> DRONE CONTROL SERVICE DISCONNECTED");

		Intent intent = intentCache.get(DRONE_CONNECTION_CHANGED_ACTION);
		intent.putExtra(EXTRA_CONNECTION_STATE, "disconnected");
		LocalBroadcastManager.getInstance(getApplicationContext())
				.sendBroadcast(intent);
	}

	protected void onPaused() {
		if (wakeLock != null && wakeLock.isHeld()) {
			wakeLock.release();
		}

		Log.d(TAG, "====>>> DRONE CONTROL SERVICE PAUSED");
	}

	protected void onResumed() {
		synchronized (navdataThreadLock) {
			navdataThreadLock.notify();
		}

		if (wakeLock != null && !wakeLock.isHeld()) {
			wakeLock.acquire();
		}

		Log.d(TAG, "====>>> DRONE CONTROL SERVICE RESUMED");
	}

	protected void onTookOff() {
		Intent intent = intentCache.get(DRONE_FLYING_STATE_CHANGED_ACTION);
		intent.putExtra(EXTRA_DRONE_FLYING, true);

		LocalBroadcastManager.getInstance(getApplicationContext())
				.sendBroadcast(intent);
	}

	protected void onLanded() {
		Intent intent = intentCache.get(DRONE_FLYING_STATE_CHANGED_ACTION);
		intent.putExtra(EXTRA_DRONE_FLYING, false);

		LocalBroadcastManager.getInstance(getApplicationContext())
				.sendBroadcast(intent);
	}

	protected void onBatteryStateChanged(int batteryStatus) {
		Intent intent = intentCache.get(DRONE_BATTERY_CHANGED_ACTION);
		intent.putExtra(EXTRA_DRONE_BATTERY, batteryStatus);

		LocalBroadcastManager.getInstance(getApplicationContext())
				.sendBroadcast(intent);
	}

	public void onConfigStateChanged() {
		Intent intent = intentCache.get(DRONE_CONFIG_STATE_CHANGED_ACTION);

		LocalBroadcastManager.getInstance(getApplicationContext())
				.sendBroadcast(intent);
	}

	private void onEmergencyStateChanged(int emergency) {
		Intent intent = intentCache.get(DRONE_EMERGENCY_STATE_CHANGED_ACTION);
		intent.putExtra(EXTRA_EMERGENCY_CODE, emergency);

		LocalBroadcastManager.getInstance(getApplicationContext())
				.sendBroadcast(intent);
	}

	private void onRecordChanged(boolean inProgress, boolean usbActive,
			int remaining) {
		this.usbActive = usbActive;

		Intent intent = intentCache.get(VIDEO_RECORDING_STATE_CHANGED_ACTION);
		intent.putExtra(EXTRA_USB_ACTIVE, usbActive);
		intent.putExtra(EXTRA_RECORDING_STATE, inProgress);
		intent.putExtra(EXTRA_USB_REMAINING_TIME, remaining);

		LocalBroadcastManager.getInstance(getApplicationContext())
				.sendBroadcast(intent);
	}

	private void onRecordReadyChanged(boolean recordReady) {
		Intent intent = intentCache.get(RECORD_READY_CHANGED_ACTION);
		intent.putExtra(EXTRA_RECORD_READY, recordReady);

		LocalBroadcastManager.getInstance(getApplicationContext())
				.sendBroadcast(intent);
	}

	private void onCameraReadyChanged(boolean cameraReady) {
		Intent intent = intentCache.get(CAMERA_READY_CHANGED_ACTION);
		intent.putExtra(EXTRA_CAMERA_READY, cameraReady);

		LocalBroadcastManager.getInstance(getApplicationContext())
				.sendBroadcast(intent);
	}

	private void onDroneReady() {
		Intent intent = intentCache.get(DRONE_STATE_READY_ACTION);

		LocalBroadcastManager.getInstance(getApplicationContext())
				.sendBroadcast(intent);
	}

	protected void startCommand(DroneServiceCommand cmd) {
		synchronized (commandQueue) {
			commandQueue.add(cmd);
		}

		synchronized (commandQueueLock) {
			commandQueueLock.notify();
		}
	}

	protected void stopWorkerThreads() {
		stopThreads = true;

		synchronized (navdataThreadLock) {
			navdataThreadLock.notify();
		}

		try {
			navdataUpdateThread.join(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		synchronized (commandQueueLock) {
			commandQueueLock.notify();
		}

		// TODO: Commented this out in order to fix bug
		// https://mantis.parrot.biz/view.php?id=80831
		// as it causes app to join threads too long. Also right after this
		// method
		// kill process is called, so anyway app is going to die hard.
		// Need to find solution how to quickly stop all threads within 1
		// second.

		// try {
		// workerThread.join(3000);
		// } catch (InterruptedException e) {
		// e.printStackTrace();
		// }
	}

	public void run() {
		while (!stopThreads) {
			synchronized (commandQueueLock) {
				try {
					if (commandQueue.isEmpty() && !stopThreads) {
						commandQueueLock.wait();
					}
				} catch (InterruptedException e) {
					return;
				}

				if (stopThreads) {
					return;
				}
			}

			DroneServiceCommand command = null;

			synchronized (commandQueue) {
				command = commandQueue.poll();
			}

			if (command != null) {
				try {
					synchronized (workerThreadLock) {
						command.execute();
					}
				} catch (Exception e) {
					Log.e(TAG, "Commang " + command.getClass().getSimpleName()
							+ " has failed with exception " + e.toString());
					e.printStackTrace();
				}
			}
		}
	}

	public void setMagnetoEnabled(boolean absoluteControlEnabled) {
		droneProxy.setMagnetoEnabled(absoluteControlEnabled);
	}

	public boolean isDroneConnected() {
		return currState instanceof ConnectedServiceState
				|| currState instanceof PausedServiceState;
	}

	public void requestDroneStatus() {
		onBatteryStateChanged(prevNavData.batteryStatus);
		onRecordChanged(prevNavData.recording, prevNavData.usbActive,
				prevNavData.usbRemainingTime);
		onCameraReadyChanged(prevNavData.cameraReady);
		onRecordReadyChanged(prevNavData.recordReady);
		onEmergencyStateChanged(prevNavData.emergencyState);

		if (prevNavData.initialized) {
			onDroneReady();
		}
	}

	public EDroneVersion getDroneVersion() {
		if (droneVersion == EDroneVersion.UNKNOWN) {
			EDroneVersion version = getDroneConfig().getDroneVersion();

			if (version == EDroneVersion.UNKNOWN) {
				String strVersion = FTPUtils.downloadFile(this,
						DroneConfig.getHost(), DroneConfig.getFtpPort(),
						"version.txt");
				if (strVersion != null && strVersion.startsWith("1.")) {
					return EDroneVersion.DRONE_1;
				} else if (strVersion != null && strVersion.startsWith("2.")) {
					return EDroneVersion.DRONE_2;
				}
			}

			droneVersion = version;
			;
		}

		return droneVersion;
	}

	/**
	 * Retrieves FreeFlight media directory where photos and videos are saved.
	 * 
	 * @return Media directory to store the media files or null if media storage
	 *         is not mounted.
	 */
	public File getMediaDir() {
		return FileUtils.getMediaFolder(this);
	}

	// This thread will poll navdata from the drone native lib
	// and will notify listeners about state change such as land/take off or
	// some alert has occured
	private Runnable navdataUpdateRunnable = new Runnable() {

		public void run() {

			droneProxy.initNavdata();

			while (!stopThreads) {
				droneProxy.updateNavdata();
				NavData navData = droneProxy.getNavdata();

				if (navData.emergencyState != prevNavData.emergencyState) {
					onEmergencyStateChanged(navData.emergencyState);
				}

				if (navData.batteryStatus != prevNavData.batteryStatus) {
					onBatteryStateChanged(navData.batteryStatus);
				}

				if (navData.recording != prevNavData.recording
						|| navData.usbRemainingTime != prevNavData.usbRemainingTime
						|| navData.usbActive != prevNavData.usbActive
						|| navData.cameraReady != prevNavData.cameraReady
						|| navData.recordReady != prevNavData.recordReady
						|| navData.flying != prevNavData.flying) {

					onRecordChanged(navData.recording, navData.usbActive,
							navData.usbRemainingTime);
					onCameraReadyChanged(navData.cameraReady);
					onRecordReadyChanged(navData.recordReady);

					if (navData.flying) {
						onTookOff();
					} else {
						onLanded();
					}

					if ((navData.recording != prevNavData.recording)
							&& navData.recording && navData.usbActive
							&& navData.usbRemainingTime == 0
							&& droneProxy.getConfig().isRecordOnUsb()) {
						// Stopping recording because we have not enough space
						// left on USB.
						Log.i(TAG,
								"Not enough space left on USB drive. Stopping recording.");
						droneProxy.record();
					}
				}

				if (navData.initialized != prevNavData.initialized
						&& navData.initialized) {
					onDroneReady();
				}

				if (debugListener != null) {
					if (prevVideoFrames == 0) {
						startTime = System.currentTimeMillis();
						prevVideoFrames = navData.numFrames;
					} else {

						float fps = (float) (navData.numFrames - prevNavData.numFrames)
								/ (float) ((System.currentTimeMillis() - startTime) / 1000.0f);
						startTime = System.currentTimeMillis();

						debugListener.onShowFps((int) fps);
					}
				}

				prevNavData.copyFrom(navData);

				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
					return;
				}

				if (currState instanceof PausedServiceState && !stopThreads) {
					synchronized (navdataThreadLock) {
						try {
							navdataThreadLock.wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
	};

	public void setProgressiveCommandEnabled(boolean b) {
		droneProxy.setCommandFlag(
				DroneProgressiveCommandFlag.ARDRONE_PROGRESSIVE_CMD_ENABLE
						.ordinal(), b);
	}

	public void setProgressiveCommandCombinedYawEnabled(boolean b) {
		droneProxy
				.setCommandFlag(
						DroneProgressiveCommandFlag.ARDRONE_PROGRESSIVE_CMD_COMBINED_YAW_ACTIVE
								.ordinal(), b);
	}

	public void onNewMediaIsAvailable(final String path) {
		final File file = new File(path);
		// We need to add folder/name.jpg info to .jpg files.
		// .mp4 files already contains this information
		if (path.endsWith(".jpg")) {
			try {
				ExifInterface eif = new ExifInterface(path);

				String dir_file = file.getParentFile().getName();
				String dir_date = dir_file.substring(6, 10) + ":"
						+ dir_file.substring(10, 12) + ":"
						+ dir_file.substring(12, 14) + " "
						+ dir_file.substring(15, 17) + ":"
						+ dir_file.substring(17, 19) + ":"
						+ dir_file.substring(19);
				eif.setAttribute(ExifInterface.TAG_GPS_TIMESTAMP, dir_date);
				eif.saveAttributes();

			} catch (IOException ioe) {
				Log.w(TAG, "Error opening exif interface", ioe);
			}
		}

		// Remove temporary directory
		File newFile = null;
		File dcimDir = getMediaDir();

		if (dcimDir != null) {
			newFile = new File(dcimDir, file.getName());

			MoveFileTask moveFile = new MoveFileTask() {
				@Override
				protected void onPostExecute(Boolean result) {
					if (result.equals(Boolean.TRUE)) {
						// Delete temp directory if not empty
						file.getParentFile().delete();

						File newFile = getResultFile();

						gallery.insertMedia(newFile);

						// Notify the rest of the app that new media become
						// available
						Intent intent = intentCache
								.get(NEW_MEDIA_IS_AVAILABLE_ACTION);
						intent.putExtra(EXTRA_MEDIA_PATH,
								newFile.getAbsolutePath());

						LocalBroadcastManager.getInstance(
								getApplicationContext()).sendBroadcast(intent);
					}
				}
			};

			moveFile.execute(file, newFile);
		}
	}

	public void onNewMediaToQueue(String path) {
		mediaDownloaded.add(path);
	}

	public void onQueueComplete() {
		for (String path : mediaDownloaded) {
			this.onNewMediaIsAvailable(path);
		}
		mediaDownloaded.clear();
	}

	public void onLocationChanged(Location location) {
		if (location.hasAltitude() && location.hasAccuracy()
				&& location.getAccuracy() < 100) {
			droneProxy.setLocation(location.getLatitude(),
					location.getLongitude(), location.getAltitude());

			GPSHelper gpsHelper = GPSHelper.getInstance(this);
			gpsHelper.stopListening(this);
		} else {
			Log.d(TAG,
					"Skipped location value as it doesn't have desired accuracy. Accuracy: "
							+ location.getAccuracy() + " meters");
		}
	}

	public void onProviderDisabled(String provider) {
		// Left unimplemented
	}

	public void onProviderEnabled(String provider) {
		// Left unimplemented
	}

	public void onStatusChanged(String provider, int status, Bundle extras) {
		// Left unimplemented
	}

	public boolean isUSBInserted() {
		return usbActive;
	}
}

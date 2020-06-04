/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2018 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * An additional term exception under section 7 of the GNU Affero
 * General Public License, version 3, is available at
 * http://developer.catrobat.org/license_additional_term
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.utils;

import android.os.SystemClock;
import android.os.Vibrator;
import android.util.Log;

import java.util.concurrent.Semaphore;

public final class VibrationUtil {

	private static final String TAG = VibrationUtil.class.getSimpleName();
	private static Vibrator vibration = null;

	private static boolean vibrationActive = false;
	private static boolean paused = false;

	private static Semaphore vibrationThreadSemaphore = new Semaphore(1);

	private static long startTime = 0L;
	private static long timeToVibrate = 0L;
	private static long savedTimeToVibrate = 0L;

	private static final int MAX_TIME_TO_VIBRATE = 60000;

	private VibrationUtil() {
	}

	private static Thread vibrationThread = null;

	public static void setTimeToVibrate(double timeInMillis) {
		Log.d(TAG, "setTimeToVibrate: " + timeInMillis + "ms");
		timeToVibrate = (long) timeInMillis;
		if (timeToVibrate > 0) {
			startVibrate();
		}
		checkAndReleaseVibrationLock();
	}

	public static long getTimeToVibrate() {
		long currentTime = SystemClock.uptimeMillis();
		long timePassed = currentTime - startTime;
		if ((timeToVibrate - timePassed) < 0) {
			return 0;
		}
		return timeToVibrate - timePassed;
	}

	public static void pauseVibration() {
		if (paused) {
			return;
		}
		if ((startTime + timeToVibrate) > SystemClock.uptimeMillis()) {
			savedTimeToVibrate = timeToVibrate - (SystemClock.uptimeMillis() - startTime);
			Log.d(TAG, "pauseVibration - Time left is: " + savedTimeToVibrate + "ms");
		} else {
			savedTimeToVibrate = 0;
		}
		timeToVibrate = 0;
		killVibrationThread();
		paused = true;
	}

	public static void resumeVibration() {

		if (paused) {
			timeToVibrate = savedTimeToVibrate;
			savedTimeToVibrate = 0;
			activateVibrationThread();
			if (timeToVibrate > 0) {
				Log.d(TAG, "resumeVibration - time left: " + timeToVibrate + "ms");
				vibrationThreadSemaphore.release();
				startVibrate();
			}
		}
		paused = false;
	}

	public static void destroy() {
		Log.d(TAG, "reset() - called by StageActivity::onDestroy");
		startTime = 0L;
		timeToVibrate = 0L;
		savedTimeToVibrate = 0L;
		checkAndReleaseVibrationLock();
		paused = false;
		vibration = null;
		vibrationThread = null;
	}

	public static void reset() {
		setTimeToVibrate(0.0);
	}

	public static void setVibrator(Vibrator vibrator) {
		vibration = vibrator;
	}

	public static boolean isActive() {
		return vibrationActive;
	}

	public static void activateVibrationThread() {
		Log.d(TAG, "activateVibrationThread");

		if (vibrationThread == null) {
			vibrationThread = new Thread(() -> {
				try {
					vibrationThreadSemaphore.acquire();
					while ((startTime + timeToVibrate) > SystemClock.uptimeMillis()) {
						Thread.yield();
					}
				} catch (Exception e) {
					Log.e(TAG, "vibrationThreadSemaphore! " + e.getMessage());
				} finally {
					if (vibrationActive) {
						stopVibrate();
					}
					vibrationThreadSemaphore.release();
				}
			});
		}

		if (!vibrationThread.isAlive()) {
			try {
				vibrationThreadSemaphore.acquire();
			} catch (InterruptedException e) {
				Log.e(TAG, "vibrationThreadSemaphore! " + e.getMessage());
			}
			vibrationThread.setName("vibrationThread");
			Log.d(TAG, "starting thread...");
			vibrationThread.start();
		}
	}

	private static void killVibrationThread() {
		Log.d(TAG, "destroy()");
		checkAndReleaseVibrationLock();
		startTime = 0;
		timeToVibrate = 0;
		vibrationThread = null;
	}

	private static void checkAndReleaseVibrationLock() {
		if (vibrationThreadSemaphore.hasQueuedThreads()) {
			vibrationThreadSemaphore.release();
		}
	}

	private static synchronized void startVibrate() {
		if (vibration != null) {
			Log.d(TAG, "startVibrate()");
			startTime = SystemClock.uptimeMillis();
			vibration.vibrate(MAX_TIME_TO_VIBRATE);
			vibrationActive = true;
			Log.d(TAG, "start time was: " + startTime);
		}
	}

	private static synchronized void stopVibrate() {
		if (vibration != null) {
			Log.d(TAG, "stopVibrate()");
			vibration.cancel();
			vibrationActive = false;
		}
	}
}

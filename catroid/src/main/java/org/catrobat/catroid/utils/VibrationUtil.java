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

	private static boolean keepAlive = false;
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
		Log.d(TAG, "setTimeToVibrate()");
		timeToVibrate = (long) timeInMillis;
		releaseQueuedThreads();
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
		Log.d(TAG, "pauseVibration()");
		if (paused) {
			return;
		}
		if ((startTime + timeToVibrate) > SystemClock.uptimeMillis()) {
			savedTimeToVibrate = timeToVibrate - (SystemClock.uptimeMillis() - startTime);
			Log.d(TAG, "PAUSED! time left was: " + savedTimeToVibrate);
		} else {
			savedTimeToVibrate = 0;
		}
		timeToVibrate = 0;
		killVibrationThread();
		paused = true;
	}

	public static void resumeVibration() {
		Log.d(TAG, "resumeVibration()");
		if (paused) {
			timeToVibrate = savedTimeToVibrate;
			Log.d(TAG, "savedTimeToVibrate = " + savedTimeToVibrate);
			savedTimeToVibrate = 0;
			keepAlive = true;
			activateVibrationThread();
			if (timeToVibrate > 0) {
				vibrationThreadSemaphore.release();
			} else {
				Log.d(TAG, "nothing to do");
			}
		}
		paused = false;
	}

	public static void destroy() {
		Log.d(TAG, "reset() - called by StageActivity::onDestroy");
		startTime = 0L;
		timeToVibrate = 0L;
		savedTimeToVibrate = 0L;
		keepAlive = false;
		releaseQueuedThreads();
		paused = false;
		vibration = null;
		vibrationThread = null;
	}

	public static void reset() {
		setTimeToVibrate(0.0);
	}

	public static void setVibration(Vibrator vibration) {
		VibrationUtil.vibration = vibration;
	}

	public static boolean isActive() {
		return keepAlive;
	}

	public static void activateVibrationThread() {
		Log.d(TAG, "activateVibrationThread");
		if (vibration == null) {
			Log.e(TAG, "ERROR: set vibration first!");
			return;
		}
		if (vibrationThread == null) {
			vibrationThread = new Thread(() -> {
				while (keepAlive) {
					try {
						vibrationThreadSemaphore.acquire();
						if (!startVibrate()) {
							break;
						}
						while ((startTime + timeToVibrate) > SystemClock.uptimeMillis()) {
							Thread.yield();
						}
						stopVibrate();
					} catch (InterruptedException e) {
						Log.e(TAG, "vibrationThreadSemaphore! " + e.getMessage());
					}
				}
				vibrationThreadSemaphore.release();
			});
		}

		if (!vibrationThread.isAlive()) {
			try {
				vibrationThreadSemaphore.acquire();
			} catch (InterruptedException e) {
				Log.e(TAG, "vibrationThreadSemaphore! " + e.getMessage());
			}
			keepAlive = true;
			vibrationThread.setName("vibrationThread");
			Log.d(TAG, "starting thread...");
			vibrationThread.start();
		}
	}

	private static void killVibrationThread() {
		Log.d(TAG, "destroy()");
		keepAlive = false;
		releaseQueuedThreads();
		startTime = 0;
		timeToVibrate = 0;
		vibrationThread = null;
	}

	private static synchronized boolean startVibrate() {
		if (vibration != null) {
			startTime = SystemClock.uptimeMillis();
			if (!keepAlive) {
				return false;
			}
			Log.d(TAG, "startVibrate()");
			vibration.vibrate(MAX_TIME_TO_VIBRATE);
			Log.d(TAG, "start time was: " + startTime);
		}
		return true;
	}

	private static synchronized void stopVibrate() {
		if (vibration != null) {
			Log.d(TAG, "stopVibrate()");
			Log.d(TAG, "time spent vibrating is: " + (SystemClock.uptimeMillis() - startTime));
			vibration.cancel();
		}
	}

	private static void releaseQueuedThreads() {
		if (vibrationThreadSemaphore.hasQueuedThreads()) {
			vibrationThreadSemaphore.release();
		}
	}
}


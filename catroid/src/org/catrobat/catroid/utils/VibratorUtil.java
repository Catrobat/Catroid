/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2013 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://developer.catrobat.org/license_additional_term
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.utils;

import android.content.Context;
import android.os.SystemClock;
import android.os.Vibrator;
import android.util.Log;

import java.util.concurrent.Semaphore;

import static android.content.Context.*;

public class VibratorUtil {

	private static final String LOG_TAG = "VibratorUtil::";
	private static Context context = null;
	private static Vibrator vibrator = null;

	private static boolean keepAlive = false;
	private static boolean paused = false;

	private static Semaphore vibratorThreadSemaphore = new Semaphore(1);

	private static long startTime = 0L;
	private static long timeToVibrate = 0L;
	private static long savedTimeToVibrate = 0L;

	private VibratorUtil() {
	}

	private static Thread vibratorThread = null;

	public static void setTimeToVibrate(double timeInMillis) {
		Log.d(LOG_TAG, "setTimeToVibrate()");
		if (vibratorThreadSemaphore.hasQueuedThreads()) {
			vibratorThreadSemaphore.release();
		} else {
			startTime = SystemClock.uptimeMillis();
		}
		timeToVibrate = (long) timeInMillis;
	}

	public static void pauseVibrator() {
		Log.d(LOG_TAG, "pauseVibrator()");
		if (paused) {
			return;
		}
		if ((startTime + timeToVibrate) > SystemClock.uptimeMillis()) {
			savedTimeToVibrate = timeToVibrate - (SystemClock.uptimeMillis() - startTime);
			Log.d(LOG_TAG, "PAUSED! time left was: " + Long.toString(savedTimeToVibrate));
		} else {
			savedTimeToVibrate = 0;
		}
		timeToVibrate = 0;
		killVibratorThread();
		paused = true;
	}

	public static void resumeVibrator() {
		Log.d(LOG_TAG, "resumeVibrator()");
		if (paused) {
			timeToVibrate = savedTimeToVibrate;
			savedTimeToVibrate = 0;
			activateVibratorThread();
			if (timeToVibrate > 0) {
				vibratorThreadSemaphore.release();
			}
		}
		paused = false;
	}

	public static void setContext(Context stageContext) {
		context = stageContext;
	}

	public static boolean isActive() {
		return keepAlive;
	}

	public static void activateVibratorThread() {
		Log.d(LOG_TAG, "activateVibratorThread");
		if (context == null) {
			Log.e(LOG_TAG, "ERROR: set Context first!");
			return;
		}
		if (vibratorThread == null) {
			vibratorThread = new Thread(new Runnable() {
				@Override
				public void run() {
					while (keepAlive) {
						try {
							vibratorThreadSemaphore.acquire();
							startVibrate();
							while ((startTime + timeToVibrate) > SystemClock.uptimeMillis()) {
								Thread.yield();
							}
							stopVibrate();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					vibratorThreadSemaphore.release();
				}
			});
		}

		if (vibrator == null) {
			vibrator = (Vibrator) context.getSystemService(VIBRATOR_SERVICE);
		}

		if (!vibratorThread.isAlive()) {
			try {
				vibratorThreadSemaphore.acquire();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			keepAlive = true;
			vibratorThread.setName("vibratorThread");
			vibratorThread.start();
		}
	}

	public static void killVibratorThread() {
		Log.d(LOG_TAG, "killVibratorThread()");
		keepAlive = false;
		if (vibratorThreadSemaphore.hasQueuedThreads()) {
			vibratorThreadSemaphore.release();
		}
		startTime = 0;
		savedTimeToVibrate = 0;
		timeToVibrate = 0;
		paused = false;
		vibratorThread = null;
	}

	private static synchronized void startVibrate() {
		Log.d(LOG_TAG, "startVibrate()");
		startTime = SystemClock.uptimeMillis();
		vibrator.vibrate(60000);
		Log.d(LOG_TAG, "start time was: " + Long.toString(startTime));
	}

	private static synchronized void stopVibrate() {
		Log.d(LOG_TAG, "stopVibrate()");
		vibrator.cancel();
	}
}

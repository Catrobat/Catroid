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

	private VibratorUtil() {
	}

	private static Thread vibratorThread = null;

	public static void setTimeToVibrate(double timeInMillis) {
		Log.d(LOG_TAG, "setTimeToVibrate()");
		if ((startTime + timeToVibrate) < SystemClock.uptimeMillis()) {
			return;
		}
		timeToVibrate = (long) timeInMillis;
		vibratorThreadSemaphore.release();
	}

	public static void pauseVibrator() {
		Log.d(LOG_TAG, "pauseVibrator()");
		if (paused) {
			return;
		}
		if ((startTime + timeToVibrate) < SystemClock.uptimeMillis()) {
			timeToVibrate -= SystemClock.uptimeMillis() - startTime;
		}
		killVibratorThread();
		paused = true;
	}

	public static void resumeVibrator() {
		Log.d(LOG_TAG, "resumeVibrator()");
		if (paused) {
			activateVibratorThread(null);
		}
		paused = false;
	}

	public static boolean isActive() {
		return keepAlive;
	}

	public static void activateVibratorThread(Context stageContext) {
		Log.d(LOG_TAG, "activateVibratorThread");
		if (stageContext != null) {
			context = stageContext;
		}
		if (vibratorThread == null) {
			vibratorThread = new Thread(new Runnable() {
				@Override
				public void run() {
					while (keepAlive) {
						try {
							vibratorThreadSemaphore.acquire();
							setVibrator();
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
		vibratorThread = null;
	}

	private static synchronized void setVibrator() {
		Log.d(LOG_TAG, "setVibrator()");
		if (timeToVibrate > 0) {
			startVibrate();
		} else {
			stopVibrate();
		}
	}

	private static synchronized void startVibrate() {
		Log.d(LOG_TAG, "startVibrate()");
		startTime = SystemClock.uptimeMillis();
		vibrator.vibrate(timeToVibrate);
	}

	private static synchronized void stopVibrate() {
		Log.d(LOG_TAG, "stopVibrate()");
		vibrator.cancel();
	}
}

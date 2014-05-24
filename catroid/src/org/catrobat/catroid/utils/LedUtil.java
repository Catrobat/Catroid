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

import android.annotation.TargetApi;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Build;
import android.util.Log;

import java.util.concurrent.Semaphore;

public final class LedUtil {

	private static final String LOG_TAG = "LedUtil::";
	private static Camera cam = Camera.open();
	private static Camera.Parameters paramsOn = null;
	private static Camera.Parameters paramsOff = null;

	private static SurfaceTexture surfaceTexture = null;

	private static Semaphore lightThreadSemaphore = new Semaphore(1);

	private static boolean paused = false;
	private static boolean keepAlive = false;

	private static boolean currentLedValue = false;
	private static boolean nextLedValue = false;

	private LedUtil() {
	}

	private static Thread lightThread = new Thread(new Runnable() {
		@Override
		public void run() {
			while (keepAlive) {
				try {
					lightThreadSemaphore.acquire();
					setLed();
				} catch (InterruptedException e) {
					Log.e(LOG_TAG, "lightThreadSemaphore " + e.getMessage());
				}
			}
			lightThreadSemaphore.release();
		}
	});

	public static boolean isActive() {
		return keepAlive;
	}

	public static void setNextLedValue(boolean val) {
		nextLedValue = val;
		lightThreadSemaphore.release();
	}

	public static void pauseLed() {
		Log.d(LOG_TAG, "pauseLed");
		if (!paused) {
			nextLedValue = currentLedValue;
			paused = true;
			killLedThread();
		}
	}

	public static void resumeLed() {
		Log.d(LOG_TAG, "resumeLed()");
		if (paused) {
			activateLedThread();
		}
		setNextLedValue(nextLedValue);
		paused = false;
	}

	public static void reset() {
		Log.d(LOG_TAG, "reset all variables - called by StageActivity::onDestroy");
		currentLedValue = false;
		nextLedValue = false;
		paused = false;
		keepAlive = false;
		if (lightThreadSemaphore.hasQueuedThreads()) {
			lightThreadSemaphore.release();
		}
		cam = null;
		paramsOff = null;
		paramsOn = null;
		surfaceTexture = null;
	}

	public static void activateLedThread() {
		Log.d(LOG_TAG, "activateLedThread()");

		if (lightThread == null) {
			lightThread = new Thread(new Runnable() {
				@Override
				public void run() {
					while (keepAlive) {
						try {
							lightThreadSemaphore.acquire();
							setLed();
						} catch (InterruptedException e) {
							Log.e(LOG_TAG, "lightThreadSemaphore " + e.getMessage());
						}
					}
					lightThreadSemaphore.release();
				}
			});
		}

		if (cam == null) {
			cam = Camera.open();
		}

		if (cam != null) {

			paramsOn = cam.getParameters();
			if (paramsOn != null) {
				paramsOn.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
			}
			paramsOff = cam.getParameters();
			if (paramsOff != null) {
				paramsOff.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
			}

			initializeCamera();

			if (!lightThread.isAlive()) {
				try {
					lightThreadSemaphore.acquire();
				} catch (InterruptedException e) {
					Log.e(LOG_TAG, "lightThreadSemaphore " + e.getMessage());
				}
				keepAlive = true;
				lightThread.setName("lightThread");
				lightThread.start();
			}
		} else {
			Log.e(LOG_TAG, "cam.open() failed!");
		}
	}

	public static void killLedThread() {
		Log.d(LOG_TAG, "killLedThread()");

		keepAlive = false;

		if (lightThreadSemaphore.hasQueuedThreads()) {
			lightThreadSemaphore.release();
		}

		lightThread = null;

		if (cam != null) {
			cam.stopPreview();
			cam.release();
			cam = null;
			paramsOn = null;
			paramsOff = null;
			currentLedValue = false;
			Log.d(LOG_TAG, "killLedThread() : camera released! nextLedValue="+nextLedValue);
		}
	}

	@TargetApi(Build.VERSION_CODES.KITKAT)
	private static void initializeCamera() {
		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1) {
			try {
				surfaceTexture = new SurfaceTexture(1, true);
				cam.setPreviewTexture(surfaceTexture);
			} catch (Exception e) {
				Log.e(LOG_TAG, "surfaceTexture failed! " + e.getMessage());
			}
		}
	}

	private static synchronized void setLed() {
		Log.d(LOG_TAG, "setLed()");
		if (nextLedValue != currentLedValue) {
			if (nextLedValue) {
				ledOn();
			} else {
				ledOff();
			}
		} else {
			Log.d(LOG_TAG, "nothing to do setLed()");
		}
	}

	private static synchronized void ledOn() {
		Log.d(LOG_TAG, "ledOn()");
		cam.setParameters(paramsOn);
		cam.startPreview();
		currentLedValue = true;
	}

	private static synchronized void ledOff() {
		Log.d(LOG_TAG, "ledOff()");
		cam.setParameters(paramsOff);
		cam.startPreview();
		currentLedValue = false;
	}
}

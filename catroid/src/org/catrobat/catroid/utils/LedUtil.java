/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2014 The Catrobat Team
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

import android.annotation.TargetApi;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Build;
import android.util.Log;

import java.io.IOException;
import java.util.concurrent.Semaphore;

public final class LedUtil {

	private static final String TAG = LedUtil.class.getSimpleName();
	private static Camera cam = null;
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
				} catch (InterruptedException interruptedException) {
					Log.e(TAG, "lightThreadSemaphore " + interruptedException.getMessage());
				}
			}
			lightThreadSemaphore.release();
		}
	});

	public static boolean isActive() {
		return keepAlive;
	}

	public static Camera getCamera() {
		return cam;
	}

	public static void openCamera() {
		if (cam == null) {
			try {
				cam = Camera.open();
			} catch (Exception exception) {
				Log.e(TAG, "failed to open Camera", exception);
			}
		}
	}

	public static void setNextLedValue(boolean val) {
		nextLedValue = val;
		lightThreadSemaphore.release();
	}

	public static void pauseLed() {
		Log.d(TAG, "pauseLed");
		if (!paused) {
			nextLedValue = currentLedValue;
			paused = true;
			killLedThread();
		}
	}

	public static void resumeLed() {
		Log.d(TAG, "resumeLed()");
		if (paused) {
			activateLedThread();
		}
		setNextLedValue(nextLedValue);
		paused = false;
	}

	public static void destroy() {
		Log.d(TAG, "reset all variables - called by StageActivity::onDestroy");
		currentLedValue = false;
		nextLedValue = false;
		paused = false;
		keepAlive = false;
		if (lightThreadSemaphore.hasQueuedThreads()) {
			lightThreadSemaphore.release();
		}
		lightThread = null;
		if (cam != null) {
			cam.stopPreview();
			cam.release();
			cam = null;
			paramsOff = null;
			paramsOn = null;
			surfaceTexture = null;
		}
	}

	public static void reset() {
		setNextLedValue(false);
	}

	public static void activateLedThread() {
		Log.d(TAG, "activateLedThread()");

		if (lightThread == null) {
			lightThread = new Thread(new Runnable() {
				@Override
				public void run() {
					while (keepAlive) {
						try {
							lightThreadSemaphore.acquire();
							setLed();
						} catch (InterruptedException interruptedException) {
							Log.e(TAG, "lightThreadSemaphore " + interruptedException.getMessage());
						}
					}
					lightThreadSemaphore.release();
				}
			});
		}

		openCamera();

		if (cam != null) {

			paramsOn = cam.getParameters();
			if (paramsOn != null) {
				paramsOn.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
			}
			paramsOff = cam.getParameters();
			if (paramsOff != null) {
				paramsOff.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
			}

			initializeSurfaceTexture();

			if (!lightThread.isAlive()) {
				try {
					lightThreadSemaphore.acquire();
				} catch (InterruptedException interruptedException) {
					Log.e(TAG, "lightThreadSemaphore " + interruptedException.getMessage());
				}
				keepAlive = true;
				lightThread.setName("lightThread");
				lightThread.start();
			}
		}
	}

	public static void killLedThread() {
		Log.d(TAG, "killLedThread()");

		keepAlive = false;

		if (lightThreadSemaphore.hasQueuedThreads()) {
			lightThreadSemaphore.release();
		}

		lightThread = null;

		closeCamera();
	}

	public static void closeCamera() {
		if (cam != null) {
			cam.stopPreview();
			cam.release();
			cam = null;
			paramsOn = null;
			paramsOff = null;
			currentLedValue = false;
			Log.d(TAG, "killLedThread() : camera released! nextLedValue="+nextLedValue);
		}
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private static void initializeSurfaceTexture() {
		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1) {
			try {
				surfaceTexture = new SurfaceTexture(1);
				cam.setPreviewTexture(surfaceTexture);
			} catch (IOException ioException) {
				Log.e(TAG, "surfaceTexture failed! " + ioException.getMessage());
			}
		}
	}

	private static synchronized void setLed() {
		Log.d(TAG, "setLed()");
		if (nextLedValue != currentLedValue) {
			if (nextLedValue) {
				ledOn();
			} else {
				ledOff();
			}
		} else {
			Log.d(TAG, "nothing to do setLed()");
		}
	}

	private static synchronized void ledOn() {
		Log.d(TAG, "ledOn()");
		cam.setParameters(paramsOn);
		cam.startPreview();
		currentLedValue = true;
	}

	private static synchronized void ledOff() {
		Log.d(TAG, "ledOff()");
		cam.setParameters(paramsOff);
		cam.startPreview();
		currentLedValue = false;
	}
}

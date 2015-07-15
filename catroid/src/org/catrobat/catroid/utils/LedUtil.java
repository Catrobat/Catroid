/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2015 The Catrobat Team
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

import android.hardware.Camera;
import android.util.Log;

import org.catrobat.catroid.camera.CameraManager;

import java.util.concurrent.Semaphore;

public final class LedUtil {

	private static final String TAG = LedUtil.class.getSimpleName();
	private static Camera.Parameters paramsOn = null;
	private static Camera.Parameters paramsOff = null;

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
		if (CameraManager.getInstance().getCamera() != null) {
			CameraManager.getInstance().releaseCamera();
			paramsOff = null;
			paramsOn = null;
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

		if (CameraManager.getInstance().getCamera() != null) {

			paramsOn = CameraManager.getInstance().getCamera().getParameters();
			if (paramsOn != null) {
				paramsOn.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
			}
			paramsOff = CameraManager.getInstance().getCamera().getParameters();
			if (paramsOff != null) {
				paramsOff.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
			}

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
		} else {
			Log.e(TAG, "cam.open() failed!");
		}
	}

	public static void killLedThread() {
		Log.d(TAG, "killLedThread()");

		keepAlive = false;

		if (lightThreadSemaphore.hasQueuedThreads()) {
			lightThreadSemaphore.release();
		}

		lightThread = null;

		if (CameraManager.getInstance().getCamera() != null) {
			CameraManager.getInstance().releaseCamera();
			paramsOn = null;
			paramsOff = null;
			currentLedValue = false;
			Log.d(TAG, "killLedThread() : camera released! nextLedValue=" + nextLedValue);
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
		CameraManager.getInstance().setLedParams(paramsOn);
		currentLedValue = true;
	}

	private static synchronized void ledOff() {
		Log.d(TAG, "ledOff()");
		CameraManager.getInstance().setLedParams(paramsOff);
		currentLedValue = false;
	}
}

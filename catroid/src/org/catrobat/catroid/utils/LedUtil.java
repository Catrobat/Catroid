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

import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Build;
import android.util.Log;

import java.util.concurrent.Semaphore;

public class LedUtil {

	private static final String LOG_TAG = "LedUtil::";
	private static Camera cam = null;

	private static final long CAM_RELEASE_DELAY_MILLIS = 4000;
	private static long switchOffTimeMillis;

	private static boolean lightOn = false;
	private static boolean previousLightOn = false;
	private static boolean ledValue = false;
	private static boolean paused = false;

	private static boolean lightThreadActive = true;
	private static Semaphore lightThreadSemaphore = new Semaphore(1);
	private static Semaphore cameraReleaseThreadSemaphore = new Semaphore(1);
	private static Semaphore lightOnSemaphore = new Semaphore(1);
	private static Semaphore camSemaphore = new Semaphore(1);

	private static Thread lightThread = new Thread(new Runnable() {
		@Override
		public void run() {
			while (lightThreadActive) {
				try {
					lightThreadSemaphore.acquire();
					setLed(ledValue);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			}
			lightThreadSemaphore.release();
		}
	});

	private static Thread cameraReleaseThread = new Thread(new Runnable () {
		@Override
		public void run() {
			while (lightThreadActive) {
				try {
					cameraReleaseThreadSemaphore.acquire();
					while (switchOffTimeMillis > System.currentTimeMillis()) {
						Thread.sleep(100);
					}
					camSemaphore.acquire();
					if (cam != null && !getLightOn()) {
//						cam.release();
//						cam = null;
						Log.d(LOG_TAG, "cameraReleaseThread: camera released! " + System.currentTimeMillis());
					}
					camSemaphore.release();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			cameraReleaseThreadSemaphore.release();
		}
	});

	private static void setLightOn(boolean lightOn) throws InterruptedException {
		lightOnSemaphore.acquire();
		LedUtil.lightOn = lightOn;
		lightOnSemaphore.release();
	}

	private static boolean getLightOn() throws InterruptedException {
		lightOnSemaphore.acquire();
		boolean ret = LedUtil.lightOn;
		lightOnSemaphore.release();
		return ret;
	}

	public static void setLedValue(boolean val) {
		ledValue = val;
		lightThreadSemaphore.release();
	}

	public static void pauseLed() {
		Log.d(LOG_TAG, "pauseLed");
		if (!paused) {
			paused = true;
			try {
				if (getLightOn()) {
					setLedValue(false);
					previousLightOn = true;
				} else {
					previousLightOn = false;
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public static void resumeLed() {
		Log.d(LOG_TAG, "resumeLed()");
		setLedValue(previousLightOn);
		paused = false;
	}

	private LedUtil() {
	}

	public static void activateLedThread() {
		Log.d(LOG_TAG, "activateLedThread()");

		if (!lightThread.isAlive()) {
			try {
				// threads have to start in waiting state
				lightThreadSemaphore.acquire();
				cameraReleaseThreadSemaphore.acquire();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			lightThread.setName("lightThread");
			lightThread.start();
		}
		if (!cameraReleaseThread.isAlive()) {
			Log.d(LOG_TAG, "start cameraReleaseThread");
			cameraReleaseThread.setName("cameraReleaseThread");
			cameraReleaseThread.start();
		}
	}

	private static synchronized void setLed(boolean ledValue) {
		try {
			if (ledValue) {
				ledOn();
			} else {
				ledOff();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private static synchronized void ledOn() throws InterruptedException {
		if (getLightOn()) {
			return;
		}

		try {
			camSemaphore.acquire();
			if (cam != null) {
				cam.startPreview();
				setLightOn(true);
				Log.d(LOG_TAG, "ledOn()");
			} else {
				cam = Camera.open();

				if (cam != null) {
					Camera.Parameters params = cam.getParameters();
					if (params != null) {
						params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);

						if (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1) {
							cam.setPreviewTexture(new SurfaceTexture(0));
						}

						cam.setParameters(params);
						cam.startPreview();
						setLightOn(true);
						Log.d(LOG_TAG, "ledON() + open()");
					}
				}
			}
			camSemaphore.release();
		} catch (Exception e) {
			Log.d(LOG_TAG, e.getMessage());
		}
	}

	private static synchronized void ledOff() throws InterruptedException {
		if (!getLightOn()) {
			return;
		}

		Log.d(LOG_TAG, "ledOff()");
		try {
			camSemaphore.acquire();
			if (cam != null) {
				cam.stopPreview();
				setLightOn(false);
				switchOffTimeMillis = System.currentTimeMillis() + CAM_RELEASE_DELAY_MILLIS;
				Log.d(LOG_TAG, "release at " + switchOffTimeMillis + " ms");
				if (cameraReleaseThreadSemaphore.hasQueuedThreads()) {
					cameraReleaseThreadSemaphore.release();
				}
			}
			camSemaphore.release();
		} catch (Exception e) {
			Log.d(LOG_TAG, e.getMessage());
		}
	}
}

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


/**
 * @author BerndBaumann
 *
 * Run a daemon thread that controls the LED flash of a back camera.
 */
public class LedUtil {

	private static final String LOG_TAG = "LedUtil::";
	private static Camera cam = null;

	private static boolean lightON = false;
	private static boolean previousLightOn = false;
	private static boolean ledValue = false;
	private static boolean paused = false;

	private static boolean lightThreadActive = true;
	private static Semaphore lightThreadSemaphore = new Semaphore(1);
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


	public static boolean getLedValue() {
		return ledValue;
	}

	public static void setLedValue(boolean val) {
		ledValue = val;
		lightThreadSemaphore.release();
	}

	public static void pauseLed() {
		Log.d(LOG_TAG, "pauseLed");
		if (!paused) {
			paused = true;
			if (lightON == true) {
				setLedValue(false);
				previousLightOn = true;
			} else {
				previousLightOn = false;
			}
		}
	}

	public static void resumeLed() {
		Log.d(LOG_TAG, "resumeLed()");
		setLedValue(previousLightOn);
		paused = false;
	}

	public LedUtil() {
	}

	public static void activateLedThread() {
		Log.d(LOG_TAG, "activateLedThread()");

		if (!lightThread.isAlive()) {
			try {
				// thread has to start in waiting state
				lightThreadSemaphore.acquire();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			lightThread.setName("lightThread");
			lightThread.start();
		}
	}

	private synchronized static void setLed(boolean ledValue) {
		Log.d(LOG_TAG, "setLed()");
		if (ledValue)
			ledON();
		else
			ledOFF();
	}

	private synchronized static void ledON() {
		if (lightON == true)
			return;

		Log.d(LOG_TAG, "ledON()");
		try {
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
					lightON = true;
				}
			}
		} catch (Exception e) {
			// TODO: Toast message
			Log.d(LOG_TAG, e.getMessage());
		}
	}

	private synchronized static void ledOFF() {
		if (lightON == false)
			return;

		Log.d(LOG_TAG, "ledOFF()");
		try {
			if (cam != null) {
				cam.stopPreview();
				cam.release();
				cam = null;
				lightON = false;
			}
		} catch (Exception e) {
			// TODO: Toast message
			Log.d(LOG_TAG, e.getMessage());
		}
	}
}

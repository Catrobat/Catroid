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

public final class LedUtil {

	private static final String TAG = LedUtil.class.getSimpleName();
	private static Camera.Parameters paramsOn = null;
	private static Camera.Parameters paramsOff = null;

	private static boolean paused = false;
	private static boolean available = false;

	private static boolean currentLedValue = false;

	private static boolean startAgain = false;

	private LedUtil() {
	}

	public static boolean isAvailable() {
		return available;
	}

	public static boolean isOn() {
		return currentLedValue;
	}

	public static void pauseLed() {
		Log.d(TAG, "pauseLed");
		if (!paused && isAvailable()) {
			paused = true;
			if (isOn()) {
				startAgain = true;
				ledOff();
			}
		}
	}

	public static void resumeLed() {
		Log.d(TAG, "resumeLed()");
		if (paused) {
			initializeLed();
			if (startAgain) {
				ledOn();
				startAgain = false;
			}
		}
		paused = false;
	}

	public static void destroy() {
		Log.d(TAG, "reset all variables - called by StageActivity::onDestroy");
		currentLedValue = false;
		paused = false;
		available = false;

		if (CameraManager.getInstance().isReady()) {
			paramsOff = null;
			paramsOn = null;
		}
	}

	public static void reset() {
		currentLedValue = false;
	}

	public static void initializeLed() {
		Log.d(TAG, "initializeLed()");

		available = true;

		if (!CameraManager.getInstance().isReady()) {
			CameraManager.getInstance().startCamera();
		}

		paramsOn = CameraManager.getInstance().getCamera().getParameters();
		if (paramsOn != null) {
			paramsOn.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
		}
		paramsOff = CameraManager.getInstance().getCamera().getParameters();
		if (paramsOff != null) {
			paramsOff.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
		}
	}

	public static void ledOn() {
		Log.d(TAG, "ledOn()");
		CameraManager.getInstance().setLedParams(paramsOn);
		currentLedValue = true;
	}

	public static void ledOff() {
		Log.d(TAG, "ledOff()");
		CameraManager.getInstance().setLedParams(paramsOff);
		currentLedValue = false;
	}
}

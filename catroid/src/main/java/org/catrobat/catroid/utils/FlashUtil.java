/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2017 The Catrobat Team
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

public final class FlashUtil {

	private static final String TAG = FlashUtil.class.getSimpleName();
	private static Camera.Parameters paramsOn = null;
	private static Camera.Parameters paramsOff = null;

	private static boolean paused = false;
	private static boolean available = false;

	private static boolean currentFlashValue = false;

	private static boolean startAgain = false;

	private FlashUtil() {
	}

	public static boolean isAvailable() {
		return available;
	}

	public static boolean isOn() {
		return currentFlashValue;
	}

	public static void pauseFlash() {
		Log.d(TAG, "pauseFlash");
		if (!paused && isAvailable()) {
			paused = true;
			if (isOn()) {
				startAgain = true;
				flashOff();
			}
		}
	}

	public static void resumeFlash() {
		Log.d(TAG, "resumeFlash()");
		if (paused) {
			initializeFlash();
			if (startAgain) {
				flashOn();
				startAgain = false;
			}
		}
		paused = false;
	}

	public static void destroy() {
		Log.d(TAG, "reset all variables - called by StageActivity::onDestroy");
		currentFlashValue = false;
		paused = false;
		available = false;
		startAgain = false;

		if (CameraManager.getInstance().isReady()) {
			paramsOff = null;
			paramsOn = null;
		}
	}

	public static void reset() {
		currentFlashValue = false;
	}

	public static void initializeFlash() {
		Log.d(TAG, "initializeFlash()");

		available = true;

		if (!CameraManager.getInstance().isReady()) {
			CameraManager.getInstance().startCamera();
		}

		paramsOn = CameraManager.getInstance().getCurrentCamera().getParameters();
		if (paramsOn != null) {
			paramsOn.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
		}
		paramsOff = CameraManager.getInstance().getCurrentCamera().getParameters();
		if (paramsOff != null) {
			paramsOff.setFlashMode("off");
		}
	}

	public static void flashOn() {
		Log.d(TAG, "flashOn()");
		if (!CameraManager.getInstance().hasCurrentCameraFlash()) {
			currentFlashValue = false;
			CameraManager.getInstance().destroyStage();
		} else {
			CameraManager.getInstance().setFlashParams(paramsOn);
			currentFlashValue = true;
		}
	}

	public static void flashOff() {
		Log.d(TAG, "flashOff()");
		CameraManager.getInstance().setFlashParams(paramsOff);
		currentFlashValue = false;
	}
}

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
package org.catrobat.catroid.facedetection;

import android.hardware.Camera;
import android.util.Log;

import org.catrobat.catroid.camera.CameraManager;
import org.catrobat.catroid.formulaeditor.SensorCustomEventListener;

public final class FaceDetectionHandler {

	private static final String TAG = FaceDetectionHandler.class.getSimpleName();
	private static FaceDetector faceDetector;
	private static boolean running = false;
	private static boolean paused = false;

	// Suppress default constructor for noninstantiability
	private FaceDetectionHandler() {
		throw new AssertionError();
	}

	private static void createFaceDetector() {
		if (isIcsFaceDetectionSupported()) {
			faceDetector = new IcsFaceDetector();
		} else {
			faceDetector = new SlowFaceDetector();
		}
	}

	public static boolean isFaceDetectionRunning() {
		return running;
	}

	public static boolean startFaceDetection() {
		if (running) {
			return true;
		}
		if (faceDetector == null) {
			createFaceDetector();
			if (faceDetector == null) {
				return false;
			}
		}
		running = faceDetector.startFaceDetection();
		return running;
	}

	public static void resetFaceDedection() {
		if (running) {
			stopFaceDetection();
		}
		paused = false;
	}

	public static void stopFaceDetection() {
		if (!running) {
			return;
		}
		if (faceDetector == null) {
			return;
		}

		faceDetector.stopFaceDetection();
		running = false;
	}

	public static void pauseFaceDetection() {
		if (!running) {
			return;
		}
		if (faceDetector == null) {
			return;
		}
		paused = true;
		stopFaceDetection();
		running = false;
	}

	public static void resumeFaceDetection() {
		if (!paused) {
			return;
		}
		startFaceDetection();
		paused = false;
	}

	public static void registerOnFaceDetectedListener(SensorCustomEventListener listener) {
		if (faceDetector == null) {
			createFaceDetector();
		}
		faceDetector.addOnFaceDetectedListener(listener);
	}

	public static void unregisterOnFaceDetectedListener(SensorCustomEventListener listener) {
		if (faceDetector == null) {
			return;
		}
		faceDetector.removeOnFaceDetectedListener(listener);
	}

	public static void registerOnFaceDetectionStatusListener(SensorCustomEventListener listener) {
		if (faceDetector == null) {
			createFaceDetector();
		}
		faceDetector.addOnFaceDetectionStatusListener(listener);
	}

	public static void unregisterOnFaceDetectionStatusListener(SensorCustomEventListener listener) {
		if (faceDetector == null) {
			return;
		}
		faceDetector.removeOnFaceDetectionStatusListener(listener);
	}

	public static boolean isIcsFaceDetectionSupported() {
		int possibleFaces = 0;
		try {
			if (!CameraManager.getInstance().isReady()) {
				CameraManager.getInstance().startCamera();
			}

			Camera camera = CameraManager.getInstance().getCamera();
			possibleFaces = getNumberOfCameras(camera);

			CameraManager.getInstance().releaseCamera();
		} catch (Exception exception) {
			Log.e(TAG, "Camera unaccessable!", exception);
		}
		return possibleFaces > 0;
	}

	private static int getNumberOfCameras(Camera camera) {
		if (camera != null && camera.getParameters() != null) {
			return camera.getParameters().getMaxNumDetectedFaces();
		}
		return 0;
	}
}

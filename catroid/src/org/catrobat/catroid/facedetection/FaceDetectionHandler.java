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
package org.catrobat.catroid.facedetection;

import android.hardware.Camera;

import org.catrobat.catroid.camera.VideoDisplayHandler;
import org.catrobat.catroid.formulaeditor.SensorCustomEventListener;

public class FaceDetectionHandler {

	private static FaceDetector faceDetector;
	private static boolean running = false;
	private static boolean paused = false;

	private static void createFaceDetector() {
		if (isIcsFaceDetectionSupported()) {
			faceDetector = new IcsFaceDetector();
		} else {
			faceDetector = new SlowFaceDetector();
		}
	}

	public boolean isFaceDetectionRunning() {
		return running;
	}

	public static boolean startFaceDetection() {
		if (faceDetector == null) {
			createFaceDetector();
			if (faceDetector == null) {
				return false;
			}
		}
		running = true;
		VideoDisplayHandler.startVideoStream();//TODO
		faceDetector.startFaceDetection();
		return true;
	}

	public static void stopFaceDetection() {
		if (!running) {
			return;
		}
		if (faceDetector == null) {
			return;
		}
		VideoDisplayHandler.stopVideoStream();//TODO
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
		if (true) {
			return false; // FIXME just for testing
		}
		int currentApi = android.os.Build.VERSION.SDK_INT;
		if (currentApi < android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			return false;
		}
		int possibleFaces = 0;
		Camera camera = null;
		try {
			camera = Camera.open();
			possibleFaces = camera.getParameters().getMaxNumDetectedFaces();
			camera.release();
		} catch (Exception exc) {
		} finally {
			if (camera != null) {
				camera.release();
			}
		}
		return possibleFaces > 0;
	}
}

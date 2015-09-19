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
package org.catrobat.catroid.test.facedetection;

import android.annotation.TargetApi;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.Face;
import android.os.Build;
import android.test.InstrumentationTestCase;
import android.util.Log;

import org.catrobat.catroid.common.ScreenValues;
import org.catrobat.catroid.facedetection.IcsFaceDetector;
import org.catrobat.catroid.formulaeditor.SensorCustomEvent;
import org.catrobat.catroid.formulaeditor.SensorCustomEventListener;
import org.catrobat.catroid.formulaeditor.Sensors;
import org.catrobat.catroid.uitest.annotation.Device;

import java.util.Random;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class IcsFaceDetectorTest extends InstrumentationTestCase {

	private static final String TAG = IcsFaceDetectorTest.class.getSimpleName();
	private static final int FACE_RECT_SIZE = 2000; // see reference of Camera.Face.rect (1000 - -1000 = 2000)

	private static final int COUNTER_INDEX = 0;
	private static final int SIZE_INDEX = 1;
	private static final int X_POSITION_INDEX = 2;
	private static final int Y_POSITION_INDEX = 3;

	private static final int FACE_LEFT = -20;
	private static final int FACE_RIGHT = 200;
	private static final int FACE_TOP = -100;
	private static final int FACE_BOTTOM = 300;
	private static final int LOW_SCORE_FACE_WIDTH = 10;

	@Override
	public void setUp() throws Exception {
		super.setUp();
		ScreenValues.SCREEN_WIDTH = 720;
		ScreenValues.SCREEN_HEIGHT = 1080;
	}

	// does not run on emulator, and nexus 7
	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	@Device
	public void testDeviceFaceDetectionSupport() {
		if (!isMinApiICS()) {
			Log.w(TAG, "testDeviceFaceDetectionSupport was not performed (higher API level required)");
		}

		int possibleFaces = 0;
		Camera camera = null;

		try {
			camera = Camera.open();
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
				possibleFaces = camera.getParameters().getMaxNumDetectedFaces();
			}
			camera.release();
		} catch (Exception exc) {
			Log.e("ICSFacedetectionTest", Log.getStackTraceString(exc));
		} finally {
			if (camera != null) {
				camera.release();
			}
		}
		if (possibleFaces == 0) {
			Log.w("CAMERA", "The hardware does not support facedetection");
		}
	}

	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	@Device
	public void testNotAvailable() {
		if (!isMinApiICS()) {
			Log.w(TAG, "testNotAvailable was not performed (higher API level required)");
		}

		Camera camera = null;
		try {
			camera = Camera.open();
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
				IcsFaceDetector detector = new IcsFaceDetector();
				boolean success = false;
				if ((camera.getParameters()).getMaxNumDetectedFaces() > 0) {
					success = detector.startFaceDetection();
					assertFalse("IcsFaceDetector should not start if camera not available.", success);
				}
			}
		} catch (Exception exc) {
			fail("Camera not available (" + exc.getMessage() + ")");
		} finally {
			if (camera != null) {
				camera.release();
			}
		}
	}

	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	@Device
	public void testStartAndStop() {
		if (!isMinApiICS()) {
			Log.w(TAG, "testStartAndStop was not performed (higher API level required)");
		}

		Camera camera = null;
		try {
			camera = Camera.open();
		} catch (Exception exc) {
			fail("Camera not available (" + exc.getMessage() + ")");

			if (camera != null) {
				camera.release();
			}
		}

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			int faces = camera.getParameters().getMaxNumDetectedFaces();
			camera.release();
			camera = null;

			if (faces > 0) {
				IcsFaceDetector detector = new IcsFaceDetector();
				assertNotNull("Cannot instantiate IcsFaceDetector", detector);

				try {
					boolean success = detector.startFaceDetection();
					assertTrue("IcsFaceDetector could not start", success);
				} catch (Exception exc) {
					fail("Cannot start face detection (" + exc.getMessage() + ")");
				}

				try {
					detector.stopFaceDetection();
				} catch (Exception exc) {
					fail("Cannot stop face detection (" + exc.getMessage() + ")");
				}

				camera = null;
				try {
					camera = Camera.open();
				} catch (Exception exc) {
					fail("Ressources were not properly released");
				}
			}
			if (camera != null) {
				camera.release();
			}
		}
	}

	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	@Device
	public void testDoubleStart() {
		if (!isMinApiICS()) {
			Log.w(TAG, "testDoubleStart was not performed (higher API level required)");
		}

		Camera camera = null;
		try {
			camera = Camera.open();
		} catch (Exception exc) {
			fail("Camera not available (" + exc.getMessage() + ")");
		}
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			IcsFaceDetector detector = new IcsFaceDetector();
			if ((camera.getParameters()).getMaxNumDetectedFaces() > 0) {
				detector.startFaceDetection();
				try {
					detector.startFaceDetection();
				} catch (Exception e) {
					fail("Second start of face detector should be ignored and not cause errors: " + e.getMessage());
				} finally {
					detector.stopFaceDetection();
					camera.release();
					camera = null;
				}
			}
			if (camera != null) {
				camera.release();
			}
		}
	}

	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	@Device
	public void testOnFaceDetectionStatusListener() {
		if (!isMinApiICS()) {
			Log.w(TAG, "testOnFaceDetectionStatusListener was not performed (higher API level required)");
		}

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			IcsFaceDetector detector = new IcsFaceDetector();
			final float[] detected = new float[1];
			SensorCustomEventListener listener = new SensorCustomEventListener() {
				public void onCustomSensorChanged(SensorCustomEvent event) {
					detected[0] = event.values[0];
				}
			};
			detector.addOnFaceDetectionStatusListener(listener);
			assertEquals("unexpected detection of a face", 0f, detected[0]);

			detector.onFaceDetection(new Face[0], null);
			assertEquals("ICS Face Detector posted wrong status", 0f, detected[0]);
			Face[] faces = new Face[1];
			faces[0] = new Face();
			faces[0].rect = new Rect();
			detector.onFaceDetection(faces, null);
			assertEquals("ICS Face Detector posted wrong status", 1f, detected[0]);
			detector.onFaceDetection(new Face[0], null);
			assertEquals("ICS Face Detector did not post status change", 0f, detected[0]);
		}
	}

	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	@Device
	public void testOnFaceDetectedListener() {
		if (!isMinApiICS()) {
			Log.w(TAG, "testOnFaceDetectedListener was not performed (higher API level required)");
		}

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			IcsFaceDetector detector = new IcsFaceDetector();

			final int[] detectedFaces = new int[4];
			SensorCustomEventListener detectionListener = new SensorCustomEventListener() {

				public void onCustomSensorChanged(SensorCustomEvent event) {
					detectedFaces[COUNTER_INDEX]++;
					int icsValue = (int) event.values[0];
					float intFloatDifference = event.values[0] - icsValue;
					assertEquals("Face detection values should be integer", intFloatDifference, 0f);
					switch (event.sensor) {
						case FACE_X_POSITION:
							detectedFaces[X_POSITION_INDEX] = icsValue;
							break;
						case FACE_Y_POSITION:
							detectedFaces[Y_POSITION_INDEX] = icsValue;
							break;
						case FACE_SIZE:
							detectedFaces[SIZE_INDEX] = icsValue;
							break;
						default:
							fail("Unexpected Sensor on Ics Face Detection event. Expected face size or position."
									+ event.sensor);
					}
				}
			};
			detector.addOnFaceDetectedListener(detectionListener);
			assertEquals("Face Detection Listener receives unexpected calls", 0, detectedFaces[COUNTER_INDEX]);

			Rect faceBounds = new Rect(FACE_LEFT, FACE_TOP, FACE_RIGHT, FACE_BOTTOM);
			Face[] faces = new Face[2];
			faces[0] = new Face();
			faces[0].rect = new Rect(0, 0, LOW_SCORE_FACE_WIDTH, 0);
			faces[0].score = 60;
			faces[1] = new Face();
			faces[1].rect = faceBounds;
			faces[1].score = 80;

			detector.onFaceDetection(faces, null);
			assertEquals("Face Detection Listener does not receive calls", 3, detectedFaces[COUNTER_INDEX]);

			int lowScoreFaceSize = LOW_SCORE_FACE_WIDTH * 100 * 2 / FACE_RECT_SIZE;
			if (detectedFaces[SIZE_INDEX] == lowScoreFaceSize) {
				fail("Wrong face used for face detection");
			}

			int expectedSize = (FACE_RIGHT - FACE_LEFT) * 100 * 2 / FACE_RECT_SIZE;
			assertEquals("Unexpected size of face", expectedSize, detectedFaces[SIZE_INDEX]);

			int expectedXPosition = (FACE_TOP + (FACE_BOTTOM - FACE_TOP) / 2) * ScreenValues.SCREEN_WIDTH * (-1)
					/ FACE_RECT_SIZE;
			assertEquals("Unexpected x position of face", expectedXPosition, detectedFaces[X_POSITION_INDEX]);

			int expectedYPosition = (FACE_LEFT + (FACE_RIGHT - FACE_LEFT) / 2) * ScreenValues.SCREEN_HEIGHT * (-1)
					/ FACE_RECT_SIZE;
			assertEquals("Unexpected y position of face", expectedYPosition, detectedFaces[Y_POSITION_INDEX]);

			detector.onFaceDetection(faces, null);
			assertTrue("Face Detection Listener reveices too many calls", detectedFaces[COUNTER_INDEX] <= 6);
			assertEquals("Face Detection Listener does not receive calls", 6, detectedFaces[COUNTER_INDEX]);

			detector.removeOnFaceDetectedListener(detectionListener);
		}
	}

	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	@Device
	public void testFaceSizeBounds() {
		if (!isMinApiICS()) {
			Log.w(TAG, "testFaceSizeBounds was not performed (higher API level required)");
		}

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			IcsFaceDetector detector = new IcsFaceDetector();

			final float[] faceSize = new float[1];
			SensorCustomEventListener detectionListener = new SensorCustomEventListener() {
				public void onCustomSensorChanged(SensorCustomEvent event) {
					if (event.sensor == Sensors.FACE_SIZE) {
						faceSize[0] = event.values[0];
					}
				}
			};
			detector.addOnFaceDetectedListener(detectionListener);

			Rect faceBounds = new Rect(FACE_LEFT, FACE_TOP, FACE_RIGHT, FACE_BOTTOM);
			Face[] faces = new Face[1];
			faces[0] = new Face();
			faces[0].rect = faceBounds;

			detector.onFaceDetection(faces, null);
			assertTrue("Face size must not be negative", faceSize[0] >= 0);
			assertTrue("Illegal face size, range is [0,100]", faceSize[0] <= 100);

			Random random = new Random();
			int left = random.nextInt(FACE_RECT_SIZE - 1);
			int top = random.nextInt(FACE_RECT_SIZE - 1);
			int right = left + random.nextInt(FACE_RECT_SIZE - left);
			int bottom = top + random.nextInt(FACE_RECT_SIZE - top);
			faceBounds = new Rect(left - FACE_RECT_SIZE / 2, top - FACE_RECT_SIZE / 2, right - FACE_RECT_SIZE / 2, bottom
					- FACE_RECT_SIZE / 2);

			faces[0].rect = faceBounds;
			detector.onFaceDetection(faces, null);
			assertTrue("Face size must not be negative", faceSize[0] >= 0);
			assertTrue("Illegal face size, range is [0,100]", faceSize[0] <= 100);

			detector.removeOnFaceDetectedListener(detectionListener);
		}
	}

	private boolean isMinApiICS() {
		int currentApi = android.os.Build.VERSION.SDK_INT;
		return currentApi >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH;
	}
}

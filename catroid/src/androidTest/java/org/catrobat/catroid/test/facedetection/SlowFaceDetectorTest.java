/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2018 The Catrobat Team
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

import android.graphics.PointF;
import android.hardware.Camera;
import android.support.test.runner.AndroidJUnit4;

import org.catrobat.catroid.common.ScreenValues;
import org.catrobat.catroid.facedetection.SlowFaceDetector;
import org.catrobat.catroid.formulaeditor.SensorCustomEvent;
import org.catrobat.catroid.formulaeditor.SensorCustomEventListener;
import org.catrobat.catroid.formulaeditor.Sensors;
import org.catrobat.catroid.test.utils.Reflection;
import org.catrobat.catroid.test.utils.Reflection.ParameterList;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Random;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

@RunWith(AndroidJUnit4.class)
public class SlowFaceDetectorTest {

	private static final int DETECTION_WIDTH = 400;
	private static final int DETECTION_HEIGHT = 300;
	private static final float EYE_DISTANCE = 4.0f;

	private static final int COUNTER_INDEX = 0;
	private static final int SIZE_INDEX = 1;
	private static final int X_POSITION_INDEX = 2;
	private static final int Y_POSITION_INDEX = 3;

	@Before
	public void setUp() throws Exception {
		ScreenValues.SCREEN_WIDTH = 720;
		ScreenValues.SCREEN_HEIGHT = 1080;
	}

	@Test
	public void testStartAndStop() {

		Camera camera = null;
		try {
			camera = Camera.open();
		} catch (Exception exc) {
			fail("Camera not available (" + exc.getMessage() + ")");
		} finally {
			if (camera != null) {
				camera.release();
			}
		}

		SlowFaceDetector detector = new SlowFaceDetector();
		assertNotNull(detector);

		try {
			detector.startFaceDetection();
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
			fail("Ressources were not propperly released");
		} finally {
			if (camera != null) {
				camera.release();
			}
		}
	}

	@Test
	public void testDoubleStart() {
		SlowFaceDetector detector = new SlowFaceDetector();
		detector.startFaceDetection();
		try {
			detector.startFaceDetection();
		} catch (Exception e) {
			fail("Second start of face detector should be ignored and not cause errors: " + e.getMessage());
		} finally {
			detector.stopFaceDetection();
		}
	}

	@Test
	public void testOnFaceDetectedListener() {
		SlowFaceDetector detector = new SlowFaceDetector();

		final int[] detectedFaces = new int[4];
		SensorCustomEventListener detectionListener = new SensorCustomEventListener() {

			public void onCustomSensorChanged(SensorCustomEvent event) {
				detectedFaces[COUNTER_INDEX]++;
				int value = (int) event.values[0];
				float intFloatDifference = event.values[0] - value;
				assertEquals(intFloatDifference, 0f);
				switch (event.sensor) {
					case FACE_X_POSITION:
						detectedFaces[X_POSITION_INDEX] = value;
						break;
					case FACE_Y_POSITION:
						detectedFaces[Y_POSITION_INDEX] = value;
						break;
					case FACE_SIZE:
						detectedFaces[SIZE_INDEX] = value;
						break;
					default:
						fail("Unexpected Sensor on Face Detection event. Expected face size or position.");
				}
			}
		};
		detector.addOnFaceDetectedListener(detectionListener);
		assertEquals(0, detectedFaces[COUNTER_INDEX]);

		PointF centerPoint = new PointF(DETECTION_WIDTH / 2, DETECTION_HEIGHT / 2);
		ParameterList parameters = new ParameterList(centerPoint, Float.valueOf(EYE_DISTANCE),
				Integer.valueOf(DETECTION_WIDTH), Integer.valueOf(DETECTION_HEIGHT));
		Reflection.invokeMethod(detector, "onFaceFound", parameters);
		assertEquals(3, detectedFaces[COUNTER_INDEX]);

		int expectedSize = (int) (EYE_DISTANCE * 400 / DETECTION_WIDTH);
		assertEquals(expectedSize, detectedFaces[SIZE_INDEX]);

		int expectedXPosition = (int) (centerPoint.x / DETECTION_WIDTH * (-1) * ScreenValues.SCREEN_WIDTH)
				+ ScreenValues.SCREEN_WIDTH / 2;
		assertEquals(expectedXPosition, detectedFaces[X_POSITION_INDEX]);

		int expectedYPosition = (int) (centerPoint.y / DETECTION_HEIGHT * (-1) * ScreenValues.SCREEN_HEIGHT)
				+ ScreenValues.SCREEN_HEIGHT / 2;
		assertEquals(expectedYPosition, detectedFaces[Y_POSITION_INDEX]);

		Reflection.invokeMethod(detector, "onFaceFound", parameters);
		assertTrue(detectedFaces[COUNTER_INDEX] <= 6);
		assertEquals(6, detectedFaces[COUNTER_INDEX]);
	}

	@Test
	public void testFaceSizeBounds() {
		SlowFaceDetector detector = new SlowFaceDetector();

		final float[] faceSize = new float[1];
		SensorCustomEventListener detectionListener = new SensorCustomEventListener() {
			public void onCustomSensorChanged(SensorCustomEvent event) {
				if (event.sensor == Sensors.FACE_SIZE) {
					faceSize[0] = event.values[0];
				}
			}
		};
		detector.addOnFaceDetectedListener(detectionListener);

		ParameterList parameters = new ParameterList(new PointF(), Float.valueOf(EYE_DISTANCE),
				Integer.valueOf(DETECTION_WIDTH), Integer.valueOf(DETECTION_HEIGHT));
		Reflection.invokeMethod(detector, "onFaceFound", parameters);
		assertTrue(faceSize[0] >= 0);
		assertTrue(faceSize[0] <= 100);

		Random random = new Random();
		parameters = new ParameterList(new PointF(), Float.valueOf(random.nextInt(DETECTION_WIDTH)),
				Integer.valueOf(DETECTION_WIDTH), Integer.valueOf(DETECTION_HEIGHT));
		Reflection.invokeMethod(detector, "onFaceFound", parameters);
		assertTrue(faceSize[0] >= 0);
		assertTrue(faceSize[0] <= 100);

		detector.removeOnFaceDetectedListener(detectionListener);
	}
}

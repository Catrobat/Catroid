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

import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.Face;
import android.support.test.runner.AndroidJUnit4;

import org.catrobat.catroid.common.ScreenValues;
import org.catrobat.catroid.facedetection.IcsFaceDetector;
import org.catrobat.catroid.formulaeditor.SensorCustomEvent;
import org.catrobat.catroid.formulaeditor.SensorCustomEventListener;
import org.catrobat.catroid.formulaeditor.Sensors;
import org.catrobat.catroid.uiespresso.annotations.Device;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Random;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.number.OrderingComparison.greaterThanOrEqualTo;
import static org.hamcrest.number.OrderingComparison.lessThanOrEqualTo;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
public class IcsFaceDetectorTest {

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
	private Camera camera;
	private IcsFaceDetector detector;

	@Before
	public void setUp() throws Exception {
		ScreenValues.SCREEN_WIDTH = 720;
		ScreenValues.SCREEN_HEIGHT = 1080;
		camera = Camera.open();
		detector = new IcsFaceDetector();
	}

	@After
	public void tearDown() {
		detector.stopFaceDetection();

		if (camera != null) {
			camera.release();
		}
	}

	@Device
	@Test
	public void testOnFaceDetectionStatusListener() {
		final float[] detected = new float[1];
		SensorCustomEventListener listener = new SensorCustomEventListener() {
			public void onCustomSensorChanged(SensorCustomEvent event) {
				detected[0] = event.values[0];
			}
		};
		detector.addOnFaceDetectionStatusListener(listener);
		assertEquals(0f, detected[0]);

		detector.onFaceDetection(new Face[0], null);
		assertEquals(0f, detected[0]);
		Face[] faces = new Face[1];
		faces[0] = new Face();
		faces[0].rect = new Rect();
		detector.onFaceDetection(faces, null);
		assertEquals(1f, detected[0]);
		detector.onFaceDetection(new Face[0], null);
		assertEquals(0f, detected[0]);
	}

	@Device
	@Test
	public void testOnFaceDetectedListener() {
		final int[] detectedFaces = new int[4];
		SensorCustomEventListener detectionListener = new SensorCustomEventListener() {

			public void onCustomSensorChanged(SensorCustomEvent event) {
				detectedFaces[COUNTER_INDEX]++;
				int icsValue = (int) event.values[0];
				float intFloatDifference = event.values[0] - icsValue;
				assertEquals(intFloatDifference, 0f);
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
		assertEquals(0, detectedFaces[COUNTER_INDEX]);

		Rect faceBounds = new Rect(FACE_LEFT, FACE_TOP, FACE_RIGHT, FACE_BOTTOM);
		Face[] faces = new Face[2];
		faces[0] = new Face();
		faces[0].rect = new Rect(0, 0, LOW_SCORE_FACE_WIDTH, 0);
		faces[0].score = 60;
		faces[1] = new Face();
		faces[1].rect = faceBounds;
		faces[1].score = 80;

		detector.onFaceDetection(faces, null);
		assertEquals(3, detectedFaces[COUNTER_INDEX]);

		assertThat(LOW_SCORE_FACE_WIDTH * 100 * 2 / FACE_RECT_SIZE, is(not(detectedFaces[SIZE_INDEX])));

		int expectedSize = (FACE_RIGHT - FACE_LEFT) * 100 * 2 / FACE_RECT_SIZE;
		assertEquals(expectedSize, detectedFaces[SIZE_INDEX]);

		int expectedXPosition = Math.abs((FACE_TOP + (FACE_BOTTOM - FACE_TOP) / 2) * ScreenValues.SCREEN_WIDTH
				/ FACE_RECT_SIZE);
		assertEquals(expectedXPosition, Math.abs(detectedFaces[X_POSITION_INDEX]));

		int expectedYPosition = Math.abs((FACE_LEFT + (FACE_RIGHT - FACE_LEFT) / 2) * ScreenValues.SCREEN_HEIGHT
				/ FACE_RECT_SIZE);
		assertEquals(expectedYPosition, Math.abs(detectedFaces[Y_POSITION_INDEX]));

		detector.onFaceDetection(faces, null);

		assertEquals(6, detectedFaces[COUNTER_INDEX]);

		detector.removeOnFaceDetectedListener(detectionListener);
	}

	@Device
	@Test
	public void testFaceSizeBounds() {
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
		assertThat(faceSize[0], allOf(greaterThanOrEqualTo(0f), lessThanOrEqualTo(100f)));

		Random random = new Random();
		int left = random.nextInt(FACE_RECT_SIZE - 1);
		int top = random.nextInt(FACE_RECT_SIZE - 1);
		int right = left + random.nextInt(FACE_RECT_SIZE - left);
		int bottom = top + random.nextInt(FACE_RECT_SIZE - top);
		faceBounds = new Rect(left - FACE_RECT_SIZE / 2, top - FACE_RECT_SIZE / 2, right - FACE_RECT_SIZE / 2, bottom
				- FACE_RECT_SIZE / 2);

		faces[0].rect = faceBounds;
		detector.onFaceDetection(faces, null);
		assertThat(faceSize[0], allOf(greaterThanOrEqualTo(0f), lessThanOrEqualTo(100f)));

		detector.removeOnFaceDetectedListener(detectionListener);
	}
}

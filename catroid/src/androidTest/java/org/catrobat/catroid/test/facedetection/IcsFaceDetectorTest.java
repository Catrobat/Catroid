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
import org.catrobat.catroid.uiespresso.annotations.Device;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.List;
import java.util.Random;

import static junit.framework.Assert.assertEquals;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.number.OrderingComparison.greaterThanOrEqualTo;
import static org.hamcrest.number.OrderingComparison.lessThanOrEqualTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@RunWith(AndroidJUnit4.class)
public class IcsFaceDetectorTest {

	@Rule
	public MockitoRule rule = MockitoJUnit.rule();

	@Captor
	private ArgumentCaptor<SensorCustomEvent> captor;

	private static final int FACE_RECT_SIZE = 2000; // see reference of Camera.Face.rect (1000 - -1000 = 2000)

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
		SensorCustomEventListener mockedListener = Mockito.mock(SensorCustomEventListener.class);
		detector.addOnFaceDetectedListener(mockedListener);

		Rect faceBounds = new Rect(FACE_LEFT, FACE_TOP, FACE_RIGHT, FACE_BOTTOM);
		Face[] faces = new Face[2];
		faces[0] = new Face();
		faces[0].rect = new Rect(0, 0, LOW_SCORE_FACE_WIDTH, 0);
		faces[0].score = 60;
		faces[1] = new Face();
		faces[1].rect = faceBounds;
		faces[1].score = 80;

		detector.onFaceDetection(faces, null);
		verify(mockedListener, times(3)).onCustomSensorChanged(captor.capture());
		List<SensorCustomEvent> capturedEvents = captor.getAllValues();

		assertThat(LOW_SCORE_FACE_WIDTH * 100 * 2 / FACE_RECT_SIZE, is(not((int) capturedEvents.get(2).values[0])));

		float expectedSize = (FACE_RIGHT - FACE_LEFT) * 100 * 2 / FACE_RECT_SIZE;
		assertEquals(expectedSize, capturedEvents.get(2).values[0]);

		float expectedXPosition = Math.abs((FACE_TOP + (FACE_BOTTOM - FACE_TOP) / 2) * ScreenValues.SCREEN_WIDTH
				/ FACE_RECT_SIZE);
		assertEquals(expectedXPosition, Math.abs(capturedEvents.get(0).values[0]));

		float expectedYPosition = Math.abs((FACE_LEFT + (FACE_RIGHT - FACE_LEFT) / 2) * ScreenValues.SCREEN_HEIGHT
				/ FACE_RECT_SIZE);
		assertEquals(expectedYPosition, Math.abs(capturedEvents.get(1).values[0]));

		detector.onFaceDetection(faces, null);
		verify(mockedListener, times(6)).onCustomSensorChanged(captor.capture());

		detector.removeOnFaceDetectedListener(mockedListener);
	}

	@Device
	@Test
	public void testFaceSizeBounds() {
		SensorCustomEventListener mockedListener = Mockito.mock(SensorCustomEventListener.class);
		detector.addOnFaceDetectedListener(mockedListener);

		Rect faceBounds = new Rect(FACE_LEFT, FACE_TOP, FACE_RIGHT, FACE_BOTTOM);
		Face[] faces = new Face[1];
		faces[0] = new Face();
		faces[0].rect = faceBounds;

		detector.onFaceDetection(faces, null);
		verify(mockedListener, times(3)).onCustomSensorChanged(captor.capture());
		List<SensorCustomEvent> capturedEvents = captor.getAllValues();

		assertThat(capturedEvents.get(1).values[0], allOf(greaterThanOrEqualTo(0f), lessThanOrEqualTo(100f)));

		Random random = new Random();
		int left = random.nextInt(FACE_RECT_SIZE - 1);
		int top = random.nextInt(FACE_RECT_SIZE - 1);
		int right = left + random.nextInt(FACE_RECT_SIZE - left);
		int bottom = top + random.nextInt(FACE_RECT_SIZE - top);
		faceBounds = new Rect(left - FACE_RECT_SIZE / 2, top - FACE_RECT_SIZE / 2, right - FACE_RECT_SIZE / 2, bottom
				- FACE_RECT_SIZE / 2);

		faces[0].rect = faceBounds;

		detector.onFaceDetection(faces, null);
		verify(mockedListener, times(6)).onCustomSensorChanged(captor.capture());
		capturedEvents = captor.getAllValues();

		assertThat(capturedEvents.get(1).values[0], allOf(greaterThanOrEqualTo(0f), lessThanOrEqualTo(100f)));

		detector.removeOnFaceDetectedListener(mockedListener);
	}
}

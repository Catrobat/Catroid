/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2019 The Catrobat Team
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
import android.support.test.rule.GrantPermissionRule;
import android.support.test.runner.AndroidJUnit4;

import org.catrobat.catroid.camera.CameraManager;
import org.catrobat.catroid.common.ScreenValues;
import org.catrobat.catroid.facedetection.SlowFaceDetector;
import org.catrobat.catroid.formulaeditor.SensorCustomEvent;
import org.catrobat.catroid.formulaeditor.SensorCustomEventListener;
import org.catrobat.catroid.formulaeditor.Sensors;
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
import static org.hamcrest.number.OrderingComparison.greaterThanOrEqualTo;
import static org.hamcrest.number.OrderingComparison.lessThanOrEqualTo;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@RunWith(AndroidJUnit4.class)
public class SlowFaceDetectorTest {

	@Rule
	public MockitoRule rule = MockitoJUnit.rule();

	@Rule
	public GrantPermissionRule runtimePermissionRule = GrantPermissionRule.grant(android.Manifest.permission.CAMERA);

	@Captor
	private ArgumentCaptor<SensorCustomEvent> captor;

	private static final int DETECTION_WIDTH = 400;
	private static final int DETECTION_HEIGHT = 300;
	private static final float EYE_DISTANCE = 4.0f;

	private SlowFaceDetector detector;

	@Before
	public void setUp() throws Exception {
		CameraManager.makeInstance();
		ScreenValues.SCREEN_WIDTH = 720;
		ScreenValues.SCREEN_HEIGHT = 1080;
		detector = new SlowFaceDetector();
	}

	@After
	public void tearDown() {
		detector.stopFaceDetection();
	}

	@Test
	public void testDoubleStart() {
		detector.startFaceDetection();
		detector.startFaceDetection();
	}

	@Test
	public void testOnFaceDetectedListener() {
		SensorCustomEventListener mockedListener = Mockito.mock(SensorCustomEventListener.class);
		detector.addOnFaceDetectedListener(mockedListener);
		PointF centerPoint = new PointF(DETECTION_WIDTH / 2, DETECTION_HEIGHT / 2);
		detector.callOnFaceFound(centerPoint, EYE_DISTANCE, DETECTION_WIDTH, DETECTION_HEIGHT);

		verify(mockedListener, times(3)).onCustomSensorChanged(captor.capture());
		List<SensorCustomEvent> capturedEvents = captor.getAllValues();

		float expectedSize = (int) (EYE_DISTANCE * 400 / DETECTION_WIDTH);
		assertEquals(expectedSize, capturedEvents.get(2).values[0]);
		assertEquals(Sensors.FACE_SIZE, capturedEvents.get(2).sensor);

		float expectedXPosition = (int) (centerPoint.x / DETECTION_WIDTH * (-1) * ScreenValues.SCREEN_WIDTH)
				+ ScreenValues.SCREEN_WIDTH / 2;
		assertEquals(expectedXPosition, capturedEvents.get(0).values[0]);
		assertEquals(Sensors.FACE_X_POSITION, capturedEvents.get(0).sensor);

		float expectedYPosition = (int) (centerPoint.y / DETECTION_HEIGHT * (-1) * ScreenValues.SCREEN_HEIGHT)
				+ ScreenValues.SCREEN_HEIGHT / 2;
		assertEquals(expectedYPosition, capturedEvents.get(1).values[0]);
		assertEquals(Sensors.FACE_Y_POSITION, capturedEvents.get(1).sensor);

		detector.callOnFaceFound(centerPoint, EYE_DISTANCE, DETECTION_WIDTH, DETECTION_HEIGHT);
		verify(mockedListener, times(6)).onCustomSensorChanged(any(SensorCustomEvent.class));

		detector.removeOnFaceDetectedListener(mockedListener);
	}

	@Test
	public void testFaceSizeBounds() {
		SensorCustomEventListener mockedListener = Mockito.mock(SensorCustomEventListener.class);
		detector.addOnFaceDetectedListener(mockedListener);

		detector.callOnFaceFound(new PointF(), EYE_DISTANCE,
				DETECTION_WIDTH, DETECTION_HEIGHT);

		verify(mockedListener, times(3)).onCustomSensorChanged(captor.capture());
		List<SensorCustomEvent> capturedEvents = captor.getAllValues();

		assertThat(capturedEvents.get(2).values[0], allOf(greaterThanOrEqualTo(0f), lessThanOrEqualTo(100f)));

		Random random = new Random();
		detector.callOnFaceFound(new PointF(), random.nextInt(DETECTION_WIDTH),
				DETECTION_WIDTH, DETECTION_HEIGHT);
		verify(mockedListener, times(6)).onCustomSensorChanged(captor.capture());
		capturedEvents = captor.getAllValues();
		assertThat(capturedEvents.get(2).values[0], allOf(greaterThanOrEqualTo(0f), lessThanOrEqualTo(100f)));

		detector.removeOnFaceDetectedListener(mockedListener);
	}
}

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

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.catrobat.catroid.facedetection.FaceDetector;
import org.catrobat.catroid.formulaeditor.SensorCustomEvent;
import org.catrobat.catroid.formulaeditor.SensorCustomEventListener;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import static junit.framework.Assert.assertEquals;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@RunWith(AndroidJUnit4.class)
public class FaceDetectorTest {
	@Rule
	public MockitoRule rule = MockitoJUnit.rule();

	@Captor
	private ArgumentCaptor<SensorCustomEvent> captor;

	@Test
	public void testStatusListenerCallback() {
		FaceDetector detector = new FaceDetector() {
			@Override
			public boolean startFaceDetection() {
				return true;
			}

			@Override
			public void stopFaceDetection() {
			}
		};

		SensorCustomEventListener onFaceDetectionStatusListener = Mockito.mock(SensorCustomEventListener.class);

		detector.addOnFaceDetectionStatusListener(onFaceDetectionStatusListener);
		verifyNoMoreInteractions(onFaceDetectionStatusListener);

		detector.callOnFaceDetected(false);
		verifyNoMoreInteractions(onFaceDetectionStatusListener);

		detector.callOnFaceDetected(true);
		verify(onFaceDetectionStatusListener).onCustomSensorChanged(captor.capture());
		assertEquals(1.0f, captor.getValue().values[0]);
		verifyNoMoreInteractions(onFaceDetectionStatusListener);

		detector.callOnFaceDetected(true);
		verify(onFaceDetectionStatusListener).onCustomSensorChanged(captor.capture());
		assertEquals(1.0f, captor.getValue().values[0]);
		verifyNoMoreInteractions(onFaceDetectionStatusListener);

		detector.callOnFaceDetected(false);
		verify(onFaceDetectionStatusListener, times(2)).onCustomSensorChanged(captor.capture());
		assertEquals(0.0f, captor.getValue().values[0]);
		verifyNoMoreInteractions(onFaceDetectionStatusListener);
	}
}

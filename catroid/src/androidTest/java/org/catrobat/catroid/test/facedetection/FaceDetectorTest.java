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

import android.support.test.runner.AndroidJUnit4;

import org.catrobat.catroid.formulaeditor.SensorCustomEvent;
import org.catrobat.catroid.formulaeditor.SensorCustomEventListener;
import org.catrobat.catroid.test.utils.TestFaceDetector;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

@RunWith(AndroidJUnit4.class)
public class FaceDetectorTest {

	private int numberOfCalls = 0;
	private boolean statusFaceDetected = false;

	private SensorCustomEventListener onFaceDetectionStatusListener = new SensorCustomEventListener() {

		public void onCustomSensorChanged(SensorCustomEvent event) {
			numberOfCalls++;
			if (event.values[0] == 1.0f) {
				statusFaceDetected = true;
			} else if (event.values[0] == 0.0f) {
				statusFaceDetected = false;
			} else {
				fail("Unexpected value for face detected. Should be 1 for \"detected\" or 0 for \"not detected\".");
			}
		}
	};

	@Test
	public void testStatusListenerCallback() {
		TestFaceDetector detector = new TestFaceDetector();
		numberOfCalls = 0;
		statusFaceDetected = false;
		detector.addOnFaceDetectionStatusListener(onFaceDetectionStatusListener);
		assertEquals(0, numberOfCalls);
		detector.sendFaceDetected(false);
		assertEquals(0, numberOfCalls);
		assertFalse(statusFaceDetected);
		detector.sendFaceDetected(true);
		assertTrue(statusFaceDetected);
		assertTrue(numberOfCalls <= 1);
		assertEquals(1, numberOfCalls);
		detector.sendFaceDetected(true);
		assertEquals(1, numberOfCalls);
		detector.sendFaceDetected(false);
		assertEquals(2, numberOfCalls);
		assertFalse(statusFaceDetected);
	}
}

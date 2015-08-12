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

import junit.framework.TestCase;

import org.catrobat.catroid.formulaeditor.SensorCustomEvent;
import org.catrobat.catroid.formulaeditor.SensorCustomEventListener;
import org.catrobat.catroid.test.utils.TestFaceDetector;

public class FaceDetectorTest extends TestCase {

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

	public void testStatusListenerCallback() {
		TestFaceDetector detector = new TestFaceDetector();
		numberOfCalls = 0;
		statusFaceDetected = false;
		detector.addOnFaceDetectionStatusListener(onFaceDetectionStatusListener);
		assertEquals("Status Listener received unexpected calls", 0, numberOfCalls);
		detector.sendFaceDetected(false);
		assertEquals("Status Listener received call although still no face detected", 0, numberOfCalls);
		assertFalse("Wrong detection status", statusFaceDetected);
		detector.sendFaceDetected(true);
		assertTrue("Status Listener received the wrong status", statusFaceDetected);
		assertTrue("Status Listener received too many calls for one event", numberOfCalls <= 1);
		assertEquals("Status Listener did not receive a call although face detected", 1, numberOfCalls);
		detector.sendFaceDetected(true);
		assertEquals("Status Listener received a call although status did not change", 1, numberOfCalls);
		detector.sendFaceDetected(false);
		assertEquals("Status Listener did not receive exactly one call for a change", 2, numberOfCalls);
		assertFalse("Status Listener received the wrong status", statusFaceDetected);
	}
}

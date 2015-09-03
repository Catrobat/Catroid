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

import org.catrobat.catroid.facedetection.FaceDetectionHandler;
import org.catrobat.catroid.test.utils.Reflection;
import org.catrobat.catroid.test.utils.TestFaceDetector;

public class FaceDetectionHandlerTest extends TestCase {

	public void testResume() {
		TestFaceDetector detector = new TestFaceDetector();
		Reflection.setPrivateField(FaceDetectionHandler.class, "faceDetector", detector);
		assertFalse("Unexpected start of FaceDetector", detector.started);

		FaceDetectionHandler.resumeFaceDetection();
		assertFalse("FaceDetector could be resumed although it was never started", detector.started);

		FaceDetectionHandler.startFaceDetection(null);
		assertTrue("FaceDetector was not started", detector.started);

		FaceDetectionHandler.pauseFaceDetection();
		assertFalse("FaceDetector was not stopped on pause call", detector.started);

		FaceDetectionHandler.resumeFaceDetection();
		assertTrue("FaceDetector was not resumed", detector.started);

		FaceDetectionHandler.stopFaceDetection();
		assertFalse("FaceDetector was not stopped", detector.started);

		FaceDetectionHandler.resumeFaceDetection();
		assertFalse("FaceDetector could be resumed although it was stopped", detector.started);
	}
}

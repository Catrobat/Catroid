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

import org.catrobat.catroid.facedetection.FaceDetectionHandler;
import org.catrobat.catroid.test.utils.Reflection;
import org.catrobat.catroid.test.utils.TestFaceDetector;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class FaceDetectionHandlerTest {

	@Test
	public void testResume() throws Exception {
		TestFaceDetector detector = new TestFaceDetector();
		Reflection.setPrivateField(FaceDetectionHandler.class, "faceDetector", detector);
		assertFalse(detector.started);

		FaceDetectionHandler.resumeFaceDetection();
		assertFalse(detector.started);

		FaceDetectionHandler.startFaceDetection();
		assertTrue(detector.started);

		FaceDetectionHandler.pauseFaceDetection();
		assertFalse(detector.started);

		FaceDetectionHandler.resumeFaceDetection();
		assertTrue(detector.started);

		FaceDetectionHandler.stopFaceDetection();
		assertFalse(detector.started);

		FaceDetectionHandler.resumeFaceDetection();
		assertFalse(detector.started);
	}
}

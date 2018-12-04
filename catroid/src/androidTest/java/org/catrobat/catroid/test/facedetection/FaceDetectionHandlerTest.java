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

import org.catrobat.catroid.camera.CameraManager;
import org.catrobat.catroid.facedetection.FaceDetectionHandler;
import org.catrobat.catroid.facedetection.FaceDetector;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mockito;

import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class)
public class FaceDetectionHandlerTest {

	@After
	public void tearDown() {
		FaceDetectionHandler.setFaceDetector(null);
	}

	@Test
	public void testResume() {
		CameraManager.makeInstance();
		FaceDetector detector = Mockito.mock(FaceDetector.class);
		when(detector.startFaceDetection()).thenReturn(true);
		InOrder inOrder = inOrder(detector);
		FaceDetectionHandler.setFaceDetector(detector);

		FaceDetectionHandler.resumeFaceDetection();
		verifyNoMoreInteractions(detector);

		FaceDetectionHandler.startFaceDetection();
		inOrder.verify(detector).startFaceDetection();
		verifyNoMoreInteractions(detector);

		FaceDetectionHandler.pauseFaceDetection();
		inOrder.verify(detector).stopFaceDetection();
		verifyNoMoreInteractions(detector);

		FaceDetectionHandler.resumeFaceDetection();
		inOrder.verify(detector).startFaceDetection();
		verifyNoMoreInteractions(detector);

		FaceDetectionHandler.stopFaceDetection();
		inOrder.verify(detector).stopFaceDetection();
		verifyNoMoreInteractions(detector);

		FaceDetectionHandler.resumeFaceDetection();
		verifyNoMoreInteractions(detector);
	}
}

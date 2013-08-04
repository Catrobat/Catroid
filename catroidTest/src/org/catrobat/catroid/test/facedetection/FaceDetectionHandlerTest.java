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

		FaceDetectionHandler.startFaceDetection();
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

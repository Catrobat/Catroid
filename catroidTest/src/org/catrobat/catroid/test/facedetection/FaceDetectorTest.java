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

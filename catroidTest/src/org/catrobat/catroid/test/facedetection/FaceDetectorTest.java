package org.catrobat.catroid.test.facedetection;

import junit.framework.TestCase;

import org.catrobat.catroid.facedetection.FaceDetector;
import org.catrobat.catroid.facedetection.OnFaceDetectedListener;
import org.catrobat.catroid.facedetection.OnFaceDetectionStatusChangedListener;

import android.graphics.Point;

public class FaceDetectorTest extends TestCase {

	private int numberOfCalls = 0;
	private boolean statusFaceDetected = false;

	private OnFaceDetectedListener onFaceDetectedListener = new OnFaceDetectedListener() {

		public void onFaceDetected(Point position, int size) {
		}
	};
	private OnFaceDetectionStatusChangedListener onFaceDetectionStatusListener = new OnFaceDetectionStatusChangedListener() {

		public void onFaceDetectionStatusChanged(boolean faceDetected) {
			numberOfCalls++;
			statusFaceDetected = faceDetected;
		}
	};

	public void testAutoStartAndStop() {
		TestFaceDetector detector = new TestFaceDetector();
		assertFalse("Unexpected start of FaceDetector", detector.started);
		detector.addOnFaceDetectedListener(null);
		detector.addOnFaceDetectionStatusListener(null);
		assertFalse("FaceDetector was started from null listener", detector.started);
		detector.addOnFaceDetectedListener(onFaceDetectedListener);
		assertTrue("FaceDetector was not started when listener was added", detector.started);
		detector.removeOnFaceDetectedListener(onFaceDetectedListener);
		assertFalse("FaceDetector was not stoped when only listener removed", detector.started);
		detector.addOnFaceDetectionStatusListener(onFaceDetectionStatusListener);
		assertTrue("FaceDetector was not started when status listener was added", detector.started);
		detector.removeOnFaceDetectionStatusListener(onFaceDetectionStatusListener);
		assertFalse("FaceDetector was not stoped when only listener removed", detector.started);
		detector.addOnFaceDetectedListener(onFaceDetectedListener);
		detector.addOnFaceDetectionStatusListener(onFaceDetectionStatusListener);
		assertTrue("FaceDetector was not started when listeners were added", detector.started);
		detector.removeAllListeners();
		assertFalse("FaceDetector was not stoped when all listeners were removed", detector.started);
	}

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

	private class TestFaceDetector extends FaceDetector {

		public boolean started = false;

		@Override
		public void startFaceDetection() {
			started = true;
		}

		@Override
		public void stopFaceDetection() {
			started = false;
		}

		public void sendFaceDetected(boolean detected) {
			this.onFaceDetected(detected);
		}

	}

}

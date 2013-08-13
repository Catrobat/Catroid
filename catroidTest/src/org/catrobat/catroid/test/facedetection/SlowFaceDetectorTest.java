package org.catrobat.catroid.test.facedetection;

import android.graphics.PointF;
import android.hardware.Camera;
import android.test.InstrumentationTestCase;

import org.catrobat.catroid.common.ScreenValues;
import org.catrobat.catroid.facedetection.SlowFaceDetector;
import org.catrobat.catroid.formulaeditor.SensorCustomEvent;
import org.catrobat.catroid.formulaeditor.SensorCustomEventListener;
import org.catrobat.catroid.formulaeditor.Sensors;
import org.catrobat.catroid.test.utils.Reflection;
import org.catrobat.catroid.test.utils.Reflection.ParameterList;

import java.util.Random;

public class SlowFaceDetectorTest extends InstrumentationTestCase {

	private static final int DETECTION_WIDTH = 400;
	private static final int DETECTION_HEIGHT = 300;
	private static final float EYE_DISTANCE = 4.0f;

	private static final int COUNTER_INDEX = 0;
	private static final int SIZE_INDEX = 1;
	private static final int X_POSITION_INDEX = 2;
	private static final int Y_POSITION_INDEX = 3;

	@Override
	public void setUp() throws Exception {
		super.setUp();
		ScreenValues.SCREEN_WIDTH = 720;
		ScreenValues.SCREEN_HEIGHT = 1080;
	}

	public void testStartAndStop() {

		Camera camera = null;
		try {
			camera = Camera.open();
		} catch (Exception exc) {
			fail("Camera not available (" + exc.getMessage() + ")");
		} finally {
			if (camera != null) {
				camera.release();
			}
		}

		SlowFaceDetector detector = new SlowFaceDetector();
		assertNotNull("Cannot instantiate SlowFaceDetector", detector);

		try {
			detector.startFaceDetection();
		} catch (Exception exc) {
			fail("Cannot start face detection (" + exc.getMessage() + ")");
		}

		try {
			detector.stopFaceDetection();
		} catch (Exception exc) {
			fail("Cannot stop face detection (" + exc.getMessage() + ")");
		}

		camera = null;
		try {
			camera = Camera.open();
		} catch (Exception exc) {
			fail("Ressources were not propperly released");
		} finally {
			if (camera != null) {
				camera.release();
			}
		}
	}

	public void testDoubleStart() {
		SlowFaceDetector detector = new SlowFaceDetector();
		detector.startFaceDetection();
		try {
			detector.startFaceDetection();
		} catch (Exception e) {
			fail("Second start of face detector should be ignored and not cause errors: " + e.getMessage());
		} finally {
			detector.stopFaceDetection();
		}
	}

	public void testOnFaceDetectedListener() {
		SlowFaceDetector detector = new SlowFaceDetector();

		final int[] detectedFaces = new int[4];
		SensorCustomEventListener detectionListener = new SensorCustomEventListener() {

			public void onCustomSensorChanged(SensorCustomEvent event) {
				detectedFaces[COUNTER_INDEX]++;
				int value = (int) event.values[0];
				float intFloatDifference = event.values[0] - value;
				assertEquals("Face detection values should be integer", intFloatDifference, 0f);
				switch (event.sensor) {
					case FACE_X_POSITION:
						detectedFaces[X_POSITION_INDEX] = value;
						break;
					case FACE_Y_POSITION:
						detectedFaces[Y_POSITION_INDEX] = value;
						break;
					case FACE_SIZE:
						detectedFaces[SIZE_INDEX] = value;
						break;
					default:
						fail("Unexpected Sensor on Face Detection event. Expected face size or position.");
				}
			}
		};
		detector.addOnFaceDetectedListener(detectionListener);
		assertEquals("Face Detection Listener receives unexpected calls", 0, detectedFaces[COUNTER_INDEX]);

		PointF centerPoint = new PointF(DETECTION_WIDTH / 2, DETECTION_HEIGHT / 2);
		ParameterList parameters = new ParameterList(centerPoint, Float.valueOf(EYE_DISTANCE),
				Integer.valueOf(DETECTION_WIDTH), Integer.valueOf(DETECTION_HEIGHT));
		Reflection.invokeMethod(detector, "onFaceFound", parameters);
		assertEquals("Face Detection Listener does not receive calls", 3, detectedFaces[COUNTER_INDEX]);

		int expectedSize = (int) (EYE_DISTANCE * 400 / DETECTION_WIDTH);
		assertEquals("Unexpected size of face", expectedSize, detectedFaces[SIZE_INDEX]);

		int expectedXPosition = (int) (centerPoint.x / DETECTION_WIDTH * (-1) * ScreenValues.SCREEN_WIDTH)
				+ ScreenValues.SCREEN_WIDTH / 2;
		assertEquals("Unexpected x position of face", expectedXPosition, detectedFaces[X_POSITION_INDEX]);

		int expectedYPosition = (int) (centerPoint.y / DETECTION_HEIGHT * (-1) * ScreenValues.SCREEN_HEIGHT)
				+ ScreenValues.SCREEN_HEIGHT / 2;
		assertEquals("Unexpected y position of face", expectedYPosition, detectedFaces[Y_POSITION_INDEX]);

		Reflection.invokeMethod(detector, "onFaceFound", parameters);
		assertTrue("Face Detection Listener reveices too many calls", detectedFaces[COUNTER_INDEX] <= 6);
		assertEquals("Face Detection Listener does not receive calls", 6, detectedFaces[COUNTER_INDEX]);

	}

	public void testFaceSizeBounds() {
		SlowFaceDetector detector = new SlowFaceDetector();

		final float[] faceSize = new float[1];
		SensorCustomEventListener detectionListener = new SensorCustomEventListener() {
			public void onCustomSensorChanged(SensorCustomEvent event) {
				if (event.sensor == Sensors.FACE_SIZE) {
					faceSize[0] = event.values[0];
				}
			}
		};
		detector.addOnFaceDetectedListener(detectionListener);

		ParameterList parameters = new ParameterList(new PointF(), Float.valueOf(EYE_DISTANCE),
				Integer.valueOf(DETECTION_WIDTH), Integer.valueOf(DETECTION_HEIGHT));
		Reflection.invokeMethod(detector, "onFaceFound", parameters);
		assertTrue("Face size must not be negative", faceSize[0] >= 0);
		assertTrue("Illegal face size, range is [0,100]", faceSize[0] <= 100);

		Random random = new Random();
		parameters = new ParameterList(new PointF(), Float.valueOf(random.nextInt(DETECTION_WIDTH)),
				Integer.valueOf(DETECTION_WIDTH), Integer.valueOf(DETECTION_HEIGHT));
		Reflection.invokeMethod(detector, "onFaceFound", parameters);
		assertTrue("Face size must not be negative", faceSize[0] >= 0);
		assertTrue("Illegal face size, range is [0,100]", faceSize[0] <= 100);

		detector.removeOnFaceDetectedListener(detectionListener);
	}

	//	public void testCameraPreviewCallback() {
	//		SlowFaceDetectionTester testFaceDetector = new SlowFaceDetectionTester();
	//		assertFalse("Wrong tester setup", testFaceDetector.receivedFrame);
	//
	//		testFaceDetector.startFaceDetection();
	//		try {
	//			Thread.sleep(10000);
	//		} catch (InterruptedException e) {
	//			testFaceDetector.stopFaceDetection();
	//			fail("Thread interrupted unexpectedly");
	//		}
	//		testFaceDetector.stopFaceDetection();
	//		assertTrue("No preview frame received from camera", testFaceDetector.receivedFrame);
	//	}
	//
	//	private class SlowFaceDetectionTester extends SlowFaceDetector {
	//
	//		public boolean receivedFrame = false;
	//
	//		@Override
	//		public void onPreviewFrame(byte[] data, Camera camera) {
	//			receivedFrame = true;
	//			assertNotNull("No data in preview frame", data);
	//			this.notify();
	//		}
	//
	//	}

}

package org.catrobat.catroid.test.facedetection;

import java.util.Random;

import org.catrobat.catroid.common.ScreenValues;
import org.catrobat.catroid.facedetection.IcsFaceDetector;
import org.catrobat.catroid.facedetection.OnFaceDetectedListener;
import org.catrobat.catroid.facedetection.OnFaceDetectionStatusChangedListener;
import org.catrobat.catroid.test.utils.Reflection;

import android.annotation.TargetApi;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.Face;
import android.os.Build;
import android.test.InstrumentationTestCase;

public class IcsFaceDetectorTest extends InstrumentationTestCase {

	private static final int FACE_RECT_SIZE = 2000; // see reference of Camera.Face.rect (1000 - -1000 = 2000)

	private static final int COUNTER_INDEX = 0;
	private static final int SIZE_INDEX = 1;
	private static final int X_POSITION_INDEX = 2;
	private static final int Y_POSITION_INDEX = 3;

	private static final int FACE_LEFT = -20;
	private static final int FACE_RIGHT = 200;
	private static final int FACE_TOP = -100;
	private static final int FACE_BOTTOM = 300;
	private static final int LOW_SCORE_FACE_WIDTH = 10;

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

		IcsFaceDetector detector = new IcsFaceDetector();
		assertNotNull("Cannot instantiate IcsFaceDetector", detector);

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

	public void testAutoStartAndAutoStop() {
		IcsFaceDetector detector = new IcsFaceDetector();

		OnFaceDetectedListener detectionListener = new OnFaceDetectedListener() {

			public void onFaceDetected(Point position, int size) {
			}
		};

		assertNull("Face Detector started unnesesarily", Reflection.getPrivateField(detector, "camera"));

		detector.addOnFaceDetectedListener(detectionListener);

		assertNotNull("Face Detector did not start when listener was added",
				Reflection.getPrivateField(detector, "camera"));

		detector.removeOnFaceDetectedListener(detectionListener);

		assertNull("Face Detector did not stop afer all listeners were removed",
				Reflection.getPrivateField(detector, "camera"));

	}

	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	public void testOnFaceDetectionStatusListener() {
		IcsFaceDetector detector = new IcsFaceDetector();
		final boolean[] detected = new boolean[1];
		OnFaceDetectionStatusChangedListener listener = new OnFaceDetectionStatusChangedListener() {

			public void onFaceDetectionStatusChanged(boolean faceDetected) {
				detected[0] = faceDetected;
			}
		};
		detector.addOnFaceDetectionStatusListener(listener);
		assertFalse("unexpected detection of a face", detected[0]);

		detector.onFaceDetection(new Face[0], null);
		assertFalse("ICS Face Detector posted wrong status", detected[0]);
		Face[] faces = new Face[1];
		faces[0] = new Face();
		faces[0].rect = new Rect();
		detector.onFaceDetection(faces, null);
		assertTrue("ICS Face Detector posted wrong status", detected[0]);
		detector.onFaceDetection(new Face[0], null);
		assertFalse("ICS Face Detector did not post status change", detected[0]);
	}

	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	public void testOnFaceDetectedListener() {
		IcsFaceDetector detector = new IcsFaceDetector();

		final int[] detectedFaces = new int[4];
		OnFaceDetectedListener detectionListener = new OnFaceDetectedListener() {

			public void onFaceDetected(Point position, int size) {
				detectedFaces[COUNTER_INDEX]++;
				detectedFaces[X_POSITION_INDEX] = position.x;
				detectedFaces[Y_POSITION_INDEX] = position.y;
				detectedFaces[SIZE_INDEX] = size;
			}
		};
		detector.addOnFaceDetectedListener(detectionListener);
		assertEquals("Face Detection Listener receives unexpected calls", 0, detectedFaces[COUNTER_INDEX]);

		Rect faceBounds = new Rect(FACE_LEFT, FACE_TOP, FACE_RIGHT, FACE_BOTTOM);
		Face[] faces = new Face[2];
		faces[0] = new Face();
		faces[0].rect = new Rect(0, 0, LOW_SCORE_FACE_WIDTH, 0);
		faces[0].score = 60;
		faces[1] = new Face();
		faces[1].rect = faceBounds;
		faces[1].score = 80;

		detector.onFaceDetection(faces, null);
		assertEquals("Face Detection Listener does not receive call", 1, detectedFaces[COUNTER_INDEX]);

		int lowScoreFaceSize = LOW_SCORE_FACE_WIDTH * 100 * 2 / FACE_RECT_SIZE;
		if (detectedFaces[SIZE_INDEX] == lowScoreFaceSize) {
			fail("Wrong face used for face detection");
		}

		int expectedSize = (FACE_RIGHT - FACE_LEFT) * 100 * 2 / FACE_RECT_SIZE;
		assertEquals("Unexpected size of face", expectedSize, detectedFaces[SIZE_INDEX]);

		int expectedXPosition = (FACE_TOP + (FACE_BOTTOM - FACE_TOP) / 2) * ScreenValues.SCREEN_WIDTH * (-1)
				/ FACE_RECT_SIZE;
		assertEquals("Unexpected x position of face", expectedXPosition, detectedFaces[X_POSITION_INDEX]);

		int expectedYPosition = (FACE_LEFT + (FACE_RIGHT - FACE_LEFT) / 2) * ScreenValues.SCREEN_HEIGHT * (-1)
				/ FACE_RECT_SIZE;
		assertEquals("Unexpected y position of face", expectedYPosition, detectedFaces[Y_POSITION_INDEX]);

		detector.onFaceDetection(faces, null);
		assertEquals("Face Detection Listener does not receive calls", 2, detectedFaces[COUNTER_INDEX]);

		detector.removeOnFaceDetectedListener(detectionListener);
	}

	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	public void testFaceSizeBounds() {
		IcsFaceDetector detector = new IcsFaceDetector();

		final int[] faceSize = new int[1];
		OnFaceDetectedListener detectionListener = new OnFaceDetectedListener() {

			public void onFaceDetected(Point position, int size) {
				faceSize[0] = size;
			}
		};
		detector.addOnFaceDetectedListener(detectionListener);

		Rect faceBounds = new Rect(FACE_LEFT, FACE_TOP, FACE_RIGHT, FACE_BOTTOM);
		Face[] faces = new Face[1];
		faces[0] = new Face();
		faces[0].rect = faceBounds;

		detector.onFaceDetection(faces, null);
		assertTrue("Face size must not be negative", faceSize[0] >= 0);
		assertTrue("Illegal face size, range is [0,100]", faceSize[0] <= 100);

		Random random = new Random();
		int left = random.nextInt(FACE_RECT_SIZE - 1);
		int top = random.nextInt(FACE_RECT_SIZE - 1);
		int right = left + random.nextInt(FACE_RECT_SIZE - left);
		int bottom = top + random.nextInt(FACE_RECT_SIZE - top);
		faceBounds = new Rect(left - FACE_RECT_SIZE / 2, top - FACE_RECT_SIZE / 2, right - FACE_RECT_SIZE / 2, bottom
				- FACE_RECT_SIZE / 2);

		faces[0].rect = faceBounds;
		detector.onFaceDetection(faces, null);
		assertTrue("Face size must not be negative", faceSize[0] >= 0);
		assertTrue("Illegal face size, range is [0,100]", faceSize[0] <= 100);

		detector.removeOnFaceDetectedListener(detectionListener);
	}
}

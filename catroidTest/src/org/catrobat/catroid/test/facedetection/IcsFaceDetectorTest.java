package org.catrobat.catroid.test.facedetection;

import org.catrobat.catroid.common.ScreenValues;
import org.catrobat.catroid.facedetection.IcsFaceDetector;
import org.catrobat.catroid.facedetection.OnFaceDetectedListener;

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
	private static final int FACE_TOP = 100;
	private static final int FACE_BOTTOM = 300;

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
		Face[] faces = new Face[1];
		faces[0] = new Face();
		faces[0].rect = faceBounds;

		detector.onFaceDetection(faces, null);
		assertEquals("Face Detection Listener does not receive call", 1, detectedFaces[COUNTER_INDEX]);

		int expectedSize = (FACE_RIGHT - FACE_LEFT) * 100 * 2 / FACE_RECT_SIZE;
		assertEquals("Unexpected size of face", expectedSize, detectedFaces[SIZE_INDEX]);

		int expectedXPosition = (FACE_LEFT + (FACE_RIGHT - FACE_LEFT)) * ScreenValues.SCREEN_WIDTH / FACE_RECT_SIZE;
		assertEquals("Unexpected x position of face", expectedXPosition, detectedFaces[X_POSITION_INDEX]);

		int expectedYPosition = (FACE_TOP + (FACE_BOTTOM - FACE_TOP)) * ScreenValues.SCREEN_HEIGHT / FACE_RECT_SIZE;
		assertEquals("Unexpected y position of face", expectedYPosition, detectedFaces[Y_POSITION_INDEX]);

		detector.onFaceDetection(faces, null);
		assertEquals("Face Detection Listener does not receive calls", 2, detectedFaces[COUNTER_INDEX]);

	}

}

package org.catrobat.catroid.test.camera;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;

import junit.framework.TestCase;

import org.catrobat.catroid.camera.CameraManager;
import org.catrobat.catroid.camera.JpgPreviewCallback;

public class CameraManagerTest extends TestCase {

	private static final int MAX_FRAME_DELAY_IN_MS = 1000;

	public void testCameraNotAvailable() {
		Camera camera = null;
		camera = Camera.open();

		try {
			boolean started = CameraManager.getInstance().startCamera();
			assertFalse("Expected camera not to be able to start when hardware already in use", started);
		} catch (Exception exc) {
			fail("Unavailable camera should not cause an exception \"" + exc.getMessage() + "\"");
		} finally {
			if (camera != null) {
				camera.release();
			}
		}
	}

	public void testCameraRelease() {
		CameraManager.getInstance().startCamera();
		CameraManager.getInstance().releaseCamera();

		Camera camera = null;
		try {
			camera = Camera.open();
		} catch (Exception exc) {
			fail("Camera was not propperly released");
		} finally {
			if (camera != null) {
				camera.release();
			}
		}
	}

	public void testDoubleStart() {
		boolean success = CameraManager.getInstance().startCamera();
		assertTrue("Camera was not started properly", success);
		try {
			CameraManager.getInstance().startCamera();
		} catch (Exception e) {
			fail("Secound start of camera should be ignored but produced exception: " + e.getMessage());
		}
		CameraManager.getInstance().releaseCamera();
	}

	public void testJpgPreviewFrameCallback() {
		final int[] calls = new int[1];
		calls[0] = 0;
		JpgPreviewCallback callback = new JpgPreviewCallback() {
			public void onJpgPreviewFrame(byte[] jpgData) {
				calls[0]++;
				if (calls[0] == 1) {
					Bitmap bitmap = BitmapFactory.decodeByteArray(jpgData, 0, jpgData.length);
					assertNotNull("Could not create bitmap from data - wrong format?", bitmap);
				}
			}
		};
		CameraManager.getInstance().addOnJpgPreviewFrameCallback(callback);
		boolean success = CameraManager.getInstance().startCamera();
		assertTrue("Camera was not started properly", success);
		try {
			Thread.sleep(MAX_FRAME_DELAY_IN_MS);
		} catch (InterruptedException e) {
		}
		assertTrue("Did not receive frame data from camera", calls[0] > 0);
		CameraManager.getInstance().removeOnJpgPreviewFrameCallback(callback);
		CameraManager.getInstance().releaseCamera();
	}

	public void testGetInstance() {
		assertNotNull("Could not get instance of CameraManager", CameraManager.getInstance());
	}
}

/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2016 The Catrobat Team
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
package org.catrobat.catroid.camera;

import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.util.Log;
import android.view.ViewGroup;
import android.view.ViewParent;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.ScreenValues;
import org.catrobat.catroid.facedetection.FaceDetectionHandler;
import org.catrobat.catroid.stage.CameraSurface;
import org.catrobat.catroid.stage.DeviceCameraControl;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.utils.FlashUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class CameraManager implements DeviceCameraControl, Camera.PreviewCallback {

	public static final int TEXTURE_NAME = 1;
	public static final int NO_CAMERA = -1;
	private static final String TAG = CameraManager.class.getSimpleName();
	private static CameraManager instance;
	private Camera camera;
	private SurfaceTexture texture;
	private List<JpgPreviewCallback> callbacks = new ArrayList<JpgPreviewCallback>();
	private int previewFormat;
	private int previewWidth;
	private int previewHeight;
	private int currentCameraID = NO_CAMERA;
	private int frontCameraID = NO_CAMERA;
	private int backCameraID = NO_CAMERA;
	private int defaultCameraID = NO_CAMERA;
	private boolean hasFlashBack = false;
	private boolean hasFlashFront = false;
	private int cameraCount = 0;

	private int orientation = 0;

	StageActivity stageActivity = null;
	CameraSurface cameraSurface = null;

	public final Object cameraChangeLock = new Object();
	private final Object cameraBaseLock = new Object();
	private boolean wasRunning = false;

	//Mode used for CameraPreview
	public enum CameraState {
		notUsed,
		prepare,
		previewRunning,
		previewPaused,
		stopped
	}

	private CameraState state = CameraState.notUsed;

	public static CameraManager getInstance() {
		if (instance == null) {
			instance = new CameraManager();
		}
		return instance;
	}

	private CameraManager() {
		cameraCount = Camera.getNumberOfCameras();
		for (int id = 0; id < cameraCount; id++) {
			Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
			Camera.getCameraInfo(id, cameraInfo);

			if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
				backCameraID = id;
				hasFlashBack = hasCameraFlash(id);
			}
			if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
				frontCameraID = id;
			}
			defaultCameraID = id;
		}
		currentCameraID = defaultCameraID;
		createTexture();
	}

	public void setToDefaultCamera() {
		currentCameraID = defaultCameraID;
	}

	public boolean setToFrontCamera() {
		if (!hasFrontCamera()) {
			return false;
		}
		updateCamera(frontCameraID);
		return true;
	}

	public boolean setToBackCamera() {
		if (!hasBackCamera()) {
			return false;
		}
		updateCamera(backCameraID);
		return true;
	}

	public boolean hasBackCamera() {
		if (backCameraID == NO_CAMERA) {
			return false;
		}
		return true;
	}

	public boolean hasFrontCamera() {
		if (frontCameraID == NO_CAMERA) {
			return false;
		}
		return true;
	}

	public CameraState getState() {
		return state;
	}

	public Camera getCamera() {
		return camera;
	}

	public int getOrientation() {
		return orientation;
	}

	public boolean isFacingBack() {
		return currentCameraID == backCameraID;
	}

	public boolean isFacingFront() {
		return currentCameraID == frontCameraID;
	}

	private boolean createCamera() {
		if (camera != null) {
			return false;
		}

		try {
			camera = Camera.open(currentCameraID);
			if (ProjectManager.getInstance().getCurrentProject().islandscapeMode()) {
				camera.setDisplayOrientation(0);
			} else {
				camera.setDisplayOrientation(90);
			}

			Camera.Parameters cameraParameters = camera.getParameters();

			previewHeight = ScreenValues.SCREEN_HEIGHT;
			previewWidth = ScreenValues.SCREEN_WIDTH;

			cameraParameters.setPreviewSize(previewWidth, previewHeight);
			camera.setParameters(cameraParameters);
		} catch (RuntimeException runtimeException) {
			Log.e(TAG, "Creating camera failed!", runtimeException);
			return false;
		}

		camera.setPreviewCallbackWithBuffer(this);
		if (texture != null) {
			try {
				setTexture();
			} catch (IOException iOException) {
				Log.e(TAG, "Setting preview texture failed!", iOException);
				return false;
			}
		}
		return true;
	}

	public boolean startCamera() {
		synchronized (cameraBaseLock) {
			if (camera == null) {
				boolean success = createCamera();
				if (!success) {
					return false;
				}
			}
			Parameters parameters = camera.getParameters();
			previewFormat = parameters.getPreviewFormat();
			previewWidth = parameters.getPreviewSize().width;
			previewHeight = parameters.getPreviewSize().height;
			try {
				camera.startPreview();
			} catch (Exception e) {
				Log.e(TAG, e.getMessage());
				return false;
			}
			return true;
		}
	}

	public void releaseCamera() {
		synchronized (cameraBaseLock) {
			if (camera == null) {
				return;
			}
			camera.setPreviewCallback(null);
			camera.stopPreview();
			camera.release();
			camera = null;
		}
	}

	public void addOnJpgPreviewFrameCallback(JpgPreviewCallback callback) {
		if (callbacks.contains(callback)) {
			return;
		}
		callbacks.add(callback);
	}

	public void removeOnJpgPreviewFrameCallback(JpgPreviewCallback callback) {
		callbacks.remove(callback);
	}

	@Override
	public void onPreviewFrame(byte[] data, Camera camera) {
		if (callbacks.size() == 0) {
			return;
		}
		byte[] jpgData = getDecodeableBytesFromCameraFrame(data);
		for (JpgPreviewCallback callback : callbacks) {
			callback.onFrame(jpgData);
		}
	}

	private byte[] getDecodeableBytesFromCameraFrame(byte[] cameraData) {
		byte[] decodableBytes;
		YuvImage image = new YuvImage(cameraData, previewFormat, previewWidth, previewHeight, null);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		image.compressToJpeg(new Rect(0, 0, previewWidth, previewHeight), 50, out);
		decodableBytes = out.toByteArray();
		return decodableBytes;
	}

	private void createTexture() {
		texture = new SurfaceTexture(TEXTURE_NAME);
	}

	private void setTexture() throws IOException {
		camera.setPreviewTexture(texture);
	}

	public void setFlashParams(Parameters flash) {
		if (camera != null && flash != null) {
			Parameters current = camera.getParameters();
			current.setFlashMode(flash.getFlashMode());
			camera.setParameters(current);
		}
	}

	@Override
	public void prepareCamera() {
		state = CameraState.previewRunning;

		if (cameraSurface == null) {
			cameraSurface = new CameraSurface(stageActivity);
		}

		ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup
				.LayoutParams.WRAP_CONTENT);

		//java.lang.IllegalStateException: The specified child already has a parent. You must call removeView() on the child's parent first.
		ViewGroup parent = (ViewGroup) cameraSurface.getParent();
		if (parent != null) {
			parent.removeView(cameraSurface);
		}
		stageActivity.addContentView(cameraSurface, params);
		startCamera();
	}

	@Override
	public void stopPreview() {
		state = CameraState.notUsed;
		if (cameraSurface != null) {
			ViewParent parentView = cameraSurface.getParent();
			if (parentView instanceof ViewGroup) {
				ViewGroup viewGroup = (ViewGroup) parentView;
				viewGroup.removeView(cameraSurface);
			}

			cameraSurface = null;
			try {
				if (camera != null) {
					camera.stopPreview();
					setTexture();
				}
				if (FaceDetectionHandler.isFaceDetectionRunning() || FlashUtil.isAvailable()) {
					camera.startPreview();
				}
			} catch (IOException e) {
				Log.e(TAG, "reset Texture failed at stopPreview");
				Log.e(TAG, e.getMessage());
			}
		}
	}

	@Override
	public void pausePreview() {
		if (state == CameraState.previewRunning) {
			wasRunning = true;
		}

		stopPreview();
		state = CameraState.previewPaused;
	}

	@Override
	public void resumePreview() {
		prepareCamera();
		wasRunning = false;
	}

	@Override
	public void prepareCameraAsync() {
		Runnable r = new Runnable() {
			public void run() {
				prepareCamera();
			}
		};
		stageActivity.post(r);
	}

	@Override
	public void stopPreviewAsync() {
		Runnable r = new Runnable() {
			public void run() {
				stopPreview();
			}
		};
		stageActivity.post(r);
	}

	@Override
	public void pausePreviewAsync() {
		if (state == CameraState.previewPaused
				|| state == CameraState.stopped
				|| state == CameraState.notUsed) {
			return;
		}

		Runnable r = new Runnable() {
			public void run() {
				pausePreview();
			}
		};
		stageActivity.post(r);
	}

	@Override
	public void resumePreviewAsync() {
		if (state != CameraState.previewPaused || !wasRunning) {
			return;
		}

		Runnable r = new Runnable() {
			public void run() {
				resumePreview();
			}
		};
		stageActivity.post(r);
	}

	@Override
	public boolean isReady() {
		if (camera != null) {
			return true;
		}
		return false;
	}

	public void updatePreview(CameraState newState) {

		synchronized (cameraChangeLock) {
			if (state == CameraState.previewRunning
					&& newState != CameraState.prepare) {
				stopPreviewAsync();
			} else if (state == CameraState.notUsed
					&& newState != CameraState.stopped) {
				prepareCameraAsync();
			}
		}
	}

	private void updateCamera(int cameraId) {

		synchronized (cameraChangeLock) {
			CameraState currentState = state;

			if (cameraId == currentCameraID) {
				return;
			}

			currentCameraID = cameraId;

			boolean changingCameraWithoutFlash = (isFacingFront() && !hasFlashFront())
					|| (isFacingBack() && !hasFlashBlack());

			if (FlashUtil.isOn() && changingCameraWithoutFlash) {
				Log.w(TAG, "destroy Stage because flash isOn while chaning camera");
				CameraManager.getInstance().destroyStage();
				return;
			}

			FlashUtil.pauseFlash();
			FaceDetectionHandler.pauseFaceDetection();
			releaseCamera();

			startCamera();
			FaceDetectionHandler.resumeFaceDetection();
			FlashUtil.resumeFlash();

			if (currentState == CameraState.prepare
					|| currentState == CameraState.previewRunning) {
				changeCameraAsync();
			}
		}
	}

	public void changeCameraAsync() {
		Runnable r = new Runnable() {
			public void run() {
				changeCamera();
			}
		};
		stageActivity.post(r);
	}

	public void changeCamera() {
		stopPreview();
		prepareCamera();
	}

	public void setStageActivity(StageActivity stageActivity) {
		this.stageActivity = stageActivity;
	}

	public void destroyStage() {
		if (this.stageActivity != null) {
			stageActivity.destroy();
		}
	}

	public boolean hasFlashBlack() {
		return hasFlashBack;
	}

	public boolean hasFlashFront() {
		return hasFlashFront;
	}

	private boolean hasCameraFlash(int cameraID) {
		try {
			Camera camera;
			camera = Camera.open(cameraID);

			if (camera == null) {
				return false;
			}

			Camera.Parameters parameters = camera.getParameters();

			if (parameters.getFlashMode() == null) {
				camera.release();
				return false;
			}

			List<String> supportedFlashModes = parameters.getSupportedFlashModes();
			if (supportedFlashModes == null || supportedFlashModes.isEmpty()
					|| (supportedFlashModes.size() == 1 && supportedFlashModes.get(0).equals(Camera.Parameters.FLASH_MODE_OFF))) {
				camera.release();
				return false;
			}

			camera.release();
			return true;
		} catch (Exception exception) {
			Log.e(TAG, "failed checking for flash", exception);
			return false;
		}
	}
}

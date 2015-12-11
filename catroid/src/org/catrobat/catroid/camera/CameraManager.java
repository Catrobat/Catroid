/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2015 The Catrobat Team
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

import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.Build;
import android.util.Log;
import android.view.ViewGroup;
import android.view.ViewParent;

import org.catrobat.catroid.facedetection.FaceDetectionHandler;
import org.catrobat.catroid.stage.CameraSurface;
import org.catrobat.catroid.stage.DeviceCameraControl;
import org.catrobat.catroid.stage.StageActivity;
import org.catrobat.catroid.utils.LedUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class CameraManager implements DeviceCameraControl, Camera.PreviewCallback {

	public static final int TEXTURE_NAME = 1;
	private static final String TAG = CameraManager.class.getSimpleName();
	private static CameraManager instance;
	private Camera camera;
	private SurfaceTexture texture;
	private List<JpgPreviewCallback> callbacks = new ArrayList<JpgPreviewCallback>();
	private int previewFormat;
	private int previewWidth;
	private int previewHeight;
	private int cameraID = 1;
	private int orientation = 0;

	private boolean useTexture = false;

	StageActivity stageActivity = null;
	CameraSurface cameraSurface = null;

	private boolean updateBackgroundToTransparent = false;
	private boolean updateBackgroundToNotTransparent = false;

	public final Object cameraChangeLock = new Object();
	private final Object cameraBaseLock = new Object();
	private boolean wasRunning = false;

	public static CameraManager getInstance() {
		if (instance == null) {
			instance = new CameraManager();
		}
		return instance;
	}

	private CameraManager() {
		int currentApi = android.os.Build.VERSION.SDK_INT;
		if (currentApi >= android.os.Build.VERSION_CODES.HONEYCOMB) {
			useTexture = true;
			createTexture();
		}
	}

	public void setCameraID(int cameraId) {
		this.cameraID = cameraId;
	}

	//Mode used for CameraPreview
	public enum CameraState {
		notUsed,
		prepare,
		previewRunning,
		previewPaused,
		stopped
	}

	private CameraState state = CameraState.notUsed;

	public CameraState getState() {
		return state;
	}

	public void setState(CameraState state) {
		this.state = state;
	}

	public Camera getCamera() {
		return camera;
	}

	public boolean isCameraAvailable(Context context, String feature) {
		PackageManager pm = context.getPackageManager();

		if (pm.hasSystemFeature(feature)) {
			return true;
		}

		return false;
	}

	public int getCameraID() {
		return cameraID;
	}

	public int getOrientation() {
		return orientation;
	}

	public boolean isFacingBack() {
		return cameraID == 0;
	}

	private boolean createCamera() {

		if (camera != null) {
			return false;
		}
		try {
			camera = Camera.open(cameraID);
			camera.setDisplayOrientation(90);

			Camera.Parameters p = camera.getParameters();
			List<Camera.Size> sizes = p.getSupportedPictureSizes();
			for (int i = 0; i < sizes.size(); i++) {
				try {
					p.setPreviewSize(sizes.get(i).width, sizes.get(i).height);
					camera.setParameters(p);
					Log.d("VALID Preview Size", "Supported size: " + sizes.get(i).width + " x " + sizes.get(i).height);
					break;
				} catch (RuntimeException e) {
					Log.d("INVALID Preview Size", "Supported size: " + sizes.get(i).width + " x " + sizes.get(i).height);
				}
			}
		} catch (RuntimeException runtimeException) {
			Log.e(TAG, "Creating camera failed!", runtimeException);
			return false;
		}

		camera.setPreviewCallbackWithBuffer(this);

		if (useTexture && texture != null) {
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

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void createTexture() {
		texture = new SurfaceTexture(TEXTURE_NAME);
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setTexture() throws IOException {
		camera.setPreviewTexture(texture);
	}

	public void setLedParams(Parameters led) {
		if (camera != null && led != null) {
			Parameters current = camera.getParameters();
			current.setFlashMode(led.getFlashMode());
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
				if (FaceDetectionHandler.isFaceDetectionRunning() || LedUtil.isAvailable()) {
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

		if (state == CameraState.previewRunning) {
			wasRunning = true;
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
				updateBackgroundToNotTransparent = true;
			} else if (state == CameraState.notUsed
					&& newState != CameraState.stopped) {
				updateBackgroundToTransparent = true;
				prepareCameraAsync();
			}
		}
	}

	public void setUpdateBackgroundToTransparent(boolean updateBackgroundToTransparent) {
		this.updateBackgroundToTransparent = updateBackgroundToTransparent;
	}

	public boolean isUpdateBackgroundToTransparent() {
		return updateBackgroundToTransparent;
	}

	public boolean isUpdateBackgroundToNotTransparent() {
		return updateBackgroundToNotTransparent;
	}

	public void setUpdateBackgroundToNotTransparent(boolean updateBackgroundToNotTransparent) {
		this.updateBackgroundToNotTransparent = updateBackgroundToNotTransparent;
	}

	public void updateCamera(int cameraId) {

		synchronized (cameraChangeLock) {
			CameraState currentState = state;

			if (cameraId == getCameraID()) {
				return;
			}

			setCameraID(cameraId);

			if (LedUtil.isOn() && !isFacingBack()) {
				Log.w("FlashError", "destroy Stage because flash isOn and front Camera was chosen");
				CameraManager.getInstance().destroyStage();
				return;
			}

			LedUtil.pauseLed();
			FaceDetectionHandler.pauseFaceDetection();

			releaseCamera();
			startCamera();

			FaceDetectionHandler.resumeFaceDetection();
			LedUtil.resumeLed();

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
}

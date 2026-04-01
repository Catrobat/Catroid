package org.catrobat.catroid.FaceRecognizer;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Size;
import android.view.Surface;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import org.catrobat.catroid.FaceRecognizer.env.FileUtils;
import org.catrobat.catroid.FaceRecognizer.env.ImageUtils;
import org.catrobat.catroid.FaceRecognizer.ml.BlazeFace;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;

public class MainActivityForRecog extends AppCompatActivity {

	// Result extras
	public static final String EXTRA_DETECTED_NAME = "detected_name";
	public static final String EXTRA_CONFIDENCE    = "detected_conf";

	private static final int REQ_CAMERA = 1201;

	// Prefer front cam first; if no face, retry once with back cam
	private boolean preferFrontActive = true;
	private boolean retriedWithBack   = false;

	// BlazeFace input size
	private static final int CROP_W = BlazeFace.INPUT_SIZE_WIDTH;
	private static final int CROP_H = BlazeFace.INPUT_SIZE_HEIGHT;

	// Recognizer
	private Recognizer recognizer;
	private boolean initialized = false;

	// Cancellation guard (e.g., Back pressed)
	private volatile boolean aborted = false;

	// Camera2
	private HandlerThread bgThread;
	private Handler bgHandler;
	private CameraDevice cameraDevice;
	private CameraCaptureSession captureSession;
	private ImageReader imageReader;
	private String cameraId;
	private boolean captureDone = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// No setContentView(): activity is invisible (NoDisplay theme).

		startBackgroundThread();
		initRecognizerThenCapture();
	}

	// ---------------- Initialization ----------------

	private void initRecognizerThenCapture() {
		new Thread(() -> {
			try {
				// Ensure model root exists
				if (FileUtils.ROOT == null || FileUtils.ROOT.trim().isEmpty()) {
					File root = new File(getFilesDir(), "face_models");
					if (!root.exists()) root.mkdirs();
					FileUtils.ROOT = root.getAbsolutePath();
				}
				File dir = new File(FileUtils.ROOT);
				if (!dir.isDirectory()) {
					if (dir.exists()) dir.delete();
					dir.mkdirs();
				}

				// Copy model assets if missing (does NOT overwrite your trained data)
				AssetManager mgr = getAssets();
				if (!new File(dir, FileUtils.DATA_FILE).exists())  FileUtils.copyAsset(mgr, FileUtils.DATA_FILE);
				if (!new File(dir, FileUtils.MODEL_FILE).exists()) FileUtils.copyAsset(mgr, FileUtils.MODEL_FILE);
				if (!new File(dir, FileUtils.LABEL_FILE).exists()) FileUtils.copyAsset(mgr, FileUtils.LABEL_FILE);

				recognizer = Recognizer.getInstance(getAssets());
				initialized = true;

				runOnUiThread(this::ensureCameraPermissionThenCapture);

			} catch (Exception e) {
				// Fatal init error -> cancel cleanly
				finishCanceled();
			}
		}).start();
	}

	private void ensureCameraPermissionThenCapture() {
		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
				!= PackageManager.PERMISSION_GRANTED) {
			ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQ_CAMERA);
		} else {
			recaptureOnce();
		}
	}

	// ---------------- Capture flow ----------------

	private void recaptureOnce() {
		if (aborted) return;
		captureDone = false;
		closeCamera();
		// small delay to let camera close
		getWindow().getDecorView().postDelayed(() -> { if (!aborted) captureOnce(); }, 120);
	}

	private void captureOnce() {
		if (aborted || !initialized || captureDone) return;
		captureDone = true;

		try {
			CameraManager mgr = (CameraManager) getSystemService(CAMERA_SERVICE);
			cameraId = chooseCameraId(mgr, preferFrontActive);
			if (cameraId == null) { finishUnknown(); return; }

			Size jpeg = pickJpegSize(mgr, cameraId, 1280);
			imageReader = ImageReader.newInstance(jpeg.getWidth(), jpeg.getHeight(), ImageFormat.JPEG, 2);
			imageReader.setOnImageAvailableListener(onImageAvailableListener, bgHandler);

			if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
					!= PackageManager.PERMISSION_GRANTED) {
				finishCanceled();
				return;
			}

			mgr.openCamera(cameraId, new CameraDevice.StateCallback() {
				@Override public void onOpened(@NonNull CameraDevice cd) {
					if (aborted) { cd.close(); return; }
					cameraDevice = cd;
					createSessionAndShoot();
				}
				@Override public void onDisconnected(@NonNull CameraDevice cd) { cd.close(); }
				@Override public void onError(@NonNull CameraDevice cd, int error) { cd.close(); finishUnknown(); }
			}, bgHandler);

		} catch (Exception e) {
			finishUnknown();
		}
	}

	private void createSessionAndShoot() {
		try {
			final Surface target = imageReader.getSurface();

			cameraDevice.createCaptureSession(
					Arrays.asList(target),
					new CameraCaptureSession.StateCallback() {
						@Override public void onConfigured(@NonNull CameraCaptureSession session) {
							if (aborted) { closeCamera(); return; }
							captureSession = session;
							try {
								final CaptureRequest.Builder still =
										cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
								still.addTarget(target);
								still.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
								still.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);

								bgHandler.postDelayed(() -> {
									if (aborted) { closeCamera(); return; }
									try {
										session.capture(still.build(), new CameraCaptureSession.CaptureCallback(){}, bgHandler);
									} catch (Exception e) {
										finishUnknown();
										closeCamera();
									}
								}, 300);

							} catch (Exception e) {
								finishUnknown();
								closeCamera();
							}
						}
						@Override public void onConfigureFailed(@NonNull CameraCaptureSession session) {
							finishUnknown();
							closeCamera();
						}
					}, bgHandler
			);
		} catch (Exception e) {
			finishUnknown();
			closeCamera();
		}
	}

	private final ImageReader.OnImageAvailableListener onImageAvailableListener = ir -> {
		if (aborted) {
			Image imgAbort = null;
			try { imgAbort = ir.acquireLatestImage(); } catch (Throwable ignore) {}
			finally { if (imgAbort != null) imgAbort.close(); }
			closeCamera();
			return;
		}

		Image img = null;
		try {
			img = ir.acquireLatestImage();
			if (img == null) { maybeRetryBackOrFinish("Unknown", 0f); return; }

			ByteBuffer buf = img.getPlanes()[0].getBuffer();
			byte[] bytes = new byte[buf.remaining()];
			buf.get(bytes);

			BitmapFactory.Options opts = new BitmapFactory.Options();
			opts.inPreferredConfig = Bitmap.Config.ARGB_8888;
			Bitmap decoded = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, opts);
			if (decoded == null) { maybeRetryBackOrFinish("Unknown", 0f); return; }

			// Make upright via software (sensor+device)
			Bitmap upright = rotateToUpright(decoded);

			// Try 0/90/180/270, plus a mirrored pass when using front
			boolean isFront = isFrontCamera();
			Result bestRes = tryAllVariants(upright, isFront);

			boolean ok = bestRes != null && bestRes.best != null;
			String name = ok && bestRes.best.getTitle() != null ? bestRes.best.getTitle() : "Unknown";
			float  conf = ok && bestRes.best.getConfidence() != null ? bestRes.best.getConfidence() : 0f;

			if (!ok && !retriedWithBack) {
				retriedWithBack = true;
				preferFrontActive = false; // switch to back cam
				recaptureOnce();
			} else {
				finishOk(name, conf);
			}

		} catch (Exception e) {
			finishUnknown();
		} finally {
			if (img != null) img.close();
			closeCamera();
		}
	};

	// ---------------- Recognition helpers ----------------

	private static class Result {
		Bitmap usedBitmap;
		Recognizer.Recognition best;
		Bitmap faceCrop;
		int rot;
		boolean mirrored;
	}

	private Result tryAllVariants(Bitmap upright, boolean isFront) {
		float bestConf = -1f;
		Result out = null;
		int[] rots = new int[]{0, 90, 180, 270};

		for (int rot : rots) {
			Bitmap base = rotate(upright, rot);

			// normal
			Result r1 = runDetectorOn(base);
			if (r1 != null && r1.best != null) {
				r1.rot = rot; r1.mirrored = false;
				float c = val(r1.best.getConfidence());
				if (c > bestConf) { bestConf = c; out = r1; }
			}

			// mirrored (for front cam)
			if (isFront) {
				Bitmap mir = mirror(base);
				Result r2 = runDetectorOn(mir);
				if (r2 != null && r2.best != null) {
					r2.rot = rot; r2.mirrored = true;
					float c = val(r2.best.getConfidence());
					if (c > bestConf) { bestConf = c; out = r2; }
				}
			}
		}
		return out;
	}

	private Result runDetectorOn(Bitmap frameBmp) {
		try {
			if (recognizer == null) return null;

			Matrix frameToCrop = ImageUtils.getTransformationMatrix(
					frameBmp.getWidth(), frameBmp.getHeight(),
					CROP_W, CROP_H,
					/*sensorOrientation=*/0, /*maintainAspect=*/false);
			Matrix cropToFrame = new Matrix();
			frameToCrop.invert(cropToFrame);

			Bitmap crop = Bitmap.createBitmap(CROP_W, CROP_H, Bitmap.Config.ARGB_8888);
			new android.graphics.Canvas(crop).drawBitmap(frameBmp, frameToCrop, null);

			List<Recognizer.Recognition> mapped = recognizer.recognizeImage(crop, cropToFrame);
			if (mapped == null || mapped.isEmpty()) return null;

			Recognizer.Recognition best = null;
			for (Recognizer.Recognition r : mapped) {
				if (best == null || val(r.getConfidence()) > val(best.getConfidence())) best = r;
			}
			if (best == null) return null;

			// (optional) faceCrop available if you ever need it
			Bitmap faceCrop = null;
			if (best.getLocation() != null) {
				RectF box = new RectF(best.getLocation());
				float pad = Math.min(frameBmp.getWidth(), frameBmp.getHeight()) * 0.04f;
				box.inset(-pad, -pad);
				clampToBitmap(box, frameBmp.getWidth(), frameBmp.getHeight());
				try {
					faceCrop = Bitmap.createBitmap(
							frameBmp,
							(int) box.left,
							(int) box.top,
							Math.max(1, (int) (box.width())),
							Math.max(1, (int) (box.height())));
				} catch (Throwable ignored) {}
			}

			Result res = new Result();
			res.usedBitmap = frameBmp;
			res.best = best;
			res.faceCrop = faceCrop;
			return res;

		} catch (Throwable t) {
			return null;
		}
	}

	// ---------------- Return paths ----------------

	private void finishOk(String name, float conf) {
		if (aborted) return;
		Intent out = new Intent()
				.putExtra(EXTRA_DETECTED_NAME, (name == null || name.trim().isEmpty()) ? "Unknown" : name)
				.putExtra(EXTRA_CONFIDENCE, conf);
		setResult(Activity.RESULT_OK, out);
		finish();
	}

	private void finishUnknown() {
		if (aborted) return;
		Intent out = new Intent()
				.putExtra(EXTRA_DETECTED_NAME, "Unknown")
				.putExtra(EXTRA_CONFIDENCE, 0f);
		setResult(Activity.RESULT_OK, out);
		finish();
	}

	private void finishCanceled() {
		if (aborted) return;
		setResult(Activity.RESULT_CANCELED);
		finish();
	}

	private void maybeRetryBackOrFinish(String fallbackName, float conf) {
		if (aborted) return;
		if (!retriedWithBack) {
			retriedWithBack = true;
			preferFrontActive = false;
			recaptureOnce();
		} else {
			finishOk(fallbackName, conf);
		}
	}

	// ---------------- Orientation & math helpers ----------------

	private Bitmap rotateToUpright(Bitmap decoded) {
		try {
			CameraManager mgr = (CameraManager) getSystemService(CAMERA_SERVICE);
			CameraCharacteristics cc = mgr.getCameraCharacteristics(cameraId);
			Integer sensor = cc.get(CameraCharacteristics.SENSOR_ORIENTATION);
			if (sensor == null) sensor = 0;

			int device = getDeviceRotationDegrees();
			boolean isFront = cc.get(CameraCharacteristics.LENS_FACING) != null
					&& cc.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_FRONT;

			int rotate = isFront ? (sensor + device) % 360
					: (sensor - device + 360) % 360;
			if (rotate % 360 == 0) return decoded;

			Matrix m = new Matrix();
			m.postRotate(rotate);
			return Bitmap.createBitmap(decoded, 0, 0, decoded.getWidth(), decoded.getHeight(), m, true);
		} catch (Exception e) {
			return decoded;
		}
	}

	private static Bitmap rotate(Bitmap src, int degrees) {
		if (degrees % 360 == 0) return src;
		Matrix m = new Matrix(); m.postRotate(degrees);
		return Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), m, true);
	}

	private static Bitmap mirror(Bitmap src) {
		Matrix m = new Matrix(); m.preScale(-1f, 1f, src.getWidth()/2f, src.getHeight()/2f);
		return Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), m, true);
	}

	private static void clampToBitmap(RectF r, int w, int h) {
		r.left = Math.max(0, r.left);
		r.top = Math.max(0, r.top);
		r.right = Math.min(w, r.right);
		r.bottom = Math.min(h, r.bottom);
	}

	private boolean isFrontCamera() {
		try {
			CameraManager mgr = (CameraManager) getSystemService(CAMERA_SERVICE);
			CameraCharacteristics cc = mgr.getCameraCharacteristics(cameraId);
			Integer facing = cc.get(CameraCharacteristics.LENS_FACING);
			return facing != null && facing == CameraCharacteristics.LENS_FACING_FRONT;
		} catch (Exception e) {
			return preferFrontActive;
		}
	}

	private float val(Float f) { return f == null ? 0f : f; }

	// ---------------- Camera helpers ----------------

	private void startBackgroundThread() {
		if (bgThread != null) return;
		bgThread = new HandlerThread("oneshot_bg");
		bgThread.start();
		bgHandler = new Handler(bgThread.getLooper());
	}

	private void stopBackgroundThread() {
		if (bgThread != null) {
			if (Build.VERSION.SDK_INT >= 18) bgThread.quitSafely(); else bgThread.quit();
			try { bgThread.join(); } catch (InterruptedException ignored) {}
			bgThread = null; bgHandler = null;
		}
	}

	private void closeCamera() {
		try {
			if (captureSession != null) { captureSession.close(); captureSession = null; }
			if (cameraDevice != null)   { cameraDevice.close();    cameraDevice = null; }
			if (imageReader != null)    { imageReader.close();     imageReader = null; }
		} catch (Exception ignored) {}
	}

	private String chooseCameraId(CameraManager mgr, boolean preferFront) throws CameraAccessException {
		String back = null, front = null;
		for (String id : mgr.getCameraIdList()) {
			CameraCharacteristics c = mgr.getCameraCharacteristics(id);
			Integer facing = c.get(CameraCharacteristics.LENS_FACING);
			if (facing != null && facing == CameraCharacteristics.LENS_FACING_FRONT) front = id;
			if (facing != null && facing == CameraCharacteristics.LENS_FACING_BACK)  back  = id;
		}
		return preferFront && front != null ? front : (back != null ? back : front);
	}

	private Size pickJpegSize(CameraManager mgr, String id, int targetMaxSide) throws CameraAccessException {
		StreamConfigurationMap map =
				mgr.getCameraCharacteristics(id).get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
		Size[] sizes = (map != null) ? map.getOutputSizes(ImageFormat.JPEG) : null;
		if (sizes == null || sizes.length == 0) return new Size(1280, 960);

		Size bestUnder = null;
		Size smallest = sizes[0];
		for (Size s : sizes) {
			if (s.getWidth()*s.getHeight() < smallest.getWidth()*smallest.getHeight()) smallest = s;
			int maxSide = Math.max(s.getWidth(), s.getHeight());
			if (maxSide <= targetMaxSide) {
				if (bestUnder == null ||
						(s.getWidth()*s.getHeight()) > (bestUnder.getWidth()*bestUnder.getHeight())) {
					bestUnder = s;
				}
			}
		}
		return (bestUnder != null) ? bestUnder : smallest;
	}

	private int getDeviceRotationDegrees() {
		int r = getWindowManager().getDefaultDisplay().getRotation();
		switch (r) {
			case Surface.ROTATION_0:   return 0;
			case Surface.ROTATION_90:  return 90;
			case Surface.ROTATION_180: return 180;
			case Surface.ROTATION_270: return 270;
			default: return 0;
		}
	}

	// ---------------- Back / lifecycle ----------------

	@Override
	public void onBackPressed() {
		aborted = true;                 // stop any pending capture
		setResult(Activity.RESULT_CANCELED);
		finish();
	}

	@Override
	protected void onDestroy() {
		aborted = true;
		super.onDestroy();
		closeCamera();
		stopBackgroundThread();
		// NOTE: do not close recognizer here to avoid reusing a closed singleton on next run.
		// If your Recognizer is not a singleton, you can close it safely.
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] perms, @NonNull int[] res) {
		super.onRequestPermissionsResult(requestCode, perms, res);
		if (requestCode == REQ_CAMERA && res.length > 0 && res[0] == PackageManager.PERMISSION_GRANTED) {
			recaptureOnce();
		} else {
			finishCanceled();
		}
	}
}

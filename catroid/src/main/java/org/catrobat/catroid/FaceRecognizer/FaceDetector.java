package org.catrobat.catroid.FaceRecognizer;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
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
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.Log;
import android.util.Size;
import android.view.Surface;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import org.catrobat.catroid.FaceRecognizer.env.FileUtils;
import org.catrobat.catroid.formulaeditor.SensorHandler;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Face detection with no Activity.
 *
 * Camera2 needs a Context, a CameraManager and a surface to write into. With no
 * preview there is no window, so the whole capture runs on a background thread
 * from the application context.
 *
 * That is the entire point. Launching an Activity to take the picture paused the
 * Catroid stage, Android could then destroy it, and Catroid restarted the
 * program. Every lifecycle problem this feature had came from that: the restart
 * loop, request codes falling through to endStageActivity, RESULT_CANCELED
 * turning into resourceFailed. None of it can happen here, because nothing is
 * launched.
 *
 * Usage:
 *
 *     FaceDetector.resetForNewRun();                       // once per program
 *     FaceDetector.requestDetection(context, onFinished);  // returns immediately
 *
 * The result is written straight into SensorHandler, so there is no
 * onActivityResult to route and nothing to add to any switch statement.
 *
 * The only thing an Activity was ever needed for is asking the CAMERA
 * permission, and Catroid already does that before the stage starts through
 * BrickResourcesToRuntimePermissions.
 */
public final class FaceDetector {
	private static final String TAG = "FaceDetector";

	public static final String UNKNOWN = "Unknown";

	/** Frames to score before deciding. More frames, less noise. */
	private static final int BURST_FRAMES = 3;

	/** Longest JPEG side. A bigger face crop makes a sharper network input. */
	private static final int TARGET_JPEG_SIDE = 1280;

	private static final long FIRST_SHOT_DELAY_MS = 700;
	private static final long BETWEEN_SHOTS_DELAY_MS = 250;

	/** Nothing may leave the camera open, or a program waiting, longer than this. */
	private static final long WATCHDOG_MS = 20000;

	/** Shortest gap between attempts, in case something asks repeatedly. */
	private static final long MIN_INTERVAL_MS = 5000;

	/**
	 * Stop the burst early when the first frame is already this far above the
	 * accept threshold. Turns a typical detection from about two seconds into
	 * under one, and still uses all three frames when the match is marginal.
	 */
	private static final float EARLY_EXIT_MARGIN = 0.15f;

	// ---------------- The gate ----------------

	private static volatile boolean detectionRunning = false;
	private static volatile boolean detectionDone = false;
	private static long lastDetectionStart = 0L;

	private static volatile String lastName = UNKNOWN;
	private static volatile float lastConfidence = 0f;

	/**
	 * The camera is only allowed to open while a script is actually running.
	 *
	 * A sensor is read from more places than a script: the brick view evaluates
	 * formulas to draw the block label, and the stage evaluates them while it is
	 * preparing. Those reads must report the last known name, never open a camera,
	 * or the camera fires before the script reaches the block that wants it.
	 *
	 * StageActivity turns this on when the program starts and off when it stops.
	 */
	private static volatile boolean scriptRunning = false;

	public static void setScriptRunning(boolean running) {
		scriptRunning = running;
		Log.i(TAG, "Script running = " + running);
	}

	public static boolean isScriptRunning() {
		return scriptRunning;
	}

	/** Called once when a detection ends, whatever the outcome. */
	public interface Callback {
		void onFinished(String name, float confidence);
	}

	private FaceDetector() {
	}

	public static boolean hasPermission(Context context) {
		return context != null && ContextCompat.checkSelfPermission(
				context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
	}

	public static String getLastName() {
		return lastName;
	}

	public static float getLastConfidence() {
		return lastConfidence;
	}

	public static boolean isDetectionDone() {
		return detectionDone;
	}

	/**
	 * How long a blocking read will wait. Kept well under any watchdog so the
	 * caller always gets an answer rather than being stuck.
	 */
	private static final long BLOCKING_WAIT_MS = 12000;

	/**
	 * Detects and waits for the answer, then returns the name.
	 *
	 * This is what makes
	 *
	 *     Set variable rr to (face name detection)
	 *
	 * work on its own, with no separate detect block. The read pauses until the
	 * camera is finished, so the very next block sees the real name.
	 *
	 * Safe to call from a Catroid script, because scripts run on the libGDX
	 * thread, not the Android main thread, so pausing it cannot cause an ANR. It
	 * pauses the stage for about a second while the camera works.
	 *
	 * Called from the main thread it never blocks. It starts the capture and
	 * returns the previous name, because freezing the UI thread would be an ANR.
	 *
	 * Only the first read in a program run opens the camera. Later reads return
	 * the stored name immediately, so a script can use the sensor as often as it
	 * likes.
	 */
	@RequiresApi(api = Build.VERSION_CODES.N)
	public static String detectBlocking(Context context) {
		if (detectionDone) {
			return lastName;
		}

		// Rule one: only a running script may open the camera. Everything else
		// gets the last known name. This is what stops the camera firing before
		// the script reaches the block that asked for it.
		if (!scriptRunning) {
			Log.i(TAG, "Read outside a running script, reporting '" + lastName
					+ "' without opening the camera");
			return lastName;
		}

		// Rule two: only the stage thread may open the camera. The UI thread
		// reads sensors to draw brick labels.
		if (Looper.myLooper() == Looper.getMainLooper()) {
			Log.i(TAG, "Read on the main thread, reporting '" + lastName
					+ "' without opening the camera");
			return lastName;
		}

		Log.i(TAG, "Blocking read from thread '"
				+ Thread.currentThread().getName() + "', opening the camera");
		if (context == null) {
			return lastName;
		}

		final CountDownLatch latch = new CountDownLatch(1);
		boolean started = requestDetection(context, (name, confidence) -> latch.countDown());

		if (!started && !detectionRunning) {
			return lastName;
		}

		try {
			if (!latch.await(BLOCKING_WAIT_MS, TimeUnit.MILLISECONDS)) {
				Log.w(TAG, "Blocking read timed out");
			}
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
		Log.i(TAG, "Blocking read returning '" + lastName + "'");
		return lastName;
	}

	/** True while a capture is in flight. */
	public static boolean isRunning() {
		return detectionRunning;
	}

	/**
	 * Forces a fresh detection, ignoring the one per run latch and the interval.
	 *
	 * This is what a brick uses. A brick is an explicit instruction from the
	 * child, so unlike a sensor read it should always mean "look now".
	 */
	@RequiresApi(api = Build.VERSION_CODES.N)
	public static boolean detectNow(Context context, Callback callback) {
		synchronized (FaceDetector.class) {
			if (detectionRunning) {
				Log.i(TAG, "A capture is already in flight, joining it");
				return false;
			}
			detectionDone = false;
			lastDetectionStart = 0L;
		}
		return requestDetection(context, callback);
	}

	/**
	 * Opens detection again. Call once per program start, including the restart
	 * button, or only the first run will ever detect.
	 */
	public static synchronized void resetForNewRun() {
		scriptRunning = false;
		detectionDone = false;
		detectionRunning = false;
		lastDetectionStart = 0L;
		lastName = UNKNOWN;
		lastConfidence = 0f;
		SensorHandler.setFaceNameRecognitionResult(UNKNOWN);
		Log.i(TAG, "Detection reopened for a new run");
	}

	/**
	 * Runs one detection in the background. Returns straight away.
	 *
	 * The callback is guaranteed to fire exactly once, on success, on failure and
	 * on timeout, so a caller can safely use it to release a waiting program.
	 * Returns false when nothing was started, in which case the callback has
	 * already been called.
	 */
	@RequiresApi(api = Build.VERSION_CODES.N)
	public static boolean requestDetection(Context context, Callback callback) {
		final Callback safeCallback = (callback != null) ? callback : new Callback() {
			@Override public void onFinished(String name, float confidence) {
			}
		};

		if (context == null) {
			Log.e(TAG, "No context");
			finish(UNKNOWN, 0f, safeCallback);
			return false;
		}

		synchronized (FaceDetector.class) {
			if (detectionDone) {
				Log.i(TAG, "Already detected this run");
				safeCallback.onFinished(lastName, lastConfidence);
				return false;
			}
			Log.i(TAG, "Capture starting, requested by thread '"
					+ Thread.currentThread().getName() + "'");
			if (detectionRunning) {
				Log.i(TAG, "A detection is already running");
				return false;
			}
			long now = System.currentTimeMillis();
			if (now - lastDetectionStart < MIN_INTERVAL_MS) {
				return false;
			}
			detectionRunning = true;
			lastDetectionStart = now;
		}

		if (!hasPermission(context)) {
			Log.e(TAG, "CAMERA permission is not granted. Map FACE_NAME_DETECTION to "
					+ "CAMERA in BrickResourcesToRuntimePermissions so Catroid asks "
					+ "for it before the stage starts.");
			finish(UNKNOWN, 0f, safeCallback);
			return false;
		}

		new Session(context.getApplicationContext(), safeCallback).start();
		return true;
	}

	private static synchronized void finish(String name, float confidence, Callback callback) {
		lastName = (name == null || name.trim().isEmpty()) ? UNKNOWN : name.trim();
		lastConfidence = confidence;
		detectionRunning = false;
		detectionDone = true;

		try {
			SensorHandler.setFaceNameRecognitionResult(lastName);
		} catch (Throwable t) {
			Log.e(TAG, "Could not write the sensor", t);
		}
		Log.i(TAG, "Detection finished: '" + lastName + "' confidence " + confidence);

		try {
			callback.onFinished(lastName, confidence);
		} catch (Throwable t) {
			Log.e(TAG, "Callback failed", t);
		}
	}

	// ---------------- One capture ----------------

	private static final class Session {
		private final Context context;
		private final Callback callback;

		private Recognizer recognizer;
		private Recognizer.Session scores;

		private HandlerThread thread;
		private Handler handler;
		private CameraDevice cameraDevice;
		private CameraCaptureSession captureSession;
		private ImageReader imageReader;
		private CaptureRequest.Builder stillBuilder;
		private String cameraId;

		private int shotsRequested = 0;
		private volatile boolean ended = false;

		Session(Context context, Callback callback) {
			this.context = context;
			this.callback = callback;
		}

		void start() {
			try {
				FileUtils.init(context);
				recognizer = Recognizer.getInstance(context);
				if (recognizer.getClassNames().isEmpty()) {
					Log.w(TAG, "Nobody has been trained yet");
					end(UNKNOWN, 0f);
					return;
				}
				scores = recognizer.newSession();
			} catch (Exception e) {
				Log.e(TAG, "Recognizer not available", e);
				end(UNKNOWN, 0f);
				return;
			}

			thread = new HandlerThread("face_detector");
			thread.start();
			handler = new Handler(thread.getLooper());

			handler.postDelayed(() -> {
				if (!ended) {
					Log.w(TAG, "Watchdog fired");
					decide();
				}
			}, WATCHDOG_MS);

			handler.post(this::openCamera);
		}

		private void openCamera() {
			try {
				CameraManager manager =
						(CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
				if (manager == null) {
					end(UNKNOWN, 0f);
					return;
				}

				cameraId = chooseCameraId(manager);
				if (cameraId == null) {
					Log.e(TAG, "No usable camera");
					end(UNKNOWN, 0f);
					return;
				}

				Size jpeg = pickJpegSize(manager, cameraId);
				Log.i(TAG, "Capturing " + jpeg.getWidth() + "x" + jpeg.getHeight()
						+ " on camera " + cameraId);

				imageReader = ImageReader.newInstance(jpeg.getWidth(), jpeg.getHeight(),
						ImageFormat.JPEG, BURST_FRAMES + 1);
				imageReader.setOnImageAvailableListener(onImageAvailable, handler);

				manager.openCamera(cameraId, new CameraDevice.StateCallback() {
					@Override public void onOpened(@NonNull CameraDevice device) {
						cameraDevice = device;
						createSession();
					}

					@Override public void onDisconnected(@NonNull CameraDevice device) {
						device.close();
						decide();
					}

					@Override public void onError(@NonNull CameraDevice device, int error) {
						Log.e(TAG, "Camera error " + error);
						device.close();
						decide();
					}
				}, handler);

			} catch (SecurityException e) {
				Log.e(TAG, "Camera permission was revoked", e);
				end(UNKNOWN, 0f);
			} catch (Exception e) {
				Log.e(TAG, "Could not open the camera", e);
				end(UNKNOWN, 0f);
			}
		}

		private void createSession() {
			try {
				final Surface target = imageReader.getSurface();
				cameraDevice.createCaptureSession(Arrays.asList(target),
						new CameraCaptureSession.StateCallback() {
							@Override public void onConfigured(@NonNull CameraCaptureSession s) {
								captureSession = s;
								try {
									stillBuilder = cameraDevice.createCaptureRequest(
											CameraDevice.TEMPLATE_STILL_CAPTURE);
									stillBuilder.addTarget(target);
									stillBuilder.set(CaptureRequest.CONTROL_MODE,
											CaptureRequest.CONTROL_MODE_AUTO);
									stillBuilder.set(CaptureRequest.CONTROL_AF_MODE,
											CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
									stillBuilder.set(CaptureRequest.CONTROL_AE_MODE,
											CaptureRequest.CONTROL_AE_MODE_ON);
									stillBuilder.set(CaptureRequest.CONTROL_SCENE_MODE,
											CaptureRequest.CONTROL_SCENE_MODE_FACE_PRIORITY);
									stillBuilder.set(CaptureRequest.JPEG_QUALITY, (byte) 95);

									handler.postDelayed(Session.this::takeShot,
											FIRST_SHOT_DELAY_MS);
								} catch (Exception e) {
									Log.e(TAG, "Could not build the request", e);
									decide();
								}
							}

							@Override public void onConfigureFailed(@NonNull CameraCaptureSession s) {
								Log.e(TAG, "Session configuration failed");
								decide();
							}
						}, handler);
			} catch (Exception e) {
				Log.e(TAG, "Could not create the session", e);
				decide();
			}
		}

		private void takeShot() {
			if (ended || captureSession == null || stillBuilder == null) {
				return;
			}
			if (shotsRequested >= BURST_FRAMES) {
				decide();
				return;
			}
			shotsRequested++;
			try {
				captureSession.capture(stillBuilder.build(),
						new CameraCaptureSession.CaptureCallback() { }, handler);
			} catch (Exception e) {
				Log.e(TAG, "Capture " + shotsRequested + " failed", e);
				decide();
			}
		}

		private final ImageReader.OnImageAvailableListener onImageAvailable = reader -> {
			Image image = null;
			Bitmap decoded = null;
			Bitmap upright = null;
			try {
				image = reader.acquireLatestImage();
				if (ended || image == null) {
					return;
				}

				ByteBuffer buffer = image.getPlanes()[0].getBuffer();
				byte[] bytes = new byte[buffer.remaining()];
				buffer.get(bytes);

				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inPreferredConfig = Bitmap.Config.ARGB_8888;
				decoded = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
				if (decoded == null) {
					next();
					return;
				}

				upright = rotateToSensorUpright(decoded);
				recognizer.addFrame(scores, upright, isFrontCamera());

				Recognizer.Result early = recognizer.peekSession(scores);
				if (early != null
						&& early.confidence > FaceDatabase.minSimilarity + EARLY_EXIT_MARGIN) {
					Log.i(TAG, "Clear match on frame " + shotsRequested + ", stopping early");
					end(early.name, early.confidence);
					return;
				}

				if (shotsRequested >= BURST_FRAMES) {
					decide();
				} else {
					next();
				}
			} catch (Throwable t) {
				Log.e(TAG, "Frame failed", t);
				next();
			} finally {
				if (upright != null && upright != decoded && !upright.isRecycled()) {
					upright.recycle();
				}
				if (decoded != null && !decoded.isRecycled()) {
					decoded.recycle();
				}
				if (image != null) {
					image.close();
				}
			}
		};

		private void next() {
			if (ended) {
				return;
			}
			if (shotsRequested >= BURST_FRAMES) {
				decide();
			} else {
				handler.postDelayed(this::takeShot, BETWEEN_SHOTS_DELAY_MS);
			}
		}

		private void decide() {
			if (ended) {
				return;
			}
			Recognizer.Result result = null;
			try {
				result = recognizer.finishSession(scores);
			} catch (Throwable t) {
				Log.e(TAG, "Scoring failed", t);
			}
			if (result == null) {
				end(UNKNOWN, 0f);
			} else {
				end(result.name, result.confidence);
			}
		}

		private synchronized void end(String name, float confidence) {
			if (ended) {
				return;
			}
			ended = true;
			closeCamera();
			FaceDetector.finish(name, confidence, callback);
			stopThread();
		}

		private void closeCamera() {
			try {
				if (captureSession != null) {
					captureSession.close();
					captureSession = null;
				}
				if (cameraDevice != null) {
					cameraDevice.close();
					cameraDevice = null;
				}
				if (imageReader != null) {
					imageReader.close();
					imageReader = null;
				}
				stillBuilder = null;
			} catch (Exception ignored) {
				// nothing useful to do
			}
		}

		private void stopThread() {
			final HandlerThread toStop = thread;
			thread = null;
			if (toStop == null) {
				return;
			}
			// Quit from outside its own looper, or the callback never returns.
			new Thread(() -> {
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
					toStop.quitSafely();
				} else {
					toStop.quit();
				}
			}).start();
		}

		/**
		 * Rotates by sensor orientation only. There is no activity, so screen
		 * rotation is unknown. FaceEmbedder tries all four rotations anyway, so an
		 * approximate upright is enough.
		 */
		private Bitmap rotateToSensorUpright(Bitmap decoded) {
			try {
				CameraManager manager =
						(CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
				Integer sensor = manager.getCameraCharacteristics(cameraId)
						.get(CameraCharacteristics.SENSOR_ORIENTATION);
				if (sensor == null || sensor % 360 == 0) {
					return decoded;
				}
				Matrix matrix = new Matrix();
				matrix.postRotate(sensor);
				return Bitmap.createBitmap(decoded, 0, 0,
						decoded.getWidth(), decoded.getHeight(), matrix, true);
			} catch (Exception e) {
				return decoded;
			}
		}

		private boolean isFrontCamera() {
			try {
				CameraManager manager =
						(CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
				Integer facing = manager.getCameraCharacteristics(cameraId)
						.get(CameraCharacteristics.LENS_FACING);
				return facing != null && facing == CameraCharacteristics.LENS_FACING_FRONT;
			} catch (Exception e) {
				return true;
			}
		}

		private String chooseCameraId(CameraManager manager) throws CameraAccessException {
			String back = null;
			String front = null;
			for (String id : manager.getCameraIdList()) {
				Integer facing = manager.getCameraCharacteristics(id)
						.get(CameraCharacteristics.LENS_FACING);
				if (facing == null) {
					continue;
				}
				if (facing == CameraCharacteristics.LENS_FACING_FRONT) {
					front = id;
				} else if (facing == CameraCharacteristics.LENS_FACING_BACK) {
					back = id;
				}
			}
			return front != null ? front : back;
		}

		private Size pickJpegSize(CameraManager manager, String id) throws CameraAccessException {
			StreamConfigurationMap map = manager.getCameraCharacteristics(id)
					.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
			Size[] sizes = (map != null) ? map.getOutputSizes(ImageFormat.JPEG) : null;
			if (sizes == null || sizes.length == 0) {
				return new Size(1280, 960);
			}

			Size bestUnder = null;
			Size smallest = sizes[0];
			for (Size s : sizes) {
				if (s.getWidth() * s.getHeight() < smallest.getWidth() * smallest.getHeight()) {
					smallest = s;
				}
				if (Math.max(s.getWidth(), s.getHeight()) <= TARGET_JPEG_SIDE
						&& (bestUnder == null || s.getWidth() * s.getHeight()
						> bestUnder.getWidth() * bestUnder.getHeight())) {
					bestUnder = s;
				}
			}
			return (bestUnder != null) ? bestUnder : smallest;
		}
	}
}
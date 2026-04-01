package org.catrobat.catroid.AgeGender;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Size;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.Tensor;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class VisitorAnalysisActivity extends AppCompatActivity {

	// ===== Request/Result protocol =====
	public static final String EXTRA_REQUEST = "req";
	public static final String EXTRA_RECEIVER = "rcv";
	public static final String EXTRA_ERROR = "err";
	public static final String EXTRA_AGE = "age";
	public static final String EXTRA_GENDER = "gender";
	public static final String EXTRA_EXPRESSION = "expr";

	public static final int RESULT_OK = 1;
	public static final int RESULT_ERROR = 0;

	public static final String REQUEST_AGE = "age";
	public static final String REQUEST_GENDER = "gender";
	public static final String REQUEST_EXPRESSION = "expr";
	public static final String REQUEST_ALL = "all";

	// ===== Permissions =====
	private static final int RC_CAMERA = 2001;

	// ===== CameraX =====
	private ImageCapture imageCapture;
	private ExecutorService cameraExecutor;

	// ===== ML =====
	private FaceDetector faceDetector;
	private Interpreter ageInterpreter;
	private Interpreter genderInterpreter;

	// ===== Params =====
	private static final float FACE_PAD_FRAC = 0.15f;
	private static final float MIN_FACE_AREA_FRAC = 0.06f;

	// gender mapping
	private static final int DEFAULT_MALE_INDEX = 0; // set 1 if your model has male at index 1
	private int MALE_INDEX = DEFAULT_MALE_INDEX;
	private String[] genderLabels = new String[]{"Male", "Female"};

	// request/receiver
	private @Nullable String request;
	private @Nullable ResultReceiver receiver;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// No setContentView -> headless

		// Read extras
		request = getIntent().getStringExtra(EXTRA_REQUEST);
		if (Build.VERSION.SDK_INT >= 33) {
			receiver = getIntent().getParcelableExtra(EXTRA_RECEIVER, ResultReceiver.class);
		} else {
			receiver = getIntent().getParcelableExtra(EXTRA_RECEIVER);
		}
		if (receiver == null || request == null) {
			deliverError("Missing request/receiver");
			finish();
			return;
		}

		cameraExecutor = Executors.newSingleThreadExecutor();
		setupFaceDetector();
		loadTFLiteModels();
		loadGenderIndexOverrideIfAny();
		loadGenderLabelsIfAny();

		ensurePermissionAndStart();
	}

	// ===== Permissions =====
	private void ensurePermissionAndStart() {
		if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
				== PackageManager.PERMISSION_GRANTED) {
			startSingleShotFlow();
		} else {
			ActivityCompat.requestPermissions(this,
					new String[]{Manifest.permission.CAMERA}, RC_CAMERA);
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
										   @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (requestCode == RC_CAMERA) {
			if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				startSingleShotFlow();
			} else {
				deliverError("Camera permission denied");
				finish();
			}
		}
	}

	// ===== Face Detector =====
	private void setupFaceDetector() {
		FaceDetectorOptions options = new FaceDetectorOptions.Builder()
				.setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
				.setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
				.setContourMode(FaceDetectorOptions.CONTOUR_MODE_NONE)
				.setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
				.enableTracking()
				.build();
		faceDetector = FaceDetection.getClient(options);
	}

	// ===== TFLite =====
	private void loadTFLiteModels() {
		try {
			MappedByteBuffer ageMbb = loadModelFromAssetsToMapped("model_lite_age_q.tflite");
			MappedByteBuffer genderMbb = loadModelFromAssetsToMapped("model_lite_gender_q.tflite");
			ageInterpreter = new Interpreter(ageMbb, new Interpreter.Options());
			genderInterpreter = new Interpreter(genderMbb, new Interpreter.Options());
		} catch (Exception e) {
			deliverError("Failed to load models: " + e.getMessage());
			finish();
		}
	}

	private void loadGenderIndexOverrideIfAny() {
		try (InputStream is = getAssets().open("gender_index.txt");
			 BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
			String line = br.readLine();
			if (line != null) {
				line = line.trim();
				if ("0".equals(line) || "1".equals(line)) {
					MALE_INDEX = Integer.parseInt(line);
				}
			}
		} catch (Exception ignore) { /* keep default */ }
	}

	private void loadGenderLabelsIfAny() {
		try (InputStream is = getAssets().open("labels_gender.txt");
			 BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
			List<String> lines = new ArrayList<>();
			String line;
			while ((line = br.readLine()) != null) {
				line = line.trim();
				if (!line.isEmpty()) lines.add(line);
			}
			if (lines.size() >= 2) {
				genderLabels = new String[]{lines.get(0), lines.get(1)};
			}
		} catch (Exception ignore) { /* default */ }
	}

	private MappedByteBuffer loadModelFromAssetsToMapped(String assetName) throws Exception {
		File dir = new File(getFilesDir(), "tflite_models");
		if (!dir.exists()) dir.mkdirs();
		File outFile = new File(dir, assetName);
		if (!outFile.exists()) {
			try (InputStream is = getAssets().open(assetName);
				 FileOutputStream fos = new FileOutputStream(outFile)) {
				byte[] buf = new byte[8192];
				int r;
				while ((r = is.read(buf)) != -1) fos.write(buf, 0, r);
				fos.flush();
			}
		}
		try (RandomAccessFile raf = new RandomAccessFile(outFile, "r");
			 FileChannel channel = raf.getChannel()) {
			return channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
		}
	}

	// ===== Camera single-shot =====
	private void startSingleShotFlow() {
		final ListenableFuture<ProcessCameraProvider> providerFuture =
				ProcessCameraProvider.getInstance(this);

		providerFuture.addListener(() -> {
			try {
				ProcessCameraProvider cameraProvider = providerFuture.get();

				imageCapture = new ImageCapture.Builder()
						.setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
						.setTargetResolution(new Size(1280, 720))
						.setFlashMode(ImageCapture.FLASH_MODE_OFF)
						.build();

				CameraSelector front = new CameraSelector.Builder()
						.requireLensFacing(CameraSelector.LENS_FACING_FRONT)
						.build();

				cameraProvider.unbindAll();
				cameraProvider.bindToLifecycle(this, front, imageCapture);

				// take one shot
				getWindow().getDecorView().postDelayed(this::takeOnePicture, 200);

			} catch (Exception e) {
				deliverError("Camera init error: " + e.getMessage());
				finish();
			}
		}, ContextCompat.getMainExecutor(this));
	}

	private void takeOnePicture() {
		if (imageCapture == null) {
			deliverError("ImageCapture not ready");
			finish();
			return;
		}
		imageCapture.takePicture(
				Executors.newSingleThreadExecutor(),
				new ImageCapture.OnImageCapturedCallback() {
					@Override public void onCaptureSuccess(@NonNull ImageProxy imageProxy) {
						handleCapturedImage(imageProxy);
					}
					@Override public void onError(@NonNull ImageCaptureException ex) {
						deliverError("Capture failed: " + ex.getMessage());
						finish();
					}
				}
		);
	}

	private void handleCapturedImage(@NonNull ImageProxy imageProxy) {
		try {
			Bitmap frame = imageProxyToBitmap(imageProxy);
			int rotationDegrees = imageProxy.getImageInfo().getRotationDegrees();
			imageProxy.close();

			if (frame == null) {
				deliverError("Empty image");
				finish();
				return;
			}

			Bitmap rotated = rotateAndMirror(frame, rotationDegrees, true);
			InputImage input = InputImage.fromBitmap(rotated, 0);

			faceDetector.process(input)
					.addOnSuccessListener(faces -> {
						if (faces == null || faces.isEmpty()) {
							deliverError("No face detected");
							finish();
							return;
						}

						Face face = Collections.max(faces, (f1, f2) -> {
							int a1 = f1.getBoundingBox().width() * f1.getBoundingBox().height();
							int a2 = f2.getBoundingBox().width() * f2.getBoundingBox().height();
							return a1 - a2;
						});

						float imgArea = rotated.getWidth() * rotated.getHeight();
						float faceArea = face.getBoundingBox().width() * face.getBoundingBox().height();
						if (faceArea / imgArea < MIN_FACE_AREA_FRAC) {
							deliverError("Face too small/far");
							finish();
							return;
						}

						Rect padded = padRect(face.getBoundingBox(),
								rotated.getWidth(), rotated.getHeight(), FACE_PAD_FRAC);
						Bitmap faceCrop = cropToBox(rotated, padded);
						if (faceCrop == null) {
							deliverError("Face crop failed");
							finish();
							return;
						}

						cameraExecutor.execute(() -> {
							try {
								Integer ageOut = null;
								String genderOut = null;
								String exprOut = null;

								if (REQUEST_AGE.equals(request) || REQUEST_ALL.equals(request)) {
									ageOut = Math.max(0, Math.round(inferAge(faceCrop)));
								}
								if (REQUEST_GENDER.equals(request) || REQUEST_ALL.equals(request)) {
									genderOut = inferGender(faceCrop);
								}
								if (REQUEST_EXPRESSION.equals(request) || REQUEST_ALL.equals(request)) {
									exprOut = inferExpression(face);
								}

								deliverSuccess(ageOut, genderOut, exprOut);
								finish();
							} catch (Throwable t) {
								deliverError("Inference error: " + t.getMessage());
								finish();
							}
						});
					})
					.addOnFailureListener(e -> {
						deliverError("Face detection failed: " + e.getMessage());
						finish();
					});

		} catch (Exception e) {
			deliverError("Process error: " + e.getMessage());
			finish();
		}
	}

	// ===== Expression rules (uses ML Kit attributes only) =====
	private String inferExpression(@NonNull Face face) {
		if (face.getSmilingProbability() == null
				|| face.getLeftEyeOpenProbability() == null
				|| face.getRightEyeOpenProbability() == null) {
			return "Unknown";
		}
		float sp = face.getSmilingProbability();
		float leftEye = face.getLeftEyeOpenProbability();
		float rightEye = face.getRightEyeOpenProbability();
		float avgEye = (leftEye + rightEye) / 2.0f;

		if (sp > 0.8f && avgEye > 0.8f) return "Happy";
		else if (sp < 0.1f && avgEye < 0.3f) return "Sad";
		else if (sp < 0.1f && avgEye > 0.7f) return "Angry";
		else if (sp > 0.3f && avgEye > 0.8f) return "Surprised";
		else if (sp < 0.1f && avgEye < 0.5f) return "Disgusted";
		else if (avgEye > 0.8f) return "Fearful";
		else if (sp < 0.2f && avgEye > 0.5f) return "Contempt";
		else if (sp > 0.2f && sp < 0.6f && avgEye > 0.5f) return "Neutral";
		else if (avgEye < 0.3f) return "Bored";
		else if (leftEye > 0.7f && rightEye < 0.3f) return "Confused";
		else return "Neutral";
	}

	// ===== Age/Gender inference =====
	private float inferAge(Bitmap faceBmp) {
		if (ageInterpreter == null) return 0f;

		Tensor in = ageInterpreter.getInputTensor(0);
		int[] ishape = in.shape();
		int inH = ishape.length >= 2 ? ishape[1] : 64;
		int inW = ishape.length >= 3 ? ishape[2] : inH;
		int inC = ishape.length >= 4 ? ishape[3] : 3;
		DataType inType = in.dataType();

		Bitmap resized = Bitmap.createScaledBitmap(faceBmp, inW, inH, true);
		ByteBuffer inputBuf = makeInputBuffer(resized, inType, inC, quantParams(in));

		Tensor out = ageInterpreter.getOutputTensor(0);
		int[] oshape = out.shape();
		DataType outType = out.dataType();
		int outLen = (oshape.length >= 2) ? oshape[1] : 1;

		Object outObj = (outType == DataType.FLOAT32) ? new float[1][outLen] : new byte[1][outLen];
		ageInterpreter.run(inputBuf, outObj);

		float[] v = toDequantizedVector(outObj, outType, quantParams(out), outLen);
		if (outLen == 1) return v[0] * 116f; // many age models: 0..1 normalized
		return expectation(v);
	}

	private String inferGender(Bitmap faceBmp) {
		if (genderInterpreter == null) return "Unknown";

		Tensor in = genderInterpreter.getInputTensor(0);
		int[] ishape = in.shape();
		int inH = ishape.length >= 2 ? ishape[1] : 64;
		int inW = ishape.length >= 3 ? ishape[2] : inH;
		int inC = ishape.length >= 4 ? ishape[3] : 3;
		DataType inType = in.dataType();

		Bitmap resized = Bitmap.createScaledBitmap(faceBmp, inW, inH, true);
		ByteBuffer inputBuf = makeInputBuffer(resized, inType, inC, quantParams(in));

		Tensor out = genderInterpreter.getOutputTensor(0);
		int[] oshape = out.shape();  // [1,1] or [1,2]
		DataType outType = out.dataType();
		int outLen = (oshape.length >= 2) ? oshape[1] : 1;

		Object outObj = (outType == DataType.FLOAT32) ? new float[1][outLen] : new byte[1][outLen];
		genderInterpreter.run(inputBuf, outObj);

		float[] v = toDequantizedVector(outObj, outType, quantParams(out), outLen);

		if (outLen == 1) {
			float maleProb = v[0]; // assume single output = P(male)
			return maleProb >= 0.5f ? genderLabels[MALE_INDEX]
					: genderLabels[(MALE_INDEX == 0) ? 1 : 0];
		} else {
			int femaleIndex = (MALE_INDEX == 0) ? 1 : 0;
			return (v[MALE_INDEX] >= v[femaleIndex]) ? genderLabels[MALE_INDEX]
					: genderLabels[femaleIndex];
		}
	}

	// ===== Tensor helpers =====
	private static final class QParams {
		final float scale; final int zeroPoint;
		QParams(float s, int z) { this.scale = s; this.zeroPoint = z; }
	}

	private QParams quantParams(Tensor t) {
		try {
			// Try public method (older/newer TF Lite jars vary)
			java.lang.reflect.Method m = t.getClass().getMethod("getQuantizationParams");
			Object qp = m.invoke(t);
			if (qp != null) {
				float s = ((Number) qp.getClass().getMethod("getScale").invoke(qp)).floatValue();
				int z = ((Number) qp.getClass().getMethod("getZeroPoint").invoke(qp)).intValue();
				return new QParams(s, z);
			}
		} catch (Throwable ignore) { }
		try {
			// Fallback
			java.lang.reflect.Method m2 = t.getClass().getMethod("quantizationParams");
			Object qp2 = m2.invoke(t);
			if (qp2 != null) {
				float s = ((Number) qp2.getClass().getMethod("getScale").invoke(qp2)).floatValue();
				int z = ((Number) qp2.getClass().getMethod("getZeroPoint").invoke(qp2)).intValue();
				return new QParams(s, z);
			}
		} catch (Throwable ignore) { }
		return null;
	}

	private static int clamp(int v, int lo, int hi) { return Math.max(lo, Math.min(hi, v)); }

	private ByteBuffer makeInputBuffer(Bitmap bmp, DataType inType, int channels, QParams q) {
		int w = bmp.getWidth(), h = bmp.getHeight();
		int[] px = new int[w * h];
		bmp.getPixels(px, 0, w, 0, 0, w, h);

		if (inType == DataType.FLOAT32) {
			ByteBuffer buf = ByteBuffer.allocateDirect(4 * w * h * channels).order(ByteOrder.nativeOrder());
			for (int p : px) {
				float r = ((p >> 16) & 0xFF) / 255f;
				float g = ((p >> 8) & 0xFF) / 255f;
				float b = (p & 0xFF) / 255f;
				if (channels == 3) { buf.putFloat(r); buf.putFloat(g); buf.putFloat(b); }
				else { float y = (0.299f*r + 0.587f*g + 0.114f*b); buf.putFloat(y); }
			}
			buf.rewind();
			return buf;
		}

		float scale = (q != null) ? q.scale : 1f;
		int zp = (q != null) ? q.zeroPoint : 0;

		if (inType == DataType.UINT8) {
			ByteBuffer buf = ByteBuffer.allocateDirect(w * h * channels).order(ByteOrder.nativeOrder());
			for (int p : px) {
				int r = (p >> 16) & 0xFF, g = (p >> 8) & 0xFF, b = p & 0xFF;
				if (channels == 3) { buf.put((byte) r); buf.put((byte) g); buf.put((byte) b); }
				else { int y = (int)(0.299f*r + 0.587f*g + 0.114f*b); buf.put((byte) y); }
			}
			buf.rewind();
			return buf;
		}

		if (inType == DataType.INT8) {
			boolean expectNeg1to1 = (Math.abs(scale - (1f/128f)) < 1e-3 && Math.abs(zp) <= 1);
			ByteBuffer buf = ByteBuffer.allocateDirect(w * h * channels).order(ByteOrder.nativeOrder());
			for (int p : px) {
				float r = ((p >> 16) & 0xFF) / 255f;
				float g = ((p >> 8) & 0xFF) / 255f;
				float b = (p & 0xFF) / 255f;
				if (expectNeg1to1) { r = r*2f-1f; g = g*2f-1f; b = b*2f-1f; }
				int qr = clamp(Math.round(r/scale + zp), -128, 127);
				int qg = clamp(Math.round(g/scale + zp), -128, 127);
				int qb = clamp(Math.round(b/scale + zp), -128, 127);
				if (channels == 3) { buf.put((byte) qr); buf.put((byte) qg); buf.put((byte) qb); }
				else {
					float yReal = 0.299f*((qr - zp)*scale) + 0.587f*((qg - zp)*scale) + 0.114f*((qb - zp)*scale);
					int qy = clamp(Math.round(yReal/scale + zp), -128, 127);
					buf.put((byte) qy);
				}
			}
			buf.rewind();
			return buf;
		}

		// Fallback → FLOAT32
		ByteBuffer buf = ByteBuffer.allocateDirect(4 * w * h * channels).order(ByteOrder.nativeOrder());
		for (int p : px) {
			float r = ((p >> 16) & 0xFF) / 255f;
			float g = ((p >> 8) & 0xFF) / 255f;
			float b = (p & 0xFF) / 255f;
			if (channels == 3) { buf.putFloat(r); buf.putFloat(g); buf.putFloat(b); }
			else { float y = (0.299f*r + 0.587f*g + 0.114f*b); buf.putFloat(y); }
		}
		buf.rewind();
		return buf;
	}

	private float[] toDequantizedVector(Object outObj, DataType outType, QParams q, int outLen) {
		float[] out = new float[outLen];
		if (outType == DataType.FLOAT32) {
			float[][] f = (float[][]) outObj;
			System.arraycopy(f[0], 0, out, 0, outLen);
			return out;
		}
		float scale = (q != null) ? q.scale : 1f;
		int zp = (q != null) ? q.zeroPoint : 0;
		byte[][] b = (byte[][]) outObj;
		for (int i = 0; i < outLen; i++) {
			int qv = b[0][i];
			if (outType == DataType.UINT8) qv = qv & 0xFF;
			out[i] = scale * (qv - zp);
		}
		return out;
	}

	private float expectation(float[] probs) {
		float sum = 0f, wsum = 0f;
		for (int i = 0; i < probs.length; i++) { wsum += probs[i]; sum += probs[i] * i; }
		if (wsum == 0f) return 0f;
		return sum / wsum;
	}

	// ===== Imaging =====
	private Bitmap imageProxyToBitmap(ImageProxy proxy) {
		Image image = proxy.getImage();
		if (image == null) return null;

		if (proxy.getFormat() == android.graphics.ImageFormat.JPEG) {
			ByteBuffer buffer = proxy.getPlanes()[0].getBuffer();
			byte[] bytes = new byte[buffer.remaining()];
			buffer.get(bytes);
			return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
		} else {
			int width = proxy.getWidth();
			int height = proxy.getHeight();
			byte[] nv21 = yuv420ToNV21(proxy);
			if (nv21 == null) return null;

			YuvImage yuvImage = new YuvImage(
					nv21,
					android.graphics.ImageFormat.NV21,
					width,
					height,
					null
			);
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			yuvImage.compressToJpeg(new Rect(0, 0, width, height), 92, out);
			byte[] imageBytes = out.toByteArray();
			return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
		}
	}

	private byte[] yuv420ToNV21(ImageProxy image) {
		ImageProxy.PlaneProxy[] planes = image.getPlanes();
		byte[] out = new byte[image.getWidth() * image.getHeight() * 3 / 2];
		int width = image.getWidth();
		int height = image.getHeight();

		copyPlane(planes[0], width, height, out, 0, 1);

		int chromaHeight = height / 2;
		int chromaWidth = width / 2;
		int outputOffset = width * height;

		copyChromaPlaneVU(planes[2], planes[1], chromaWidth, chromaHeight, out, outputOffset);
		return out;
	}

	private void copyPlane(ImageProxy.PlaneProxy plane, int width, int height,
						   byte[] out, int outOffset, int outPixelStride) {
		ByteBuffer buffer = plane.getBuffer();
		int rowStride = plane.getRowStride();
		int pixelStride = plane.getPixelStride();

		int pos = outOffset;
		byte[] row = new byte[rowStride];
		for (int rowIndex = 0; rowIndex < height; rowIndex++) {
			int length;
			if (pixelStride == 1) {
				length = width;
				buffer.get(out, pos, length);
				pos += length * outPixelStride;
				buffer.position(buffer.position() + rowStride - length);
			} else {
				length = (width - 1) * pixelStride + 1;
				buffer.get(row, 0, length);
				for (int col = 0; col < width; col++) {
					out[pos] = row[col * pixelStride];
					pos += outPixelStride;
				}
				if (length < rowStride) buffer.position(buffer.position() + (rowStride - length));
			}
		}
	}

	private void copyChromaPlaneVU(ImageProxy.PlaneProxy vPlane, ImageProxy.PlaneProxy uPlane,
								   int chromaWidth, int chromaHeight, byte[] out, int outOffset) {
		ByteBuffer vBuf = vPlane.getBuffer();
		ByteBuffer uBuf = uPlane.getBuffer();
		int vRowStride = vPlane.getRowStride();
		int vPixelStride = vPlane.getPixelStride();
		int uRowStride = uPlane.getRowStride();
		int uPixelStride = uPlane.getPixelStride();

		byte[] vRow = new byte[vRowStride];
		byte[] uRow = new byte[uRowStride];

		int pos = outOffset;

		for (int row = 0; row < chromaHeight; row++) {
			int vLength = (chromaWidth - 1) * vPixelStride + 1;
			int uLength = (chromaWidth - 1) * uPixelStride + 1;

			vBuf.get(vRow, 0, vLength);
			uBuf.get(uRow, 0, uLength);

			for (int col = 0; col < chromaWidth; col++) {
				byte v = vRow[col * vPixelStride];
				byte u = uRow[col * uPixelStride];
				out[pos++] = v; // NV21: V first
				out[pos++] = u; // then U
			}

			if (vLength < vRowStride) vBuf.position(vBuf.position() + (vRowStride - vLength));
			if (uLength < uRowStride) uBuf.position(uBuf.position() + (uRowStride - uLength));
		}
	}

	private Bitmap rotateAndMirror(Bitmap src, int rotationDegrees, boolean mirrorHorizontally) {
		Matrix m = new Matrix();
		if (rotationDegrees != 0) m.postRotate(rotationDegrees);
		if (mirrorHorizontally) {
			m.postScale(-1f, 1f);
			m.postTranslate(src.getWidth(), 0);
		}
		return Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), m, true);
	}

	private Rect padRect(Rect b, int imgW, int imgH, float padFrac) {
		int w = b.width(), h = b.height();
		int padW = Math.round(w * padFrac);
		int padH = Math.round(h * padFrac);
		Rect r = new Rect(b.left - padW, b.top - padH, b.right + padW, b.bottom + padH);
		r.left = Math.max(0, r.left);
		r.top = Math.max(0, r.top);
		r.right = Math.min(imgW, r.right);
		r.bottom = Math.min(imgH, r.bottom);
		return r;
	}

	private Bitmap cropToBox(Bitmap src, Rect box) {
		int left = Math.max(0, box.left);
		int top = Math.max(0, box.top);
		int right = Math.min(src.getWidth(), box.right);
		int bottom = Math.min(src.getHeight(), box.bottom);
		if (right > left && bottom > top) {
			return Bitmap.createBitmap(src, left, top, right - left, bottom - top);
		}
		return null;
	}

	// ===== Deliver results via ResultReceiver =====
	private void deliverSuccess(@Nullable Integer age, @Nullable String gender, @Nullable String expr) {
		if (receiver == null) return;
		Bundle b = new Bundle();
		if (REQUEST_ALL.equals(request)) {
			if (age != null) b.putInt(EXTRA_AGE, age);
			if (gender != null) b.putString(EXTRA_GENDER, gender);
			if (expr != null) b.putString(EXTRA_EXPRESSION, expr);
			receiver.send(RESULT_OK, b);
			return;
		}
		switch (request) {
			case REQUEST_AGE:
				if (age != null) b.putInt(EXTRA_AGE, age);
				receiver.send(RESULT_OK, b);
				break;
			case REQUEST_GENDER:
				if (gender != null) b.putString(EXTRA_GENDER, gender);
				receiver.send(RESULT_OK, b);
				break;
			case REQUEST_EXPRESSION:
				if (expr != null) b.putString(EXTRA_EXPRESSION, expr);
				receiver.send(RESULT_OK, b);
				break;
			default:
				b.putString(EXTRA_ERROR, "Unknown request: " + request);
				receiver.send(RESULT_ERROR, b);
		}
	}

	private void deliverError(@NonNull String err) {
		if (receiver == null) return;
		Bundle b = new Bundle();
		b.putString(EXTRA_ERROR, err);
		receiver.send(RESULT_ERROR, b);
	}

	// ===== Cleanup =====
	@Override
	protected void onDestroy() {
		super.onDestroy();
		try { if (faceDetector != null) faceDetector.close(); } catch (Exception ignore) {}
		try { if (ageInterpreter != null) ageInterpreter.close(); } catch (Exception ignore) {}
		try { if (genderInterpreter != null) genderInterpreter.close(); } catch (Exception ignore) {}
		try { if (cameraExecutor != null) cameraExecutor.shutdown(); } catch (Exception ignore) {}
	}
}

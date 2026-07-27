package org.catrobat.catroid.FaceRecognizer;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import org.catrobat.catroid.FaceRecognizer.env.FileUtils;
import org.catrobat.catroid.FaceRecognizer.ml.BlazeFace;
import org.catrobat.catroid.FaceRecognizer.ml.FaceNet;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Turns a full resolution photo into one L2 normalised FaceNet embedding.
 *
 * Training and detection both go through this class, so the crop size, the crop
 * margin and the normalisation are identical on both sides. That symmetry is what
 * makes distance matching work.
 */
public class FaceEmbedder {
	public static final String TAG = "FaceEmbedder";

	/** Extra context around the BlazeFace box, as a fraction of the box size. */
	private static final float BOX_MARGIN = 0.20f;

	/** Faces smaller than this in the source photo are too blurry to enrol or match. */
	private static final int MIN_FACE_PX = 56;

	private static final int[] ROTATIONS = {0, 90, 180, 270};

	/**
	 * How tightly the face is cropped, as a multiplier on the box size.
	 * A camera may frame a face tighter or looser than a gallery photo did, and
	 * FaceNet is sensitive to that. Embedding several tightnesses and keeping the
	 * best match removes the difference.
	 */
	private static final float[] CROP_SCALES = {0.85f, 1.0f, 1.18f};

	// ---- Alignment template, in the 160x160 network input ----
	/** Where the midpoint between the eyes is placed. */
	private static final float EYE_MID_X = 80f;
	private static final float EYE_MID_Y = 72f;
	/** Distance between the eyes. The three values act as crop tightness. */
	private static final float[] EYE_DISTANCES = {46f, 53f, 61f};
	private static final int NET_SIZE = 160;

	private final BlazeFace blazeFace;
	private final FaceNet faceNet;

	/** Why the last call returned null. Shown to the user so failures are not silent. */
	public String lastProblem = "";

	/**
	 * When set, the exact crop handed to FaceNet is written to the face data folder.
	 * Set it to "crop_train" before enrolling and "crop_detect" before recognising,
	 * then compare the two PNGs. If they are not both a centred face, the bug is in
	 * the crop, not in the matching.
	 */
	public static String debugCropName = null;

	/** Eyes from the most recent largestFaceRect call, in frame coordinates. */
	private float[] lastLeftEye;
	private float[] lastRightEye;

	private static final Rect FULL_NET_RECT = new Rect(0, 0, NET_SIZE, NET_SIZE);
	private static final Paint ALIGN_PAINT =
			new Paint(Paint.FILTER_BITMAP_FLAG | Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);

	/** A located face plus the frame it was located in. */
	public static class Face {
		public final Bitmap frame;
		public final Rect box;
		public final int rotation;
		/** Eye positions in frame coordinates, or null when unavailable. */
		public final float[] leftEye;
		public final float[] rightEye;

		Face(Bitmap frame, Rect box, int rotation, float[] leftEye, float[] rightEye) {
			this.frame = frame;
			this.box = box;
			this.rotation = rotation;
			this.leftEye = leftEye;
			this.rightEye = rightEye;
		}

		public boolean hasEyes() {
			return leftEye != null && rightEye != null;
		}

		/** Recycles the rotated copy, if one was made. Never recycles the caller's bitmap. */
		public void release(Bitmap original) {
			if (frame != original && frame != null && !frame.isRecycled()) {
				frame.recycle();
			}
		}
	}

	private FaceEmbedder(BlazeFace blazeFace, FaceNet faceNet) {
		this.blazeFace = blazeFace;
		this.faceNet = faceNet;
	}

	public static FaceEmbedder create(AssetManager assetManager) {
		return new FaceEmbedder(BlazeFace.create(assetManager), FaceNet.create(assetManager));
	}

	/**
	 * Finds the largest face, trying all four rotations so that photos with wrong or
	 * missing EXIF orientation still work. Returns null when no usable face is found.
	 * The caller must call face.release(sourceBitmap) when done.
	 */
	@RequiresApi(api = Build.VERSION_CODES.N)
	public synchronized Face findBestFace(Bitmap source) {
		lastProblem = "";
		if (source == null || source.isRecycled()) {
			lastProblem = "no bitmap";
			return null;
		}

		// Upright first, and the FIRST rotation that finds a face wins.
		// Picking the largest box across all four rotations was wrong: a sideways
		// or false detection with a bigger box could beat the real upright face,
		// and gallery photos and camera frames would then land on different
		// rotations, producing crops that could never match each other.
		for (int rotation : ROTATIONS) {
			Bitmap frame;
			try {
				frame = (rotation == 0) ? source : rotate(source, rotation);
			} catch (OutOfMemoryError oom) {
				Log.w(TAG, "Out of memory rotating to " + rotation);
				lastProblem = "out of memory while searching rotations";
				return null;
			}
			if (frame == null) {
				continue;
			}

			Rect box = largestFaceRect(frame);
			if (box != null) {
				if (rotation != 0) {
					Log.i(TAG, "Face found only after rotating " + rotation + " degrees");
				}
				return new Face(frame, box, rotation, lastLeftEye, lastRightEye);
			}
			if (frame != source) {
				frame.recycle();
			}
		}

		if (lastProblem.isEmpty()) {
			lastProblem = "no face detected at any rotation";
		}
		return null;
	}

	/** Embeds a known box. Returns a normalised float[EMBEDDING_SIZE] or null. */
	public synchronized float[] embed(Bitmap frame, Rect box) {
		if (frame == null || frame.isRecycled() || box == null
				|| box.width() <= 0 || box.height() <= 0) {
			lastProblem = "invalid crop";
			return null;
		}
		try {
			if (debugCropName != null) {
				try {
					Bitmap crop = Bitmap.createBitmap(frame,
							box.left, box.top, box.width(), box.height());
					FileUtils.saveBitmap(crop, debugCropName + ".png");
					crop.recycle();
				} catch (Throwable ignored) {
					// diagnostics must never break the real work
				}
			}

			Log.i(TAG, "Crop " + box.width() + "x" + box.height()
					+ " at (" + box.left + "," + box.top + ") in frame "
					+ frame.getWidth() + "x" + frame.getHeight());

			FloatBuffer buffer = faceNet.getEmbeddings(frame, box);
			if (buffer == null) {
				lastProblem = "FaceNet returned nothing";
				return null;
			}

			// Do NOT rely on the buffer position or limit. Depending on the TFLite
			// build, run() may or may not advance the position, and the flip() inside
			// FaceNet can leave the limit at zero. Reset explicitly and read from 0.
			buffer.clear();
			if (buffer.remaining() < FaceNet.EMBEDDING_SIZE) {
				lastProblem = "embedding buffer too small: " + buffer.remaining();
				return null;
			}

			float[] embedding = new float[FaceNet.EMBEDDING_SIZE];
			buffer.get(embedding);

			float[] normalized = normalize(embedding);
			if (normalized == null) {
				lastProblem = "embedding was all zeros";
			}
			return normalized;
		} catch (Exception e) {
			Log.e(TAG, "Embedding failed", e);
			lastProblem = "embedding error: " + e.getClass().getSimpleName();
			return null;
		}
	}

	/**
	 * Embeds the same face several ways: three crop tightnesses, each optionally
	 * mirrored. Used on both sides. At enrolment every variant is stored, at
	 * detection every variant is scored and the best match wins.
	 */
	public synchronized List<float[]> embedVariants(Bitmap frame, Rect box, boolean includeMirror) {
		return embedVariants(frame, box, includeMirror, null, null);
	}

	public synchronized List<float[]> embedVariants(Bitmap frame, Rect box,
			boolean includeMirror, float[] leftEye, float[] rightEye) {
		List<float[]> out = new ArrayList<>();
		if (frame == null || frame.isRecycled() || box == null) {
			return out;
		}

		// Aligned path. Rotating the face so the eyes are level, and placing them at
		// a fixed spot, removes head tilt, scale and position as sources of
		// variation. FaceNet was trained on aligned faces, and doing this on both
		// the gallery side and the camera side is what makes them comparable.
		if (leftEye != null && rightEye != null) {
			for (float eyeDistance : EYE_DISTANCES) {
				Bitmap aligned = alignFace(frame, leftEye, rightEye, eyeDistance, false);
				if (aligned != null) {
					float[] e = embedAligned(aligned);
					aligned.recycle();
					if (e != null) {
						out.add(e);
					}
				}
				if (!includeMirror) {
					continue;
				}
				Bitmap mirroredAligned =
						alignFace(frame, leftEye, rightEye, eyeDistance, true);
				if (mirroredAligned != null) {
					float[] m = embedAligned(mirroredAligned);
					mirroredAligned.recycle();
					if (m != null) {
						out.add(m);
					}
				}
			}
			if (!out.isEmpty()) {
				return out;
			}
			Log.w(TAG, "Alignment produced nothing, falling back to plain crop");
		}

		for (float scale : CROP_SCALES) {
			Rect scaled = scaleBox(box, scale, frame.getWidth(), frame.getHeight());
			if (scaled == null) {
				continue;
			}
			float[] e = embed(frame, scaled);
			if (e != null) {
				out.add(e);
			}
			if (!includeMirror) {
				continue;
			}
			Bitmap mirrored = null;
			try {
				mirrored = mirror(frame);
				float[] m = embed(mirrored, mirrorRect(scaled, frame.getWidth()));
				if (m != null) {
					out.add(m);
				}
			} catch (Throwable t) {
				Log.w(TAG, "Mirror variant failed", t);
			} finally {
				if (mirrored != null && !mirrored.isRecycled()) {
					mirrored.recycle();
				}
			}
		}
		return out;
	}

	/** Locates the face, then returns every variant of it. */
	@RequiresApi(api = Build.VERSION_CODES.N)
	public synchronized List<float[]> embedAllVariants(Bitmap source, boolean includeMirror) {
		Face face = findBestFace(source);
		if (face == null) {
			return new ArrayList<>();
		}
		try {
			return embedVariants(face.frame, face.box, includeMirror,
					face.leftEye, face.rightEye);
		} finally {
			face.release(source);
		}
	}

	/**
	 * Warps the face into the 160x160 network input so that the eyes are level,
	 * a fixed distance apart, and centred at a fixed point. One similarity
	 * transform does rotation, scale and translation in a single draw, so there is
	 * no extra resampling loss.
	 */
	private Bitmap alignFace(Bitmap frame, float[] leftEye, float[] rightEye,
			float targetEyeDistance, boolean mirrored) {
		float dx = rightEye[0] - leftEye[0];
		float dy = rightEye[1] - leftEye[1];
		float eyeDistance = (float) Math.sqrt(dx * dx + dy * dy);
		if (eyeDistance < 4f) {
			return null;
		}

		float angleDegrees = (float) Math.toDegrees(Math.atan2(dy, dx));
		float scale = targetEyeDistance / eyeDistance;
		float midX = (leftEye[0] + rightEye[0]) / 2f;
		float midY = (leftEye[1] + rightEye[1]) / 2f;

		Matrix matrix = new Matrix();
		matrix.postTranslate(-midX, -midY);
		matrix.postRotate(-angleDegrees);
		matrix.postScale(mirrored ? -scale : scale, scale);
		matrix.postTranslate(EYE_MID_X, EYE_MID_Y);

		try {
			Bitmap aligned = Bitmap.createBitmap(NET_SIZE, NET_SIZE, Bitmap.Config.ARGB_8888);
			Canvas canvas = new Canvas(aligned);
			canvas.drawColor(Color.BLACK);
			canvas.drawBitmap(frame, matrix, ALIGN_PAINT);
			if (debugCropName != null) {
				FileUtils.saveBitmap(aligned, debugCropName + ".png");
			}
			return aligned;
		} catch (Throwable t) {
			Log.w(TAG, "Alignment failed", t);
			return null;
		}
	}

	/** Embeds an already aligned 160x160 bitmap. */
	private float[] embedAligned(Bitmap aligned) {
		try {
			FloatBuffer buffer = faceNet.getEmbeddings(aligned, FULL_NET_RECT);
			if (buffer == null) {
				return null;
			}
			buffer.clear();
			if (buffer.remaining() < FaceNet.EMBEDDING_SIZE) {
				return null;
			}
			float[] embedding = new float[FaceNet.EMBEDDING_SIZE];
			buffer.get(embedding);
			return normalize(embedding);
		} catch (Exception e) {
			Log.e(TAG, "Aligned embedding failed", e);
			return null;
		}
	}

	/** Grows or shrinks a square box around its centre, kept inside the frame. */
	private static Rect scaleBox(Rect box, float scale, int frameWidth, int frameHeight) {
		float cx = box.exactCenterX();
		float cy = box.exactCenterY();
		float half = Math.max(box.width(), box.height()) * scale / 2f;
		half = Math.min(half, Math.min(frameWidth, frameHeight) / 2f);

		RectF r = new RectF(cx - half, cy - half, cx + half, cy + half);
		if (r.left < 0) {
			r.offset(-r.left, 0);
		}
		if (r.top < 0) {
			r.offset(0, -r.top);
		}
		if (r.right > frameWidth) {
			r.offset(frameWidth - r.right, 0);
		}
		if (r.bottom > frameHeight) {
			r.offset(0, frameHeight - r.bottom);
		}

		Rect out = new Rect();
		r.round(out);
		out.left = Math.max(0, out.left);
		out.top = Math.max(0, out.top);
		out.right = Math.min(frameWidth, out.right);
		out.bottom = Math.min(frameHeight, out.bottom);
		return (out.width() < MIN_FACE_PX || out.height() < MIN_FACE_PX) ? null : out;
	}

	/** Convenience: locate and embed in one call. */
	@RequiresApi(api = Build.VERSION_CODES.N)
	public synchronized float[] embedBestFace(Bitmap source) {
		Face face = findBestFace(source);
		if (face == null) {
			return null;
		}
		try {
			return embed(face.frame, face.box);
		} finally {
			face.release(source);
		}
	}

	@RequiresApi(api = Build.VERSION_CODES.N)
	private Rect largestFaceRect(Bitmap frame) {
		lastLeftEye = null;
		lastRightEye = null;

		Bitmap small = null;
		List<BlazeFace.FaceBox> faces;
		try {
			small = Bitmap.createScaledBitmap(frame,
					BlazeFace.INPUT_SIZE_WIDTH, BlazeFace.INPUT_SIZE_HEIGHT, true);
			faces = blazeFace.detectWithLandmarks(small);
		} catch (Exception e) {
			Log.e(TAG, "Detection failed", e);
			return null;
		} finally {
			if (small != null && small != frame && !small.isRecycled()) {
				small.recycle();
			}
		}

		if (faces == null || faces.isEmpty()) {
			return null;
		}

		BlazeFace.FaceBox biggest = null;
		float biggestArea = 0f;
		for (BlazeFace.FaceBox f : faces) {
			float a = f.location.width() * f.location.height();
			if (a > biggestArea) {
				biggestArea = a;
				biggest = f;
			}
		}
		if (biggest == null) {
			return null;
		}

		float scaleX = (float) frame.getWidth() / BlazeFace.INPUT_SIZE_WIDTH;
		float scaleY = (float) frame.getHeight() / BlazeFace.INPUT_SIZE_HEIGHT;

		if (biggest.keypoints != null && biggest.keypoints.length >= 4) {
			float[] a = {biggest.keypoints[0] * scaleX, biggest.keypoints[1] * scaleY};
			float[] b = {biggest.keypoints[2] * scaleX, biggest.keypoints[3] * scaleY};
			// Order by x so left and right are consistent whatever the model returns.
			lastLeftEye = (a[0] <= b[0]) ? a : b;
			lastRightEye = (a[0] <= b[0]) ? b : a;
		}

		RectF scaled = new RectF(
				biggest.location.left * scaleX,
				biggest.location.top * scaleY,
				biggest.location.right * scaleX,
				biggest.location.bottom * scaleY);

		scaled.inset(-scaled.width() * BOX_MARGIN, -scaled.height() * BOX_MARGIN);

		// Make the crop square around the same centre. FaceNet takes a 160x160
		// input, so a rectangular crop gets stretched, and how much it stretches
		// depends on the box shape. That alone makes a portrait photo and a
		// landscape capture of one face produce embeddings that do not match.
		float cx = scaled.centerX();
		float cy = scaled.centerY();
		float half = Math.max(scaled.width(), scaled.height()) / 2f;
		half = Math.min(half, Math.min(frame.getWidth(), frame.getHeight()) / 2f);
		scaled.set(cx - half, cy - half, cx + half, cy + half);

		// Slide the square back inside the frame instead of clipping it, which
		// would make it rectangular again.
		if (scaled.left < 0) {
			scaled.offset(-scaled.left, 0);
		}
		if (scaled.top < 0) {
			scaled.offset(0, -scaled.top);
		}
		if (scaled.right > frame.getWidth()) {
			scaled.offset(frame.getWidth() - scaled.right, 0);
		}
		if (scaled.bottom > frame.getHeight()) {
			scaled.offset(0, frame.getHeight() - scaled.bottom);
		}

		Rect box = new Rect();
		scaled.round(box);
		box.left = Math.max(0, box.left);
		box.top = Math.max(0, box.top);
		box.right = Math.min(frame.getWidth(), box.right);
		box.bottom = Math.min(frame.getHeight(), box.bottom);

		if (box.width() < MIN_FACE_PX || box.height() < MIN_FACE_PX) {
			lastProblem = "face too small: " + box.width() + "x" + box.height()
					+ " px, need " + MIN_FACE_PX;
			return null;
		}
		return box;
	}

	public static Bitmap rotate(Bitmap src, int degrees) {
		if (degrees % 360 == 0) {
			return src;
		}
		Matrix m = new Matrix();
		m.postRotate(degrees);
		return Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), m, true);
	}

	public static Bitmap mirror(Bitmap src) {
		Matrix m = new Matrix();
		m.preScale(-1f, 1f, src.getWidth() / 2f, src.getHeight() / 2f);
		return Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), m, true);
	}

	public static Rect mirrorRect(Rect box, int frameWidth) {
		return new Rect(frameWidth - box.right, box.top, frameWidth - box.left, box.bottom);
	}

	private static long area(Rect r) {
		return (long) r.width() * (long) r.height();
	}

	/** Unit length, so a dot product between two embeddings is the cosine similarity. */
	private static float[] normalize(float[] v) {
		double sum = 0.0;
		for (float value : v) {
			sum += value * value;
		}
		float norm = (float) Math.sqrt(sum);
		if (norm < 1e-10f) {
			return null;
		}
		for (int i = 0; i < v.length; i++) {
			v[i] /= norm;
		}
		return v;
	}

	public void close() {
		blazeFace.close();
		faceNet.close();
	}
}
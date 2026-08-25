package org.catrobat.catroid.FaceRecognizer

import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import org.catrobat.catroid.FaceRecognizer.env.FileUtils.saveBitmap
import org.catrobat.catroid.FaceRecognizer.ml.BlazeFace
import org.catrobat.catroid.FaceRecognizer.ml.BlazeFace.FaceBox
import org.catrobat.catroid.FaceRecognizer.ml.FaceNet
import kotlin.math.atan2
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

/**
 * Turns a full resolution photo into one L2 normalised FaceNet embedding.
 * 
 * Training and detection both go through this class, so the crop size, the crop
 * margin and the normalisation are identical on both sides. That symmetry is what
 * makes distance matching work.
 */
class FaceEmbedder private constructor(
    private val blazeFace: BlazeFace,
    private val faceNet: FaceNet
) {
    /** Why the last call returned null. Shown to the user so failures are not silent.  */
    var lastProblem: String = ""

    /** Eyes from the most recent largestFaceRect call, in frame coordinates.  */
    private var lastLeftEye: FloatArray? = null
    private var lastRightEye: FloatArray? = null

    /** A located face plus the frame it was located in.  */
    class Face internal constructor(
        val frame: Bitmap?, val box: Rect?, val rotation: Int,
        /** Eye positions in frame coordinates, or null when unavailable.  */
        val leftEye: FloatArray?, val rightEye: FloatArray?
    ) {
        fun hasEyes(): Boolean {
            return leftEye != null && rightEye != null
        }

        /** Recycles the rotated copy, if one was made. Never recycles the caller's bitmap.  */
        fun release(original: Bitmap?) {
            if (frame != original && frame != null && !frame.isRecycled()) {
                frame.recycle()
            }
        }
    }

    /**
     * Finds the largest face, trying all four rotations so that photos with wrong or
     * missing EXIF orientation still work. Returns null when no usable face is found.
     * The caller must call face.release(sourceBitmap) when done.
     */
    @RequiresApi(Build.VERSION_CODES.N)
    @Synchronized
    fun findBestFace(source: Bitmap?): Face? {
        lastProblem = ""

        if (source == null || source.isRecycled) {
            lastProblem = "no bitmap"
            return null
        }

        val face = findFaceAcrossRotations(source)

        if (face == null && lastProblem.isEmpty()) {
            lastProblem = "no face detected at any rotation"
        }

        return face
    }

    private fun findFaceAcrossRotations(source: Bitmap): Face? {
        // Upright is checked first. The first rotation containing a face wins,
        // ensuring gallery photos and camera frames use consistent face crops.
        for (rotation in ROTATIONS) {
            val frame = createRotatedFrame(source, rotation) ?: return null
            val box = largestFaceRect(frame)

            if (box != null) {
                logAppliedRotation(rotation)

                return Face(
                    frame,
                    box,
                    rotation,
                    lastLeftEye,
                    lastRightEye
                )
            }

            recycleRotatedFrame(frame, source)
        }

        return null
    }

    private fun createRotatedFrame(
        source: Bitmap,
        rotation: Int
    ): Bitmap? {
        return try {
            if (rotation == 0) {
                source
            } else {
                rotate(source, rotation)
            }
        } catch (error: OutOfMemoryError) {
            Log.w(TAG, "Out of memory rotating to $rotation", error)
            lastProblem = "out of memory while searching rotations"
            null
        }
    }

    private fun logAppliedRotation(rotation: Int) {
        if (rotation != 0) {
            Log.i(TAG, "Face found only after rotating $rotation degrees")
        }
    }

    private fun recycleRotatedFrame(
        frame: Bitmap,
        source: Bitmap
    ) {
        if (frame !== source && !frame.isRecycled) {
            frame.recycle()
        }
    }

    /** Embeds a known box. Returns a normalised float[EMBEDDING_SIZE] or null.  */
    @Synchronized
    fun embed(frame: Bitmap?, box: Rect?): FloatArray? {
        if (frame == null || frame.isRecycled() || box == null || box.width() <= 0 || box.height() <= 0) {
            lastProblem = "invalid crop"
            return null
        }
        try {
            if (debugCropName != null) {
                try {
                    val crop = Bitmap.createBitmap(
                        frame,
                        box.left, box.top, box.width(), box.height()
                    )
                    saveBitmap(crop, debugCropName + ".png")
                    crop.recycle()
                } catch (ignored: Throwable) {
                    // diagnostics must never break the real work
                }
            }

            Log.i(
                TAG, ("Crop " + box.width() + "x" + box.height()
                    + " at (" + box.left + "," + box.top + ") in frame "
                    + frame.getWidth() + "x" + frame.getHeight())
            )

            val buffer = faceNet.getEmbeddings(frame, box)
            if (buffer == null) {
                lastProblem = "FaceNet returned nothing"
                return null
            }

            // Do NOT rely on the buffer position or limit. Depending on the TFLite
            // build, run() may or may not advance the position, and the flip() inside
            // FaceNet can leave the limit at zero. Reset explicitly and read from 0.
            buffer.clear()
            if (buffer.remaining() < FaceNet.EMBEDDING_SIZE) {
                lastProblem = "embedding buffer too small: " + buffer.remaining()
                return null
            }

            val embedding = FloatArray(FaceNet.EMBEDDING_SIZE)
            buffer.get(embedding)

            val normalized: FloatArray? = normalize(embedding)
            if (normalized == null) {
                lastProblem = "embedding was all zeros"
            }
            return normalized
        } catch (e: Exception) {
            Log.e(TAG, "Embedding failed", e)
            lastProblem = "embedding error: " + e.javaClass.getSimpleName()
            return null
        }
    }

    /**
     * Embeds the same face several ways: three crop tightnesses, each optionally
     * mirrored. Used on both sides. At enrolment every variant is stored, at
     * detection every variant is scored and the best match wins.
     */
    @Synchronized
    fun embedVariants(
        frame: Bitmap?,
        box: Rect?,
        includeMirror: Boolean
    ): MutableList<FloatArray> {
        return embedVariants(frame, box, includeMirror, null, null)
    }

    @Synchronized
    fun embedVariants(
        frame: Bitmap?,
        box: Rect?,
        includeMirror: Boolean,
        leftEye: FloatArray?,
        rightEye: FloatArray?
    ): MutableList<FloatArray> {
        val embeddings = mutableListOf<FloatArray>()

        if (!isValidInput(frame, box)) {
            return embeddings
        }

        frame ?: return embeddings
        box ?: return embeddings

        addAlignedVariants(
            embeddings,
            frame,
            includeMirror,
            leftEye,
            rightEye
        )

        if (embeddings.isNotEmpty()) {
            return embeddings
        }

        logAlignmentFallback(leftEye, rightEye)
        addCropVariants(embeddings, frame, box, includeMirror)

        return embeddings
    }

    private fun isValidInput(
        frame: Bitmap?,
        box: Rect?
    ): Boolean =
        frame != null && !frame.isRecycled && box != null

    private fun addAlignedVariants(
        embeddings: MutableList<FloatArray>,
        frame: Bitmap,
        includeMirror: Boolean,
        leftEye: FloatArray?,
        rightEye: FloatArray?
    ) {
        if (leftEye == null || rightEye == null) {
            return
        }

        for (eyeDistance in EYE_DISTANCES) {
            addAlignedVariant(
                embeddings,
                frame,
                leftEye,
                rightEye,
                eyeDistance,
                mirror = false
            )

            if (includeMirror) {
                addAlignedVariant(
                    embeddings,
                    frame,
                    leftEye,
                    rightEye,
                    eyeDistance,
                    mirror = true
                )
            }
        }
    }

    private fun addAlignedVariant(
        embeddings: MutableList<FloatArray>,
        frame: Bitmap,
        leftEye: FloatArray,
        rightEye: FloatArray,
        eyeDistance: Float,
        mirror: Boolean
    ) {
        val aligned = alignFace(
            frame,
            leftEye,
            rightEye,
            eyeDistance,
            mirror
        ) ?: return

        try {
            embedAligned(aligned)?.let(embeddings::add)
        } finally {
            recycleSafely(aligned)
        }
    }

    private fun logAlignmentFallback(
        leftEye: FloatArray?,
        rightEye: FloatArray?
    ) {
        if (leftEye != null && rightEye != null) {
            Log.w(
                TAG,
                "Alignment produced nothing, falling back to plain crop"
            )
        }
    }

    private fun addCropVariants(
        embeddings: MutableList<FloatArray>,
        frame: Bitmap,
        box: Rect,
        includeMirror: Boolean
    ) {
        for (scale in CROP_SCALES) {
            val scaledBox = scaleBox(
                box,
                scale,
                frame.width,
                frame.height
            ) ?: continue

            embed(frame, scaledBox)?.let(embeddings::add)

            if (includeMirror) {
                addMirroredCropVariant(
                    embeddings,
                    frame,
                    scaledBox
                )
            }
        }
    }

    private fun addMirroredCropVariant(
        embeddings: MutableList<FloatArray>,
        frame: Bitmap,
        scaledBox: Rect
    ) {
        var mirroredFrame: Bitmap? = null

        try {
            mirroredFrame = mirror(frame)

            val mirroredBox = mirrorRect(
                scaledBox,
                frame.width
            )

            embed(mirroredFrame, mirroredBox)?.let(embeddings::add)
        } catch (error: Throwable) {
            Log.w(TAG, "Mirror variant failed", error)
        } finally {
            recycleSafely(mirroredFrame)
        }
    }

    private fun recycleSafely(bitmap: Bitmap?) {
        if (bitmap != null && !bitmap.isRecycled) {
            bitmap.recycle()
        }
    }

    /** Locates the face, then returns every variant of it.  */
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Synchronized
    fun embedAllVariants(source: Bitmap?, includeMirror: Boolean): MutableList<FloatArray> {
        val face = findBestFace(source)
        if (face == null) {
            return mutableListOf()
        }
        try {
            return embedVariants(
                face.frame, face.box, includeMirror,
                face.leftEye, face.rightEye
            )
        } finally {
            face.release(source)
        }
    }

    /**
     * Warps the face into the 160x160 network input so that the eyes are level,
     * a fixed distance apart, and centred at a fixed point. One similarity
     * transform does rotation, scale and translation in a single draw, so there is
     * no extra resampling loss.
     */
    private fun alignFace(
        frame: Bitmap, leftEye: FloatArray, rightEye: FloatArray,
        targetEyeDistance: Float, mirrored: Boolean
    ): Bitmap? {
        val dx = rightEye[0] - leftEye[0]
        val dy = rightEye[1] - leftEye[1]
        val eyeDistance = sqrt((dx * dx + dy * dy).toDouble()).toFloat()
        if (eyeDistance < 4f) {
            return null
        }

        val angleDegrees = Math.toDegrees(atan2(dy.toDouble(), dx.toDouble())).toFloat()
        val scale = targetEyeDistance / eyeDistance
        val midX = (leftEye[0] + rightEye[0]) / 2f
        val midY = (leftEye[1] + rightEye[1]) / 2f

        val matrix = Matrix()
        matrix.postTranslate(-midX, -midY)
        matrix.postRotate(-angleDegrees)
        matrix.postScale(if (mirrored) -scale else scale, scale)
        matrix.postTranslate(EYE_MID_X, EYE_MID_Y)

        try {
            val aligned = Bitmap.createBitmap(NET_SIZE, NET_SIZE, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(aligned)
            canvas.drawColor(Color.BLACK)
            canvas.drawBitmap(frame, matrix, ALIGN_PAINT)
            if (debugCropName != null) {
                saveBitmap(aligned, debugCropName + ".png")
            }
            return aligned
        } catch (t: Throwable) {
            Log.w(TAG, "Alignment failed", t)
            return null
        }
    }

    /** Embeds an already aligned 160x160 bitmap.  */
    private fun embedAligned(aligned: Bitmap): FloatArray? {
        try {
            val buffer = faceNet.getEmbeddings(aligned, FULL_NET_RECT)
            if (buffer == null) {
                return null
            }
            buffer.clear()
            if (buffer.remaining() < FaceNet.EMBEDDING_SIZE) {
                return null
            }
            val embedding = FloatArray(FaceNet.EMBEDDING_SIZE)
            buffer.get(embedding)
            return normalize(embedding)
        } catch (e: Exception) {
            Log.e(TAG, "Aligned embedding failed", e)
            return null
        }
    }

    /** Convenience: locate and embed in one call.  */
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Synchronized
    fun embedBestFace(source: Bitmap?): FloatArray? {
        val face = findBestFace(source)
        if (face == null) {
            return null
        }
        try {
            return embed(face.frame, face.box)
        } finally {
            face.release(source)
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private fun largestFaceRect(frame: Bitmap): Rect? {
        lastLeftEye = null
        lastRightEye = null

        var small: Bitmap? = null
        var faces: MutableList<FaceBox>?
        try {
            small = Bitmap.createScaledBitmap(
                frame,
                BlazeFace.INPUT_SIZE_WIDTH, BlazeFace.INPUT_SIZE_HEIGHT, true
            )
            faces = blazeFace.detectWithLandmarks(small)
        } catch (e: Exception) {
            Log.e(TAG, "Detection failed", e)
            return null
        } finally {
            if (small != null && small != frame && !small.isRecycled()) {
                small.recycle()
            }
        }

        if (faces == null || faces.isEmpty()) {
            return null
        }

        var biggest: FaceBox? = null
        var biggestArea = 0f
        for (f in faces) {
            val a = f.location!!.width() * f.location.height()
            if (a > biggestArea) {
                biggestArea = a
                biggest = f
            }
        }
        if (biggest == null) {
            return null
        }

        val scaleX = frame.getWidth().toFloat() / BlazeFace.INPUT_SIZE_WIDTH
        val scaleY = frame.getHeight().toFloat() / BlazeFace.INPUT_SIZE_HEIGHT

        if (biggest.keypoints != null && biggest.keypoints.size >= 4) {
            val a = floatArrayOf(biggest.keypoints[0] * scaleX, biggest.keypoints[1] * scaleY)
            val b = floatArrayOf(biggest.keypoints[2] * scaleX, biggest.keypoints[3] * scaleY)
            // Order by x so left and right are consistent whatever the model returns.
            lastLeftEye = if (a[0] <= b[0]) a else b
            lastRightEye = if (a[0] <= b[0]) b else a
        }

        val scaled = RectF(
            biggest.location!!.left * scaleX,
            biggest.location.top * scaleY,
            biggest.location.right * scaleX,
            biggest.location.bottom * scaleY
        )

        scaled.inset(-scaled.width() * BOX_MARGIN, -scaled.height() * BOX_MARGIN)

        // Make the crop square around the same centre. FaceNet takes a 160x160
        // input, so a rectangular crop gets stretched, and how much it stretches
        // depends on the box shape. That alone makes a portrait photo and a
        // landscape capture of one face produce embeddings that do not match.
        val cx = scaled.centerX()
        val cy = scaled.centerY()
        var half = max(scaled.width(), scaled.height()) / 2f
        half = min(half, min(frame.getWidth(), frame.getHeight()) / 2f)
        scaled.set(cx - half, cy - half, cx + half, cy + half)

        // Slide the square back inside the frame instead of clipping it, which
        // would make it rectangular again.
        if (scaled.left < 0) {
            scaled.offset(-scaled.left, 0f)
        }
        if (scaled.top < 0) {
            scaled.offset(0f, -scaled.top)
        }
        if (scaled.right > frame.getWidth()) {
            scaled.offset(frame.getWidth() - scaled.right, 0f)
        }
        if (scaled.bottom > frame.getHeight()) {
            scaled.offset(0f, frame.getHeight() - scaled.bottom)
        }

        val box = Rect()
        scaled.round(box)
        box.left = max(0, box.left)
        box.top = max(0, box.top)
        box.right = min(frame.getWidth(), box.right)
        box.bottom = min(frame.getHeight(), box.bottom)

        if (box.width() < MIN_FACE_PX || box.height() < MIN_FACE_PX) {
            lastProblem = ("face too small: " + box.width() + "x" + box.height()
                + " px, need " + MIN_FACE_PX)
            return null
        }
        return box
    }

    fun close() {
        blazeFace.close()
        faceNet.close()
    }

    companion object {
        const val TAG: String = "FaceEmbedder"

        /** Extra context around the BlazeFace box, as a fraction of the box size.  */
        private const val BOX_MARGIN = 0.20f

        /** Faces smaller than this in the source photo are too blurry to enrol or match.  */
        private const val MIN_FACE_PX = 56

        private val ROTATIONS = intArrayOf(0, 90, 180, 270)

        /**
         * How tightly the face is cropped, as a multiplier on the box size.
         * A camera may frame a face tighter or looser than a gallery photo did, and
         * FaceNet is sensitive to that. Embedding several tightnesses and keeping the
         * best match removes the difference.
         */
        private val CROP_SCALES = floatArrayOf(0.85f, 1.0f, 1.18f)

        // ---- Alignment template, in the 160x160 network input ----
        /** Where the midpoint between the eyes is placed.  */
        private const val EYE_MID_X = 80f
        private const val EYE_MID_Y = 72f

        /** Distance between the eyes. The three values act as crop tightness.  */
        private val EYE_DISTANCES = floatArrayOf(46f, 53f, 61f)
        private const val NET_SIZE = 160

        /**
         * When set, the exact crop handed to FaceNet is written to the face data folder.
         * Set it to "crop_train" before enrolling and "crop_detect" before recognising,
         * then compare the two PNGs. If they are not both a centred face, the bug is in
         * the crop, not in the matching.
         */
        var debugCropName: String? = null

        private val FULL_NET_RECT = Rect(0, 0, NET_SIZE, NET_SIZE)
        private val ALIGN_PAINT =
            Paint(Paint.FILTER_BITMAP_FLAG or Paint.ANTI_ALIAS_FLAG or Paint.DITHER_FLAG)

        fun create(assetManager: AssetManager): FaceEmbedder {
            return FaceEmbedder(BlazeFace.create(assetManager), FaceNet.create(assetManager))
        }

        /** Grows or shrinks a square box around its centre, kept inside the frame.  */
        private fun scaleBox(box: Rect, scale: Float, frameWidth: Int, frameHeight: Int): Rect? {
            val cx = box.exactCenterX()
            val cy = box.exactCenterY()
            var half = max(box.width(), box.height()) * scale / 2f
            half = min(half, min(frameWidth, frameHeight) / 2f)

            val r = RectF(cx - half, cy - half, cx + half, cy + half)
            if (r.left < 0) {
                r.offset(-r.left, 0f)
            }
            if (r.top < 0) {
                r.offset(0f, -r.top)
            }
            if (r.right > frameWidth) {
                r.offset(frameWidth - r.right, 0f)
            }
            if (r.bottom > frameHeight) {
                r.offset(0f, frameHeight - r.bottom)
            }

            val out = Rect()
            r.round(out)
            out.left = max(0, out.left)
            out.top = max(0, out.top)
            out.right = min(frameWidth, out.right)
            out.bottom = min(frameHeight, out.bottom)
            return if (out.width() < MIN_FACE_PX || out.height() < MIN_FACE_PX) null else out
        }

        fun rotate(src: Bitmap, degrees: Int): Bitmap {
            if (degrees % 360 == 0) {
                return src
            }
            val m = Matrix()
            m.postRotate(degrees.toFloat())
            return Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), m, true)
        }

        fun mirror(src: Bitmap): Bitmap {
            val m = Matrix()
            m.preScale(-1f, 1f, src.getWidth() / 2f, src.getHeight() / 2f)
            return Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), m, true)
        }

        fun mirrorRect(box: Rect, frameWidth: Int): Rect {
            return Rect(frameWidth - box.right, box.top, frameWidth - box.left, box.bottom)
        }

        private fun area(r: Rect): Long {
            return r.width().toLong() * r.height().toLong()
        }

        /** Unit length, so a dot product between two embeddings is the cosine similarity.  */
        private fun normalize(v: FloatArray): FloatArray? {
            var sum = 0.0
            for (value in v) {
                sum += (value * value).toDouble()
            }
            val norm = sqrt(sum).toFloat()
            if (norm < 1e-10f) {
                return null
            }
            for (i in v.indices) {
                v[i] /= norm
            }
            return v
        }
    }
}
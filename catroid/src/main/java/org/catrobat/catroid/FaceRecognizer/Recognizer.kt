package org.catrobat.catroid.FaceRecognizer

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresApi
import org.catrobat.catroid.FaceRecognizer.env.FileUtils
import org.catrobat.catroid.FaceRecognizer.env.FileUtils.file
import org.catrobat.catroid.FaceRecognizer.env.FileUtils.init
import org.catrobat.catroid.FaceRecognizer.env.FileUtils.isReady
import java.util.Locale
import kotlin.collections.ArrayList
import kotlin.collections.MutableList
import kotlin.collections.indices
import kotlin.concurrent.Volatile
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt
class Recognizer private constructor() {
    class Result internal constructor(@JvmField val index: Int, @JvmField val name: String?, @JvmField val confidence: Float)

    private var embedder: FaceEmbedder? = null
    private val database = FaceDatabase()

    @get:Synchronized
    val minSimilarity: Float
        // ---------------- People ----------------
        get() = FaceDatabase.Companion.minSimilarity

    @get:Synchronized
    val minMargin: Float
        get() = FaceDatabase.Companion.minMargin

    @Synchronized
    fun setThresholds(similarity: Float, margin: Float) {
        database.setThresholds(similarity, margin)
    }

    @get:Synchronized
    val classNames: MutableList<String>
        get() {
            database.ensureFresh()
            return database.getNames()
        }

    @Synchronized
    fun getPhotoCount(index: Int): Int {
        return database.getEmbeddingCount(index)
    }

    @Synchronized
    fun addPerson(name: String): Int {
        val safeName = name.trim()
        require(safeName.isNotEmpty()) { "Person name must not be blank" }
        val existing = database.indexOf(safeName)
        if (existing >= 0) {
            return existing
        }
        val index = database.addPerson(safeName)
        database.save()
        return index
    }

    @Synchronized
    fun deletePerson(index: Int) {
        database.deletePerson(index)
        database.save()
    }

    // ---------------- Training ----------------
    /** Reports enrolment progress so the dialog can show a real progress bar.  */
    fun interface ProgressListener {
        fun onPhoto(done: Int, total: Int)
    }

    /** Result of reading a batch of photos, including why each one failed.  */
    class EnrolResult {
        val embeddings: MutableList<FloatArray> = ArrayList<FloatArray>()
        val report: MutableList<String> = ArrayList<String>()
    }

    /** Result callback for non-blocking gallery enrolment. Runs on the main thread.  */
    fun interface EnrolCallback {
        fun onFinished(result: EnrolResult?)
    }

    /** Handle retained by the Activity so onStop() can terminate gallery work.  */
    class EnrolTask {
        @Volatile
        var isCancelled: Boolean = false
            private set
        internal var worker: Thread? = null

        fun cancel() {
            this.isCancelled = true
            val t = worker
            if (t != null) {
                t.interrupt()
            }
        }
    }

    /**
     * Gallery training without blocking Android's UI thread. Progress and completion
     * are delivered on the main thread. Keep the returned task and cancel it from
     * Activity/Fragment onStop().
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    fun extractEmbeddingsAsync(
        resolver: ContentResolver?, uris: MutableList<Uri?>?,
        listener: ProgressListener?, callback: EnrolCallback?
    ): EnrolTask {
        val task = EnrolTask()
        val main = Handler(Looper.getMainLooper())
        val safeUris: MutableList<Uri> = uris?.filterNotNull()?.toMutableList()
            ?: mutableListOf()
        task.worker = Thread(Runnable {
            val safeProgress = Recognizer.ProgressListener { done: Int, total: Int ->
                if (listener != null && !task.isCancelled) {
                    main.post(Runnable {
                        if (!task.isCancelled) {
                            listener.onPhoto(done, total)
                        }
                    })
                }
            }
            var result: EnrolResult?
            try {
                result = extractEmbeddings(resolver, safeUris, safeProgress)
            } catch (t: Throwable) {
                Log.e(TAG, "Background enrolment failed", t)
                result = EnrolResult()
                result.report.add("Training failed: " + t.javaClass.getSimpleName())
            }
            val deliveredResult: EnrolResult? = result
            if (callback != null && !task.isCancelled) {
                main.post(Runnable {
                    if (!task.isCancelled) {
                        callback.onFinished(deliveredResult)
                    }
                })
            }
        }, "face_enrolment")
        task.worker!!.start()
        return task
    }

    /**
     * Reads the picked photos and returns one embedding per photo that contained a
     * usable face, plus a line per photo explaining what happened.
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Synchronized
    fun extractEmbeddings(resolver: ContentResolver?, uris: MutableList<Uri>?): EnrolResult {
        return extractEmbeddings(resolver, uris, null)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    @Synchronized
    fun extractEmbeddings(
        resolver: ContentResolver?,
        uris: List<Uri>?,
        listener: ProgressListener?
    ): EnrolResult {
        val result = EnrolResult()

        if (resolver == null || uris == null) {
            return result
        }

        val activeEmbedder = embedder

        if (activeEmbedder == null) {
            result.report.add(
                "Training failed: face embedder is not initialized"
            )

            return result
        }

        for ((index, uri) in uris.withIndex()) {
            if (Thread.currentThread().isInterrupted) {
                result.report.add("Training cancelled")
                break
            }

            val photoNumber = index + 1

            listener?.onPhoto(
                photoNumber,
                uris.size
            )

            val bitmap =
                decodeScaled(
                    resolver = resolver,
                    uri = uri,
                    maxSide = MAX_ENROL_SIDE
                )

            if (bitmap == null) {
                result.report.add(
                    "$photoNumber: $lastDecodeProblem"
                )

                Log.w(
                    TAG,
                    "Could not decode $uri"
                )

                continue
            }

            try {
                val face =
                    activeEmbedder.findBestFace(bitmap)

                if (face == null) {
                    result.report.add(
                        "$photoNumber: " +
                            activeEmbedder.lastProblem +
                            " (${bitmap.width}x${bitmap.height})"
                    )

                    continue
                }

                val faceFrame = face.frame
                val faceBox = face.box

                /*
                 * Automatically converted Face class-এ frame ও box nullable থাকতে পারে।
                 * Mandatory face data না থাকলে safely reject করা হবে।
                 */
                if (
                    faceFrame == null ||
                    faceFrame.isRecycled ||
                    faceBox == null ||
                    faceBox.width() <= 0 ||
                    faceBox.height() <= 0
                ) {
                    result.report.add(
                        "$photoNumber: invalid detected face"
                    )

                    face.release(bitmap)
                    continue
                }

                val faceWidth = faceBox.width()
                val faceHeight = faceBox.height()

                val variants: List<FloatArray>

                try {
                    variants =
                        activeEmbedder.embedVariants(
                            frame = faceFrame,
                            box = faceBox,
                            includeMirror = true,
                            leftEye = face.leftEye,
                            rightEye = face.rightEye
                        )
                } finally {
                    face.release(bitmap)
                }

                if (variants.isEmpty()) {
                    result.report.add(
                        "$photoNumber: " +
                            activeEmbedder.lastProblem
                    )

                    continue
                }

                var keptCount = 0

                for (candidate in variants) {
                    if (
                        !tooSimilarToStored(
                            stored = result.embeddings,
                            candidate = candidate
                        )
                    ) {
                        result.embeddings.add(candidate)
                        keptCount++
                    }
                }

                result.report.add(
                    "$photoNumber: OK, face " +
                        "${faceWidth}x${faceHeight} px, " +
                        "$keptCount new views"
                )
            } catch (error: OutOfMemoryError) {
                Log.e(
                    TAG,
                    "Photo $photoNumber failed: insufficient memory",
                    error
                )

                result.report.add(
                    "$photoNumber: insufficient memory"
                )
            } catch (error: Exception) {
                Log.e(
                    TAG,
                    "Photo $photoNumber failed",
                    error
                )

                result.report.add(
                    "$photoNumber: " +
                        error.javaClass.simpleName
                )
            } finally {
                if (!bitmap.isRecycled) {
                    bitmap.recycle()
                }
            }
        }

        Log.i(
            TAG,
            "Enrolment report: ${result.report}"
        )

        return result
    }
    /**
     * Embeds the largest face in a live camera frame, for camera based enrolment.
     * Uses exactly the same crop and normalisation as detection, which is the whole
     * point: a photo enrolled this way and a photo detected later differ only by
     * the person, not by the camera or the pipeline.
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Synchronized
    fun embedFrame(frame: Bitmap?): MutableList<FloatArray> {
        if (frame == null || frame.isRecycled()) {
            return mutableListOf()
        }
        return embedder!!.embedAllVariants(frame, true)
    }

    @get:Synchronized
    val embedderProblem: String?
        get() = if (embedder == null) "" else embedder!!.lastProblem

    /** Stores the embeddings under an existing person and writes both files.  */
    @Synchronized
    fun addEmbeddings(index: Int, embeddings: List<FloatArray>): Int {
        database.addEmbeddings(index, embeddings)
        database.save()
        return database.getEmbeddingCount(index)
    }

    // ---------------- Detection ----------------
    /**
     * Recognises the largest face in the frame. Returns null when no face is found or
     * when no enrolled person is close enough.
     *
     * @param mirrorToo also try the mirrored face, needed for front camera frames
     */
    /**
     * Accumulates scores across several camera frames. One frame can be blurry,
     * badly exposed or caught mid blink. Averaging over a few frames removes most
     * of that noise and is the largest single accuracy gain available here.
     */
    class Session {
        internal var totals: FloatArray? = null
        internal var framesWithFace: Int = 0
        internal var framesTried: Int = 0
    }

    /** Plain text summary of the last finished session. Shown to the user.  */
    @get:Synchronized
    var lastSummary: String = ""
        private set

    @Synchronized
    fun newSession(): Session {
        database.ensureFresh()
        return Session()
    }

    /**
     * Scores one frame into the session. Returns true if a face was found and used.
     * The frame is not modified and is not recycled.
     */
    @RequiresApi(Build.VERSION_CODES.N)
    @Synchronized
    fun addFrame(
        session: Session?,
        frame: Bitmap?,
        mirrorToo: Boolean
    ): Boolean {
        if (
            session == null ||
            frame == null ||
            frame.isRecycled
        ) {
            return false
        }

        val activeEmbedder = embedder ?: run {
            Log.e(TAG, "Face embedder is not initialized")
            return false
        }

        session.framesTried++

        val face = activeEmbedder.findBestFace(frame)

        if (face == null) {
            Log.i(
                TAG,
                "Frame ${session.framesTried}: " +
                    activeEmbedder.lastProblem
            )

            return false
        }

        val faceFrame = face.frame

        if (
            faceFrame == null ||
            faceFrame.isRecycled
        ) {
            Log.i(
                TAG,
                "Frame ${session.framesTried} rejected: invalid face bitmap"
            )

            face.release(frame)
            return false
        }

        val faceBox = face.box

        if (
            faceBox == null ||
            faceBox.width() <= 0 ||
            faceBox.height() <= 0
        ) {
            Log.i(
                TAG,
                "Frame ${session.framesTried} rejected: invalid face rectangle"
            )

            face.release(frame)
            return false
        }

        val faceWidth = faceBox.width()
        val faceHeight = faceBox.height()

        val shortestSide = min(
            faceFrame.width,
            faceFrame.height
        )

        if (shortestSide <= 0) {
            face.release(frame)
            return false
        }

        val faceFraction =
            min(faceWidth, faceHeight).toFloat() /
                shortestSide.toFloat()

        if (faceFraction < 0.12f) {
            Log.i(
                TAG,
                "Frame ${session.framesTried} rejected: " +
                    "detected face is too small, " +
                    "fraction=$faceFraction"
            )

            face.release(frame)
            return false
        }

        val qualityProblem =
            faceQualityProblem(faceFrame, faceBox)

        if (qualityProblem != null) {
            Log.i(
                TAG,
                "Frame ${session.framesTried} rejected: " +
                    qualityProblem
            )

            face.release(frame)
            return false
        }

        val bestScores: FloatArray?
        val variantCount: Int

        try {
            val variants = activeEmbedder.embedVariants(
                frame = faceFrame,
                box = faceBox,
                includeMirror = mirrorToo,
                leftEye = face.leftEye,
                rightEye = face.rightEye
            )

            variantCount = variants.size

            bestScores =
                database.scoreAllVariants(variants)
        } finally {
            face.release(frame)
        }

        if (bestScores == null) {
            return false
        }

        Log.i(
            TAG,
            "Frame ${session.framesTried} face " +
                "${faceWidth}x${faceHeight} px " +
                "($variantCount views): " +
                database.describeScoreArray(bestScores)
        )

        var totals = session.totals

        if (
            totals == null ||
            totals.size != bestScores.size
        ) {
            totals = FloatArray(bestScores.size)
            session.totals = totals
            session.framesWithFace = 0
        }

        for (index in bestScores.indices) {
            totals[index] += bestScores[index]
        }

        session.framesWithFace++

        return true
    }

    @Synchronized
    fun peekSession(session: Session?): Result? {
        if (session == null || session.framesWithFace == 2) {
            return null
        }
        return finishSession(session)
    }

    /** Averages the session and applies the thresholds once. Null means Unknown.  */
    /** True when the stored photos were made by a different embedding pipeline.  */
    @Synchronized
    fun needsRetraining(): Boolean {
        database.ensureFresh()
        return database.hasStaleEmbeddings()
    }

    @Synchronized
    fun finishSession(session: Session?): Result? {
        if (session == null || session.totals == null || session.framesWithFace == 0) {
            val tried = if (session == null) 0 else session.framesTried
            lastSummary = "No face found in " + tried + " frame(s)"
            Log.i(TAG, lastSummary)
            return null
        }

        val average = FloatArray(session.totals!!.size)
        for (i in average.indices) {
            average[i] = session.totals!![i] / session.framesWithFace
        }
        Log.i(
            TAG, ("Session average over " + session.framesWithFace + " frame(s): "
                + database.describeScoreArray(average))
        )

        if (session.framesWithFace == 1) {
            var bestSingle = -1f
            for (score in average) {
                bestSingle = max(bestSingle, score)
            }
            if (bestSingle < SINGLE_FRAME_MIN_SIMILARITY) {
                lastSummary = String.format(
                    Locale.US,
                    "REJECTED: only one usable lighting frame, best %.3f; need %.2f",
                    bestSingle, SINGLE_FRAME_MIN_SIMILARITY
                )
                Log.i(TAG, lastSummary)
                return null
            }
        }

        lastSummary = (database.describeScoreArray(average)
            + "\nfrom " + session.framesWithFace + " frame(s)"
            + "\nneed " + FaceDatabase.Companion.minSimilarity
            + ", gap " + FaceDatabase.Companion.minMargin)

        val match = database.decide(average)
        if (match == null) {
            lastSummary = "REJECTED\n" + lastSummary
            if (database.hasStaleEmbeddings()) {
                lastSummary = "PHOTOS ARE OUT OF DATE, RETRAIN\n" + lastSummary
                Log.e(
                    TAG, "Rejected, and the stored photos were made by an older "
                        + "pipeline. Delete each person and add their photos again."
                )
            }
            return null
        }
        lastSummary = match.name + "\n" + lastSummary
        Log.i(
            TAG, ("Match " + match.name + " similarity " + match.similarity
                + " margin " + match.margin)
        )
        return Result(match.index, match.name, match.similarity)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    @Synchronized
    fun recognize(
        frame: Bitmap?,
        mirrorToo: Boolean
    ): Result? {
        if (
            frame == null ||
            frame.isRecycled
        ) {
            return null
        }

        val activeEmbedder = embedder

        if (activeEmbedder == null) {
            Log.e(
                TAG,
                "Recognition failed: embedder is not initialized"
            )

            return null
        }

        database.ensureFresh()

        val face =
            activeEmbedder.findBestFace(frame)

        if (face == null) {
            Log.i(
                TAG,
                "No face in frame: ${activeEmbedder.lastProblem}"
            )

            return null
        }

        val faceFrame = face.frame
        val faceBox = face.box

        if (
            faceFrame == null ||
            faceFrame.isRecycled ||
            faceBox == null ||
            faceBox.width() <= 0 ||
            faceBox.height() <= 0
        ) {
            Log.i(
                TAG,
                "Recognition rejected: invalid detected face"
            )

            face.release(frame)
            return null
        }

        var bestMatch: FaceDatabase.Match? = null

        try {
            val embedding =
                activeEmbedder.embed(
                    frame = faceFrame,
                    box = faceBox
                )

            if (embedding != null) {
                Log.d(
                    TAG,
                    "Scores: ${database.describeScores(embedding)}"
                )

                bestMatch =
                    database.match(embedding)
            }

            if (mirrorToo) {
                var mirroredBitmap: Bitmap? = null

                try {
                    mirroredBitmap =
                        FaceEmbedder.mirror(faceFrame)

                    val mirroredBox =
                        FaceEmbedder.mirrorRect(
                            box = faceBox,
                            frameWidth = faceFrame.width
                        )

                    val mirroredEmbedding =
                        activeEmbedder.embed(
                            frame = mirroredBitmap,
                            box = mirroredBox
                        )

                    if (mirroredEmbedding != null) {
                        val mirroredMatch =
                            database.match(mirroredEmbedding)

                        if (
                            mirroredMatch != null &&
                            (
                                bestMatch == null ||
                                    mirroredMatch.similarity >
                                    bestMatch.similarity
                                )
                        ) {
                            bestMatch = mirroredMatch
                        }
                    }
                } catch (error: Exception) {
                    Log.w(
                        TAG,
                        "Mirrored recognition failed",
                        error
                    )
                } finally {
                    if (
                        mirroredBitmap != null &&
                        !mirroredBitmap.isRecycled
                    ) {
                        mirroredBitmap.recycle()
                    }
                }
            }
        } catch (error: Exception) {
            Log.e(
                TAG,
                "Face recognition failed",
                error
            )

            return null
        } finally {
            face.release(frame)
        }

        val match = bestMatch ?: return null
        val matchedName = match.name ?: return null

        Log.i(
            TAG,
            "Match $matchedName, " +
                "similarity ${match.similarity}, " +
                "margin ${match.margin}"
        )

        return Result(
            index = match.index,
            name = matchedName,
            confidence = match.similarity
        )
    }

    // ---------------- Helpers ----------------
    /** Why the last decode failed. Surfaced in the per photo report.  */
    private var lastDecodeProblem = ""

    private fun decodeScaled(resolver: ContentResolver, uri: Uri, maxSide: Int): Bitmap? {
        lastDecodeProblem = ""
        val bounds = BitmapFactory.Options()
        bounds.inJustDecodeBounds = true

        var decoded: Bitmap? = null
        try {
            resolver.openFileDescriptor(uri, "r").use { pfd ->
                if (pfd == null) {
                    lastDecodeProblem = "file descriptor was null"
                    return null
                }
                BitmapFactory.decodeFileDescriptor(pfd.getFileDescriptor(), null, bounds)
            }
        } catch (e: SecurityException) {
            // Happens when the activity that opened the picker was destroyed and the
            // read grant went with it.
            lastDecodeProblem = "no permission to read this photo (SecurityException)"
            Log.e(TAG, "Lost URI permission for " + uri, e)
            return null
        } catch (oom: OutOfMemoryError) {
            lastDecodeProblem = "not enough memory to inspect this photo"
            Log.e(TAG, "Out of memory reading image bounds " + uri, oom)
            return null
        } catch (e: Throwable) {
            lastDecodeProblem = "could not open: " + e.javaClass.getSimpleName()
            Log.e(TAG, "Could not read bounds of " + uri, e)
            return null
        }

        if (bounds.outWidth <= 0 || bounds.outHeight <= 0) {
            lastDecodeProblem = "unsupported, cloud-only, or damaged image"
            return null
        }
        val sourcePixels = bounds.outWidth.toLong() * bounds.outHeight
        if (sourcePixels > MAX_SOURCE_PIXELS) {
            lastDecodeProblem = ("image dimensions are too large: "
                + bounds.outWidth + "x" + bounds.outHeight)
            return null
        }

        var sample = 1
        val longest = max(bounds.outWidth, bounds.outHeight)
        /* Power-of-two sampling is supported consistently by older BitmapFactory. */
        while (longest.toLong() / sample > maxSide && sample <= 1024) {
            sample *= 2
        }

        val options = BitmapFactory.Options()
        options.inSampleSize = sample
        options.inPreferredConfig = Bitmap.Config.ARGB_8888
        options.inScaled = false
        options.inDither = false

        try {
            resolver.openFileDescriptor(uri, "r").use { pfd ->
                if (pfd == null) {
                    return null
                }
                decoded = BitmapFactory.decodeFileDescriptor(
                    pfd.getFileDescriptor(), null, options
                )
                if (decoded == null) {
                    lastDecodeProblem = "decoder returned no bitmap"
                    return null
                }
                /* Some vendor decoders ignore inSampleSize for uncommon formats. */
                val decodedLongest = max(decoded.getWidth(), decoded.getHeight())
                if (decodedLongest > maxSide * 2) {
                    val scale = maxSide.toFloat() / decodedLongest
                    val width = max(1, Math.round(decoded.getWidth() * scale))
                    val height = max(1, Math.round(decoded.getHeight() * scale))
                    val reduced = Bitmap.createScaledBitmap(decoded, width, height, true)
                    if (reduced != decoded) {
                        decoded.recycle()
                    }
                    decoded = reduced
                }
                return decoded
            }
        } catch (oom: OutOfMemoryError) {
            if (decoded != null && !decoded.isRecycled()) {
                decoded.recycle()
            }
            lastDecodeProblem = "image skipped: not enough memory"
            Log.e(TAG, "Out of memory decoding " + uri, oom)
            return null
        } catch (e: Throwable) {
            if (decoded != null && !decoded.isRecycled()) {
                decoded.recycle()
            }
            lastDecodeProblem = "could not decode: " + e.javaClass.getSimpleName()
            Log.e(TAG, "Could not decode " + uri, e)
            return null
        }
    }

    /**
     * Matches every enrolled person against their own first stored embedding.
     *
     * Each person must come back as themselves with a score near 1.0. This uses only
     * what is on disk, no camera and no photos, so it separates two very different
     * failures:
     * - a person scores near 1.0 -> storage and matching are fine, and any Unknown
     * from the camera is a threshold or image quality problem
     * - a person scores low, or matches the wrong name -> the embeddings written to
     * "data" are not the embeddings being read back, and the problem is storage
     */
    @Synchronized
    fun selfTest(): String {
        database.ensureFresh()
        val names = database.getNames()

        val sb = StringBuilder()
        if (names.isEmpty()) {
            return "Nothing trained yet."
        }

        sb.append("SAME PERSON\n")
        var worstNearest = 2f
        var measured = false
        for (i in names.indices) {
            val n = database.getEmbeddingCount(i)
            val avg = database.internalConsistency(i)
            val aff = database.photoAffinities(i)

            if (aff == null || aff.size < 2) {
                sb.append("  ").append(names.get(i))
                    .append(": needs 2+ photos (has ").append(n).append(")\n")
                continue
            }
            measured = true

            var strays = 0
            var nearestAvg = 0f
            for (a in aff) {
                nearestAvg += a
                if (a < 0.35f) {
                    strays++
                }
            }
            nearestAvg /= aff.size.toFloat()
            worstNearest = min(worstNearest, nearestAvg)

            sb.append(
                String.format(
                    Locale.US,
                    "  %s: %d photos\n    nearest match %.3f  (want 0.60+)\n"
                        + "    all pairs %.3f\n",
                    names.get(i), n, nearestAvg, avg
                )
            )
            if (strays > 0) {
                sb.append("    ").append(strays)
                    .append(" photo(s) match nothing. Tap Clean photos.\n")
            }
        }

        if (names.size > 1) {
            sb.append("\nDIFFERENT PEOPLE similarity\n")
            sb.append("(healthy is below 0.40)\n")
            for (i in names.indices) {
                for (j in i + 1..<names.size) {
                    sb.append(
                        String.format(
                            Locale.US, "  %s vs %s: %.3f\n",
                            names.get(i), names.get(j), database.crossSimilarity(i, j)
                        )
                    )
                }
            }
        }

        sb.append("\n")
        if (!measured) {
            sb.append("Add at least 2 photos per person, then run this again.")
        } else if (worstNearest < 0.35f) {
            sb.append(
                ("BROKEN. One person's own photos do not even resemble each "
                    + "other. The embeddings carry no identity information, so no "
                    + "threshold will help. The problem is in the face crop or the "
                    + "FaceNet input, not in the matching.")
            )
        } else if (worstNearest < 0.60f) {
            sb.append(
                ("WEAK. Matching now uses the single closest photo, so photos "
                    + "from different ages are fine. What is missing is a photo "
                    + "close to how the person looks right now, taken with this "
                    + "phone camera. Add 4 or 5 of those.")
            )
        } else {
            sb.append(
                ("GOOD. Stored embeddings are healthy. If the camera still "
                    + "says Unknown, the gap is between your saved photos and the "
                    + "live capture, not in the storage.")
            )
        }
        return sb.toString()
    }

    /** Human readable dump of what is actually on disk. Shown by the training dialog.  */
    /** Drops photos that resemble none of the person's other photos.  */
    @Synchronized
    fun cleanStrayPhotos(): String {
        database.ensureFresh()
        val names = database.getNames()
        val sb = StringBuilder()
        var total = 0
        for (i in names.indices) {
            val removed = database.removeStrayPhotos(i, 0.35f)
            total += removed
            sb.append("  ").append(names.get(i)).append(": removed ")
                .append(removed).append(", kept ")
                .append(database.getEmbeddingCount(i)).append("\n")
        }
        if (total == 0) {
            return "Nothing to clean. Every photo resembles at least one other."
        }
        return "Removed " + total + " photo(s) that matched nothing:\n\n" + sb
    }

    @get:Synchronized
    val status: String
        get() {
            val sb = StringBuilder()
            sb.append("Folder:\n").append(
                if (isReady)
                    file(FileUtils.LABEL_FILE)
                        .getParent()
                else
                    "NOT READY"
            ).append("\n\n")

            if (isReady) {
                sb.append("label : ").append(
                    file(FileUtils.LABEL_FILE)
                        .length()
                ).append(" bytes\n")
                sb.append("data  : ").append(
                    file(FileUtils.DATA_FILE)
                        .length()
                ).append(" bytes\n")
                sb.append("model : ").append(
                    file(FileUtils.MODEL_FILE)
                        .length()
                ).append(" bytes\n\n")
            }

            if (database.hasStaleEmbeddings()) {
                sb.append("WARNING: stored photos were made by an older face pipeline.\n")
                    .append("They cannot match a new capture. Retrain everyone.\n\n")
            }

            val names = database.getNames()
            sb.append("People: ").append(names.size).append("\n")
            for (i in names.indices) {
                sb.append("  ").append(i).append(". ").append(names.get(i))
                    .append("  ").append(database.getEmbeddingCount(i)).append(" photos\n")
            }
            if (names.isEmpty()) {
                sb.append("  (none)\n")
            }
            return sb.toString()
        }

    companion object {
        const val TAG: String = "Recognizer"

        /** Photos are downscaled to this longest side before detection, to bound memory.  */ /* FaceNet finally consumes only 160x160. 960 keeps ample face detail while
       cutting gallery bitmap/rotation memory by about 44% versus 1280. */
        private const val MAX_ENROL_SIDE = 960

        /** Reject pathological panoramas/broken metadata before allocating a bitmap.  */
        private const val MAX_SOURCE_PIXELS = 120000000L

        /** Below this many photos, recognition is unreliable.  */
        const val RECOMMENDED_PHOTOS: Int = 5

        /** A lone usable bracket frame must be exceptionally clear to identify anyone.  */
        private const val SINGLE_FRAME_MIN_SIMILARITY = 0.75f

        private var instance: Recognizer? = null

        /** Prevent two Select presses from leaving concurrent dataset jobs alive.  */
        @Volatile
        private var activeEnrolmentTask: EnrolTask? = null

        @JvmStatic
        @Synchronized
        @Throws(Exception::class)
        fun getInstance(context: Context): Recognizer {
            if (instance == null) {
                init(context)
                val r = Recognizer()
                r.embedder = FaceEmbedder.Companion.create(context.getAssets())
                r.database.load()
                instance = r
            }
            return instance!!
        }

        /**
         * Cold-start-safe enrolment. Unlike calling getInstance() from an Activity
         * first, this loads BlazeFace, FaceNet and the database on the worker thread.
         * Use this method for the first and all later gallery training operations.
         */
        @RequiresApi(api = Build.VERSION_CODES.N)
        fun startEnrolmentAsync(
            context: Context?,
            resolver: ContentResolver?, uris: MutableList<Uri?>?,
            listener: ProgressListener?, callback: EnrolCallback?
        ): EnrolTask {
            val task = EnrolTask()
            val previous: EnrolTask? = activeEnrolmentTask
            if (previous != null) {
                previous.cancel()
            }
            activeEnrolmentTask = task
            val main = Handler(Looper.getMainLooper())
            val appContext = requireNotNull(context) { "Context is null" }.applicationContext
            val safeUris: MutableList<Uri> = uris?.filterNotNull()?.toMutableList()
                ?: mutableListOf()
            task.worker = Thread(Runnable {
                var result: EnrolResult?
                try {
                    val recognizer: Recognizer = getInstance(appContext)
                    val safeProgress = Recognizer.ProgressListener { done: Int, total: Int ->
                        if (listener != null && !task.isCancelled) {
                            main.post(Runnable {
                                if (!task.isCancelled) {
                                    listener.onPhoto(done, total)
                                }
                            })
                        }
                    }
                    result = recognizer.extractEmbeddings(resolver, safeUris, safeProgress)
                } catch (t: Throwable) {
                    Log.e(TAG, "Cold-start background enrolment failed", t)
                    result = EnrolResult()
                    result.report.add("Training failed: " + t.javaClass.getSimpleName())
                }
                val delivered: EnrolResult? = result
                if (activeEnrolmentTask == task) {
                    activeEnrolmentTask = null
                }
                if (callback != null && !task.isCancelled) {
                    main.post(Runnable {
                        if (!task.isCancelled) {
                            callback.onFinished(delivered)
                        }
                    })
                }
            }, "face_enrolment_cold_start")
            task.worker!!.start()
            return task
        }

        private const val DUPLICATE_THRESHOLD = 0.995f

        private fun tooSimilarToStored(
            stored: MutableList<FloatArray>,
            candidate: FloatArray
        ): Boolean {
            for (existing in stored) {
                var dot = 0f
                var i = 0
                while (i < existing.size && i < candidate.size) {
                    dot += existing[i] * candidate[i]
                    i++
                }
                if (dot > DUPLICATE_THRESHOLD) {
                    return true
                }
            }
            return false
        }

        /** Rejects silhouette/clipped crops before they can collapse to a false identity.  */
        private fun faceQualityProblem(bitmap: Bitmap?, box: Rect?): String? {
            if (bitmap == null || box == null || box.width() <= 0 || box.height() <= 0) {
                return "invalid face crop"
            }
            val step = max(1, min(box.width(), box.height()) / 80)
            var sum = 0L
            var sumSquares = 0L
            var dark = 0
            var bright = 0
            var count = 0
            var y = box.top
            while (y < box.bottom) {
                var x = box.left
                while (x < box.right) {
                    val p = bitmap.getPixel(x, y)
                    val r = (p shr 16) and 0xff
                    val g = (p shr 8) and 0xff
                    val b = p and 0xff
                    val luma = (77 * r + 150 * g + 29 * b) shr 8
                    sum += luma.toLong()
                    sumSquares += luma.toLong() * luma
                    if (luma < 20) dark++
                    if (luma > 245) bright++
                    count++
                    x += step
                }
                y += step
            }
            if (count == 0) return "empty face crop"
            val mean = sum.toFloat() / count
            val variance = max(0f, sumSquares.toFloat() / count - mean * mean)
            val deviation = sqrt(variance.toDouble()).toFloat()
            val darkRatio = dark.toFloat() / count
            val brightRatio = bright.toFloat() / count
            if (mean < 30f || darkRatio > 0.65f) {
                return String.format(
                    Locale.US,
                    "face is a dark silhouette (mean %.1f, dark %.0f%%)",
                    mean, darkRatio * 100f
                )
            }
            if (mean > 230f || brightRatio > 0.60f) {
                return String.format(
                    Locale.US,
                    "face highlights are clipped (mean %.1f, bright %.0f%%)",
                    mean, brightRatio * 100f
                )
            }
            if (deviation < 18f) {
                return String.format(
                    Locale.US,
                    "face has too little visible detail (contrast %.1f)", deviation
                )
            }
            return null
        }

        private fun higherOf(a: FloatArray?, b: FloatArray?): FloatArray? {
            if (a == null) {
                return b
            }
            if (b == null || b.size != a.size) {
                return a
            }
            for (i in a.indices) {
                if (b[i] > a[i]) {
                    a[i] = b[i]
                }
            }
            return a
        }

        @JvmStatic
        @Synchronized
        fun release() {
            if (instance != null && instance!!.embedder != null) {
                instance!!.embedder!!.close()
            }
            instance = null
        }
    }
}
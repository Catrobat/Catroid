package org.catrobat.catroid.FaceRecognizer

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.annotation.VisibleForTesting
import org.catrobat.catroid.FaceRecognizer.env.FileUtils.init
import java.io.IOException
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

    @Volatile
    private var embedder: FaceEmbedder? = null
    private val database = FaceDatabase()

    /**
     * Guards the face models (TFLite interpreters, not thread safe). The database
     * and the names are guarded by this object. Model work never takes this
     * object's lock, so the training dialogs on the main thread never wait for a
     * frame or a training batch. Lock order: this object, then [modelLock].
     */
    private val modelLock = Any()

    @get:Synchronized
    val minSimilarity: Float
        // ---------------- People ----------------
        get() = FaceDatabase.Companion.minSimilarity

    @get:Synchronized
    val minMargin: Float
        get() = FaceDatabase.Companion.minMargin

    @Synchronized
    @Throws(IOException::class)
    fun setThresholds(similarity: Float, margin: Float) {
        if (!database.setThresholds(similarity, margin)) {
            throw IOException("The face thresholds could not be saved")
        }
    }

    @get:Synchronized
    val classNames: List<String>
        get() {
            database.ensureFresh()
            return database.getNames()
        }

    @Synchronized
    fun getPhotoCount(index: Int): Int {
        return database.getEmbeddingCount(index)
    }

    /**
     * Adds a person, or returns the index the name already has.
     * Throws IOException when the face files could not be saved; nothing changes then.
     */
    @Synchronized
    @Throws(IOException::class)
    fun addPerson(name: String): Int {
        val safeName = name.trim()
        require(safeName.isNotEmpty()) { "Person name must not be blank" }
        require(!hasLineBreak(safeName)) { "Person name must not contain a line break" }
        val existing = database.indexOf(safeName)
        if (existing >= 0) {
            return existing
        }
        val index = database.addPerson(safeName)
        saveOrThrow()
        return index
    }

    @Synchronized
    @Throws(IOException::class)
    fun deletePerson(index: Int) {
        database.deletePerson(index)
        saveOrThrow()
    }

    /**
     * Deletes the person with this name. False if nobody has it any more, for
     * example after another training menu deleted it: a row index from an older
     * list would point at someone else by then.
     */
    @Synchronized
    @Throws(IOException::class)
    fun deletePerson(name: String): Boolean {
        database.ensureFresh()
        val index = database.indexOf(name)
        if (index < 0) {
            return false
        }
        deletePerson(index)
        return true
    }

    /** A failed save has already put the database back as it is on disk. */
    private fun saveOrThrow() {
        if (!database.save()) {
            throw IOException("The face files could not be saved")
        }
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

    /**
     * Reads the picked photos and returns one embedding per photo that contained a
     * usable face, plus a line per photo explaining what happened.
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    fun extractEmbeddings(resolver: ContentResolver?, uris: List<Uri>?): EnrolResult {
        return extractEmbeddings(resolver, uris, listener = null)
    }
    @RequiresApi(Build.VERSION_CODES.N)
    fun extractEmbeddings(
        resolver: ContentResolver?,
        uris: List<Uri>?,
        listener: ProgressListener?
    ): EnrolResult = synchronized(modelLock) { extractEmbeddingsWithModels(resolver, uris, listener) }

    /** Runs under [modelLock]; touches only the models, not the database. */
    @RequiresApi(Build.VERSION_CODES.N)
    private fun extractEmbeddingsWithModels(
        resolver: ContentResolver?,
        uris: List<Uri>?,
        listener: ProgressListener?
    ): EnrolResult {
        val result = EnrolResult()

        if (resolver == null || uris == null) {
            return result
        }

        val activeEmbedder = embedder
            ?: return result.withReport(
                "Training failed: face embedder is not initialized"
            )

        for ((index, uri) in uris.withIndex()) {
            if (isTrainingCancelled(result)) {
                break
            }

            processEnrolmentPhoto(
                resolver = resolver,
                uri = uri,
                photoNumber = index + 1,
                totalPhotos = uris.size,
                activeEmbedder = activeEmbedder,
                listener = listener,
                result = result
            )
        }

        Log.i(
            TAG,
            "Enrolment report: ${result.report}"
        )

        return result
    }

    private fun EnrolResult.withReport(
        message: String
    ): EnrolResult {
        report.add(message)
        return this
    }

    private fun isTrainingCancelled(
        result: EnrolResult
    ): Boolean {
        if (!Thread.currentThread().isInterrupted) {
            return false
        }

        result.report.add("Training cancelled")
        return true
    }
    private fun processEnrolmentPhoto(
        resolver: ContentResolver,
        uri: Uri,
        photoNumber: Int,
        totalPhotos: Int,
        activeEmbedder: FaceEmbedder,
        listener: ProgressListener?,
        result: EnrolResult
    ) {
        listener?.onPhoto(
            photoNumber,
            totalPhotos
        )

        val bitmap = decodeEnrolmentBitmap(
            resolver = resolver,
            uri = uri,
            photoNumber = photoNumber,
            result = result
        ) ?: return

        try {
            processDecodedBitmap(
                bitmap = bitmap,
                photoNumber = photoNumber,
                activeEmbedder = activeEmbedder,
                result = result
            )
        } catch (error: OutOfMemoryError) {
            reportOutOfMemory(
                photoNumber = photoNumber,
                error = error,
                result = result
            )
        } catch (error: Exception) {
            reportPhotoError(
                photoNumber = photoNumber,
                error = error,
                result = result
            )
        } finally {
            recycleSafely(bitmap)
        }
    }

    private fun decodeEnrolmentBitmap(
        resolver: ContentResolver,
        uri: Uri,
        photoNumber: Int,
        result: EnrolResult
    ): Bitmap? {
        val bitmap = decodeScaled(
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
        }

        return bitmap
    }

    private fun processDecodedBitmap(
        bitmap: Bitmap,
        photoNumber: Int,
        activeEmbedder: FaceEmbedder,
        result: EnrolResult
    ) {
        val face = activeEmbedder.findBestFace(bitmap)

        if (face == null) {
            result.report.add(
                "$photoNumber: ${activeEmbedder.lastProblem} " +
                    "(${bitmap.width}x${bitmap.height})"
            )
            return
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
            result.report.add(
                "$photoNumber: invalid detected face"
            )

            face.release(bitmap)
            return
        }

        val faceWidth = faceBox.width()
        val faceHeight = faceBox.height()

        val variants: List<FloatArray> = try {
            activeEmbedder.embedVariants(
                frame = faceFrame,
                box = faceBox,
                includeMirror = true,
                leftEye = face.leftEye,
                rightEye = face.rightEye
            ).toList()
        } finally {
            face.release(bitmap)
        }

        if (variants.isEmpty()) {
            result.report.add(
                "$photoNumber: ${activeEmbedder.lastProblem}"
            )
            return
        }

        val keptCount = addUniqueEmbeddings(
            storedEmbeddings = result.embeddings,
            candidates = variants
        )

        result.report.add(
            "$photoNumber: OK, face " +
                "${faceWidth}x${faceHeight} px, " +
                "$keptCount new views"
        )
    }

    private fun addUniqueEmbeddings(
        storedEmbeddings: MutableList<FloatArray>,
        candidates: List<FloatArray>
    ): Int {
        var keptCount = 0

        for (candidate in candidates) {
            if (tooSimilarToStored(storedEmbeddings, candidate)) {
                continue
            }

            storedEmbeddings.add(candidate)
            keptCount++
        }

        return keptCount
    }

    private fun reportOutOfMemory(
        photoNumber: Int,
        error: OutOfMemoryError,
        result: EnrolResult
    ) {
        Log.e(
            TAG,
            "Photo $photoNumber failed: insufficient memory",
            error
        )

        result.report.add(
            "$photoNumber: insufficient memory"
        )
    }

    private fun reportPhotoError(
        photoNumber: Int,
        error: Exception,
        result: EnrolResult
    ) {
        Log.e(
            TAG,
            "Photo $photoNumber failed",
            error
        )

        result.report.add(
            "$photoNumber: ${error.javaClass.simpleName}"
        )
    }

    /**
     * Embeds the largest face in a live camera frame, for camera based enrolment.
     * Uses exactly the same crop and normalisation as detection, which is the whole
     * point: a photo enrolled this way and a photo detected later differ only by
     * the person, not by the camera or the pipeline.
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    fun embedFrame(frame: Bitmap?): MutableList<FloatArray> {
        if (frame == null || frame.isRecycled()) {
            return mutableListOf()
        }
        return synchronized(modelLock) { embedder?.embedAllVariants(frame, true) ?: mutableListOf() }
    }

    /**
     * Stores the photos under [name], adding the name if it is new, in one step:
     * a delete between adding the name and storing the photos would otherwise
     * move them to the next person. Returns that person's photo count; throws
     * IOException when the files could not be saved, and nothing changes then.
     */
    @Synchronized
    @Throws(IOException::class)
    fun addPhotos(name: String, embeddings: List<FloatArray>): Int =
        addEmbeddings(addPerson(name), embeddings)

    /** Stores the embeddings under an existing person and writes the files. Throws IOException if that failed. */
    @Synchronized
    @Throws(IOException::class)
    fun addEmbeddings(index: Int, embeddings: List<FloatArray>): Int {
        database.addEmbeddings(index, embeddings)
        saveOrThrow()
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
     * Scores the largest face in [frame] against every enrolled person: one
     * score per person, in [classNames] order. Null when the frame has no
     * usable face. The frame is not modified and is not recycled.
     */
    @RequiresApi(Build.VERSION_CODES.N)
    fun scoreFrame(frame: Bitmap?, mirrorToo: Boolean): FloatArray? =
        scoreFrameForNames(frame, mirrorToo)?.scores

    /** Scores of one frame, with the names they belong to, in the same order. */
    class ScoredFrame(val names: List<String>, val scores: FloatArray)

    /**
     * Like [scoreFrame], together with the names the scores were taken for: a
     * person may be added or deleted before the caller uses them.
     */
    @RequiresApi(Build.VERSION_CODES.N)
    fun scoreFrameForNames(frame: Bitmap?, mirrorToo: Boolean): ScoredFrame? {
        if (frame == null || frame.isRecycled) {
            return null
        }

        // The models run under their own lock, so the names and the database stay
        // available to the training dialogs while a frame is being embedded.
        val face = synchronized(modelLock) { embedLargestFace(frame, mirrorToo) } ?: return null

        synchronized(this) {
            database.ensureFresh()
            val bestScores = database.scoreAllVariants(face.variants) ?: return null
            Log.i(
                TAG,
                "Frame face " +
                    "${face.width}x${face.height} px " +
                    "(${face.variants.size} views): " +
                    database.describeScoreArray(bestScores)
            )
            return ScoredFrame(database.getNames(), bestScores)
        }
    }

    private class EmbeddedFace(val variants: List<FloatArray>, val width: Int, val height: Int)

    /** Finds the largest usable face in [frame] and embeds its views. Runs under [modelLock]. */
    @RequiresApi(Build.VERSION_CODES.N)
    private fun embedLargestFace(frame: Bitmap, mirrorToo: Boolean): EmbeddedFace? {
        val activeEmbedder = embedder ?: run {
            Log.e(TAG, "Face embedder is not initialized")
            return null
        }

        val face = activeEmbedder.findBestFace(frame)

        if (face == null) {
            Log.i(
                TAG,
                "No usable face: " +
                    activeEmbedder.lastProblem
            )

            return null
        }

        val faceFrame = face.frame

        if (
            faceFrame == null ||
            faceFrame.isRecycled
        ) {
            Log.i(
                TAG,
                "Frame rejected: invalid face bitmap"
            )

            face.release(frame)
            return null
        }

        val faceBox = face.box

        if (
            faceBox == null ||
            faceBox.width() <= 0 ||
            faceBox.height() <= 0
        ) {
            Log.i(
                TAG,
                "Frame rejected: invalid face rectangle"
            )

            face.release(frame)
            return null
        }

        val faceWidth = faceBox.width()
        val faceHeight = faceBox.height()

        val shortestSide = min(
            faceFrame.width,
            faceFrame.height
        )

        if (shortestSide <= 0) {
            face.release(frame)
            return null
        }

        val faceFraction =
            min(faceWidth, faceHeight).toFloat() /
                shortestSide.toFloat()

        if (faceFraction < 0.12f) {
            Log.i(
                TAG,
                "Frame rejected: " +
                    "detected face is too small, " +
                    "fraction=$faceFraction"
            )

            face.release(frame)
            return null
        }

        val qualityProblem =
            faceQualityProblem(faceFrame, faceBox)

        if (qualityProblem != null) {
            Log.i(
                TAG,
                "Frame rejected: " +
                    qualityProblem
            )

            face.release(frame)
            return null
        }

        try {
            val variants = activeEmbedder.embedVariants(
                frame = faceFrame,
                box = faceBox,
                includeMirror = mirrorToo,
                leftEye = face.leftEye,
                rightEye = face.rightEye
            ).toList()
            return EmbeddedFace(variants, faceWidth, faceHeight)
        } finally {
            face.release(frame)
        }
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

    /**
     * Recognises the largest face in [frame] with the sensor's views and
     * thresholds. The models run under [modelLock] only, the decision under
     * this object's lock, as in [scoreFrame].
     */
    @RequiresApi(Build.VERSION_CODES.N)
    fun recognize(
        frame: Bitmap?,
        mirrorToo: Boolean
    ): Result? {
        val scored = scoreFrameForNames(frame, mirrorToo) ?: return null
        synchronized(this) {
            if (scored.names != database.getNames()) {
                // Someone was added or deleted while the frame was scored.
                return null
            }
            return createRecognitionResult(database.decide(scored.scores))
        }
    }

    private fun createRecognitionResult(
        match: FaceDatabase.Match?
    ): Result? {
        match ?: return null

        val matchedName = match.name
            ?: return null

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

    private fun decodeScaled(
        resolver: ContentResolver,
        uri: Uri,
        maxSide: Int
    ): Bitmap? {
        lastDecodeProblem = ""

        val bounds = readImageBounds(
            resolver = resolver,
            uri = uri
        ) ?: return null

        if (!validateImageBounds(bounds)) {
            return null
        }

        val options = createDecodeOptions(
            bounds = bounds,
            maxSide = maxSide
        )

        return decodeSampledBitmap(
            resolver = resolver,
            uri = uri,
            options = options,
            maxSide = maxSide
        )
    }

    private fun readImageBounds(
        resolver: ContentResolver,
        uri: Uri
    ): BitmapFactory.Options? {
        val bounds = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }

        return try {
            resolver.openFileDescriptor(uri, "r").use { descriptor ->
                if (descriptor == null) {
                    lastDecodeProblem = "file descriptor was null"
                    return null
                }

                BitmapFactory.decodeFileDescriptor(
                    descriptor.fileDescriptor,
                    null,
                    bounds
                )
            }

            bounds
        } catch (error: SecurityException) {
            handleBoundsSecurityError(uri, error)
            null
        } catch (error: OutOfMemoryError) {
            handleBoundsMemoryError(uri, error)
            null
        } catch (error: Throwable) {
            handleBoundsReadError(uri, error)
            null
        }
    }

    private fun handleBoundsSecurityError(
        uri: Uri,
        error: SecurityException
    ) {
        /*
         * This can happen when the activity that opened the picker is
         * destroyed and its temporary URI permission is lost.
         */
        lastDecodeProblem =
            "no permission to read this photo (SecurityException)"

        Log.e(
            TAG,
            "Lost URI permission for $uri",
            error
        )
    }

    private fun handleBoundsMemoryError(
        uri: Uri,
        error: OutOfMemoryError
    ) {
        lastDecodeProblem =
            "not enough memory to inspect this photo"

        Log.e(
            TAG,
            "Out of memory reading image bounds $uri",
            error
        )
    }

    private fun handleBoundsReadError(
        uri: Uri,
        error: Throwable
    ) {
        lastDecodeProblem =
            "could not open: ${error.javaClass.simpleName}"

        Log.e(
            TAG,
            "Could not read bounds of $uri",
            error
        )
    }

    private fun validateImageBounds(
        bounds: BitmapFactory.Options
    ): Boolean {
        if (bounds.outWidth <= 0 || bounds.outHeight <= 0) {
            lastDecodeProblem =
                "unsupported, cloud-only, or damaged image"
            return false
        }

        val sourcePixels =
            bounds.outWidth.toLong() * bounds.outHeight.toLong()

        if (sourcePixels > MAX_SOURCE_PIXELS) {
            lastDecodeProblem =
                "image dimensions are too large: " +
                    "${bounds.outWidth}x${bounds.outHeight}"
            return false
        }

        return true
    }

    private fun createDecodeOptions(
        bounds: BitmapFactory.Options,
        maxSide: Int
    ): BitmapFactory.Options {
        return BitmapFactory.Options().apply {
            inSampleSize = calculateSampleSize(
                width = bounds.outWidth,
                height = bounds.outHeight,
                maxSide = maxSide
            )
            inPreferredConfig = Bitmap.Config.ARGB_8888
            inScaled = false
            inDither = false
        }
    }

    private fun calculateSampleSize(
        width: Int,
        height: Int,
        maxSide: Int
    ): Int {
        var sampleSize = 1
        val longestSide = max(width, height)

        /*
         * Power-of-two sampling works consistently with older
         * BitmapFactory implementations.
         */
        while (
            longestSide.toLong() / sampleSize > maxSide &&
            sampleSize <= 1024
        ) {
            sampleSize *= 2
        }

        return sampleSize
    }

    private fun decodeSampledBitmap(
        resolver: ContentResolver,
        uri: Uri,
        options: BitmapFactory.Options,
        maxSide: Int
    ): Bitmap? {
        var decoded: Bitmap? = null

        return try {
            resolver.openFileDescriptor(uri, "r").use { descriptor ->
                if (descriptor == null) {
                    lastDecodeProblem = "file descriptor was null"
                    return null
                }

                val bitmap = BitmapFactory.decodeFileDescriptor(
                    descriptor.fileDescriptor,
                    null,
                    options
                )

                if (bitmap == null) {
                    lastDecodeProblem = "decoder returned no bitmap"
                    return null
                }

                // Kept in decoded so the catch blocks can recycle it.
                decoded = bitmap
                decoded = reduceOversizedBitmap(
                    bitmap = bitmap,
                    maxSide = maxSide
                )

                decoded
            }
        } catch (error: OutOfMemoryError) {
            recycleSafely(decoded)
            handleDecodeMemoryError(uri, error)
            null
        } catch (error: Throwable) {
            recycleSafely(decoded)
            handleDecodeError(uri, error)
            null
        }
    }

    private fun reduceOversizedBitmap(
        bitmap: Bitmap,
        maxSide: Int
    ): Bitmap {
        val longestSide =
            max(bitmap.width, bitmap.height)

        /*
         * Some vendor decoders ignore inSampleSize for uncommon formats.
         */
        if (longestSide <= maxSide * 2) {
            return bitmap
        }

        val scale =
            maxSide.toFloat() / longestSide.toFloat()

        val targetWidth = max(
            1,
            Math.round(bitmap.width * scale)
        )

        val targetHeight = max(
            1,
            Math.round(bitmap.height * scale)
        )

        val reduced = Bitmap.createScaledBitmap(
            bitmap,
            targetWidth,
            targetHeight,
            true
        )

        if (reduced !== bitmap) {
            bitmap.recycle()
        }

        return reduced
    }

    private fun recycleSafely(
        bitmap: Bitmap?
    ) {
        if (bitmap != null && !bitmap.isRecycled) {
            bitmap.recycle()
        }
    }

    private fun handleDecodeMemoryError(
        uri: Uri,
        error: OutOfMemoryError
    ) {
        lastDecodeProblem =
            "image skipped: not enough memory"

        Log.e(
            TAG,
            "Out of memory decoding $uri",
            error
        )
    }

    private fun handleDecodeError(
        uri: Uri,
        error: Throwable
    ) {
        lastDecodeProblem =
            "could not decode: ${error.javaClass.simpleName}"

        Log.e(
            TAG,
            "Could not decode $uri",
            error
        )
    }

    companion object {
        const val TAG: String = "Recognizer"

        /**
         * The face database stores one name per line, so a name with a line
         * break would come back as two names and shift every later person.
         */
        private val LINE_BREAKS = setOf('\n', '\r', '\u000B', '\u000C', '\u0085', '\u2028', '\u2029')

        @JvmStatic
        fun hasLineBreak(name: String): Boolean = name.any { it in LINE_BREAKS }

        /** Photos are downscaled to this longest side before detection, to bound memory.  */ /* MobileFaceNet finally consumes only 112x112. 960 keeps ample face detail while
       cutting gallery bitmap/rotation memory by about 44% versus 1280. */
        private const val MAX_ENROL_SIDE = 960

        /** Reject pathological panoramas/broken metadata before allocating a bitmap.  */
        private const val MAX_SOURCE_PIXELS = 120000000L

        /**
         * A lone usable bracket frame must be exceptionally clear to identify anyone.
         * 0.75 with FaceNet; see FaceDatabase for how it was carried over.
         */
        private const val SINGLE_FRAME_MIN_SIMILARITY = 0.65f

        private var instance: Recognizer? = null

        /**
         * A recogniser with its database but without the face models, for local
         * tests of the session logic. It cannot detect or embed faces.
         */
        @VisibleForTesting
        internal fun withoutModelsForTest(context: Context): Recognizer {
            init(context)
            val r = Recognizer()
            r.database.load()
            return r
        }

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

        private const val DUPLICATE_THRESHOLD = 0.995f

        private fun tooSimilarToStored(
            stored: List<FloatArray>,
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
        private data class FaceQualityStats(
            val count: Int,
            val sum: Long,
            val sumSquares: Long,
            val darkCount: Int,
            val brightCount: Int
        )

        private fun faceQualityProblem(
            bitmap: Bitmap?,
            box: Rect?
        ): String? {
            if (!isValidFaceCrop(bitmap, box)) {
                return "invalid face crop"
            }

            val validBitmap = requireNotNull(bitmap)
            val validBox = requireNotNull(box)
            val stats = collectFaceQualityStats(validBitmap, validBox)

            if (stats.count == 0) {
                return "empty face crop"
            }

            return evaluateFaceQuality(stats)
        }

        private fun isValidFaceCrop(
            bitmap: Bitmap?,
            box: Rect?
        ): Boolean {
            return bitmap != null &&
                !bitmap.isRecycled &&
                box != null &&
                box.width() > 0 &&
                box.height() > 0 &&
                box.left >= 0 &&
                box.top >= 0 &&
                box.right <= bitmap.width &&
                box.bottom <= bitmap.height
        }

        private fun collectFaceQualityStats(
            bitmap: Bitmap,
            box: Rect
        ): FaceQualityStats {
            val step = max(
                1,
                min(box.width(), box.height()) / 80
            )

            var sum = 0L
            var sumSquares = 0L
            var darkCount = 0
            var brightCount = 0
            var count = 0
            var y = box.top

            while (y < box.bottom) {
                var x = box.left

                while (x < box.right) {
                    val luma = calculateLuma(
                        bitmap.getPixel(x, y)
                    )

                    sum += luma
                    sumSquares += luma * luma
                    darkCount += countIf(luma < 20)
                    brightCount += countIf(luma > 245)
                    count++

                    x += step
                }

                y += step
            }

            return FaceQualityStats(
                count = count,
                sum = sum,
                sumSquares = sumSquares,
                darkCount = darkCount,
                brightCount = brightCount
            )
        }

        private fun calculateLuma(
            pixel: Int
        ): Long {
            val red = (pixel shr 16) and 0xff
            val green = (pixel shr 8) and 0xff
            val blue = pixel and 0xff

            return ((77 * red + 150 * green + 29 * blue) shr 8)
                .toLong()
        }

        private fun countIf(
            condition: Boolean
        ): Int {
            return if (condition) 1 else 0
        }

        private fun evaluateFaceQuality(
            stats: FaceQualityStats
        ): String? {
            val mean = stats.sum.toFloat() / stats.count

            val variance = max(
                0f,
                stats.sumSquares.toFloat() / stats.count - mean * mean
            )

            val deviation =
                sqrt(variance.toDouble()).toFloat()

            val darkRatio =
                stats.darkCount.toFloat() / stats.count

            val brightRatio =
                stats.brightCount.toFloat() / stats.count

            return when {
                mean < 30f || darkRatio > 0.65f ->
                    formatDarkFaceProblem(mean, darkRatio)

                mean > 230f || brightRatio > 0.60f ->
                    formatBrightFaceProblem(mean, brightRatio)

                deviation < 18f ->
                    formatLowContrastProblem(deviation)

                else -> null
            }
        }

        private fun formatDarkFaceProblem(
            mean: Float,
            darkRatio: Float
        ): String {
            return String.format(
                Locale.US,
                "face is a dark silhouette (mean %.1f, dark %.0f%%)",
                mean,
                darkRatio * 100f
            )
        }

        private fun formatBrightFaceProblem(
            mean: Float,
            brightRatio: Float
        ): String {
            return String.format(
                Locale.US,
                "face highlights are clipped (mean %.1f, bright %.0f%%)",
                mean,
                brightRatio * 100f
            )
        }

        private fun formatLowContrastProblem(
            deviation: Float
        ): String {
            return String.format(
                Locale.US,
                "face has too little visible detail (contrast %.1f)",
                deviation
            )
        }

        @JvmStatic
        @Synchronized
        fun release() {
            val released = instance ?: return
            // Not while a frame or a training batch is using the models.
            synchronized(released) {
                synchronized(released.modelLock) {
                    released.embedder?.close()
                    // Anyone still holding this recogniser finds no models, not closed ones.
                    released.embedder = null
                }
            }
            instance = null
        }
    }
}
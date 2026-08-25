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
    @RequiresApi(Build.VERSION_CODES.N)
    fun extractEmbeddingsAsync(
        resolver: ContentResolver?,
        uris: MutableList<Uri?>?,
        listener: ProgressListener?,
        callback: EnrolCallback?
    ): EnrolTask {
        val task = EnrolTask()
        val mainHandler = Handler(Looper.getMainLooper())
        val safeUris = uris.orEmpty().filterNotNull().toMutableList()

        val worker = Thread(
            {
                val progressListener = createProgressListener(
                    task,
                    mainHandler,
                    listener
                )

                val result = extractEmbeddingsSafely(
                    resolver,
                    safeUris,
                    progressListener
                )

                deliverResult(
                    task,
                    mainHandler,
                    callback,
                    result
                )
            },
            "face_enrolment"
        )

        task.worker = worker
        worker.start()

        return task
    }

    private fun createProgressListener(
        task: EnrolTask,
        mainHandler: Handler,
        listener: ProgressListener?
    ): Recognizer.ProgressListener {
        return Recognizer.ProgressListener { done, total ->
            postProgress(
                task,
                mainHandler,
                listener,
                done,
                total
            )
        }
    }

    private fun postProgress(
        task: EnrolTask,
        mainHandler: Handler,
        listener: ProgressListener?,
        done: Int,
        total: Int
    ) {
        if (listener == null || task.isCancelled) {
            return
        }

        mainHandler.post {
            if (!task.isCancelled) {
                listener.onPhoto(done, total)
            }
        }
    }

    private fun extractEmbeddingsSafely(
        resolver: ContentResolver?,
        uris: MutableList<Uri>,
        progressListener: Recognizer.ProgressListener
    ): EnrolResult? {
        return try {
            extractEmbeddings(
                resolver,
                uris,
                progressListener
            )
        } catch (error: Throwable) {
            createFailureResult(error)
        }
    }

    private fun createFailureResult(error: Throwable): EnrolResult {
        Log.e(TAG, "Background enrolment failed", error)

        return EnrolResult().apply {
            report.add(
                "Training failed: ${error.javaClass.simpleName}"
            )
        }
    }

    private fun deliverResult(
        task: EnrolTask,
        mainHandler: Handler,
        callback: EnrolCallback?,
        result: EnrolResult?
    ) {
        if (callback == null || task.isCancelled) {
            return
        }

        mainHandler.post {
            if (!task.isCancelled) {
                callback.onFinished(result)
            }
        }
    }

    /**
     * Reads the picked photos and returns one embedding per photo that contained a
     * usable face, plus a line per photo explaining what happened.
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Synchronized
    fun extractEmbeddings(resolver: ContentResolver?, uris: MutableList<Uri>?): EnrolResult {
        return extractEmbeddings(resolver, uris, listener = null)
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

    private fun recycleSafely(
        bitmap: Bitmap
    ) {
        if (!bitmap.isRecycled) {
            bitmap.recycle()
        }
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
            ).toList()

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
        if (!isUsableBitmap(frame)) {
            return null
        }

        val activeEmbedder = getActiveEmbedder()
            ?: return null

        database.ensureFresh()

        val face = activeEmbedder.findBestFace(frame)

        if (face == null) {
            Log.i(
                TAG,
                "No face in frame: ${activeEmbedder.lastProblem}"
            )
            return null
        }

        val faceFrame = face.frame
        val faceBox = face.box

        if (!isValidDetectedFace(faceFrame, faceBox)) {
            Log.i(
                TAG,
                "Recognition rejected: invalid detected face"
            )

            face.release(frame)
            return null
        }

        val match = try {
            findBestRecognitionMatch(
                activeEmbedder = activeEmbedder,
                faceFrame = requireNotNull(faceFrame),
                faceBox = requireNotNull(faceBox),
                mirrorToo = mirrorToo
            )
        } catch (error: Exception) {
            Log.e(
                TAG,
                "Face recognition failed",
                error
            )
            null
        } finally {
            face.release(frame)
        }

        return createRecognitionResult(match)
    }
    private fun isUsableBitmap(
        bitmap: Bitmap?
    ): Boolean {
        return bitmap != null && !bitmap.isRecycled
    }

    private fun getActiveEmbedder(): FaceEmbedder? {
        val activeEmbedder = embedder

        if (activeEmbedder == null) {
            Log.e(
                TAG,
                "Recognition failed: embedder is not initialized"
            )
        }

        return activeEmbedder
    }

    private fun isValidDetectedFace(
        faceFrame: Bitmap?,
        faceBox: Rect?
    ): Boolean {
        return faceFrame != null &&
            !faceFrame.isRecycled &&
            faceBox != null &&
            faceBox.width() > 0 &&
            faceBox.height() > 0
    }

    private fun findBestRecognitionMatch(
        activeEmbedder: FaceEmbedder,
        faceFrame: Bitmap,
        faceBox: Rect,
        mirrorToo: Boolean
    ): FaceDatabase.Match? {
        val normalMatch = findNormalMatch(
            activeEmbedder = activeEmbedder,
            faceFrame = faceFrame,
            faceBox = faceBox
        )

        if (!mirrorToo) {
            return normalMatch
        }

        val mirroredMatch = findMirroredMatch(
            activeEmbedder = activeEmbedder,
            faceFrame = faceFrame,
            faceBox = faceBox
        )

        return selectBetterMatch(
            first = normalMatch,
            second = mirroredMatch
        )
    }

    private fun findNormalMatch(
        activeEmbedder: FaceEmbedder,
        faceFrame: Bitmap,
        faceBox: Rect
    ): FaceDatabase.Match? {
        val embedding = activeEmbedder.embed(
            frame = faceFrame,
            box = faceBox
        ) ?: return null

        Log.d(
            TAG,
            "Scores: ${database.describeScores(embedding)}"
        )

        return database.match(embedding)
    }

    private fun findMirroredMatch(
        activeEmbedder: FaceEmbedder,
        faceFrame: Bitmap,
        faceBox: Rect
    ): FaceDatabase.Match? {
        var mirroredBitmap: Bitmap? = null

        return try {
            mirroredBitmap = FaceEmbedder.mirror(faceFrame)

            val mirroredBox = FaceEmbedder.mirrorRect(
                box = faceBox,
                frameWidth = faceFrame.width
            )

            val mirroredEmbedding = activeEmbedder.embed(
                frame = mirroredBitmap,
                box = mirroredBox
            ) ?: return null

            database.match(mirroredEmbedding)
        } catch (error: Exception) {
            Log.w(
                TAG,
                "Mirrored recognition failed",
                error
            )
            null
        } finally {
            recycleMirroredBitmap(
                mirroredBitmap = mirroredBitmap,
                originalBitmap = faceFrame
            )
        }
    }
    private fun recycleMirroredBitmap(
        mirroredBitmap: Bitmap?,
        originalBitmap: Bitmap
    ) {
        if (
            mirroredBitmap != null &&
            mirroredBitmap !== originalBitmap &&
            !mirroredBitmap.isRecycled
        ) {
            mirroredBitmap.recycle()
        }
    }

    private fun selectBetterMatch(
        first: FaceDatabase.Match?,
        second: FaceDatabase.Match?
    ): FaceDatabase.Match? {
        if (first == null) {
            return second
        }

        if (second == null) {
            return first
        }

        return if (second.similarity > first.similarity) {
            second
        } else {
            first
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

                decoded = BitmapFactory.decodeFileDescriptor(
                    descriptor.fileDescriptor,
                    null,
                    options
                )

                if (decoded == null) {
                    lastDecodeProblem = "decoder returned no bitmap"
                    return null
                }

                decoded = reduceOversizedBitmap(
                    bitmap = requireNotNull(decoded),
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
    private data class SelfTestSummary(
        val measured: Boolean,
        val worstNearest: Float
    )

    @Synchronized
    fun selfTest(): String {
        database.ensureFresh()

        val names: List<String> = database.getNames().toList()

        if (names.isEmpty()) {
            return "Nothing trained yet."
        }

        val report = StringBuilder("SAME PERSON\n")
        val summary = appendSamePersonResults(report, names)

        appendDifferentPeopleResults(report, names)

        report.append("\n")
        report.append(
            createSelfTestConclusion(
                measured = summary.measured,
                worstNearest = summary.worstNearest
            )
        )

        return report.toString()
    }

    private fun appendSamePersonResults(
        report: StringBuilder,
        names: List<String>
    ): SelfTestSummary {
        var measured = false
        var worstNearest = 2f

        for (index in names.indices) {
            val result = appendPersonResult(
                report = report,
                name = names[index],
                personIndex = index
            )

            if (result != null) {
                measured = true
                worstNearest = min(
                    worstNearest,
                    result
                )
            }
        }

        return SelfTestSummary(
            measured = measured,
            worstNearest = worstNearest
        )
    }

    private fun appendPersonResult(
        report: StringBuilder,
        name: String,
        personIndex: Int
    ): Float? {
        val embeddingCount =
            database.getEmbeddingCount(personIndex)

        val affinities =
            database.photoAffinities(personIndex)

        if (affinities == null || affinities.size < 2) {
            appendInsufficientPhotos(
                report = report,
                name = name,
                photoCount = embeddingCount
            )
            return null
        }

        val nearestAverage =
            affinities.average().toFloat()

        val strayCount =
            affinities.count { affinity ->
                affinity < 0.35f
            }

        appendPersonMeasurements(
            report = report,
            name = name,
            photoCount = embeddingCount,
            nearestAverage = nearestAverage,
            consistency = database.internalConsistency(personIndex)
        )

        appendStrayWarning(
            report = report,
            strayCount = strayCount
        )

        return nearestAverage
    }

    private fun appendInsufficientPhotos(
        report: StringBuilder,
        name: String,
        photoCount: Int
    ) {
        report.append("  ")
            .append(name)
            .append(": needs 2+ photos (has ")
            .append(photoCount)
            .append(")\n")
    }

    private fun appendPersonMeasurements(
        report: StringBuilder,
        name: String,
        photoCount: Int,
        nearestAverage: Float,
        consistency: Float
    ) {
        report.append(
            String.format(
                Locale.US,
                "  %s: %d photos\n" +
                    "    nearest match %.3f  (want 0.60+)\n" +
                    "    all pairs %.3f\n",
                name,
                photoCount,
                nearestAverage,
                consistency
            )
        )
    }

    private fun appendStrayWarning(
        report: StringBuilder,
        strayCount: Int
    ) {
        if (strayCount <= 0) {
            return
        }

        report.append("    ")
            .append(strayCount)
            .append(
                " photo(s) match nothing. " +
                    "Tap Clean photos.\n"
            )
    }

    private fun appendDifferentPeopleResults(
        report: StringBuilder,
        names: List<String>
    ) {
        if (names.size <= 1) {
            return
        }

        report.append("\nDIFFERENT PEOPLE similarity\n")
        report.append("(healthy is below 0.40)\n")

        for (firstIndex in names.indices) {
            appendPersonComparisons(
                report = report,
                names = names,
                firstIndex = firstIndex
            )
        }
    }

    private fun appendPersonComparisons(
        report: StringBuilder,
        names: List<String>,
        firstIndex: Int
    ) {
        for (secondIndex in firstIndex + 1 until names.size) {
            report.append(
                String.format(
                    Locale.US,
                    "  %s vs %s: %.3f\n",
                    names[firstIndex],
                    names[secondIndex],
                    database.crossSimilarity(
                        firstIndex,
                        secondIndex
                    )
                )
            )
        }
    }

    private fun createSelfTestConclusion(
        measured: Boolean,
        worstNearest: Float
    ): String {
        return when {
            !measured ->
                "Add at least 2 photos per person, then run this again."

            worstNearest < 0.35f ->
                "BROKEN. One person's own photos do not even resemble each " +
                    "other. The embeddings carry no identity information, so no " +
                    "threshold will help. The problem is in the face crop or the " +
                    "FaceNet input, not in the matching."

            worstNearest < 0.60f ->
                "WEAK. Matching now uses the single closest photo, so photos " +
                    "from different ages are fine. What is missing is a photo " +
                    "close to how the person looks right now, taken with this " +
                    "phone camera. Add 4 or 5 of those."

            else ->
                "GOOD. Stored embeddings are healthy. If the camera still " +
                    "says Unknown, the gap is between your saved photos and the " +
                    "live capture, not in the storage."
        }
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
            sb.append("  ").append(names[i]).append(": removed ")
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
                sb.append("  ").append(i).append(". ").append(names[i])
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
        @RequiresApi(Build.VERSION_CODES.N)
        fun startEnrolmentAsync(
            context: Context?,
            resolver: ContentResolver?,
            uris: MutableList<Uri?>?,
            listener: ProgressListener?,
            callback: EnrolCallback?
        ): EnrolTask {
            val task = EnrolTask()

            replaceActiveEnrolmentTask(task)

            val mainHandler = Handler(Looper.getMainLooper())
            val appContext = requireNotNull(context) {
                "Context is null"
            }.applicationContext

            val safeUris = uris
                ?.filterNotNull()
                ?.toMutableList()
                ?: mutableListOf()

            val worker = Thread(
                createEnrolmentWorker(
                    task = task,
                    context = appContext,
                    resolver = resolver,
                    uris = safeUris,
                    listener = listener,
                    callback = callback,
                    mainHandler = mainHandler
                ),
                "face_enrolment_cold_start"
            )

            task.worker = worker
            worker.start()

            return task
        }

        private fun replaceActiveEnrolmentTask(
            newTask: EnrolTask
        ) {
            activeEnrolmentTask?.cancel()
            activeEnrolmentTask = newTask
        }

        @RequiresApi(Build.VERSION_CODES.N)
        private fun createEnrolmentWorker(
            task: EnrolTask,
            context: Context,
            resolver: ContentResolver?,
            uris: MutableList<Uri>,
            listener: ProgressListener?,
            callback: EnrolCallback?,
            mainHandler: Handler
        ): Runnable {
            return Runnable {
                val result = performEnrolment(
                    context = context,
                    resolver = resolver,
                    uris = uris,
                    task = task,
                    listener = listener,
                    mainHandler = mainHandler
                )

                clearActiveTask(task)

                postEnrolmentResult(
                    task = task,
                    result = result,
                    callback = callback,
                    mainHandler = mainHandler
                )
            }
        }

        @RequiresApi(Build.VERSION_CODES.N)
        private fun performEnrolment(
            context: Context,
            resolver: ContentResolver?,
            uris: MutableList<Uri>,
            task: EnrolTask,
            listener: ProgressListener?,
            mainHandler: Handler
        ): EnrolResult {
            return try {
                val recognizer = getInstance(context)

                val safeProgress = createSafeProgressListener(
                    task = task,
                    listener = listener,
                    mainHandler = mainHandler
                )

                recognizer.extractEmbeddings(
                    resolver = resolver,
                    uris = uris,
                    listener = safeProgress
                )
            } catch (error: Throwable) {
                createFailedEnrolmentResult(error)
            }
        }

        private fun createSafeProgressListener(
            task: EnrolTask,
            listener: ProgressListener?,
            mainHandler: Handler
        ): Recognizer.ProgressListener {
            return Recognizer.ProgressListener { done, total ->
                if (listener != null && !task.isCancelled) {
                    mainHandler.post {
                        deliverProgress(
                            task = task,
                            listener = listener,
                            done = done,
                            total = total
                        )
                    }
                }
            }
        }

        private fun deliverProgress(
            task: EnrolTask,
            listener: ProgressListener,
            done: Int,
            total: Int
        ) {
            if (!task.isCancelled) {
                listener.onPhoto(done, total)
            }
        }

        private fun createFailedEnrolmentResult(
            error: Throwable
        ): EnrolResult {
            Log.e(
                TAG,
                "Cold-start background enrolment failed",
                error
            )

            return EnrolResult().apply {
                report.add(
                    "Training failed: ${error.javaClass.simpleName}"
                )
            }
        }

        private fun clearActiveTask(
            completedTask: EnrolTask
        ) {
            if (activeEnrolmentTask === completedTask) {
                activeEnrolmentTask = null
            }
        }

        private fun postEnrolmentResult(
            task: EnrolTask,
            result: EnrolResult,
            callback: EnrolCallback?,
            mainHandler: Handler
        ) {
            if (callback == null || task.isCancelled) {
                return
            }

            mainHandler.post {
                deliverEnrolmentResult(
                    task = task,
                    result = result,
                    callback = callback
                )
            }
        }

        private fun deliverEnrolmentResult(
            task: EnrolTask,
            result: EnrolResult,
            callback: EnrolCallback
        ) {
            if (!task.isCancelled) {
                callback.onFinished(result)
            }
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
            if (instance != null && instance!!.embedder != null) {
                instance!!.embedder!!.close()
            }
            instance = null
        }
    }
}
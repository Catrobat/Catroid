package org.catrobat.catroid.FaceRecognizer

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.test.platform.app.InstrumentationRegistry
import org.catrobat.catroid.FaceRecognizer.env.FileUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import java.io.File

/**
 * Drives the recogniser the way the product does, on the real face fixtures
 * (faces.zip, unpacked to assets/faces/).
 *
 *  - Training goes through [Recognizer.extractEmbeddings] with file URIs,
 *    followed by addPerson/addEmbeddings, as FaceNameTrainAction does after the
 *    photo picker returns.
 *  - [StagePath] recognises through the session API (newSession -> addFrame ->
 *    peekSession early exit -> finishSession) with the frame count and early
 *    exit rule of the capture loop in FaceDetector. That path has its own
 *    face-size and quality filters, frame averaging, decide() and the stricter
 *    single-frame threshold.
 *  - [RecognizePath] uses [Recognizer.recognize] (FaceDatabase.match()).
 *
 * Only Recognizer API that also exists at Catrobat/Catroid#5237 (5b82fd56) is
 * used, so the tests built on this harness can be copied unchanged into that
 * tree for a red run against the old recogniser.
 */
class FaceRecognitionHarness {

    val appContext: Context = InstrumentationRegistry.getInstrumentation().targetContext
    private val testContext: Context = InstrumentationRegistry.getInstrumentation().context

    lateinit var recognizer: Recognizer
        private set

    private var savedMinSimilarity = 0f
    private var savedMinMargin = 0f

    /** Empty database, fresh recogniser, explicit thresholds. */
    fun start() {
        savedMinSimilarity = FaceDatabase.minSimilarity
        savedMinMargin = FaceDatabase.minMargin

        FileUtils.init(appContext)
        FileUtils.deleteAll()
        Recognizer.release()
        recognizer = Recognizer.getInstance(appContext)

        // Pin the thresholds instead of relying on whatever the statics or a
        // previously saved model header happen to contain.
        recognizer.setThresholds(MIN_SIMILARITY, MIN_MARGIN)
        assertEquals(MIN_SIMILARITY, FaceDatabase.minSimilarity, 0.0001f)
        assertEquals(MIN_MARGIN, FaceDatabase.minMargin, 0.0001f)
    }

    fun stop() {
        FileUtils.deleteAll()
        Recognizer.release()
        FaceDatabase.minSimilarity = savedMinSimilarity
        FaceDatabase.minMargin = savedMinMargin
    }

    /** Simulates a new process: everything has to come back from the saved files. */
    fun restartRecognizer() {
        Recognizer.release()
        recognizer = Recognizer.getInstance(appContext)
    }

    fun trainPerson(name: String, assetPrefix: String) {
        val index = recognizer.addPerson(name)
        val uris = (1..TRAINING_PHOTOS).map { assetUri("${assetPrefix}_train$it.jpg") }

        val result = recognizer.extractEmbeddings(appContext.contentResolver, uris)

        assertFalse(
            "Training photos for $name produced no embeddings; report: ${result.report}",
            result.embeddings.isEmpty()
        )
        val stored = recognizer.addEmbeddings(recognizer.addPerson(name), result.embeddings)
        assertTrue("No embeddings were stored for $name", stored > 0)
        assertEquals(
            "addPerson must return the existing index for $name",
            index,
            recognizer.addPerson(name)
        )
    }

    abstract inner class RecognitionPath {
        abstract val label: String

        abstract fun recognise(bitmap: Bitmap): Recognizer.Result?

        open fun describe(): String = ""

        /** Recognises one fixture photo; null means Unknown or no face. */
        fun recogniseFile(fileName: String): Recognizer.Result? =
            withBitmap(fileName) { recognise(it) }

        fun assertRecognisedAs(fileName: String, expectedName: String) {
            val result = withBitmap(fileName) { recognise(it) }
            if (result == null) {
                fail("[$label] $fileName should be $expectedName but was Unknown. ${describe()}")
            }
            assertEquals(
                "[$label] $fileName was given the wrong name. ${describe()}",
                expectedName,
                result?.name
            )
        }

        fun assertUnknown(fileName: String) {
            val result = withBitmap(fileName) { recognise(it) }
            assertNull(
                "[$label] $fileName must be Unknown, but was ${result?.name} " +
                    "(${result?.confidence}). ${describe()}",
                result
            )
        }
    }

    /**
     * The capture loop from FaceDetector.Session, without the camera. The same
     * decoded still is fed as every frame of the burst; with identical frames the
     * session average equals the single-frame scores, so the thresholds are
     * applied as they would be to a steady face.
     */
    inner class StagePath : RecognitionPath() {
        override val label = "stage session path"

        override fun recognise(bitmap: Bitmap): Recognizer.Result? {
            if (recognizer.classNames.isEmpty()) {
                return null
            }
            val session = recognizer.newSession()
            for (shot in 1..BURST_FRAMES) {
                recognizer.addFrame(session, bitmap, FRONT_CAMERA_MIRROR)
                val early = recognizer.peekSession(session)
                if (early != null &&
                    early.confidence > FaceDatabase.minSimilarity + EARLY_EXIT_MARGIN
                ) {
                    return early
                }
            }
            return recognizer.finishSession(session)
        }

        override fun describe(): String = "Summary: ${recognizer.lastSummary}"
    }

    inner class RecognizePath : RecognitionPath() {
        override val label = "recognize() path"

        override fun recognise(bitmap: Bitmap): Recognizer.Result? =
            recognizer.recognize(bitmap, FRONT_CAMERA_MIRROR)
    }

    private fun <T> withBitmap(fileName: String, block: (Bitmap) -> T): T {
        val bitmap = requiredBitmap(fileName)
        try {
            return block(bitmap)
        } finally {
            bitmap.recycle()
        }
    }

    private fun requiredBitmap(fileName: String): Bitmap {
        val assetPath = "faces/$fileName"
        val bitmap = try {
            testContext.assets.open(assetPath).use { input ->
                BitmapFactory.decodeStream(input)
            }
        } catch (error: Exception) {
            throw AssertionError("Required test asset is missing: $assetPath", error)
        }

        return requireNotNull(bitmap) {
            "Required test asset could not be decoded: $assetPath"
        }
    }

    /** Copies an asset to a file so it can be handed over as a URI, like a picker result. */
    private fun assetUri(fileName: String): Uri {
        val assetPath = "faces/$fileName"
        val output = File(appContext.cacheDir, "face_fixture_$fileName")
        try {
            testContext.assets.open(assetPath).use { input ->
                output.outputStream().use { input.copyTo(it) }
            }
        } catch (error: Exception) {
            throw AssertionError("Required test asset is missing: $assetPath", error)
        }
        return Uri.fromFile(output)
    }

    companion object {
        const val PERSON_A = "Person A"
        const val PERSON_B = "Person B"
        const val PERSON_C = "Person C"

        const val TRAINING_PHOTOS = 4

        /** Explicit thresholds. 0.60 is also the recogniser's absolute floor. */
        const val MIN_SIMILARITY = 0.60f
        const val MIN_MARGIN = 0.05f

        /** Must match FaceDetector.BURST_FRAMES. */
        const val BURST_FRAMES = 3

        /** Must match FaceDetector.EARLY_EXIT_MARGIN. */
        const val EARLY_EXIT_MARGIN = 0.15f

        /** FaceDetector passes isFrontCamera; the brick uses the front camera. */
        const val FRONT_CAMERA_MIRROR = true
    }
}

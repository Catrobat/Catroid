package org.catrobat.catroid.facerecognition

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.test.platform.app.InstrumentationRegistry
import org.catrobat.catroid.facerecognition.env.FileUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import java.io.File

/**
 * Drives the recogniser the way the product does, on the real face fixtures
 * (faces.zip, unpacked to the generated androidTest assets under faces/).
 *
 *  - Training goes through [Recognizer.extractEmbeddings] with file URIs,
 *    followed by [Recognizer.addPhotos], as FaceNameTrainAction does after the
 *    photo picker returns.
 *  - [StagePath] recognises through [FaceNameWindow], the decision behind the
 *    "detected face name with threshold" sensor (clipped-frame check,
 *    scoreFrameForNames, and the average of the last three usable frames at
 *    the default cutoff). Only the camera is replaced: the fixture photo is
 *    offered as three consecutive camera frames.
 */
class FaceRecognitionHarness {

    val appContext: Context = InstrumentationRegistry.getInstrumentation().targetContext
    private val testContext: Context = InstrumentationRegistry.getInstrumentation().context

    lateinit var recognizer: Recognizer
        private set

    /** Empty database, fresh recogniser. */
    fun start() {
        FileUtils.init(appContext)
        FileUtils.deleteAll()
        Recognizer.release()
        recognizer = Recognizer.getInstance(appContext)
    }

    fun stop() {
        FileUtils.deleteAll()
        Recognizer.release()
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
        val stored = recognizer.addPhotos(name, result.embeddings)
        assertTrue("No embeddings were stored for $name", stored > 0)
        assertEquals(
            "addPerson must return the existing index for $name",
            index,
            recognizer.addPerson(name)
        )
    }

    abstract inner class RecognitionPath {
        abstract val label: String

        /** The name given to [bitmap]; null means Unknown or no face. */
        abstract fun recognise(bitmap: Bitmap): String?

        open fun describe(): String = ""

        /** Recognises one fixture photo; null means Unknown or no face. */
        fun recogniseFile(fileName: String): String? =
            withBitmap(fileName) { recognise(it) }

        fun assertRecognisedAs(fileName: String, expectedName: String) {
            val result = withBitmap(fileName) { recognise(it) }
            if (result == null) {
                fail("[$label] $fileName should be $expectedName but was Unknown. ${describe()}")
            }
            assertEquals(
                "[$label] $fileName was given the wrong name. ${describe()}",
                expectedName,
                result
            )
        }

        fun assertUnknown(fileName: String) {
            val result = withBitmap(fileName) { recognise(it) }
            if (result != null) {
                fail("[$label] $fileName must be Unknown, but was $result. ${describe()}")
            }
        }
    }

    /**
     * The sensor's decision without the camera: a fresh production
     * [FaceNameWindow], offered the same decoded still as three consecutive
     * analysed camera frames, as a steady face in front of the camera would be.
     */
    inner class StagePath : RecognitionPath() {
        override val label = "stage sensor path"

        override fun recognise(bitmap: Bitmap): String? {
            val window = FaceNameWindow()
            for (frame in 0 until FaceNameWindow.FRAMES) {
                val scored = FaceNameWindow.scoreOf(recognizer, bitmap, FRONT_CAMERA_MIRROR)
                window.addScores(recognizer, scored?.scores, frame * FaceNameWindow.FRAME_INTERVAL_MS, scored?.names)
            }
            val name = window.nameWithThreshold(recognizer, DEFAULT_THRESHOLD)
            return if (name == FaceNameWindow.UNKNOWN) null else name
        }

        override fun describe(): String = "Threshold: $DEFAULT_THRESHOLD"
    }

    private fun <T> withBitmap(fileName: String, block: (Bitmap) -> T): T {
        val bitmap = requiredBitmap(fileName)
        try {
            return block(bitmap)
        } finally {
            bitmap.recycle()
        }
    }

    /** A freshly decoded fixture photo; the caller owns and recycles it. */
    fun fixtureBitmap(fileName: String): Bitmap = requiredBitmap(fileName)

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

        /** The default parameter of "detected face name with threshold". */
        const val DEFAULT_THRESHOLD = 0.53f

        /** FaceNameDetector passes mirrorToo while the front camera, the default, is in use. */
        const val FRONT_CAMERA_MIRROR = true
    }
}

package org.catrobat.catroid.FaceRecognizer

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.catrobat.catroid.FaceRecognizer.env.FileUtils
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Regression test for the historical "only the last-trained face is remembered" bug.
 *
 * The order is deliberately interleaved:
 * train A, train B, recognise A, train C, recognise B.
 * This must not be rewritten as "train everybody, then test everybody", because the
 * interleaving is the behaviour under test.
 */
@RunWith(AndroidJUnit4::class)
class RecognizerTrainingOrderRegressionTest {

    private lateinit var appContext: Context
    private lateinit var testContext: Context
    private lateinit var recognizer: Recognizer

    @Before
    fun setUp() {
        appContext = InstrumentationRegistry.getInstrumentation().targetContext
        testContext = InstrumentationRegistry.getInstrumentation().context

        FileUtils.init(appContext)
        FileUtils.deleteAll()
        Recognizer.release()
        recognizer = Recognizer.getInstance(appContext)
    }

    @After
    fun tearDown() {
        FileUtils.deleteAll()
        Recognizer.release()
    }

    @Test
    fun earlierPeopleRemainRecognisableAfterLaterPeopleAreTrained() {
        trainPerson("Person A", "p01")
        trainPerson("Person B", "p02")

        assertRecognisedAs("p01_test.jpg", "Person A")

        trainPerson("Person C", "p03")

        assertRecognisedAs("p02_test.jpg", "Person B")
        assertEquals(listOf("Person A", "Person B", "Person C"), recognizer.classNames)
    }

    private fun trainPerson(name: String, assetPrefix: String) {
        val personIndex = recognizer.addPerson(name)
        var storedCount = 0

        for (photoNumber in 1..4) {
            val fileName = "${assetPrefix}_train$photoNumber.jpg"
            val bitmap = requiredBitmap(fileName)
            try {
                val embeddings = recognizer.embedFrame(bitmap)
                assertFalse(
                    "$fileName produced no face embedding; the fixture or pipeline is invalid",
                    embeddings.isEmpty()
                )
                storedCount = recognizer.addEmbeddings(personIndex, embeddings)
            } finally {
                bitmap.recycle()
            }
        }

        assertTrue("No embeddings were stored for $name", storedCount > 0)
    }

    private fun assertRecognisedAs(fileName: String, expectedName: String) {
        val bitmap = requiredBitmap(fileName)
        try {
            val result = recognizer.recognize(bitmap, true)
            assertNotNull("$fileName was returned as Unknown", result)
            assertEquals(
                "$fileName was confused after another person was trained",
                expectedName,
                result?.name
            )
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
}

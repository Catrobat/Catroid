package org.catrobat.catroid.FaceRecognizer

import android.content.Context
import android.net.Uri
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.catrobat.catroid.FaceRecognizer.env.FileUtils
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread

@RunWith(AndroidJUnit4::class)
class FaceRecognizerLifecycleTest {

    private lateinit var context: Context
    private lateinit var recognizer: Recognizer

    @Before
    fun setUp() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        FileUtils.init(context)
        FileUtils.deleteAll()
        Recognizer.release()
        recognizer = Recognizer.getInstance(context)
    }

    @After
    fun tearDown() {
        FileUtils.deleteAll()
        Recognizer.release()
    }

    @Test
    fun labelCanBeAddedAndIsVisibleImmediately() {
        val index = recognizer.addPerson("Person A")

        assertEquals(0, index)
        assertEquals(listOf("Person A"), recognizer.classNames)
    }

    @Test
    fun labelIsTrimmedAndDuplicateIsNotAdded() {
        assertEquals(0, recognizer.addPerson("  Person A  "))
        assertEquals(0, recognizer.addPerson("Person A"))
        assertEquals(listOf("Person A"), recognizer.classNames)
    }

    @Test
    fun blankLabelIsRejected() {
        try {
            recognizer.addPerson("   ")
            fail("A blank label must be rejected")
        } catch (_: IllegalArgumentException) {
            // Expected.
        }
        assertTrue(recognizer.classNames.isEmpty())
    }

    @Test
    fun labelsSurviveRecognizerRestart() {
        recognizer.addPerson("Person A")
        recognizer.addPerson("Person B")

        Recognizer.release()
        recognizer = Recognizer.getInstance(context)

        assertEquals(listOf("Person A", "Person B"), recognizer.classNames)
    }

    @Test
    fun deletingOneLabelKeepsTheRemainingLabelsInOrder() {
        recognizer.addPerson("Person A")
        recognizer.addPerson("Person B")
        recognizer.addPerson("Person C")

        recognizer.deletePerson(1)

        assertEquals(listOf("Person A", "Person C"), recognizer.classNames)
    }

    @Test
    fun deletedLabelStaysDeletedAfterRestart() {
        recognizer.addPerson("Person A")
        recognizer.addPerson("Person B")
        recognizer.deletePerson(0)

        Recognizer.release()
        recognizer = Recognizer.getInstance(context)

        assertEquals(listOf("Person B"), recognizer.classNames)
    }

    @Test
    fun deletingUnknownIndexDoesNotDamageDatabase() {
        recognizer.addPerson("Person A")

        recognizer.deletePerson(99)

        assertEquals(listOf("Person A"), recognizer.classNames)
    }

    /**
     * The training dialogs read the names and add or delete people on the main
     * thread. Training and frame recognition run the face models on their own
     * threads; the main thread must not wait for them, or the stage freezes for
     * as long as a whole training batch takes.
     */
    @Test
    fun namesCanBeReadAndChangedWhileTheModelsAreBusyTraining() {
        recognizer.addPerson("Person A")
        val insideTraining = CountDownLatch(1)
        val finishTraining = CountDownLatch(1)
        val training = thread(name = "training") {
            recognizer.extractEmbeddings(
                context.contentResolver,
                listOf(Uri.parse("content://org.catrobat.catroid.missing/photo.jpg")),
                Recognizer.ProgressListener { _, _ ->
                    insideTraining.countDown()
                    finishTraining.await(TIMEOUT_S, TimeUnit.SECONDS)
                }
            )
        }
        val mainThreadStandIn = Executors.newSingleThreadExecutor()
        try {
            assertTrue(insideTraining.await(TIMEOUT_S, TimeUnit.SECONDS))

            val names = mainThreadStandIn.submit<List<String>> { recognizer.classNames }
                .get(MAX_WAIT_MS, TimeUnit.MILLISECONDS)
            val added = mainThreadStandIn.submit<Int> { recognizer.addPerson("Person B") }
                .get(MAX_WAIT_MS, TimeUnit.MILLISECONDS)

            assertEquals(listOf("Person A"), names)
            assertNotNull(added)
            assertEquals(listOf("Person A", "Person B"), recognizer.classNames)
        } finally {
            finishTraining.countDown()
            training.join()
            mainThreadStandIn.shutdownNow()
        }
    }

    private companion object {
        const val TIMEOUT_S = 10L
        const val MAX_WAIT_MS = 2_000L
    }
}

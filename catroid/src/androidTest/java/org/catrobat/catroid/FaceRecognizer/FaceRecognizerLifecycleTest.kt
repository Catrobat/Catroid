package org.catrobat.catroid.FaceRecognizer

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.catrobat.catroid.FaceRecognizer.env.FileUtils
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

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
}

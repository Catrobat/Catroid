package org.catrobat.catroid.FaceRecognizer

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import androidx.test.core.app.ApplicationProvider
import org.catrobat.catroid.FaceRecognizer.env.FileUtils
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * Pins the session rules FaceDetector's capture loop relies on: when
 * [Recognizer.peekSession] may decide, and how [FrameBurst] counts frames.
 *
 * Sessions are filled with known per-person scores directly, so no face model
 * is needed. Scores are summed over frames, as Recognizer.addFrame does.
 */
@RunWith(RobolectricTestRunner::class)
class RecognizerSessionTest {

    private lateinit var recognizer: Recognizer

    private var savedMinSimilarity = 0f
    private var savedMinMargin = 0f

    @Before
    fun setUp() {
        savedMinSimilarity = FaceDatabase.minSimilarity
        savedMinMargin = FaceDatabase.minMargin

        val context = ApplicationProvider.getApplicationContext<Context>()
        FileUtils.init(context)
        FileUtils.deleteAll()
        recognizer = Recognizer.withoutModelsForTest(context)
        recognizer.setThresholds(MIN_SIMILARITY, MIN_MARGIN)
        recognizer.addPerson(PERSON_A)
        recognizer.addPerson(PERSON_B)
    }

    @After
    fun tearDown() {
        FileUtils.deleteAll()
        FaceDatabase.minSimilarity = savedMinSimilarity
        FaceDatabase.minMargin = savedMinMargin
    }

    @Test
    fun peekBeforeAnyUsableFrameDoesNotDecide() {
        val session = session(framesWithFace = 0, totals = null, framesTried = 2)
        val summaryBefore = recognizer.lastSummary

        assertNull(recognizer.peekSession(session))
        assertEquals(
            "A peek without a usable frame must not finish the session",
            summaryBefore, recognizer.lastSummary
        )
    }

    @Test
    fun peekAfterOneClearFrameDecides() {
        val result = recognizer.peekSession(session(framesWithFace = 1, totals = floatArrayOf(0.90f, 0.20f)))

        assertNotNull(result)
        assertEquals(PERSON_A, result?.name)
        assertEquals(0.90f, result!!.confidence, 0.0001f)
    }

    @Test
    fun peekAfterOneFrameAppliesTheStricterSingleFrameThreshold() {
        // 0.70 passes the normal threshold (0.60) but not the single-frame one (0.75).
        assertNull(recognizer.peekSession(session(framesWithFace = 1, totals = floatArrayOf(0.70f, 0.10f))))
    }

    @Test
    fun peekAfterTwoFramesDecidesOnTheirAverage() {
        // Frames scored A=0.95 and A=0.85: average 0.90. With the old
        // "framesWithFace == 2" guard this peek returned null.
        val result = recognizer.peekSession(session(framesWithFace = 2, totals = floatArrayOf(1.80f, 0.40f)))

        assertNotNull("A peek after two usable frames must decide", result)
        assertEquals(PERSON_A, result?.name)
        assertEquals(0.90f, result!!.confidence, 0.0001f)
    }

    @Test
    fun peekAfterTwoFramesUsesTheNormalThresholdNotTheSingleFrameOne() {
        // Average 0.70: accepted with two frames, rejected with one.
        val result = recognizer.peekSession(session(framesWithFace = 2, totals = floatArrayOf(1.40f, 0.20f)))

        assertNotNull(result)
        assertEquals(PERSON_A, result?.name)
        assertEquals(0.70f, result!!.confidence, 0.0001f)
    }

    @Test
    fun peekDoesNotEndOrChangeTheSession() {
        // Frame 1: A = 0.70, too weak for a single frame, so the peek is null.
        val session = session(framesWithFace = 1, totals = floatArrayOf(0.70f, 0.10f))
        assertNull(recognizer.peekSession(session))

        // The session keeps collecting: frame 2 adds A = 0.90, as addFrame would.
        session.totals!![0] += 0.90f
        session.totals!![1] += 0.10f
        session.framesWithFace++
        val result = recognizer.finishSession(session)

        assertNotNull("The peek must leave the session usable", result)
        assertEquals(PERSON_A, result?.name)
        assertEquals("Average over both frames", 0.80f, result!!.confidence, 0.0001f)
    }

    @Test
    fun ambiguousSessionIsUnknownOnPeek() {
        // Both people at 0.80: best passes the threshold, the gap does not.
        assertNull(recognizer.peekSession(session(framesWithFace = 2, totals = floatArrayOf(1.60f, 1.60f))))
    }




    @Test
    fun noBurstWhenNobodyIsTrained() {
        recognizer.deletePerson(1)
        recognizer.deletePerson(0)

        assertNull(recognizer.newBurst())
    }

    @Test
    fun burstOfUndecodableFramesEndsAfterThreeFramesAsUnknown() {
        val burst = requireNotNull(recognizer.newBurst())

        for (frame in 1 until FrameBurst.FRAMES) {
            assertFalse("frame $frame must ask for another frame", burst.addFrame(null, true))
        }
        assertTrue(burst.addFrame(null, true))
        assertTrue(burst.isFinished)
        assertEquals(FrameBurst.FRAMES, burst.framesSeen)
        assertNull(burst.result)

        // A late frame after the decision is ignored.
        assertTrue(burst.addFrame(null, true))
        assertEquals(FrameBurst.FRAMES, burst.framesSeen)
    }

    @Test
    fun finishingABurstEarlyGivesUnknownWithoutFrames() {
        val burst = requireNotNull(recognizer.newBurst())

        assertNull(burst.finish())
        assertTrue(burst.isFinished)
        assertEquals(0, burst.framesSeen)
    }

    @Test
    fun clippedFrameIsRejectedAndNormalFrameIsKept() {
        assertTrue(FrameBurst.isSeverelyOverexposed(filled(Color.WHITE)))
        assertFalse(FrameBurst.isSeverelyOverexposed(filled(Color.rgb(120, 100, 90))))
        assertTrue(FrameBurst.isSeverelyOverexposed(null))
    }

    private fun session(framesWithFace: Int, totals: FloatArray?, framesTried: Int = framesWithFace) =
        recognizer.newSession().apply {
            this.totals = totals
            this.framesWithFace = framesWithFace
            this.framesTried = framesTried
        }

    private fun filled(color: Int): Bitmap =
        Bitmap.createBitmap(64, 64, Bitmap.Config.ARGB_8888).apply { eraseColor(color) }

    private companion object {
        const val PERSON_A = "Person A"
        const val PERSON_B = "Person B"
        const val MIN_SIMILARITY = 0.60f
        const val MIN_MARGIN = 0.05f
    }
}

/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2026 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * An additional term exception under section 7 of the GNU Affero
 * General Public License, version 3, is available at
 * http://developer.catrobat.org/license_additional_term
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.FaceRecognizer

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import androidx.test.core.app.ApplicationProvider
import org.catrobat.catroid.FaceRecognizer.FaceNameWindow.Companion.FRAMES
import org.catrobat.catroid.FaceRecognizer.FaceNameWindow.Companion.FRAME_INTERVAL_MS
import org.catrobat.catroid.FaceRecognizer.FaceNameWindow.Companion.NO_FACE_RESET_MS
import org.catrobat.catroid.FaceRecognizer.FaceNameWindow.Companion.UNKNOWN
import org.catrobat.catroid.FaceRecognizer.env.FileUtils
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * The decision behind "detected face name", with known per-person scores for
 * each frame, so no face model is needed. Thresholds (MobileFaceNet scale):
 * 0.53 with a gap of 0.04, 0.65 while the window holds a single frame, 0.61
 * with one person enrolled. The FaceNet scale had 0.60, 0.05, 0.75 and 0.70;
 * the scores below moved with them, so each keeps its place between them.
 */
@RunWith(RobolectricTestRunner::class)
class FaceNameWindowTest {

    private lateinit var recognizer: Recognizer
    private lateinit var window: FaceNameWindow

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
        window = FaceNameWindow()
    }

    @After
    fun tearDown() {
        FileUtils.deleteAll()
        FaceDatabase.minSimilarity = savedMinSimilarity
        FaceDatabase.minMargin = savedMinMargin
    }

    // ---------------- One, two and three usable frames ----------------

    @Test
    fun theNameIsUnknownBeforeTheFirstFrame() {
        assertEquals(UNKNOWN, window.name)
    }

    @Test
    fun oneClearFrameDecides() {
        assertEquals(PERSON_A, add(0.90f, 0.20f))
    }

    @Test
    fun oneFrameNeedsTheStricterSingleFrameThreshold() {
        // 0.61 passes the normal threshold (0.53) but not the single-frame one (0.65).
        assertEquals(UNKNOWN, add(0.61f, 0.10f))
    }

    @Test
    fun twoFramesDecideOnTheirAverageWithTheNormalThreshold() {
        add(0.61f, 0.10f)
        // Average 0.61: accepted with two frames, rejected with one.
        assertEquals(PERSON_A, add(0.61f, 0.10f))
    }

    @Test
    fun threeFramesDecideOnTheirAverage() {
        add(0.45f, 0.10f)
        assertEquals(UNKNOWN, add(0.45f, 0.10f))
        // Average 0.60 over three frames; the first two alone average 0.45.
        assertEquals(PERSON_A, add(0.90f, 0.10f))
        assertEquals(FRAMES, window.framesInWindow)
    }

    @Test
    fun theNameIsStoredAfterEveryUsableFrame() {
        assertEquals(PERSON_A, add(0.90f, 0.20f))
        assertEquals(PERSON_A, window.name)
        // Average with the first frame: A 0.45, B 0.10, below the threshold.
        assertEquals(UNKNOWN, add(0.00f, 0.00f))
        assertEquals(UNKNOWN, window.name)
    }

    // ---------------- The sliding window ----------------

    @Test
    fun theOldestFrameDropsOutOfTheWindow() {
        repeat(FRAMES) { add(0.90f, 0.20f) }
        assertEquals(PERSON_A, window.name)

        // Window A, A, B: A 0.67, B 0.43.
        assertEquals(PERSON_A, add(0.20f, 0.90f))
        // Window A, B, B: A 0.43, B 0.67. The first A frame has left the window.
        assertEquals(PERSON_B, add(0.20f, 0.90f))
        assertEquals(PERSON_B, add(0.20f, 0.90f))
        assertEquals(FRAMES, window.framesInWindow)
    }

    // ---------------- Frames without a usable face ----------------

    @Test
    fun aFrameWithoutAUsableFaceKeepsTheLastDecision() {
        add(0.90f, 0.20f, at = 0)
        assertEquals(PERSON_A, window.addScores(recognizer, null, NO_FACE_RESET_MS - 1))
        assertEquals(1, window.framesInWindow)
    }

    @Test
    fun oneSecondWithoutAUsableFaceGivesUnknownAndClearsTheFrames() {
        add(0.90f, 0.20f, at = 0)
        add(0.90f, 0.20f, at = 300)
        window.addScores(recognizer, null, 600)

        assertEquals(UNKNOWN, window.addScores(recognizer, null, 300 + NO_FACE_RESET_MS))
        assertEquals(0, window.framesInWindow)

        // A new start: one frame again needs the single-frame threshold.
        assertEquals(UNKNOWN, add(0.61f, 0.10f, at = 1500))
    }

    @Test
    fun framesWithoutAUsableFaceFromTheStartStayUnknown() {
        assertEquals(UNKNOWN, window.addScores(recognizer, null, 0))
        assertEquals(UNKNOWN, window.addScores(recognizer, null, 2000))
    }

    // ---------------- Thresholds ----------------

    @Test
    fun ambiguousScoresAreUnknown() {
        // Both people at 0.80: the best passes the threshold, the gap does not.
        add(0.80f, 0.80f)
        assertEquals(UNKNOWN, add(0.80f, 0.80f))
    }

    @Test
    fun withOnePersonEnrolledAMatchJustAboveTheNormalThresholdIsUnknown() {
        recognizer.deletePerson(0)
        assertEquals(listOf(PERSON_B), recognizer.classNames)

        // The frames from the phone after deleting the other person, FaceNet
        // 0.609, 0.610, 0.608; on the MobileFaceNet scale 0.536, 0.537, 0.535.
        add(0.536f)
        add(0.537f)
        assertEquals(UNKNOWN, add(0.535f))
    }

    @Test
    fun withOnePersonEnrolledAClearMatchIsRecognised() {
        recognizer.deletePerson(0)

        // 0.698: the weakest genuine single-person match in the device tests.
        add(0.698f)
        assertEquals(PERSON_B, add(0.698f))
    }

    // ---------------- Frames scored for other people ----------------

    /**
     * Found in a review: a frame is scored against the names as they were, and
     * the window only compared how many there were. One name deleted and another
     * added while the frame was scored, and its scores were taken for the new
     * names. A frame scored for other names is left out.
     */
    @Test
    fun aFrameScoredForOtherNamesIsLeftOut() {
        assertEquals(UNKNOWN, window.addScores(recognizer, floatArrayOf(0.90f, 0.20f), 0, listOf(PERSON_A, PERSON_C)))

        assertEquals(0, window.framesInWindow)
        assertEquals(PERSON_A, window.addScores(recognizer, floatArrayOf(0.90f, 0.20f), 300, listOf(PERSON_A, PERSON_B)))
    }

    /**
     * Found in a review: a person without usable photos has no score in a frame.
     * Averaged with real scores later in the window, that turned into a low
     * score of a rival, so the one person who has photos was named with the
     * normal threshold instead of the stricter single-person one. A person
     * without a score in any frame of the window has none in the average.
     */
    @Test
    fun aPersonWithoutAScoreInOneFrameIsNoRival() {
        add(0.57f, NO_SCORE)
        add(0.57f, 0.10f)

        // A alone has 0.57: above the normal threshold, below the single-person one.
        assertEquals(UNKNOWN, add(0.57f, 0.10f))
    }

    // ---------------- People trained while the program runs ----------------

    @Test
    fun aPersonTrainedWhileRunningIsScoredFromTheNextFrame() {
        repeat(FRAMES) { add(0.20f, 0.20f) }
        assertEquals(UNKNOWN, window.name)

        recognizer.addPerson(PERSON_C)

        // The older frames had no score for Person C, so they leave the window.
        assertEquals(PERSON_C, add(0.10f, 0.10f, 0.90f))
        assertEquals(1, window.framesInWindow)
    }

    @Test
    fun aFrameScoredForOtherPeopleIsIgnored() {
        add(0.90f, 0.20f)
        // Two scores, but three people enrolled by now.
        recognizer.addPerson(PERSON_C)
        assertEquals(PERSON_A, window.addScores(recognizer, floatArrayOf(0.20f, 0.90f), 300))
        assertEquals(1, window.framesInWindow)
    }

    // ---------------- The 300 ms throttle ----------------

    @Test
    fun atMostOneFrameEvery300Ms() {
        assertTrue(window.isFrameDue(1000))
        assertFalse(window.isFrameDue(1000 + FRAME_INTERVAL_MS - 1))
        assertTrue(window.isFrameDue(1000 + FRAME_INTERVAL_MS))
        assertFalse(window.isFrameDue(1000 + FRAME_INTERVAL_MS + 100))
        assertTrue(window.isFrameDue(1000 + 3 * FRAME_INTERVAL_MS))
    }

    @Test
    fun clearStartsAgainFromUnknownWithTheNextFrameDue() {
        assertTrue(window.isFrameDue(1000))
        add(0.90f, 0.20f)

        window.clear()

        assertEquals(UNKNOWN, window.name)
        assertEquals(0, window.framesInWindow)
        assertTrue(window.isFrameDue(1001))
    }

    // ---------------- Clipped frames ----------------

    @Test
    fun clippedFrameIsRejectedAndNormalFrameIsKept() {
        assertTrue(FaceNameWindow.isSeverelyOverexposed(filled(Color.WHITE)))
        assertFalse(FaceNameWindow.isSeverelyOverexposed(filled(Color.rgb(120, 100, 90))))
        assertTrue(FaceNameWindow.isSeverelyOverexposed(null))
    }

    private var nextAt = 0L

    private fun add(vararg scores: Float, at: Long = nextAt): String {
        nextAt = at + FRAME_INTERVAL_MS
        return window.addScores(recognizer, scores, at)
    }

    private fun filled(color: Int): Bitmap =
        Bitmap.createBitmap(64, 64, Bitmap.Config.ARGB_8888).apply { eraseColor(color) }

    private companion object {
        const val PERSON_A = "Person A"
        const val PERSON_B = "Person B"
        const val PERSON_C = "Person C"
        const val MIN_SIMILARITY = 0.53f
        const val MIN_MARGIN = 0.04f
        const val NO_SCORE = FaceDatabase.NO_SCORE
    }
}

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
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.FaceRecognizer

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.catrobat.catroid.FaceRecognizer.FaceNameWindow.Companion.FRAME_INTERVAL_MS
import org.catrobat.catroid.FaceRecognizer.FaceNameWindow.Companion.FRAMES
import org.catrobat.catroid.FaceRecognizer.FaceNameWindow.Companion.NO_FACE_RESET_MS
import org.catrobat.catroid.FaceRecognizer.FaceNameWindow.Companion.UNKNOWN
import org.catrobat.catroid.FaceRecognizer.env.FileUtils
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/** Exercises the sensor's per-formula threshold against cached frame scores. */
@RunWith(RobolectricTestRunner::class)
class FaceNameThresholdTest {

    private lateinit var recognizer: Recognizer
    private lateinit var window: FaceNameWindow
    private var savedMinSimilarity = 0f
    private var savedMinMargin = 0f
    private var nextAt = 0L

    @Before
    fun setUp() {
        savedMinSimilarity = FaceDatabase.minSimilarity
        savedMinMargin = FaceDatabase.minMargin
        val context = ApplicationProvider.getApplicationContext<Context>()
        FileUtils.init(context)
        FileUtils.deleteAll()
        recognizer = Recognizer.withoutModelsForTest(context)
        recognizer.setThresholds(0.53f, 0.04f)
        recognizer.addPerson(PERSON_A)
        window = FaceNameWindow()
    }

    @After
    fun tearDown() {
        FileUtils.deleteAll()
        FaceDatabase.minSimilarity = savedMinSimilarity
        FaceDatabase.minMargin = savedMinMargin
    }

    @Test
    fun theVisibleDefaultAlsoAppliesToTheFirstFrameAndOnlyEnrolledPerson() {
        assertEquals(UNKNOWN, read(0f))
        assertEquals(PERSON_A, add(0.55f))
        assertEquals(PERSON_A, window.name)
        assertEquals(PERSON_A, read(0.53f))
        assertEquals(UNKNOWN, read(0.61f))
    }

    @Test
    fun thresholdsCanBeChangedInBothDirectionsWithoutWaitingForAnotherFrame() {
        repeat(FRAMES) { add(0.55f) }

        assertEquals(UNKNOWN, read(0.61f))
        assertEquals(PERSON_A, read(0.53f))
        assertEquals(UNKNOWN, read(0.80f))
        assertEquals(PERSON_A, read(0.50f))
        assertEquals(FRAMES, window.framesInWindow)
        assertEquals(PERSON_A, window.name)
    }

    @Test
    fun theArgumentCanChooseAThresholdBelowTheOldSafetyFloor() {
        add(0.40f)

        assertEquals(UNKNOWN, window.name)
        assertEquals(PERSON_A, read(0.30f))
        assertEquals(UNKNOWN, read(0.53f))
    }

    @Test
    fun changingAFormulaDoesNotChangeGlobalThresholdsOrOtherFormulaResults() {
        recognizer.addPerson(PERSON_B)
        FaceDatabase.minSimilarity = 0.88f
        FaceDatabase.minMargin = 0.08f
        add(0.55f, 0.54f)

        // Neither the saved similarity nor its margin is a hidden requirement.
        assertEquals(PERSON_A, window.name)
        assertEquals(PERSON_A, read(0.53f))
        assertEquals(UNKNOWN, read(0.61f))
        assertEquals(PERSON_A, read(0.53f))
        assertEquals(0.88f, FaceDatabase.minSimilarity, 0f)
        assertEquals(0.08f, FaceDatabase.minMargin, 0f)
    }

    @Test
    fun havingPreviouslyDisplayedANameDoesNotRelaxTheRequestedThreshold() {
        repeat(FRAMES) { add(0.80f) }
        repeat(FRAMES) { add(0.54f) }

        assertEquals(PERSON_A, window.name)
        assertEquals(UNKNOWN, read(0.61f))
        assertEquals(PERSON_A, read(0.53f))
        assertEquals(UNKNOWN, read(0.61f))
    }

    @Test
    fun everyThresholdUsesTheSameThreeFrameAverage() {
        add(0.90f)
        add(0.20f)
        add(0.55f)

        assertEquals(PERSON_A, read(0.53f))
        assertEquals(UNKNOWN, read(0.60f))
        assertEquals(FRAMES, window.framesInWindow)
    }

    @Test
    fun cachedNamesExpireAtOneSecondWithoutAUsableFaceForEveryThreshold() {
        add(0.55f, at = 0)
        window.addScores(recognizer, null, NO_FACE_RESET_MS - 1)

        assertEquals(PERSON_A, read(0.53f))
        assertEquals(UNKNOWN, read(0.61f))

        window.addScores(recognizer, null, NO_FACE_RESET_MS)

        assertEquals(0, window.framesInWindow)
        assertEquals(UNKNOWN, window.name)
        assertEquals(UNKNOWN, read(0f))
        assertEquals(UNKNOWN, read(0.53f))
    }

    @Test
    fun clearingTheWindowCannotReviveACachedNameAtALowerThreshold() {
        add(0.55f)
        window.clear()

        assertEquals(UNKNOWN, read(0f))
        assertEquals(UNKNOWN, window.name)
        assertEquals(0, window.framesInWindow)
    }

    @Test
    fun finiteThresholdsAreClampedToTheSimilarityRange() {
        add(-0.10f)
        assertEquals(UNKNOWN, read(-1f))

        window.clear()
        add(0f)
        assertEquals(PERSON_A, read(-1f))
        assertEquals(PERSON_A, read(0f))

        window.clear()
        add(1f)
        assertEquals(PERSON_A, read(2f))
        assertEquals(PERSON_A, read(1f))
    }

    @Test
    fun theExactCutoffPassesWithoutAnImplicitExtraRequirement() {
        add(0.55f)

        assertEquals(PERSON_A, read(0.55f))
        assertEquals(UNKNOWN, read(0.5501f))
    }

    @Test
    fun nonfiniteThresholdsCannotProduceAMatchOrPoisonTheCachedScores() {
        add(0.80f)

        for (threshold in listOf(Float.NaN, Float.POSITIVE_INFINITY, Float.NEGATIVE_INFINITY)) {
            assertEquals(UNKNOWN, read(threshold))
            assertEquals(PERSON_A, read(0.53f))
        }
    }

    @Test
    fun nonfiniteCandidateScoresAreIgnoredWhileValidCandidatesCanStillMatch() {
        recognizer.addPerson(PERSON_B)

        for (invalidScore in listOf(Float.NaN, Float.POSITIVE_INFINITY, Float.NEGATIVE_INFINITY)) {
            window.clear()
            add(invalidScore, 0.70f)
            assertEquals(PERSON_B, read(0.53f))
        }

        window.clear()
        add(Float.NaN, Float.POSITIVE_INFINITY)
        assertEquals(UNKNOWN, read(0f))
    }

    @Test
    fun changingTheNumberOfEnrolledPeopleInvalidatesCachedNameReads() {
        add(0.80f)
        recognizer.addPerson(PERSON_B)

        assertEquals(UNKNOWN, read(0f))
        assertEquals(PERSON_B, add(0.10f, 0.80f))
        assertEquals(1, window.framesInWindow)
    }

    @Test
    fun replacingANameWithTheSamePersonCountCannotReuseTheOldPersonsScores() {
        add(0.80f)
        recognizer.deletePerson(0)
        recognizer.addPerson(PERSON_B)

        assertEquals(listOf(PERSON_B), recognizer.classNames)
        assertEquals(UNKNOWN, read(0f))

        // An in-flight frame still scored for A must not be assigned to B.
        window.addScores(recognizer, floatArrayOf(0.90f), nextAt, listOf(PERSON_A))
        assertEquals(UNKNOWN, read(0f))

        assertEquals(PERSON_B, add(0.80f))
        assertEquals(1, window.framesInWindow)
    }

    private fun read(threshold: Float): String = window.nameWithThreshold(recognizer, threshold)

    private fun add(vararg scores: Float, at: Long = nextAt): String {
        nextAt = at + FRAME_INTERVAL_MS
        return window.addScores(recognizer, scores, at)
    }

    private companion object {
        const val PERSON_A = "Person A"
        const val PERSON_B = "Person B"
    }
}

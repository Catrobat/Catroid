package org.catrobat.catroid.FaceRecognizer

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.catrobat.catroid.FaceRecognizer.env.FileUtils
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * Pins how [Recognizer.finishSession] decides from averaged scores, the
 * decision FaceNameWindow uses for "detected face name": the single-person
 * rule, and the normal threshold while two people are enrolled.
 *
 * Sessions are filled with known per-person scores directly, so no face model
 * is needed. Scores are summed over frames, as FaceNameWindow does.
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

    /**
     * Found on the phone: two people trained, one deleted, then the deleted
     * person's face scored 0.609 against the one left over three frames and was
     * given that name. With nobody else to compare against, the margin rule says
     * nothing, so the single person needs SINGLE_PERSON_MIN_SIMILARITY.
     */
    @Test
    fun afterDeletingTheOtherPersonAFaceJustAboveTheNormalThresholdIsUnknown() {
        recognizer.deletePerson(0)
        assertEquals(listOf(PERSON_B), recognizer.classNames)

        // Frames 0.609, 0.610, 0.608 as logged on the phone.
        assertNull(recognizer.finishSession(session(framesWithFace = 3, totals = floatArrayOf(1.827f))))
    }

    @Test
    fun theOnlyEnrolledPersonIsStillRecognisedOnAClearMatch() {
        recognizer.deletePerson(0)

        // 0.787: the weakest genuine single-person match in the device tests.
        val result = recognizer.finishSession(session(framesWithFace = 2, totals = floatArrayOf(1.574f)))

        assertEquals(PERSON_B, result?.name)
        assertEquals(0.787f, result!!.confidence, 0.0001f)
    }

    @Test
    fun withTwoPeopleTheNormalThresholdStillApplies() {
        // 0.65 is below the single-person bar but has a clear margin over B.
        val result = recognizer.finishSession(session(framesWithFace = 2, totals = floatArrayOf(1.30f, 0.40f)))

        assertEquals(PERSON_A, result?.name)
    }

    private fun session(framesWithFace: Int, totals: FloatArray?, framesTried: Int = framesWithFace) =
        recognizer.newSession().apply {
            this.totals = totals
            this.framesWithFace = framesWithFace
            this.framesTried = framesTried
        }

    private companion object {
        const val PERSON_A = "Person A"
        const val PERSON_B = "Person B"
        const val MIN_SIMILARITY = 0.60f
        const val MIN_MARGIN = 0.05f
    }
}

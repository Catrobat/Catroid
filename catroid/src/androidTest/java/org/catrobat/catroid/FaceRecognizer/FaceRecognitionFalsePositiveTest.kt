package org.catrobat.catroid.FaceRecognizer

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.catrobat.catroid.FaceRecognizer.FaceRecognitionHarness.Companion.PERSON_A
import org.catrobat.catroid.FaceRecognizer.FaceRecognitionHarness.Companion.PERSON_B
import org.catrobat.catroid.FaceRecognizer.FaceRecognitionHarness.Companion.PERSON_C
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * False positives with real faces, through the stage's session path.
 *
 * Restores the stranger and impostor checks that existed in
 * Catrobat/Catroid#5237 (RecognizerInstrumentedTest.aDifferentPersonIsNotAccepted,
 * FaceRecognitionAccuracyTest.anUnenrolledPersonIsNotGivenSomebodyElsesName),
 * on the current fixtures (p01..p03).
 */
@RunWith(AndroidJUnit4::class)
class FaceRecognitionFalsePositiveTest {

    private val harness = FaceRecognitionHarness()

    @Before
    fun setUp() {
        harness.start()
    }

    @After
    fun tearDown() {
        harness.stop()
    }

    /**
     * Impostor with a single enrolled person. The margin check cannot help here
     * (there is no runner-up), so only the similarity threshold stands between a
     * stranger and the one enrolled name.
     */
    @Test
    fun onlyEnrolledPersonIsNotGivenToStrangers() {
        harness.trainPerson(PERSON_A, "p01")
        val stage = harness.StagePath()

        stage.assertRecognisedAs("p01_test.jpg", PERSON_A)
        stage.assertUnknown("p02_test.jpg")
        stage.assertUnknown("p03_test.jpg")
    }

    /** Leave-one-out: each person, when not enrolled, must come back as Unknown. */
    @Test
    fun personLeftOutOfTrainingIsUnknown() {
        assertLeftOutIsUnknown(
            enrolled = listOf(PERSON_B to "p02", PERSON_C to "p03"),
            strangerPhoto = "p01_test.jpg"
        )
        assertLeftOutIsUnknown(
            enrolled = listOf(PERSON_A to "p01", PERSON_C to "p03"),
            strangerPhoto = "p02_test.jpg"
        )
        assertLeftOutIsUnknown(
            enrolled = listOf(PERSON_A to "p01", PERSON_B to "p02"),
            strangerPhoto = "p03_test.jpg"
        )
    }

    private fun assertLeftOutIsUnknown(enrolled: List<Pair<String, String>>, strangerPhoto: String) {
        harness.stop()
        harness.start()
        enrolled.forEach { (name, prefix) -> harness.trainPerson(name, prefix) }

        harness.StagePath().assertUnknown(strangerPhoto)
    }
}

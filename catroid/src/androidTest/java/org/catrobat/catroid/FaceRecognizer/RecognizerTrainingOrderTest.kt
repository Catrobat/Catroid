package org.catrobat.catroid.FaceRecognizer

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.catrobat.catroid.FaceRecognizer.FaceRecognitionHarness.Companion.PERSON_A
import org.catrobat.catroid.FaceRecognizer.FaceRecognitionHarness.Companion.PERSON_B
import org.catrobat.catroid.FaceRecognizer.FaceRecognitionHarness.Companion.PERSON_C
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Checks that every trained person stays recognisable while further people are
 * trained, and that an untrained person is not given a trained name.
 *
 * The order is deliberately interleaved, and must not be rewritten as
 * "train everybody, then test everybody", because the interleaving is the
 * behaviour under test:
 *
 *   train A, train B
 *   recognise A        -> A        (A checked while only two people exist)
 *   recognise p03_test -> Unknown  (a real, untrained person is rejected)
 *   train C            (C is the person in p03_*)
 *   recognise B        -> B
 *   recognise C        -> C        (the last-trained person is recognised)
 *   recognise A        -> A        (the first-trained person is still recognised)
 *   recognise no_face  -> null
 *
 * The sequence runs once through FaceDetector's recognition loop ([FrameBurst],
 * the camera replaced by the fixture photo) and once through recognize(), so a
 * disagreement between the two paths is one red test rather than a silent pass.
 * See [FaceRecognitionHarness] for what each path covers.
 */
@RunWith(AndroidJUnit4::class)
class RecognizerTrainingOrderTest {

    private val harness = FaceRecognitionHarness()

    @Before
    fun setUp() {
        harness.start()
    }

    @After
    fun tearDown() {
        harness.stop()
    }

    @Test
    fun stagePathKeepsEveryPersonRecognisableWhilePeopleAreAdded() {
        runSequence(harness.StagePath())
    }

    @Test
    fun recognizePathFollowsTheSameSequence() {
        runSequence(harness.RecognizePath())
    }

    @Test
    fun stagePathStillRecognisesEveryoneAfterRecognizerRestart() {
        harness.trainPerson(PERSON_A, "p01")
        harness.trainPerson(PERSON_B, "p02")
        harness.trainPerson(PERSON_C, "p03")

        harness.restartRecognizer()

        val stage = harness.StagePath()
        assertEquals(listOf(PERSON_A, PERSON_B, PERSON_C), harness.recognizer.classNames)
        stage.assertRecognisedAs("p01_test.jpg", PERSON_A)
        stage.assertRecognisedAs("p02_test.jpg", PERSON_B)
        stage.assertRecognisedAs("p03_test.jpg", PERSON_C)
    }

    private fun runSequence(path: FaceRecognitionHarness.RecognitionPath) {
        harness.trainPerson(PERSON_A, "p01")
        harness.trainPerson(PERSON_B, "p02")

        path.assertRecognisedAs("p01_test.jpg", PERSON_A)
        path.assertUnknown("p03_test.jpg")

        harness.trainPerson(PERSON_C, "p03")
        assertEquals(listOf(PERSON_A, PERSON_B, PERSON_C), harness.recognizer.classNames)

        path.assertRecognisedAs("p02_test.jpg", PERSON_B)
        path.assertRecognisedAs("p03_test.jpg", PERSON_C)
        path.assertRecognisedAs("p01_test.jpg", PERSON_A)
        path.assertUnknown("no_face.jpeg")
    }
}

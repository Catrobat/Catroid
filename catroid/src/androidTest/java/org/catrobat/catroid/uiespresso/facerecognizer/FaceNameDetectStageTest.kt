package org.catrobat.catroid.uiespresso.facerecognizer

import android.Manifest
import android.content.Context
import android.hardware.camera2.CameraManager
import android.os.Handler
import android.os.HandlerThread
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import org.catrobat.catroid.FaceRecognizer.FaceDetector
import org.catrobat.catroid.FaceRecognizer.FaceRecognitionHarness
import org.catrobat.catroid.FaceRecognizer.FaceRecognitionHarness.Companion.PERSON_A
import org.catrobat.catroid.FaceRecognizer.FaceRecognitionHarness.Companion.PERSON_B
import org.catrobat.catroid.content.bricks.FaceNameDetect
import org.catrobat.catroid.content.bricks.SetVariableBrick
import org.catrobat.catroid.content.bricks.ShowTextColorSizeAlignmentBrick
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.formulaeditor.FormulaElement
import org.catrobat.catroid.formulaeditor.Sensors
import org.catrobat.catroid.formulaeditor.UserVariable
import org.catrobat.catroid.stage.StageActivity
import org.catrobat.catroid.uiespresso.stage.utils.ScriptEvaluationGateBrick
import org.catrobat.catroid.uiespresso.util.UiTestUtils
import org.catrobat.catroid.uiespresso.util.UserVariableAssertions.assertUserVariableEqualsWithTimeout
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityTestRule
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.Collections
import java.util.concurrent.atomic.AtomicInteger

/**
 * Runs the program from the review screenshot on the real stage:
 *
 *   When scene starts
 *     Face name detection            (FaceNameDetect brick -> FaceNameDetectAction)
 *     Set variable sa to (face name detection)
 *     Show variable sa at X 100 Y 200, size 120 %, '#FF0000', centered
 *
 * and checks what reaches the variable. Everything between the brick and the
 * variable is production code: the action, FaceDetector's permission check,
 * run gate and finish(), SensorHandler and the formula. Two tests use the real
 * camera Session; the others replace only the camera with a fixture photo fed
 * through the stage's recognition loop, so the expected name is known.
 *
 * Whether the camera was opened is observed with CameraManager's availability
 * callback, which reports every open and close of a camera device.
 */
@RunWith(AndroidJUnit4::class)
class FaceNameDetectStageTest {

    @get:Rule
    val stageRule = BaseActivityTestRule(StageActivity::class.java, false, false)

    @get:Rule
    val cameraPermission: GrantPermissionRule = GrantPermissionRule.grant(Manifest.permission.CAMERA)

    private val harness = FaceRecognitionHarness()
    private lateinit var sa: UserVariable
    private lateinit var endOfScript: ScriptEvaluationGateBrick
    private lateinit var cameraWatch: CameraWatch

    @Before
    fun setUp() {
        harness.start()
        FaceDetector.useCameraCapture()
        createProject()
        cameraWatch = CameraWatch(harness.appContext)
    }

    @After
    fun tearDown() {
        cameraWatch.stop()
        FaceDetector.useCameraCapture()
        FaceDetector.resetForNewRun()
        harness.stop()
    }

    @Test
    fun withoutTrainedDataDetectionEndsUnknownWithoutOpeningTheCamera() {
        stageRule.launchActivity(null)

        endOfScript.waitUntilEvaluated(NO_CAMERA_TIMEOUT_MS)
        assertUserVariableEqualsWithTimeout(sa, FaceDetector.UNKNOWN, 1000)
        assertDetectionFinished(FaceDetector.UNKNOWN)
        assertEquals(
            "With nobody trained the camera must not be opened",
            0, cameraWatch.opened.get()
        )
    }

    @Test
    fun withTrainedDataTheRealCameraIsOpenedCapturesAndIsReleased() {
        harness.trainPerson(PERSON_A, "p01")

        stageRule.launchActivity(null)

        // Whoever is in front of the phone, the detection has to finish with a
        // trained name or Unknown, and the script has to carry on.
        endOfScript.waitUntilEvaluated(CAMERA_TIMEOUT_MS)
        val name = FaceDetector.lastName
        assertTrue(
            "Detected name must be a trained name or Unknown, was '$name'",
            name == PERSON_A || name == FaceDetector.UNKNOWN
        )
        assertUserVariableEqualsWithTimeout(sa, name, 1000)
        assertDetectionFinished(name)

        assertTrue(
            "The front camera should have been opened for the capture",
            cameraWatch.opened.get() > 0
        )
        assertTrue(
            "The camera must be released after the capture; still open: ${cameraWatch.openIds}",
            cameraWatch.waitUntilAllClosed(CAMERA_RELEASE_TIMEOUT_MS)
        )
    }

    @Test
    fun trainedPersonInFrontOfTheCameraIsWrittenIntoTheVariable() {
        harness.trainPerson(PERSON_A, "p01")
        harness.trainPerson(PERSON_B, "p02")
        feedCaptureWith("p02_test.jpg")

        stageRule.launchActivity(null)

        endOfScript.waitUntilEvaluated(FIXTURE_TIMEOUT_MS)
        assertUserVariableEqualsWithTimeout(sa, PERSON_B, 1000)
        assertDetectionFinished(PERSON_B)
    }

    @Test
    fun untrainedPersonInFrontOfTheCameraIsUnknown() {
        harness.trainPerson(PERSON_A, "p01")
        harness.trainPerson(PERSON_B, "p02")
        feedCaptureWith("p03_test.jpg")

        stageRule.launchActivity(null)

        endOfScript.waitUntilEvaluated(FIXTURE_TIMEOUT_MS)
        assertUserVariableEqualsWithTimeout(sa, FaceDetector.UNKNOWN, 1000)
        assertDetectionFinished(FaceDetector.UNKNOWN)
    }

    @Test
    fun frameWithoutAFaceIsUnknown() {
        harness.trainPerson(PERSON_A, "p01")
        feedCaptureWith("no_face.jpeg")

        stageRule.launchActivity(null)

        endOfScript.waitUntilEvaluated(FIXTURE_TIMEOUT_MS)
        assertUserVariableEqualsWithTimeout(sa, FaceDetector.UNKNOWN, 1000)
        assertDetectionFinished(FaceDetector.UNKNOWN)
    }

    private fun createProject() {
        val project = UiTestUtils.createDefaultTestProject("FaceNameDetectStageTest")
        sa = UserVariable("sa")
        project.addUserVariable(sa)

        val script = UiTestUtils.getDefaultTestScript(project)
        script.addBrick(FaceNameDetect())
        script.addBrick(SetVariableBrick(faceNameSensor(), sa))
        script.addBrick(
            ShowTextColorSizeAlignmentBrick(100, 200, 120.0, "#FF0000").apply { userVariable = sa }
        )
        endOfScript = ScriptEvaluationGateBrick.appendToScript(script)
    }

    private fun faceNameSensor() = Formula(
        FormulaElement(
            FormulaElement.ElementType.SENSOR, Sensors.ON_DEVICE_FACE_RECOGNITION.name, null
        )
    )

    /** Replaces the camera with one fixture photo run through the stage's recognition loop. */
    private fun feedCaptureWith(fileName: String) {
        val stage = harness.StagePath()
        FaceDetector.captureStarter = FaceDetector.CaptureStarter { _, onResult ->
            Thread({
                val result = stage.recogniseFile(fileName)
                onResult.onFinished(result?.name, result?.confidence ?: 0f)
            }, "fake_face_capture").start()
        }
    }

    private fun assertDetectionFinished(expectedName: String) {
        assertFalse("Detection must not still be running", FaceDetector.isRunning)
        assertTrue("Detection must be marked done", FaceDetector.isDetectionDone)
        assertEquals(expectedName, FaceDetector.lastName)
    }

    /** Counts camera opens and tracks which cameras are open right now. */
    private class CameraWatch(context: Context) {
        private val manager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        private val thread = HandlerThread("camera_watch").apply { start() }
        private val initiallyUnavailable: MutableSet<String> = Collections.synchronizedSet(HashSet())
        private var registered = false

        val opened = AtomicInteger(0)
        val openIds: MutableSet<String> = Collections.synchronizedSet(HashSet())

        private val callback = object : CameraManager.AvailabilityCallback() {
            override fun onCameraUnavailable(cameraId: String) {
                if (registered) {
                    opened.incrementAndGet()
                    openIds.add(cameraId)
                } else {
                    initiallyUnavailable.add(cameraId)
                }
            }

            override fun onCameraAvailable(cameraId: String) {
                openIds.remove(cameraId)
            }
        }

        init {
            // The callback first reports the current state of every camera; let
            // that settle before counting opens caused by the test.
            manager.registerAvailabilityCallback(callback, Handler(thread.looper))
            Thread.sleep(SETTLE_MS)
            registered = true
        }

        fun waitUntilAllClosed(timeoutMs: Long): Boolean {
            val deadline = System.currentTimeMillis() + timeoutMs
            while (System.currentTimeMillis() < deadline) {
                if (openIds.isEmpty()) return true
                Thread.sleep(100)
            }
            return openIds.isEmpty()
        }

        fun stop() {
            manager.unregisterAvailabilityCallback(callback)
            thread.quitSafely()
        }

        private companion object {
            const val SETTLE_MS = 300L
        }
    }

    private companion object {
        const val NO_CAMERA_TIMEOUT_MS = 10_000
        const val FIXTURE_TIMEOUT_MS = 20_000
        /** FaceNameDetectAction gives up after 30 s; the Session watchdog fires after 20 s. */
        const val CAMERA_TIMEOUT_MS = 35_000
        const val CAMERA_RELEASE_TIMEOUT_MS = 3_000L
    }
}

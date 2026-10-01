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
package org.catrobat.catroid.uiespresso.facerecognizer

import android.Manifest
import android.graphics.Bitmap
import android.graphics.PixelFormat
import android.media.Image
import android.media.ImageReader
import android.os.Handler
import android.os.HandlerThread
import androidx.camera.core.ImageProxy
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import com.google.mlkit.vision.common.InputImage
import io.mockk.mockk
import io.mockk.verify
import org.catrobat.catroid.FaceRecognizer.FaceRecognitionHarness
import org.catrobat.catroid.FaceRecognizer.FaceRecognitionHarness.Companion.PERSON_A
import org.catrobat.catroid.FaceRecognizer.FaceRecognitionHarness.Companion.PERSON_B
import org.catrobat.catroid.R
import org.catrobat.catroid.camera.DetectorsCompleteListener
import org.catrobat.catroid.camera.VisualDetectionHandler
import org.catrobat.catroid.camera.mlkitdetectors.FaceDetector
import org.catrobat.catroid.camera.mlkitdetectors.FaceNameTestFrames
import org.catrobat.catroid.content.Script
import org.catrobat.catroid.content.StartScript
import org.catrobat.catroid.content.bricks.ChangeVariableBrick
import org.catrobat.catroid.content.bricks.ChooseCameraBrick
import org.catrobat.catroid.content.bricks.ForeverBrick
import org.catrobat.catroid.content.bricks.SetVariableBrick
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.formulaeditor.FormulaElement
import org.catrobat.catroid.formulaeditor.SensorCustomEventListener
import org.catrobat.catroid.formulaeditor.Sensors
import org.catrobat.catroid.formulaeditor.UserVariable
import org.catrobat.catroid.stage.StageActivity
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment
import org.catrobat.catroid.uiespresso.util.UiTestUtils
import org.catrobat.catroid.uiespresso.util.rules.BaseActivityTestRule
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.Collections
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

/**
 * "detected face name" as a sensor, like the face detection sensors: a program
 * that reads it in a formula starts camera analysis at program start, every
 * analysed frame updates the stored name, and reading the sensor never waits.
 *
 * Every program here has no detect brick. One script copies "detected face
 * name" into a variable in a forever loop; a second script increments a
 * counter in a forever loop, so a stage that stops running would show.
 *
 * The camera runs as in the app (CameraX, the camera Catroid's camera sensors
 * use); only the frames face name detection analyses are replaced by a fixture
 * photo through FaceNameTestFrames.
 */
@RunWith(AndroidJUnit4::class)
class FaceNameSensorStageTest {

    @get:Rule
    val stageRule = BaseActivityTestRule(StageActivity::class.java, false, false)

    @get:Rule
    val cameraPermission: GrantPermissionRule = GrantPermissionRule.grant(Manifest.permission.CAMERA)

    private val harness = FaceRecognitionHarness()
    private lateinit var name: UserVariable
    private lateinit var ticks: UserVariable
    private lateinit var faceX: UserVariable

    /** Frames face name detection asked for, and the camera facing for each. */
    private val framesAnalysed = AtomicInteger(0)
    private val cameraFacings: MutableList<Boolean> = Collections.synchronizedList(ArrayList())

    @Volatile
    private var fixture: String? = null

    private var savedFaceNameSetting = false
    private var savedFaceDetectionSetting = false

    @Before
    fun setUp() {
        harness.start()
        val context = harness.appContext
        savedFaceNameSetting = SettingsFragment.isAIFaceNameDetectionSharedPreferenceEnabled(context)
        savedFaceDetectionSetting = SettingsFragment.isAIFaceDetectionSharedPreferenceEnabled(context)
        SettingsFragment.setAIFaceNameDetectionPreferenceEnabled(context, true)
        SettingsFragment.setAIFaceDetectionPreferenceEnabled(context, false)

        FaceNameTestFrames.source = { frontCamera ->
            framesAnalysed.incrementAndGet()
            cameraFacings.add(frontCamera)
            fixture?.let { harness.fixtureBitmap(it) }
        }
    }

    @After
    fun tearDown() {
        stopUsingTheFaceModel()
        stageRule.finishActivity()
        val context = harness.appContext
        SettingsFragment.setAIFaceNameDetectionPreferenceEnabled(context, savedFaceNameSetting)
        SettingsFragment.setAIFaceDetectionPreferenceEnabled(context, savedFaceDetectionSetting)
        harness.stop()
        FaceNameTestFrames.source = null
    }

    /**
     * harness.stop() closes the face model, and a frame still being recognised
     * with it would crash the app. From here on face name detection gets frames
     * without a face, which never reach the model, until after harness.stop();
     * two more of them mean the frame being recognised has finished.
     */
    private fun stopUsingTheFaceModel() {
        val analysedSoFar = framesAnalysed.get()
        FaceNameTestFrames.source = {
            framesAnalysed.incrementAndGet()
            null
        }
        if (analysedSoFar == 0) {
            return
        }
        val end = System.currentTimeMillis() + TIMEOUT_MS
        while (framesAnalysed.get() < analysedSoFar + 2 && System.currentTimeMillis() < end) {
            Thread.sleep(POLL_MS)
        }
    }

    // ---------------- What the sensor reports ----------------

    @Test
    fun trainedPersonIsWrittenIntoTheVariableWhileTheStageKeepsRunning() {
        trainBoth()
        fixture = "p02_test.jpg"
        createProject()

        launchStage()

        val ticksBefore = ticks.value as Double
        waitForName(PERSON_B)
        assertTrue("The counter script must keep running", ticks.value as Double > ticksBefore)
        assertStageKeepsRunning()
    }

    @Test
    fun untrainedPersonIsUnknown() {
        trainBoth()
        fixture = "p03_test.jpg"
        createProject()

        launchStage()

        waitForAnalysedFrames(ENOUGH_FRAMES)
        assertNameStaysUnknown(STAY_UNKNOWN_MS)
        assertStageKeepsRunning()
    }

    @Test
    fun frameWithoutAFaceIsUnknown() {
        trainBoth()
        fixture = "no_face.jpeg"
        createProject()

        launchStage()

        waitForAnalysedFrames(ENOUGH_FRAMES)
        assertNameStaysUnknown(STAY_UNKNOWN_MS)
        assertStageKeepsRunning()
    }

    @Test
    fun withNobodyTrainedTheNameIsUnknown() {
        fixture = "p02_test.jpg"
        createProject()

        launchStage()

        waitForAnalysedFrames(ENOUGH_FRAMES)
        assertNameStaysUnknown(STAY_UNKNOWN_MS)
        assertStageKeepsRunning()
    }

    @Test
    fun personTrainedWhileTheProgramRunsIsRecognisedFromTheNextFrames() {
        harness.trainPerson(PERSON_A, "p01")
        fixture = "p02_test.jpg"
        createProject()

        launchStage()
        waitForAnalysedFrames(ENOUGH_FRAMES)
        assertNameStaysUnknown(1000)

        // The same recogniser the training brick stores into.
        harness.trainPerson(PERSON_B, "p02")

        waitForName(PERSON_B)
        assertStageKeepsRunning()
    }

    @Test
    fun restartFromTheStageMenuStartsAgainFromUnknown() {
        trainBoth()
        fixture = "p02_test.jpg"
        createProject()
        launchStage()
        waitForName(PERSON_B)

        // Frames of someone untrained from now on: frames of Person B left in
        // the window from before the restart would still decide Person B.
        fixture = "p03_test.jpg"
        pressBack()
        onView(withId(R.id.stage_dialog_button_restart)).inRoot(isDialog()).perform(click())

        val framesAtRestart = framesAnalysed.get()
        waitForAnalysedFrames(framesAtRestart + ENOUGH_FRAMES)
        assertNameStaysUnknown(STAY_UNKNOWN_MS)

        fixture = "p02_test.jpg"
        waitForName(PERSON_B)
    }

    // ---------------- When the camera runs ----------------

    @Test
    fun programWithoutTheSensorDoesNotAnalyseFramesForFaceNames() {
        trainBoth()
        fixture = "p02_test.jpg"
        createProject(readFaceName = false)

        launchStage()

        val ticksBefore = ticks.value as Double
        Thread.sleep(STAY_UNKNOWN_MS)
        assertTrue("The counter script must run", ticks.value as Double > ticksBefore)
        assertEquals("No frame may be analysed for face names", 0, framesAnalysed.get())
    }

    /**
     * Face name detection uses the camera Catroid's other camera sensors and
     * bricks use: the front camera by default, the back camera after "Choose
     * camera back".
     */
    @Test
    fun frontCameraIsTheDefaultAndChooseCameraSwitchesTheAnalysedCamera() {
        trainBoth()
        fixture = "p02_test.jpg"
        createProject()
        launchStage()
        waitForName(PERSON_B)

        val cameraManager = StageActivity.getActiveCameraManager()
        assertNotNull(cameraManager)
        assertTrue("This test needs a phone with a front camera", cameraManager!!.hasFrontCamera)
        assertTrue("Front camera by default", cameraFacings.first())
        assertTrue(cameraManager.isCameraFacingFront)

        cameraManager.switchToBackCamera()
        cameraFacings.clear()
        waitForAnalysedFrames(framesAnalysed.get() + ENOUGH_FRAMES)

        assertFalse(cameraManager.isCameraFacingFront)
        assertTrue(
            "After the switch only back camera frames may be analysed, were $cameraFacings",
            cameraFacings.isNotEmpty() && cameraFacings.none { it }
        )
        waitForName(PERSON_B)
        assertStageKeepsRunning()
    }

    @Test
    fun chooseCameraBackBrickIsFollowed() {
        trainBoth()
        fixture = "p02_test.jpg"
        createProject(firstBrick = ChooseCameraBrick(false))

        launchStage()

        waitForName(PERSON_B)
        val cameraManager = StageActivity.getActiveCameraManager()!!
        assertFalse(cameraManager.isCameraFacingFront)
        val facings = synchronized(cameraFacings) { cameraFacings.toList() }
        assertFalse("A frame after the switch must come from the back camera", facings.last())
    }

    // ---------------- Together with another camera sensor ----------------

    /**
     * "face x position" (ML Kit face detection) and "detected face name" in one
     * program: both run on the same camera frames at the same time.
     */
    @Test
    fun faceXPositionSensorWorksTogetherWithTheFaceNameSensor() {
        SettingsFragment.setAIFaceDetectionPreferenceEnabled(harness.appContext, true)
        trainBoth()
        fixture = "p02_test.jpg"
        createProject(readFaceX = true)

        val faceSensorWrites = AtomicInteger(0)
        val faceXValues: MutableList<Double> = Collections.synchronizedList(ArrayList())
        val listener = SensorCustomEventListener { event ->
            when (event.sensor) {
                Sensors.FACE_DETECTED -> faceSensorWrites.incrementAndGet()
                Sensors.FACE_X -> faceXValues.add((event.value as Number).toDouble())
                else -> Unit
            }
        }
        VisualDetectionHandler.addListener(listener)
        try {
            launchStage()
            waitForName(PERSON_B)

            // ML Kit's face detection analyses the real camera frames meanwhile.
            val writesBefore = faceSensorWrites.get()
            Thread.sleep(1000)
            assertTrue("ML Kit face detection must keep running", faceSensorWrites.get() > writesBefore)

            // The real camera shows no face, so give ML Kit's face detector the
            // same photo face name detection is analysing.
            val frameClosed = feedPhotoToMlKitFaceDetection("p02_test.jpg")
            verify(exactly = 1) { frameClosed.close() }
            waitUntil("face x position from the photo") { faceXValues.isNotEmpty() }
            assertEquals("face x position must reach the program", faceXValues.last(), faceX.value as Double, 0.5)

            assertEquals(PERSON_B, name.value)
            assertStageKeepsRunning()
        } finally {
            VisualDetectionHandler.removeListener(listener)
        }
    }

    // ---------------- Helpers ----------------

    private fun trainBoth() {
        harness.trainPerson(PERSON_A, "p01")
        harness.trainPerson(PERSON_B, "p02")
    }

    private fun createProject(
        readFaceName: Boolean = true,
        readFaceX: Boolean = false,
        firstBrick: ChooseCameraBrick? = null
    ) {
        val project = UiTestUtils.createDefaultTestProject("FaceNameSensorStageTest")
        name = UserVariable("name", "")
        ticks = UserVariable("ticks", 0.0)
        faceX = UserVariable("faceX", 0.0)
        project.addUserVariable(name)
        project.addUserVariable(ticks)
        project.addUserVariable(faceX)

        val readingScript: Script = UiTestUtils.getDefaultTestScript(project)
        firstBrick?.let { readingScript.addBrick(it) }
        if (readFaceName || readFaceX) {
            readingScript.addBrick(
                ForeverBrick().apply {
                    if (readFaceName) {
                        addBrick(SetVariableBrick(sensor(Sensors.ON_DEVICE_FACE_RECOGNITION), name))
                    }
                    if (readFaceX) {
                        addBrick(SetVariableBrick(sensor(Sensors.FACE_X), faceX))
                    }
                }
            )
        }

        UiTestUtils.getDefaultTestSprite(project).addScript(
            StartScript().apply {
                addBrick(ForeverBrick().apply { addBrick(ChangeVariableBrick(Formula(1.0), ticks)) })
            }
        )
    }

    private fun sensor(sensor: Sensors) =
        Formula(FormulaElement(FormulaElement.ElementType.SENSOR, sensor.name, null))

    private fun launchStage() {
        stageRule.launchActivity(null)
        waitUntil("the counter script to start") { (ticks.value as Double) > 0.0 }
    }

    private fun waitForName(expected: String) {
        waitUntil("'$expected' in the variable, was '${name.value}' after ${framesAnalysed.get()} frames") {
            name.value == expected
        }
    }

    private fun waitForAnalysedFrames(count: Int) {
        waitUntil("$count analysed frames, were ${framesAnalysed.get()}") { framesAnalysed.get() >= count }
    }

    private fun assertNameStaysUnknown(durationMs: Long) {
        val end = System.currentTimeMillis() + durationMs
        while (System.currentTimeMillis() < end) {
            val value = name.value
            if (value != UNKNOWN) {
                fail("The name must be $UNKNOWN, was '$value'")
            }
            Thread.sleep(POLL_MS)
        }
    }

    /** The counter keeps increasing: nothing holds the stage. */
    private fun assertStageKeepsRunning() {
        val before = ticks.value as Double
        Thread.sleep(500)
        assertTrue("The stage must keep running", ticks.value as Double > before)
    }

    private fun waitUntil(what: String, condition: () -> Boolean) {
        val end = System.currentTimeMillis() + TIMEOUT_MS
        while (System.currentTimeMillis() < end) {
            if (condition()) {
                return
            }
            Thread.sleep(POLL_MS)
        }
        if (!condition()) {
            fail("Timed out after $TIMEOUT_MS ms waiting for $what")
        }
    }

    /**
     * Runs ML Kit's face detection (the face position sensors) on a photo, as
     * CatdroidImageAnalyzer does with a camera frame. Returns the frame, which
     * must have been released once.
     */
    private fun feedPhotoToMlKitFaceDetection(fileName: String): ImageProxy {
        val photo = harness.fixtureBitmap(fileName)
        val image = imageOfSize(photo)
        val frame = mockk<ImageProxy>(relaxed = true)
        FaceDetector.processImage(image, InputImage.fromBitmap(photo, 0), DetectorsCompleteListener(1, frame))
        waitUntil("ML Kit to release the frame") {
            try {
                verify(exactly = 1) { frame.close() }
                true
            } catch (notYet: AssertionError) {
                false
            }
        }
        image.close()
        return frame
    }

    /** An Image of the photo's size; ML Kit's face detector reads only its size. */
    private fun imageOfSize(photo: Bitmap): Image {
        val thread = HandlerThread("photo_image").apply { start() }
        val reader = ImageReader.newInstance(photo.width, photo.height, PixelFormat.RGBA_8888, 1)
        val ready = CountDownLatch(1)
        reader.setOnImageAvailableListener({ ready.countDown() }, Handler(thread.looper))
        val canvas = reader.surface.lockCanvas(null)
        canvas.drawBitmap(photo, 0f, 0f, null)
        reader.surface.unlockCanvasAndPost(canvas)
        assertTrue("No image from the ImageReader", ready.await(5, TimeUnit.SECONDS))
        thread.quitSafely()
        return reader.acquireLatestImage()
    }

    private companion object {
        const val UNKNOWN = "Unknown"

        /** Recognition loads its models on first use; generous for a slow phone. */
        const val TIMEOUT_MS = 30_000L
        const val POLL_MS = 50L

        /** Frames to analyse before "still Unknown" means something. */
        const val ENOUGH_FRAMES = 5
        const val STAY_UNKNOWN_MS = 2_000L
    }
}

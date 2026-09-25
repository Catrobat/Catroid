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
package org.catrobat.catroid.content.actions

import android.Manifest
import android.app.Application
import androidx.test.core.app.ApplicationProvider
import org.catrobat.catroid.FaceRecognizer.FaceDetector
import org.catrobat.catroid.formulaeditor.SensorHandler
import org.catrobat.catroid.formulaeditor.Sensors
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf

/**
 * The recognition half of the feature: the Face Name Detect brick's action,
 * through FaceDetector, into the face-name sensor that formulas read.
 *
 * Only the camera capture is replaced (FaceDetector.captureStarter). The
 * permission check, the per-run gate, FaceDetector.finish() writing the sensor
 * and the action's waiting logic are the production code.
 */
@RunWith(RobolectricTestRunner::class)
class FaceNameDetectActionTest {

    private lateinit var application: Application
    private lateinit var capture: FakeCapture
    private lateinit var action: FaceNameDetectAction

    @Before
    fun setUp() {
        application = ApplicationProvider.getApplicationContext()
        capture = FakeCapture()
        FaceDetector.captureStarter = capture
        FaceDetector.resetForNewRun()
        action = FaceNameDetectAction()
    }

    @After
    fun tearDown() {
        capture.completePendingWith(null)
        FaceDetector.useCameraCapture()
        FaceDetector.resetForNewRun()
    }

    @Test
    fun recognisedNameIsWrittenIntoTheSensor() {
        grantCamera()

        assertFalse("The action must wait while the camera is working", action.act(FRAME))
        assertEquals(1, capture.starts)

        capture.completePendingWith("Person A")

        assertTrue(action.act(FRAME))
        assertEquals("Person A", action.detectedName)
        assertEquals("Person A", faceNameSensor())
    }

    @Test
    fun actionKeepsWaitingUntilTheCaptureReports() {
        grantCamera()

        assertFalse(action.act(FRAME))
        assertFalse(action.act(FRAME))
        assertFalse(action.act(FRAME))
        assertEquals("Only one capture may be started per brick run", 1, capture.starts)
        assertEquals(FaceDetector.UNKNOWN, faceNameSensor())

        capture.completePendingWith("Person B")

        assertTrue(action.act(FRAME))
        assertEquals("Person B", faceNameSensor())
    }

    @Test
    fun unrecognisedFaceWritesUnknown() {
        grantCamera()

        action.act(FRAME)
        capture.completePendingWith(null)

        assertTrue(action.act(FRAME))
        assertEquals(FaceDetector.UNKNOWN, action.detectedName)
        assertEquals(FaceDetector.UNKNOWN, faceNameSensor())
    }

    @Test
    fun nameFromThePreviousRunIsClearedBeforeTheNextDetection() {
        grantCamera()
        action.act(FRAME)
        capture.completePendingWith("Person A")
        assertTrue(action.act(FRAME))
        assertEquals("Person A", faceNameSensor())

        // The brick is reached again, e.g. in a loop.
        assertFalse(action.act(FRAME))
        assertEquals(
            "A stale name must not be visible while the next detection runs",
            FaceDetector.UNKNOWN,
            faceNameSensor()
        )
        assertEquals(2, capture.starts)

        capture.completePendingWith("Person C")
        assertTrue(action.act(FRAME))
        assertEquals("Person C", action.detectedName)
        assertEquals("Person C", faceNameSensor())
    }

    @Test
    fun missingCameraPermissionFinishesWithUnknownWithoutOpeningTheCamera() {
        SensorHandler.setFaceNameRecognitionResult("Person A")

        assertTrue(action.act(FRAME))

        assertEquals(0, capture.starts)
        assertEquals(FaceDetector.UNKNOWN, action.detectedName)
        assertEquals(FaceDetector.UNKNOWN, faceNameSensor())
    }

    @Test
    fun captureThatNeverReportsTimesOutWithUnknown() {
        grantCamera()

        assertFalse(action.act(FRAME))
        assertTrue("The action must give up after its timeout", action.act(TIMEOUT_PLUS_ONE))

        assertEquals(FaceDetector.UNKNOWN, action.detectedName)
        assertEquals(FaceDetector.UNKNOWN, faceNameSensor())
    }

    private fun grantCamera() {
        shadowOf(application).grantPermissions(Manifest.permission.CAMERA)
    }

    private fun faceNameSensor(): Any? {
        val value = SensorHandler.getSensorValue(Sensors.ON_DEVICE_FACE_RECOGNITION)
        assertNotNull(value)
        return value
    }

    /** Stands in for the camera session; the test decides when and what it reports. */
    private class FakeCapture : FaceDetector.CaptureStarter {
        var starts = 0
            private set
        private var pending: FaceDetector.Callback? = null

        override fun start(context: android.content.Context, onResult: FaceDetector.Callback) {
            starts++
            pending = onResult
        }

        fun completePendingWith(name: String?) {
            val callback = pending ?: return
            pending = null
            callback.onFinished(name, if (name == null) 0f else 0.8f)
        }
    }

    private companion object {
        const val FRAME = 1f / 60f

        /** FaceNameDetectAction.TIMEOUT_SECONDS is 30. */
        const val TIMEOUT_PLUS_ONE = 31f
    }
}

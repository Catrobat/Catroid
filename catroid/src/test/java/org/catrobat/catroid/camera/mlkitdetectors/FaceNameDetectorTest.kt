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
package org.catrobat.catroid.camera.mlkitdetectors

import android.graphics.Bitmap
import android.media.Image
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import io.mockk.mockk
import io.mockk.verify
import org.catrobat.catroid.FaceRecognizer.FaceNameWindow
import org.catrobat.catroid.FaceRecognizer.Recognizer
import org.catrobat.catroid.camera.DetectorsCompleteListener
import org.catrobat.catroid.formulaeditor.SensorHandler
import org.catrobat.catroid.formulaeditor.Sensors
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.io.IOException
import java.util.concurrent.Executor

/**
 * Every camera frame passed to [FaceNameDetector] must be released exactly once,
 * whether it is analysed, skipped by the 300 ms throttle, skipped because the
 * previous frame is still being recognised, or unreadable. A frame that is not
 * released is never closed, and then all camera analysis stops.
 *
 * Recognition runs on a queue the test runs by hand, so "still being
 * recognised" is a state the test controls.
 */
@RunWith(RobolectricTestRunner::class)
class FaceNameDetectorTest {

    private val queued = ArrayDeque<Runnable>()
    private var now = 10_000L

    private lateinit var savedClock: () -> Long
    private lateinit var savedExecutor: Executor
    private lateinit var savedRecognizerProvider: () -> Recognizer?

    @Before
    fun setUp() {
        savedClock = FaceNameDetector.clock
        savedExecutor = FaceNameDetector.recognitionExecutor
        savedRecognizerProvider = FaceNameDetector.recognizerProvider

        FaceNameDetector.clock = { now }
        FaceNameDetector.recognitionExecutor = Executor { queued.addLast(it) }
        FaceNameDetector.recognizerProvider = { null }
        FaceNameTestFrames.source = { Bitmap.createBitmap(4, 4, Bitmap.Config.ARGB_8888) }
        FaceNameDetector.reset()
    }

    @After
    fun tearDown() {
        runRecognition()
        FaceNameTestFrames.source = null
        FaceNameDetector.clock = savedClock
        FaceNameDetector.recognitionExecutor = savedExecutor
        FaceNameDetector.recognizerProvider = savedRecognizerProvider
        FaceNameDetector.reset()
    }

    @Test
    fun anAnalysedFrameIsReleasedOnceAndRecognisedOnTheOtherThread() {
        val frame = offerFrame()

        verify(exactly = 1) { frame.close() }
        assertEquals("Recognition must not run on the camera analysis thread", 1, queued.size)
    }

    @Test
    fun aFrameWithin300MsIsSkippedAndReleasedOnce() {
        offerFrame()
        runRecognition()

        now += FaceNameWindow.FRAME_INTERVAL_MS - 1
        val skipped = offerFrame()

        verify(exactly = 1) { skipped.close() }
        assertEquals("A skipped frame must not be recognised", 0, queued.size)
    }

    @Test
    fun aFrameWhileRecognitionIsBusyIsSkippedAndReleasedOnce() {
        offerFrame()

        now += 10 * FaceNameWindow.FRAME_INTERVAL_MS
        val whileBusy = offerFrame()

        verify(exactly = 1) { whileBusy.close() }
        assertEquals("Only the first frame may be waiting for recognition", 1, queued.size)
    }

    @Test
    fun theNextDueFrameIsAnalysedOnceRecognitionIsDone() {
        offerFrame()
        runRecognition()

        now += FaceNameWindow.FRAME_INTERVAL_MS
        val next = offerFrame()

        verify(exactly = 1) { next.close() }
        assertEquals(1, queued.size)
    }

    @Test
    fun aFrameThatCannotBeReadIsReleasedOnce() {
        FaceNameTestFrames.source = { throw IllegalStateException("broken frame") }

        val broken = offerFrame()

        verify(exactly = 1) { broken.close() }
        assertEquals(0, queued.size)
    }

    @Test
    fun aStreamOfFramesIsReleasedFrameByFrame() {
        val frames = (1..60).map { index ->
            now += 50
            offerFrame().also {
                if (index % 4 == 0) {
                    runRecognition()
                }
            }
        }

        frames.forEach { verify(exactly = 1) { it.close() } }
    }

    @Test
    fun untilTheRecognizerIsLoadedTheNameStaysUnknown() {
        offerFrame()
        runRecognition()

        assertEquals(FaceNameWindow.UNKNOWN, SensorHandler.getSensorValue(Sensors.ON_DEVICE_FACE_RECOGNITION))
    }

    @Test
    fun resetSetsTheSensorBackToUnknown() {
        SensorHandler.setFaceNameRecognitionResult("Person A")

        FaceNameDetector.reset()

        assertEquals(FaceNameWindow.UNKNOWN, SensorHandler.getSensorValue(Sensors.ON_DEVICE_FACE_RECOGNITION))
    }

    // ---------------- Errors never escape a camera or recognition thread ----------------

    @Test
    fun aFrameThatRunsOutOfMemoryIsReleasedOnceAndNothingEscapes() {
        FaceNameTestFrames.source = { throw OutOfMemoryError("no room for the frame") }

        val frame = offerFrame()

        verify(exactly = 1) { frame.close() }
        assertEquals(0, queued.size)
    }

    @Test
    fun faceModelsThatCannotBeReadDoNotEscapeTheRecognitionThread() {
        FaceNameDetector.recognizerProvider = { throw IOException("facenet.tflite is missing") }

        offerFrame()
        runRecognition()

        assertEquals(FaceNameWindow.UNKNOWN, SensorHandler.getSensorValue(Sensors.ON_DEVICE_FACE_RECOGNITION))
    }

    @Test
    fun aNativeLibraryThatCannotBeLoadedDoesNotEscapeAndIsNotRetriedOnEveryFrame() {
        var attempts = 0
        FaceNameDetector.recognizerProvider = {
            attempts++
            throw UnsatisfiedLinkError("libtensorflowlite_jni.so not found")
        }

        offerFrame()
        runRecognition()
        repeat(5) {
            now += FaceNameWindow.FRAME_INTERVAL_MS
            val frame = offerFrame()
            verify(exactly = 1) { frame.close() }
            runRecognition()
        }

        assertEquals("The models are not loaded again until the next program start", 1, attempts)
        assertEquals(FaceNameWindow.UNKNOWN, SensorHandler.getSensorValue(Sensors.ON_DEVICE_FACE_RECOGNITION))

        FaceNameDetector.reset()
        now += FaceNameWindow.FRAME_INTERVAL_MS
        offerFrame()
        runRecognition()
        assertEquals("A new program start tries again", 2, attempts)
    }

    /** Passes one camera frame through the detector, as CatdroidImageAnalyzer does. */
    private fun offerFrame(): ImageProxy {
        val frame = mockk<ImageProxy>(relaxed = true)
        FaceNameDetector.processImage(
            mockk<Image>(relaxed = true),
            mockk<InputImage>(relaxed = true),
            DetectorsCompleteListener(1, frame)
        )
        return frame
    }

    private fun runRecognition() {
        while (queued.isNotEmpty()) {
            queued.removeFirst().run()
        }
    }
}

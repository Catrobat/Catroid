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
package org.catrobat.catroid.camera

import android.media.Image
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.catrobat.catroid.camera.mlkitdetectors.FaceNameDetector
import org.catrobat.catroid.formulaeditor.SensorHandler
import org.catrobat.catroid.formulaeditor.Sensors
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.util.concurrent.Executor

/**
 * The camera keeps sending frames only while each one is released. Found in a
 * review of the camera paths.
 */
@RunWith(RobolectricTestRunner::class)
class CameraAnalyzersTest {

    private val savedExecutor: Executor = FaceNameDetector.recognitionExecutor

    @After
    fun tearDown() {
        FaceNameDetector.recognitionExecutor = savedExecutor
        FaceNameDetector.frameSource = null
        FaceNameDetector.reset()
        CatdroidImageAnalyzer.setActiveDetectorsWithContext(null)
    }

    /**
     * With every AI setting off (for example face name detection turned off
     * while a project that uses it is open) no detector releases the frame:
     * the camera stopped sending any. A frame no detector looks at is released.
     */
    @Test
    fun aFrameNoDetectorLooksAtIsReleased() {
        CatdroidImageAnalyzer.setActiveDetectorsWithContext(null)
        val frame = frame()

        CatdroidImageAnalyzer.analyze(frame)

        verify(exactly = 1) { frame.close() }
    }

    @Test
    fun aFrameWithoutAnImageIsReleased() {
        val frame = mockk<ImageProxy>(relaxed = true)
        every { frame.image } returns null

        CatdroidImageAnalyzer.analyze(frame)

        verify(exactly = 1) { frame.close() }
    }

    /**
     * Face name detection needs no mobile services, but it ran only in the
     * Google Play services analyser: on Huawei devices, and on devices with
     * neither, the sensor stayed Unknown. It runs before the Huawei analyser,
     * or alone, and the frame is released once.
     */
    @Test
    fun withoutGooglePlayServicesFaceNamesAreStillAnalysedAndTheFrameReleasedOnce() {
        var analysed = 0
        FaceNameDetector.recognitionExecutor = Executor { }
        FaceNameDetector.frameSource = {
            analysed++
            null
        }
        SensorHandler.getSensorValue(Sensors.ON_DEVICE_FACE_RECOGNITION)
        val alone = frame()

        FaceNameFirstAnalyzer(null).analyze(alone)

        assertEquals(1, analysed)
        verify(exactly = 1) { alone.close() }
    }

    @Test
    fun beforeAnotherAnalyserTheFrameIsHandedOnNotReleased() {
        val next = mockk<ImageAnalysis.Analyzer>(relaxed = true)
        val frame = frame()

        FaceNameFirstAnalyzer(next).analyze(frame)

        verify(exactly = 1) { next.analyze(frame) }
        verify(exactly = 0) { frame.close() }
    }

    private fun frame(): ImageProxy {
        val frame = mockk<ImageProxy>(relaxed = true)
        every { frame.image } returns mockk<Image>(relaxed = true)
        return frame
    }
}

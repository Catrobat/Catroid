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
import android.graphics.ImageFormat
import android.graphics.Matrix
import android.media.Image
import android.os.SystemClock
import android.util.Log
import androidx.annotation.VisibleForTesting
import com.google.mlkit.vision.common.InputImage
import org.catrobat.catroid.CatroidApplication
import org.catrobat.catroid.FaceRecognizer.FaceNameWindow
import org.catrobat.catroid.FaceRecognizer.Recognizer
import org.catrobat.catroid.camera.DetectorsCompleteListener
import org.catrobat.catroid.formulaeditor.SensorHandler
import org.catrobat.catroid.stage.StageActivity
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import java.util.concurrent.RejectedExecutionException
import java.util.concurrent.atomic.AtomicBoolean

/**
 * "detected face name": recognises the largest face in the camera frames the
 * face detection sensors also analyse, and stores the name in SensorHandler.
 *
 * At most one frame every [FaceNameWindow.FRAME_INTERVAL_MS] is analysed, and
 * none while the previous one is still being recognised. An analysed frame is
 * turned into an upright bitmap on the camera analysis thread, the camera frame
 * is released, and recognition runs on a separate thread. The name is decided
 * by [FaceNameWindow] from the last three frames with a usable face.
 */
object FaceNameDetector : Detector {
    private const val TAG = "FaceNameDetector"

    @VisibleForTesting
    internal val window = FaceNameWindow()

    @VisibleForTesting
    internal var clock: () -> Long = { SystemClock.elapsedRealtime() }

    /** Recognition runs here, never on the camera analysis thread. */
    @VisibleForTesting
    internal var recognitionExecutor: Executor = Executors.newSingleThreadExecutor { runnable ->
        Thread(runnable, "FaceNameRecognition")
    }

    /**
     * Test-only: replaces the camera frames face name detection analyses with
     * fixture photos; everything after the frame stays the production code.
     * Called once for every frame that is analysed, with whether the active
     * camera faces front; returns the upright frame, which the detector then
     * owns and recycles, or null for a frame without a usable face. Null in
     * the app: frames come from the camera.
     */
    @VisibleForTesting
    @Volatile
    internal var frameSource: ((frontCamera: Boolean) -> Bitmap?)? = null

    /** Loads the face models on first use; until then the name stays Unknown. */
    @VisibleForTesting
    internal var recognizerProvider: () -> Recognizer? = {
        CatroidApplication.getAppContext()?.let { Recognizer.getInstance(it) }
    }

    private val busy = AtomicBoolean(false)

    /**
     * Set when the face models could not be loaded. Until the next program
     * start no frame is analysed and the name stays Unknown, instead of the
     * models being loaded again for every frame.
     */
    @Volatile
    private var recognitionUnavailable = false

    /**
     * Face name analysis is much heavier than the ML Kit detectors, so it stays
     * off until the program first reads "detected face name" (see [activate]).
     * Until then every frame is released without being looked at. A project
     * that uses only other camera sensors never turns it on.
     */
    @Volatile
    private var activated = false

    /** Guards the window and the sensor against a frame recognised across a [reset]. */
    private val lock = Any()
    private var runId = 0

    private class Task(val frame: Bitmap?, val frontCamera: Boolean, val takenAt: Long, val runId: Int)

    override fun processImage(
        mediaImage: Image,
        inputImage: InputImage,
        onCompleteListener: DetectorsCompleteListener
    ) {
        val task = try {
            takeFrame(mediaImage, inputImage.rotationDegrees)
        } catch (exception: RuntimeException) {
            Log.e(TAG, "Could not read the camera frame", exception)
            null
        } catch (error: OutOfMemoryError) {
            Log.e(TAG, "No memory for the camera frame; skipped", error)
            null
        } finally {
            onCompleteListener.onComplete()
        }
        task?.let { recognise(it) }
    }

    /**
     * Start of a program run, including a restart from the stage menu: no
     * frames in the window and "detected face name" back to Unknown.
     */
    /** Called when the program reads "detected face name": from now on frames are analysed. */
    @JvmStatic
    fun activate() {
        if (!activated) {
            activated = true
            Log.i(TAG, "Face name analysis started: the program read the sensor")
        }
    }

    @JvmStatic
    fun reset() {
        synchronized(lock) {
            runId++
            activated = false
            recognitionUnavailable = false
            window.clear()
            SensorHandler.setFaceNameRecognitionResult(FaceNameWindow.UNKNOWN)
        }
    }

    /** The frame to recognise, or null when this one is skipped. */
    private fun takeFrame(mediaImage: Image, rotationDegrees: Int): Task? {
        if (!activated || busy.get() || recognitionUnavailable) {
            return null
        }
        val now = clock()
        if (!window.isFrameDue(now)) {
            return null
        }
        val frontCamera = isFrontCamera()
        val source = frameSource
        val frame = if (source != null) source(frontCamera) else uprightBitmap(mediaImage, rotationDegrees)
        busy.set(true)
        return Task(frame, frontCamera, now, synchronized(lock) { runId })
    }

    private fun recognise(task: Task) {
        try {
            recognitionExecutor.execute {
                try {
                    recogniseNow(task)
                } catch (exception: Exception) {
                    Log.e(TAG, "Face name recognition failed", exception)
                } catch (error: OutOfMemoryError) {
                    Log.e(TAG, "No memory to recognise the frame; skipped", error)
                } catch (error: LinkageError) {
                    // Fails the same way on every frame; stop until the next program start.
                    giveUp(error)
                } finally {
                    task.frame?.recycle()
                    busy.set(false)
                }
            }
        } catch (exception: RejectedExecutionException) {
            Log.e(TAG, "Face name recognition is not running", exception)
            task.frame?.recycle()
            busy.set(false)
        }
    }

    private fun recogniseNow(task: Task) {
        val recognizer = loadRecognizer() ?: return
        val scored = FaceNameWindow.scoreOf(recognizer, task.frame, task.frontCamera)
        synchronized(lock) {
            if (task.runId != runId) {
                return
            }
            val name = window.addScores(recognizer, scored?.scores, task.takenAt, scored?.names)
            SensorHandler.setFaceNameRecognitionResult(name)
        }
    }

    /** The recogniser, loading the face models on first use; null when they cannot be loaded. */
    private fun loadRecognizer(): Recognizer? = try {
        recognizerProvider()
    } catch (exception: Exception) {
        giveUp(exception)
    } catch (error: LinkageError) {
        giveUp(error)
    } catch (error: OutOfMemoryError) {
        giveUp(error)
    }

    private fun giveUp(cause: Throwable): Recognizer? {
        recognitionUnavailable = true
        Log.e(TAG, "The face models cannot be used; detected face name stays Unknown", cause)
        return null
    }

    /** False when there is no stage or no camera manager. */
    private fun isFrontCamera(): Boolean =
        StageActivity.getActiveCameraManager()?.isCameraFacingFront == true

    /** The YUV_420_888 camera frame as an upright ARGB_8888 bitmap. */
    private fun uprightBitmap(image: Image, rotationDegrees: Int): Bitmap? {
        if (image.format != ImageFormat.YUV_420_888) {
            Log.w(TAG, "Unexpected camera frame format ${image.format}")
            return null
        }
        val width = image.width
        val height = image.height
        val yPlane = image.planes[0]
        val uPlane = image.planes[1]
        val vPlane = image.planes[2]
        val yBuffer = yPlane.buffer
        val uBuffer = uPlane.buffer
        val vBuffer = vPlane.buffer
        val yRowStride = yPlane.rowStride
        val yPixelStride = yPlane.pixelStride
        val uvRowStride = uPlane.rowStride
        val uvPixelStride = uPlane.pixelStride

        val pixels = IntArray(width * height)
        for (row in 0 until height) {
            val yRow = row * yRowStride
            val uvRow = (row shr 1) * uvRowStride
            for (column in 0 until width) {
                val y = yBuffer[yRow + column * yPixelStride].toInt() and 0xff
                val uvIndex = uvRow + (column shr 1) * uvPixelStride
                val u = (uBuffer[uvIndex].toInt() and 0xff) - 128
                val v = (vBuffer[uvIndex].toInt() and 0xff) - 128
                val red = clamp(y + ((1436 * v) shr 10))
                val green = clamp(y - ((352 * u + 731 * v) shr 10))
                val blue = clamp(y + ((1815 * u) shr 10))
                pixels[row * width + column] = (0xff shl 24) or (red shl 16) or (green shl 8) or blue
            }
        }
        val frame = Bitmap.createBitmap(pixels, width, height, Bitmap.Config.ARGB_8888)
        if (rotationDegrees % 360 == 0) {
            return frame
        }
        val rotation = Matrix().apply { postRotate(rotationDegrees.toFloat()) }
        val upright = Bitmap.createBitmap(frame, 0, 0, width, height, rotation, true)
        if (upright !== frame) {
            frame.recycle()
        }
        return upright
    }

    private fun clamp(value: Int): Int = value.coerceIn(0, 255)
}

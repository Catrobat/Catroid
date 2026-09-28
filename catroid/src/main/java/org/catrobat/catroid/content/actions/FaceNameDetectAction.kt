package org.catrobat.catroid.content.actions

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.badlogic.gdx.scenes.scene2d.Action
import org.catrobat.catroid.CatroidApplication
import org.catrobat.catroid.FaceRecognizer.FaceDetector

/**
 * Opens face recognition and pauses the script until detection finishes.
 */
class FaceNameDetectAction : Action() {

    private enum class State {
        IDLE,
        WAITING,
        DONE
    }

    private var state: State = State.IDLE
    private var elapsed: Float = 0f

    @Volatile
    private var resultReady: Boolean = false

    var detectedName: String = FaceDetector.UNKNOWN
        private set

    @RequiresApi(Build.VERSION_CODES.N)
    override fun act(delta: Float): Boolean {
        if (state == State.DONE) {
            Log.i(
                TAG,
                "Brick reached again; resetting detection"
            )

            clearState()
        }

        if (state == State.IDLE) {
            Log.i(
                TAG,
                "Brick reached; starting face detection"
            )

            state = State.WAITING
            elapsed = 0f
            resultReady = false
            detectedName = FaceDetector.UNKNOWN

            if (beginDetection()) {
                state = State.DONE
                return true
            }

            return false
        }

        if (resultReady) {
            state = State.DONE

            Log.i(
                TAG,
                "Detection finished with '$detectedName'"
            )

            return true
        }

        elapsed += delta

        if (!FaceDetector.isRunning) {
            detectedName =
                FaceDetector.lastName.ifBlank {
                    FaceDetector.UNKNOWN
                }

            state = State.DONE
            return true
        }

        if (elapsed > TIMEOUT_SECONDS) {
            Log.w(
                TAG,
                "Detection timed out; continuing with Unknown"
            )

            detectedName = FaceDetector.UNKNOWN
            state = State.DONE

            return true
        }

        return false
    }

    /**
     * Returns true when detection has already ended;
     * false when the action must continue waiting.
     */
    @RequiresApi(Build.VERSION_CODES.N)
    private fun beginDetection(): Boolean {
        val context = applicationContext()

        if (context == null) {
            Log.e(
                TAG,
                "Application context is unavailable"
            )

            detectedName = FaceDetector.UNKNOWN
            return true
        }

        FaceDetector.resetForNewRun()

        val callback = FaceDetector.Callback { name, confidence ->
            detectedName = name
                ?.trim()
                ?.takeIf { it.isNotEmpty() }
                ?: FaceDetector.UNKNOWN

            Log.i(
                TAG,
                "Detected '$detectedName' with confidence $confidence"
            )

            resultReady = true
        }

        val started =
            FaceDetector.detectNow(
                context,
                callback
            )

        if (
            !started &&
            !FaceDetector.isRunning
        ) {
            detectedName =
                FaceDetector.lastName.ifBlank {
                    FaceDetector.UNKNOWN
                }

            return true
        }

        return false
    }

    private fun applicationContext(): Context? {
        return try {
            CatroidApplication.getAppContext()
        } catch (error: Throwable) {
            Log.e(
                TAG,
                "Could not obtain application context",
                error
            )

            null
        }
    }

    override fun restart() {
        super.restart()
        clearState()
    }

    override fun reset() {
        super.reset()
        clearState()
    }

    private fun clearState() {
        state = State.IDLE
        elapsed = 0f
        resultReady = false
        detectedName = FaceDetector.UNKNOWN
    }

    companion object {
        private const val TAG =
            "FaceNameDetectAction"

        private const val TIMEOUT_SECONDS =
            30f
    }
}
package org.catrobat.catroid.content.actions

import android.content.Context
import android.util.Log
import com.badlogic.gdx.scenes.scene2d.Action
import org.catrobat.catroid.CatroidApplication
import org.catrobat.catroid.FaceRecognizer.FaceDetector

/**
 * Detect the face, then let the next block run.
 *
 *     Ask "What's your name?" and store in rr
 *     Recognise Face                            <- blocks here
 *     Set variable rr to (face name detection)  <- fresh value
 *     Show variable rr
 *
 * act() returns false while the camera works, which is how a libGDX action says
 * "not finished, call me again next frame". Ask holds the sequence until it is
 * answered, so this cannot run before it.
 *
 * The state machine matters. Catroid may reuse this action object when the
 * Restart button is pressed rather than building a new one, so "already
 * finished" cannot be a permanent flag. Once DONE has been reported, any further
 * call means the sequence has come back round, and that is a new detection.
 */
class FaceNameDetectAction : Action() {

    companion object {
        private const val TAG = "FaceNameDetectAction"

        /** Never hang a child's program, whatever goes wrong. */
        private const val TIMEOUT_SECONDS = 30f
    }

    private enum class State {
        IDLE,       // not started, or finished and reported
        WAITING,    // camera is working
        DONE        // result in, true already returned
    }

    private var state = State.IDLE
    private var elapsed = 0f

    @Volatile
    private var resultReady = false

    /** The name this pass produced. Also written to the face name sensor. */
    var detectedName: String = FaceDetector.UNKNOWN
        private set

    override fun act(delta: Float): Boolean {
        if (state == State.DONE) {
            // We already returned true. Being called again means the sequence
            // came back to this brick, so start a fresh detection.
            Log.i(TAG, "Brick reached again, resetting for a new detection")
            state = State.IDLE
        }

        if (state == State.IDLE) {
            Log.i(TAG, "Brick reached, opening the camera")
            state = State.WAITING
            elapsed = 0f
            resultReady = false
            detectedName = FaceDetector.UNKNOWN

            if (begin()) {
                state = State.DONE
                return true
            }
            return false
        }

        // WAITING
        if (resultReady) {
            state = State.DONE
            Log.i(TAG, "Detection finished with '$detectedName', continuing the script")
            return true
        }

        elapsed += delta

        // The capture ended without calling us, which happens when another one
        // was already in flight when we started.
        if (!FaceDetector.isRunning()) {
            detectedName = FaceDetector.getLastName()
            state = State.DONE
            return true
        }

        if (elapsed > TIMEOUT_SECONDS) {
            Log.w(TAG, "Detection timed out, continuing with Unknown")
            detectedName = FaceDetector.UNKNOWN
            state = State.DONE
            return true
        }
        return false
    }

    /** Returns true when the work is already over, false while waiting. */
    private fun begin(): Boolean {
        val context: Context? = try {
            CatroidApplication.getAppContext()
        } catch (t: Throwable) {
            null
        }

        if (context == null) {
            Log.e(TAG, "No application context")
            detectedName = FaceDetector.UNKNOWN
            return true
        }

        // A brick means "look now". Never reuse a name from an earlier run, and
        // do not depend on anything else having called reset.
        FaceDetector.resetForNewRun()

        val started = FaceDetector.detectNow(context) { name, confidence ->
            Log.i(TAG, "Got '$name' at $confidence")
            detectedName = name
            resultReady = true
        }

        if (!started && !FaceDetector.isRunning()) {
            // Refused and nothing is in flight, so no callback is coming.
            detectedName = FaceDetector.getLastName()
            return true
        }
        return false
    }

    /** Called when a loop comes back to this brick. */
    override fun restart() {
        super.restart()
        clear()
    }

    override fun reset() {
        clear()
    }

    private fun clear() {
        state = State.IDLE
        elapsed = 0f
        resultReady = false
        detectedName = FaceDetector.UNKNOWN
    }
}
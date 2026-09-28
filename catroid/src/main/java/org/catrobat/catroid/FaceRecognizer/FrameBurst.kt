package org.catrobat.catroid.FaceRecognizer

import android.graphics.Bitmap
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.annotation.VisibleForTesting
import kotlin.math.max
import kotlin.math.min

/**
 * The recognition loop of one capture. Every captured frame goes through
 * [addFrame], which drops clipped frames, scores the frame into the session,
 * peeks at the session so far, and ends the burst early on a clear match or
 * after [FRAMES] frames with one decision over all usable frames.
 *
 * FaceDetector feeds it camera frames. The instrumented tests feed it fixture
 * photos, so both run this code; there is no copy of the loop in the tests.
 *
 * Not thread safe. FaceDetector uses one burst from its detector thread only.
 */
class FrameBurst internal constructor(private val recognizer: Recognizer) {

    private val session: Recognizer.Session = recognizer.newSession()

    /** Frames passed to [addFrame], usable or not. */
    var framesSeen: Int = 0
        private set

    var isFinished: Boolean = false
        private set

    /** The answer once [isFinished]; null means Unknown. */
    var result: Recognizer.Result? = null
        private set

    /**
     * Handles one captured frame. [frame] is null when the capture could not be
     * decoded; it still counts towards [FRAMES]. The frame is neither modified
     * nor recycled.
     *
     * Returns true when the burst has its answer in [result], false when the
     * caller should capture another frame.
     */
    @RequiresApi(Build.VERSION_CODES.N)
    fun addFrame(frame: Bitmap?, mirrorToo: Boolean): Boolean {
        if (isFinished) {
            return true
        }
        framesSeen++

        if (frame == null) {
            Log.i(TAG, "Frame $framesSeen could not be decoded")
        } else {
            if (isSeverelyOverexposed(frame)) {
                Log.i(TAG, "Frame $framesSeen rejected: highlights are clipped")
            } else {
                recognizer.addFrame(session, frame, mirrorToo)
            }

            val early = recognizer.peekSession(session)
            if (early != null && early.confidence > FaceDatabase.minSimilarity + EARLY_EXIT_MARGIN) {
                Log.i(TAG, "Clear match on frame $framesSeen, stopping early")
                return finishWith(early)
            }
        }

        if (framesSeen >= FRAMES) {
            return finishWith(recognizer.finishSession(session))
        }
        return false
    }

    /**
     * Decides with the frames that arrived so far. Used when the camera stops
     * delivering before the burst is complete, for example by the watchdog.
     */
    fun finish(): Recognizer.Result? {
        if (!isFinished) {
            finishWith(recognizer.finishSession(session))
        }
        return result
    }

    private fun finishWith(answer: Recognizer.Result?): Boolean {
        result = answer
        isFinished = true
        return true
    }

    companion object {
        private const val TAG = "FrameBurst"

        /** Frames to score before deciding. More frames, less noise. */
        const val FRAMES = 3

        /**
         * Stop the burst early when the session is already this far above the
         * accept threshold. Turns a typical detection from about two seconds into
         * under one, and still uses all three frames when the match is marginal.
         */
        const val EARLY_EXIT_MARGIN = 0.15f

        /**
         * Clipped white pixels contain no recoverable facial texture. Do not let an
         * overexposed 0-EV frame lower the session average; the following -1/-2 EV
         * bracket frames will retain the eyes, nose and skin texture.
         */
        @VisibleForTesting
        internal fun isSeverelyOverexposed(bitmap: Bitmap?): Boolean {
            if (bitmap == null || bitmap.isRecycled()) {
                return true
            }
            val left = bitmap.getWidth() / 4
            val top = bitmap.getHeight() / 4
            val right = bitmap.getWidth() * 3 / 4
            val bottom = bitmap.getHeight() * 3 / 4
            var sampled = 0
            var clipped = 0
            var luminanceSum = 0L
            val step = max(1, min(bitmap.getWidth(), bitmap.getHeight()) / 120)
            var y = top
            while (y < bottom) {
                var x = left
                while (x < right) {
                    val p = bitmap.getPixel(x, y)
                    val r = (p shr 16) and 0xff
                    val g = (p shr 8) and 0xff
                    val b = p and 0xff
                    val luma = (77 * r + 150 * g + 29 * b) shr 8
                    luminanceSum += luma.toLong()
                    if (r >= 250 && g >= 250 && b >= 250) {
                        clipped++
                    }
                    sampled++
                    x += step
                }
                y += step
            }
            if (sampled == 0) {
                return false
            }
            val clippedRatio = clipped.toFloat() / sampled
            val meanLuma = luminanceSum.toFloat() / sampled
            return clippedRatio > 0.45f || meanLuma > 238f
        }
    }
}

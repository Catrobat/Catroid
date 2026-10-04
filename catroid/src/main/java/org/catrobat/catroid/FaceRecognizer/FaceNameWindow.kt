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
package org.catrobat.catroid.FaceRecognizer

import android.graphics.Bitmap
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.VisibleForTesting
import kotlin.math.max
import kotlin.math.min

/**
 * The decision behind "detected face name": a sliding window of the last
 * [FRAMES] analysed frames that contained a usable face.
 *
 * A frame with a usable face enters the window, the oldest one leaving when the
 * window is full, and the name is decided again: the window's scores are
 * averaged and the recogniser's thresholds applied, with the stricter
 * single-frame threshold while the window holds one frame and the single-person
 * rule when one person is enrolled. A frame without a usable face changes
 * nothing, until no usable face was seen for [NO_FACE_RESET_MS]; then the
 * window is cleared and the name is [UNKNOWN].
 *
 * The window is also the throttle: [isFrameDue] lets one frame through every
 * [FRAME_INTERVAL_MS].
 *
 * It replaces FrameBurst, the fixed three-frame burst of the removed one-shot capture.
 */
class FaceNameWindow {

    private val frames = ArrayDeque<FloatArray>(FRAMES)

    /** The enrolled people the frames in the window were scored against, in score order. */
    private var framesScoredFor: List<String> = emptyList()

    private var lastFrameAt: Long? = null
    private var lastUsableFaceAt: Long? = null

    /** The latest decision; [UNKNOWN] until the first one. */
    @get:Synchronized
    var name: String = UNKNOWN
        private set

    @get:Synchronized
    val framesInWindow: Int
        get() = frames.size

    /**
     * True when at least [FRAME_INTERVAL_MS] passed since the last frame this
     * returned true for. A true answer counts that frame as analysed.
     */
    @Synchronized
    fun isFrameDue(nowMs: Long): Boolean {
        val last = lastFrameAt
        if (last != null && nowMs - last < FRAME_INTERVAL_MS) {
            return false
        }
        lastFrameAt = nowMs
        return true
    }

    /**
     * Scores [frame] and adds it, see [addScores]. [frame] is null, or rejected
     * here, when it holds no usable face. The frame is not modified or recycled.
     */
    @RequiresApi(Build.VERSION_CODES.N)
    fun addFrame(recognizer: Recognizer, frame: Bitmap?, mirrorToo: Boolean, nowMs: Long): String {
        val scored = scoreOf(recognizer, frame, mirrorToo)
        return addScores(recognizer, scored?.scores, nowMs, scored?.names)
    }

    /**
     * Adds one analysed frame: [scores] has one score per enrolled person, in
     * [Recognizer.classNames] order, or is null when the frame held no usable
     * face. Returns the name, which is also [name].
     */
    @Synchronized
    fun addScores(
        recognizer: Recognizer,
        scores: FloatArray?,
        nowMs: Long,
        scoredFor: List<String>? = null
    ): String {
        if (scores == null) {
            val last = lastUsableFaceAt
            if (last == null || nowMs - last >= NO_FACE_RESET_MS) {
                clearFrames()
                name = UNKNOWN
            }
            return name
        }

        val people = recognizer.classNames
        if (scores.size != people.size || (scoredFor != null && scoredFor != people)) {
            // Someone was added or deleted while this frame was scored.
            return name
        }
        if (people != framesScoredFor) {
            // Older frames were scored against other people; their scores do not line up.
            clearFrames()
            framesScoredFor = people
        }
        if (frames.size == FRAMES) {
            frames.removeFirst()
        }
        frames.addLast(scores.copyOf())
        lastUsableFaceAt = nowMs

        name = decide(recognizer)
        return name
    }

    /** Back to the start of a program run: no frames, [UNKNOWN], the next frame due at once. */
    @Synchronized
    fun clear() {
        clearFrames()
        lastFrameAt = null
        name = UNKNOWN
    }

    private fun clearFrames() {
        frames.clear()
        framesScoredFor = emptyList()
        lastUsableFaceAt = null
    }

    private fun decide(recognizer: Recognizer): String {
        val totals = FloatArray(framesScoredFor.size)
        for (scores in frames) {
            for (index in totals.indices) {
                totals[index] += scores[index]
            }
        }
        // A person without a score in any frame (no usable photo then) has none
        // in the average either; mixed with real scores it would look like a
        // weak rival and lower the bar for everyone else.
        for (index in totals.indices) {
            if (frames.any { it[index] == FaceDatabase.NO_SCORE }) {
                totals[index] = FaceDatabase.NO_SCORE * frames.size
            }
        }
        val session = recognizer.newSession().apply {
            this.totals = totals
            framesWithFace = frames.size
            framesTried = frames.size
        }
        return recognizer.finishSession(session)?.name ?: UNKNOWN
    }

    companion object {
        const val UNKNOWN: String = "Unknown"

        /** Usable frames whose scores are averaged. More frames, less noise. */
        const val FRAMES = 3

        /** At most one analysed frame per this many milliseconds. */
        const val FRAME_INTERVAL_MS = 300L

        /** Without a usable face for this long, the name goes back to Unknown. */
        const val NO_FACE_RESET_MS = 1000L

        /**
         * The scores [addScores] takes for [frame]; null when it holds no usable
         * face: none at all, or clipped highlights.
         */
        @RequiresApi(Build.VERSION_CODES.N)
        fun scoreOf(recognizer: Recognizer, frame: Bitmap?, mirrorToo: Boolean): Recognizer.ScoredFrame? {
            if (frame == null || recognizer.classNames.isEmpty() || isSeverelyOverexposed(frame)) {
                return null
            }
            return recognizer.scoreFrameForNames(frame, mirrorToo)
        }

        /**
         * Clipped white pixels contain no recoverable facial texture, so such a
         * frame must not lower the window's average.
         */
        @VisibleForTesting
        internal fun isSeverelyOverexposed(bitmap: Bitmap?): Boolean {
            if (bitmap == null || bitmap.isRecycled) {
                return true
            }
            val left = bitmap.width / 4
            val top = bitmap.height / 4
            val right = bitmap.width * 3 / 4
            val bottom = bitmap.height * 3 / 4
            var sampled = 0
            var clipped = 0
            var luminanceSum = 0L
            val step = max(1, min(bitmap.width, bitmap.height) / 120)
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

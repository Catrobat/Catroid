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

import androidx.camera.core.ImageProxy
import io.mockk.mockk
import io.mockk.verify
import org.junit.Test
import java.util.concurrent.CountDownLatch
import kotlin.concurrent.thread

/**
 * The ML Kit detectors report a finished frame on the main thread, the face
 * name detector on the camera analysis thread. A completion lost between the
 * two left the frame open, and CameraX sends no new frame until it is closed:
 * every camera sensor stopped for the rest of the program.
 */
class DetectorsCompleteListenerTest {

    @Test
    fun completionsFromSeveralThreadsCloseTheFrameExactlyOnce() {
        repeat(ROUNDS) {
            val frame = mockk<ImageProxy>(relaxed = true)
            val listener = DetectorsCompleteListener(THREADS * CALLS_PER_THREAD, frame)
            val start = CountDownLatch(1)
            val workers = List(THREADS) {
                thread {
                    start.await()
                    repeat(CALLS_PER_THREAD) { listener.onComplete() }
                }
            }
            start.countDown()
            workers.forEach { it.join() }

            verify(exactly = 1) { frame.close() }
        }
    }

    private companion object {
        const val ROUNDS = 10
        const val THREADS = 4
        const val CALLS_PER_THREAD = 2_000_000
    }
}

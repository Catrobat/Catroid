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
package org.catrobat.catroid.facerecognition

import android.graphics.Bitmap
import android.graphics.Rect
import io.mockk.every
import io.mockk.mockk
import org.catrobat.catroid.facerecognition.ml.BlazeFace
import org.catrobat.catroid.facerecognition.ml.MobileFaceNet
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * Found in review: an embedding is normalised to unit length and dropped when
 * its length is zero, but a length of NaN passed that check, and a broken
 * embedding could be stored as a person's photo.
 */
@RunWith(RobolectricTestRunner::class)
class FaceEmbeddingValidityTest {

    private val model = mockk<MobileFaceNet>(relaxed = true)
    private val frame = Bitmap.createBitmap(FRAME_SIZE, FRAME_SIZE, Bitmap.Config.ARGB_8888)

    @Test
    fun anEmbeddingThatIsNotANumberIsDropped() {
        every { model.embed(any()) } answers { firstArg<List<Bitmap>>().map { FloatArray(EMBEDDING_SIZE) { Float.NaN } } }

        val views = embedder().embedVariants(
            frame = frame,
            box = Rect(0, 0, FRAME_SIZE, FRAME_SIZE),
            includeMirror = true,
            leftEye = null,
            rightEye = null
        )

        assertEquals(emptyList<FloatArray>(), views.toList())
    }

    private fun embedder(): FaceEmbedder =
        FaceEmbedder::class.java
            .getDeclaredConstructor(BlazeFace::class.java, MobileFaceNet::class.java)
            .apply { isAccessible = true }
            .newInstance(mockk<BlazeFace>(relaxed = true), model)

    private companion object {
        const val FRAME_SIZE = 224
        const val EMBEDDING_SIZE = 128
    }
}

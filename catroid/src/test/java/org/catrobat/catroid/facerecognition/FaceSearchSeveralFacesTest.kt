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
import android.graphics.RectF
import io.mockk.every
import io.mockk.mockk
import org.catrobat.catroid.facerecognition.ml.BlazeFace
import org.catrobat.catroid.facerecognition.ml.BlazeFace.FaceBox
import org.catrobat.catroid.facerecognition.ml.MobileFaceNet
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * Found in review: training stored the largest face of each photo under the
 * typed name. In a photo of two people that can be the other person, who is
 * then recognised under this name. A training photo with several similar-sized
 * faces is skipped; a small face far in the background does not matter.
 */
@RunWith(RobolectricTestRunner::class)
class FaceSearchSeveralFacesTest {

    private val blazeFace = mockk<BlazeFace>(relaxed = true)
    private val photo = Bitmap.createBitmap(PHOTO_SIZE, PHOTO_SIZE, Bitmap.Config.ARGB_8888)

    @Test
    fun aTrainingPhotoWithTwoPeopleIsSkipped() {
        detects(face(left = 10f, size = 40f), face(left = 70f, size = 36f))

        assertNull(embedder().findBestFace(photo))
    }

    @Test
    fun aSmallFaceInTheBackgroundDoesNotMatter() {
        detects(face(left = 10f, size = 60f), face(left = 100f, size = 12f))

        assertNotNull(embedder().findBestFace(photo))
    }

    @Test
    fun aCameraFrameWithTwoPeopleStillUsesTheLargestFace() {
        detects(face(left = 10f, size = 40f), face(left = 70f, size = 36f))

        assertNotNull(embedder().findBestFace(photo, searchRotations = false))
    }

    private fun detects(vararg faces: FaceBox) {
        every { blazeFace.detectWithLandmarks(any()) } returns faces.toList()
    }

    /** A square face at [left], 30 px from the top, in BlazeFace's 128 px input space. */
    private fun face(left: Float, size: Float): FaceBox {
        val top = 30f
        val eyeY = top + size * EYE_HEIGHT
        return FaceBox(
            RectF(left, top, left + size, top + size),
            floatArrayOf(
                left + size * RIGHT_EYE_X, eyeY, left + size * LEFT_EYE_X, eyeY,
                0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f
            )
        )
    }

    private fun embedder(): FaceEmbedder =
        FaceEmbedder::class.java
            .getDeclaredConstructor(BlazeFace::class.java, MobileFaceNet::class.java)
            .apply { isAccessible = true }
            .newInstance(blazeFace, mockk<MobileFaceNet>(relaxed = true))

    private companion object {
        const val PHOTO_SIZE = 512
        const val EYE_HEIGHT = 0.4f
        const val RIGHT_EYE_X = 0.3f
        const val LEFT_EYE_X = 0.7f
    }
}

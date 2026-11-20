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

import android.content.Context
import android.graphics.Bitmap
import android.graphics.RectF
import androidx.test.core.app.ApplicationProvider
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.catrobat.catroid.facerecognition.env.FileUtils
import org.catrobat.catroid.facerecognition.ml.BlazeFace
import org.catrobat.catroid.facerecognition.ml.MobileFaceNet
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * Gallery photos can carry a wrong or missing EXIF orientation, so a photo is
 * searched at 0, 90, 180 and 270 degrees. Found in review: every live camera
 * frame was searched the same way, although FaceNameDetector has already
 * turned it upright. Without a face in view, each analysed frame ran face
 * detection four times and made three rotated copies.
 */
@RunWith(RobolectricTestRunner::class)
class FaceSearchRotationTest {

    private lateinit var context: Context
    private val blazeFace = mockk<BlazeFace>(relaxed = true)
    private val frame = Bitmap.createBitmap(FRAME_SIZE, FRAME_SIZE, Bitmap.Config.ARGB_8888)

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        FileUtils.init(context)
        FileUtils.deleteAll()
        every { blazeFace.detectWithLandmarks(any()) } returns emptyList()
    }

    @After
    fun tearDown() {
        FileUtils.deleteAll()
    }

    @Test
    fun aCameraFrameIsSearchedUprightOnly() {
        val recognizer = recognizerWith(embedder())
        recognizer.addPerson("Ada")

        assertNull(recognizer.scoreFrameForNames(frame, mirrorToo = true))

        verify(exactly = 1) { blazeFace.detectWithLandmarks(any()) }
    }

    @Test
    fun aGalleryPhotoIsStillSearchedAtEveryRotation() {
        assertNull(embedder().findBestFace(frame))

        verify(exactly = ROTATIONS) { blazeFace.detectWithLandmarks(any()) }
    }

    /**
     * SonarCloud reports the check for "no face at this rotation" as always
     * true. It is not: without it, a photo with no upright face would return an
     * empty face at once instead of trying the other rotations.
     */
    @Test
    fun aFaceFoundOnlyAfterRotatingIsStillFound() {
        val sideways = BlazeFace.FaceBox(
            RectF(FACE_LEFT, FACE_LEFT, FACE_LEFT + FACE_SIZE, FACE_LEFT + FACE_SIZE),
            floatArrayOf(
                FACE_LEFT + FACE_SIZE * RIGHT_EYE_X, FACE_LEFT + FACE_SIZE * EYE_HEIGHT,
                FACE_LEFT + FACE_SIZE * LEFT_EYE_X, FACE_LEFT + FACE_SIZE * EYE_HEIGHT,
                0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f
            )
        )
        every { blazeFace.detectWithLandmarks(any()) } returnsMany listOf(emptyList(), listOf(sideways))

        val face = embedder().findBestFace(frame)

        assertNotNull("The face at 90 degrees was not found", face)
        assertNotNull(face!!.box)
        assertEquals(QUARTER_TURN, face.rotation)
    }

    private fun embedder(): FaceEmbedder =
        FaceEmbedder::class.java
            .getDeclaredConstructor(BlazeFace::class.java, MobileFaceNet::class.java)
            .apply { isAccessible = true }
            .newInstance(blazeFace, mockk<MobileFaceNet>(relaxed = true))

    private fun recognizerWith(embedder: FaceEmbedder): Recognizer =
        Recognizer.withoutModelsForTest(context).also {
            Recognizer::class.java.getDeclaredField("embedder").apply { isAccessible = true }.set(it, embedder)
        }

    private companion object {
        const val FRAME_SIZE = 256
        const val ROTATIONS = 4
        const val QUARTER_TURN = 90
        const val FACE_LEFT = 20f
        const val FACE_SIZE = 60f
        const val EYE_HEIGHT = 0.4f
        const val RIGHT_EYE_X = 0.3f
        const val LEFT_EYE_X = 0.7f
    }
}

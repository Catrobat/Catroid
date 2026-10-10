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
import android.graphics.Color
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.After
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Found in review: when BlazeFace found no face, it kept the faces of the image
 * before, so detectWithLandmarks() returned that image's boxes and landmarks.
 * FaceEmbedder then accepted a frame without a face, at a wrong rotation, and
 * cropped the old position. Uses the real face models.
 */
@RunWith(AndroidJUnit4::class)
class FaceDetectionCacheTest {

    private val harness = FaceRecognitionHarness()
    private lateinit var embedder: FaceEmbedder

    @Before
    fun setUp() {
        harness.start()
        embedder = FaceEmbedder.create(harness.appContext.assets)
    }

    @After
    fun tearDown() {
        embedder.close()
        harness.stop()
    }

    @Test
    fun anImageWithoutAFaceAfterOneWithAFaceHasNoFace() {
        val photo = harness.fixtureBitmap("p02_test.jpg")
        val face = embedder.findBestFace(photo)
        assertNotNull("The photo must contain a face", face)
        face!!.release(photo)

        val blank = Bitmap.createBitmap(BLANK_WIDTH, BLANK_HEIGHT, Bitmap.Config.ARGB_8888).apply {
            eraseColor(Color.GRAY)
        }

        assertNull(
            "A plain grey image must have no face, not the face of the image before",
            embedder.findBestFace(blank)
        )
    }

    @Test
    fun anImageWithoutAFaceOnItsOwnHasNoFace() {
        val blank = Bitmap.createBitmap(BLANK_WIDTH, BLANK_HEIGHT, Bitmap.Config.ARGB_8888).apply {
            eraseColor(Color.GRAY)
        }

        assertNull(embedder.findBestFace(blank))
    }

    private companion object {
        const val BLANK_WIDTH = 640
        const val BLANK_HEIGHT = 480
    }
}

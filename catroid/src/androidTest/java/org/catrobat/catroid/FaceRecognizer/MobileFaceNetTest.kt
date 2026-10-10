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
import android.graphics.BitmapFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.catrobat.catroid.FaceRecognizer.ml.MobileFaceNet
import org.junit.After
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.math.sqrt

/** The MobileFaceNet model in the app, on the real face fixtures (faces.zip). */
@RunWith(AndroidJUnit4::class)
class MobileFaceNetTest {

    private val instrumentation = InstrumentationRegistry.getInstrumentation()
    private val appContext = instrumentation.targetContext
    private val testAssets = instrumentation.context.assets
    private val bitmaps = mutableListOf<Bitmap>()

    private lateinit var model: MobileFaceNet

    @Before
    fun setUp() {
        model = MobileFaceNet.create(appContext.assets)
    }

    @After
    fun tearDown() {
        model.close()
        bitmaps.forEach { it.recycle() }
    }

    @Test
    fun theAppShipsMobileFaceNetInsteadOfFaceNet() {
        val assets = appContext.assets.list("").orEmpty().toList()

        assertTrue(assets.toString(), "mobile_facenet.tflite" in assets)
        assertFalse(assets.toString(), "facenet.tflite" in assets)
    }

    @Test
    fun everyFaceGets128Values() {
        val embeddings = model.embed(listOf(input("p01_test.jpg"), input("p02_test.jpg"), input("p03_test.jpg")))

        assertEquals(3, embeddings.size)
        embeddings.forEach { assertEquals(MobileFaceNet.EMBEDDING_SIZE, it.size) }
    }

    /** img1 and img2 are separate faces: a face scores the same in either slot and alone. */
    @Test
    fun twoFacesPerRunGiveTheSameEmbeddingsAsOneFacePerRun() {
        val first = input("p01_test.jpg")
        val second = input("p02_test.jpg")

        val together = model.embed(listOf(first, second))
        val swapped = model.embed(listOf(second, first))
        val firstAlone = model.embed(listOf(first)).single()
        val secondAlone = model.embed(listOf(second)).single()

        assertArrayEquals(firstAlone, together[0], TOLERANCE)
        assertArrayEquals(secondAlone, together[1], TOLERANCE)
        assertArrayEquals(firstAlone, swapped[1], TOLERANCE)
        assertArrayEquals(secondAlone, swapped[0], TOLERANCE)
    }

    /** Three eye distances, each also mirrored, as training and the front camera use them. */
    @Test
    fun theEmbedderGivesSixUnitLengthViewsOfAFace() {
        val embedder = FaceEmbedder.create(appContext.assets)
        try {
            val views = embedder.embedAllVariants(photo("p01_train1.jpg"), true)

            assertEquals(embedder.lastProblem, 6, views.size)
            views.forEach {
                assertEquals(MobileFaceNet.EMBEDDING_SIZE, it.size)
                assertEquals(1f, length(it), 0.0001f)
            }
            assertNotNull(embedder.embedBestFace(photo("p01_train1.jpg")))
        } finally {
            embedder.close()
        }
    }

    private fun photo(name: String): Bitmap =
        testAssets.open("faces/$name").use { BitmapFactory.decodeStream(it) }.also { bitmaps.add(it) }

    private fun input(name: String): Bitmap =
        Bitmap.createScaledBitmap(photo(name), MobileFaceNet.INPUT_SIZE, MobileFaceNet.INPUT_SIZE, true)
            .also { bitmaps.add(it) }

    private fun length(vector: FloatArray): Float = sqrt(vector.sumOf { it.toDouble() * it }).toFloat()

    private companion object {
        const val TOLERANCE = 1e-4f
    }
}

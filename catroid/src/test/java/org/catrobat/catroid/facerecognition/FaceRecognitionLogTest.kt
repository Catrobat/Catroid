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
import android.net.Uri
import android.util.Log
import androidx.test.core.app.ApplicationProvider
import io.mockk.every
import io.mockk.mockk
import org.catrobat.catroid.facerecognition.env.FileUtils
import org.catrobat.catroid.facerecognition.ml.BlazeFace
import org.catrobat.catroid.facerecognition.ml.MobileFaceNet
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.shadows.ShadowLog
import kotlin.random.Random

/**
 * Found in review: every analysed camera frame, about three a second, was
 * logged at INFO level with the enrolled names and their scores, and the
 * release build keeps INFO logs. Per-frame details are debug logs.
 */
@RunWith(RobolectricTestRunner::class)
class FaceRecognitionLogTest {

    private lateinit var context: Context
    private val blazeFace = mockk<BlazeFace>(relaxed = true)
    private val model = mockk<MobileFaceNet>(relaxed = true)
    private val frame = Bitmap.createBitmap(FRAME_SIZE, FRAME_SIZE, Bitmap.Config.ARGB_8888)

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        FileUtils.init(context)
        FileUtils.deleteAll()
        // A face with visible detail, so the quality checks let it through.
        val random = Random(SEED)
        frame.setPixels(
            IntArray(FRAME_SIZE * FRAME_SIZE) { random.nextInt() or OPAQUE }, 0, FRAME_SIZE,
            0, 0, FRAME_SIZE, FRAME_SIZE
        )
        every { model.embed(any()) } answers {
            firstArg<List<Bitmap>>().map { FloatArray(EMBEDDING_SIZE) { if (it == 0) 1f else 0f } }
        }
    }

    @After
    fun tearDown() {
        FileUtils.deleteAll()
    }

    @Test
    fun aScoredFrameDoesNotLogTheNamesAtInfoLevel() {
        every { blazeFace.detectWithLandmarks(any()) } returns listOf(face())
        val recognizer = recognizer()
        recognizer.addPhotos("Ada", listOf(FloatArray(EMBEDDING_SIZE) { if (it == 0) 1f else 0f }))
        ShadowLog.clear()

        assertNotNull(recognizer.scoreFrameForNames(frame, mirrorToo = true))

        assertEquals(emptyList<String>(), infoLogsMentioning("Ada"))
    }

    @Test
    fun aFrameWithoutAFaceIsNotLoggedAtInfoLevel() {
        every { blazeFace.detectWithLandmarks(any()) } returns emptyList()
        val recognizer = recognizer()
        recognizer.addPerson("Ada")
        ShadowLog.clear()

        recognizer.scoreFrameForNames(frame, mirrorToo = true)

        assertEquals(emptyList<String>(), infoLogsMentioning(""))
    }

    /** Found in review: photos from the FaceNet build were reported with the names, at ERROR level. */
    @Test
    fun outdatedPhotosAreReportedWithoutTheNames() {
        FileUtils.writeLines(FileUtils.LABEL_FILE, listOf("Ada"))
        FileUtils.writeLines(FileUtils.DATA_FILE, listOf("0 " + List(FACENET_SIZE) { "0.1" }.joinToString(" ")))
        ShadowLog.clear()

        Recognizer.withoutModelsForTest(context)

        assertEquals(emptyList<String>(), infoLogsMentioning("Ada"))
    }

    /** Found in a review of this feature: the address of a photo that could not be read was logged. */
    @Test
    fun aPhotoThatCannotBeReadIsLoggedWithoutItsAddress() {
        val recognizer = recognizer()
        ShadowLog.clear()

        recognizer.extractEmbeddings(context.contentResolver, listOf(Uri.parse("content://photos/holiday-at-grandma")))

        assertEquals(emptyList<String>(), infoLogsMentioning("holiday-at-grandma"))
    }

    /** Messages logged at INFO level or above that contain [text]. */
    private fun infoLogsMentioning(text: String): List<String> =
        ShadowLog.getLogs()
            .filter { it.type >= Log.INFO && it.msg.contains(text) }
            .map { "${it.tag}: ${it.msg}" }

    private fun face(): BlazeFace.FaceBox =
        BlazeFace.FaceBox(
            RectF(FACE_LEFT, FACE_LEFT, FACE_LEFT + FACE_SIZE, FACE_LEFT + FACE_SIZE),
            floatArrayOf(
                FACE_LEFT + FACE_SIZE * RIGHT_EYE_X, FACE_LEFT + FACE_SIZE * EYE_HEIGHT,
                FACE_LEFT + FACE_SIZE * LEFT_EYE_X, FACE_LEFT + FACE_SIZE * EYE_HEIGHT,
                0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f
            )
        )

    private fun recognizer(): Recognizer {
        val embedder = FaceEmbedder::class.java
            .getDeclaredConstructor(BlazeFace::class.java, MobileFaceNet::class.java)
            .apply { isAccessible = true }
            .newInstance(blazeFace, model)
        return Recognizer.withoutModelsForTest(context).also {
            Recognizer::class.java.getDeclaredField("embedder").apply { isAccessible = true }.set(it, embedder)
        }
    }

    private companion object {
        const val FRAME_SIZE = 512
        const val EMBEDDING_SIZE = 128
        const val FACENET_SIZE = 512
        const val SEED = 7
        const val OPAQUE = -0x1000000
        const val FACE_LEFT = 30f
        const val FACE_SIZE = 60f
        const val EYE_HEIGHT = 0.4f
        const val RIGHT_EYE_X = 0.3f
        const val LEFT_EYE_X = 0.7f
    }
}

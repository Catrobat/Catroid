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
import androidx.test.core.app.ApplicationProvider
import io.mockk.every
import io.mockk.mockk
import org.catrobat.catroid.facerecognition.env.FileUtils
import org.catrobat.catroid.facerecognition.ml.BlazeFace
import org.catrobat.catroid.facerecognition.ml.MobileFaceNet
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * The face models are released in the background when the user leaves the
 * stage. Found on the phone: a program that already held the recogniser lost
 * its models when the release ran late (Android destroys a left stage after
 * the next one may have started): face names stayed Unknown and training
 * failed. Releasing frees the models only; the recogniser loads them again
 * when it needs them.
 */
@RunWith(RobolectricTestRunner::class)
class RecognizerReleaseTest {

    private lateinit var context: Context
    private val savedFactory = Recognizer.embedderFactory
    private var loads = 0

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        FileUtils.init(context)
        FileUtils.deleteAll()
        Recognizer.release()
        Recognizer.embedderFactory = {
            loads++
            val blazeFace = mockk<BlazeFace>(relaxed = true)
            every { blazeFace.detectWithLandmarks(any()) } returns emptyList()
            FaceEmbedder::class.java
                .getDeclaredConstructor(BlazeFace::class.java, MobileFaceNet::class.java)
                .apply { isAccessible = true }
                .newInstance(blazeFace, mockk<MobileFaceNet>(relaxed = true))
        }
    }

    @After
    fun tearDown() {
        Recognizer.release()
        Recognizer.embedderFactory = savedFactory
        FileUtils.deleteAll()
    }

    @Test
    fun aRecogniserHeldAcrossAReleaseLoadsItsModelsAgain() {
        val held = Recognizer.getInstance(context)
        assertEquals(1, loads)

        Recognizer.releaseModels()
        held.scoreFrameForNames(Bitmap.createBitmap(FRAME_SIZE, FRAME_SIZE, Bitmap.Config.ARGB_8888), mirrorToo = false)

        assertEquals("The models were not loaded again", 2, loads)
        assertSame("Everyone keeps the same recogniser", held, Recognizer.getInstance(context))
    }

    private companion object {
        const val FRAME_SIZE = 128
    }
}

/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2023 The Catrobat Team
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

package org.catrobat.catroid.test.sensing

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.catrobat.catroid.camera.mlkitdetectors.FaceDetector
import org.catrobat.catroid.common.Constants.CACHE_DIRECTORY
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.nio.file.Files

@RunWith(AndroidJUnit4::class)
class FaceDetectorTest {

    private val faceMale = File(CACHE_DIRECTORY, "face_male.jpg")
    private val faceFemale = File(CACHE_DIRECTORY, "face_female.jpg")
    private val cat = File(CACHE_DIRECTORY, "cat.jpg")
    private val dog = File(CACHE_DIRECTORY, "dog.jpg")
    private val bird = File(CACHE_DIRECTORY, "bird.jpg")
    private val textFile = File(CACHE_DIRECTORY, "not_an_image.png")
    private val timeoutLimitMS = 5_000

    @Before
    fun setup() {
        cleanDirectory(CACHE_DIRECTORY)
        CACHE_DIRECTORY.mkdirs()

        Files.copy(
            InstrumentationRegistry.getInstrumentation().context.assets.open(faceMale.name),
            faceMale.toPath()
        )

        Files.copy(
            InstrumentationRegistry.getInstrumentation().context.assets.open(faceFemale.name),
            faceFemale.toPath()
        )

        Files.copy(
            InstrumentationRegistry.getInstrumentation().context.assets.open(cat.name),
            cat.toPath()
        )

        Files.copy(
            InstrumentationRegistry.getInstrumentation().context.assets.open(dog.name),
            dog.toPath()
        )

        Files.copy(
            InstrumentationRegistry.getInstrumentation().context.assets.open(bird.name),
            bird.toPath()
        )

        Files.copy(
            InstrumentationRegistry.getInstrumentation().context.assets.open(textFile.name),
            textFile.toPath()
        )
    }

    @After
    fun cleanup() {
        cleanDirectory(CACHE_DIRECTORY)
    }

    @Test
    fun testSingleImageNoFaceFound() {
        var returnValue = true
        val images: ArrayList<String> = ArrayList()
        images.add(cat.toString())

        FaceDetector.analyzeProjectImages(images, { b -> returnValue = b }, 0)

        var timeMS = 0
        while (true) {
            timeMS += 50
            Thread.sleep(50)
            if (!returnValue || timeMS == timeoutLimitMS) {
                assertTrue(timeMS < timeoutLimitMS)
                assertThat(returnValue, `is`(false))
                break
            }
        }
    }

    @Test
    fun testMultipleImagesNoFaceFound() {
        var returnValue = true
        val images: ArrayList<String> = ArrayList()
        images.add(cat.toString())
        images.add(dog.toString())
        images.add(bird.toString())

        FaceDetector.analyzeProjectImages(images, { b -> returnValue = b }, 0)

        var timeMS = 0
        while (true) {
            timeMS += 50
            Thread.sleep(50)
            if (!returnValue || timeMS == timeoutLimitMS) {
                assertTrue(timeMS < timeoutLimitMS)
                assertThat(returnValue, `is`(false))
                break
            }
        }
    }

    @Test
    fun testSingleImageFaceFound() {
        var returnValue = false
        val images: ArrayList<String> = ArrayList()
        images.add(faceFemale.toString())

        FaceDetector.analyzeProjectImages(images, { b -> returnValue = b }, 0)

        var timeMS = 0
        while (true) {
            timeMS += 50
            Thread.sleep(50)
            if (returnValue || timeMS == timeoutLimitMS) {
                assertTrue(timeMS < timeoutLimitMS)
                assertThat(returnValue, `is`(true))
                break
            }
        }
    }

    @Test
    fun testMultipleImagesFaceFound() {
        var returnValue = false
        val images: ArrayList<String> = ArrayList()
        images.add(cat.toString())
        images.add(dog.toString())
        images.add(bird.toString())
        images.add(faceMale.toString())

        FaceDetector.analyzeProjectImages(images, { b -> returnValue = b }, 0)

        var timeMS = 0
        while (true) {
            timeMS += 50
            Thread.sleep(50)
            if (returnValue || timeMS == timeoutLimitMS) {
                assertTrue(timeMS < timeoutLimitMS)
                assertThat(returnValue, `is`(true))
                break
            }
        }
    }

    @Test
    fun testFaceDetectorBitmapIsNull() {
        var returnValue = true
        val images: ArrayList<String> = ArrayList()
        images.add(textFile.toString())
        images.add(textFile.toString())

        FaceDetector.analyzeProjectImages(images, { b -> returnValue = b }, 0)

        var timeMS = 0
        while (true) {
            timeMS += 50
            Thread.sleep(50)
            if (!returnValue || timeMS == timeoutLimitMS) {
                assertTrue(timeMS < timeoutLimitMS)
                assertThat(returnValue, `is`(false))
                break
            }
        }
    }

    private fun cleanDirectory(directory: File) {
        val directoryFiles = directory.listFiles()
        if (directoryFiles != null) {
            for (i in directoryFiles.indices) {
                if (directoryFiles[i].isDirectory) {
                    cleanDirectory(directoryFiles[i])
                } else {
                    directoryFiles[i].delete()
                }
            }
        } else {
            directory.delete()
        }
    }
}

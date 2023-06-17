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

package org.catrobat.catroid.test.utiltests

import org.catrobat.catroid.common.Constants
import org.catrobat.catroid.common.Constants.CACHE_DIRECTORY
import org.catrobat.catroid.utils.ProjectImages
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.File
import java.util.ArrayList

class ProjectImagesTest {

    private val testImages: ArrayList<String> = ArrayList()
    private val emptyDir = File(CACHE_DIRECTORY.path + "/empty")
    private val imageDir = File(CACHE_DIRECTORY.path + "/images")
    private val imageOne = File(CACHE_DIRECTORY.path + "/images/image1.png")
    private val imageTwo = File(CACHE_DIRECTORY.path + "/images/image2.png")

    @Before
    fun setUp() {
        cleanDirectory(CACHE_DIRECTORY)
        CACHE_DIRECTORY.mkdirs()

        emptyDir.mkdir()
        imageDir.mkdir()
        imageOne.createNewFile()
        imageTwo.createNewFile()

        testImages.add("image.jpg")
        testImages.add("image.JPG")
        testImages.add("image.jpeg")
        testImages.add("image.JPEG")
        testImages.add("image.png")
        testImages.add("image.PNG")
        testImages.add("image.bmp")
        testImages.add("image.BMP")
    }

    @After
    fun cleanup() {
        cleanDirectory(CACHE_DIRECTORY)
    }

    @Test
    fun testIsImage() {
        for (i in testImages.indices) {
            assertTrue(ProjectImages.isImage(testImages[i]))
        }
    }

    @Test
    fun testIsImageAutomaticScreenshot() {
        assertFalse(ProjectImages.isImage(Constants.SCREENSHOT_AUTOMATIC_FILE_NAME))
    }

    @Test
    fun testGetImagePathsFromDirectory() {
        var imagePaths: ArrayList<String> = ArrayList()
        imagePaths = ProjectImages.getImagePathsFromDirectory(imagePaths, CACHE_DIRECTORY)

        assertEquals(2, imagePaths.size)
        assertTrue(imagePaths[0].endsWith("/image1.png"))
        assertTrue(imagePaths[1].endsWith("/image2.png"))
    }

    @Test
    fun testGetImagePathsFromEmptyDirectory() {
        var imagePaths: ArrayList<String> = ArrayList()
        val emptyDirectory = File(CACHE_DIRECTORY.path + "/empty")
        imagePaths = ProjectImages.getImagePathsFromDirectory(imagePaths, emptyDirectory)

        assertEquals(0, imagePaths.size)
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

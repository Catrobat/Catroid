/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2022 The Catrobat Team
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

import android.content.Context
import androidx.exifinterface.media.ExifInterface
import androidx.test.core.app.ApplicationProvider
import androidx.test.platform.app.InstrumentationRegistry
import org.catrobat.catroid.common.Constants
import org.catrobat.catroid.utils.Utils
import org.catrobat.paintroid.FileIO
import org.junit.After
import org.junit.Before
import org.junit.Test

import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

import org.junit.Assert.assertTrue
import org.junit.Assert.assertFalse
import org.junit.Assert.assertEquals

class ExifDataTest {

    companion object {
        private const val IMAGE_NAME = "Exif.jpg"
        private val CACHE_FOLDER = File(ApplicationProvider.getApplicationContext<Context>().cacheDir.absolutePath)
    }

    private lateinit var cacheFile: File

    @Before
    fun setUp() {
        cacheFile = File(CACHE_FOLDER, IMAGE_NAME)
        val originalImage = InstrumentationRegistry.getInstrumentation().context.assets.open(IMAGE_NAME)
        val buf = ByteArray(originalImage.available())
        originalImage.read(buf)
        val outputStream: OutputStream = FileOutputStream(cacheFile)
        outputStream.write(buf)
    }

    @After
    fun tearDown() {
        assertTrue(cacheFile.delete())
    }

    @Test
    fun testRemoveExifData() {
        var exif = ExifInterface(cacheFile.absolutePath)
        assertFalse(exif.getAttribute(ExifInterface.TAG_ARTIST)!!.isEmpty())
        assertFalse(exif.getAttribute(ExifInterface.TAG_DATETIME)!!.isEmpty())

        Utils.removeExifData(CACHE_FOLDER, IMAGE_NAME)
        exif = ExifInterface(cacheFile.absolutePath)

        Constants.EXIFTAGS_FOR_EXIFREMOVER.forEach { exifTag ->
            val tag = exif.getAttribute(exifTag)
            assertTrue(tag == null || tag.isEmpty())
        }
    }

    @Test
    fun testPocketPaintExifInterfaceCall() {
        val exif = ExifInterface(cacheFile.absolutePath)
        assertEquals(0f, FileIO.getBitmapOrientation(exif))
        exif.setAttribute(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_ROTATE_180.toString())
        exif.saveAttributes()
        assertEquals(180f, FileIO.getBitmapOrientation(exif))
    }
}

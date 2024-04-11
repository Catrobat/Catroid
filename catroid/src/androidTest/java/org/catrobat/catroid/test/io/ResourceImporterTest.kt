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

package org.catrobat.catroid.test.io

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import junit.framework.Assert
import org.catrobat.catroid.common.FlavoredConstants
import org.catrobat.catroid.io.ResourceImporter
import org.catrobat.catroid.io.StorageOperations
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class ResourceImporterTest {
    private val testDir = File(FlavoredConstants.DEFAULT_ROOT_DIRECTORY, "ResourceImporterTest")
    @Before
    fun setUp() {
        testDir.mkdirs()
    }

    @After
    @Throws(IOException::class)
    fun tearDown() {
        StorageOperations.deleteDir(testDir)
    }

    @Test
    @Throws(IOException::class)
    fun testImportImageFile() {
        val fileFromDrawables = ResourceImporter.createImageFileFromResourcesInDirectory(
            InstrumentationRegistry.getInstrumentation().context.resources,
            org.catrobat.catroid.test.R.drawable.catroid_banzai, testDir, "drawable.png", 1.0
        )
        Assert.assertTrue(
            fileFromDrawables.absolutePath + " does not exist",
            fileFromDrawables.exists()
        )
        val fileFromRaw = ResourceImporter.createImageFileFromResourcesInDirectory(
            InstrumentationRegistry.getInstrumentation().context.resources,
            org.catrobat.catroid.test.R.raw.alpha_test_image,
            testDir,
            "raw.png",
            1.0
        )
        Assert.assertTrue(fileFromRaw.absolutePath + " does not exist", fileFromRaw.exists())
    }

    @Test
    @Throws(IOException::class)
    fun testImportSoundFile() {
        val fileFromRaw = ResourceImporter.createSoundFileFromResourcesInDirectory(
            InstrumentationRegistry.getInstrumentation().context.resources,
            org.catrobat.catroid.test.R.raw.longtestsound,
            testDir,
            "sound.m4a"
        )
        Assert.assertTrue(fileFromRaw.absolutePath + " does not exist", fileFromRaw.exists())
    }
}

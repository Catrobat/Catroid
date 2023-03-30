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
package org.catrobat.catroid.test.embroidery

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.android.dex.util.FileUtils
import com.badlogic.gdx.graphics.Color
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.common.Constants
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.embroidery.DSTFileGenerator
import org.catrobat.catroid.embroidery.DSTHeader
import org.catrobat.catroid.embroidery.DSTStream
import org.catrobat.catroid.embroidery.EmbroideryStream
import org.catrobat.catroid.io.StorageOperations
import org.catrobat.catroid.test.R
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class DSTFileGeneratorTest {
    private val projectName = DSTFileGeneratorTest::class.java.simpleName
    private var dstFile: File? = null
    @Before
    @Throws(IOException::class)
    fun setUp() {
        val project = Project(ApplicationProvider.getApplicationContext(), projectName)
        ProjectManager.getInstance().currentProject = project
        dstFile = File(Constants.CACHE_DIRECTORY, "$projectName.dst")
        if (dstFile!!.exists()) {
            dstFile!!.delete()
        }
        if (!Constants.CACHE_DIRECTORY.exists()) {
            Constants.CACHE_DIRECTORY.mkdirs()
        }
        dstFile!!.createNewFile()
    }

    @Test
    @Throws(IOException::class)
    fun testWriteToSampleDSTFile() {
        val stream: EmbroideryStream = DSTStream(DSTHeader())
        stream.addStitchPoint(-10f, 0f, Color.BLACK)
        stream.addStitchPoint(10f, 0f, Color.BLACK)
        stream.addStitchPoint(10f, 10f, Color.BLACK)
        stream.addStitchPoint(0f, 15f, Color.BLACK)
        stream.addStitchPoint(-10f, 10f, Color.BLACK)
        stream.addStitchPoint(-10f, 0f, Color.BLACK)
        stream.addStitchPoint(10f, 10f, Color.BLACK)
        stream.addStitchPoint(-10f, 10f, Color.BLACK)
        stream.addStitchPoint(10f, 0f, Color.BLACK)
        val fileGenerator = DSTFileGenerator(stream)
        fileGenerator.writeToDSTFile(dstFile)
        val inputStream =
            InstrumentationRegistry.getInstrumentation().context.resources.openRawResource(
                R.raw.sample_dst_file
            )
        val compareFile = StorageOperations.copyStreamToDir(
            inputStream,
            Constants.CACHE_DIRECTORY,
            "sample_dst_file.dst"
        )
        Assert.assertEquals(compareFile.length(), dstFile!!.length())
        val compareFileBytes = FileUtils.readFile(compareFile)
        val dstFileBytes = FileUtils.readFile(dstFile)
        Assert.assertArrayEquals(compareFileBytes, dstFileBytes)
    }

    private fun addArrowToStream(stream: EmbroideryStream, shiftFactor: Int) {
        stream.addStitchPoint(0f, shiftFactor.toFloat(), Color.BLACK)
        stream.addStitchPoint(40f, (-40 + shiftFactor).toFloat(), Color.BLACK)
        stream.addStitchPoint(-40f, (-40 + shiftFactor).toFloat(), Color.BLACK)
        stream.addStitchPoint(0f, shiftFactor.toFloat(), Color.BLACK)
    }

    private fun addColorChangeAndJumpToStream(stream: EmbroideryStream, shiftFactor: Int) {
        stream.addColorChange()
        stream.addStitchPoint(0f, shiftFactor.toFloat(), Color.BLACK)
        stream.addJump()
        stream.addStitchPoint(0f, shiftFactor.toFloat(), Color.BLACK)
    }

    @Test
    @Throws(IOException::class)
    fun testWriteToComplexSampleDSTFile() {
        val stream: EmbroideryStream = DSTStream(DSTHeader())
        addArrowToStream(stream, 0)
        addColorChangeAndJumpToStream(stream, 0)
        addArrowToStream(stream, 20)
        addColorChangeAndJumpToStream(stream, 20)
        addArrowToStream(stream, 40)
        val fileGenerator = DSTFileGenerator(stream)
        fileGenerator.writeToDSTFile(dstFile)
        val inputStream =
            InstrumentationRegistry.getInstrumentation().context.resources.openRawResource(
                R.raw.complex_sample_dst_file
            )
        val compareFile = StorageOperations.copyStreamToDir(
            inputStream, Constants.CACHE_DIRECTORY,
            "complex_sample_dst_file.dst"
        )
        Assert.assertEquals(compareFile.length(), dstFile!!.length())
        val compareFileBytes = FileUtils.readFile(compareFile)
        val dstFileBytes = FileUtils.readFile(dstFile)
        Assert.assertArrayEquals(compareFileBytes, dstFileBytes)
    }
}

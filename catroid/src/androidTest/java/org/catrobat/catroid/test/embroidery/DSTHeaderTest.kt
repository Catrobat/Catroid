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
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.embroidery.DSTFileConstants
import org.catrobat.catroid.embroidery.DSTHeader
import org.catrobat.catroid.embroidery.EmbroideryHeader
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import java.io.FileOutputStream
import java.io.IOException
import java.util.Locale

@RunWith(AndroidJUnit4::class)
class DSTHeaderTest {
    private val projectName = DSTHeaderTest::class.java.simpleName
    private var fileOutputStream: FileOutputStream? = null
    @Before
    fun setUp() {
        val project = Project(ApplicationProvider.getApplicationContext(), projectName)
        ProjectManager.getInstance().currentProject = project
        fileOutputStream = Mockito.mock(FileOutputStream::class.java)
    }

    @Test
    @Throws(IOException::class)
    fun testDSTHeaderInitialize() {
        val expectedX = 2.0f
        val expectedY = 4.0f
        val stringBuilder = StringBuilder()
        stringBuilder.append(String.format(DSTFileConstants.DST_HEADER_LABEL, projectName))
            .append(
                String.format(
                    Locale.getDefault(),
                    DSTFileConstants.DST_HEADER,
                    1,
                    1,
                    expectedX.toInt(),
                    expectedX.toInt(),
                    expectedY.toInt(),
                    expectedY.toInt(),
                    (expectedX - expectedX).toInt(),
                    (expectedY - expectedY).toInt(),
                    0,
                    0,
                    "*****"
                ).replace(
                    ' ',
                    '\u0000'
                )
            )
            .append(DSTFileConstants.HEADER_FILL)
        val header: EmbroideryHeader = DSTHeader()
        header.initialize(1.0f, 2.0f)
        header.appendToStream(fileOutputStream)
        Mockito.verify(fileOutputStream, Mockito.times(1))
            ?.write(stringBuilder.toString().toByteArray())
    }

    @Test
    @Throws(IOException::class)
    fun testDSTHeaderColorChangeReset() {
        val stringBuilder = StringBuilder()
        stringBuilder.append(String.format(DSTFileConstants.DST_HEADER_LABEL, projectName))
            .append(
                String.format(
                    Locale.getDefault(), DSTFileConstants.DST_HEADER, 1,
                    2, 0, 0, 0, 0, 0, 0, 0, 0, "*****"
                ).replace(' ', '\u0000')
            )
            .append(DSTFileConstants.HEADER_FILL)
        val header: EmbroideryHeader = DSTHeader()
        header.initialize(0f, 0f)
        header.addColorChange()
        header.appendToStream(fileOutputStream)
        Mockito.verify(fileOutputStream, Mockito.times(1))
            ?.write(stringBuilder.toString().toByteArray())
    }

    @Test
    @Throws(IOException::class)
    fun testDSTHeaderUpdate() {
        val expectedXInit = 0.0f
        val expectedXHigh = 10.0f
        val expectedXLow = -4.0f
        val expectedYInit = 0.0f
        val expectedYHigh = 4.0f
        val expectedYLow = -10.0f
        val stringBuilder = StringBuilder()
        stringBuilder.append(String.format(DSTFileConstants.DST_HEADER_LABEL, projectName))
            .append(
                String.format(
                    Locale.getDefault(),
                    DSTFileConstants.DST_HEADER,
                    3,
                    1,
                    expectedXHigh.toInt(),
                    expectedXLow.toInt(),
                    expectedYHigh.toInt(),
                    expectedYLow.toInt(),
                    (expectedXHigh - expectedXInit).toInt(),
                    (expectedYHigh - expectedYInit).toInt(),
                    0,
                    0,
                    "*****"
                ).replace(
                    ' ',
                    '\u0000'
                )
            )
            .append(DSTFileConstants.HEADER_FILL)
        val header: EmbroideryHeader = DSTHeader()
        header.initialize(0f, 0f)
        header.update(-2.0f, -5.0f)
        header.update(5.0f, 2.0f)
        header.appendToStream(fileOutputStream)
        Mockito.verify(fileOutputStream, Mockito.times(1))
            ?.write(stringBuilder.toString().toByteArray())
    }
}

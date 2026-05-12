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
import org.mockito.Mockito.mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import java.io.FileOutputStream
import java.io.IOException
import java.nio.charset.StandardCharsets.US_ASCII
import java.util.Locale

@RunWith(AndroidJUnit4::class)
class DSTHeaderTest {
    private val projectName = DSTHeaderTest::class.java.simpleName
    private lateinit var fileOutputStream: FileOutputStream

    @Before
    fun setUp() {
        val project = Project(ApplicationProvider.getApplicationContext(), projectName)
        ProjectManager.getInstance().currentProject = project
        fileOutputStream = mock(FileOutputStream::class.java)
    }

    @Test
    @Throws(IOException::class)
    fun testDSTHeaderInitialize() {
        val header: EmbroideryHeader = DSTHeader()
        header.initialize(1.0f, 2.0f)
        header.appendToStream(fileOutputStream)

        verify(fileOutputStream, times(1)).write(
            expectedHeader(
                stitchCount = 1,
                colorChanges = 1,
                positiveXExtent = 2,
                negativeXExtent = 0,
                positiveYExtent = 4,
                negativeYExtent = 0,
                deltaX = 0,
                deltaY = 0
            )
        )
    }

    @Test
    @Throws(IOException::class)
    fun testDSTHeaderColorChangeReset() {
        val header: EmbroideryHeader = DSTHeader()
        header.initialize(0.0f, 0.0f)
        header.addColorChange()

        header.appendToStream(fileOutputStream)

        verify(fileOutputStream, times(1)).write(
            expectedHeader(
                stitchCount = 1,
                colorChanges = 2,
                positiveXExtent = 0,
                negativeXExtent = 0,
                positiveYExtent = 0,
                negativeYExtent = 0,
                deltaX = 0,
                deltaY = 0
            )
        )
    }

    @Test
    @Throws(IOException::class)
    fun testDSTHeaderUpdate() {
        val header: EmbroideryHeader = DSTHeader()
        header.initialize(0.0f, 0.0f)
        header.update(-2.0f, -5.0f)
        header.update(5.0f, 2.0f)

        header.appendToStream(fileOutputStream)

        verify(fileOutputStream, times(1)).write(
            expectedHeader(
                stitchCount = 3,
                colorChanges = 1,
                positiveXExtent = 10,
                negativeXExtent = 4,
                positiveYExtent = 4,
                negativeYExtent = 10,
                deltaX = 10,
                deltaY = 4
            )
        )
    }

    private fun expectedHeader(
        stitchCount: Int,
        colorChanges: Int,
        positiveXExtent: Int,
        negativeXExtent: Int,
        positiveYExtent: Int,
        negativeYExtent: Int,
        deltaX: Int,
        deltaY: Int
    ): ByteArray =
        buildString {
            append(String.format(DSTFileConstants.DST_HEADER_LABEL, projectName))
            append(
                String.format(
                    Locale.getDefault(),
                    DSTFileConstants.DST_HEADER,
                    stitchCount,
                    colorChanges,
                    positiveXExtent,
                    negativeXExtent,
                    positiveYExtent,
                    negativeYExtent,
                    deltaX,
                    deltaY,
                    0,
                    0,
                    "*****"
                ).replace(' ', '\u0000')
            )
            append(DSTFileConstants.HEADER_FILL)
        }.toByteArray(US_ASCII)
}

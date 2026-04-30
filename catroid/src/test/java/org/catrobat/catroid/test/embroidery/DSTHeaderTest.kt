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

import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.embroidery.DSTHeader
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.Mockito.mockStatic
import java.io.File
import java.io.FileOutputStream
import java.nio.charset.StandardCharsets
import java.nio.file.Files

class DSTHeaderTest {

    @Test
    fun testAppendToStreamUsesFixedWidthHeaderForNegativeExtents() {
        val header = DSTHeader()
        header.initialize(365f, 847f)
        header.update(-143f, -759.5f)

        val projectManager = mock(ProjectManager::class.java)
        val project = mock(Project::class.java)
        val tempFile = File.createTempFile("dst-header", ".dst")
        val projectManagerMock = mockStatic(ProjectManager::class.java)

        try {
            projectManagerMock.`when`<Any> { ProjectManager.getInstance() }.thenReturn(projectManager)
            Mockito.`when`(projectManager.currentProject).thenReturn(project)
            Mockito.`when`(project.name).thenReturn("My project")

            FileOutputStream(tempFile).use(header::appendToStream)

            val headerBytes = Files.readAllBytes(tempFile.toPath())
            val headerText = String(headerBytes, StandardCharsets.US_ASCII)

            assertEquals(512, headerBytes.size)
            assertTrue(headerText.contains("+X:730"))
            assertTrue(headerText.contains("-X:286"))
            assertTrue(headerText.contains("+Y:1694"))
            assertTrue(headerText.contains("-Y:1519"))
        } finally {
            projectManagerMock.close()
            tempFile.delete()
        }
    }
}

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

package org.catrobat.catroid.test.utils

import org.catrobat.catroid.common.Constants
import org.catrobat.catroid.utils.ProjectIdUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.io.File

@RunWith(JUnit4::class)
class ServerProjectIdTest {

    @get:Rule
    val folder = TemporaryFolder()

    private val ownId = "0f8fad5b-d9cb-469f-a165-70867728950e"

    @Test
    fun `a new project has no server id`() {
        val project = project("<header><url></url></header>")

        assertNull(ProjectIdUtils.readServerProjectId(project))
    }

    @Test
    fun `an uploaded project is updated on the next upload`() {
        val project = project("<header><url></url></header>")

        ProjectIdUtils.saveServerProjectId(project, ownId)

        assertEquals(ownId, ProjectIdUtils.readServerProjectId(project))
        assertEquals(
            "<header><url>${Constants.SHARE_PROJECT_URL}$ownId</url></header>",
            File(project, Constants.CODE_XML_FILE_NAME).readText()
        )
    }

    @Test
    fun `a project from before the id file is found by its share url`() {
        val project = project("<header><url>https://share.catrobat.org/app/project/$ownId</url></header>")

        assertEquals(ownId, ProjectIdUtils.readServerProjectId(project))
    }

    @Test
    fun `a copy or a download is uploaded as a new project, even though its share url names the original`() {
        val copy = project("<header><url>https://share.catrobat.org/app/project/$ownId</url></header>")
        File(copy, Constants.SERVER_PROJECT_ID_FILE_NAME).writeText(ownId)

        ProjectIdUtils.markAsNewProject(copy)

        assertNull(ProjectIdUtils.readServerProjectId(copy))
    }

    @Test
    fun `an id that is not a uuid is never written into code xml`() {
        val project = project("<header><url></url></header>")

        assertFalse(ProjectIdUtils.saveServerProjectId(project, "\$1</url><evil/>"))
        assertEquals("<header><url></url></header>", File(project, Constants.CODE_XML_FILE_NAME).readText())
        assertNull(ProjectIdUtils.readServerProjectId(project))
    }

    private fun project(codeXml: String): File {
        val dir = folder.newFolder()
        File(dir, Constants.CODE_XML_FILE_NAME).writeText(codeXml)
        return dir
    }
}

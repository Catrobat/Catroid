/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2025 The Catrobat Team
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
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.catroid.test.ui.launcher

import org.catrobat.catroid.ui.recyclerview.dialog.LauncherIconPreviewDialog
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class LauncherIconPreviewDialogTest {

    private lateinit var tempDir: File

    @Before
    fun setUp() {
        tempDir = createTempDir("dialogTest")
    }

    @After
    fun tearDown() {
        tempDir.deleteRecursively()
    }

    @Test
    fun `newInstance stores project name in arguments bundle`() {
        val dialog = LauncherIconPreviewDialog.newInstance("Test Project Alpha", tempDir)
        assertEquals("Test Project Alpha", dialog.requireArguments().getString("projectName"))
    }

    @Test
    fun `newInstance stores project dir path in arguments bundle`() {
        val dialog = LauncherIconPreviewDialog.newInstance("My App", tempDir)
        assertEquals(tempDir.absolutePath, dialog.requireArguments().getString("projectDirPath"))
    }

    @Test
    fun `newInstance with empty name stores empty string in bundle`() {
        val dialog = LauncherIconPreviewDialog.newInstance("", tempDir)
        assertEquals("", dialog.requireArguments().getString("projectName"))
    }

    @Test
    fun `newInstance with long project name stores full name in bundle`() {
        val longName = "A".repeat(200)
        val dialog = LauncherIconPreviewDialog.newInstance(longName, tempDir)
        assertEquals(longName, dialog.requireArguments().getString("projectName"))
    }

    @Test
    fun `newInstance arguments bundle is non-null`() {
        val dialog = LauncherIconPreviewDialog.newInstance("Project X", tempDir)
        assertNotNull(dialog.arguments)
    }

    @Test
    fun `newInstance with different dirs stores distinct paths`() {
        val otherDir = createTempDir("otherProject")
        try {
            val dialog1 = LauncherIconPreviewDialog.newInstance("A", tempDir)
            val dialog2 = LauncherIconPreviewDialog.newInstance("B", otherDir)
            val path1 = dialog1.requireArguments().getString("projectDirPath")
            val path2 = dialog2.requireArguments().getString("projectDirPath")
            assertNull("Paths should differ", if (path1 != path2) null else "same")
        } finally {
            otherDir.deleteRecursively()
        }
    }
}

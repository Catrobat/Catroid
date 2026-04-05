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

package org.catrobat.catroid.ui.recyclerview.fragment

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class ProjectUndoManagerTest {

    @get:Rule
    val tempFolder = TemporaryFolder()

    private lateinit var projectDir: File
    private lateinit var undoManager: ProjectUndoManager

    @Before
    fun setUp() {
        projectDir = tempFolder.newFolder("testProject")
        File(projectDir, "code.xml").createNewFile()
        undoManager = ProjectUndoManager(projectDir)
    }

    @Test
    fun testUndoStackLimit() {
        for (i in 0 until 25) {
            undoManager.pushState(
                "scene", "sprite",
                ProjectUndoManager.VariableSnapshot(
                    emptyList(), emptyList(), emptyList(), emptyList(), emptyList()
                )
            )
        }

        assertTrue("Undo stack should be limited", undoManager.canUndo())

        var count = 0
        while (undoManager.popUndo(
                "scene", "sprite",
                ProjectUndoManager.VariableSnapshot(
                    emptyList(), emptyList(), emptyList(), emptyList(), emptyList()
                )
            ) != null
        ) {
            count++
        }
        assertEquals(20, count)
    }

    @Test
    fun testInitClearsExistingHistory() {
        val undoDir = File(projectDir, "undo_history")
        if (!undoDir.exists()) {
            undoDir.mkdirs()
        }
        val oldFile = File(undoDir, "old_snap.xml")
        oldFile.createNewFile()
        val recentFile = File(undoDir, "recent_snap.xml")
        recentFile.createNewFile()

        undoManager = ProjectUndoManager(projectDir)

        assertFalse("Recent file should be deleted during initialization", recentFile.exists())
        assertFalse("Old file should be deleted during initialization", oldFile.exists())
        assertTrue("Undo directory should still exist after initialization", undoDir.exists())
    }

    @Test
    fun testClearHistory() {
        undoManager.pushState(
            "scene", "sprite",
            ProjectUndoManager.VariableSnapshot(
                emptyList(), emptyList(), emptyList(), emptyList(), emptyList()
            )
        )
        assertTrue(undoManager.canUndo())

        undoManager.clearHistory()
        assertFalse("Undo stack should be empty after clearHistory", undoManager.canUndo())
        assertFalse("Redo stack should be empty after clearHistory", undoManager.canRedo())
    }

    @Test
    fun testUniqueFilenames() {
        undoManager.pushState(
            "scene", "sprite",
            ProjectUndoManager.VariableSnapshot(
                emptyList(), emptyList(), emptyList(), emptyList(), emptyList()
            )
        )
        undoManager.pushState(
            "scene", "sprite",
            ProjectUndoManager.VariableSnapshot(
                emptyList(), emptyList(), emptyList(), emptyList(), emptyList()
            )
        )

        val entry1 = undoManager.popUndo(
            "scene", "sprite",
            ProjectUndoManager.VariableSnapshot(
                emptyList(), emptyList(), emptyList(), emptyList(), emptyList()
            )
        )
        val entry2 = undoManager.popUndo(
            "scene", "sprite",
            ProjectUndoManager.VariableSnapshot(
                emptyList(), emptyList(), emptyList(), emptyList(), emptyList()
            )
        )

        assertTrue(
            "Snapshot names should be unique",
            entry1!!.snapshotFileName != entry2!!.snapshotFileName
        )
    }
}

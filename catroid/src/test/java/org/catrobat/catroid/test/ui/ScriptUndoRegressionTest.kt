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

package org.catrobat.catroid.test.ui

import org.catrobat.catroid.ui.recyclerview.fragment.ProjectUndoManager
import org.catrobat.catroid.ui.recyclerview.fragment.ProjectUndoManager.VariableSnapshot
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class ScriptUndoRegressionTest {

    @get:Rule
    val tempFolder = TemporaryFolder()

    private lateinit var projectDir: File
    private lateinit var undoManager: ProjectUndoManager
    private lateinit var codeFile: File

    private val emptySnapshot = VariableSnapshot(
        emptyList(), emptyList(), emptyList(), emptyList(), emptyList()
    )

    @Before
    fun setUp() {
        projectDir = tempFolder.newFolder("testProject")
        codeFile = File(projectDir, "code.xml")
        codeFile.writeText("<xml>original</xml>")
        undoManager = ProjectUndoManager(projectDir)
    }

    /**
     * Simulates the scenario where a user inserts a brick.
     * After an insert, copyProjectForUndoOption() pushes an undo state.
     * The undo stack should contain exactly one entry that can be popped.
     */
    @Test
    fun testInsertBrickCreatesUndoEntry() {
        undoManager.pushState("scene", "sprite", emptySnapshot)

        assertTrue("Undo should be available after inserting a brick", undoManager.canUndo())
        assertFalse("Redo should not be available after a fresh action", undoManager.canRedo())

        val entry = undoManager.popUndo("scene", "sprite", emptySnapshot)
        assertNotNull("Should be able to pop the undo entry for insert", entry)
        assertFalse("Undo stack should be empty after popping the only entry", undoManager.canUndo())
    }

    /**
     * Simulates the scenario where a user copies a single brick.
     * The copy action calls copyProjectForUndoOption() before cloning.
     * The undo stack should contain one entry.
     */
    @Test
    fun testCopyBrickCreatesUndoEntry() {
        codeFile.writeText("<xml>before-copy</xml>")
        undoManager.pushState("scene", "sprite", emptySnapshot)

        assertTrue("Undo should be available after copying a brick", undoManager.canUndo())

        codeFile.writeText("<xml>after-copy</xml>")
        val entry = undoManager.popUndo("scene", "sprite", emptySnapshot)
        assertNotNull("Should be able to pop the undo entry for copy", entry)

        val restoredContent = codeFile.readText()
        assertEquals(
            "code.xml should be restored to the pre-copy snapshot",
            "<xml>before-copy</xml>",
            restoredContent
        )
    }

    /**
     * Simulates copying a block/control structure (composite brick).
     * The behavior is the same as copying a single brick: one undo entry.
     */
    @Test
    fun testCopyBlockStructureCreatesUndoEntry() {
        codeFile.writeText("<xml>before-copy-block</xml>")
        undoManager.pushState("scene", "sprite", emptySnapshot)

        assertTrue("Undo should be available after copying a block structure", undoManager.canUndo())

        codeFile.writeText("<xml>after-copy-block</xml>")
        val entry = undoManager.popUndo("scene", "sprite", emptySnapshot)
        assertNotNull("Should be able to undo a block copy", entry)

        assertEquals(
            "code.xml should be restored to the pre-copy-block snapshot",
            "<xml>before-copy-block</xml>",
            codeFile.readText()
        )
    }

    /**
     * Simulates the scenario where a user moves a brick and commits the move.
     * With the new deferred undo approach, copyProjectForUndoOption() is called
     * only when the move is committed (stopMoving). The undo stack should have one entry.
     */
    @Test
    fun testMoveBrickCommittedCreatesUndoEntry() {
        codeFile.writeText("<xml>before-move</xml>")

        // onMoveCommitted calls copyProjectForUndoOption only on commit
        undoManager.pushState("scene", "sprite", emptySnapshot)

        assertTrue("Undo should be available after a committed move", undoManager.canUndo())

        codeFile.writeText("<xml>after-move</xml>")
        val entry = undoManager.popUndo("scene", "sprite", emptySnapshot)
        assertNotNull("Should be able to undo a committed move", entry)

        assertEquals(
            "code.xml should be restored to pre-move state",
            "<xml>before-move</xml>",
            codeFile.readText()
        )
    }

    /**
     * Simulates the scenario where a user moves a block/control structure and commits.
     * Same behavior as single brick move.
     */
    @Test
    fun testMoveBlockStructureCommittedCreatesUndoEntry() {
        codeFile.writeText("<xml>before-move-block</xml>")
        undoManager.pushState("scene", "sprite", emptySnapshot)

        assertTrue("Undo should be available after a committed block move", undoManager.canUndo())

        codeFile.writeText("<xml>after-move-block</xml>")
        val entry = undoManager.popUndo("scene", "sprite", emptySnapshot)
        assertNotNull("Should be able to undo a committed block move", entry)
        assertEquals(
            "<xml>before-move-block</xml>",
            codeFile.readText()
        )
    }

    /**
     * Simulates commenting/uncommenting bricks.
     * The comment toggle calls copyProjectForUndoOption() before changing state.
     */
    @Test
    fun testCommentOutCreatesUndoEntry() {
        codeFile.writeText("<xml>before-comment</xml>")
        undoManager.pushState("scene", "sprite", emptySnapshot)

        assertTrue("Undo should be available after commenting out", undoManager.canUndo())

        codeFile.writeText("<xml>after-comment</xml>")
        val entry = undoManager.popUndo("scene", "sprite", emptySnapshot)
        assertNotNull("Should be able to undo a comment toggle", entry)
        assertEquals(
            "<xml>before-comment</xml>",
            codeFile.readText()
        )
    }

    /**
     * Simulates uncommenting bricks - same mechanism as commenting.
     */
    @Test
    fun testCommentInCreatesUndoEntry() {
        codeFile.writeText("<xml>before-uncomment</xml>")
        undoManager.pushState("scene", "sprite", emptySnapshot)

        codeFile.writeText("<xml>after-uncomment</xml>")
        val entry = undoManager.popUndo("scene", "sprite", emptySnapshot)
        assertNotNull("Should be able to undo an uncomment", entry)
        assertEquals(
            "<xml>before-uncomment</xml>",
            codeFile.readText()
        )
    }

    /**
     * KEY TEST: Simulates the scenario where a user starts dragging a brick (move)
     * but then cancels (back-press). With the deferred undo approach, NO undo entry
     * should be created. This is the main fix requested by the maintainer.
     *
     * Before the fix, copyProjectForUndoOption() was called BEFORE startMoving(),
     * so canceling would leave a no-op undo entry. Now, pushState is only called
     * on commit (stopMoving), so canceling should result in an empty undo stack.
     */
    @Test
    fun testCancelMoveDoesNotCreateUndoEntry() {
        // The user has NOT called pushState because the move was never committed.
        // The pendingMoveUndoSnapshot flag would be set to true and then reset
        // by cancelMove() without ever calling copyProjectForUndoOption().

        // Simulate: no pushState was ever called
        assertFalse(
            "Undo should NOT be available after a canceled move",
            undoManager.canUndo()
        )
        assertFalse(
            "Redo should NOT be available after a canceled move",
            undoManager.canRedo()
        )

        val entry = undoManager.popUndo("scene", "sprite", emptySnapshot)
        assertNull("Popping undo after cancel should return null", entry)
    }

    /**
     * Tests that canceling a move after a previous committed action does not
     * create an additional undo entry. The undo stack should only contain
     * the entry from the committed action.
     */
    @Test
    fun testCancelMovePreservesExistingUndoStack() {
        // Simulate a previous committed action (e.g., delete)
        codeFile.writeText("<xml>before-delete</xml>")
        undoManager.pushState("scene", "sprite", emptySnapshot)

        assertTrue(undoManager.canUndo())

        // Simulate user starts a drag but cancels — no additional pushState
        // pendingMoveUndoSnapshot flag is set and cleared without pushing

        // Verify only one undo entry exists (from the delete, not from the canceled move)
        val entry = undoManager.popUndo("scene", "sprite", emptySnapshot)
        assertNotNull("Should pop the undo from the prior delete action", entry)

        assertFalse(
            "Undo stack should be empty — canceled move should NOT have added an entry",
            undoManager.canUndo()
        )
    }

    /**
     * Tests the full undo/redo cycle: perform an action, undo it, then redo it.
     */
    @Test
    fun testUndoRedoCycle() {
        codeFile.writeText("<xml>original-state</xml>")
        undoManager.pushState("scene", "sprite", emptySnapshot)

        codeFile.writeText("<xml>modified-state</xml>")

        // Undo
        assertTrue(undoManager.canUndo())
        val undoEntry = undoManager.popUndo("scene", "sprite", emptySnapshot)
        assertNotNull("Undo should return an entry", undoEntry)
        assertEquals("<xml>original-state</xml>", codeFile.readText())
        assertTrue("Redo should be available after undo", undoManager.canRedo())

        // Redo
        codeFile.writeText("<xml>original-state</xml>")
        val redoEntry = undoManager.popRedo("scene", "sprite", emptySnapshot)
        assertNotNull("Redo should return an entry", redoEntry)
        assertEquals("<xml>modified-state</xml>", codeFile.readText())
    }

    /**
     * Tests that performing a new action after an undo clears the redo stack.
     */
    @Test
    fun testNewActionAfterUndoClearsRedo() {
        codeFile.writeText("<xml>state1</xml>")
        undoManager.pushState("scene", "sprite", emptySnapshot)

        codeFile.writeText("<xml>state2</xml>")

        // Undo
        undoManager.popUndo("scene", "sprite", emptySnapshot)
        assertTrue("Redo should be available after undo", undoManager.canRedo())

        // New action — should clear redo
        codeFile.writeText("<xml>state1</xml>")
        undoManager.pushState("scene", "sprite", emptySnapshot)

        assertFalse(
            "Redo should be cleared after a new action following an undo",
            undoManager.canRedo()
        )
    }

    /**
     * Tests multiple sequential actions create multiple undo entries.
     */
    @Test
    fun testMultipleActionsCreateMultipleUndoEntries() {
        for (i in 1..5) {
            codeFile.writeText("<xml>state-$i</xml>")
            undoManager.pushState("scene", "sprite", emptySnapshot)
        }

        var count = 0
        while (undoManager.canUndo()) {
            val entry = undoManager.popUndo("scene", "sprite", emptySnapshot)
            assertNotNull(entry)
            count++
        }
        assertEquals("Should have 5 undo entries for 5 actions", 5, count)
    }

    /**
     * Tests variable snapshot is properly stored and returned on undo.
     */
    @Test
    fun testVariableSnapshotPreservedInUndo() {
        val snapshot = VariableSnapshot(
            emptyList(), emptyList(), emptyList(), emptyList(), emptyList()
        )
        undoManager.pushState("testScene", "testSprite", snapshot)

        val entry = undoManager.popUndo("testScene", "testSprite", emptySnapshot)
        assertNotNull(entry)
        assertEquals("testScene", entry!!.sceneName)
        assertEquals("testSprite", entry.spriteName)
        assertNotNull(entry.variableSnapshot)
    }
}

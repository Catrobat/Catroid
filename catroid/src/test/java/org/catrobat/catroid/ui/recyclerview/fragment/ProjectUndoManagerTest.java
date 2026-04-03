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
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.catroid.ui.recyclerview.fragment;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ProjectUndoManagerTest {

	@Rule
	public TemporaryFolder tempFolder = new TemporaryFolder();

	private File projectDir;
	private ProjectUndoManager undoManager;

	@Before
	public void setUp() throws IOException {
		projectDir = tempFolder.newFolder("testProject");
		new File(projectDir, "code.xml").createNewFile();
		undoManager = new ProjectUndoManager(projectDir);
	}

	@Test
	public void testUndoStackLimit() {
		// Default limit is 20 in the current code, but let's test if it respects a limit.
		// We'll push 25 states.
		for (int i = 0; i < 25; i++) {
			undoManager.pushState("scene", "sprite", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
		}

		// It should be capped at 20 (MAX_UNDO_STEPS currently).
		assertTrue("Undo stack should be limited", undoManager.canUndo());
		// We can't access undoStack directly, but we can pop until empty.
		int count = 0;
		while (undoManager.popUndo("scene", "sprite", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>()) != null) {
			count++;
		}
		assertEquals(20, count);
	}

	@Test
	public void testInitClearsExistingHistory() throws IOException {
		File undoDir = new File(projectDir, "undo_history");
		if (!undoDir.exists()) {
			undoDir.mkdirs();
		}
		File oldFile = new File(undoDir, "old_snap.xml");
		oldFile.createNewFile();
		File recentFile = new File(undoDir, "recent_snap.xml");
		recentFile.createNewFile();

		undoManager = new ProjectUndoManager(projectDir);

		assertFalse("Recent file should be deleted during initialization", recentFile.exists());
		assertFalse("Old file should be deleted during initialization", oldFile.exists());
		assertTrue("Undo directory should still exist after initialization", undoDir.exists());
	}

	@Test
	public void testClearHistory() {
		undoManager.pushState("scene", "sprite", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
		assertTrue(undoManager.canUndo());

		undoManager.clearHistory();
		assertFalse("Undo stack should be empty after clearHistory", undoManager.canUndo());
		assertFalse("Redo stack should be empty after clearHistory", undoManager.canRedo());
	}

	@Test
	public void testUniqueFilenames() {
		undoManager.pushState("scene", "sprite", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
		undoManager.pushState("scene", "sprite", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());

		// We can't access redoStack or undoStack directly, but if we pop them, we can check.
		// Wait, pushState clears redoStack. So we have 2 in undoStack.
		ProjectUndoManager.UndoEntry entry1 = undoManager.popUndo("scene", "sprite", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
		ProjectUndoManager.UndoEntry entry2 = undoManager.popUndo("scene", "sprite", new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());

		assertTrue("Snapshot names should be unique", !entry1.snapshotFileName.equals(entry2.snapshotFileName));
	}
}

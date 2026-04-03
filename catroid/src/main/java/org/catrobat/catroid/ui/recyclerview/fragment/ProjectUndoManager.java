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

import android.util.Log;

import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.formulaeditor.UserList;
import org.catrobat.catroid.formulaeditor.UserVariable;
import org.catrobat.catroid.io.StorageOperations;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ProjectUndoManager {
	private static final String TAG = ProjectUndoManager.class.getSimpleName();
	private static final int MAX_UNDO_STEPS = 20;

	private final File projectDir;
	private final File undoDir;
	private final List<UndoEntry> undoStack = new ArrayList<>();
	private final List<UndoEntry> redoStack = new ArrayList<>();

	public static class VariableSnapshot {
		public final List<UserVariable> savedUserVariables;
		public final List<UserVariable> savedMultiplayerVariables;
		public final List<UserList> savedUserLists;
		public final List<UserVariable> savedLocalUserVariables;
		public final List<UserList> savedLocalLists;

		public VariableSnapshot(List<UserVariable> userVariables, List<UserVariable> multiplayerVariables,
								List<UserList> userLists, List<UserVariable> localUserVariables,
								List<UserList> localLists) {
			this.savedUserVariables = userVariables;
			this.savedMultiplayerVariables = multiplayerVariables;
			this.savedUserLists = userLists;
			this.savedLocalUserVariables = localUserVariables;
			this.savedLocalLists = localLists;
		}
	}

	public static class UndoEntry {
		public final String snapshotFileName;
		public final String sceneName;
		public final String spriteName;
		public final VariableSnapshot variableSnapshot;

		public UndoEntry(String snapshotFileName, String sceneName, String spriteName,
						VariableSnapshot variableSnapshot) {
			this.snapshotFileName = snapshotFileName;
			this.sceneName = sceneName;
			this.spriteName = spriteName;
			this.variableSnapshot = variableSnapshot;
		}
	}

	public ProjectUndoManager(File projectDir) {
		this.projectDir = projectDir;
		this.undoDir = new File(projectDir, Constants.UNDO_DIRECTORY_NAME);
		if (undoDir.exists()) {
			clearHistory();
		}
		if (!undoDir.exists() && !undoDir.mkdirs()) {
			Log.e(TAG, "Failed to create undo history directory: " + undoDir.getAbsolutePath());
		}
	}

	public static void clearUndoHistoryForProject(File projectDir) {
		File undoDir = new File(projectDir, Constants.UNDO_DIRECTORY_NAME);
		if (undoDir.exists()) {
			File[] files = undoDir.listFiles();
			if (files != null) {
				for (File file : files) {
					if (file.exists() && !file.delete()) {
						Log.w(TAG, "Failed to delete project snapshot file: " + file.getAbsolutePath());
					}
				}
			}
			if (undoDir.exists() && !undoDir.delete()) {
				Log.w(TAG, "Failed to delete undo directory: " + undoDir.getAbsolutePath());
			}
		}
	}

	public void pushState(String sceneName, String spriteName,
						List<UserVariable> userVariables, List<UserVariable> multiplayerVariables,
						List<UserList> userLists, List<UserVariable> localUserVariables,
						List<UserList> localLists) {
		File currentCodeFile = new File(projectDir, Constants.CODE_XML_FILE_NAME);
		if (!currentCodeFile.exists()) {
			return;
		}

		try {
			File snapshotFile = File.createTempFile("snap_", ".xml", undoDir);
			String snapshotName = snapshotFile.getName();

			StorageOperations.copyFile(currentCodeFile, snapshotFile);
			VariableSnapshot variables = new VariableSnapshot(
					new ArrayList<>(userVariables), new ArrayList<>(multiplayerVariables),
					new ArrayList<>(userLists), new ArrayList<>(localUserVariables),
					new ArrayList<>(localLists));
			undoStack.add(new UndoEntry(snapshotName, sceneName, spriteName, variables));
			for (UndoEntry redoEntry : redoStack) {
				File redoFile = new File(undoDir, redoEntry.snapshotFileName);
				if (redoFile.exists() && !redoFile.delete()) {
					Log.w(TAG, "Failed to delete redo snapshot: " + redoFile.getAbsolutePath());
				}
			}
			redoStack.clear();

			if (undoStack.size() > MAX_UNDO_STEPS) {
				UndoEntry oldest = undoStack.remove(0);
				File oldestFile = new File(undoDir, oldest.snapshotFileName);
				if (oldestFile.exists() && !oldestFile.delete()) {
					Log.w(TAG, "Failed to delete oldest snapshot: " + oldestFile.getAbsolutePath());
				}
			}
		} catch (IOException e) {
			Log.e(TAG, "Failed to push undo state", e);
		}
	}

	public UndoEntry popUndo(String sceneName, String spriteName,
						List<UserVariable> userVariables, List<UserVariable> multiplayerVariables,
						List<UserList> userLists, List<UserVariable> localUserVariables,
						List<UserList> localLists) {
		if (undoStack.isEmpty()) {
			return null;
		}

		UndoEntry redoEntry = pushCurrentToRedo(sceneName, spriteName,
				userVariables, multiplayerVariables, userLists, localUserVariables, localLists);
		if (redoEntry == null) {
			return null;
		}

		UndoEntry entry = undoStack.remove(undoStack.size() - 1);
		if (restoreSnapshot(entry)) {
			File snapshotFile = new File(undoDir, entry.snapshotFileName);
			if (snapshotFile.exists() && !snapshotFile.delete()) {
				Log.w(TAG, "Failed to delete undo snapshot file: " + snapshotFile.getAbsolutePath());
			}
			return entry;
		} else {
			undoStack.add(entry);
			redoStack.remove(redoStack.size() - 1);
			File redoFile = new File(undoDir, redoEntry.snapshotFileName);
			if (redoFile.exists() && !redoFile.delete()) {
				Log.w(TAG, "Failed to delete redundant redo snapshot: " + redoFile.getAbsolutePath());
			}
			return null;
		}
	}

	public UndoEntry popRedo(String sceneName, String spriteName,
						List<UserVariable> userVariables, List<UserVariable> multiplayerVariables,
						List<UserList> userLists, List<UserVariable> localUserVariables,
						List<UserList> localLists) {
		if (redoStack.isEmpty()) {
			return null;
		}

		UndoEntry undoEntry = pushCurrentToUndoInternal(sceneName, spriteName,
				userVariables, multiplayerVariables, userLists, localUserVariables, localLists);
		if (undoEntry == null) {
			return null;
		}

		UndoEntry entry = redoStack.remove(redoStack.size() - 1);
		if (restoreSnapshot(entry)) {
			File snapshotFile = new File(undoDir, entry.snapshotFileName);
			if (snapshotFile.exists() && !snapshotFile.delete()) {
				Log.w(TAG, "Failed to delete redo snapshot file: " + snapshotFile.getAbsolutePath());
			}
			return entry;
		} else {
			redoStack.add(entry);
			undoStack.remove(undoStack.size() - 1);
			File undoFile = new File(undoDir, undoEntry.snapshotFileName);
			if (undoFile.exists() && !undoFile.delete()) {
				Log.w(TAG, "Failed to delete redundant undo snapshot: " + undoFile.getAbsolutePath());
			}
			return null;
		}
	}

	private UndoEntry pushCurrentToRedo(String sceneName, String spriteName,
						List<UserVariable> userVariables, List<UserVariable> multiplayerVariables,
						List<UserList> userLists, List<UserVariable> localUserVariables,
						List<UserList> localLists) {
		File currentCodeFile = new File(projectDir, Constants.CODE_XML_FILE_NAME);
		try {
			File snapshotFile = File.createTempFile("redo_", ".xml", undoDir);
			String snapshotName = snapshotFile.getName();

			StorageOperations.copyFile(currentCodeFile, snapshotFile);
			if (redoStack.size() >= MAX_UNDO_STEPS) {
				UndoEntry oldest = redoStack.remove(0);
				File oldestFile = new File(undoDir, oldest.snapshotFileName);
				if (oldestFile.exists() && !oldestFile.delete()) {
					Log.w(TAG, "Failed to delete oldest redo snapshot: " + oldestFile.getAbsolutePath());
				}
			}
			VariableSnapshot variables = new VariableSnapshot(
					new ArrayList<>(userVariables), new ArrayList<>(multiplayerVariables),
					new ArrayList<>(userLists), new ArrayList<>(localUserVariables),
					new ArrayList<>(localLists));
			UndoEntry entry = new UndoEntry(snapshotName, sceneName, spriteName, variables);
			redoStack.add(entry);
			return entry;
		} catch (IOException e) {
			Log.e(TAG, "Failed to push redo state", e);
			return null;
		}
	}

	private UndoEntry pushCurrentToUndoInternal(String sceneName, String spriteName,
						List<UserVariable> userVariables, List<UserVariable> multiplayerVariables,
						List<UserList> userLists, List<UserVariable> localUserVariables,
						List<UserList> localLists) {
		File currentCodeFile = new File(projectDir, Constants.CODE_XML_FILE_NAME);
		try {
			File snapshotFile = File.createTempFile("snap_", ".xml", undoDir);
			String snapshotName = snapshotFile.getName();

			StorageOperations.copyFile(currentCodeFile, snapshotFile);
			if (undoStack.size() >= MAX_UNDO_STEPS) {
				UndoEntry oldest = undoStack.remove(0);
				File oldestFile = new File(undoDir, oldest.snapshotFileName);
				if (oldestFile.exists() && !oldestFile.delete()) {
					Log.w(TAG, "Failed to delete oldest undo internal snapshot: " + oldestFile.getAbsolutePath());
				}
			}
			VariableSnapshot variables = new VariableSnapshot(
					new ArrayList<>(userVariables), new ArrayList<>(multiplayerVariables),
					new ArrayList<>(userLists), new ArrayList<>(localUserVariables),
					new ArrayList<>(localLists));
			UndoEntry entry = new UndoEntry(snapshotName, sceneName, spriteName, variables);
			undoStack.add(entry);
			return entry;
		} catch (IOException e) {
			Log.e(TAG, "Failed to push undo internal state", e);
			return null;
		}
	}

	private boolean restoreSnapshot(UndoEntry entry) {
		File snapshotFile = new File(undoDir, entry.snapshotFileName);
		File currentCodeFile = new File(projectDir, Constants.CODE_XML_FILE_NAME);
		try {
			StorageOperations.copyFile(snapshotFile, currentCodeFile);
			return true;
		} catch (IOException e) {
			Log.e(TAG, "Failed to restore snapshot " + entry.snapshotFileName, e);
			return false;
		}
	}

	public boolean canUndo() {
		return !undoStack.isEmpty();
	}

	public boolean canRedo() {
		return !redoStack.isEmpty();
	}

	public void clearHistory() {
		undoStack.clear();
		redoStack.clear();
		File[] files = undoDir.listFiles();
		if (files != null) {
			for (File file : files) {
				if (file.exists() && !file.delete()) {
					Log.w(TAG, "Failed to delete snapshot file: " + file.getAbsolutePath());
				}
			}
		}
	}
}

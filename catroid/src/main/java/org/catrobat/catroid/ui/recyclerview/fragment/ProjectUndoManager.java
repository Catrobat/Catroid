package org.catrobat.catroid.ui.recyclerview.fragment;

import android.util.Log;

import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.io.StorageOperations;
import org.catrobat.catroid.formulaeditor.UserList;
import org.catrobat.catroid.formulaeditor.UserVariable;

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

	public static class UndoEntry {
		public final String snapshotFileName;
		public final String sceneName;
		public final String spriteName;
		public final List<UserVariable> savedUserVariables;
		public final List<UserVariable> savedMultiplayerVariables;
		public final List<UserList> savedUserLists;
		public final List<UserVariable> savedLocalUserVariables;
		public final List<UserList> savedLocalLists;

		public UndoEntry(String snapshotFileName, String sceneName, String spriteName,
						List<UserVariable> userVariables, List<UserVariable> multiplayerVariables,
						List<UserList> userLists, List<UserVariable> localUserVariables,
						List<UserList> localLists) {
			this.snapshotFileName = snapshotFileName;
			this.sceneName = sceneName;
			this.spriteName = spriteName;
			this.savedUserVariables = userVariables;
			this.savedMultiplayerVariables = multiplayerVariables;
			this.savedUserLists = userLists;
			this.savedLocalUserVariables = localUserVariables;
			this.savedLocalLists = localLists;
		}
	}

	public ProjectUndoManager(File projectDir) {
		this.projectDir = projectDir;
		this.undoDir = new File(projectDir, Constants.UNDO_DIRECTORY_NAME);
		if (!undoDir.exists()) {
			undoDir.mkdirs();
		} else {
			clearHistory();
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

		String snapshotName = "snap_" + System.currentTimeMillis() + ".xml";
		File snapshotFile = new File(undoDir, snapshotName);

		try {
			StorageOperations.copyFile(currentCodeFile, snapshotFile);
			undoStack.add(new UndoEntry(snapshotName, sceneName, spriteName,
					userVariables, multiplayerVariables, userLists, localUserVariables, localLists));
			redoStack.clear();

			if (undoStack.size() > MAX_UNDO_STEPS) {
				UndoEntry oldest = undoStack.remove(0);
				new File(undoDir, oldest.snapshotFileName).delete();
			}
		} catch (IOException e) {
			Log.e(TAG, "Failed to push undo state", e);
		}
	}

	public UndoEntry popUndo(List<UserVariable> userVariables, List<UserVariable> multiplayerVariables,
						List<UserList> userLists, List<UserVariable> localUserVariables,
						List<UserList> localLists) {
		if (undoStack.isEmpty()) {
			return null;
		}

		pushCurrentToRedo(userVariables, multiplayerVariables, userLists, localUserVariables, localLists);

		UndoEntry entry = undoStack.remove(undoStack.size() - 1);
		restoreSnapshot(entry);
		return entry;
	}

	public UndoEntry popRedo(List<UserVariable> userVariables, List<UserVariable> multiplayerVariables,
						List<UserList> userLists, List<UserVariable> localUserVariables,
						List<UserList> localLists) {
		if (redoStack.isEmpty()) {
			return null;
		}

		pushCurrentToUndoInternal(userVariables, multiplayerVariables, userLists, localUserVariables, localLists);

		UndoEntry entry = redoStack.remove(redoStack.size() - 1);
		restoreSnapshot(entry);
		return entry;
	}

	private void pushCurrentToRedo(List<UserVariable> userVariables, List<UserVariable> multiplayerVariables,
						List<UserList> userLists, List<UserVariable> localUserVariables,
						List<UserList> localLists) {
		File currentCodeFile = new File(projectDir, Constants.CODE_XML_FILE_NAME);
		String snapshotName = "redo_" + System.currentTimeMillis() + ".xml";
		File snapshotFile = new File(undoDir, snapshotName);
		try {
			StorageOperations.copyFile(currentCodeFile, snapshotFile);
			redoStack.add(new UndoEntry(snapshotName, null, null,
					userVariables, multiplayerVariables, userLists, localUserVariables, localLists)); 
		} catch (IOException e) {
			Log.e(TAG, "Failed to push redo state", e);
		}
	}

	private void pushCurrentToUndoInternal(List<UserVariable> userVariables, List<UserVariable> multiplayerVariables,
						List<UserList> userLists, List<UserVariable> localUserVariables,
						List<UserList> localLists) {
		File currentCodeFile = new File(projectDir, Constants.CODE_XML_FILE_NAME);
		String snapshotName = "snap_" + System.currentTimeMillis() + ".xml";
		File snapshotFile = new File(undoDir, snapshotName);
		try {
			StorageOperations.copyFile(currentCodeFile, snapshotFile);
			undoStack.add(new UndoEntry(snapshotName, null, null,
					userVariables, multiplayerVariables, userLists, localUserVariables, localLists));
		} catch (IOException e) {
			Log.e(TAG, "Failed to push undo internal state", e);
		}
	}

	private void restoreSnapshot(UndoEntry entry) {
		File snapshotFile = new File(undoDir, entry.snapshotFileName);
		File currentCodeFile = new File(projectDir, Constants.CODE_XML_FILE_NAME);
		try {
			StorageOperations.copyFile(snapshotFile, currentCodeFile);
		} catch (IOException e) {
			Log.e(TAG, "Failed to restore snapshot " + entry.snapshotFileName, e);
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
				file.delete();
			}
		}
	}
}

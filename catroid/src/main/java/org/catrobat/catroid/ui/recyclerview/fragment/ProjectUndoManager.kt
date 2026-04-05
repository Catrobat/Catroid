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

import android.util.Log
import org.catrobat.catroid.common.Constants
import org.catrobat.catroid.formulaeditor.UserList
import org.catrobat.catroid.formulaeditor.UserVariable
import org.catrobat.catroid.io.StorageOperations
import java.io.File
import java.io.IOException

class ProjectUndoManager(private val projectDir: File) {

	private val undoDir: File = File(projectDir, Constants.UNDO_DIRECTORY_NAME)
	private val undoStack = mutableListOf<UndoEntry>()
	private val redoStack = mutableListOf<UndoEntry>()

	data class VariableSnapshot(
		@JvmField val savedUserVariables: List<UserVariable>,
		@JvmField val savedMultiplayerVariables: List<UserVariable>,
		@JvmField val savedUserLists: List<UserList>,
		@JvmField val savedLocalUserVariables: List<UserVariable>,
		@JvmField val savedLocalLists: List<UserList>
	)

	data class UndoEntry(
		@JvmField val snapshotFileName: String,
		@JvmField val sceneName: String,
		@JvmField val spriteName: String,
		@JvmField val variableSnapshot: VariableSnapshot
	)

	init {
		if (undoDir.exists()) {
			undoDir.listFiles()?.forEach { file ->
				if (file.exists() && !file.delete()) {
					Log.w(TAG, "Failed to delete stale snapshot on init: ${file.absolutePath}")
				}
			}
		} else if (!undoDir.mkdirs()) {
			Log.e(TAG, "Failed to create undo history directory: ${undoDir.absolutePath}")
		}
	}

	fun pushState(
		sceneName: String,
		spriteName: String,
		userVariables: List<UserVariable>,
		multiplayerVariables: List<UserVariable>,
		userLists: List<UserList>,
		localUserVariables: List<UserVariable>,
		localLists: List<UserList>
	) {
		val currentCodeFile = File(projectDir, Constants.CODE_XML_FILE_NAME)
		if (!currentCodeFile.exists()) {
			return
		}

		var snapshotFile: File? = null
		try {
			snapshotFile = File.createTempFile("snap_", ".xml", undoDir)
			val snapshotName = snapshotFile.name

			StorageOperations.copyFile(currentCodeFile, snapshotFile)
			val variables = VariableSnapshot(
				ArrayList(userVariables), ArrayList(multiplayerVariables),
				ArrayList(userLists), ArrayList(localUserVariables),
				ArrayList(localLists)
			)
			undoStack.add(UndoEntry(snapshotName, sceneName, spriteName, variables))
			for (redoEntry in redoStack) {
				val redoFile = File(undoDir, redoEntry.snapshotFileName)
				if (redoFile.exists() && !redoFile.delete()) {
					Log.w(TAG, "Failed to delete redo snapshot: ${redoFile.absolutePath}")
				}
			}
			redoStack.clear()

			if (undoStack.size > MAX_UNDO_STEPS) {
				val oldest = undoStack.removeAt(0)
				val oldestFile = File(undoDir, oldest.snapshotFileName)
				if (oldestFile.exists() && !oldestFile.delete()) {
					Log.w(TAG, "Failed to delete oldest snapshot: ${oldestFile.absolutePath}")
				}
			}
		} catch (e: IOException) {
			if (snapshotFile != null && snapshotFile.exists() && !snapshotFile.delete()) {
				Log.w(TAG, "Failed to delete orphaned snapshot: ${snapshotFile.absolutePath}")
			}
			Log.e(TAG, "Failed to push undo state", e)
		}
	}

	fun popUndo(
		sceneName: String,
		spriteName: String,
		userVariables: List<UserVariable>,
		multiplayerVariables: List<UserVariable>,
		userLists: List<UserList>,
		localUserVariables: List<UserVariable>,
		localLists: List<UserList>
	): UndoEntry? {
		if (undoStack.isEmpty()) {
			return null
		}

		val redoEntry = pushCurrentToRedo(
			sceneName, spriteName,
			userVariables, multiplayerVariables, userLists, localUserVariables, localLists
		) ?: return null

		val entry = undoStack.removeAt(undoStack.size - 1)
		return if (restoreSnapshot(entry)) {
			val snapshotFile = File(undoDir, entry.snapshotFileName)
			if (snapshotFile.exists() && !snapshotFile.delete()) {
				Log.w(TAG, "Failed to delete undo snapshot file: ${snapshotFile.absolutePath}")
			}
			entry
		} else {
			undoStack.add(entry)
			redoStack.removeAt(redoStack.size - 1)
			val redoFile = File(undoDir, redoEntry.snapshotFileName)
			if (redoFile.exists() && !redoFile.delete()) {
				Log.w(TAG, "Failed to delete redundant redo snapshot: ${redoFile.absolutePath}")
			}
			null
		}
	}

	fun popRedo(
		sceneName: String,
		spriteName: String,
		userVariables: List<UserVariable>,
		multiplayerVariables: List<UserVariable>,
		userLists: List<UserList>,
		localUserVariables: List<UserVariable>,
		localLists: List<UserList>
	): UndoEntry? {
		if (redoStack.isEmpty()) {
			return null
		}

		val undoEntry = pushCurrentToUndoInternal(
			sceneName, spriteName,
			userVariables, multiplayerVariables, userLists, localUserVariables, localLists
		) ?: return null

		val entry = redoStack.removeAt(redoStack.size - 1)
		return if (restoreSnapshot(entry)) {
			val snapshotFile = File(undoDir, entry.snapshotFileName)
			if (snapshotFile.exists() && !snapshotFile.delete()) {
				Log.w(TAG, "Failed to delete redo snapshot file: ${snapshotFile.absolutePath}")
			}
			entry
		} else {
			redoStack.add(entry)
			undoStack.removeAt(undoStack.size - 1)
			val undoFile = File(undoDir, undoEntry.snapshotFileName)
			if (undoFile.exists() && !undoFile.delete()) {
				Log.w(TAG, "Failed to delete redundant undo snapshot: ${undoFile.absolutePath}")
			}
			null
		}
	}

	private fun pushCurrentToRedo(
		sceneName: String,
		spriteName: String,
		userVariables: List<UserVariable>,
		multiplayerVariables: List<UserVariable>,
		userLists: List<UserList>,
		localUserVariables: List<UserVariable>,
		localLists: List<UserList>
	): UndoEntry? {
		val currentCodeFile = File(projectDir, Constants.CODE_XML_FILE_NAME)
		var snapshotFile: File? = null
		return try {
			snapshotFile = File.createTempFile("redo_", ".xml", undoDir)
			val snapshotName = snapshotFile.name

			StorageOperations.copyFile(currentCodeFile, snapshotFile)
			if (redoStack.size >= MAX_UNDO_STEPS) {
				val oldest = redoStack.removeAt(0)
				val oldestFile = File(undoDir, oldest.snapshotFileName)
				if (oldestFile.exists() && !oldestFile.delete()) {
					Log.w(TAG, "Failed to delete oldest redo snapshot: ${oldestFile.absolutePath}")
				}
			}
			val variables = VariableSnapshot(
				ArrayList(userVariables), ArrayList(multiplayerVariables),
				ArrayList(userLists), ArrayList(localUserVariables),
				ArrayList(localLists)
			)
			val entry = UndoEntry(snapshotName, sceneName, spriteName, variables)
			redoStack.add(entry)
			entry
		} catch (e: IOException) {
			if (snapshotFile != null && snapshotFile.exists() && !snapshotFile.delete()) {
				Log.w(TAG, "Failed to delete orphaned redo snapshot: ${snapshotFile.absolutePath}")
			}
			Log.e(TAG, "Failed to push redo state", e)
			null
		}
	}

	private fun pushCurrentToUndoInternal(
		sceneName: String,
		spriteName: String,
		userVariables: List<UserVariable>,
		multiplayerVariables: List<UserVariable>,
		userLists: List<UserList>,
		localUserVariables: List<UserVariable>,
		localLists: List<UserList>
	): UndoEntry? {
		val currentCodeFile = File(projectDir, Constants.CODE_XML_FILE_NAME)
		var snapshotFile: File? = null
		return try {
			snapshotFile = File.createTempFile("snap_", ".xml", undoDir)
			val snapshotName = snapshotFile.name

			StorageOperations.copyFile(currentCodeFile, snapshotFile)
			if (undoStack.size >= MAX_UNDO_STEPS) {
				val oldest = undoStack.removeAt(0)
				val oldestFile = File(undoDir, oldest.snapshotFileName)
				if (oldestFile.exists() && !oldestFile.delete()) {
					Log.w(TAG, "Failed to delete oldest undo internal snapshot: ${oldestFile.absolutePath}")
				}
			}
			val variables = VariableSnapshot(
				ArrayList(userVariables), ArrayList(multiplayerVariables),
				ArrayList(userLists), ArrayList(localUserVariables),
				ArrayList(localLists)
			)
			val entry = UndoEntry(snapshotName, sceneName, spriteName, variables)
			undoStack.add(entry)
			entry
		} catch (e: IOException) {
			if (snapshotFile != null && snapshotFile.exists() && !snapshotFile.delete()) {
				Log.w(TAG, "Failed to delete orphaned undo snapshot: ${snapshotFile.absolutePath}")
			}
			Log.e(TAG, "Failed to push undo internal state", e)
			null
		}
	}

	private fun restoreSnapshot(entry: UndoEntry): Boolean {
		val snapshotFile = File(undoDir, entry.snapshotFileName)
		val currentCodeFile = File(projectDir, Constants.CODE_XML_FILE_NAME)
		return try {
			StorageOperations.copyFile(snapshotFile, currentCodeFile)
			true
		} catch (e: IOException) {
			Log.e(TAG, "Failed to restore snapshot ${entry.snapshotFileName}", e)
			false
		}
	}

	fun canUndo(): Boolean = undoStack.isNotEmpty()

	fun canRedo(): Boolean = redoStack.isNotEmpty()

	fun clearHistory() {
		undoStack.clear()
		redoStack.clear()
		undoDir.listFiles()?.forEach { file ->
			if (file.exists() && !file.delete()) {
				Log.w(TAG, "Failed to delete snapshot file: ${file.absolutePath}")
			}
		}
	}

	companion object {
		private val TAG = ProjectUndoManager::class.java.simpleName
		private const val MAX_UNDO_STEPS = 20

		@JvmStatic
		fun clearUndoHistoryForProject(projectDir: File) {
			val undoDir = File(projectDir, Constants.UNDO_DIRECTORY_NAME)
			if (undoDir.exists()) {
				undoDir.listFiles()?.forEach { file ->
					if (file.exists() && !file.delete()) {
						Log.w(TAG, "Failed to delete project snapshot file: ${file.absolutePath}")
					}
				}
				if (undoDir.exists() && !undoDir.delete()) {
					Log.w(TAG, "Failed to delete undo directory: ${undoDir.absolutePath}")
				}
			}
		}
	}
}

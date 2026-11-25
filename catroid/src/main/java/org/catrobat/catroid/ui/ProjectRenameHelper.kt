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

package org.catrobat.catroid.ui

import android.content.Context
import android.preference.PreferenceManager
import android.util.Log
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.common.Constants
import org.catrobat.catroid.io.XstreamSerializer
import org.catrobat.catroid.io.XstreamSerializer.SaveResult
import org.catrobat.catroid.io.asynctask.loadProject
import org.catrobat.catroid.io.asynctask.saveProjectSerialWithResult
import org.catrobat.catroid.ui.shortcut.ShortcutHelper
import org.catrobat.catroid.utils.Utils
import java.io.File
import org.catrobat.catroid.io.asynctask.renameProject as renameProjectOnDisk

/**
 * The one place where a project is renamed. Used by the project list, the project options
 * and the pin-to-home-screen dialog, so all of them behave the same.
 */
object ProjectRenameHelper {

    private val TAG = ProjectRenameHelper::class.java.simpleName

    /**
     * Renames a project and everything that refers to it by name:
     *
     * 1. If the project is the one currently open, it is saved first, so unsaved edits move
     *    with it. If that save fails, the rename is aborted.
     * 2. The folder is moved and `<programName>` in code.xml is rewritten. If the rewrite
     *    fails, the folder is moved back.
     * 3. If the project is open, it is reloaded from the renamed folder, so every file
     *    reference points to the new folder. The edited scene and sprite are restored.
     * 4. The last used project and all shortcuts of the project follow the new name.
     *
     * When this returns null, nothing was changed. This does disk I/O and blocks.
     *
     * @return the renamed project directory, or null if the rename failed.
     */
    @JvmStatic
    fun renameProject(
        context: Context,
        projectDirectory: File,
        oldName: String,
        newName: String
    ): File? {
        val appContext = context.applicationContext ?: context
        val renamedDirectory = synchronized(XstreamSerializer.getSaveLock()) {
            renameAndReload(appContext, projectDirectory, oldName, newName)
        } ?: return null

        updateLastUsedProjectName(appContext, oldName, newName)
        ShortcutHelper.updateShortcutOnRename(appContext, oldName, newName)
        return renamedDirectory
    }

    /**
     * Runs [renameProject] on [dispatcher]. A rename that has started always finishes, even if
     * the caller's scope is cancelled, so the project and its shortcuts are never left half
     * renamed. A cancelled caller still does not receive the result, so no UI code runs on a
     * destroyed screen.
     */
    suspend fun renameProjectAsync(
        context: Context,
        projectDirectory: File,
        oldName: String,
        newName: String,
        dispatcher: CoroutineDispatcher = Dispatchers.IO
    ): File? = withContext(dispatcher + NonCancellable) {
        renameProject(context, projectDirectory, oldName, newName)
    }

    private fun renameAndReload(
        context: Context,
        projectDirectory: File,
        oldName: String,
        newName: String
    ): File? {
        val projectManager = ProjectManager.getInstance()
        val openProject = projectManager.currentProject
            ?.takeIf { it.directory?.absolutePath == projectDirectory.absolutePath }
        val editedScene = projectManager.currentlyEditedScene
        val currentSprite = projectManager.currentSprite

        // Unsaved edits would be lost by the reload below, so a failed save aborts the rename
        if (openProject != null &&
            saveProjectSerialWithResult(openProject, context) == SaveResult.FAILED
        ) {
            Log.e(TAG, "Cannot save $oldName before renaming it, rename aborted")
            return null
        }

        val renamedDirectory = renameProjectOnDisk(projectDirectory, newName) ?: return null
        if (openProject == null) {
            return renamedDirectory
        }

        if (!loadProject(renamedDirectory, context)) {
            Log.e(TAG, "Cannot reload renamed project, moving it back to $oldName")
            if (renameProjectOnDisk(renamedDirectory, oldName) == null) {
                Log.e(TAG, "Rollback of ${renamedDirectory.name} failed")
            }
            projectManager.currentProject = openProject
            projectManager.currentlyEditedScene = editedScene
            projectManager.currentSprite = currentSprite
            return null
        }
        restoreEditingState(editedScene?.name, currentSprite?.name)

        // A save that still holds the old object, such as an onPause save waiting for the
        // lock, must write to the renamed folder instead of recreating the old one. The
        // reloaded project stays the one in use.
        openProject.name = newName
        openProject.directory = renamedDirectory
        return renamedDirectory
    }

    /** Selects the scene and sprite of the reloaded project that match the given names. */
    private fun restoreEditingState(editedSceneName: String?, currentSpriteName: String?) {
        val projectManager = ProjectManager.getInstance()
        val project = projectManager.currentProject ?: return
        val scene = editedSceneName?.let { project.getSceneByName(it) } ?: project.defaultScene
        projectManager.currentlyEditedScene = scene
        projectManager.currentSprite = currentSpriteName?.let { scene?.getSprite(it) }
    }

    private fun updateLastUsedProjectName(context: Context, oldName: String, newName: String) {
        val lastUsedName = PreferenceManager.getDefaultSharedPreferences(context)
            .getString(Constants.PREF_PROJECTNAME_KEY, null)
        if (lastUsedName == oldName) {
            Utils.setLastUsedProjectName(context, newName)
        }
    }
}

/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2023 The Catrobat Team
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
package org.catrobat.catroid.io.asynctask

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.exceptions.ProjectException
import org.catrobat.catroid.io.DeviceListAccessor
import org.catrobat.catroid.io.DeviceVariableAccessor
import org.catrobat.catroid.io.LookFileGarbageCollector
import org.koin.java.KoinJavaComponent.inject
import java.io.File
import java.lang.ref.WeakReference

class ProjectLoader(private var projectDir: File) {
    private var weakListenerReference: WeakReference<ProjectLoadListener>? = null

    companion object {
        val TAG = ProjectLoader::class.java.simpleName
    }

    fun setListener(listener: ProjectLoadListener): ProjectLoader {
        weakListenerReference = WeakReference(listener)
        return this
    }

    @JvmOverloads
    fun loadProjectAsync(scope: CoroutineScope = CoroutineScope(Dispatchers.IO)) {
        scope.launch {
            val projectSaved = loadProject(projectDir)

            withContext(Dispatchers.Main) {
                val listener = weakListenerReference?.get() ?: return@withContext
                listener.onLoadFinished(projectSaved)
            }
        }
    }

    interface ProjectLoadListener {
        fun onLoadFinished(success: Boolean)
    }
}

fun loadProject(projectDir: File?): Boolean {
    return try {
        val projectManager: ProjectManager by inject(ProjectManager::class.java)
        projectManager.loadProject(projectDir)
        val project = projectManager.currentProject
        DeviceVariableAccessor(projectDir).cleanUpDeletedUserData(project)
        DeviceListAccessor(projectDir).cleanUpDeletedUserData(project)
        LookFileGarbageCollector().cleanUpUnusedLookFiles(project)
        true
    } catch (e: ProjectException) {
        projectDir ?: return false
        Log.e(ProjectLoader.TAG, "Cannot load project in " + projectDir.absolutePath, e)
        false
    }
}

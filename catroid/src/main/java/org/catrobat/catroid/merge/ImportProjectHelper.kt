/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2021 The Catrobat Team
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
package org.catrobat.catroid.merge

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.util.Log
import org.catrobat.catroid.ProjectManager.checkForVariablesConflicts
import org.catrobat.catroid.R
import org.catrobat.catroid.common.Constants
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Scene
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.formulaeditor.UserList
import org.catrobat.catroid.formulaeditor.UserVariable
import org.catrobat.catroid.io.StorageOperations
import org.catrobat.catroid.io.XstreamSerializer
import org.catrobat.catroid.io.ZipArchiver
import org.catrobat.catroid.utils.ToastUtil
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException

class ImportProjectHelper(
    private val lookFileName: String,
    contentResolver: ContentResolver,
    uri: Uri,
    private val currentScene: Scene,
    private val context: Context
) {
    companion object {
        private val TAG = ImportProjectHelper::class.simpleName
    }

    private var newSprite: Sprite = Sprite("Sprite")
    private var spriteToAdd: Sprite? = null
    private var newProject: Project? = null

    fun addObjectDataToSprite(): Sprite {
        copyFilesToSoundAndSpriteDir()
        newSprite.replaceSpriteWithSprite(spriteToAdd)
        newProject?.let { currentScene.project?.userLists?.addAll(it.userLists) }
        newProject?.let { currentScene.project?.userVariables?.addAll(it.userVariables) }
        return newSprite
    }

    fun checkForConflicts(): Boolean {
        val conflicts: ArrayList<String> = ArrayList()

        if (newProject == null || spriteToAdd == null) {
            return false
        }

        checkForVariablesConflicts(
            currentScene.project?.userLists as List<Any>?,
            spriteToAdd?.userLists as List<Any>?
        ).forEach { elem ->
            conflicts.add((elem as UserList).name)
        }

        checkForVariablesConflicts(
            currentScene.project?.userVariables as List<Any>?,
            spriteToAdd?.userVariables as List<Any>?
        ).forEach { elem ->
            conflicts.add((elem as UserVariable).name)
        }

        currentScene.project?.sceneList?.forEach { scene ->
            scene.spriteList?.forEach { sprite ->
                checkForVariablesConflicts(
                    newProject?.userLists as List<Any>?,
                    sprite.userLists as List<Any>?
                ).forEach { elem -> conflicts.add((elem as UserList).name) }
                checkForVariablesConflicts(
                    newProject?.userVariables as List<Any>?,
                    sprite.userVariables as List<Any>?
                ).forEach { elem -> conflicts.add((elem as UserVariable).name) }
            }
        }

        if (conflicts.size > 0) {
            rejectImportDialog()
            return false
        }
        return true
    }

    private fun copyFilesToSoundAndSpriteDir() {
        val imageDirectory = File(
            currentScene.directory,
            Constants.IMAGE_DIRECTORY_NAME
        )
        val soundsDirectory = File(
            currentScene.directory,
            Constants.SOUND_DIRECTORY_NAME
        )

        spriteToAdd?.lookList?.forEach { currentListObject ->
            StorageOperations.copyFileToDir(
                currentListObject.file,
                imageDirectory
            )
        }
        spriteToAdd?.soundList?.forEach { currentListObject ->
            StorageOperations.copyFileToDir(
                currentListObject.file,
                soundsDirectory
            )
        }
    }

    fun getNewProject(resolvedName: String): Project? {
        try {
            val cachedProjectDir =
                File(Constants.MEDIA_LIBRARY_CACHE_DIR, resolvedName)
            val cachedProject =
                File(Constants.MEDIA_LIBRARY_CACHE_DIR, lookFileName)

            ZipArchiver().unzip(cachedProject, cachedProjectDir)
            return XstreamSerializer.getInstance()
                .loadProject(cachedProjectDir, context)
        } catch (e: IOException) {
            Log.e(TAG, Log.getStackTraceString(e))
        } catch (e: FileNotFoundException) {
            Log.e(TAG, Log.getStackTraceString(e))
        }
        rejectImportDialog()
        return null
    }

    fun rejectImportDialog() {
        ToastUtil.showError(context, R.string.reject_import)
    }

    init {
        val resolvedFileName = StorageOperations.resolveFileName(contentResolver, uri)
        val resolvedName = StorageOperations.getSanitizedFileName(resolvedFileName)

        if (lookFileName == resolvedName + Constants.CATROBAT_EXTENSION) {
            val project = getNewProject(resolvedName)
            val firstScene = project?.defaultScene
            if (project == null || firstScene!!.spriteList.size < 2) {
                rejectImportDialog()
            } else {
                newProject = project
                spriteToAdd = firstScene.spriteList[1]
            }
        } else {
            rejectImportDialog()
        }
    }
}

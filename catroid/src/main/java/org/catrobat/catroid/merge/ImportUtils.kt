/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2022 The Catrobat Team
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

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.annotation.VisibleForTesting
import androidx.fragment.app.FragmentActivity
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.R
import org.catrobat.catroid.common.Constants
import org.catrobat.catroid.common.FlavoredConstants
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Scene
import org.catrobat.catroid.io.StorageOperations
import org.catrobat.catroid.io.XstreamSerializer
import org.catrobat.catroid.io.ZipArchiver
import org.catrobat.catroid.ui.ProjectActivity.Companion.SPRITE_FROM_LOCAL
import org.catrobat.catroid.ui.ProjectActivity.Companion.SPRITE_OBJECT
import org.catrobat.catroid.ui.recyclerview.dialog.RejectImportDialogFragment
import org.catrobat.catroid.ui.recyclerview.dialog.RejectImportDialogFragment.Companion.CONFLICT_PROJECT_NAME
import org.catrobat.catroid.ui.recyclerview.fragment.SpriteListFragment.Companion.IMPORT_LOCAL_OBJECT
import org.catrobat.catroid.ui.recyclerview.fragment.SpriteListFragment.Companion.IMPORT_MEDIA_OBJECT
import org.catrobat.catroid.ui.recyclerview.util.UniqueNameProvider
import org.catrobat.catroid.utils.ToastUtil
import org.koin.java.KoinJavaComponent.inject
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException

class ImportUtils(val context: Context) {
    companion object {
        val TAG: String = ImportUtils::class.java.simpleName
    }

    private var projectManager: ProjectManager = inject(ProjectManager::class.java).value
    private var currentScene = projectManager.currentlyEditedScene

    fun processImportData(uri: Uri, requestCode: Int, importData: Bundle?):
        List<ImportSpriteData>? {
        val resolvedFileName = StorageOperations.resolveFileName(context.contentResolver, uri)
        val useDefaultSpriteName = resolvedFileName == null || StorageOperations
            .getSanitizedFileName(resolvedFileName) == Constants.TMP_IMAGE_FILE_NAME
        val (resolvedName, lookFileName) = createFileNames(useDefaultSpriteName, resolvedFileName)
        val sourceProject = getProject(resolvedName, lookFileName)
        if (sourceProject == null) {
            RejectImportDialogFragment(null, CONFLICT_PROJECT_NAME).show(
                (context as FragmentActivity).supportFragmentManager,
                RejectImportDialogFragment.TAG
            )
            return null
        }

        return when (requestCode) {
            SPRITE_FROM_LOCAL -> processLocalImport(importData, lookFileName, sourceProject, true)
            IMPORT_LOCAL_OBJECT -> processLocalImport(
                importData,
                lookFileName,
                sourceProject,
                false
            )
            SPRITE_OBJECT -> processMediaImport(lookFileName, sourceProject, true)
            IMPORT_MEDIA_OBJECT -> processMediaImport(lookFileName, sourceProject, false)
            else -> null
        }
    }

    @VisibleForTesting(otherwise = MODE_PRIVATE)
    fun processMediaImport(
        lookFileName: String,
        sourceProject: Project,
        isObject: Boolean
    ): List<ImportSpriteData>? {
        val spriteToImport = sourceProject.defaultScene.spriteList?.get(1) ?: return null
        val lookDataName = UniqueNameProvider().getUniqueNameInNameables(
            spriteToImport.name,
            currentScene.spriteList
        )

        val importSpriteData = listOf(
            ImportSpriteData(
                spriteToImport,
                sourceProject,
                false,
                lookDataName,
                lookFileName,
                isObject
            )
        )

        val conflicts = ImportVariablesManager.validateVariableConflictsForImport(
            listOf(spriteToImport), sourceProject
        )
        if (conflicts.isNotEmpty()) {
            RejectImportDialogFragment(conflicts).show(
                (context as FragmentActivity).supportFragmentManager, RejectImportDialogFragment.TAG
            )
            return null
        }
        return importSpriteData
    }

    @VisibleForTesting(otherwise = MODE_PRIVATE)
    fun processLocalImport(
        importData: Bundle?,
        lookFileName: String,
        sourceProject: Project,
        isObject: Boolean
    ): ArrayList<ImportSpriteData>? {
        val sceneName = importData?.getString(
            ImportLocalObjectActivity
                .REQUEST_SCENE
        ) ?: return null
        val spriteNames = importData.getStringArrayList(
            ImportLocalObjectActivity
                .REQUEST_SPRITE
        ) ?: return null

        val importSpritesData: ArrayList<ImportSpriteData> =
            createNewSpritesData(
                spriteNames, lookFileName, sourceProject, sceneName,
                isObject
            )

        val conflicts = ImportVariablesManager.validateVariableConflictsForImport(
            importSpritesData.map { it.sprite }, sourceProject
        )
        if (conflicts.isNotEmpty()) {
            RejectImportDialogFragment(conflicts).show(
                (context as FragmentActivity).supportFragmentManager,
                RejectImportDialogFragment.TAG
            )
            return null
        }

        return importSpritesData
    }

    fun getProject(resolvedName: String, lookFileName: String): Project? {
        val projectDir = File(FlavoredConstants.DEFAULT_ROOT_DIRECTORY, resolvedName)
        return if (projectDir.exists() && projectDir.isDirectory) {
            XstreamSerializer.getInstance().loadProject(projectDir, context)
        } else {
            getNewProject(resolvedName, lookFileName)
        }
    }

    fun getNewProject(resolvedName: String, lookFileName: String): Project? {
        try {
            val cachedProjectDir =
                File(Constants.MEDIA_LIBRARY_CACHE_DIRECTORY, resolvedName)
            val cachedProject =
                File(Constants.MEDIA_LIBRARY_CACHE_DIRECTORY, lookFileName)

            ZipArchiver().unzip(cachedProject, cachedProjectDir)
            return XstreamSerializer.getInstance()
                .loadProject(cachedProjectDir, context)
        } catch (e: IOException) {
            Log.e(TAG, Log.getStackTraceString(e))
        } catch (e: FileNotFoundException) {
            Log.e(TAG, Log.getStackTraceString(e))
        }
       return null
    }

    fun showRejectToast() {
        ToastUtil.showError(context, R.string.reject_import)
    }

    fun createFileNames(
        useDefaultSpriteName: Boolean,
        resolvedFileName: String
    ): Pair<String, String> {
        return if (useDefaultSpriteName) {
            Pair(
                context.getString(R.string.default_sprite_name),
                context.getString(R.string.default_sprite_name) + Constants.CATROBAT_EXTENSION
            )
        } else {
            Pair(
                StorageOperations.getSanitizedFileName(resolvedFileName),
                resolvedFileName
            )
        }
    }

    private fun createNewLookDataName(name: String, currentScene: Scene): String {
        return UniqueNameProvider().getUniqueNameInNameables(
            name,
            currentScene.spriteList
        )
    }

    @VisibleForTesting(otherwise = MODE_PRIVATE)
    fun createNewSpritesData(
        spriteNames: ArrayList<String>,
        lookFileName: String,
        sourceProject: Project,
        sceneName: String,
        isObject: Boolean
    ): ArrayList<ImportSpriteData> {
        val data: ArrayList<ImportSpriteData> = ArrayList()
        val scene = sourceProject.getSceneByName(sceneName)
        val currentScene = projectManager.currentlyEditedScene
        spriteNames.forEach {
            data.add(
                ImportSpriteData(
                    scene.getSprite(it),
                    sourceProject,
                    false,
                    createNewLookDataName(it, currentScene),
                    lookFileName,
                    isObject
                )
            )
        }
        return data
    }
}

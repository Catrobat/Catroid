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
import org.catrobat.catroid.ui.recyclerview.dialog.RejectImportDialogFragment
import org.catrobat.catroid.ui.recyclerview.dialog.RejectImportDialogFragment.Companion.CONFLICT_PROJECT_NAME
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
            Constants.REQUEST_IMPORT_LOCAL_SPRITE,
            Constants.REQUEST_IMPORT_LOCAL_SCENE ->
                processLocalImport(importData, lookFileName, sourceProject)
            Constants.REQUEST_MERGE_LOCAL_SPRITE ->
                processLocalImport(importData, lookFileName, sourceProject)
            Constants.REQUEST_IMPORT_MEDIA_OBJECT ->
                processMediaImport(lookFileName, sourceProject)
            Constants.REQUEST_MERGE_MEDIA_OBJECT ->
                processMediaImport(lookFileName, sourceProject)
            else -> null
        }
    }

    @VisibleForTesting(otherwise = MODE_PRIVATE)
    fun processMediaImport(
        lookFileName: String,
        sourceProject: Project
    ): List<ImportSpriteData>? {
        val spriteToImport = sourceProject.defaultScene.spriteList?.get(1) ?: return null
        val lookDataName = UniqueNameProvider().getUniqueNameInNameables(spriteToImport.name,
            currentScene.spriteList
        )

        val importSpriteData = listOf(
            ImportSpriteData(
                spriteToImport,
                sourceProject,
                lookDataName,
                lookFileName,
                isGroup = false,
                isGroupItem = false
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
        sourceProject: Project
    ): ArrayList<ImportSpriteData>? {
        val sceneName = importData?.getString(Constants.EXTRA_SCENE_NAME) ?: return null
        val scene = sourceProject.getSceneByName(sceneName) ?: return null
        val spriteNames = importData.getStringArrayList(Constants.EXTRA_SPRITE_NAMES)
        val groupNames = importData.getStringArrayList(Constants.EXTRA_GROUP_SPRITE_NAMES)

        if (spriteNames.isNullOrEmpty() && groupNames.isNullOrEmpty()) return null

        val importSpritesData: ArrayList<ImportSpriteData> = createNewSpritesData(
            spriteNames,
            groupNames,
            lookFileName,
            scene
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
        spriteNames: ArrayList<String?>?,
        groupSprites: ArrayList<String?>?,
        lookFileName: String,
        scene: Scene
    ): ArrayList<ImportSpriteData> {
        val data: ArrayList<ImportSpriteData> = ArrayList()
        val currentScene = projectManager.currentlyEditedScene
        spriteNames?.forEach {
            if (it != null) {
                data.add(
                    ImportSpriteData(
                        scene.getSprite(it),
                        scene.project,
                        createNewLookDataName(it, currentScene),
                        lookFileName,
                        isGroup = false,
                        isGroupItem = false
                    )
                )
            }
        }

        groupSprites?.forEach {
            if (it != null) {
                data.add(
                    ImportSpriteData(
                        scene.getSprite(it),
                        scene.project,
                        createNewLookDataName(it, currentScene),
                        it,
                        isGroup = true,
                        isGroupItem = false
                    )
                )
            }
        }
        return data
    }
}

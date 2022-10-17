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

import android.app.Activity
import android.content.DialogInterface
import android.net.Uri
import android.text.SpannableStringBuilder
import android.text.style.BulletSpan
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import org.catrobat.catroid.ProjectManager.checkForVariablesConflicts
import org.catrobat.catroid.R
import org.catrobat.catroid.common.Constants
import org.catrobat.catroid.common.FlavoredConstants.DEFAULT_ROOT_DIRECTORY
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Scene
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.formulaeditor.UserList
import org.catrobat.catroid.formulaeditor.UserVariable
import org.catrobat.catroid.io.StorageOperations
import org.catrobat.catroid.io.XstreamSerializer
import org.catrobat.catroid.io.ZipArchiver
import org.catrobat.catroid.ui.recyclerview.util.UniqueNameProvider
import org.catrobat.catroid.utils.ToastUtil
import org.catrobat.catroid.utils.UserDataUtil.renameUserData
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException

private val TAG = ImportProjectHelper::class.simpleName
private const val DISPLAYED_CONFLICT_VARIABLE: Int = 3
private const val GAP_WIDTH: Int = 15

class ImportProjectHelper(
    internal var lookFileName: String,
    currentScene: Scene,
    private var context: Activity,
    internal var lookDataName: String? = null,
    internal var uri: Uri? = null
) {
    private var currentScene: Scene? = currentScene
    private var newSprite: Sprite = Sprite("Sprite")
    private var spriteToAdd: Sprite? = null
    private var newProject: Project? = null
    private var mergeProjectListener: MergeProjectListener? = null

    interface MergeProjectListener {
        fun onResolvedConflicts(importProjectHelper: ImportProjectHelper)
    }

    fun getSpriteToAddName(): String? = spriteToAdd?.name

    fun addObjectDataToNewSprite(spriteToAddTo: Sprite?): Sprite {
        copyFilesToSoundAndSpriteDir()
        if (spriteToAddTo == null) {
            newSprite.replaceSpriteWithSprite(spriteToAdd)
        } else {
            spriteToAddTo.mergeSprites(spriteToAdd)
        }
        newProject?.let {
            for (userList in it.userLists) {
                if (!currentScene?.project?.userLists!!.contains(userList)) {
                    currentScene?.project?.userLists?.add(userList)
                }
            }
        }
        newProject?.let {
            for (userVariable in it.userVariables) {
                if (!currentScene?.project?.userVariables!!.contains(userVariable)) {
                    currentScene?.project?.userVariables?.add(userVariable)
                }
            }
        }
        addGlobalsToProject(newProject!!.userLists, currentScene!!.project.userLists)
        addGlobalsToProject(newProject!!.userVariables, currentScene!!.project.userVariables)
        addGlobalsToProject(newProject!!.broadcastMessageContainer.broadcastMessages,
                            currentScene!!.project.broadcastMessageContainer.broadcastMessages)

        currentScene?.project?.broadcastMessageContainer?.update()
        return newSprite
    }

    private fun addGlobalsToProject(globalList: List<Any>, globalsToAdd: List<Any>) {
        for (global in globalsToAdd) {
            if (!globalList.contains(global)) {
                globalList.plus(global)
            }
        }
    }
    @SuppressWarnings("ComplexMethod")
    fun checkForConflicts(): Boolean {
        val conflicts: ArrayList<Any> = ArrayList()
        val allVariablesAndListNames: ArrayList<String> = ArrayList()
        if (newProject == null || spriteToAdd == null) {
            return false
        }

        checkForVariablesConflicts(
            currentScene?.project?.userLists as List<Any>?,
            spriteToAdd?.userLists as List<Any>?
        ).forEach { elem ->
            conflicts.add(elem as UserList)
        }
        spriteToAdd?.userLists?.forEach { elem: UserList ->
            allVariablesAndListNames.add(elem.name)
        }

        checkForVariablesConflicts(
            currentScene?.project?.userVariables as List<Any>?,
            spriteToAdd?.userVariables as List<Any>?
        ).forEach { elem ->
            conflicts.add(elem as UserVariable)
        }
        spriteToAdd?.userVariables?.forEach { elem: UserVariable ->
            allVariablesAndListNames.add(elem.name)
        }

        currentScene?.project?.sceneList?.forEach { scene ->
            scene.spriteList?.forEach { sprite ->
                checkForVariablesConflicts(
                    newProject?.userLists as List<Any>?,
                    sprite.userLists as List<Any>?
                ).forEach { elem -> conflicts.add(elem as UserList) }
                checkForVariablesConflicts(
                    newProject?.userVariables as List<Any>?,
                    sprite.userVariables as List<Any>?
                ).forEach { elem -> conflicts.add(elem as UserVariable) }
                currentScene?.project?.userLists?.forEach { elem: UserList ->
                    allVariablesAndListNames.add(elem.name)
                }
                currentScene?.project?.userVariables?.forEach { elem: UserVariable ->
                    allVariablesAndListNames.add(elem.name)
                }
                sprite.userLists.forEach { elem: UserList ->
                    allVariablesAndListNames.add(elem.name)
                }
                sprite.userVariables.forEach { elem: UserVariable ->
                    allVariablesAndListNames.add(elem.name)
                }
            }
        }

        if (conflicts.size > 0) {
            rejectImportDialog(conflicts, allVariablesAndListNames)
            return false
        }
        return true
    }

    private fun copyFilesToSoundAndSpriteDir() {
        val imageDirectory = File(
            currentScene?.directory,
            Constants.IMAGE_DIRECTORY_NAME
        )
        val soundsDirectory = File(
            currentScene?.directory,
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

    fun getProject(resolvedName: String): Project? {
        val projectDir = File(DEFAULT_ROOT_DIRECTORY, resolvedName)
        return if (projectDir.exists() && projectDir.isDirectory) {
            XstreamSerializer.getInstance().loadProject(projectDir, context)
        } else {
            getNewProject(resolvedName)
        }
    }

    fun getNewProject(resolvedName: String): Project? {
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
        rejectImportDialog(null, null)
        return null
    }

    fun rejectImportDialog(
        conflicts: ArrayList<Any>?,
        allVariablesAndListNames: ArrayList<String>?
    ) {
        if (conflicts == null || allVariablesAndListNames == null) {
            ToastUtil.showError(context, R.string.reject_import)
        } else {
            val view = View.inflate(context, R.layout.dialog_import_rejected, null)
            context.runOnUiThread {
                val alertDialog: AlertDialog = AlertDialog.Builder(context)
                    .setTitle(R.string.warning)
                    .setView(view)
                    .setPositiveButton(context.getString(R.string.ok)) { dialog: DialogInterface, _: Int ->
                        dialog.cancel()
                    }
                    .setNegativeButton(context.getString(R.string.merge_automatically)) {
                            dialog:
                    DialogInterface, _: Int ->
                        autoResolveConflicts(conflicts, allVariablesAndListNames)
                        mergeProjectListener?.onResolvedConflicts(this)
                        dialog.cancel()
                    }
                    .setCancelable(false)
                    .create()

                alertDialog.show()

                val conflictField = alertDialog.findViewById<TextView>(R.id.conflicting_variables)
                val numberOfIterations = minOf(conflicts.size, DISPLAYED_CONFLICT_VARIABLE)
                val content = SpannableStringBuilder()

                for (iterator in conflicts.withIndex().take(numberOfIterations)) {
                    val contentStart = content.length
                    when (iterator.value) {
                        is UserList ->
                            if (iterator.index < numberOfIterations - 1) {
                                content.append((iterator.value as? UserList)?.name + System.lineSeparator())
                            } else {
                                content.append((iterator.value as? UserList)?.name)
                            }
                        is UserVariable ->
                            if (iterator.index < numberOfIterations - 1) {
                                content.append((iterator.value as? UserVariable)?.name + System.lineSeparator())
                            } else {
                                content.append((iterator.value as? UserVariable)?.name)
                            }
                    }
                    content.setSpan(
                        BulletSpan(GAP_WIDTH), contentStart, content.length, 0
                    )
                }
                conflictField?.text = content
            }
        }
    }

    private fun autoResolveConflicts(conflicts: ArrayList<Any>, allVariablesAndListNames: ArrayList<String>) {
        val uniqueNameProvider = UniqueNameProvider()
        for (conflict in conflicts) {
            when (conflict) {
                is UserList -> {
                    val variable: UserList = (conflict as? UserList)!!
                    val name: String = uniqueNameProvider.getUniqueName(variable.name, allVariablesAndListNames)
                    spriteToAdd?.updateUserDataReferences(
                        variable.name,
                        name,
                        conflict
                    )
                    renameUserData(variable, name)
                }
                is UserVariable -> {
                    val variable: UserVariable = (conflict as? UserVariable)!!
                    val name: String = uniqueNameProvider.getUniqueName(variable.name, allVariablesAndListNames)
                    spriteToAdd?.updateUserDataReferences(
                        variable.name,
                        name,
                        conflict
                    )
                    renameUserData(variable, name)
                }
            }
        }
    }
    fun setMergeProjectListener(listener: MergeProjectListener) {
        this.mergeProjectListener = listener
    }

    init {
        val resolvedName = StorageOperations.getSanitizedFileName(lookFileName)
        val project = getProject(resolvedName)
        val firstScene = project?.defaultScene
        if (project == null || firstScene!!.spriteList.size < 2) {
            rejectImportDialog(null, null)
        } else {
            newProject = project
            spriteToAdd = firstScene.spriteList[1]
        }
    }
}

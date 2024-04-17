/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2024 The Catrobat Team
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
import android.text.SpannableStringBuilder
import android.text.style.BulletSpan
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.R
import org.catrobat.catroid.common.FlavoredConstants
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Scene
import org.catrobat.catroid.formulaeditor.UserList
import org.catrobat.catroid.formulaeditor.UserVariable
import org.catrobat.catroid.io.StorageOperations
import org.catrobat.catroid.io.XstreamSerializer
import org.catrobat.catroid.ui.recyclerview.controller.SceneController
import org.catrobat.catroid.ui.recyclerview.util.UniqueNameProvider
import org.catrobat.catroid.utils.ToastUtil
import java.io.File

private const val DISPLAYED_CONFLICT_VARIABLE: Int = 3
private const val GAP_WIDTH: Int = 15

class ImportSceneHelper(
    projectName: String,
    private var context: Activity,
    private var currentProject: Project,
    private var sceneName: String?,
) {
    var newProject: Project? = null
    private var sceneToAdd: Scene? = null

    init {
        val resolvedName = StorageOperations.getSanitizedFileName(projectName)
        newProject = getProject(resolvedName)
        sceneToAdd = newProject?.sceneList?.first()
        if (newProject == null || sceneToAdd == null) {
            rejectImportDialog(null)
        }
        if (sceneName == null) {
            sceneName = newProject?.sceneList?.get(0)?.name
        }
    }

    fun setSpecificScene(newSceneName: String?) {
        sceneName = newSceneName
        sceneToAdd = newProject?.getSceneByName(newSceneName)
    }

    fun importScene() {
        newProject?.let {
            for (userList in it.userLists) {
                if (!currentProject.userLists!!.contains(userList)) {
                    currentProject.userLists?.add(userList)
                }
            }
        }
        newProject?.let {
            for (userVariable in it.userVariables) {
                if (!currentProject.userVariables!!.contains(userVariable)) {
                    currentProject.userVariables?.add(userVariable)
                }
            }
        }

        addGlobalsToProject(newProject!!.userLists, currentProject.userLists)
        addGlobalsToProject(newProject!!.userVariables, currentProject.userVariables)
        addGlobalsToProject(
            newProject!!.broadcastMessageContainer.broadcastMessages,
            currentProject.broadcastMessageContainer.broadcastMessages
        )

        currentProject.broadcastMessageContainer?.update()

        val newSceneToAddName = UniqueNameProvider().getUniqueNameInNameables(
            sceneName,
            currentProject.sceneList
        )
        sceneToAdd?.name = newSceneToAddName

        val sceneController = SceneController()
        currentProject.addScene(sceneController.copy(sceneToAdd, currentProject))
    }

    fun getSceneCount(): Int? = newProject?.sceneList?.size

    fun getProject(resolvedName: String): Project? {
        val projectDir = File(FlavoredConstants.DEFAULT_ROOT_DIRECTORY, resolvedName)
        return if (projectDir.exists() && projectDir.isDirectory) {
            XstreamSerializer.getInstance()
                .loadProject(projectDir, context)
        } else {
            null
        }
    }

    fun addGlobalsToProject(globalList: List<Any>, globalsToAdd: List<Any>) {
        for (global in globalsToAdd) {
            if (!globalList.contains(global)) {
                globalList.plus(global)
            }
        }
    }

    fun checkForConflicts(): Boolean {
        val conflicts: ArrayList<String> = ArrayList()

        if (newProject == null || sceneToAdd == null) {
            return false
        }

        ProjectManager.checkForVariablesConflicts(
            currentProject.userLists as List<Any>?,
            newProject?.userLists as List<Any>?
        ).forEach { elem ->
            conflicts.add((elem as UserList).name)
        }

        ProjectManager.checkForVariablesConflicts(
            currentProject.userVariables as List<Any>?,
            newProject?.userVariables as List<Any>?
        ).forEach { elem ->
            conflicts.add((elem as UserVariable).name)
        }

        if (conflicts.size > 0) {
            rejectImportDialog(conflicts)
            return false
        }
        return true
    }

    fun rejectImportDialog(conflicts: ArrayList<String>?) {
        if (conflicts == null) {
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
                    .setCancelable(false)
                    .create()

                alertDialog.show()

                val conflictField = alertDialog.findViewById<TextView>(R.id.conflicting_variables)
                val numberOfIterations = minOf(conflicts.size, DISPLAYED_CONFLICT_VARIABLE)
                val content = SpannableStringBuilder()

                for (iterator in conflicts.withIndex().take(numberOfIterations)) {
                    val contentStart = content.length

                    if (iterator.index < numberOfIterations - 1) {
                        content.append(iterator.value + System.lineSeparator())
                    } else {
                        content.append(iterator.value)
                    }

                    content.setSpan(
                        BulletSpan(GAP_WIDTH), contentStart, content.length, 0
                    )
                }
                conflictField?.text = content
            }
        }
    }
}

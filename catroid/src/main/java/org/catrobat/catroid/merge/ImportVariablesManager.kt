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

import androidx.annotation.VisibleForTesting
import androidx.appcompat.app.AppCompatActivity
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.formulaeditor.UserList
import org.catrobat.catroid.formulaeditor.UserVariable
import org.koin.java.KoinJavaComponent.inject

object ImportVariablesManager {
    @JvmStatic
    fun validateVariableConflictsForImport(
        spritesToAdd: List<Sprite>?,
        sourceProject: Project?,
        currentProject: Project
    ): List<String> {
        val conflicts: ArrayList<String> = ArrayList()

        if (sourceProject == null || spritesToAdd == null) {
            return conflicts
        }

        spritesToAdd.forEach { sprite ->
            getVariableConflicts(
                currentProject.userLists,
                sprite.userLists
            ).forEach { elem ->
                conflicts.add((elem as UserList).name)
            }

            getVariableConflicts(
                currentProject.userVariables,
                sprite.userVariables
            ).forEach { elem ->
                conflicts.add((elem as UserVariable).name)
            }
        }

        currentProject.sceneList?.forEach { scene ->
            scene.spriteList?.forEach { sprite ->
                getVariableConflicts(
                    sourceProject.userLists,
                    sprite.userLists
                ).forEach { elem -> conflicts.add((elem as UserList).name) }
                getVariableConflicts(
                    sourceProject.userVariables,
                    sprite.userVariables
                ).forEach { elem -> conflicts.add((elem as UserVariable).name) }
            }
        }

        return conflicts
    }

    @JvmStatic
    @VisibleForTesting(otherwise = AppCompatActivity.MODE_PRIVATE)
    fun getGlobalVariableConflicts(project1: Project, project2: Project):
        List<UserVariable> {
        val project1GlobalVars = project1.userVariables
        val project2GlobalVars = project2.userVariables
        val conflicts: MutableList<UserVariable> = ArrayList()
        for (project1GlobalVar in project1GlobalVars) {
            for (project2GlobalVar in project2GlobalVars) {
                if (project1GlobalVar.name == project2GlobalVar.name && project1GlobalVar.value != project2GlobalVar.value) {
                    conflicts.add(project1GlobalVar)
                }
            }
        }
        return conflicts
    }

    @JvmStatic
    @VisibleForTesting(otherwise = AppCompatActivity.MODE_PRIVATE)
    fun getGlobalListConflicts(project1: Project, project2: Project): List<UserList> {
        val project1GlobalLists = project1.userLists
        val project2GlobalLists = project2.userLists
        val conflicts: MutableList<UserList> = ArrayList()
        for (project1GlobalList in project1GlobalLists) {
            for (project2GlobalList in project2GlobalLists) {
                if (project1GlobalList.name == project2GlobalList.name && project1GlobalList.value != project2GlobalList.value) {
                    conflicts.add(project1GlobalList)
                }
            }
        }
        return conflicts
    }

    @JvmStatic
    @VisibleForTesting(otherwise = AppCompatActivity.MODE_PRIVATE)
    fun getVariableConflicts(
        globalVariables: List<Any?>,
        localVariables: List<Any>
    ): ArrayList<Any> {
        val conflicts = ArrayList<Any>()
        for (localVar in localVariables) {
            if (globalVariables.contains(localVar)) {
                conflicts.add(localVar)
            }
        }
        return conflicts
    }

    @JvmStatic
    fun importProjectVariables(sourceProject: Project) {
        val projectManager: ProjectManager = inject(ProjectManager::class.java).value
        val currentProject: Project = projectManager.currentProject

        sourceProject.userLists.forEach {
            if (!currentProject.userLists.contains(it)) {
                currentProject.userLists.add(it)
            }
        }

        sourceProject.userVariables.forEach {
            if (!currentProject.userVariables.contains(it)) {
                currentProject.userVariables.add(it)
            }
        }

        sourceProject.broadcastMessageContainer.broadcastMessages.forEach {
            if (!currentProject.broadcastMessageContainer.broadcastMessages.contains(it)) {
                currentProject.broadcastMessageContainer.broadcastMessages.add(it)
            }
        }

        currentProject.broadcastMessageContainer?.update()
    }
}

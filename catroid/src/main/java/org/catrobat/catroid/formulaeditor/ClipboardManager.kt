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

package org.catrobat.catroid.formulaeditor

import org.catrobat.catroid.CatroidApplication.getAppContext
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.R
import org.catrobat.catroid.common.Constants.CLIPBOARD_DIRECTORY_NAME
import org.catrobat.catroid.common.Constants.CLIPBOARD_JSON_FILE_NAME
import org.catrobat.catroid.common.FlavoredConstants.DEFAULT_ROOT_DIRECTORY
import org.catrobat.catroid.formulaeditor.InternTokenType.COLLISION_FORMULA
import org.catrobat.catroid.formulaeditor.InternTokenType.STRING
import org.catrobat.catroid.formulaeditor.InternTokenType.USER_LIST
import org.catrobat.catroid.formulaeditor.InternTokenType.USER_VARIABLE
import org.koin.java.KoinJavaComponent.inject
import java.io.File

object ClipboardManager {

    private val clipboardDirectory = File(DEFAULT_ROOT_DIRECTORY, CLIPBOARD_DIRECTORY_NAME)
    private val clipboardFile = File(clipboardDirectory, CLIPBOARD_JSON_FILE_NAME)

    private var clipboard = Clipboard()

    private val clipboardSerializer = ClipboardSerializer(clipboardFile)

    private val projectManager: ProjectManager by inject(ProjectManager::class.java)

    init {
        createClipboardDirectories()
    }

    private fun createClipboardDirectories() {
        if (!DEFAULT_ROOT_DIRECTORY.exists()) {
            DEFAULT_ROOT_DIRECTORY.mkdir()
        }
        if (!clipboardDirectory.exists()) {
            clipboardDirectory.mkdir()
        }
    }

    fun getClipboardContent(): List<InternToken> = adaptMissingUserDataAndSprites()

    fun setClipboardContent(content: List<InternToken>) {
        val temporaryList: MutableList<InternToken> = java.util.ArrayList()
        for (token in content) {
            temporaryList.add(token.deepCopy())
        }
        clipboard.content = temporaryList
    }

    fun saveClipboard() {
        createClipboardDirectories()
        clipboardSerializer.saveClipboard(clipboard)
    }

    fun loadClipboard() {
        if (clipboard.content.isEmpty()) {
            clipboard = clipboardSerializer.loadClipboard()
        }
    }

    fun isClipboardEmpty(): Boolean = clipboard.content.isEmpty()

    private fun adaptMissingUserDataAndSprites(): List<InternToken> {
        if (clipboard.content.isEmpty()) {
            return listOf()
        }

        val userVariableNames = collectUserVariableNames()
        val userListNames = collectUserListNames()
        val spriteNames = collectSpriteNames()

        val adaptedContent = ArrayList<InternToken>()

        clipboard.content.forEach { internToken ->
            var token = internToken.deepCopy()

            when (internToken.internTokenType) {
                USER_VARIABLE ->
                    if (!userVariableNames.contains(internToken.tokenStringValue)) {
                        token = InternToken(STRING,
                                        "${getAppContext().getString(R.string.formula_editor_missing_variable)} ${internToken.tokenStringValue}")
                    }
                USER_LIST ->
                    if (!userListNames.contains(internToken.tokenStringValue)) {
                        token = InternToken(STRING,
                                            "${getAppContext().getString(R.string.formula_editor_missing_list)} ${internToken.tokenStringValue}")
                    }
                COLLISION_FORMULA ->
                    if (!spriteNames.contains(internToken.tokenStringValue)) {
                        token = InternToken(COLLISION_FORMULA, getAppContext().getString(R.string.background))
                    }
                else -> {}
            }

            adaptedContent.add(token)
        }

        return adaptedContent
    }

    private fun collectUserVariableNames(): List<String> {
        val userVariableNameList = ArrayList<String>()
        projectManager.currentProject.userVariables.forEach { variable -> userVariableNameList.add(variable.name) }
        projectManager.currentProject.multiplayerVariables.forEach { variable -> userVariableNameList.add(variable.name) }
        projectManager.currentSprite.userVariables.forEach { variable -> userVariableNameList.add(variable.name) }
        return userVariableNameList
    }

    private fun collectUserListNames(): List<String> {
        val userListNameList = ArrayList<String>()
        projectManager.currentProject.userLists.forEach { list -> userListNameList.add(list.name) }
        projectManager.currentSprite.userLists.forEach { list -> userListNameList.add(list.name) }
        return userListNameList
    }

    private fun collectSpriteNames(): Set<String> {
        val objectNameSet = HashSet<String>()
        projectManager.currentProject.sceneList.forEach { scene ->
            scene.spriteList.forEach { sprite ->
                if (sprite.name != "Background") {
                    objectNameSet.add(sprite.name)
                }
            }
        }
        return objectNameSet
    }
}

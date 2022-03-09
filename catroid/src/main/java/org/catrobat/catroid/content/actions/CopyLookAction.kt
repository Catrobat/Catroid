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

package org.catrobat.catroid.content.actions

import android.util.Log
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.PixmapIO
import com.badlogic.gdx.scenes.scene2d.Action
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.common.Constants
import org.catrobat.catroid.common.LookData
import org.catrobat.catroid.content.Scope
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.io.StorageOperations
import org.catrobat.catroid.io.XstreamSerializer
import org.catrobat.catroid.ui.recyclerview.util.UniqueNameProvider
import org.koin.java.KoinJavaComponent.inject
import java.io.File
import java.io.IOException

private const val TAG = "CopyLookAction"

class CopyLookAction : Action() {
    var formula: Formula? = null
    var scope: Scope? = null
    var nextLookAction: SetNextLookAction? = null
    private var xstreamSerializer = XstreamSerializer.getInstance()

    override fun act(delta: Float): Boolean {
        nextLookAction?.change = 0
        copyLook()
        return true
    }

    fun nextLookAction(nextLookAction: SetNextLookAction) {
        this.nextLookAction = nextLookAction
    }

    private fun copyLook() {
        try {
            copyLookFile()
        } catch (e: IOException) {
            Log.e(TAG, Log.getStackTraceString(e))
        }
    }

    private fun copyLookFile() {
        val sprite = scope?.sprite ?: return
        val formula = formula ?: return
        val currentLookData = sprite.look?.lookData ?: return
        val currentLookDataFile = sprite.look?.lookData?.file ?: return
        var lookDataName = formula.interpretObject(scope).toString()
        val currentLookDataIndex = sprite.lookList.indexOf(sprite.look.lookData)
        val projectManager: ProjectManager by inject(ProjectManager::class.java)
        val currentScene = projectManager.currentlyPlayingScene
        lookDataName = UniqueNameProvider().getUniqueNameInNameables(lookDataName, sprite.lookList)

        try {
            if (currentLookDataFile.exists()) {
                val copiedLookDataFile = StorageOperations.duplicateFile(currentLookDataFile)
                val copiedLookData = LookData(lookDataName, copiedLookDataFile)
                sprite.lookList.add(currentLookDataIndex + 1, copiedLookData)
                copiedLookData.collisionInformation.calculate()
            } else {
                val imageDirectory = File(currentScene.directory, Constants.IMAGE_DIRECTORY_NAME)
                val copiedLookDataFile = File(imageDirectory, lookDataName + Constants
                    .DEFAULT_IMAGE_EXTENSION)
                copiedLookDataFile.createNewFile()
                val fileHandle = FileHandle(copiedLookDataFile)
                PixmapIO.writePNG(fileHandle, currentLookData.pixmap)
                val copiedLookData = LookData(lookDataName, copiedLookDataFile)
                sprite.lookList.add(sprite.look.lookListIndexBeforeLookRequest + 1, copiedLookData)
                nextLookAction?.change = 1
                copiedLookData.collisionInformation.calculate()
            }
        } catch (e: IOException) {
            Log.e(TAG, Log.getStackTraceString(e))
        }
        xstreamSerializer.saveProject(projectManager.currentProject)
    }
}

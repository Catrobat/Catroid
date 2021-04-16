/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2020 The Catrobat Team
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

import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import androidx.annotation.VisibleForTesting
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.common.Constants
import org.catrobat.catroid.common.LookData
import org.catrobat.catroid.io.StorageOperations
import org.catrobat.catroid.stage.StageActivity
import org.catrobat.catroid.ui.recyclerview.util.UniqueNameProvider
import java.io.File
import java.io.IOException

class PaintNewLookAction : PocketPaintAction() {
    companion object {
        private const val TAG = "PaintNewLookAction"
    }

    override fun act(delta: Float): Boolean {
        if (!questionAsked && LookRequester.requestNewLook(this)) {
            nextLookAction?.change = 0
            questionAsked = true
        }
        return responseReceived
    }

    override fun getTargetIntent(): Intent? {
        return StageActivity.activeStageActivity.get()?.let { stageActivity ->
            val intent = Intent("android.intent.action.MAIN").setComponent(ComponentName(
                stageActivity, Constants.POCKET_PAINT_INTENT_ACTIVITY_NAME))
            val bundle = Bundle()
            bundle.putString(Constants.EXTRA_PICTURE_PATH_POCKET_PAINT, createEmptyImageFile().absolutePath)
            intent.putExtras(bundle)
            intent.addCategory("android.intent.category.LAUNCHER")
            StageActivity.activeStageActivity.get()?.onPause()
            intent
        }
    }

    private fun createEmptyImageFile(): File {
        val pocketPaintImageFileName = Constants.TMP_IMAGE_FILE_NAME + Constants.DEFAULT_IMAGE_EXTENSION
        Constants.POCKET_PAINT_CACHE_DIR.mkdirs()
        if (!Constants.POCKET_PAINT_CACHE_DIR.isDirectory) {
            Log.e(TAG, "Failed to create directory!")
        }
        val currentProject = ProjectManager.getInstance().currentProject
        val bitmap = Bitmap.createBitmap(
            currentProject.xmlHeader.virtualScreenWidth,
            currentProject.xmlHeader.virtualScreenHeight, Bitmap.Config.ARGB_8888
        )
        return StorageOperations.compressBitmapToPng(
            bitmap, File(Constants.POCKET_PAINT_CACHE_DIR, pocketPaintImageFileName))
    }

    override fun onIntentResult(resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            val file = LookRequester.getFile()
            if (file != null) {
                addLookFromFile(file)
                xstreamSerializer.saveProject(ProjectManager.getInstance().currentProject)
            }
        } else {
            LookRequester.anyAsked = false
        }
        StageActivity.activeStageActivity.get()?.onResume()
        responseReceived = true
    }

    @VisibleForTesting
    fun addLookFromFile(file: File) {
        val sprite = scope?.sprite ?: return
        val look = sprite.look ?: return
        val formula = formula ?: return
        var lookDataName = formula.interpretObject(scope).toString()
        lookDataName = UniqueNameProvider().getUniqueNameInNameables(lookDataName, sprite.lookList)
        val lookData = LookData(lookDataName, file)
        val lookDataIndex = if (look.lookListIndexBeforeLookRequest > -1) {
            look.lookListIndexBeforeLookRequest
        } else {
            sprite.lookList.indexOf(sprite.look.lookData)
        }
        sprite.lookList.add(lookDataIndex + 1, lookData)
        lookData.collisionInformation.calculate()
        nextLookAction?.change = 1
    }
}

object LookRequester {
    var anyAsked = false
    @Synchronized
    fun requestNewLook(pocketPaintAction: PocketPaintAction): Boolean {
        if (anyAsked) {
            return false
        }
        StageActivity.messageHandler?.obtainMessage(StageActivity.REGISTER_INTENT, arrayListOf(pocketPaintAction)
        )?.sendToTarget()
        anyAsked = true
        return true
    }

    @Synchronized
    fun getFile(): File? {
        var file: File? = null
        val TAG = "LookRequester"
        try {
            val currentScene = ProjectManager.getInstance().currentlyPlayingScene
            val imageDirectory = File(currentScene.directory, Constants.IMAGE_DIRECTORY_NAME)
            val pocketPaintImageFileName = Constants.TMP_IMAGE_FILE_NAME + Constants.DEFAULT_IMAGE_EXTENSION
            val pocketPaintFile = File(Constants.POCKET_PAINT_CACHE_DIR, pocketPaintImageFileName)
            file = StorageOperations.copyFileToDir(pocketPaintFile, imageDirectory)
        } catch (e: IOException) {
            Log.e(TAG, Log.getStackTraceString(e))
        }
        anyAsked = false
        return file
    }
}

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

import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.annotation.VisibleForTesting
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.common.Constants
import org.catrobat.catroid.common.LookData
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.io.StorageOperations
import org.catrobat.catroid.stage.StageActivity
import java.io.IOException

class EditLookAction : PocketPaintAction() {
    companion object {
        private const val TAG = "EditLookAction"
    }

    override fun act(delta: Float): Boolean {
        if (!questionAsked && LookRequester.requestNewLook(this)) {
            nextLookAction?.change = 0
            questionAsked = true
        }
        return responseReceived
    }

    override fun getTargetIntent(): Intent? {
        if (scope?.sprite?.look?.lookData?.file?.exists() != true) {
            return null
        }
        val lookAbsolutePath = scope?.sprite?.look?.lookData?.file?.absolutePath ?: return null
        return StageActivity.activeStageActivity.get()?.let { stageActivity ->
            val intent = Intent("android.intent.action.MAIN").setComponent(ComponentName(
                stageActivity, Constants.POCKET_PAINT_INTENT_ACTIVITY_NAME))
            val bundle = Bundle()
            bundle.putString(Constants.EXTRA_PICTURE_PATH_POCKET_PAINT, lookAbsolutePath)
            intent.putExtras(bundle)
            intent.addCategory("android.intent.category.LAUNCHER")
            stageActivity.onPause()
            intent
        }
    }

    override fun restart() {
        val stageActivity = StageActivity.activeStageActivity.get()
        if (stageActivity != null && scope?.sprite?.isClone == true) {
            protectOriginalLookData()
        }
        super.restart()
    }

    override fun onIntentResult(resultCode: Int, data: Intent?) {
        val stageActivity = StageActivity.activeStageActivity.get()
        if (resultCode == Activity.RESULT_OK && stageActivity != null) {
            setLookData()
        }
        LookRequester.anyAsked = false
        responseReceived = true
    }

    @VisibleForTesting
    fun protectOriginalLookData() {
        val sprite = scope?.sprite ?: return
        val oldLookData = sprite.look?.lookData ?: return
        val lookDataName = oldLookData.name ?: return
        val oldLookDataFile = sprite.look?.lookData?.file ?: return

        try {
            val newLookDataFile = StorageOperations.duplicateFile(oldLookDataFile)
            val newLookData = LookData(lookDataName, newLookDataFile)
            sprite.lookList.remove(oldLookData)
            sprite.lookList.add(newLookData)
            sprite.look.lookData = newLookData
        } catch (e: IOException) {
            Log.e(TAG, Log.getStackTraceString(e))
        }
    }

    @VisibleForTesting
    fun setLookData() {
        val sprite = scope?.sprite ?: return
        val tempLookData = sprite.look?.lookData ?: return
        val lookDataName = tempLookData.name ?: return

        try {
            val newLookData = createDuplicateToGetOriginalFileName(sprite, lookDataName)
            sprite.lookList.remove(tempLookData)
            sprite.look.lookListIndexBeforeLookRequest = sprite.lookList.size
            sprite.lookList.add(newLookData)
            newLookData?.collisionInformation?.calculate()
        } catch (e: IOException) {
            Log.e(TAG, Log.getStackTraceString(e))
        }
        xstreamSerializer.saveProject(ProjectManager.getInstance().currentProject)
    }

    private fun createDuplicateToGetOriginalFileName(sprite: Sprite, lookDataName: String):
        LookData? {
        var tempLookDataFile = sprite.look?.lookData?.file ?: return null
        var newLookDataFile = StorageOperations.duplicateFile(tempLookDataFile)
        if (!sprite.isClone) {
            tempLookDataFile.delete()
            tempLookDataFile = StorageOperations.duplicateFile(newLookDataFile)
            newLookDataFile.delete()
            newLookDataFile = tempLookDataFile
        }
        return LookData(lookDataName, newLookDataFile)
    }
}

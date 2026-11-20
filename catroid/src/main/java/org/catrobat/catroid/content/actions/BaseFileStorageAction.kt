/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2026 The Catrobat Team
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
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import com.badlogic.gdx.scenes.scene2d.Action
import org.catrobat.catroid.CatroidApplication
import org.catrobat.catroid.R
import org.catrobat.catroid.common.Constants
import org.catrobat.catroid.content.Scope
import org.catrobat.catroid.io.StorageOperations
import org.catrobat.catroid.stage.StageActivity
import org.catrobat.catroid.stage.StageActivity.IntentListener
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.utils.Utils
import java.io.File
import java.io.IOException
import java.util.ArrayList

abstract class BaseFileStorageAction : Action(), IntentListener {
    protected open val context: Context
        get() = CatroidApplication.getAppContext()
    abstract var formula: Formula?
    abstract var scope: Scope?
    protected abstract val errorMessageResId: Int
    protected abstract val isReadAction: Boolean
    protected abstract val fileExtension: String
    internal abstract fun handleFileWork(file: File): Boolean
    internal abstract fun checkIfDataIsReady(): Boolean
    protected abstract fun showDataMissingMessage()
    abstract override fun getTargetIntent(): Intent

    override fun act(delta: Float): Boolean {
        if (formula == null || scope == null) {
            return true
        }
        if (checkIfDataIsReady()) {
            startFileInteraction()
        } else {
            showDataMissingMessage()
        }
        return true
    }

    private fun startFileInteraction() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            StageActivity.messageHandler?.obtainMessage(
                StageActivity.REGISTER_INTENT, arrayListOf(this))?.sendToTarget()
        } else {
            performLegacyOperation()
        }
    }

    private fun performLegacyOperation() {
        val fileName = getFileName()
        val file = if (isReadAction) {
            getFile(fileName)
        } else {
            createFile(fileName)
        }
        if (file == null || (isReadAction && !file.exists())) {
            showErrorMessage()
            return
        }
        try {
            if (handleFileWork(file)) {
                onPostExecute(fileName, null)
            } else {
                showErrorMessage()
            }
        } catch (e: IOException) {
            Log.e("FileStorageAction", "Error, legacy storage operation failed", e)
        }
    }

    protected fun performUriOperation(uri: Uri?) {
        if (uri == null) {
            Log.e("FileStorageAction", "User cancelled or URI is invalid")
        }
        val activity = StageActivity.activeStageActivity?.get() ?: return
        val contentResolver = activity.contentResolver
        val fileName = if (uri != null) {
            StorageOperations.resolveFileName(contentResolver, uri) ?: getFileName()
        } else {
            getFileName()
        }
        val cacheFile = File(Constants.CACHE_DIRECTORY, fileName)
        try {
            if (isReadAction && uri != null) {
                StorageOperations.copyUriContentToFile(contentResolver, uri, cacheFile)
            } else if (!cacheFile.exists()) {
                cacheFile.createNewFile()
            }
            if (handleFileWork(cacheFile)) {
                if (!isReadAction && uri != null) {
                    context.contentResolver?.let { resolver ->
                        StorageOperations.copyFileContentToUri(resolver, uri, cacheFile)
                    } ?: Log.e("BaseFileAction", "ContentResolver is null, cannot save to URI")
                }
                onPostExecute(fileName, uri)
            } else {
                showErrorMessage()
            }
        } catch (e: IOException) {
            Log.e("FileStorageAction", "Error, storage transport failed", e)
            showErrorMessage()
        } finally {
            if (cacheFile.exists()) cacheFile.delete()
        }
    }

    override fun onIntentResult(resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                performUriOperation(uri)
            }
        }
    }

    protected open fun onPostExecute(fileName: String, uri: Uri?) {
        if (!isReadAction) showSuccessMessage(fileName)
    }

    private fun showErrorMessage() {
        val message = context.getString(errorMessageResId)
        val params = ArrayList<Any>(listOf(message))
        StageActivity.messageHandler.obtainMessage(StageActivity.SHOW_TOAST, params).sendToTarget()
    }

    private fun showSuccessMessage(fileName: String) {
        val message = context.getString(R.string.brick_write_variable_to_file_success, fileName)
        val params = ArrayList<Any>(listOf(message))
        StageActivity.messageHandler.obtainMessage(StageActivity.SHOW_TOAST, params).sendToTarget()
    }

    private fun createFile(fileName: String): File? {
        val file = File(Constants.EXTERNAL_STORAGE_ROOT_EXPORT_DIRECTORY, fileName)
        return if (file.exists() || file.createNewFile()) {
            file
        } else null
    }

    protected fun getFile(fileName: String): File? {
        val file = File(Constants.EXTERNAL_STORAGE_ROOT_EXPORT_DIRECTORY, fileName)
        return if (file.exists()) file else null
    }

    protected fun getFileName(): String {
        var fileName = Utils.sanitizeFileName(formula?.interpretString(scope))
        if (isReadAction) {
            if (!fileName.contains(Regex("\\.\\w+$"))) {
                fileName += fileExtension
            }
        }else{
            if (!fileName.endsWith(fileExtension)) {
                fileName += fileExtension
            }
        }
        return fileName
    }
}
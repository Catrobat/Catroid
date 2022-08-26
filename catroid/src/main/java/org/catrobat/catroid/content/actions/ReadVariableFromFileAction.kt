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
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.annotation.VisibleForTesting
import androidx.documentfile.provider.DocumentFile
import com.badlogic.gdx.scenes.scene2d.Action
import org.catrobat.catroid.R
import org.catrobat.catroid.common.Constants
import org.catrobat.catroid.content.Scope
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.formulaeditor.UserVariable
import org.catrobat.catroid.stage.StageActivity
import org.catrobat.catroid.stage.StageActivity.IntentListener
import org.catrobat.catroid.utils.Utils
import java.io.File
import java.io.IOException
import java.io.InputStreamReader

class ReadVariableFromFileAction : Action(), IntentListener {
    var scope: Scope? = null
    var formula: Formula? = null
    var userVariable: UserVariable? = null
    var deleteFile: Boolean = false

    override fun act(delta: Float): Boolean {
        if (userVariable == null || formula == null) {
            return true
        }
        val fileName = getFileName()
        readVariableFromFile(fileName)
        return true
    }

    @VisibleForTesting
    fun readVariableFromFile(fileName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            readUsingSystemFilePicker()
        } else {
            readUsingLegacyExternalStorage(fileName)
        }
    }

    private fun readUsingSystemFilePicker() {
        StageActivity.messageHandler?.obtainMessage(
            StageActivity.REGISTER_INTENT, arrayListOf(this))?.sendToTarget()
    }

    private fun readUsingLegacyExternalStorage(fileName: String) {
        getFile(fileName)?.let {
            val content = readFromFile(it)
            writeContentToVariable(content)
            if (deleteFile) {
                it.delete()
            }
        }
    }

    @VisibleForTesting
    fun getFile(fileName: String): File? {
        val file = File(Constants.EXTERNAL_STORAGE_ROOT_EXPORT_DIRECTORY, fileName)
        return if (file.exists()) file else null
    }

    @VisibleForTesting
    fun readFromFile(file: File): String {
        return try {
            file.readText()
        } catch (e: IOException) {
            Log.e(javaClass.simpleName, "Could not read variable value from storage.")
            "0"
        }
    }

    private fun writeContentToVariable(content: String) {
        userVariable?.value = content.toDoubleOrNull() ?: content
    }

    private fun getFileName(): String {
        var fileName = Utils.sanitizeFileName(formula?.interpretString(scope))
        if (!fileName.contains(Regex("\\.\\w+$"))) {
            fileName += ".txt"
        }
        return fileName
    }

    private fun readUriContentAndWriteToVariable(uri: Uri) {
        val contentResolver = StageActivity.activeStageActivity.get()?.contentResolver ?: return
        try {
            contentResolver.openInputStream(uri)?.use { inputStream ->
                val reader = InputStreamReader(inputStream, "UTF-8")
                val content = reader.readText()
                writeContentToVariable(content)
            }
        } catch (e: IOException) {
            Log.e(javaClass.simpleName, "Could not read variable value from storage. (using uri)")
        }
    }

    private fun deleteFileWithUri(uri: Uri) {
        val context = StageActivity.activeStageActivity.get()?.context ?: return
        val file = DocumentFile.fromSingleUri(context, uri) ?: return
        if (!file.delete()) {
            Log.e(javaClass.simpleName, "Could not delete file which stores the variable content.")
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun getTargetIntent(): Intent {
        val context = StageActivity.activeStageActivity.get()?.context
        val title = context?.getString(R.string.brick_read_variable_from_file_top) ?: ""
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            type = "*/*"
            putExtra(DocumentsContract.EXTRA_INITIAL_URI, Environment.DIRECTORY_DOWNLOADS)
        }
        return Intent.createChooser(intent, title)
    }

    override fun onIntentResult(resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            data?.data?.let {
                readUriContentAndWriteToVariable(it)
                if (deleteFile) {
                    deleteFileWithUri(it)
                }
            }
        }
    }
}

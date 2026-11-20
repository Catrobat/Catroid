/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2025 The Catrobat Team
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

import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.DocumentsContract
import android.util.Log
import androidx.documentfile.provider.DocumentFile
import org.catrobat.catroid.R
import org.catrobat.catroid.common.Constants
import org.catrobat.catroid.content.Scope
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.formulaeditor.UserVariable
import java.io.File

class ReadVariableFromFileAction : BaseFileStorageAction() {

    override var formula: Formula? = null
    override var scope: Scope? = null
    override val fileExtension = Constants.TEXT_FILE_EXTENSION
    override val isReadAction = true
    override val errorMessageResId = R.string.error_variable_file_read
    var userVariable: UserVariable? = null
    var deleteFile: Boolean = false

    override fun checkIfDataIsReady(): Boolean {
        return userVariable != null
    }

    override fun showDataMissingMessage() {
        // No-op: Reading actions do not require a specific message if data is empty
    }

    override fun handleFileWork(file: File): Boolean {
        val content = file.readText()
        writeContentToVariable(content)
        return true
    }

    private fun writeContentToVariable(content: String) {
        userVariable?.value = content.toDoubleOrNull() ?: content
    }

    override fun onPostExecute(fileName: String, uri: Uri?) {
        if (deleteFile) {
            if (uri != null) {
                deleteFileWithUri(uri)
            } else {
                val deleted = getFile(fileName)?.delete() ?: false
                if (!deleted) {
                    Log.w("FileStorageAction", "Could not delete temporary file: $fileName")
                }
            }
        }
    }

    private fun deleteFileWithUri(uri: Uri) {
        try {
            val documentFile = DocumentFile.fromSingleUri(context, uri)
            if (documentFile?.delete() == false) {
                Log.e("FileStorageAction", "Could not delete file via URI: $uri")
            }
        } catch (e: Exception) {
            Log.e("FileStorageAction", "Error during URI deletion process: $uri", e)
        }
    }

    override fun getTargetIntent(): Intent {
        val title = context.getString(R.string.brick_read_variable_from_file_top)
        return Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            type = "*/*"
            putExtra(DocumentsContract.EXTRA_INITIAL_URI, Environment.DIRECTORY_DOWNLOADS)
        }.let { Intent.createChooser(it, title) }
    }
}
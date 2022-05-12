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
import androidx.annotation.VisibleForTesting
import com.badlogic.gdx.scenes.scene2d.Action
import org.catrobat.catroid.CatroidApplication
import org.catrobat.catroid.R
import org.catrobat.catroid.common.Constants
import org.catrobat.catroid.common.Constants.ANY_EXTENSION_REGEX
import org.catrobat.catroid.common.Constants.DOWNLOAD_DIRECTORY
import org.catrobat.catroid.content.Scope
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.formulaeditor.UserVariable
import org.catrobat.catroid.stage.StageActivity
import org.catrobat.catroid.utils.Utils
import java.io.File
import java.io.IOException
import java.util.ArrayList

class WriteVariableToFileAction : Action() {
    var scope: Scope? = null
    var formula: Formula? = null
    var userVariable: UserVariable? = null

    override fun act(delta: Float): Boolean {
        if (userVariable == null || formula == null) {
            return true
        }
        createAndWriteToFile()
        return true
    }

    @VisibleForTesting
    fun createAndWriteToFile() {
        val fileName = getFileName()
        createFile(fileName)?.let {
            val content = userVariable?.value.toString() ?: "0"
            writeToFile(it, content)
        }
    }

    private fun getFileName(): String {
        var fileName = Utils.sanitizeFileName(formula?.interpretString(scope))
        if (!fileName.contains(Regex(ANY_EXTENSION_REGEX))) {
            fileName += Constants.TEXT_FILE_EXTENSION
        }
        return fileName
    }

    @VisibleForTesting
    fun createFile(fileName: String): File? {
        val file = File(DOWNLOAD_DIRECTORY, fileName)
        return if (file.exists() || file.createNewFile()) {
            file
        } else null
    }

    @VisibleForTesting
    fun writeToFile(file: File, content: String) {
        try {
            file.writeText(content)
            showSuccessMessage(file.name)
        } catch (e: IOException) {
            Log.e(javaClass.simpleName, "Could not write variable value to storage.")
        }
    }

    private fun showSuccessMessage(fileName: String) {
        val context = CatroidApplication.getAppContext()
        val message = context.getString(R.string.brick_write_variable_to_file_success, fileName)
        val params = ArrayList<Any>(listOf(message))
        StageActivity.messageHandler.obtainMessage(StageActivity.SHOW_TOAST, params).sendToTarget()
    }
}

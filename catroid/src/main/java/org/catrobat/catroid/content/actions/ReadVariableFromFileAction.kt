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
import org.catrobat.catroid.common.Constants.ANY_EXTENSION_REGEX
import org.catrobat.catroid.common.Constants.DOWNLOAD_DIRECTORY
import org.catrobat.catroid.content.Scope
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.formulaeditor.UserVariable
import org.catrobat.catroid.utils.Utils
import java.io.File
import java.io.IOException

class ReadVariableFromFileAction : Action() {
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

    private fun getFileName(): String {
        var fileName = Utils.sanitizeFileName(formula?.interpretString(scope))
        if (!fileName.contains(Regex(ANY_EXTENSION_REGEX))) {
            fileName += ".txt"
        }
        return fileName
    }

    @VisibleForTesting
    fun readVariableFromFile(fileName: String) {
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
        val file = File(DOWNLOAD_DIRECTORY, fileName)
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
}

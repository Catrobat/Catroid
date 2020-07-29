/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2018 The Catrobat Team
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
import org.catrobat.catroid.common.Constants
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.formulaeditor.UserVariable
import org.catrobat.catroid.utils.Utils
import java.io.File
import java.io.IOException

class ReadVariableFromFileAction : Action() {
    var sprite: Sprite? = null
    var formula: Formula? = null
    var userVariable: UserVariable? = null
    var deleteFile: Boolean = false

    override fun act(delta: Float): Boolean {
        if (userVariable == null || formula == null) {
            return true
        }

        var fileName = Utils.sanitizeFileName(formula!!.interpretString(sprite))
        if (!fileName.contains(Regex("\\.\\w+$"))) {
            fileName += ".txt"
        }

        getFile(fileName)?.let {
            val content = readFromFile(it)
            userVariable!!.value = content.toDoubleOrNull() ?: content
            if (deleteFile) {
                it.delete()
            }
        }
        return true
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
}

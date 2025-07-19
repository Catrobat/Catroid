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
import android.os.Environment
import android.provider.DocumentsContract
import org.catrobat.catroid.R
import org.catrobat.catroid.common.Constants
import org.catrobat.catroid.content.Scope
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.formulaeditor.UserVariable
import java.io.File

class WriteVariableToFileAction : BaseFileStorageAction() {

    override var formula: Formula? = null
    override var scope: Scope? = null
    override val fileExtension = Constants.TEXT_FILE_EXTENSION
    override val isReadAction = false
    override val errorMessageResId = R.string.error_variable_file_write
    var userVariable: UserVariable? = null

    override fun checkIfDataIsReady(): Boolean {
        return userVariable != null
    }

    override fun showDataMissingMessage() {}

    override fun handleFileWork(file: File): Boolean {
        val content: String = when (val value = userVariable?.value ?: 0) {
            is Double -> (value).toBigDecimal().toPlainString()
            else -> value.toString()
        }
        file.writeText(content)
        return true
    }

    override fun getTargetIntent(): Intent {
        val title = context.getString(R.string.brick_write_variable_to_file_top)
        return Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            type = "*/*"
            putExtra(Intent.EXTRA_TITLE, getFileName())
            putExtra(DocumentsContract.EXTRA_INITIAL_URI, Environment.DIRECTORY_DOWNLOADS)
        }.let{ Intent.createChooser(it, title) }
    }
}

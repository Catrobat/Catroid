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
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.annotation.VisibleForTesting
import com.badlogic.gdx.scenes.scene2d.Action
import org.catrobat.catroid.CatroidApplication
import org.catrobat.catroid.R
import org.catrobat.catroid.common.Constants
import org.catrobat.catroid.content.Scope
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.io.StorageOperations
import org.catrobat.catroid.plot.SVGPlotGenerator
import org.catrobat.catroid.stage.StageActivity
import org.catrobat.catroid.stage.StageActivity.IntentListener
import org.catrobat.catroid.utils.Utils
import java.io.File
import java.io.IOException
import java.util.ArrayList

class SavePlotAction : Action(), IntentListener {
    var scope: Scope? = null
    var formula: Formula? = null
    val context: Context = CatroidApplication.getAppContext()

    override fun act(delta: Float): Boolean {
        if (formula == null) {
            return true
        }
        writePlotToFile()
        return true
    }

    private fun writePlotToFile() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            writeUsingSystemFilePicker()
        } else {
            writeUsingLegacyExternalStorage()
        }
    }

    private fun writeUsingSystemFilePicker() {
        StageActivity.messageHandler?.obtainMessage(
            StageActivity.REGISTER_INTENT, arrayListOf(this))?.sendToTarget()
    }

    private fun writeUsingLegacyExternalStorage() {
        var fileName = Utils.sanitizeFileName(formula?.interpretString(scope))
        if (!fileName.endsWith(Constants.SVG_FILE_EXTENSION)) {
            fileName += Constants.SVG_FILE_EXTENSION
        }
        createFile(fileName)?.let {
            writeToFile(it)
        }
    }

    private fun showMessagePlotDataIsMissing() {
        val message = context.getString(R.string.error_plot_data_not_found)
        val params = ArrayList<Any>(listOf(message))
        StageActivity.messageHandler.obtainMessage(StageActivity.SHOW_TOAST, params)
            .sendToTarget()
    }

    @VisibleForTesting
    fun createFile(fileName: String): File? {
        val file = File(Constants.EXTERNAL_STORAGE_ROOT_EXPORT_DIRECTORY, fileName)
        return if (file.exists() || file.createNewFile()) {
            file
        } else null
    }

    @VisibleForTesting
    fun writeToFile(file: File) {
        var message = context.getString(R.string.brick_write_variable_to_file_success, file)
        try {
            writePlotDataToFile(file)
        } catch (e: IOException) {
            message = context.getString(R.string.error_plot_file_write)
            Log.e(javaClass.simpleName, "Writing plot data to file failed")
        } finally {
            val params = ArrayList<Any>(listOf(message))
            StageActivity.messageHandler
                .obtainMessage(StageActivity.SHOW_TOAST, params).sendToTarget()
        }
    }

    private fun writeSVGPlotaToUri(uri: Uri) {
        val contentResolver = context.contentResolver
        val fileName = StorageOperations.resolveFileName(contentResolver, uri) ?: return
        var message = context.getString(R.string.brick_write_variable_to_file_success, fileName)
        try {
            val cacheFile = File(Constants.CACHE_DIRECTORY, fileName)
            if (!cacheFile.exists()) {
                cacheFile.createNewFile()
            }
            writePlotDataToFile(cacheFile)
            StorageOperations.copyFileContentToUri(contentResolver, uri, cacheFile)
        } catch (e: IOException) {
            message = context.getString(R.string.error_plot_file_write)
            Log.e(StageActivity.TAG, "Writing svg plot data to file failed")
        } finally {
            val params = ArrayList<String>()
            params.add(message)
            StageActivity.messageHandler.obtainMessage(
                StageActivity.SHOW_TOAST, params
            ).sendToTarget()
        }
    }

    private fun writePlotDataToFile(destinationFile: File) {
        val svgFileGenerator = SVGPlotGenerator(
            scope?.sprite?.plot
        )
        svgFileGenerator.writeToSVGFile(destinationFile)
    }

    private fun getFileName(): String {
        var fileName = Utils.sanitizeFileName(formula?.interpretString(scope))
        if (!fileName.endsWith(Constants.SVG_FILE_EXTENSION)) {
            fileName += Constants.SVG_FILE_EXTENSION
        }
        return fileName
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun getTargetIntent(): Intent {
        val fileName = getFileName()
        val title = context.getString(R.string.brick_save_plot)
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            type = "*/*"
            putExtra(Intent.EXTRA_TITLE, fileName)
            putExtra(DocumentsContract.EXTRA_INITIAL_URI, Environment.DIRECTORY_DOWNLOADS)
        }
        return Intent.createChooser(intent, title)
    }

    override fun onIntentResult(resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            data?.data?.let {
                writeSVGPlotaToUri(it)
            }
        }
    }
}

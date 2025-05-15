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

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.annotation.VisibleForTesting
import org.catrobat.catroid.CatroidApplication
import org.catrobat.catroid.R
import org.catrobat.catroid.common.Constants
import org.catrobat.catroid.content.Scope
import org.catrobat.catroid.formulaeditor.Formula
import org.catrobat.catroid.plot.SVGPlotGenerator
import org.catrobat.catroid.stage.StageActivity
import org.catrobat.catroid.stage.StageActivity.IntentListener
import com.badlogic.gdx.scenes.scene2d.Action
import org.catrobat.catroid.utils.Utils
import java.io.File
import java.io.IOException

open class SharePlotAction : Action(), IntentListener {
    var scope: Scope? = null
    var formula: Formula? = null
    val context: Context = CatroidApplication.getAppContext()
    private val duration = 0.1f

   override fun act(delta: Float): Boolean {
       if (formula == null) {
           return true
       }
       writeUsingSystemFilePicker()
       return true
   }

    private fun writeUsingSystemFilePicker() {
        StageActivity.messageHandler?.obtainMessage(
            StageActivity.REGISTER_INTENT, arrayListOf(this))?.sendToTarget()
    }

    private fun showMessagePlotDataIsMissing() {
        val message = context.getString(R.string.error_plot_data_not_found)
        val params = ArrayList<Any>(listOf(message))
        StageActivity.messageHandler.obtainMessage(StageActivity.SHOW_TOAST, params)
            .sendToTarget()
    }

    @VisibleForTesting
    fun createFile(fileName: String): File {
        val file = File(Constants.CACHE_DIRECTORY, fileName)
        return if (file.exists())
            file
        else
            File.createTempFile(file.nameWithoutExtension, file.extension, Constants
                .CACHE_DIRECTORY)
    }

    @VisibleForTesting
    fun sharePlot(file: File) {
        var message = context.getString(R.string.brick_write_variable_to_file_success, file)
        try {
            writePlotDataToFile(file)
        } catch (e: IOException) {
            message = context.getString(R.string.error_plot_file_write)
            Log.e(javaClass.simpleName, "Writing plot data to file failed")
        }
    }


    open fun writePlotDataToFile(destinationFile: File) {
        val plot = scope?.sprite?.plot!!
        val svgFileGenerator = SVGPlotGenerator(plot)
        val path =svgFileGenerator.pathFromData(plot.plotDataPointLists)
        svgFileGenerator.writeToSVGFile(destinationFile, path)
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
        val file = createFile(getFileName())
        sharePlot(file)
        val title = context.getString(R.string.brick_share_plot)
        val intent = Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_TITLE, title)
            putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file))
            type = "image/svg+xml"
        }
        return Intent.createChooser(intent, title)
    }

    override fun onIntentResult(resultCode: Int, data: Intent?) {
        if (resultCode != Activity.RESULT_OK) {
            showMessagePlotDataIsMissing()
        }
    }
}

/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2023 The Catrobat Team
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
package org.catrobat.catroid.io.asynctask

import android.content.Context
import android.net.Uri
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.catrobat.catroid.R
import org.catrobat.catroid.common.Constants
import org.catrobat.catroid.io.StorageOperations
import org.catrobat.catroid.io.ZipArchiver
import org.catrobat.catroid.utils.notifications.NotificationData
import org.catrobat.catroid.utils.notifications.StatusBarNotificationManager
import java.io.File
import java.io.IOException
import java.lang.ref.WeakReference

class ProjectExporter(
    private val projectDir: File,
    private val projectDestination: Uri,
    private val notificationData: NotificationData,
    context: Context
) {

    private val contextWeakReference: WeakReference<Context> = WeakReference(context)
    private var projectZip: File =
        File(Constants.CACHE_DIRECTORY, projectDir.name + Constants.ZIP_EXTENSION)
    private var finishedExportingCallback: (() -> Unit)? = null

    @JvmOverloads
    fun exportProjectToExternalStorageAsync(
        scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
    ) {
        scope.launch {
            exportProjectToExternalStorage()
        }
    }

    private fun exportProjectToExternalStorage() {
        val context = contextWeakReference.get() ?: return
        deleteUndoFile()

        if (projectZip.exists()) {
            projectZip.delete()
        }
        try {
            ZipArchiver().zip(projectZip, projectDir.listFiles())
            StorageOperations.copyFileContentToUri(
                context.contentResolver,
                projectDestination,
                projectZip
            )
            StatusBarNotificationManager(context).showOrUpdateNotification(
                context,
                notificationData,
                PROGRESS_FINISHED_PERCENT,
                null
            )
            finishedExportingCallback?.invoke()
        } catch (e: IOException) {
            Log.e(TAG, "Cannot create archive.", e)
            StatusBarNotificationManager(context).abortProgressNotificationWithMessage(
                context,
                notificationData,
                R.string.save_project_to_external_storage_io_exception_message
            )
        } finally {
            if (projectZip.exists()) {
                projectZip.delete()
            }
        }
    }

    fun injectExportDirectory(exportDirectory: File) {
        projectZip = File(exportDirectory, projectDir.name + Constants.ZIP_EXTENSION)
    }

    private fun deleteUndoFile() {
        val undoCodeFile = File(projectDir, Constants.UNDO_CODE_XML_FILE_NAME)
        if (undoCodeFile.exists()) {
            try {
                StorageOperations.deleteFile(undoCodeFile)
            } catch (exception: IOException) {
                Log.e(TAG, "Deleting undo file failed.", exception)
            }
        }
    }

    fun registerCallback(callback: () -> Unit) {
        finishedExportingCallback = callback
    }

    companion object {
        private val TAG = ProjectExporter::class.java.simpleName
        private const val PROGRESS_FINISHED_PERCENT: Int = 100
    }
}

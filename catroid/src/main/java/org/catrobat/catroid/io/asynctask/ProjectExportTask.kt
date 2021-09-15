/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2021 The Catrobat Team
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
import android.os.AsyncTask
import android.util.Log
import androidx.annotation.VisibleForTesting

import org.catrobat.catroid.utils.notifications.NotificationData
import org.catrobat.catroid.io.ZipArchiver
import org.catrobat.catroid.utils.notifications.StatusBarNotificationManager
import org.catrobat.catroid.R
import org.catrobat.catroid.common.Constants
import org.catrobat.catroid.io.StorageOperations

import java.io.File
import java.io.IOException
import java.lang.ref.WeakReference

class ProjectExportTask(
    private val projectDir: File,
    private val projectDestination: Uri,
    private val notificationData: NotificationData,
    context: Context
) : AsyncTask<Void?, Void?, Void?>() {

    private val contextWeakReference: WeakReference<Context> = WeakReference(context)
    private var finishedExportingCallback: ProjectExportCallback? = null

    @VisibleForTesting
    fun exportProjectToExternalStorage() {
        val context = contextWeakReference.get() ?: return
        deleteUndoFile()

        val projectFileName = projectDir.name + Constants.ZIP_EXTENSION
        val cacheFile = File(Constants.CACHE_DIR, projectFileName)
        if (cacheFile.exists()) {
            cacheFile.delete()
        }
        try {
            ZipArchiver().zip(cacheFile, projectDir.listFiles())
            val contentResolver = context.contentResolver
            StorageOperations.copyFileContentToUri(contentResolver, projectDestination, cacheFile)
            updateNotification(context)
            finishedExportingCallback?.onProjectExportFinished()
        } catch (e: IOException) {
            Log.e(TAG, "Cannot create archive.", e)
            abortNotification(context)
        } finally {
            if (cacheFile.exists()) {
                cacheFile.delete()
            }
        }
    }

    fun registerCallback(callback: ProjectExportCallback) {
        finishedExportingCallback = callback
    }

    private fun updateNotification(context: Context) {
        StatusBarNotificationManager(context).showOrUpdateNotification(
            context, notificationData, NOTIFICATION_PROGRESS_COMPLETE, null)
    }

    private fun abortNotification(context: Context) {
        StatusBarNotificationManager(context).abortProgressNotificationWithMessage(
            context, notificationData,
            R.string.save_project_to_external_storage_io_exception_message
        )
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

    override fun doInBackground(vararg voids: Void?): Void? {
        exportProjectToExternalStorage()
        return null
    }

    interface ProjectExportCallback {
        fun onProjectExportFinished()
    }

    companion object {
        private val TAG = ProjectExportTask::class.java.simpleName
        private const val NOTIFICATION_PROGRESS_COMPLETE = 100
    }
}

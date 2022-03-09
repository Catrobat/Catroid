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
package org.catrobat.catroid.web

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.ResultReceiver
import android.util.Log
import androidx.annotation.VisibleForTesting
import androidx.appcompat.app.AppCompatActivity
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.R
import org.catrobat.catroid.scratchconverter.Client.ProjectDownloadCallback
import org.catrobat.catroid.transfers.project.ProjectDownloadService
import org.catrobat.catroid.transfers.project.ProjectDownloadService.Companion.ERROR_CODE
import org.catrobat.catroid.transfers.project.ProjectDownloadService.Companion.SUCCESS_CODE
import org.catrobat.catroid.transfers.project.ProjectDownloadService.Companion.UPDATE_PROGRESS_CODE
import org.catrobat.catroid.transfers.project.ProjectDownloadService.Companion.UPDATE_PROGRESS_EXTRA
import org.catrobat.catroid.ui.recyclerview.dialog.ReplaceExistingProjectDialogFragment
import org.catrobat.catroid.ui.recyclerview.dialog.ReplaceExistingProjectDialogFragment.projectExistsInDirectory
import org.catrobat.catroid.utils.ToastUtil
import org.catrobat.catroid.utils.notifications.StatusBarNotificationManager
import org.koin.java.KoinJavaComponent.inject
import java.io.Serializable
import java.io.UnsupportedEncodingException
import java.lang.ref.WeakReference
import java.net.URLDecoder
import java.util.Collections
import java.util.Locale

class ProjectDownloader(
    private val queue: ProjectDownloadQueue,
    private val url: String,
    callback: ProjectDownloadCallback?
) : Serializable {
    private val callbackWeakReference = WeakReference<ProjectDownloadCallback>(callback)

    companion object {
        private const val serialVersionUID = 42L
        private const val FILENAME_QUERY_PARAM = "fname="
        private val TAG = ProjectDownloader::class.java.simpleName

        fun getProjectNameFromUrl(url: String): String? {
            val projectNameIndex = url.lastIndexOf(FILENAME_QUERY_PARAM) + FILENAME_QUERY_PARAM.length
            val projectNameUTF8 = url.substring(projectNameIndex)
            return try {
                URLDecoder.decode(projectNameUTF8, "UTF-8")
            } catch (e: UnsupportedEncodingException) {
                Log.e(TAG, "Could not decode project name: $projectNameUTF8", e)
                null
            }
        }
    }

    fun download(activity: AppCompatActivity) {
        val projectName = getProjectNameFromUrl(url)

        if (projectName == null) {
            ToastUtil.showError(activity, R.string.error_could_not_decode_project_name_from_url)
            return
        }

        if (queue.alreadyInQueue(projectName) || projectExistsInDirectory(projectName)) {
            val dialog = ReplaceExistingProjectDialogFragment.newInstance(projectName, this)
            dialog.show(activity.supportFragmentManager, ReplaceExistingProjectDialogFragment.TAG)
        } else {
            downloadOverwriteExistingProject(activity, projectName)
        }
    }

    fun downloadOverwriteExistingProject(context: Context, projectName: String) {
        synchronized(queue) {
            if (queue.alreadyInQueue(projectName)) {
                val errorMessage = context.getString(
                    R.string.error_project_already_in_queue,
                    projectName)
                Log.i(TAG, errorMessage)
                ToastUtil.showError(context, errorMessage)
                return
            }

            queue.enqueue(projectName)
            startService(projectName, context)
        }
    }

    @VisibleForTesting
    fun startService(projectName: String, context: Context) {
        val downloadIntent = Intent(context, ProjectDownloadService::class.java)
        downloadIntent.putExtra(ProjectDownloadService.EXTRA_DOWNLOAD_NAME, projectName)
        downloadIntent.putExtra(ProjectDownloadService.EXTRA_URL, url)
        downloadIntent.putExtra(ProjectDownloadService.EXTRA_RESULT_RECEIVER, Receiver(projectName, Handler()))

        val statusBarNotificationManager = StatusBarNotificationManager(context)
        val notificationData = statusBarNotificationManager.createProjectDownloadNotification(context, projectName)
        downloadIntent.putExtra(ProjectDownloadService.EXTRA_NOTIFICATION_DATA, notificationData)
        statusBarNotificationManager.showOrUpdateNotification(context, notificationData, 0, null)

        callbackWeakReference.get()?.onDownloadStarted(url)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(downloadIntent)
        } else {
            context.startService(downloadIntent)
        }
    }

    @SuppressLint("ParcelCreator")
    private inner class Receiver internal constructor(
        val projectName: String,
        handler: Handler
    ) : ResultReceiver(handler) {
        override fun onReceiveResult(resultCode: Int, resultData: Bundle) {
            super.onReceiveResult(resultCode, resultData)
            when (resultCode) {
                UPDATE_PROGRESS_CODE -> {
                    val progress = resultData.getInt(UPDATE_PROGRESS_EXTRA)
                    callbackWeakReference.get()?.onDownloadProgress(progress.toInt(), url)
                }
                SUCCESS_CODE -> {
                    callbackWeakReference.get()?.onDownloadFinished(projectName, url)
                    val projectManager: ProjectManager by inject(ProjectManager::class.java)
                    projectManager.addNewDownloadedProject(projectName)
                    queue.finished(projectName)
                }
                ERROR_CODE -> queue.finished(projectName)
            }
        }
    }

    interface ProjectDownloadQueue {
        fun alreadyInQueue(projectName: String): Boolean
        fun enqueue(projectName: String)
        fun finished(projectName: String)
    }
}

object GlobalProjectDownloadQueue {
    val queue = ProjectDownloadQueue()

    class ProjectDownloadQueue : ProjectDownloader.ProjectDownloadQueue {
        private var projectNameSet: Set<String> = Collections.synchronizedSet(HashSet<String>())

        @Synchronized
        override fun alreadyInQueue(projectName: String): Boolean =
            projectNameSet.contains(projectName.toLowerCase(Locale.getDefault()))

        @Synchronized
        override fun enqueue(projectName: String) {
            projectNameSet = projectNameSet.plus(projectName.toLowerCase(Locale.getDefault()))
        }

        @Synchronized
        override fun finished(projectName: String) {
            projectNameSet = projectNameSet.minus(projectName.toLowerCase(Locale.getDefault()))
        }
    }
}
